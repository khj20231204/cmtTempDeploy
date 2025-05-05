/**
 * ApprovalLineManager - 결재선 관리 모듈
 * 
 * 전자결재 결재선 관리를 위한 기능을 제공합니다.
 * - 결재선 추가/수정/삭제
 * - 결재선 유효성 검증
 * - 결재자 정보 조회 및 설정
 * - 결재자 순서 자동 관리
 * 
 * @version 1.3.0
 * @since 2025-04-04
 * @update 2025-04-04 - API 응답 처리 리팩토링 및 대문자 키 일관성 개선
 */
const ApprovalLineManager = (function() {
    //===========================================================================
    // 모듈 내부 변수
    //===========================================================================
    
    /**
     * 결재선 데이터 배열
     * 각 항목은 다음 속성을 포함합니다:
     * - approvalNo: 결재자 번호
     * - docId: 문서 ID
     * - approverId: 결재자 ID
     * - approverName: 결재자 이름
     * - approverPosition: 결재자 직위
     * - approvalOrder: 결재 순서
     * - approvalType: 결재 타입 (결재, 합의, 참조 등)
     * - approvalStatus: 결재 상태 (대기, 승인, 반려 등)
     */
    let approvalLines = [];
    
    /**
     * API URL 상수 정의 
     * 결재선 관련 API 엔드포인트 정의
     */
    const API_URLS = {
        APPROVERS: '/api/eapproval/approvers',           // 결재자 목록 조회 API
        SAVE: '/api/eapproval/approvallines/batch',      // 결재선 일괄 저장 API
        DELETE: (id) => `/api/eapproval/approvalline/${id}` // 결재선 항목 삭제 API
    };
    
    //===========================================================================
    // 초기화 및 이벤트 처리 함수
    //===========================================================================

    /**
     * 모듈 초기화 함수
     * 결재선 초기 데이터 설정 및 이벤트 등록을 수행합니다.
     * 
     * @param {Array} initialLines - 초기 결재선 데이터 (선택적)
     * @returns {Promise<void>}
     */
    async function initialize(initialLines) {
        try {
            console.log('ApprovalLineManager 초기화를 시작합니다.');
            
            // 초기 결재선 데이터 설정
            if (initialLines && Array.isArray(initialLines)) {
                approvalLines = [...initialLines];
                
                // 결재선 순서 재정렬 확인
                reorderApprovalLines();
                
                console.log(`초기 결재선 ${approvalLines.length}개가 로드되었습니다.`);
            } else {
                console.log('초기 결재선 데이터가 없습니다.');
            }
            
            // UI 렌더링
            await renderApprovalLines();
            
            // 이벤트 핸들러 설정
            await setupEventHandlers();
            
            console.log('ApprovalLineManager 초기화가 완료되었습니다.');
        } catch (error) {
            console.error('결재선 관리자 초기화 중 오류 발생:', error);
            await AlertUtil.showError('초기화 오류', '결재선 관리자 초기화 중 오류가 발생했습니다.');
        }
    }
    
    /**
     * 이벤트 핸들러 설정 함수
     * 결재선 관련 버튼 및 입력 필드에 이벤트 리스너를 등록합니다.
     * 
     * @returns {Promise<void>}
     */
    async function setupEventHandlers() {
        try {
            console.log('결재선 이벤트 핸들러 등록을 시작합니다.');
            
            // 결재자 추가 버튼
            const addApproverBtn = document.getElementById('btnAddApprover');
            if (addApproverBtn) {
                addApproverBtn.addEventListener('click', addApprover);
                console.log('결재자 추가 버튼 이벤트 등록 완료');
            }
            
            // 동적으로 생성되는 요소에 대한 이벤트 처리 (이벤트 위임)
            const approvalLineTable = document.getElementById('approvalLineTable');
            if (approvalLineTable) {
                // 결재자 삭제 버튼 이벤트
                approvalLineTable.addEventListener('click', async function(event) {
                    // 삭제 버튼 클릭 확인
                    if (event.target.closest('.btn-remove-line')) {
                        const row = event.target.closest('tr');
                        if (row && row.dataset.index !== undefined) {
                            const index = parseInt(row.dataset.index);
                            await removeApprover(index);
                        }
                    }
                });
                
                // 결재자 선택 변경 이벤트
                approvalLineTable.addEventListener('change', async function(event) {
                    // 결재자 선택 드롭다운 변경 확인
                    if (event.target.classList.contains('approver-select')) {
                        const row = event.target.closest('tr');
                        if (row && row.dataset.index !== undefined) {
                            const index = parseInt(row.dataset.index);
                            const approverId = event.target.value;
                            await handleApproverChange(index, approverId, event.target);
                        }
                    }
                    
                    // 결재 타입 변경 확인
                    if (event.target.dataset.field === 'approvalType') {
                        const row = event.target.closest('tr');
                        if (row && row.dataset.index !== undefined) {
                            const index = parseInt(row.dataset.index);
                            const value = event.target.value;
                            updateApproverData(index, 'approvalType', value);
                        }
                    }
                });
            }
            
            console.log('결재선 이벤트 핸들러 등록이 완료되었습니다.');
        } catch (error) {
            console.error('이벤트 핸들러 등록 중 오류:', error);
            throw error;
        }
    }
    
    //===========================================================================
    // 결재선 데이터 관리 함수
    //===========================================================================
    
    /**
     * 결재자 추가 함수
     * 결재선에 새로운 결재자를 추가하고 UI를 업데이트합니다.
     * 
     * @returns {Promise<void>}
     */
    async function addApprover() {
        try {
            console.log('결재자 추가');
            
            // 새 결재자 데이터 생성
            const newApprover = {
                approvalNo: null,
                docId: getDocumentId() || '',
                approverId: '',
                approverName: '',
                approverPosition: '',
                approvalOrder: approvalLines.length + 1,
                approvalType: '결재',
                approvalStatus: '대기'
            };
            
            // 결재선 배열에 추가
            approvalLines.push(newApprover);
            console.log(`새 결재자가 추가되었습니다. 현재 결재자 수: ${approvalLines.length}명`);
            
            // UI 업데이트
            await renderApprovalLines();
        } catch (error) {
            console.error('결재자 추가 중 오류:', error);
            await AlertUtil.showError('결재자 추가 오류', '결재자 추가 중 오류가 발생했습니다.');
        }
    }
    
    /**
     * 결재자 삭제 함수
     * 지정된 인덱스의 결재자를 결재선에서 제거하고 UI를 업데이트합니다.
     * 삭제 후 나머지 결재자의 순서를 재조정합니다.
     * 
     * @param {number} index - 삭제할 결재자의 인덱스
     * @returns {Promise<void>}
     */
    async function removeApprover(index) {
        try {
            console.log(`결재자 삭제: 인덱스 ${index}`);
            
            // 유효한 인덱스 확인
            if (index < 0 || index >= approvalLines.length) {
                console.warn(`유효하지 않은 인덱스: ${index}`);
                return;
            }
            
            // 배열에서 제거
            approvalLines.splice(index, 1);
            
            // 순서 재조정
            reorderApprovalLines();
            
            console.log(`결재자가 삭제되었습니다. 남은 결재자 수: ${approvalLines.length}명`);
            
            // UI 업데이트
            await renderApprovalLines();
        } catch (error) {
            console.error('결재자 삭제 중 오류:', error);
            await AlertUtil.showError('결재자 삭제 오류', '결재자 삭제 중 오류가 발생했습니다.');
        }
    }
    
    /**
     * 결재선 순서 재조정 함수
     * 모든 결재선 항목의 순서를 인덱스 순서대로 다시 설정합니다.
     */
    function reorderApprovalLines() {
        // 강제로 인덱스 기준으로 결재 순서 재설정
        approvalLines.forEach((line, idx) => {
            if (line.approvalOrder !== idx + 1) {
                line.approvalOrder = idx + 1;
                console.log(`결재선[${idx}] 순서를 ${idx + 1}로 조정했습니다.`);
            }
        });
        
        console.log('결재선 순서가 재조정되었습니다:', 
            approvalLines.map(line => `${line.approverName || '미지정'}:${line.approvalOrder}`).join(', '));
        return approvalLines;
    }
    
    /**
     * 결재자 데이터 업데이트 함수
     * 지정된 인덱스의 결재자 정보를 업데이트합니다.
     * 
     * @param {number} index - 업데이트할 결재자의 인덱스
     * @param {string} key - 업데이트할 속성 키
     * @param {*} value - 업데이트할 값
     */
    function updateApproverData(index, key, value) {
        if (index >= 0 && index < approvalLines.length) {
            approvalLines[index][key] = value;
            console.log(`결재자[${index}] ${key} 필드가 '${value}'로 업데이트되었습니다.`);
        } else {
            console.warn(`유효하지 않은 인덱스: ${index}`);
        }
    }
    
    /**
     * 결재자 선택 변경 처리 함수
     * 결재자 선택 드롭다운 변경 시 관련 데이터를 업데이트합니다.
     * 
     * @param {number} index - 결재선 인덱스
     * @param {string} approverId - 결재자 ID
     * @param {HTMLElement} selectElement - 선택 요소
     * @returns {Promise<void>}
     */
    async function handleApproverChange(index, approverId, selectElement) {
        try {
            console.log(`결재자 선택 변경: index=${index}, approverId=${approverId}`);
            
            const row = selectElement.closest('tr');
            
            // 순서값 명시적 체크
            const orderInput = row.querySelector('input[name="approvalOrder"]');
            if (orderInput && parseInt(orderInput.value) !== index + 1) {
                console.log(`순서값 불일치 감지: ${orderInput.value} !== ${index + 1}, 수정합니다.`);
                orderInput.value = index + 1;
                
                // 순서 표시 텍스트도 업데이트
                const orderSpan = row.querySelector('td:first-child span');
                if (orderSpan) {
                    orderSpan.textContent = index + 1;
                }
            }
            
            // 선택 취소 처리
            if (!approverId) {
                // 관련 필드 초기화
                const positionInput = row.querySelector('input[readonly]');
                if (positionInput) {
                    positionInput.value = '';
                }
                
                // 데이터 초기화
                updateApproverData(index, 'approverId', '');
                updateApproverData(index, 'approverPosition', '');
                updateApproverData(index, 'approverName', '');
                return;
            }
            
            // 선택된 결재자 정보 추출
            const selectedOption = selectElement.options[selectElement.selectedIndex];
            const approverName = selectedOption.getAttribute('data-name') || selectedOption.textContent.split('(')[0].trim();
            const approverPosition = selectedOption.getAttribute('data-position') || '';
            const approverDept = selectedOption.getAttribute('data-dept') || '';
            
            console.log(`선택된 결재자 정보: 이름=${approverName}, 직위=${approverPosition}, 부서=${approverDept}`);
            
            // UI 업데이트 - 직위 필드 명시적 업데이트
            const positionInput = row.querySelector('input[readonly]');
            if (positionInput) {
                positionInput.value = approverPosition;
            }
            
            // 데이터 업데이트
            updateApproverData(index, 'approverId', approverId);
            updateApproverData(index, 'approverName', approverName);
            updateApproverData(index, 'approverPosition', approverPosition);
            
            // 순서 필드 명시적 업데이트 
            updateApproverData(index, 'approvalOrder', index + 1);
            
            console.log(`결재자[${index}] 변경 완료: ${approverName}(${approverPosition}), 순서=${index + 1}`);
        } catch (error) {
            console.error('결재자 변경 처리 중 오류:', error);
            await AlertUtil.showError('결재자 변경 오류', '결재자 정보 변경 중 오류가 발생했습니다.');
        }
    }
    
    //===========================================================================
    // UI 렌더링 및 데이터 조회 함수
    //===========================================================================
    
    /**
     * 결재선 렌더링 함수
     * 현재 결재선 데이터를 기반으로 UI를 업데이트합니다.
     * 
     * @returns {Promise<void>}
     */
    async function renderApprovalLines() {
        try {
            console.log('결재선 렌더링 시작:', approvalLines);
            
            // 결재선 테이블 본문 요소 조회
            const tbody = document.getElementById('approvalLineTable').querySelector('tbody');
            if (!tbody) {
                throw new Error('결재선 테이블 본문 요소를 찾을 수 없습니다.');
            }
            
            // 기존 내용 초기화
            tbody.innerHTML = '';
            
            // 결재선이 비어있는 경우 안내 메시지 표시
            if (approvalLines.length === 0) {
                const emptyRow = document.createElement('tr');
                emptyRow.innerHTML = `
                    <td colspan="5" class="text-center text-muted py-3">
                        결재선을 추가해주세요
                    </td>
                `;
                tbody.appendChild(emptyRow);
                return;
            }
            
            // 순서 재조정 확인
            reorderApprovalLines();
            
            // 각 결재선 항목에 대한 행 생성
            approvalLines.forEach((line, index) => {
                const row = document.createElement('tr');
                row.dataset.index = index;
                
                // input 필드 대신 span으로 변경하여 직접 텍스트 표시
                row.innerHTML = `
                    <td class="text-center">
                        <span class="form-control-plaintext fw-bold">${index + 1}</span>
                        <input type="hidden" name="approvalOrder" value="${index + 1}" />
                    </td>
                    <td>
                        <select data-field="approverId" class="form-select approver-select" required>
                            <option value="">결재자 선택</option>
                            <!-- 결재자 목록은 별도 함수로 채움 -->
                        </select>
                    </td>
                    <td>
                        <input type="text" value="${line.approverPosition || ''}" class="form-control" readonly />
                    </td>
                    <td>
                        <select data-field="approvalType" class="form-select">
                            <option value="결재" ${line.approvalType === '결재' ? 'selected' : ''}>결재</option>
                            <option value="합의" ${line.approvalType === '합의' ? 'selected' : ''}>합의</option>
                            <option value="참조" ${line.approvalType === '참조' ? 'selected' : ''}>참조</option>
                        </select>
                    </td>
                    <td class="text-center">
                        <button type="button" class="btn btn-danger btn-sm btn-remove-line">
                            <i class="bi bi-trash"></i>
                        </button>
                    </td>
                `;
                
                tbody.appendChild(row);
            });
            
            // 결재자 목록 드롭다운 로드
            await loadApproversToDropdowns();
            
            console.log('결재선 렌더링 완료');
        } catch (error) {
            console.error('결재선 렌더링 중 오류:', error);
            await AlertUtil.showError('렌더링 오류', '결재선 표시 중 오류가 발생했습니다.');
        }
    }
    
    /**
     * 결재자 목록을 드롭다운에 로드하는 함수
     * API를 호출하여 결재자 목록을 가져와 모든 드롭다운에 적용합니다.
     * 
     * @returns {Promise<void>}
     */
    async function loadApproversToDropdowns() {
        try {
            // 결재자 목록 로드
            const approvers = await loadApprovers();
            if (!approvers || approvers.length === 0) {
                console.warn('결재자 목록이 비어있거나 로드에 실패했습니다.');
                return;
            }
            
            // 모든 결재자 선택 드롭다운 업데이트
            const selectElements = document.querySelectorAll('.approver-select');
            selectElements.forEach(selectElement => {
                const row = selectElement.closest('tr');
                const index = parseInt(row.dataset.index);
                const currentValue = approvalLines[index]?.approverId || '';
                
                // 기본 옵션 유지
                let optionsHtml = '<option value="">결재자 선택</option>';
                
                // 결재자 옵션 추가
                approvers.forEach(approver => {
                    // 가이드라인에 따라 대문자 키로 접근
                    const approverValue = approver.EMP_ID;
                    const selected = approverValue == currentValue ? 'selected' : '';
                    
                    optionsHtml += `<option value="${approverValue}" 
                                        data-position="${approver.DEPT_POSITION || ''}" 
                                        data-name="${approver.EMP_NAME || ''}"
                                        data-dept="${approver.DEPT_NAME || ''}"
                                        ${selected}>
                                    ${approver.EMP_NAME || ''} (${approver.DEPT_NAME || ''})
                                    </option>`;
                });
                
                // HTML 삽입
                selectElement.innerHTML = optionsHtml;
                
                // 선택된 값이 있으면 직위 정보 업데이트
                if (currentValue) {
                    const selectedOption = selectElement.querySelector('option:checked');
                    if (selectedOption) {
                        const position = selectedOption.getAttribute('data-position') || '';
                        
                        // 직위 입력 필드 업데이트
                        const positionInput = row.querySelector('input[readonly]');
                        if (positionInput) {
                            positionInput.value = position;
                        }
                    }
                }
            });
            
            console.log('결재자 드롭다운 로드 완료');
        } catch (error) {
            console.error('결재자 드롭다운 로드 오류:', error);
            await AlertUtil.showWarning('결재자 목록 로드 실패', '결재자 정보를 불러올 수 없습니다.');
        }
    }
    
    /**
     * 결재자 목록 로드 함수
     * API를 호출하여 결재자 목록을 가져옵니다.
     * 
     * @returns {Promise<Array|null>} 결재자 목록 또는 실패 시 null
     */
    async function loadApprovers() {
        try {
            console.log('결재자 목록 로드 시작');
            
            // 리팩토링된 ApiUtil 사용하여 API 호출
            const response = await ApiUtil.getWithLoading(
                API_URLS.APPROVERS,
                null,
                '결재자 목록 로드 중...'
            );
            
            // 응답 확인
            if (!response.success || !response.data) {
                throw new Error(response.message || '결재자 목록을 불러올 수 없습니다.');
            }
            
            // 결재자 목록 추출
            const approvers = response.data;
            console.log(`결재자 목록 로드 완료: ${approvers.length}명`);
            
            return approvers;
        } catch (error) {
            console.error('결재자 목록 로드 오류:', error);
            await ApiUtil.handleApiError(error, '결재자 조회 실패');
            return null;
        }
    }
    
    //===========================================================================
    // 유틸리티 및 검증 함수
    //===========================================================================
    
    /**
     * 결재선 유효성 검사 함수
     * 현재 결재선이 유효한지 검사합니다.
     * 
     * @returns {Promise<boolean>} 유효성 검사 결과
     */
    async function validateApprovalLines() {
        try {
            // 결재선이 비어있는 경우
            if (approvalLines.length === 0) {
                await AlertUtil.showWarning('유효성 검사', '결재선을 추가해주세요.');
                return false;
            }
            
            // 결재선 항목별 유효성 검사
            for (let i = 0; i < approvalLines.length; i++) {
                // 결재자 미선택 검사
                if (!approvalLines[i].approverId) {
                    await AlertUtil.showWarning('유효성 검사', `${i+1}번째 결재자를 선택해주세요.`);
                    return false;
                }
                
                // 필요시 추가 검증 로직 구현
                // 예: 동일 결재자 중복 검사, 결재 유형 검사 등
            }
            
            console.log('결재선 유효성 검사 통과');
            return true;
        } catch (error) {
            console.error('결재선 유효성 검사 중 오류:', error);
            await AlertUtil.showError('검증 오류', '결재선 검증 중 오류가 발생했습니다.');
            return false;
        }
    }
    
    /**
     * 문서 ID 가져오기
     * DocumentFormManager에서 현재 문서 ID를 가져옵니다.
     * 
     * @returns {string} 문서 ID
     */
    function getDocumentId() {
        // DocumentFormManager 의존성 확인
        if (window.DocumentFormManager && typeof DocumentFormManager.getDocumentId === 'function') {
            return DocumentFormManager.getDocumentId();
        }
        // 대체 방법으로 문서 ID 확인
        const docIdElement = document.getElementById('docId');
        return docIdElement ? docIdElement.value : '';
    }
    
    /**
     * 현재 결재선 데이터 반환 함수
     * 외부 모듈에서 결재선 데이터에 접근할 수 있도록 합니다.
     * 
     * @returns {Array} 결재선 데이터 배열
     */
    function getApprovalLines() {
        // 결재선 반환 전 순서 확인
        reorderApprovalLines();
        return approvalLines;
    }
    
    //===========================================================================
    // 공개 API - 외부에서 접근 가능한 메서드
    //===========================================================================
    
    return {
        // 초기화 및 기본 기능
        initialize,             // 모듈 초기화
        
        // 결재선 데이터 관리
        addApprover,            // 결재자 추가
        removeApprover,         // 결재자 삭제
        
        // UI 렌더링 및 조회
        renderApprovalLines,    // 결재선 UI 업데이트
        
        // 유틸리티 및 검증
        validateApprovalLines,  // 결재선 유효성 검사
        getApprovalLines        // 결재선 데이터 조회
    };
})();

// DOM 로드 시 문서 폼에서 초기화하므로 여기서는 자동 초기화하지 않음