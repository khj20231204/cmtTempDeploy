/**
 * FormContentLoader - 문서 양식 로드 모듈
 * 
 * 전자결재 문서 양식을 불러오고 관리하는 기능을 제공합니다.
 * - 양식 내용 불러오기 및 적용
 * - 에디터 초기화 및 관리
 * - 양식 변경 이벤트 처리
 * 
 * @version 1.2.0
 * @since 2025-04-04
 * @update 2025-04-04 - API 응답 처리 리팩토링 및 대문자 키 일관성 개선
 */
const FormContentLoader = (function() {
    //===========================================================================
    // 모듈 내부 변수
    //===========================================================================
    
    /**
     * 에디터 인스턴스 참조
     * Summernote 에디터 인스턴스를 추적합니다.
     */
    let editorInstance = null;
    
    /**
     * API URL 상수 정의
     * 양식 관련 API 엔드포인트 정의
     */
    const API_URLS = {
        FORM: (formId) => `/api/eapproval/form/${formId}` // 양식 조회 API
    };
    
    /**
     * 에디터 기본 옵션
     * Summernote 에디터 초기화에 사용되는 기본 옵션
     */
    const EDITOR_OPTIONS = {
        height: 600,
        lang: 'ko-KR',
        placeholder: '내용을 입력하세요',
        toolbar: [
            ['style', ['style']],
            ['font', ['bold', 'underline', 'clear']],
            ['color', ['color']],
            ['para', ['ul', 'ol', 'paragraph']],
            ['table', ['table']],
            ['insert', ['link']],
            ['view', ['fullscreen', 'codeview', 'help']]
        ]
    };
    
    //===========================================================================
    // 초기화 및 이벤트 처리 함수
    //===========================================================================
    
    /**
     * 모듈 초기화 함수
     * 양식 콘텐츠 로더를 초기화하고 이벤트 리스너를 등록합니다.
     * 
     * @returns {Promise<void>}
     */
    async function initialize() {
        try {
            console.log('FormContentLoader 초기화를 시작합니다.');
            
            // 이벤트 핸들러 설정
            await setupEventHandlers();
            
            console.log('FormContentLoader 초기화가 완료되었습니다.');
        } catch (error) {
            console.error('FormContentLoader 초기화 중 오류:', error);
            await AlertUtil.showError('초기화 오류', '양식 로더 초기화 중 오류가 발생했습니다.');
        }
    }
    
    /**
     * 이벤트 핸들러 설정 함수
     * 양식 선택 관련 이벤트 리스너를 등록합니다.
     * 
     * @returns {Promise<void>}
     */
    async function setupEventHandlers() {
        try {
            console.log('양식 이벤트 핸들러 등록을 시작합니다.');
            
            // 양식 선택 변경 이벤트
            const formIdSelect = document.getElementById('formId');
            if (formIdSelect) {
                formIdSelect.addEventListener('change', async function() {
                    const formId = this.value;
                    if (formId) {
                        await loadFormContent(formId);
                    } else {
                        // 선택 취소 시 빈 내용으로 초기화
                        if (editorInstance) {
                            $('#contentEditor').summernote('code', '');
                        }
                    }
                });
                console.log('양식 선택 변경 이벤트 등록 완료');
            } else {
                console.warn('formId 요소를 찾을 수 없습니다.');
            }
            
            console.log('양식 이벤트 핸들러 등록이 완료되었습니다.');
        } catch (error) {
            console.error('이벤트 핸들러 등록 중 오류:', error);
            throw error;
        }
    }
    
    /**
     * 에디터 초기화 함수
     * Summernote 에디터를 초기화합니다.
     * 
     * @param {Object} [customOptions] - 에디터 사용자 정의 옵션 (선택적)
     * @returns {Promise<void>}
     */
    async function initializeEditor(customOptions = {}) {
        try {
            console.log('에디터 초기화를 시작합니다.');
            
            // 에디터 요소 확인
            const editorElement = document.getElementById('contentEditor');
            if (!editorElement) {
                throw new Error('contentEditor 요소를 찾을 수 없습니다.');
            }
            
            // jQuery 객체 확인 (Summernote는 jQuery 플러그인)
            if (typeof $ !== 'function' || typeof $.fn.summernote !== 'function') {
                throw new Error('jQuery 또는 Summernote가 로드되지 않았습니다.');
            }
            
            // 기본 옵션과 사용자 정의 옵션 병합
            const options = {
                ...EDITOR_OPTIONS,
                ...customOptions,
                callbacks: {
                    ...customOptions.callbacks,
                    onInit: function() {
                        console.log('Summernote 에디터가 초기화되었습니다.');
                        editorInstance = this;
                        
                        // 초기 내용 설정 (DocumentFormManager 의존)
                        if (window.DocumentFormManager && typeof DocumentFormManager.getDocumentContent === 'function') {
                            const content = DocumentFormManager.getDocumentContent();
                            if (content) {
                                $('#contentEditor').summernote('code', content);
                                console.log('기존 문서 내용이 에디터에 로드되었습니다.');
                            }
                        }
                        
                        // 사용자 정의 onInit 함수 호출 (있는 경우)
                        if (customOptions.callbacks && typeof customOptions.callbacks.onInit === 'function') {
                            customOptions.callbacks.onInit.call(this);
                        }
                    }
                }
            };
            
            // Summernote 에디터 초기화
            $(editorElement).summernote(options);
            
            console.log('에디터 초기화가 완료되었습니다.');
        } catch (error) {
            console.error('에디터 초기화 중 오류:', error);
            await AlertUtil.showError('에디터 오류', '에디터 초기화 중 오류가 발생했습니다.');
        }
    }
    
    //===========================================================================
    // 양식 데이터 관리 함수
    //===========================================================================
    
	/**
	 * 양식 내용 로드 함수
	 * 선택된 양식 ID에 해당하는 양식 내용을 API로 조회하여 에디터에 적용합니다.
	 * 
	 * @param {string} formId - 양식 ID
	 * @returns {Promise<boolean>} 로드 성공 여부
	 */
	async function loadFormContent(formId) {
	    try {
	        if (!formId) {
	            console.warn('유효한 양식 ID가 제공되지 않았습니다.');
	            return false;
	        }
	        
	        console.log(`양식 ID ${formId} 내용 로드 시작`);
	        
	        // 에디터 컨테이너에 로딩 오버레이 추가
	        const editorElement = document.getElementById('contentEditor');
	        const editorContainer = editorElement ? editorElement.closest('.card') : null;
	        
	        if (editorContainer) {
	            // 기존 오버레이 제거
	            const existingOverlay = editorContainer.querySelector('.loading-overlay');
	            if (existingOverlay) {
	                editorContainer.removeChild(existingOverlay);
	            }
	            
	            // 새 오버레이 추가
	            const overlay = document.createElement('div');
	            overlay.className = 'loading-overlay';
	            overlay.innerHTML = `
	                <div class="spinner-border text-primary" role="status">
	                    <span class="visually-hidden">로딩 중...</span>
	                </div>
	            `;
	            editorContainer.appendChild(overlay);
	        }
	        
	        // 리팩토링된 ApiUtil 사용하여 API 호출
	        try {
	            const response = await ApiUtil.getWithLoading(
	                API_URLS.FORM(formId),
	                null,
	                '양식 로드 중...'
	            );
	            
	            // 오버레이 제거
	            if (editorContainer) {
	                const overlay = editorContainer.querySelector('.loading-overlay');
	                if (overlay) {
	                    editorContainer.removeChild(overlay);
	                }
	            }
	            
	            // 응답 확인
	            if (!response.success || !response.data) {
	                throw new Error(response.message || '양식을 불러올 수 없습니다.');
	            }
	            
	            // 양식 내용 추출 - 가이드라인에 따라 대문자 키 사용
	            const formData = response.data;
	            let formContent = formData.FORM_CONTENT || '';
	            
	            console.log('양식 내용을 성공적으로 로드했습니다.');
	            
	            // 에디터 존재 확인
	            if (!editorElement) {
	                throw new Error('에디터 요소를 찾을 수 없습니다.');
	            }
	            
	            // 플레이스홀더 처리 (TemplateProcessor 사용 가능한 경우)
				if (window.TemplateProcessor && typeof TemplateProcessor.processTemplateFromServer === 'function') {
				            try {
				                console.log('양식 내용 플레이스홀더 처리 시작');
				                formContent = await TemplateProcessor.processTemplateFromServer(formContent);
				                console.log('플레이스홀더가 사용자 정보로 치환되었습니다.');
				            } catch (templateError) {
				                console.error('플레이스홀더 처리 중 오류:', templateError);
				                // 오류가 발생해도 원본 내용은 계속 표시
				            }
				        } else {
				            console.warn('TemplateProcessor를 찾을 수 없거나 초기화되지 않았습니다.');
				        }
	            
	            // 에디터에 내용 설정
	            if (typeof $.fn.summernote === 'function') {
	                $(editorElement).summernote('code', formContent);
	                console.log('양식 내용이 에디터에 적용되었습니다.');
	            } else {
	                throw new Error('Summernote 에디터가 초기화되지 않았습니다.');
	            }
	            
	            return true;
	        } catch (apiError) {
	            // 오버레이 제거
	            if (editorContainer) {
	                const overlay = editorContainer.querySelector('.loading-overlay');
	                if (overlay) {
	                    editorContainer.removeChild(overlay);
	                }
	            }
	            
	            // API 오류 처리
	            await ApiUtil.handleApiError(apiError, '양식 로드 실패');
	            return false;
	        }
	    } catch (error) {
	        console.error('양식 로드 중 오류:', error);
	        await AlertUtil.showWarning('양식 로드 실패', error.message || '양식을 불러올 수 없습니다.');
	        return false;
	    }
	}
    
    /**
     * 현재 에디터 내용 가져오기 함수
     * Summernote 에디터의 현재 내용을 HTML 형태로 반환합니다.
     * 
     * @returns {string} 에디터 내용 HTML
     */
    function getEditorContent() {
        const editorElement = document.getElementById('contentEditor');
        if (!editorElement) {
            console.warn('contentEditor 요소를 찾을 수 없습니다.');
            return '';
        }
        
        // Summernote 사용 가능 여부 확인
        if (typeof $.fn.summernote !== 'function') {
            console.warn('Summernote 에디터가 초기화되지 않았습니다.');
            return editorElement.innerHTML || '';
        }
        
        return $(editorElement).summernote('code');
    }
    
    /**
     * 에디터 내용 설정 함수
     * Summernote 에디터의 내용을 지정된 HTML로 설정합니다.
     * 
     * @param {string} html - 설정할 HTML 내용
     * @returns {boolean} 설정 성공 여부
     */
    function setEditorContent(html) {
        try {
            const editorElement = document.getElementById('contentEditor');
            if (!editorElement) {
                console.warn('contentEditor 요소를 찾을 수 없습니다.');
                return false;
            }
            
            // Summernote 사용 가능 여부 확인
            if (typeof $.fn.summernote !== 'function') {
                console.warn('Summernote 에디터가 초기화되지 않았습니다.');
                editorElement.innerHTML = html || '';
                return true;
            }
            
            $(editorElement).summernote('code', html || '');
            console.log('에디터 내용이 업데이트되었습니다.');
            return true;
        } catch (error) {
            console.error('에디터 내용 설정 중 오류:', error);
            return false;
        }
    }
    
    //===========================================================================
    // 공개 API - 외부에서 접근 가능한 메서드
    //===========================================================================
    
    return {
        // 초기화 및 기본 기능
        initialize,             // 모듈 초기화
        initializeEditor,       // 에디터 초기화
        
        // 양식 데이터 관리
        loadFormContent,        // 양식 내용 로드
        getEditorContent,       // 에디터 내용 가져오기
        setEditorContent        // 에디터 내용 설정
    };
})();

// DOM 로드 시 documentForm.js에서 초기화하므로 여기서는 자동 초기화하지 않음