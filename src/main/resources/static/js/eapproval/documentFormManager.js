/**
 * DocumentFormManager - 문서 폼 관리 모듈
 * 
 * 전자결재 문서 폼을 관리하고 제출하는 기능을 제공합니다.
 * - 문서 데이터 초기화 및 관리
 * - 폼 유효성 검사
 * - 임시저장 및 결재요청 처리
 * - UI 관련 기능 제공
 * 
 * @version 1.3.0
 * @since 2025-04-07
 * @update 2025-04-07 - 에디터 내 폼 요소 값 저장 기능 추가
 */
const DocumentFormManager = (function() {
    //===========================================================================
    // 모듈 내부 변수
    //===========================================================================
    
    /**
     * 문서 정보 객체
     * 문서 관련 주요 데이터를 저장합니다.
     */
    let documentData = {
        docId: '',       // 문서 ID
        docNumber: '',   // 문서 번호
        formId: '',      // 양식 ID
        title: '',       // 제목
        content: ''      // 내용
    };
    
    /**
     * API URL 상수 정의
     * 문서 관련 API 엔드포인트 정의
     */
    const API_URLS = {
        SAVE: '/api/eapproval/document',           // 문서 저장/제출 API
        FORM: (formId) => `/api/eapproval/form/${formId}` // 양식 조회 API
    };
    
    //===========================================================================
    // 초기화 및 이벤트 처리 함수
    //===========================================================================
    
    /**
     * 모듈 초기화 함수
     * 문서 폼 관리자를 초기화하고 이벤트 리스너를 등록합니다.
     * 
     * @returns {Promise<void>}
     */
    async function initialize() {
        try {
            console.log('DocumentFormManager 초기화를 시작합니다.');
            
            // 모듈 내부 데이터 초기화
            await initializeData();
            
            // UI 초기화 (스타일 등)
            await initializeUI();
            
            // 이벤트 핸들러 등록
            await setupEventHandlers();
            
            console.log('DocumentFormManager 초기화가 완료되었습니다.');
        } catch (error) {
            console.error('DocumentFormManager 초기화 중 오류 발생:', error);
            await AlertUtil.showError('초기화 오류', '페이지 초기화 중 오류가 발생했습니다.');
        }
    }
    
    /**
     * 데이터 초기화 함수
     * 서버에서 전달된 데이터 또는 DOM에서 초기 데이터를 로드합니다.
     * 
     * @returns {Promise<void>}
     */
    async function initializeData() {
        try {
            console.log('문서 데이터 초기화를 시작합니다.');
            
            // Thymeleaf에서 전달받은 데이터 로드 (window.documentData 전역변수)
            if (window.documentData) {
                // 가이드라인에 따라 대문자 키로 접근
                documentData = {
                    docId: window.documentData.DOC_ID || '',
                    docNumber: window.documentData.DOC_NUMBER || '',
                    formId: window.documentData.FORM_ID || '',
                    title: window.documentData.TITLE || '',
                    content: window.documentData.CONTENT || ''
                };
                
                // 폼 필드 초기화
                const docIdElement = document.getElementById('docId');
                if (docIdElement) docIdElement.value = documentData.docId;
                
                const docNumberElement = document.getElementById('docNumber');
                if (docNumberElement) docNumberElement.value = documentData.docNumber;
                
                const formIdElement = document.getElementById('formId');
                if (formIdElement) formIdElement.value = documentData.formId;
                
                const titleElement = document.getElementById('title');
                if (titleElement) titleElement.value = documentData.title;
                
                console.log('Thymeleaf에서 문서 데이터 초기화 완료:', documentData);
            } else {
                // DOM에서 직접 데이터 로드
                console.log('DOM에서 문서 데이터를 로드합니다.');
                
                const docIdElement = document.getElementById('docId');
                documentData.docId = docIdElement ? docIdElement.value : '';
                
                const docNumberElement = document.getElementById('docNumber');
                documentData.docNumber = docNumberElement ? docNumberElement.value : '';
                
                const formIdElement = document.getElementById('formId');
                documentData.formId = formIdElement ? formIdElement.value : '';
                
                const titleElement = document.getElementById('title');
                documentData.title = titleElement ? titleElement.value : '';
                
                // content는 에디터 초기화 후 로드됩니다.
                console.log('DOM에서 문서 데이터 로드 완료:', documentData);
            }
        } catch (error) {
            console.error('데이터 초기화 중 오류:', error);
            throw error;
        }
    }
    
    /**
     * UI 초기화 함수
     * 필요한 UI 스타일이나 동적 요소를 초기화합니다.
     * 
     * @returns {Promise<void>}
     */
    async function initializeUI() {
        try {
            console.log('UI 초기화를 시작합니다.');
            
            // 로딩 오버레이 스타일 추가
            addLoadingOverlayStyle();
            
            // 에디터 컨테이너에 상대 위치 클래스 추가
            const editorCard = document.getElementById('contentEditor');
            if (editorCard) {
                const cardParent = editorCard.closest('.card');
                if (cardParent) {
                    cardParent.classList.add('relative-container');
                    console.log('에디터 카드에 relative-container 클래스 추가됨');
                }
            }
            
            console.log('UI 초기화가 완료되었습니다.');
        } catch (error) {
            console.error('UI 초기화 중 오류:', error);
            throw error;
        }
    }
    
    /**
     * 이벤트 핸들러 설정 함수
     * 버튼 클릭 및 기타 이벤트 리스너를 등록합니다.
     * 
     * @returns {Promise<void>}
     */
    async function setupEventHandlers() {
        try {
            console.log('이벤트 핸들러 등록을 시작합니다.');
            
            // 임시저장 버튼 클릭 이벤트
            const tempSaveBtn = document.getElementById('btnTempSave');
            if (tempSaveBtn) {
                tempSaveBtn.addEventListener('click', async function() {
                    await saveDocument(true);
                });
                console.log('임시저장 버튼 이벤트 등록 완료');
            }
            
            // 결재요청 버튼 클릭 이벤트
            const submitBtn = document.getElementById('btnSubmit');
            if (submitBtn) {
                submitBtn.addEventListener('click', async function() {
                    await saveDocument(false);
                });
                console.log('결재요청 버튼 이벤트 등록 완료');
            }
            
            // 취소 버튼 클릭 이벤트 (있는 경우)
            const cancelBtn = document.getElementById('btnCancel');
            if (cancelBtn) {
                cancelBtn.addEventListener('click', async function() {
                    const confirmed = await AlertUtil.showConfirm({
                        title: '작성 취소',
                        text: '현재 작성 중인 내용이 저장되지 않습니다. 정말 취소하시겠습니까?',
                        icon: 'warning'
                    });
                    
                    if (confirmed) {
                        location.href = '/eapproval/documents';
                    }
                });
                console.log('취소 버튼 이벤트 등록 완료');
            }
            
            console.log('이벤트 핸들러 등록이 완료되었습니다.');
        } catch (error) {
            console.error('이벤트 핸들러 등록 중 오류:', error);
            throw error;
        }
    }
    
    /**
     * 로딩 오버레이 스타일 추가 함수
     * 문서 헤드에 필요한 CSS 스타일을 동적으로 추가합니다.
     */
    function addLoadingOverlayStyle() {
        try {
            // 이미 스타일이 있는지 확인
            if (document.getElementById('loadingOverlayStyle')) {
                return;
            }
            
            // 스타일 요소 생성
            const styleEl = document.createElement('style');
            styleEl.id = 'loadingOverlayStyle';
            styleEl.textContent = `
                .loading-overlay {
                    position: absolute;
                    top: 0;
                    left: 0;
                    width: 100%;
                    height: 100%;
                    background: rgba(255, 255, 255, 0.8);
                    display: flex;
                    justify-content: center;
                    align-items: center;
                    z-index: 1000;
                }
                
                .relative-container {
                    position: relative;
                }
            `;
            
            // 문서 헤드에 스타일 추가
            document.head.appendChild(styleEl);
            console.log('로딩 오버레이 스타일이 추가되었습니다.');
        } catch (error) {
            console.error('스타일 추가 중 오류:', error);
            // 비 중요 오류이므로 진행 가능
        }
    }
    
    //===========================================================================
    // 문서 처리 함수
    //===========================================================================
    
    /**
     * 에디터 내 폼 요소의 값을 HTML에 반영
     * 저장 전 에디터 내의 모든 폼 요소(select, input, textarea)의 
     * 현재 값을 HTML 속성으로 업데이트합니다.
     */
    function updateFormValuesInEditor() {
        try {
            console.log('에디터 내 폼 요소 값 업데이트 시작');
            
            // 에디터 프레임 찾기 (Summernote의 편집 가능 영역)
            const editorFrame = document.querySelector('.note-editable');
            if (!editorFrame) {
                console.warn('에디터 편집 영역을 찾을 수 없습니다.');
                return;
            }
            
            // 셀렉트 박스 업데이트
            const selects = editorFrame.querySelectorAll('select');
            selects.forEach(select => {
                console.log(`셀렉트 박스 처리: id=${select.id || 'unnamed'}, 현재 값=${select.value}`);
                
                // 모든 옵션에서 selected 속성 제거
                Array.from(select.options).forEach(option => {
                    option.removeAttribute('selected');
                });
                
                // 현재 선택된 옵션에 selected 속성 추가
                if (select.selectedIndex >= 0) {
                    select.options[select.selectedIndex].setAttribute('selected', 'selected');
                }
            });
            
            // 입력 필드 업데이트
            const inputs = editorFrame.querySelectorAll('input');
            inputs.forEach(input => {
                console.log(`입력 필드 처리: id=${input.id || 'unnamed'}, type=${input.type}, 현재 값=${input.value}`);
                
                if (input.type === 'text' || input.type === 'number' || input.type === 'date' || input.type === 'hidden') {
                    // 텍스트, 숫자, 날짜 필드는 value 속성 업데이트
                    input.setAttribute('value', input.value);
                } else if (input.type === 'checkbox' || input.type === 'radio') {
                    // 체크박스와 라디오 버튼은 checked 속성 업데이트
                    if (input.checked) {
                        input.setAttribute('checked', 'checked');
                    } else {
                        input.removeAttribute('checked');
                    }
                }
            });
            
            // 텍스트 영역 업데이트
            const textareas = editorFrame.querySelectorAll('textarea');
            textareas.forEach(textarea => {
                console.log(`텍스트 영역 처리: id=${textarea.id || 'unnamed'}, 현재 값 길이=${textarea.value.length}`);
                
                // textarea의 내용을 현재 값으로 업데이트
                textarea.textContent = textarea.value;
            });
            
            console.log('에디터 내 폼 요소 값 업데이트 완료');
        } catch (error) {
            console.error('에디터 내 폼 요소 값 업데이트 중 오류:', error);
            // 오류가 발생해도 저장 프로세스 계속 진행
        }
    }
    
    /**
     * 폼 유효성 검사 함수
     * 문서 폼의 필수 필드 및 결재선을 검증합니다.
     * 
     * @returns {Promise<boolean>} 유효성 검사 결과
     */
    async function validateForm() {
        try {
            console.log('문서 폼 유효성 검사를 시작합니다.');
            
            // 필수 필드 검사
            const formId = document.getElementById('formId').value;
            console.log('양식 ID 확인:', formId);
            if (!formId) {
                console.error('유효성 검사 실패: 양식 ID가 없음');
                await AlertUtil.showWarning('유효성 검사', '문서 양식을 선택해주세요.');
                document.getElementById('formId').focus();
                return false;
            }
            
            const title = document.getElementById('title').value.trim();
            console.log('제목 확인:', title);
            if (!title) {
                console.error('유효성 검사 실패: 제목이 없음');
                await AlertUtil.showWarning('유효성 검사', '제목을 입력해주세요.');
                document.getElementById('title').focus();
                return false;
            }
            
            // 내용 검사 - 다양한 방법으로 시도
            const editorElement = document.getElementById('contentEditor');
            let content = '';
            
            if (editorElement) {
                // 방법 1: FormContentLoader 사용 시도
                if (window.FormContentLoader && typeof FormContentLoader.getEditorContent === 'function') {
                    content = FormContentLoader.getEditorContent();
                    console.log('FormContentLoader에서 에디터 내용 길이:', content.length);
                } 
                // 방법 2: Summernote API 직접 사용
                else if (window.$ && typeof $(editorElement).summernote === 'function') {
                    content = $(editorElement).summernote('code');
                    console.log('Summernote API에서 에디터 내용 길이:', content.length);
                } 
                // 방법 3: DOM에서 직접 가져오기
                else {
                    content = editorElement.innerHTML || '';
                    console.log('DOM에서 에디터 내용 길이:', content.length);
                }
            } else {
                console.warn('contentEditor 요소를 찾을 수 없습니다.');
            }
            
            // 내용이 비어있는지 검사
            if (!content || content === '<p><br></p>' || content === '<p></p>' || content.trim() === '') {
                console.error('유효성 검사 실패: 내용이 없음');
                await AlertUtil.showWarning('유효성 검사', '내용을 입력해주세요.');
                
                // 에디터에 포커스 설정 (가능한 경우)
                if (editorElement && window.$ && typeof $(editorElement).summernote === 'function') {
                    $(editorElement).summernote('focus');
                }
                return false;
            }
            
            // 결재선 유효성 검사 (ApprovalLineManager 의존)
            if (window.ApprovalLineManager && typeof ApprovalLineManager.validateApprovalLines === 'function') {
                console.log('결재선 유효성 검사 시작');
                const approvalLinesValid = await ApprovalLineManager.validateApprovalLines();
                if (!approvalLinesValid) {
                    console.error('유효성 검사 실패: 결재선 유효성 검사 실패');
                    return false;
                }
            } else {
                console.warn('ApprovalLineManager가 없어 결재선 검증을 건너뜁니다.');
            }
            
            console.log('문서 폼 유효성 검사 통과');
            return true;
        } catch (error) {
            console.error('유효성 검사 중 오류:', error);
            await AlertUtil.showError('검증 오류', '폼 유효성 검사 중 오류가 발생했습니다: ' + error.message);
            return false;
        }
    }
    
    /**
     * 문서 저장 함수 (임시저장 또는 결재요청)
     * 문서 데이터를 수집하여 API를 호출하고 결과를 처리합니다.
     * 
     * @param {boolean} isTempSave - 임시저장 여부 (true: 임시저장, false: 결재요청)
     * @returns {Promise<boolean>} 저장 성공 여부
     */
    async function saveDocument(isTempSave) {
        try {
            console.log(`문서 ${isTempSave ? '임시저장' : '결재요청'} 프로세스 시작`);
            
            // 유효성 검사
            console.log('유효성 검사 시작');
            if (!await validateForm()) {
                console.error('유효성 검사 실패로 저장 중단');
                return false;
            }
            
            console.log(`문서 ${isTempSave ? '임시저장' : '결재요청'} 데이터 준비 시작`);
            
            // 폼 데이터 수집
            const docId = documentData.docId || '';
            const docNumber = documentData.docNumber || '';
            const formId = document.getElementById('formId').value;
            const title = document.getElementById('title').value.trim();
            
            // 중요: 저장 전 에디터 내 폼 요소 값 업데이트
            updateFormValuesInEditor();
            
            // 문서 내용 (에디터) - 다양한 방법으로 시도
            const editorElement = document.getElementById('contentEditor');
            let content = '';
            
            if (editorElement) {
                // 방법 1: FormContentLoader 사용 시도
                if (window.FormContentLoader && typeof FormContentLoader.getEditorContent === 'function') {
                    content = FormContentLoader.getEditorContent();
                    console.log('FormContentLoader에서 에디터 내용 가져옴 - 길이:', content.length);
                } 
                // 방법 2: Summernote API 직접 사용
                else if (window.$ && typeof $(editorElement).summernote === 'function') {
                    content = $(editorElement).summernote('code');
                    console.log('Summernote API에서 에디터 내용 가져옴 - 길이:', content.length);
                } 
                // 방법 3: DOM에서 직접 가져오기
                else {
                    content = editorElement.innerHTML || '';
                    console.log('DOM에서 에디터 내용 가져옴 - 길이:', content.length);
                }
            } else {
                console.warn('contentEditor 요소를 찾을 수 없습니다.');
            }
            
            // 결재선 데이터
            let approvalLines = [];
            if (window.ApprovalLineManager && typeof ApprovalLineManager.getApprovalLines === 'function') {
                approvalLines = ApprovalLineManager.getApprovalLines();
                console.log('결재선 데이터:', approvalLines);
            } else {
                console.warn('ApprovalLineManager를 찾을 수 없거나 getApprovalLines 함수가 없습니다.');
            }
            
            const approvalLinesJson = JSON.stringify(approvalLines);

            // 기본 데이터 준비 - JSON 객체로 변경
            const requestData = {
                docId: docId,
                docNumber: docNumber,
                formId: formId,
                title: title,
                content: content,
                isTempSave: isTempSave,
                approvalLinesJson: approvalLinesJson
            };
            
            // FormData 주요 필드 
            console.log('서버로 전송하는 데이터:', requestData);
            console.log('API 요청 데이터 준비 완료:');
            console.log('- docId:', docId);
            console.log('- formId:', formId);
            console.log('- title:', title);
            console.log('- content 길이:', content.length);
            console.log('- isTempSave 형식:', requestData.isTempSave);
            console.log('- approvalLinesJson:', approvalLinesJson);

            // 리팩토링된 ApiUtil 사용하여 API 호출
            const response = await ApiUtil.postWithLoading(
                API_URLS.SAVE,
                requestData,
                isTempSave ? '임시저장 중...' : '결재요청 중...'
            );

            // 응답 처리 (가이드라인에 맞게 수정)
            if (response.success) {
                // 성공 알림
                await AlertUtil.showSuccess(
                    '저장 완료', 
                    isTempSave ? '문서가 임시저장되었습니다.' : '결재요청이 완료되었습니다.'
                );
                
                // 저장 완료 후 페이지 이동
                setTimeout(() => {
                    if (isTempSave) {
                        // 임시저장 완료 시 문서함으로 이동
                        location.href = '/eapproval/documents';
                    } else {
                        // 결재요청 완료 시 상세 보기 페이지로 이동
                        // 대문자 키(DOC_ID)로 접근하도록 수정
                        const docId = response.data?.DOC_ID || documentData.docId;
                        if (docId) {
                            location.href = `/eapproval/document/view/${docId}`;
                        } else {
                            location.href = '/eapproval/documents';
                        }
                    }
                }, 500);
                
                return true;
            } else {
                // 실패 처리
                await AlertUtil.showError(
                    '저장 실패', 
                    response.message || '문서 저장 중 오류가 발생했습니다.'
                );
                return false;
            }
        } catch (error) {
            console.error('문서 저장 처리 중 오류:', error);
            console.error('오류 세부정보:', error.message);
            await ApiUtil.handleApiError(error, '저장 오류', '문서 저장 중 오류가 발생했습니다.');
            return false;
        }
    }
    
    //===========================================================================
    // 유틸리티 및 접근자 함수
    //===========================================================================
    
    /**
     * 문서 ID 가져오기 함수
     * 현재 문서의 ID를 반환합니다.
     * 
     * @returns {string} 문서 ID
     */
    function getDocumentId() {
        return documentData.docId;
    }
    
    /**
     * 문서 내용 가져오기 함수
     * 현재 문서의 내용을 반환합니다.
     * 
     * @returns {string} 문서 내용 HTML
     */
    function getDocumentContent() {
        return documentData.content;
    }
    
    /**
     * 문서 데이터 업데이트 함수
     * 특정 필드의 문서 데이터를 업데이트합니다.
     * 
     * @param {string} field - 업데이트할 필드명
     * @param {*} value - 새 값
     */
    function updateDocumentData(field, value) {
        if (field in documentData) {
            documentData[field] = value;
            console.log(`문서 데이터 '${field}' 필드가 업데이트되었습니다.`);
            
            // DOM 요소도 함께 업데이트 (있는 경우)
            const element = document.getElementById(field);
            if (element) {
                element.value = value;
            }
        } else {
            console.warn(`존재하지 않는 필드 '${field}'에 대한 업데이트 시도가 무시되었습니다.`);
        }
    }
    
    //===========================================================================
    // 공개 API - 외부에서 접근 가능한 메서드
    //===========================================================================
    
    return {
        // 초기화 및 기본 기능
        initialize,          // 모듈 초기화
        
        // 문서 처리 함수
        validateForm,        // 폼 유효성 검사
        saveDocument,        // 문서 저장/제출
        updateFormValuesInEditor, // 에디터 내 폼 요소 값 업데이트
        
        // 유틸리티 및 접근자 함수
        getDocumentId,       // 문서 ID 조회
        getDocumentContent,  // 문서 내용 조회
        updateDocumentData   // 문서 데이터 업데이트
    };
})();

// DOM 로드 시 documentForm.js에서 초기화하므로 여기서는 자동 초기화하지 않음