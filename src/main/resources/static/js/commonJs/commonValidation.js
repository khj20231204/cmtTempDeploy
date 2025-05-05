/**
 * 리팩토링된 ValidationUtil - 유효성 검증 공통 라이브러리
 * 
 * ERP/MES 시스템을 위한 입력값 유효성 검증 공통 기능을 제공합니다.
 * 비동기 함수를 async/await 패턴으로 통일하고 일관된 오류 처리를 구현합니다.
 * 
 * @version 1.0.0
 * @since 2025-03-24
 */

const ValidationUtil = (function() {
    // =============================
    // 기본 유효성 검사 패턴
    // =============================
    
    // 자주 사용되는 정규식 패턴
    const PATTERNS = {
        // 기본 검증 패턴
        email: /^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*\.[a-zA-Z]{2,3}$/,
        phone: /^01([0|1|6|7|8|9])-?([0-9]{3,4})-?([0-9]{4})$/,
        koreanPhone: /^0([2|3|4|5|6|7]{1}[0-9]{1})-?([0-9]{3,4})-?([0-9]{4})$/,
        date: /^\d{4}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])$/,
        time: /^([01][0-9]|2[0-3]):([0-5][0-9])$/,
        datetime: /^\d{4}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])\s([01][0-9]|2[0-3]):([0-5][0-9])$/,
        number: /^-?\d+(\.\d+)?$/,
        integer: /^-?\d+$/,
        positiveInteger: /^[1-9]\d*$/,
        alpha: /^[a-zA-Z]+$/,
        alphaNumeric: /^[0-9a-zA-Z]+$/,
        korean: /^[가-힣]+$/,
        zipCode: /^\d{5}$/,
        busiNo: /^[0-9]{3}-[0-9]{2}-[0-9]{5}$/,
        idCard: /^[0-9]{6}-[0-9]{7}$/,
        url: /^(https?:\/\/)?(www\.)?([\w.]+)\.([a-z]{2,6}\.?)(\/[\w.]*)*\/?$/,
        password: /^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{8,16}$/
    };
    
    // =============================
    // 폼 유효성 검사 관련 함수
    // =============================
    
    /**
     * 폼 유효성 검사 함수
     * 
     * @param {Object} options - 검사 옵션
     * @param {string|HTMLFormElement} options.form - 폼 ID 또는 폼 요소
     * @param {Object} options.rules - 검사 규칙 객체
     * @param {Object} [options.messages] - 사용자 정의 오류 메시지
     * @param {boolean} [options.showAlert=true] - 오류 발생 시 알림창 표시 여부
     * @param {boolean} [options.focusOnError=true] - 첫 번째 오류 필드에 포커스 설정 여부
     * @param {Function} [options.onSuccess] - 유효성 검사 성공 시 콜백 함수
     * @param {Function} [options.onError] - 유효성 검사 실패 시 콜백 함수
     * 
     * @returns {Promise<Object>} 검사 결과 객체 { valid: boolean, errors: Array }
     */
    async function validateForm(options) {
        try {
            // 폼 요소 가져오기
            const form = typeof options.form === 'string' ? 
                document.getElementById(options.form) : 
                options.form;
            
            if (!form || !(form instanceof HTMLFormElement)) {
                throw new Error('유효한 폼 요소를 찾을 수 없습니다.');
            }
            
            // 규칙 유효성 확인
            if (!options.rules || typeof options.rules !== 'object') {
                throw new Error('유효성 검사 규칙이 필요합니다.');
            }
            
            const rules = options.rules;
            const messages = options.messages || {};
            const showAlert = options.showAlert !== false;
            const focusOnError = options.focusOnError !== false;
            
            const errors = [];
            let firstErrorField = null;
            
            // 각 필드별 유효성 검사
            for (const fieldName in rules) {
                const fieldRules = rules[fieldName];
                const field = form.elements[fieldName];
                
                if (!field) {
                    console.warn(`필드 '${fieldName}'을(를) 찾을 수 없습니다.`);
                    continue;
                }
                
                const fieldValue = field.value.trim();
                const fieldType = field.type ? field.type.toLowerCase() : '';
                const fieldMessages = messages[fieldName] || {};
                
                // 필수 입력 검사
                if (fieldRules.required && fieldValue === '') {
                    const errorMsg = fieldMessages.required || `${fieldName} 필드는 필수입니다.`;
                    errors.push(errorMsg);
                    
                    if (!firstErrorField) {
                        firstErrorField = field;
                    }
                    
                    continue; // 필수 필드가 비어있으면 다음 규칙은 검사하지 않음
                }
                
                // 값이 비어있고 필수가 아니면 다른 검사는 스킵
                if (fieldValue === '' && !fieldRules.required) {
                    continue;
                }
                
                // 최소 길이 검사
                if (fieldRules.minLength && fieldValue.length < fieldRules.minLength) {
                    const errorMsg = fieldMessages.minLength || 
                        `${fieldName} 필드는 최소 ${fieldRules.minLength}자 이상이어야 합니다.`;
                    errors.push(errorMsg);
                    
                    if (!firstErrorField) {
                        firstErrorField = field;
                    }
                }
                
                // 최대 길이 검사
                if (fieldRules.maxLength && fieldValue.length > fieldRules.maxLength) {
                    const errorMsg = fieldMessages.maxLength || 
                        `${fieldName} 필드는 최대 ${fieldRules.maxLength}자 이하여야 합니다.`;
                    errors.push(errorMsg);
                    
                    if (!firstErrorField) {
                        firstErrorField = field;
                    }
                }
                
                // 패턴 검사
                if (fieldRules.pattern) {
                    let pattern = fieldRules.pattern;
                    
                    // 문자열 패턴명인 경우 미리 정의된 패턴 사용
                    if (typeof pattern === 'string' && PATTERNS[pattern]) {
                        pattern = PATTERNS[pattern];
                    }
                    
                    if (pattern instanceof RegExp && !pattern.test(fieldValue)) {
                        const errorMsg = fieldMessages.pattern || 
                            `${fieldName} 필드의 형식이 올바르지 않습니다.`;
                        errors.push(errorMsg);
                        
                        if (!firstErrorField) {
                            firstErrorField = field;
                        }
                    }
                }
                
                // 숫자 검사
                if (fieldType === 'number' || fieldRules.isNumeric) {
                    const numValue = Number(fieldValue);
                    
                    if (isNaN(numValue)) {
                        const errorMsg = fieldMessages.number || 
                            `${fieldName} 필드는 유효한 숫자여야 합니다.`;
                        errors.push(errorMsg);
                        
                        if (!firstErrorField) {
                            firstErrorField = field;
                        }
                    } else {
                        // 최소값 검사
                        if (fieldRules.min !== undefined && numValue < fieldRules.min) {
                            const errorMsg = fieldMessages.min || 
                                `${fieldName} 필드는 ${fieldRules.min} 이상이어야 합니다.`;
                            errors.push(errorMsg);
                            
                            if (!firstErrorField) {
                                firstErrorField = field;
                            }
                        }
                        
                        // 최대값 검사
                        if (fieldRules.max !== undefined && numValue > fieldRules.max) {
                            const errorMsg = fieldMessages.max || 
                                `${fieldName} 필드는 ${fieldRules.max} 이하여야 합니다.`;
                            errors.push(errorMsg);
                            
                            if (!firstErrorField) {
                                firstErrorField = field;
                            }
                        }
                    }
                }
                
                // 일치 검사 (비밀번호 확인 등)
                if (fieldRules.equalTo) {
                    const targetField = form.elements[fieldRules.equalTo];
                    if (targetField && fieldValue !== targetField.value) {
                        const errorMsg = fieldMessages.equalTo || 
                            `${fieldName} 필드는 ${fieldRules.equalTo} 필드와 일치해야 합니다.`;
                        errors.push(errorMsg);
                        
                        if (!firstErrorField) {
                            firstErrorField = field;
                        }
                    }
                }
                
                // 커스텀 검사 함수
                if (fieldRules.validator && typeof fieldRules.validator === 'function') {
                    try {
                        const result = await fieldRules.validator(fieldValue, field, form);
                        
                        if (result !== true) {
                            const errorMsg = typeof result === 'string' ? result : 
                                (fieldMessages.validator || `${fieldName} 필드가 유효하지 않습니다.`);
                            errors.push(errorMsg);
                            
                            if (!firstErrorField) {
                                firstErrorField = field;
                            }
                        }
                    } catch (validatorError) {
                        console.error(`'${fieldName}' 필드 커스텀 검증 중 오류:`, validatorError);
                        errors.push(`${fieldName} 필드 검증 중 오류: ${validatorError.message}`);
                        
                        if (!firstErrorField) {
                            firstErrorField = field;
                        }
                    }
                }
            }
            
            // 결과 생성
            const result = {
                valid: errors.length === 0,
                errors: errors
            };
            
            // 에러 처리
            if (!result.valid) {
                // 첫 번째 오류 필드에 포커스
                if (focusOnError && firstErrorField) {
                    firstErrorField.focus();
                }
                
                // 오류 메시지 알림창 표시
                if (showAlert) {
                    const errorMessage = errors.join('\n');
                    
                    if (window.AlertUtil) {
                        await AlertUtil.showError('입력 오류', errorMessage);
                    } else {
                        alert(errorMessage);
                    }
                }
                
                // 오류 콜백 호출
                if (options.onError && typeof options.onError === 'function') {
                    await options.onError(result);
                }
            } else if (options.onSuccess && typeof options.onSuccess === 'function') {
                // 성공 콜백 호출
                await options.onSuccess(result);
            }
            
            return result;
        } catch (error) {
            console.error('폼 유효성 검사 중 오류 발생:', error);
            
            // 에러 처리
            if (options.showAlert !== false) {
                if (window.AlertUtil) {
                    await AlertUtil.showError('검증 오류', error.message);
                } else {
                    alert(`검증 중 오류가 발생했습니다: ${error.message}`);
                }
            }
            
            return {
                valid: false,
                errors: [error.message]
            };
        }
    }
    
    /**
     * 입력 필드 실시간 유효성 검사 설정 함수
     * 
     * @param {Object} options - 검사 옵션
     * @param {string|HTMLFormElement} options.form - 폼 ID 또는 폼 요소
     * @param {Object} options.rules - 검사 규칙 객체
     * @param {Object} [options.messages] - 사용자 정의 오류 메시지
     * @param {string} [options.errorClass='error'] - 오류 표시 CSS 클래스
     * @param {boolean} [options.appendMessage=true] - 오류 메시지 추가 표시 여부
     * @param {string} [options.messageClass='validation-message'] - 오류 메시지 CSS 클래스
     * 
     * @returns {Promise<boolean>} 설정 성공 여부
     */
    async function initLiveValidation(options) {
        try {
            // 폼 요소 가져오기
            const form = typeof options.form === 'string' ? 
                document.getElementById(options.form) : 
                options.form;
            
            if (!form || !(form instanceof HTMLFormElement)) {
                throw new Error('유효한 폼 요소를 찾을 수 없습니다.');
            }
            
            // 규칙 유효성 확인
            if (!options.rules || typeof options.rules !== 'object') {
                throw new Error('유효성 검사 규칙이 필요합니다.');
            }
            
            const rules = options.rules;
            const messages = options.messages || {};
            const errorClass = options.errorClass || 'error';
            const appendMessage = options.appendMessage !== false;
            const messageClass = options.messageClass || 'validation-message';
            
            // 각 필드에 검증 이벤트 설정
            for (const fieldName in rules) {
                const fieldRules = rules[fieldName];
                const field = form.elements[fieldName];
                
                if (!field) {
                    console.warn(`필드 '${fieldName}'을(를) 찾을 수 없습니다.`);
                    continue;
                }
                
                // 오류 메시지 요소 생성
                if (appendMessage) {
                    let messageElement = field.nextElementSibling;
                    
                    if (!messageElement || !messageElement.classList.contains(messageClass)) {
                        messageElement = document.createElement('div');
                        messageElement.className = messageClass;
                        messageElement.style.display = 'none';
                        messageElement.style.color = '#dc3545';
                        messageElement.style.fontSize = '0.875em';
                        messageElement.style.marginTop = '0.25rem';
                        
                        // 입력 필드 뒤에 추가
                        field.parentNode.insertBefore(messageElement, field.nextSibling);
                    }
                }
                
                // 검증 함수
                const validateField = async () => {
                    const fieldValue = field.value.trim();
                    const fieldType = field.type ? field.type.toLowerCase() : '';
                    const fieldMessages = messages[fieldName] || {};
                    
                    // 오류 메시지 리셋
                    field.classList.remove(errorClass);
                    
                    if (appendMessage) {
                        const messageElement = field.nextElementSibling;
                        if (messageElement && messageElement.classList.contains(messageClass)) {
                            messageElement.style.display = 'none';
                            messageElement.textContent = '';
                        }
                    }
                    
                    // 필수 입력 검사
                    if (fieldRules.required && fieldValue === '') {
                        const errorMsg = fieldMessages.required || `이 필드는 필수입니다.`;
                        await showFieldError(field, errorMsg);
                        return false;
                    }
                    
                    // 값이 비어있고 필수가 아니면 다른 검사는 스킵
                    if (fieldValue === '' && !fieldRules.required) {
                        return true;
                    }
                    
                    // 최소 길이 검사
                    if (fieldRules.minLength && fieldValue.length < fieldRules.minLength) {
                        const errorMsg = fieldMessages.minLength || 
                            `최소 ${fieldRules.minLength}자 이상이어야 합니다.`;
                        await showFieldError(field, errorMsg);
                        return false;
                    }
                    
                    // 최대 길이 검사
                    if (fieldRules.maxLength && fieldValue.length > fieldRules.maxLength) {
                        const errorMsg = fieldMessages.maxLength || 
                            `최대 ${fieldRules.maxLength}자 이하여야 합니다.`;
                        await showFieldError(field, errorMsg);
                        return false;
                    }
                    
                    // 패턴 검사
                    if (fieldRules.pattern) {
                        let pattern = fieldRules.pattern;
                        
                        // 문자열 패턴명인 경우 미리 정의된 패턴 사용
                        if (typeof pattern === 'string' && PATTERNS[pattern]) {
                            pattern = PATTERNS[pattern];
                        }
                        
                        if (pattern instanceof RegExp && !pattern.test(fieldValue)) {
                            const errorMsg = fieldMessages.pattern || `형식이 올바르지 않습니다.`;
                            await showFieldError(field, errorMsg);
                            return false;
                        }
                    }
                    
                    // 숫자 검사
                    if (fieldType === 'number' || fieldRules.isNumeric) {
                        const numValue = Number(fieldValue);
                        
                        if (isNaN(numValue)) {
                            const errorMsg = fieldMessages.number || `유효한 숫자여야 합니다.`;
                            await showFieldError(field, errorMsg);
                            return false;
                        } else {
                            // 최소값 검사
                            if (fieldRules.min !== undefined && numValue < fieldRules.min) {
                                const errorMsg = fieldMessages.min || 
                                    `${fieldRules.min} 이상이어야 합니다.`;
                                await showFieldError(field, errorMsg);
                                return false;
                            }
                            
                            // 최대값 검사
                            if (fieldRules.max !== undefined && numValue > fieldRules.max) {
                                const errorMsg = fieldMessages.max || 
                                    `${fieldRules.max} 이하여야 합니다.`;
                                await showFieldError(field, errorMsg);
                                return false;
                            }
                        }
                    }
                    
                    // 일치 검사 (비밀번호 확인 등)
                    if (fieldRules.equalTo) {
                        const targetField = form.elements[fieldRules.equalTo];
                        if (targetField && fieldValue !== targetField.value) {
                            const errorMsg = fieldMessages.equalTo || 
                                `입력값이 일치하지 않습니다.`;
                            await showFieldError(field, errorMsg);
                            return false;
                        }
                    }
                    
                    // 커스텀 검사 함수
                    if (fieldRules.validator && typeof fieldRules.validator === 'function') {
                        try {
                            const result = await fieldRules.validator(fieldValue, field, form);
                            
                            if (result !== true) {
                                const errorMsg = typeof result === 'string' ? result : 
                                    (fieldMessages.validator || `유효하지 않은 입력입니다.`);
                                await showFieldError(field, errorMsg);
                                return false;
                            }
                        } catch (validatorError) {
                            console.error(`'${fieldName}' 필드 커스텀 검증 중 오류:`, validatorError);
                            await showFieldError(field, `검증 중 오류: ${validatorError.message}`);
                            return false;
                        }
                    }
                    
                    return true;
                };
                
                // 오류 표시 함수
                const showFieldError = async (field, message) => {
                    field.classList.add(errorClass);
                    
                    if (appendMessage) {
                        const messageElement = field.nextElementSibling;
                        if (messageElement && messageElement.classList.contains(messageClass)) {
                            messageElement.textContent = message;
                            messageElement.style.display = 'block';
                        }
                    }
                };
                
                // 이벤트 리스너 설정
                field.addEventListener('blur', async function() {
                    await validateField();
                });
                
                field.addEventListener('input', async function() {
                    await validateField();
                });
                
                // 의존성 있는 필드 처리 (equalTo 등)
                if (fieldRules.equalTo) {
                    const targetField = form.elements[fieldRules.equalTo];
                    if (targetField) {
                        targetField.addEventListener('input', async function() {
                            await validateField();
                        });
                    }
                }
            }
            
            // 폼 제출 이벤트에 유효성 검사 연결
            form.addEventListener('submit', async function(e) {
                e.preventDefault(); // 일단 제출 방지
                
                let isValid = true;
                
                // 모든 필드 검증
                for (const fieldName in rules) {
                    const field = form.elements[fieldName];
                    if (field) {
                        // 각 필드의 검증 이벤트 트리거
                        const event = new Event('blur');
                        field.dispatchEvent(event);
                        
                        // 오류 클래스가 있으면 폼이 유효하지 않음
                        if (field.classList.contains(errorClass)) {
                            isValid = false;
                            
                            // 첫 번째 오류 필드로 스크롤 및 포커스
                            if (isValid === false) {
                                field.focus();
                                field.scrollIntoView({ behavior: 'smooth', block: 'center' });
                                isValid = false;
                                break;
                            }
                        }
                    }
                }
                
                // 유효한 경우 폼 제출
                if (isValid) {
                    form.submit();
                }
            });
            
            return true;
        } catch (error) {
            console.error('실시간 유효성 검사 설정 중 오류 발생:', error);
            return false;
        }
    }
    
    // =============================
    // 개별 유효성 검사 함수
    // =============================
    
    /**
     * 값이 비어있는지 확인하는 함수
     * 
     * @param {*} value - 검사할 값
     * @returns {boolean} 빈 값 여부
     */
    function isEmpty(value) {
        if (value === null || value === undefined) {
            return true;
        }
        
        if (typeof value === 'string') {
            return value.trim() === '';
        }
        
        if (Array.isArray(value)) {
            return value.length === 0;
        }
        
        if (typeof value === 'object') {
            return Object.keys(value).length === 0;
        }
        
        return false;
    }
    
    /**
     * 데이터 타입 유효성 검사 함수
     * 
     * @param {*} value - 검사할 값
     * @param {string} type - 예상 타입 ('string', 'number', 'boolean', 'array', 'object', 'date' 등)
     * @returns {boolean} 타입 일치 여부
     */
    function isType(value, type) {
        if (type === 'array') {
            return Array.isArray(value);
        }
        
        if (type === 'date') {
            return value instanceof Date && !isNaN(value);
        }
        
        if (type === 'null') {
            return value === null;
        }
        
        if (type === 'undefined') {
            return value === undefined;
        }
        
        if (type === 'object') {
            return typeof value === 'object' && value !== null && !Array.isArray(value);
        }
        
        return typeof value === type;
    }
    
    /**
     * 최소/최대 값 범위 검사 함수
     * 
     * @param {number} value - 검사할 값
     * @param {number} min - 최소값
     * @param {number} max - 최대값
     * @param {boolean} [inclusive=true] - 경계값 포함 여부
     * @returns {boolean} 범위 내 여부
     */
    function isInRange(value, min, max, inclusive = true) {
        const numValue = Number(value);
        
        if (isNaN(numValue)) {
            return false;
        }
        
        return inclusive ? 
            (numValue >= min && numValue <= max) : 
            (numValue > min && numValue < max);
    }
    
    /**
     * 패턴 검사 함수
     * 
     * @param {string} value - 검사할 값
     * @param {string|RegExp} pattern - 검사할 패턴 (문자열 또는 정규식)
     * @returns {boolean} 패턴 일치 여부
     */
    function matches(value, pattern) {
        if (typeof value !== 'string') {
            value = String(value);
        }
        
        // 문자열 패턴명인 경우 미리 정의된 패턴 사용
        if (typeof pattern === 'string' && PATTERNS[pattern]) {
            pattern = PATTERNS[pattern];
        }
        
        // 정규식이 아닌 경우 변환
        if (!(pattern instanceof RegExp)) {
            pattern = new RegExp(pattern);
        }
        
        return pattern.test(value);
    }
    
    /**
     * 두 값이 일치하는지 확인하는 함수
     * 
     * @param {*} value1 - 첫 번째 값
     * @param {*} value2 - 두 번째 값
     * @param {boolean} [strict=true] - 엄격한 비교 여부 (타입까지 일치)
     * @returns {boolean} 값 일치 여부
     */
    function isEqual(value1, value2, strict = true) {
        return strict ? value1 === value2 : value1 == value2;
    }
	// =============================
	// 유틸리티 함수
	// =============================

	/**
	 * 패턴 등록/추가 함수
	 * 
	 * @param {string} name - 패턴 이름
	 * @param {RegExp} pattern - 정규식 패턴
	 * @returns {boolean} 등록 성공 여부
	 */
	function registerPattern(name, pattern) {
	    if (!name || typeof name !== 'string') {
	        console.error('유효한 패턴 이름이 필요합니다.');
	        return false;
	    }
	    
	    if (!(pattern instanceof RegExp)) {
	        console.error('유효한 정규식 패턴이 필요합니다.');
	        return false;
	    }
	    
	    PATTERNS[name] = pattern;
	    return true;
	}
    
 
	    
	    // 공개 API
	    return {
	        // 폼 유효성 검사
	        validateForm,                // 폼 유효성 검사
	        initLiveValidation,          // 실시간 유효성 검사 설정
	        
	        // 개별 유효성 검사
	        isEmpty,                     // 빈 값 확인
	        isType,                      // 데이터 타입 확인
	        isInRange,                   // 범위 검사
	        matches,                     // 패턴 검사
	        isEqual,                     // 값 일치 검사
	        
       
	        // 유틸리티
	        registerPattern,             // 패턴 등록

	        // 패턴 정의 (외부에서 접근 가능하도록)
	        patterns: PATTERNS
	    };
	})();