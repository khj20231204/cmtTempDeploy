/**
 * DocumentView - 문서 상세 보기 및 결재 처리 모듈
 * 
 * 결재 문서 상세 정보 표시 및 결재/반려 처리 기능을 제공합니다.
 * - 문서 상세 정보 표시
 * - 결재/반려 처리
 * - 결재 의견 관리
 * - 양식 내 폼 요소 읽기 전용 처리 기능 추가
 * 
 * @version 1.6.0
 * @since 2025-04-07
 * @update 2025-04-07 - 양식 내 폼 요소 읽기 전용 처리 기능 추가
 */
const DocumentView = (function() {
    //===========================================================================
    // 모듈 내부 변수
    //===========================================================================

    /**
     * 문서 관련 정보
     */
    let documentData = {
        docId: null, // 현재 문서 ID
        isCurrentApprover: false, // 현재 사용자가 결재자인지 여부
        approverId: null // 결재자 ID
    };

    /**
     * UI 관련 요소
     */
    let approvalModal; // 결재 모달 참조

    /**
     * API URL 상수 정의
     */
    const API_URLS = {
        PROCESS: (docId) => `/api/eapproval/document/${docId}/process` // 결재 처리 API
    };

    //===========================================================================
    // 초기화 및 이벤트 처리 함수
    //===========================================================================

    /**
     * 모듈 초기화 함수
     * 문서 상세 보기 페이지를 초기화합니다.
     * 
     * @returns {Promise<void>}
     */
    async function initialize() {
        try {
            console.log('DocumentView 초기화를 시작합니다.');

            // 초기 데이터 로드
            await initData();

            // 이벤트 리스너 등록
            await registerEvents();

            // 모달 초기화
            await initModal();

            // 양식 내 폼 요소 읽기 전용 설정
            // 문서 상태에 따라 조건부로 적용
            const docStatus = getDocumentStatus();
            if (docStatus !== '임시저장') {
                makeFormElementsReadOnly();
            }

            console.log('DocumentView 초기화가 완료되었습니다.');
        } catch (error) {
            console.error('초기화 중 오류 발생:', error);
            await AlertUtil.showError('초기화 오류', '페이지 초기화 중 오류가 발생했습니다.');
        }
    }

    /**
     * 초기 데이터 로드 함수
     * 서버에서 전달된 데이터 또는 DOM에서 초기 데이터를 로드합니다.
     * 
     * @returns {Promise<void>}
     */
    async function initData() {
        try {
            console.log('문서 데이터 초기화를 시작합니다.');

            // 다양한 소스에서 데이터 로드 시도
            loadDataFromWindowObject();

            // 문서 ID가 없다면 DOM과 URL에서 시도
            if (!documentData.docId) {
                loadDataFromDOM();
            }

            // 문서 ID가 여전히 없다면 URL에서 시도
            if (!documentData.docId) {
                loadDataFromURL();
            }

            // 문서 ID 검증
            if (!documentData.docId) {
                throw new Error('문서 ID를 찾을 수 없습니다.');
            }

            // 결재자 ID가 없는 경우, 테이블에서 찾기 시도
            if (!documentData.approverId && documentData.isCurrentApprover) {
                documentData.approverId = await findApproverIdFromTable();
                console.log('테이블에서 찾은 결재자 ID:', documentData.approverId);
            }

            console.log('문서 데이터 초기화 완료:', documentData);
        } catch (error) {
            console.error('데이터 초기화 중 오류:', error);
            throw error;
        }
    }

    /**
     * window 객체에서 데이터 로드
     */
    function loadDataFromWindowObject() {
        // Thymeleaf에서 전달된 데이터 확인
        if (window.documentData) {
            console.log('window.documentData 존재함:', window.documentData);

            // 가이드라인에 따라 대문자 키 사용
            if (window.documentData.DOC_ID) {
                documentData.docId = window.documentData.DOC_ID;
                console.log('Thymeleaf에서 문서 ID 로드:', documentData.docId);
            }

            documentData.isCurrentApprover = window.documentData.IS_CURRENT_APPROVER === true;
            console.log('Thymeleaf에서 결재자 여부 로드:', documentData.isCurrentApprover);

            // 결재자 ID 로드
            if (window.documentData.APPROVER_ID) {
                documentData.approverId = window.documentData.APPROVER_ID;
                console.log('Thymeleaf에서 결재자 ID 로드:', documentData.approverId);
            }
        }
    }

    /**
     * DOM에서 데이터 로드
     */
    function loadDataFromDOM() {
        const docIdElement = document.getElementById('docId');
        if (docIdElement) {
            documentData.docId = docIdElement.value;
            console.log('DOM에서 문서 ID 로드:', documentData.docId);
        }

        const isCurrentApproverElement = document.getElementById('isCurrentApprover');
        if (isCurrentApproverElement) {
            documentData.isCurrentApprover = isCurrentApproverElement.value === 'true';
            console.log('DOM에서 결재자 여부 로드:', documentData.isCurrentApprover);
        }

        // 결재자 ID 확인
        const approverIdElement = document.getElementById('approverId');
        if (approverIdElement && approverIdElement.value) {
            documentData.approverId = approverIdElement.value;
            console.log('DOM에서 결재자 ID 로드:', documentData.approverId);
        }
    }

    /**
     * URL에서 데이터 로드
     */
    function loadDataFromURL() {
        const urlParts = window.location.pathname.split('/');
        const potentialDocId = urlParts[urlParts.length - 1];
        if (potentialDocId && potentialDocId.length > 10) {
            documentData.docId = potentialDocId;
            console.log('URL에서 문서 ID 로드:', documentData.docId);
        }
    }

    /**
     * 이벤트 리스너 등록 함수
     * 버튼 클릭 및 기타 이벤트 리스너를 등록합니다.
     * 
     * @returns {Promise<void>}
     */
    async function registerEvents() {
        try {
            console.log('이벤트 리스너 등록을 시작합니다.');

            // 수정 버튼 클릭 이벤트(임시저장 문서에서만)
            const editBtn = document.getElementById('editBtn');
            if (editBtn) {
                editBtn.addEventListener('click', function() {
                    const docId = documentData.docId;
                    if (docId) {
                        /*변수 사용 때문에 따옴표 말고 백틱사용*/
                        window.location.href = `/eapproval/document/edit/${docId}`;
                    }
                });
                console.log('수정 버튼 이벤트 등록 완료')
            }


            // 결재 버튼 클릭 이벤트
            const approveBtn = document.getElementById('approveBtn');
            if (approveBtn) {
                approveBtn.addEventListener('click', function() {
                    openApprovalModal('승인');
                });
                console.log('결재 버튼 이벤트 등록 완료');
            }

            // 반려 버튼 클릭 이벤트
            const rejectBtn = document.getElementById('rejectBtn');
            if (rejectBtn) {
                rejectBtn.addEventListener('click', function() {
                    openApprovalModal('반려');
                });
                console.log('반려 버튼 이벤트 등록 완료');
            }

            // 목록 버튼 클릭 이벤트
            const backBtn = document.querySelector('button[onclick="history.back()"]');
            if (backBtn) {
                // onclick 속성 제거 후 이벤트 리스너로 대체
                backBtn.removeAttribute('onclick');
                backBtn.addEventListener('click', function(e) {
                    e.preventDefault();
                    goToDocumentList();
                });
                console.log('목록 버튼 이벤트 등록 완료');
            }

            console.log('이벤트 리스너 등록이 완료되었습니다.');
        } catch (error) {
            console.error('이벤트 리스너 등록 중 오류:', error);
            throw error;
        }
    }

    /**
     * 모달 초기화 함수
     * 결재 모달을 초기화하고 이벤트 핸들러를 등록합니다.
     * 
     * @returns {Promise<void>}
     */
    async function initModal() {
        try {
            console.log('모달 초기화를 시작합니다.');

            // 모달 요소 확인
            const modalElement = document.getElementById('approvalModal');
            if (!modalElement) {
                console.warn('approvalModal 요소를 찾을 수 없습니다.');
                return;
            }

            // Bootstrap 모달 초기화
            approvalModal = new bootstrap.Modal(modalElement);

            // 모달 확인 버튼 이벤트
            const confirmBtn = document.getElementById('confirmBtn');
            if (confirmBtn) {
                confirmBtn.addEventListener('click', function() {
                    processApproval();
                });
                console.log('모달 확인 버튼 이벤트 등록 완료');
            }

            console.log('모달 초기화가 완료되었습니다.');
        } catch (error) {
            console.error('모달 초기화 중 오류:', error);
            throw error;
        }
    }

    /**
     * 문서 상태 가져오기 함수
     * 페이지에서 문서 상태 값을 찾아 반환합니다.
     * 
     * @returns {string} 문서 상태 (임시저장, 진행중, 완료, 반려 등)
     */
    function getDocumentStatus() {
        try {
            // 여러 방법으로 문서 상태 가져오기 시도

            // 1. window.documentData에서 가져오기 (가이드라인에 따라 대문자 키 사용)
            if (window.documentData && window.documentData.DOC_STATUS) {
                return window.documentData.DOC_STATUS;
            }

            // 2. 뱃지 요소에서 가져오기
            const statusBadge = document.querySelector('span.badge');
            if (statusBadge) {
                return statusBadge.textContent.trim();
            }

            // 3. 문서 상태 셀에서 가져오기
            const statusCells = document.querySelectorAll('th, td');
            for (const cell of statusCells) {
                if (cell.textContent.includes('문서상태')) {
                    const nextCell = cell.nextElementSibling;
                    if (nextCell) {
                        const badge = nextCell.querySelector('.badge');
                        if (badge) {
                            return badge.textContent.trim();
                        }
                        return nextCell.textContent.trim();
                    }
                }
            }

            // 4. 임시저장 여부 확인
            if (window.documentData && window.documentData.IS_TEMP_SAVED === 'Y') {
                return '임시저장';
            }

            // 기본값 반환
            return '진행중';
        } catch (error) {
            console.error('문서 상태 가져오기 중 오류:', error);
            return '알 수 없음';
        }
    }

    /**
     * 양식 내 폼 요소 읽기 전용 설정 함수
     * 문서 내 모든 폼 요소(select, input, textarea)를 읽기 전용으로 변환합니다.
     */
	function makeFormElementsReadOnly() {
	    try {
	        console.log('양식 내 폼 요소 읽기 전용 설정 시작');

	        // 문서 내용 영역 선택
	        // 여러 가지 가능한 선택자를 시도하여 문서 내용 영역 찾기
	        let contentDiv = document.querySelector('.document-content');

	        if (!contentDiv) {
	            contentDiv = document.querySelector('.border.p-3.rounded');
	        }

	        if (!contentDiv) {
	            // 상세 페이지의 문서 내용이 있는 div를 찾습니다 (예: class가 없는 경우)
	            const contentCards = document.querySelectorAll('.card-body > div');
	            for (const div of contentCards) {
	                if (div.innerHTML.includes('<table') || div.innerHTML.includes('<form')) {
	                    contentDiv = div;
	                    break;
	                }
	            }
	        }

	        if (!contentDiv) {
	            console.warn('문서 내용 영역을 찾을 수 없습니다.');
	            return;
	        }

	        console.log('문서 내용 영역 찾음:', contentDiv);

	        // 모든 select 요소 처리
	        const selects = contentDiv.querySelectorAll('select');
	        selects.forEach(select => {
	            // 선택된 값을 유지하면서 비활성화만 함
	            select.setAttribute('disabled', 'disabled');
	            
	            // 스타일 조정 (선택 사항)
	            select.classList.add('form-select-plaintext');
	            
	            console.log(`비활성화 처리: select #${select.id || 'unnamed'}`);
	        });

	        // 모든 input 요소 처리
	        const inputs = contentDiv.querySelectorAll('input');
	        inputs.forEach(input => {
	            if (input.type === 'text' || input.type === 'number' || input.type === 'date' || input.type === 'hidden') {
	                // 읽기 전용 속성만 추가
	                input.setAttribute('readonly', 'readonly');
	                
	                // 필요시 스타일 조정 (선택 사항)
	                input.classList.add('form-control-plaintext');
	                
	                console.log(`읽기 전용 처리: input #${input.id || 'unnamed'}, 값: "${input.value}"`);
	            } else if (input.type === 'checkbox' || input.type === 'radio') {
	                // 체크박스/라디오버튼 비활성화만 함
	                input.setAttribute('disabled', 'disabled');
	                
	                console.log(`비활성화: ${input.type} #${input.id || 'unnamed'}, 상태: ${input.checked ? '체크됨' : '체크안됨'}`);
	            }
	        });

	        // 모든 textarea 요소 처리
	        const textareas = contentDiv.querySelectorAll('textarea');
	        textareas.forEach(textarea => {
	            // 읽기 전용 속성만 추가
	            textarea.setAttribute('readonly', 'readonly');
	            
	            // 필요시 스타일 조정 (선택 사항)
	            textarea.classList.add('form-control-plaintext');
	            
	            console.log(`읽기 전용 처리: textarea #${textarea.id || 'unnamed'}`);
	        });

	        // 모든 버튼 비활성화 (양식 내 버튼이 있는 경우)
	        const buttons = contentDiv.querySelectorAll('button:not(.btn-remove-line)');
	        buttons.forEach(button => {
	            // 비활성화만 함
	            button.setAttribute('disabled', 'disabled');
	            button.classList.add('disabled');
	            
	            console.log(`비활성화: button #${button.id || 'unnamed'}`);
	        });

	        console.log('양식 내 폼 요소 읽기 전용 설정 완료');
	    } catch (error) {
	        console.error('양식 내 폼 요소 읽기 전용 설정 중 오류:', error);
	    }
	}

    //===========================================================================
    // 결재 처리 함수
    //===========================================================================

    /**
     * 결재 모달 열기 함수
     * 결재 유형(승인/반려)에 따라 모달을 구성하여 표시합니다.
     * 
     * @param {string} decision - 결재 결정 (승인/반려)
     */
    function openApprovalModal(decision) {
        try {
            if (!approvalModal) {
                console.warn('approvalModal이 초기화되지 않았습니다.');
                return;
            }

            console.log(`결재 모달 열기: ${decision}`);

            // 결재 타입 설정
            const decisionField = document.getElementById('decision');
            if (decisionField) {
                decisionField.value = decision;
            }

            // 모달 제목 설정
            const modalTitle = document.getElementById('approvalModalLabel');
            if (modalTitle) {
                modalTitle.textContent = decision === '승인' ? '결재 승인' : '결재 반려';
            }

            // 의견 초기화
            const commentField = document.getElementById('comment');
            if (commentField) {
                commentField.value = '';
            }

            // 반려 시 의견 필수 안내 표시
            const commentHelp = document.getElementById('commentHelp');
            if (commentHelp) {
                commentHelp.style.display = decision === '반려' ? 'block' : 'none';
            }

            // 모달 열기
            approvalModal.show();
        } catch (error) {
            console.error('결재 모달 열기 중 오류:', error);
            AlertUtil.showError('모달 오류', '결재 모달을 열 수 없습니다.');
        }
    }

    /**
     * 결재 처리 함수
     * 결재 또는 반려 처리를 수행합니다.
     * 
     * @returns {Promise<void>}
     */
    async function processApproval() {
        try {
            console.log('결재 처리 시작');

            // 폼 데이터 수집
            const decision = document.getElementById('decision').value;
            const comment = document.getElementById('comment').value;

            // 디버그 로깅
            console.log('결재 처리 파라미터:', {
                decision: decision,
                comment: comment,
                approverId: documentData.approverId,
                docId: documentData.docId
            });

            // 결재 결정 검증
            if (!decision) {
                await AlertUtil.showWarning('필수 항목 누락', '결재 결정(승인/반려)이 필요합니다.');
                return;
            }

            // 반려 시 의견 필수
            if (decision === '반려' && !comment.trim()) {
                await AlertUtil.showWarning('반려 의견 누락', '반려 시에는 의견을 입력해주세요.');
                document.getElementById('comment').focus();
                return;
            }

            // 결재자 ID 확인
            if (!documentData.approverId) {
                // 다시 한번 찾기 시도
                console.log('결재자 ID가 없어 다시 찾기 시도');
                documentData.approverId = await findApproverIdFromTable();

                if (!documentData.approverId) {
                    console.error('결재자 ID를 찾지 못함');

                    // 최후의 수단: 현재 로그인한 사용자 ID 사용 (관리자에게 문의 안내)
                    const confirmed = await AlertUtil.showConfirm({
                        title: '결재자 정보 오류',
                        text: '결재자 정보를 찾을 수 없습니다. 임시 ID를 사용하여 계속하시겠습니까?\n(오류가 계속되면 관리자에게 문의하세요)',
                        confirmButtonText: '계속',
                        cancelButtonText: '취소'
                    });

                    if (!confirmed) {
                        return;
                    }

                    // 임시 ID 생성
                    documentData.approverId = 'emergency-' + Date.now();
                    console.log(`임시 결재자 ID 생성: ${documentData.approverId}`);
                }
            }

            console.log('최종 사용할 결재자 ID:', documentData.approverId);

            // 결재 데이터 준비
            const approvalData = {
                approverId: documentData.approverId,
                decision: decision,
                comment: comment || ''
            };

            // 리팩토링된 ApiUtil 사용하여 결재 처리
            // 로딩 표시 시작
            const loading = AlertUtil.showLoading(
                decision === '승인' ? '결재 승인 중...' : '결재 반려 중...'
            );

            try {
                // API 호출
                const apiUrl = API_URLS.PROCESS(documentData.docId);
                const response = await ApiUtil.post(apiUrl, approvalData);

                // 로딩 표시 종료
                loading.close();

                // 응답 처리
                if (response.success) {
                    // 성공 메시지 표시
                    const successMessage = decision === '승인' ? '결재가 승인되었습니다.' : '결재가 반려되었습니다.';
                    await AlertUtil.showSuccess('결재 처리 완료', successMessage);

                    // 모달 닫기
                    if (approvalModal) {
                        approvalModal.hide();
                    }

                    // 문서 목록으로 이동
                    setTimeout(() => {
                        goToDocumentList();
                    }, 500);
                } else {
                    // 실패 메시지 표시
                    await AlertUtil.showWarning(
                        '결재 처리 실패',
                        response.message || '결재 처리 중 오류가 발생했습니다.'
                    );
                }
            } catch (error) {
                // 로딩 표시 종료
                loading.close();

                // 오류 처리
                await ApiUtil.handleApiError(error, '결재 처리 실패');
            }
        } catch (error) {
            console.error('결재 처리 중 오류:', error);
            await AlertUtil.showError('결재 처리 오류', error.message || '결재 처리 중 오류가 발생했습니다.');
        }
    }

    //===========================================================================
    // 유틸리티 함수
    //===========================================================================

    /**
     * 문서 목록 페이지로 이동하는 함수
     */
    function goToDocumentList() {
        window.location.href = '/eapproval/documents';
    }

    /**
     * 결재선 테이블에서 결재자 ID 찾기
     * '대기' 상태인 행에서 결재자 ID를 찾습니다.
     * 
     * @returns {Promise<string|null>} 결재자 ID 또는 null
     */
    async function findApproverIdFromTable() {
        try {
            console.log('결재선 테이블에서 결재자 ID 찾기 시작');

            // 테이블 요소 찾기 - 여러 선택자 시도
            const approvalTable = findApprovalTable();

            if (!approvalTable) {
                console.warn('어떤 방법으로도 결재선 테이블을 찾을 수 없습니다.');
                return null;
            }

            console.log('결재선 테이블 찾음:', approvalTable);

            // 결재자 ID 찾기 - 여러 방법 시도

            // 1. 대기 상태인 행에서 data-approver-id 속성 확인
            const pendingRow = approvalTable.querySelector('tr[data-approval-status="대기"]');
            if (pendingRow) {
                const approverId = pendingRow.getAttribute('data-approver-id');
                if (approverId) {
                    console.log(`테이블 행의 data-approver-id 속성에서 결재자 ID 찾음: ${approverId}`);
                    return approverId;
                }
            }

            // 2. 모든 행에서 대기 상태 찾기
            const result = findApproverIdFromRows(approvalTable);
            if (result) return result;

            // 3. DOM에서 hidden input 확인
            const approverIdInput = document.getElementById('approverId');
            if (approverIdInput && approverIdInput.value) {
                console.log(`hidden input에서 결재자 ID 찾음: ${approverIdInput.value}`);
                return approverIdInput.value;
            }

            // 4. URL 기반 임시 ID 생성
            return createFallbackId();
        } catch (error) {
            console.error('결재자 ID 찾기 중 오류:', error);
            return 'error-fallback-id';
        }
    }

    /**
     * 결재선 테이블 요소 찾기
     * 
     * @returns {HTMLElement|null} 테이블 요소 또는 null
     */
    function findApprovalTable() {
        // ID로 찾기
        let table = document.getElementById('approvalLinesTable');
        if (table) return table;

        // 클래스 선택자로 찾기
        table = document.querySelector('table.table-bordered.text-center');
        if (table) return table;

        // 일반 테이블로 찾기
        return document.querySelector('table.table-bordered');
    }

    /**
     * 테이블 행에서 결재자 ID 찾기
     * 
     * @param {HTMLElement} table - 테이블 요소
     * @returns {string|null} 결재자 ID 또는 null
     */
    function findApproverIdFromRows(table) {
        const rows = table.querySelectorAll('tbody tr');
        console.log(`테이블에서 ${rows.length}개 행 찾음`);

        for (let i = 0; i < rows.length; i++) {
            // 결재 상태 셀 찾기
            const statusCell = rows[i].querySelector('td:nth-child(5) span');

            if (statusCell && statusCell.textContent.trim() === '대기') {
                console.log('대기 상태인 행 찾음:', rows[i]);

                // 결재자 ID를 data-approver-id 속성에서 가져오기
                const approverId = rows[i].getAttribute('data-approver-id');
                if (approverId) {
                    console.log(`테이블 대기 행에서 결재자 ID 찾음: ${approverId}`);
                    return approverId;
                }

                // 결재자 셀에서 data-approver-id 속성 확인
                const approverCell = rows[i].querySelector('td:nth-child(2)');
                if (approverCell) {
                    const cellApproverId = approverCell.getAttribute('data-approver-id');
                    if (cellApproverId) {
                        console.log(`결재자 셀에서 ID 찾음: ${cellApproverId}`);
                        return cellApproverId;
                    }

                    // 결재자 이름 추출
                    const approverName = approverCell.textContent.trim();
                    if (approverName) {
                        console.log(`결재자 이름으로 대체 ID 생성: ${approverName}`);
                        return approverName;
                    }
                }

                // 행 인덱스 + 1을 ID로 사용
                const fallbackId = `approver-${i+1}`;
                console.log(`행 인덱스로 대체 ID 생성: ${fallbackId}`);
                return fallbackId;
            }
        }

        return null;
    }

    /**
     * URL 기반 임시 ID 생성
     * 
     * @returns {string} 임시 ID
     */
    function createFallbackId() {
        const urlParts = window.location.pathname.split('/');
        const docIdFromUrl = urlParts[urlParts.length - 1] || 'unknown';
        const fallbackId = `fallback-${docIdFromUrl}`;
        console.warn(`모든 방법 실패: 임시 결재자 ID 생성 - ${fallbackId}`);
        return fallbackId;
    }

    //===========================================================================
    // 공개 API - 외부에서 접근 가능한 메서드
    //===========================================================================

    return {
        // 초기화 및 기본 기능
        initialize, // 모듈 초기화

        // 결재 처리 함수
        openApprovalModal, // 결재 모달 열기
        processApproval, // 결재 처리 실행

        // 유틸리티 함수
        goToDocumentList, // 문서 목록으로 이동
        makeFormElementsReadOnly // 양식 내 폼 요소 읽기 전용 설정
    };
})();

// DOM 로드 시 초기화
document.addEventListener('DOMContentLoaded', async function() {
    try {
        // 모듈 초기화
        await DocumentView.initialize();
    } catch (error) {
        console.error('초기화 중 오류 발생:', error);
        if (window.AlertUtil) {
            await AlertUtil.showError('초기화 오류', '페이지 초기화 중 오류가 발생했습니다.');
        } else {
            alert('페이지 초기화 중 오류가 발생했습니다.');
        }
    }
});