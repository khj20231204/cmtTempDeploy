/**
 * 리팩토링된 UIUtil - UI 유틸리티 모듈
 * 
 * UI 관련 공통 기능을 제공하는 라이브러리입니다.
 * 
 * @version 1.0.0
 * @since 2025-03-24
 */

const UIUtil = (function() {
    // =============================
    // 이벤트 처리 관련 함수
    // =============================
    
    /**
     * 이벤트 리스너 등록 함수
     * 여러 요소에 클릭 이벤트 리스너를 일괄 등록합니다.
     * 
     * @param {Object} elements - 요소ID와 콜백 함수 맵핑 객체
     * @returns {Promise<Object>} 등록 결과 객체 (성공 및 실패 요소 목록)
     */
    async function registerEventListeners(elements) {
        try {
            console.log('이벤트 리스너 등록을 시작합니다.');
            
            if (!elements || typeof elements !== 'object') {
                throw new Error('유효한 이벤트 맵핑 객체가 필요합니다.');
            }
            
            const result = {
                success: [],
                failed: []
            };
            
            // 모든 요소에 이벤트 등록 시도
            for (const elementId of Object.keys(elements)) {
                try {
                    const element = document.getElementById(elementId);
                    const callback = elements[elementId];
                    
                    if (element && typeof callback === 'function') {
                        element.addEventListener('click', async (e) => {
                            try {
                                await callback(e);
                            } catch (callbackError) {
                                console.error(`이벤트 핸들러 실행 중 오류(${elementId}):`, callbackError);
                            }
                        });
                        console.log(`'${elementId}' 엘리먼트에 이벤트 리스너가 등록되었습니다.`);
                        result.success.push(elementId);
                    } else {
                        throw new Error(`'${elementId}' 엘리먼트를 찾을 수 없거나 콜백이 함수가 아닙니다.`);
                    }
                } catch (elementError) {
                    console.warn(elementError.message);
                    result.failed.push({ 
                        id: elementId, 
                        reason: elementError.message 
                    });
                }
            }
            
            console.log('이벤트 리스너 등록이 완료되었습니다.', result);
            return result;
        } catch (error) {
            console.error('이벤트 리스너 등록 중 오류 발생:', error);
            throw error;
        }
    }
    
    /**
     * 엔터키 검색 이벤트 바인딩 함수
     * 입력 필드에서 엔터 키를 누를 때 검색 콜백을 실행하도록 설정합니다.
     * 
     * @param {string|Array<string>} inputIds - 입력 필드 ID 또는 ID 배열
     * @param {Function} searchCallback - 검색 콜백 함수
     * @returns {Promise<boolean>} 성공 여부를 담은 Promise
     */
    async function bindEnterKeySearch(inputIds, searchCallback) {
        try {
            // 문자열인 경우 배열로 변환
            const ids = Array.isArray(inputIds) ? inputIds : [inputIds];
            
            if (ids.length === 0) {
                throw new Error('입력 필드 ID가 필요합니다.');
            }
            
            if (typeof searchCallback !== 'function') {
                throw new Error('검색 콜백은 함수여야 합니다.');
            }
            
            // 각 입력 필드에 이벤트 바인딩
            for (const inputId of ids) {
                const inputElement = document.getElementById(inputId);
                
                if (!inputElement) {
                    console.warn(`'${inputId}' 입력 필드를 찾을 수 없습니다.`);
                    continue;
                }
                
                // 이벤트 핸들러 함수
                const keyupHandler = async function(e) {
                    if (e.key === 'Enter') {
                        try {
                            await searchCallback();
                        } catch (error) {
                            console.error('엔터키 검색 콜백 실행 중 오류:', error);
                        }
                    }
                };
                
                // 기존 이벤트 제거 (중복 방지)
                inputElement.removeEventListener('keyup', keyupHandler);
                
                // 이벤트 등록
                inputElement.addEventListener('keyup', keyupHandler);
                
                console.log(`'${inputId}' 입력 필드에 엔터키 이벤트가 바인딩되었습니다.`);
            }
            
            return true;
        } catch (error) {
            console.error('엔터키 이벤트 바인딩 중 오류 발생:', error);
            return false;
        }
    }
    
    /**
     * 이벤트 리스너 제거 함수
     * 특정 요소에서 이벤트 리스너를 제거합니다.
     * 
     * @param {string} elementId - 요소 ID
     * @param {string} eventType - 이벤트 유형 (예: 'click', 'keyup')
     * @param {Function} [handler] - 제거할 특정 핸들러 (생략 시 해당 유형의 모든 핸들러 제거)
     * @returns {Promise<boolean>} 성공 여부를 담은 Promise
     */
    async function removeEventListener(elementId, eventType, handler) {
        try {
            const element = document.getElementById(elementId);
            
            if (!element) {
                throw new Error(`'${elementId}' 요소를 찾을 수 없습니다.`);
            }
            
            if (!eventType) {
                throw new Error('이벤트 유형은 필수입니다.');
            }
            
            // 핸들러가 제공된 경우 특정 핸들러만 제거
            if (handler && typeof handler === 'function') {
                element.removeEventListener(eventType, handler);
                console.log(`'${elementId}' 요소에서 '${eventType}' 이벤트 핸들러가 제거되었습니다.`);
            } 
            // 핸들러가 없는 경우, 모든 해당 유형 핸들러 제거는 클론 노드 교체로 구현
            else {
                const clone = element.cloneNode(true);
                element.parentNode.replaceChild(clone, element);
                console.log(`'${elementId}' 요소에서 모든 '${eventType}' 이벤트 핸들러가 제거되었습니다.`);
            }
            
            return true;
        } catch (error) {
            console.error('이벤트 리스너 제거 중 오류 발생:', error);
            return false;
        }
    }
    
    // =============================
    // 폼 관련 함수
    // =============================
    
    /**
     * 입력 폼 초기화 함수
     * 폼 내의 모든 입력 요소 값을 초기화합니다.
     * 
     * @param {string} formId - 폼 ID
     * @param {Object} [options] - 초기화 옵션
     * @param {boolean} [options.preserveHidden=false] - 숨겨진 필드 값 유지 여부
     * @param {Array<string>} [options.exclude=[]] - 초기화에서 제외할 필드 이름 배열
     * @returns {Promise<boolean>} 성공 여부를 담은 Promise
     */
    async function clearForm(formId, options = {}) {
        try {
            const form = document.getElementById(formId);
            
            if (!form) {
                throw new Error(`'${formId}' 폼을 찾을 수 없습니다.`);
            }
            
            const { 
                preserveHidden = false,
                exclude = []
            } = options;
            
            // 모든 입력 요소 초기화
            const inputs = form.querySelectorAll('input, select, textarea');
            
            inputs.forEach(input => {
                // 제외 목록에 있으면 건너뛰기
                if (input.name && exclude.includes(input.name)) {
                    return;
                }
                
                // 숨겨진 필드 건너뛰기 옵션
                if (preserveHidden && input.type === 'hidden') {
                    return;
                }
                
                const type = input.type.toLowerCase();
                
                if (type === 'checkbox' || type === 'radio') {
                    input.checked = false;
                } else if (input.tagName.toLowerCase() === 'select') {
                    input.selectedIndex = 0;
                } else {
                    input.value = '';
                }
            });
            
            console.log(`'${formId}' 폼이 초기화되었습니다.`);
            return true;
        } catch (error) {
            console.error('폼 초기화 중 오류 발생:', error);
            return false;
        }
    }
    
    /**
     * 입력 폼 값 설정 함수
     * 폼 요소에 데이터 객체의 값을 설정합니다.
     * 
     * @param {string} formId - 폼 ID
     * @param {Object} data - 설정할 데이터
     * @param {Object} [options] - 설정 옵션
     * @param {boolean} [options.triggerChange=false] - 값 변경 후 change 이벤트 발생 여부
     * @returns {Promise<Object>} 설정 결과 객체를 담은 Promise
     */
    async function setFormValues(formId, data, options = {}) {
        try {
            const form = document.getElementById(formId);
            
            if (!form) {
                throw new Error(`'${formId}' 폼을 찾을 수 없습니다.`);
            }
            
            if (!data || typeof data !== 'object') {
                throw new Error('유효한 데이터 객체가 필요합니다.');
            }
            
            const { triggerChange = false } = options;
            const result = { success: [], failed: [] };
            
            // 데이터 프로퍼티를 기반으로 입력 요소 값 설정
            for (const [key, value] of Object.entries(data)) {
                try {
                    // name 속성으로 요소 찾기
                    const elements = form.querySelectorAll(`[name="${key}"]`);
                    
                    if (elements.length === 0) {
                        throw new Error(`'${key}' 필드를 찾을 수 없습니다.`);
                    }
                    
                    elements.forEach(input => {
                        const type = input.type ? input.type.toLowerCase() : '';
                        const tagName = input.tagName.toLowerCase();
                        
                        if (type === 'checkbox') {
                            input.checked = value === true || value === 'Y' || value === 1;
                        } else if (type === 'radio') {
                            input.checked = input.value == value; // 의도적 동등 비교(==)
                        } else if (tagName === 'select') {
                            if (value !== null && value !== undefined) {
                                input.value = value;
                            }
                        } else {
                            input.value = value !== null && value !== undefined ? value : '';
                        }
                        
                        // change 이벤트 트리거 (옵션이 활성화된 경우)
                        if (triggerChange) {
                            const event = new Event('change', { bubbles: true });
                            input.dispatchEvent(event);
                        }
                    });
                    
                    result.success.push(key);
                } catch (fieldError) {
                    console.warn(`'${key}' 필드 설정 중 오류:`, fieldError.message);
                    result.failed.push({ field: key, reason: fieldError.message });
                }
            }
            
            console.log(`'${formId}' 폼에 데이터가 설정되었습니다.`, result);
            return result;
        } catch (error) {
            console.error('폼 값 설정 중 오류 발생:', error);
            throw error;
        }
    }
    
    /**
     * 입력 폼 값 가져오기 함수
     * 폼 요소의 모든 값을 객체로 수집합니다.
     * 
     * @param {string} formId - 폼 ID
     * @param {Object} [options] - 옵션
     * @param {boolean} [options.includeDisabled=false] - 비활성화된 요소 포함 여부
     * @param {boolean} [options.trimValues=true] - 문자열 값 양쪽 공백 제거 여부
     * @returns {Promise<Object>} 폼 데이터 객체를 담은 Promise
     */
    async function getFormValues(formId, options = {}) {
        try {
            const form = document.getElementById(formId);
            
            if (!form) {
                throw new Error(`'${formId}' 폼을 찾을 수 없습니다.`);
            }
            
            const { 
                includeDisabled = false,
                trimValues = true
            } = options;
            
            const formData = {};
            
            // 모든 입력 요소에서 값 추출
            const inputs = form.querySelectorAll('input, select, textarea');
            
            inputs.forEach(input => {
                // name 속성이 없으면 건너뛰기
                if (!input.name) return;
                
                // 비활성화 요소 건너뛰기 옵션
                if (!includeDisabled && input.disabled) return;
                
                const name = input.name;
                const type = input.type ? input.type.toLowerCase() : '';
                
                if (type === 'checkbox') {
                    formData[name] = input.checked ? 'Y' : 'N';
                } else if (type === 'radio') {
                    if (input.checked) {
                        formData[name] = input.value;
                    }
                } else {
                    let value = input.value;
                    
                    // 문자열 값 양쪽 공백 제거 옵션
                    if (trimValues && typeof value === 'string') {
                        value = value.trim();
                    }
                    
                    formData[name] = value;
                }
            });
            
            return formData;
        } catch (error) {
            console.error('폼 값 가져오기 중 오류 발생:', error);
            throw error;
        }
    }
    
    // =============================
    // UI 표시/숨김 관련 함수
    // =============================
    
    /**
     * 요소 표시/숨김 토글 함수
     * 
     * @param {string|Array<string>} elementIds - 요소 ID 또는 ID 배열
     * @param {boolean} show - 표시 여부
     * @param {Object} [options] - 옵션
     * @param {string} [options.displayType='block'] - 표시 시 display 속성 값
     * @returns {Promise<Array>} 처리 결과 배열을 담은 Promise
     */
    async function toggleElement(elementIds, show, options = {}) {
        try {
            const ids = Array.isArray(elementIds) ? elementIds : [elementIds];
            const { displayType = 'block' } = options;
            
            const results = [];
            
            for (const elementId of ids) {
                try {
                    const element = document.getElementById(elementId);
                    
                    if (!element) {
                        throw new Error(`'${elementId}' 요소를 찾을 수 없습니다.`);
                    }
                    
                    element.style.display = show ? displayType : 'none';
                    
                    results.push({
                        id: elementId,
                        success: true,
                        visible: show
                    });
                } catch (elementError) {
                    console.warn(elementError.message);
                    results.push({
                        id: elementId,
                        success: false,
                        error: elementError.message
                    });
                }
            }
            
            return results;
        } catch (error) {
            console.error('요소 표시/숨김 토글 중 오류 발생:', error);
            throw error;
        }
    }
    
    /**
     * 로딩 표시기 함수
     * 
     * @param {boolean} show - 표시 여부
     * @param {string} [message='로딩 중...'] - 로딩 메시지
     * @param {Object} [options] - 로딩 옵션
     * @param {string} [options.containerId='loadingOverlay'] - 로딩 컨테이너 ID
     * @param {boolean} [options.fullScreen=true] - 전체 화면 여부
     * @returns {Promise<boolean>} 성공 여부를 담은 Promise
     */
    async function toggleLoading(show, message = '로딩 중...', options = {}) {
        try {
            const { 
                containerId = 'loadingOverlay',
                fullScreen = true
            } = options;
            
            // 로딩 오버레이 요소 찾기
            let loadingOverlay = document.getElementById(containerId);
            
            // 요소가 없고, 표시해야 하는 경우 생성
            if (!loadingOverlay && show) {
                // 로딩 오버레이 생성
                loadingOverlay = document.createElement('div');
                loadingOverlay.id = containerId;
                
                // 스타일 설정
                Object.assign(loadingOverlay.style, {
                    position: fullScreen ? 'fixed' : 'absolute',
                    top: '0',
                    left: '0',
                    width: '100%',
                    height: '100%',
                    backgroundColor: 'rgba(0, 0, 0, 0.5)',
                    display: 'flex',
                    justifyContent: 'center',
                    alignItems: 'center',
                    zIndex: '9999'
                });
                
                // 로딩 컨테이너
                const loadingContainer = document.createElement('div');
                Object.assign(loadingContainer.style, {
                    backgroundColor: 'white',
                    padding: '20px',
                    borderRadius: '5px',
                    textAlign: 'center',
                    boxShadow: '0 2px 10px rgba(0, 0, 0, 0.2)'
                });
                
                // 로딩 스피너
                const spinner = document.createElement('div');
                Object.assign(spinner.style, {
                    border: '5px solid #f3f3f3',
                    borderTop: '5px solid #3498db',
                    borderRadius: '50%',
                    width: '40px',
                    height: '40px',
                    margin: '0 auto 10px auto',
                    animation: 'spin 2s linear infinite'
                });
                
                // 스피너 애니메이션
                if (!document.getElementById('loadingSpinnerStyle')) {
                    const style = document.createElement('style');
                    style.id = 'loadingSpinnerStyle';
                    style.textContent = `
                        @keyframes spin {
                            0% { transform: rotate(0deg); }
                            100% { transform: rotate(360deg); }
                        }
                    `;
                    document.head.appendChild(style);
                }
                
                // 로딩 메시지
                const messageElement = document.createElement('div');
                messageElement.id = `${containerId}Message`;
                messageElement.textContent = message;
                
                // 요소 조합
                loadingContainer.appendChild(spinner);
                loadingContainer.appendChild(messageElement);
                loadingOverlay.appendChild(loadingContainer);
                document.body.appendChild(loadingOverlay);
            } 
            // 요소가 있는 경우 표시/숨김 처리
            else if (loadingOverlay) {
                // 로딩 메시지 업데이트
                if (show) {
                    const messageElement = loadingOverlay.querySelector(`#${containerId}Message`);
                    if (messageElement) {
                        messageElement.textContent = message;
                    }
                    loadingOverlay.style.display = 'flex';
                } else {
                    loadingOverlay.style.display = 'none';
                }
            }
            
            return true;
        } catch (error) {
            console.error('로딩 표시기 토글 중 오류 발생:', error);
            return false;
        }
    }
    
    // =============================
    // UI 컴포넌트 관련 함수
    // =============================
    
    /**
     * 탭 초기화 함수
     * 
     * @param {string} tabContainerId - 탭 컨테이너 ID
     * @param {Object} [options] - 옵션
     * @param {Function} [options.onTabChange] - 탭 변경 시 콜백
     * @param {string} [options.defaultTab] - 기본 선택 탭 ID
     * @param {string} [options.activeClass='active'] - 활성 탭에 적용할 CSS 클래스
     * @returns {Promise<Object>} 탭 컨트롤러 객체를 담은 Promise
     */
    async function initTabs(tabContainerId, options = {}) {
        try {
            const container = document.getElementById(tabContainerId);
            
            if (!container) {
                throw new Error(`'${tabContainerId}' 컨테이너를 찾을 수 없습니다.`);
            }
            
            const { 
                onTabChange,
                defaultTab,
                activeClass = 'active'
            } = options;
            
            const tabButtons = container.querySelectorAll('[data-tab]');
            const tabContents = container.querySelectorAll('[data-tab-content]');
            
            if (tabButtons.length === 0) {
                throw new Error(`'${tabContainerId}' 컨테이너에 탭 버튼이 없습니다.`);
            }
            
            // 탭 ID 및 컨텐츠 요소 매핑 객체
            const tabMap = {};
            
            // 모든 탭 콘텐츠 숨기기 및 매핑
            tabContents.forEach(content => {
                const tabId = content.getAttribute('data-tab-content');
                content.style.display = 'none';
                
                if (tabId) {
                    tabMap[tabId] = {
                        content,
                        button: null
                    };
                }
            });
            
            // 탭 버튼 매핑
            tabButtons.forEach(button => {
                const tabId = button.getAttribute('data-tab');
                
                if (tabId && tabMap[tabId]) {
                    tabMap[tabId].button = button;
                }
            });
            
            // 탭 선택 함수
            const selectTab = async (tabId) => {
                // 모든 탭 비활성화
                tabButtons.forEach(btn => {
                    btn.classList.remove(activeClass);
                });
                
                tabContents.forEach(content => {
                    content.style.display = 'none';
                });
                
                // 선택한 탭 활성화
                if (tabMap[tabId]) {
                    const { button, content } = tabMap[tabId];
                    
                    if (button) {
                        button.classList.add(activeClass);
                    }
                    
                    if (content) {
                        content.style.display = 'block';
                    }
                    
                    // 콜백 호출
                    if (onTabChange && typeof onTabChange === 'function') {
                        try {
                            await onTabChange(tabId, content);
                        } catch (callbackError) {
                            console.error('탭 변경 콜백 실행 중 오류:', callbackError);
                        }
                    }
                    
                    return true;
                }
                
                return false;
            };
            
            // 탭 클릭 이벤트 처리
            tabButtons.forEach(button => {
                button.addEventListener('click', async function() {
                    const tabId = this.getAttribute('data-tab');
                    await selectTab(tabId);
                });
            });
            
            // 기본 탭 선택
            const initialTab = defaultTab || tabButtons[0].getAttribute('data-tab');
            await selectTab(initialTab);
            
            // 탭 컨트롤러 객체 반환
            return {
                selectTab,
                getTabs: () => Object.keys(tabMap),
                getActiveTab: () => {
                    for (const [tabId, { button }] of Object.entries(tabMap)) {
                        if (button && button.classList.contains(activeClass)) {
                            return tabId;
                        }
                    }
                    return null;
                }
            };
        } catch (error) {
            console.error('탭 초기화 중 오류 발생:', error);
            throw error;
        }
    }
    
    /**
     * 동적 컨텐츠 로드 함수
     * 
     * @param {string} containerId - 컨테이너 ID
     * @param {string} url - 컨텐츠 URL
     * @param {Object} [options] - 옵션
     * @param {boolean} [options.showLoading=true] - 로딩 표시 여부
     * @param {Function} [options.onSuccess] - 성공 시 콜백
     * @param {Function} [options.onError] - 오류 시 콜백
     * @returns {Promise<boolean>} 성공 여부를 담은 Promise
     */
    async function loadContent(containerId, url, options = {}) {
        try {
            const container = document.getElementById(containerId);
            
            if (!container) {
                throw new Error(`'${containerId}' 컨테이너를 찾을 수 없습니다.`);
            }
            
            const { 
                showLoading = true,
                onSuccess,
                onError
            } = options;
            
            // 로딩 표시
            if (showLoading) {
                await toggleLoading(true, { 
                    message: '컨텐츠를 로드하는 중...',
                    containerId: `${containerId}Loading`,
                    fullScreen: false
                });
            }
            
            try {
                // fetch API로 컨텐츠 로드
                const response = await fetch(url);
                
                if (!response.ok) {
                    throw new Error(`HTTP 오류 ${response.status}: ${response.statusText}`);
                }
                
                const html = await response.text();
                
                // 컨테이너에 컨텐츠 설정
                container.innerHTML = html;
                
                // 로딩 숨김
                if (showLoading) {
                    await toggleLoading(false, { containerId: `${containerId}Loading` });
                }
                
                // 성공 콜백 호출
                if (onSuccess && typeof onSuccess === 'function') {
                    await onSuccess(html, container);
                }
                
                return true;
            } catch (fetchError) {
                // 로딩 숨김
                if (showLoading) {
                    await toggleLoading(false, { containerId: `${containerId}Loading` });
                }
                
                // 오류 콜백 호출
                if (onError && typeof onError === 'function') {
                    await onError(fetchError);
                } else {
                    console.error(`컨텐츠 로드 실패 (${url}):`, fetchError.message);
                    container.innerHTML = `<div class="error-message">컨텐츠를 로드하지 못했습니다: ${fetchError.message}</div>`;
                }
                
                return false;
            }
        } catch (error) {
            console.error('컨텐츠 로드 중 오류 발생:', error);
            return false;
        }
    }
    
	// 공개 API
	    return {
	        // 이벤트 처리 관련 함수
	        registerEventListeners,    // 이벤트 리스너 등록
	        bindEnterKeySearch,        // 엔터키 검색 이벤트 바인딩
	        removeEventListener,       // 이벤트 리스너 제거
	        
	        // 폼 관련 함수
	        clearForm,                 // 입력 폼 초기화
	        setFormValues,             // 입력 폼 값 설정
	        getFormValues,             // 입력 폼 값 가져오기
	        
	        // UI 표시/숨김 관련 함수
	        toggleElement,             // 요소 표시/숨김 토글
	        toggleLoading,             // 로딩 표시기 함수
	        
	        // UI 컴포넌트 관련 함수
	        initTabs,                  // 탭 초기화
	        loadContent                // 동적 컨텐츠 로드
	    };
	})();