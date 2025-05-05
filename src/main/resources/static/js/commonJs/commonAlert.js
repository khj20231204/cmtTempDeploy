/**
 * commonAlert.js - 향상된 알림창 공통 라이브러리 (SweetAlert2 기반)
 * 
 * 통합 알림창 기능을 제공
 * 다양한 알림 유형(정보, 성공, 경고, 오류 등)을 일관된 방식으로 표시합니다.
 * SweetAlert2 라이브러리만 사용하며, 기본 브라우저 알림은 사용하지 않습니다.
 * 
 * @version 1.0.0
 * @since 2025-03-24
 */

const AlertUtil = (function() {
    // =============================
    // 기본 알림창 관련 함수
    // =============================

    /**
     * 기본 알림창 표시 함수
     * 
     * @param {Object} options - 알림창 옵션
     * @param {string} options.title - 알림창 제목
     * @param {string} [options.text] - 알림창 본문 (선택 사항)
     * @param {string} [options.icon='info'] - 알림창 아이콘 ('success', 'error', 'warning', 'info' 중 하나)
     * @param {string} [options.confirmButtonText='확인'] - 확인 버튼 텍스트
     * @param {boolean} [options.allowOutsideClick=true] - 외부 클릭 허용 여부
     * @param {Function} [options.callback] - 알림창 닫은 후 실행할 콜백 함수
     * 
     * @returns {Promise<Object>} 알림창 결과를 담은 Promise
     * @example
     * // 기본 알림창
     * await AlertUtil.showAlert({
     *   title: '알림',
     *   text: '처리가 완료되었습니다.',
     *   icon: 'info',
     *   callback: () => console.log('알림창 닫힘')
     * });
     */
    async function showAlert(options) {
        const {
            title,
            text,
            icon = 'info',
            confirmButtonText = '확인',
            allowOutsideClick = true,
            callback
        } = options;

        try {
            const result = await Swal.fire({
                title: title,
                text: text || '',
                icon: icon,
                confirmButtonText: confirmButtonText,
                allowOutsideClick: allowOutsideClick
            });

            if (callback && typeof callback === 'function') {
                callback(result);
            }
            
            return result;
        } catch (error) {
            console.error('알림창 표시 중 오류:', error);
            throw error;
        }
    }

    // =============================
    // 유형별 알림창 함수
    // =============================

    /**
     * 성공 알림창 표시
     * 
     * @param {string} title - 알림창 제목
     * @param {string} [text] - 알림창 본문 (선택 사항)
     * @param {Function} [callback] - 알림창 닫은 후 실행할 콜백 함수
     * 
     * @returns {Promise<Object>} 알림창 결과를 담은 Promise
     * @example
     * // 성공 알림창
     * await AlertUtil.showSuccess('저장 완료', '데이터가 성공적으로 저장되었습니다.');
     * 
     * // 콜백 함수 사용
     * await AlertUtil.showSuccess('처리 완료', null, () => {
     *   window.location.href = '/dashboard';
     * });
     */
    async function showSuccess(title, text, callback) {
        return showAlert({
            title,
            text,
            icon: 'success',
            callback
        });
    }

    /**
     * 오류 알림창 표시
     * 
     * @param {string} title - 알림창 제목
     * @param {string} [text] - 알림창 본문 (선택 사항)
     * @param {Function} [callback] - 알림창 닫은 후 실행할 콜백 함수
     * 
     * @returns {Promise<Object>} 알림창 결과를 담은 Promise
     * @example
     * // 오류 알림창
     * await AlertUtil.showError('처리 실패', '서버 연결에 문제가 발생했습니다.');
     * 
     * // try-catch 블록에서 사용
     * try {
     *   await saveData();
     * } catch (error) {
     *   await AlertUtil.showError('저장 실패', error.message);
     * }
     */
    async function showError(title, text, callback) {
        return showAlert({
            title,
            text,
            icon: 'error',
            callback
        });
    }

    /**
     * 경고 알림창 표시
     * 
     * @param {string} title - 알림창 제목
     * @param {string} [text] - 알림창 본문 (선택 사항)
     * @param {Function} [callback] - 알림창 닫은 후 실행할 콜백 함수
     * 
     * @returns {Promise<Object>} 알림창 결과를 담은 Promise
     * @example
     * // 경고 알림창
     * await AlertUtil.showWarning('입력 오류', '필수 항목이 입력되지 않았습니다.');
     */
    async function showWarning(title, text, callback) {
        return showAlert({
            title,
            text,
            icon: 'warning',
            callback
        });
    }

    /**
     * 정보 알림창 표시
     * 
     * @param {string} title - 알림창 제목
     * @param {string} [text] - 알림창 본문 (선택 사항)
     * @param {Function} [callback] - 알림창 닫은 후 실행할 콜백 함수
     * 
     * @returns {Promise<Object>} 알림창 결과를 담은 Promise
     * @example
     * // 정보 알림창
     * await AlertUtil.showInfo('알림', '새로운 업데이트가 있습니다.');
     */
    async function showInfo(title, text, callback) {
        return showAlert({
            title,
            text,
            icon: 'info',
            callback
        });
    }

    // =============================
    // 대화상자 및 특수 알림창 함수
    // =============================

    /**
     * 확인 대화상자 표시
     * 
     * @param {Object} options - 대화상자 옵션
     * @param {string} options.title - 대화상자 제목
     * @param {string} [options.text] - 대화상자 본문 (선택 사항)
     * @param {string} [options.icon='question'] - 대화상자 아이콘
     * @param {string} [options.confirmButtonText='확인'] - 확인 버튼 텍스트
     * @param {string} [options.cancelButtonText='취소'] - 취소 버튼 텍스트
     * @param {Object} [options.customClass] - 커스텀 CSS 클래스
     * @param {Function} [options.onConfirm] - 확인 버튼 클릭 시 콜백 함수
     * @param {Function} [options.onCancel] - 취소 버튼 클릭 시 콜백 함수
     * 
     * @returns {Promise<boolean>} 사용자가 확인했는지 여부를 담은 Promise
     * @example
     * // 기본 확인 대화상자
     * const confirmed = await AlertUtil.showConfirm({
     *   title: '삭제 확인',
     *   text: '이 항목을 삭제하시겠습니까?'
     * });
     * 
     * if (confirmed) {
     *   await deleteItem(itemId);
     * }
     * 
     * // 콜백 함수 사용
     * await AlertUtil.showConfirm({
     *   title: '로그아웃',
     *   text: '로그아웃 하시겠습니까?',
     *   onConfirm: () => logoutUser(),
     *   onCancel: () => console.log('로그아웃 취소')
     * });
     */
    async function showConfirm(options) {
        const {
            title,
            text,
            icon = 'question',
            confirmButtonText = '확인',
            cancelButtonText = '취소',
            customClass = {},
            onConfirm,
            onCancel
        } = options;

        try {
            const result = await Swal.fire({
                title: title,
                text: text || '',
                icon: icon,
                showCancelButton: true,
                confirmButtonText: confirmButtonText,
                cancelButtonText: cancelButtonText,
                customClass: customClass
            });

            const confirmed = result.isConfirmed;

            if (confirmed && onConfirm && typeof onConfirm === 'function') {
                await onConfirm();
            } else if (!confirmed && onCancel && typeof onCancel === 'function') {
                await onCancel();
            }

            return confirmed;
        } catch (error) {
            console.error('확인 대화상자 표시 중 오류:', error);
            throw error;
        }
    }

    /**
     * 입력 대화상자 표시
     * 
     * @param {Object} options - 입력 대화상자 옵션
     * @param {string} options.title - 대화상자 제목
     * @param {string} [options.text] - 대화상자 본문 (선택 사항)
     * @param {string} [options.inputPlaceholder=''] - 입력 필드 플레이스홀더
     * @param {string} [options.inputValue=''] - 입력 필드 초기값
     * @param {string} [options.inputType='text'] - 입력 필드 타입 ('text', 'password' 등)
     * @param {Function} [options.validator] - 입력값 유효성 검사 함수 (문자열 반환 시 오류 메시지로 사용)
     * 
     * @returns {Promise<string|null>} 입력된 값 또는 취소 시 null을 담은 Promise
     * @example
     * // 기본 입력 대화상자
     * const name = await AlertUtil.showPrompt({
     *   title: '이름 입력',
     *   inputPlaceholder: '이름을 입력하세요.'
     * });
     * 
     * // 유효성 검사가 있는 입력 대화상자
     * const email = await AlertUtil.showPrompt({
     *   title: '이메일 입력',
     *   inputType: 'email',
     *   validator: (value) => {
     *     if (!value.includes('@')) return '유효한 이메일을 입력하세요.';
     *     return true;
     *   }
     * });
     */
    async function showPrompt(options) {
        const {
            title,
            text,
            inputPlaceholder = '',
            inputValue = '',
            inputType = 'text',
            validator
        } = options;

        try {
            const result = await Swal.fire({
                title: title,
                text: text || '',
                input: inputType,
                inputPlaceholder: inputPlaceholder,
                inputValue: inputValue,
                showCancelButton: true,
                inputValidator: validator,
                confirmButtonText: '확인',
                cancelButtonText: '취소'
            });

            return result.isConfirmed ? result.value : null;
        } catch (error) {
            console.error('입력 대화상자 표시 중 오류:', error);
            throw error;
        }
    }

    /**
     * 토스트 메시지 표시 (간단한 알림)
     * 
     * @param {Object} options - 토스트 옵션
     * @param {string} options.title - 토스트 제목
     * @param {string} [options.position='top-end'] - 토스트 위치
     * @param {string} [options.icon='info'] - 토스트 아이콘
     * @param {number} [options.timer=3000] - 자동 닫힘 시간 (ms)
     * 
     * @returns {Promise<void>} 토스트 표시 완료 Promise
     * @example
     * // 성공 토스트
     * await AlertUtil.showToast({
     *   title: '저장 완료',
     *   icon: 'success',
     *   timer: 2000
     * });
     */
    async function showToast(options) {
        const {
            title,
            position = 'top-end',
            icon = 'info',
            timer = 3000
        } = options;

        try {
            await Swal.fire({
                title: title,
                position: position,
                icon: icon,
                showConfirmButton: false,
                timer: timer,
                toast: true
            });
        } catch (error) {
            console.error('토스트 메시지 표시 중 오류:', error);
            throw error;
        }
    }

    /**
     * 여러 버튼이 있는 선택 대화상자 표시
     * 
     * @param {Object} options - 선택 대화상자 옵션
     * @param {string} options.title - 대화상자 제목
     * @param {string} [options.text] - 대화상자 본문 (선택 사항)
     * @param {string} [options.icon='question'] - 대화상자 아이콘
     * @param {Array} options.buttons - 버튼 정의 배열
     * 
     * @returns {Promise<string|null>} 선택된 버튼 값 또는 취소 시 null을 담은 Promise
     * @example
     * // 여러 버튼이 있는 선택 대화상자
     * const action = await AlertUtil.showChoice({
     *   title: '문서 처리',
     *   text: '문서를 어떻게 처리하시겠습니까?',
     *   buttons: [
     *     { text: '승인', value: 'approve', color: '#28a745' },
     *     { text: '거부', value: 'reject', color: '#dc3545' },
     *     { text: '보류', value: 'hold', color: '#ffc107' }
     *   ]
     * });
     */
    async function showChoice(options) {
        const {
            title,
            text,
            icon = 'question',
            buttons = []
        } = options;

        if (!buttons || buttons.length === 0) {
            console.error('버튼 정의가 필요합니다.');
            return null;
        }

        try {
            // SweetAlert2 버튼 설정
            const buttonOptions = {};
            
            // 취소 버튼 추가
            buttonOptions.showCancelButton = true;
            buttonOptions.cancelButtonText = '취소';
            
            // 첫 번째 버튼은 confirmButton으로 설정
            buttonOptions.confirmButtonText = buttons[0].text;
            buttonOptions.confirmButtonColor = buttons[0].color || undefined;
            
            // 나머지 버튼은 footer에 추가
            if (buttons.length > 1) {
                let footerHtml = '';
                for (let i = 1; i < buttons.length; i++) {
                    const button = buttons[i];
                    const style = button.color ? `background-color: ${button.color}; border-color: ${button.color};` : '';
                    footerHtml += `<button class="swal2-confirm swal2-styled" data-value="${button.value}" style="${style}">${button.text}</button>`;
                }
                buttonOptions.footer = footerHtml;
            }
            
            const swalResult = await Swal.fire({
                title: title,
                text: text || '',
                icon: icon,
                ...buttonOptions,
                preConfirm: () => {
                    return buttons[0].value;
                },
                didOpen: (popup) => {
                    // footer 버튼에 이벤트 리스너 등록
                    if (buttons.length > 1) {
                        const footerButtons = popup.querySelectorAll('.swal2-footer button');
                        footerButtons.forEach(button => {
                            button.addEventListener('click', () => {
                                Swal.close({ value: button.dataset.value });
                            });
                        });
                    }
                }
            });
            
            return swalResult.isConfirmed ? swalResult.value : 
                   (swalResult.value ? swalResult.value : null);
        } catch (error) {
            console.error('선택 대화상자 표시 중 오류:', error);
            return null;
        }
    }

    /**
     * 로딩 알림창 표시
     * 
     * @param {string} [title='처리 중...'] - 로딩 알림창 제목
     * @param {string} [text=''] - 로딩 알림창 본문
     * @returns {Object} 로딩 알림창 닫기 함수를 담은 객체
     * @example
     * // 로딩 알림창 표시 및 닫기
     * const loading = AlertUtil.showLoading('데이터 로드 중');
     * try {
     *   const data = await fetchData();
     *   loading.close();
     *   return data;
     * } catch (error) {
     *   loading.close();
     *   throw error;
     * }
     */
    function showLoading(title = '처리 중...', text = '') {
        Swal.fire({
            title: title,
            text: text,
            allowOutsideClick: false,
            allowEscapeKey: false,
            didOpen: () => {
                Swal.showLoading();
            }
        });

        return {
            close: () => {
                Swal.close();
            },
            update: (newTitle, newText) => {
                Swal.update({
                    title: newTitle || title,
                    text: newText || text
                });
            }
        };
    }

    // =============================
    // 표준화된 작업 알림 함수 (추가 기능)
    // =============================
    
    /**
     * 저장 성공 알림
     * 
     * @param {string} [title='저장 완료'] - 알림창 제목
     * @param {string} [message='데이터가 성공적으로 저장되었습니다.'] - 알림 메시지
     * @param {Function} [callback] - 알림창 닫은 후 실행할 콜백 함수
     * @returns {Promise<Object>} 알림창 결과
     */
    async function notifySaveSuccess(title = '저장 완료', message = '데이터가 성공적으로 저장되었습니다.', callback) {
        return await showSuccess(title, message, callback);
    }
    
    /**
     * 저장 실패 알림
     * 
     * @param {string} [title='저장 실패'] - 알림창 제목
     * @param {string} [message='데이터 저장 중 오류가 발생했습니다.'] - 알림 메시지
     * @param {Function} [callback] - 알림창 닫은 후 실행할 콜백 함수
     * @returns {Promise<Object>} 알림창 결과
     */
    async function notifySaveError(title = '저장 실패', message = '데이터 저장 중 오류가 발생했습니다.', callback) {
        return await showError(title, message, callback);
    }
    
    /**
     * 조회 실패 알림
     * 
     * @param {string} [title='조회 실패'] - 알림창 제목
     * @param {string} [message='데이터 조회 중 오류가 발생했습니다.'] - 알림 메시지
     * @param {Function} [callback] - 알림창 닫은 후 실행할 콜백 함수
     * @returns {Promise<Object>} 알림창 결과
     */
    async function notifyQueryError(title = '조회 실패', message = '데이터 조회 중 오류가 발생했습니다.', callback) {
        return await showError(title, message, callback);
    }
    
    /**
     * 삭제 성공 알림
     * 
     * @param {string} [title='삭제 완료'] - 알림창 제목
     * @param {string} [message='데이터가 성공적으로 삭제되었습니다.'] - 알림 메시지
     * @param {Function} [callback] - 알림창 닫은 후 실행할 콜백 함수
     * @returns {Promise<Object>} 알림창 결과
     */
    async function notifyDeleteSuccess(title = '삭제 완료', message = '데이터가 성공적으로 삭제되었습니다.', callback) {
        return await showSuccess(title, message, callback);
    }
    
    /**
     * 삭제 실패 알림
     * 
     * @param {string} [title='삭제 실패'] - 알림창 제목
     * @param {string} [message='데이터 삭제 중 오류가 발생했습니다.'] - 알림 메시지
     * @param {Function} [callback] - 알림창 닫은 후 실행할 콜백 함수
     * @returns {Promise<Object>} 알림창 결과
     */
    async function notifyDeleteError(title = '삭제 실패', message = '데이터 삭제 중 오류가 발생했습니다.', callback) {
        return await showError(title, message, callback);
    }
    
    /**
     * 유효성 검사 실패 알림
     * 
     * @param {string} [title='입력 오류'] - 알림창 제목
     * @param {string} message - 알림 메시지
     * @param {Function} [callback] - 알림창 닫은 후 실행할 콜백 함수
     * @returns {Promise<Object>} 알림창 결과
     */
    async function notifyValidationError(title = '입력 오류', message, callback) {
        return await showWarning(title, message, callback);
    }

    // 공개 API
    return {
        // 기본 알림창 함수
        showAlert,         // 기본 알림창 표시
        showSuccess,       // 성공 알림창 표시
        showError,         // 오류 알림창 표시
        showWarning,       // 경고 알림창 표시
        showInfo,          // 정보 알림창 표시
        showConfirm,       // 확인 대화상자 표시
        showPrompt,        // 입력 대화상자 표시
        showToast,         // 토스트 메시지 표시
        showChoice,        // 선택 대화상자 표시
        showLoading,       // 로딩 알림창 표시
        
        // 표준화된 작업 알림 함수
        notifySaveSuccess,    // 저장 성공 알림
        notifySaveError,      // 저장 실패 알림
        notifyQueryError,     // 조회 실패 알림
        notifyDeleteSuccess,  // 삭제 성공 알림
        notifyDeleteError,    // 삭제 실패 알림
        notifyValidationError // 유효성 검사 실패 알림
    };
})();