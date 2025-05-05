/**
 * commonApi.js - API 응답 처리 유틸리티
 * 
 * 서버 API 응답을 일관되게 처리하기 위한 유틸리티 함수를 제공합니다.
 * 표준 API 응답 구조:
 * {
 *   success: true,                  // 성공 여부 (소문자)
 *   message: "Success",             // 메시지 (소문자)
 *   data: {                         // 데이터 객체 (소문자)
 *     FIELD_NAME: "값",             // 내부 필드는 대문자_언더스코어
 *     ANOTHER_FIELD: 123            // 내부 필드는 대문자_언더스코어
 *   },
 *   code: "CM200"                   // 코드 (소문자)
 * }
 * 
 * @version 1.0.0
 * @since 2025-04-04
 */

const ApiUtils = (function() {
    // =============================
    // API 호출 기본 함수
    // =============================
    
    /**
     * API 호출 함수 - GET 메소드
     * 
     * @param {string} url - API URL
     * @param {Object} params - 요청 파라미터 (선택적)
     * @param {Object} options - 옵션 (선택적)
     * @returns {Promise<Object>} API 응답 객체
     */
    async function get(url, params = null, options = {}) {
        try {
            // 파라미터가 있는 경우 URL에 쿼리스트링 추가
            if (params) {
                const queryString = new URLSearchParams(params).toString();
                url = `${url}${url.includes('?') ? '&' : '?'}${queryString}`;
            }
            
            // 기본 옵션과 사용자 옵션 병합
            const fetchOptions = {
                method: 'GET',
                headers: {
                    'Accept': 'application/json',
                    ...options.headers
                },
                ...options
            };
            
            // API 호출
            const response = await fetch(url, fetchOptions);
            
            // JSON 응답 파싱
            const result = await response.json();
            
            // 표준 API 응답 형식 검증 및 반환
            return validateApiResponse(result);
        } catch (error) {
            console.error('API GET 호출 오류:', error);
            // 표준 오류 응답 형식으로 변환하여 반환
            return {
                success: false,
                message: error.message || '서버 연결 오류가 발생했습니다.',
                data: null,
                code: 'ERR500'
            };
        }
    }
    
    /**
     * API 호출 함수 - POST 메소드
     * 
     * @param {string} url - API URL
     * @param {Object} data - 요청 데이터
     * @param {Object} options - 옵션 (선택적)
     * @returns {Promise<Object>} API 응답 객체
     */
    async function post(url, data, options = {}) {
        try {
            // Content-Type 설정 (기본은 JSON)
            const isFormData = data instanceof FormData;
            
            // 기본 옵션과 사용자 옵션 병합
            const fetchOptions = {
                method: 'POST',
                headers: {
                    'Accept': 'application/json',
                    ...(!isFormData && {'Content-Type': 'application/json'}),
                    ...options.headers
                },
                body: isFormData ? data : JSON.stringify(data),
                ...options
            };
            
            // API 호출
            const response = await fetch(url, fetchOptions);
            
            // JSON 응답 파싱
            const result = await response.json();
            
            // 표준 API 응답 형식 검증 및 반환
            return validateApiResponse(result);
        } catch (error) {
            console.error('API POST 호출 오류:', error);
            // 표준 오류 응답 형식으로 변환하여 반환
            return {
                success: false,
                message: error.message || '서버 연결 오류가 발생했습니다.',
                data: null,
                code: 'ERR500'
            };
        }
    }
    
    /**
     * API 호출 함수 - PUT 메소드
     * 
     * @param {string} url - API URL
     * @param {Object} data - 요청 데이터
     * @param {Object} options - 옵션 (선택적)
     * @returns {Promise<Object>} API 응답 객체
     */
    async function put(url, data, options = {}) {
        try {
            // Content-Type 설정 (기본은 JSON)
            const isFormData = data instanceof FormData;
            
            // 기본 옵션과 사용자 옵션 병합
            const fetchOptions = {
                method: 'PUT',
                headers: {
                    'Accept': 'application/json',
                    ...(!isFormData && {'Content-Type': 'application/json'}),
                    ...options.headers
                },
                body: isFormData ? data : JSON.stringify(data),
                ...options
            };
            
            // API 호출
            const response = await fetch(url, fetchOptions);
            
            // JSON 응답 파싱
            const result = await response.json();
            
            // 표준 API 응답 형식 검증 및 반환
            return validateApiResponse(result);
        } catch (error) {
            console.error('API PUT 호출 오류:', error);
            // 표준 오류 응답 형식으로 변환하여 반환
            return {
                success: false,
                message: error.message || '서버 연결 오류가 발생했습니다.',
                data: null,
                code: 'ERR500'
            };
        }
    }
    
    /**
     * API 호출 함수 - DELETE 메소드
     * 
     * @param {string} url - API URL
     * @param {Object} params - 요청 파라미터 (선택적)
     * @param {Object} options - 옵션 (선택적)
     * @returns {Promise<Object>} API 응답 객체
     */
    async function del(url, params = null, options = {}) {
        try {
            // 파라미터가 있는 경우 URL에 쿼리스트링 추가
            if (params) {
                const queryString = new URLSearchParams(params).toString();
                url = `${url}${url.includes('?') ? '&' : '?'}${queryString}`;
            }
            
            // 기본 옵션과 사용자 옵션 병합
            const fetchOptions = {
                method: 'DELETE',
                headers: {
                    'Accept': 'application/json',
                    ...options.headers
                },
                ...options
            };
            
            // API 호출
            const response = await fetch(url, fetchOptions);
            
            // JSON 응답 파싱
            const result = await response.json();
            
            // 표준 API 응답 형식 검증 및 반환
            return validateApiResponse(result);
        } catch (error) {
            console.error('API DELETE 호출 오류:', error);
            // 표준 오류 응답 형식으로 변환하여 반환
            return {
                success: false,
                message: error.message || '서버 연결 오류가 발생했습니다.',
                data: null,
                code: 'ERR500'
            };
        }
    }
    
    // =============================
    // 로딩 표시 관련 함수
    // =============================
    
    /**
     * GET 요청 (로딩 표시 포함)
     * 
     * @param {string} url - API URL
     * @param {Object} [params] - 쿼리 파라미터
     * @param {string} [loadingMessage='불러오는 중...'] - 로딩 메시지
     * @param {Object} [options] - 추가 옵션
     * @returns {Promise<Object>} API 응답 객체
     */
    async function getWithLoading(url, params, loadingMessage = '불러오는 중...', options) {
        return await withLoading(() => get(url, params, options), loadingMessage);
    }
    
    /**
     * POST 요청 (로딩 표시 포함)
     * 
     * @param {string} url - API URL
     * @param {Object} data - 요청 데이터
     * @param {string} [loadingMessage='저장 중...'] - 로딩 메시지
     * @param {Object} [options] - 추가 옵션
     * @returns {Promise<Object>} API 응답 객체
     */
    async function postWithLoading(url, data, loadingMessage = '저장 중...', options) {
        return await withLoading(() => post(url, data, options), loadingMessage);
    }
    
    /**
     * PUT 요청 (로딩 표시 포함)
     * 
     * @param {string} url - API URL
     * @param {Object} data - 요청 데이터
     * @param {string} [loadingMessage='수정 중...'] - 로딩 메시지
     * @param {Object} [options] - 추가 옵션
     * @returns {Promise<Object>} API 응답 객체
     */
    async function putWithLoading(url, data, loadingMessage = '수정 중...', options) {
        return await withLoading(() => put(url, data, options), loadingMessage);
    }
    
    /**
     * DELETE 요청 (로딩 표시 포함)
     * 
     * @param {string} url - API URL
     * @param {Object} [params] - 요청 파라미터
     * @param {string} [loadingMessage='삭제 중...'] - 로딩 메시지
     * @param {Object} [options] - 추가 옵션
     * @returns {Promise<Object>} API 응답 객체
     */
    async function delWithLoading(url, params, loadingMessage = '삭제 중...', options) {
        return await withLoading(() => del(url, params, options), loadingMessage);
    }
    
    /**
     * 로딩 표시와 함께 API 호출을 수행하는 함수
     * 
     * @param {Function} apiCall - API 호출 함수 (Promise 반환)
     * @param {string} [loadingMessage='처리 중...'] - 로딩 메시지
     * @returns {Promise<Object>} API 응답 객체
     */
    async function withLoading(apiCall, loadingMessage = '처리 중...') {
        try {
            if (window.UIUtil) {
                await UIUtil.toggleLoading(true, loadingMessage);
            } else if (window.AlertUtil && typeof AlertUtil.showLoading === 'function') {
                const loading = AlertUtil.showLoading(loadingMessage);
                try {
                    const result = await apiCall();
                    loading.close();
                    return result;
                } catch (error) {
                    loading.close();
                    throw error;
                }
            }

            return await apiCall();
        } finally {
            if (window.UIUtil) {
                await UIUtil.toggleLoading(false);
            }
        }
    }
    
    // =============================
    // 응답 처리 및 검증 함수
    // =============================
    
    /**
     * API 응답 검증 및 표준화 함수
     * 다양한 응답 형식을 표준 형식으로 변환합니다.
     * 
     * @param {Object} response - API 응답 객체
     * @returns {Object} 표준화된 API 응답 객체
     */
    function validateApiResponse(response) {
        // 이미 표준 형식인 경우 그대로 반환
        if (response !== null && typeof response === 'object' && 'success' in response) {
            return response;
        }
        
        // 없거나 유효하지 않은 응답인 경우 오류 응답 생성
        if (!response) {
            return {
                success: false,
                message: '응답 데이터가 없습니다.',
                data: null,
                code: 'ERR400'
            };
        }
        
        // 객체가 아닌 경우 응답을 data로 감싸기
        if (typeof response !== 'object') {
            return {
                success: true,
                message: 'Success',
                data: response,
                code: '200'
            };
        }
        
        // 특정 형식의 응답 변환 (예: { result: true, items: [...] })
        if ('result' in response && typeof response.result === 'boolean') {
            return {
                success: response.result,
                message: response.message || (response.result ? 'Success' : 'Error'),
                data: response.items || response.data || null,
                code: response.code || (response.result ? '200' : '500')
            };
        }
        
        // 기타 응답은 성공으로 간주하고 data로 감싸기
        return {
            success: true,
            message: 'Success',
            data: response,
            code: '200'
        };
    }
    
    /**
     * API 에러 처리 함수
     * 
     * @param {Object} error - 에러 객체
     * @param {string} [title='오류가 발생했습니다'] - 알림창 제목
     * @param {Function} [callback] - 에러 처리 후 콜백
     */
    async function handleApiError(error, title = '오류가 발생했습니다', callback) {
        console.error('API 에러:', error);

        // 응답 상세 정보 로깅
        if (error.responseJSON) {
            console.error('에러 응답:', error.responseJSON);
        } else if (error.responseText) {
            console.error('에러 텍스트:', error.responseText);
        }

        // AlertUtil 사용 가능 여부 확인
        if (window.AlertUtil) {
            // 오류 메시지 추출
            const errorMessage = error.message || '알 수 없는 오류가 발생했습니다.';
            
            // 알림창으로 오류 표시
            await AlertUtil.showError(title, errorMessage, callback);
        } else {
            // AlertUtil이 없는 경우 기본 alert 사용
            alert(`${title}\n${error.message || '알 수 없는 오류가 발생했습니다.'}`);
            
            // 콜백 호출 (있는 경우)
            if (typeof callback === 'function') {
                callback(error);
            }
        }
    }
    
    /**
     * 완전한 API 요청 처리 함수 (로딩, 결과 알림, 오류 처리 포함)
     * 
     * @param {Function} apiCall - API 호출 함수 (Promise 반환)
     * @param {Object} options - 옵션
     * @param {string} [options.loadingMessage='처리 중...'] - 로딩 메시지
     * @param {string} [options.successMessage] - 성공 메시지 (설정 시 알림창 표시)
     * @param {string} [options.errorMessage='오류가 발생했습니다'] - 오류 메시지
     * @param {Function} [options.successCallback] - 성공 시 콜백
     * @param {Function} [options.errorCallback] - 오류 시 콜백
     * @returns {Promise<Object>} 처리 결과 데이터
     */
    async function processRequest(apiCall, options = {}) {
        const {
            loadingMessage = '처리 중...',
            successMessage,
            errorMessage = '오류가 발생했습니다',
            successCallback,
            errorCallback
        } = options;

        // 로딩 객체 참조 저장
        let loadingRef = null;

        try {
            // 로딩 표시 시작
            if (window.AlertUtil && typeof AlertUtil.showLoading === 'function') {
                loadingRef = AlertUtil.showLoading(loadingMessage);
            } else if (window.UIUtil) {
                await UIUtil.toggleLoading(true, loadingMessage);
            }

            // API 호출 실행
            const response = await apiCall();

            // 로딩 표시 종료
            if (loadingRef) {
                loadingRef.close();
            } else if (window.UIUtil) {
                await UIUtil.toggleLoading(false);
            }

            // 비즈니스 로직 성공 여부 확인 (response.success 필드 사용)
            if (response.success) {
                // 성공 메시지가 지정된 경우 알림창 표시
                if (successMessage && window.AlertUtil) {
                    await AlertUtil.showSuccess('성공', successMessage, successCallback);
                } else if (successCallback) {
                    // 알림창 없이 콜백만 실행
                    successCallback(response);
                }
            } else {
                // API는 성공했지만 비즈니스 로직 실패
                const message = response.message || errorMessage;
                
                if (window.AlertUtil) {
                    await AlertUtil.showWarning("처리 실패", message, errorCallback);
                } else {
                    alert(`처리 실패: ${message}`);
                    if (errorCallback) errorCallback(response);
                }
            }

            return response;
        } catch (error) {
            // 로딩 표시 종료
            if (loadingRef) {
                loadingRef.close();
            } else if (window.UIUtil) {
                await UIUtil.toggleLoading(false);
            }

            // API 호출 자체가 실패한 경우
            await handleApiError(error, errorMessage, errorCallback);
            throw error; // 추가 처리를 위해 오류 다시 throw
        }
    }
    
    // 공개 API
    return {
        // 기본 HTTP 요청 함수
        get,
        post,
        put,
        del,
        
        // 로딩 표시 함수
        getWithLoading,
        postWithLoading,
        putWithLoading,
        delWithLoading,
        withLoading,
        
        // 응답 처리 함수
        validateApiResponse,
        handleApiError,
        processRequest
    };
})();

// 이전 ApiUtil과의 호환성을 위해 전역 변수로 노출
window.ApiUtil = ApiUtils;