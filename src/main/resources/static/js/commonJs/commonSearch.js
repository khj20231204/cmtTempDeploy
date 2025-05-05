/**
 * SearchUtil - 검색 기능 공통 라이브러리
 * 
 * grid 검색을 위한 기능 공통 모듈
 * 그리드, 폼 등과 연계하여 다양한 검색 기능을 제공
 * 
 * @version 1.0.0
 * @since 2025-03-24
 */

const SearchUtil = (function() {
    // =============================
    // 기본 검색 관련 함수
    // =============================

    /**
     * 기본 검색 수행 함수
     * 검색 폼의 데이터를 수집하여 API 호출 후 그리드에 결과를 표시합니다.
     * 
     * @param {Object} options - 검색 옵션
     * @param {string} options.formId - 검색 폼 ID
     * @param {string} options.gridId - 결과를 표시할 그리드 ID
     * @param {string} options.url - 검색 API URL
     * @param {Object} [options.additionalParams] - 추가 파라미터
     * @param {Function} [options.beforeSearch] - 검색 전 실행할 함수 (반환값이 false이면 검색 중단)
     * @param {Function} [options.afterSearch] - 검색 후 실행할 함수 (매개변수: 검색 결과)
     * @param {Function} [options.onError] - 오류 발생 시 실행할 함수
     * 
     * @returns {Promise<Object>} 검색 결과를 담은 Promise
     */
    async function search(options) {
        try {
            // 최소 필수 옵션 검증: gridId와 url은 반드시 필요
            if (!options.gridId || !options.url) {
                throw new Error('필수 옵션이 누락되었습니다: gridId, url');
            }

            // 검색 파라미터 수집
            let searchParams = {};

            // formId가 제공된 경우 폼 데이터 수집
            if (options.formId) {
                if (window.UIUtil) {
                    searchParams = await UIUtil.getFormValues(options.formId);
                } else {
                    const form = document.getElementById(options.formId);
                    if (form) {
                        const formData = new FormData(form);
                        formData.forEach((value, key) => {
                            searchParams[key] = value;
                        });
                    } else {
                        console.warn(`ID가 '${options.formId}'인 폼을 찾을 수 없습니다.`);
                    }
                }
            }

            // 추가 파라미터 병합 (있는 경우)
            if (options.additionalParams) {
                searchParams = {
                    ...searchParams,
                    ...options.additionalParams
                };
            }

            // 검색 전 콜백 실행 (있는 경우)
            if (options.beforeSearch && typeof options.beforeSearch === 'function') {
                const shouldContinue = await options.beforeSearch(searchParams);
                if (shouldContinue === false) {
                    console.log('beforeSearch 콜백에 의해 검색이 중단되었습니다.');
                    return null;
                }
            }

            // 검색 API 호출 - 리팩토링된 ApiUtil 사용
            const result = await ApiUtil.getWithLoading(
                options.url, 
                searchParams, 
                '검색 중...'
            );

            // 그리드 결과 표시 - 리팩토링된 GridUtil 사용
            const grid = GridUtil.getGrid(options.gridId);
            if (grid) {
                const data = Array.isArray(result) ? result : (result.data || []);
                grid.resetData(data);
            } else {
                console.warn(`ID가 '${options.gridId}'인 그리드를 찾을 수 없습니다.`);
            }

            // 검색 후 콜백 실행 (있는 경우)
            if (options.afterSearch && typeof options.afterSearch === 'function') {
                await options.afterSearch(result);
            }

            return result;
        } catch (error) {
            console.error('검색 중 오류 발생:', error);

            // 오류 처리
            if (options.onError && typeof options.onError === 'function') {
                options.onError(error);
            } else if (window.AlertUtil) {
                await AlertUtil.showError('검색 실패', error.message);
            } else {
                alert(`검색 중 오류가 발생했습니다: ${error.message}`);
            }

            // 그리드 초기화
            const grid = GridUtil.getGrid(options.gridId);
            if (grid) {
                grid.resetData([]);
            }

            throw error;
        }
    }

    /**
     * 페이지네이션 검색 수행 함수
     * 
     * @param {Object} options - 검색 옵션
     * @param {string} options.formId - 검색 폼 ID
     * @param {string} options.gridId - 결과를 표시할 그리드 ID
     * @param {string} options.url - 검색 API URL
     * @param {string} options.pagerId - 페이지네이션 컨테이너 ID
     * @param {number} [options.pageSize=10] - 페이지당 행 수
     * @param {Object} [options.additionalParams] - 추가 파라미터
     * @param {Function} [options.beforeSearch] - 검색 전 실행할 함수
     * @param {Function} [options.afterSearch] - 검색 후 실행할 함수
     * 
     * @returns {Promise<Object>} 검색 결과를 담은 Promise
     */
    async function searchWithPagination(options) {
        try {
            if (!options.formId || !options.gridId || !options.url || !options.pagerId) {
                throw new Error('필수 옵션이 누락되었습니다: formId, gridId, url, pagerId');
            }

            // 검색 폼 데이터 수집
            let searchParams = {};
            if (window.UIUtil) {
                searchParams = await UIUtil.getFormValues(options.formId);
            } else {
                const form = document.getElementById(options.formId);
                if (form) {
                    const formData = new FormData(form);
                    formData.forEach((value, key) => {
                        searchParams[key] = value;
                    });
                } else {
                    throw new Error(`ID가 '${options.formId}'인 폼을 찾을 수 없습니다.`);
                }
            }

            // 페이지네이션 파라미터 추가
            const pageSize = options.pageSize || 10;
            searchParams.pageSize = pageSize;
            searchParams.page = searchParams.page || 1;

            // 추가 파라미터 병합
            if (options.additionalParams) {
                searchParams = {
                    ...searchParams,
                    ...options.additionalParams
                };
            }

            // 검색 전 콜백 실행 (있는 경우)
            if (options.beforeSearch && typeof options.beforeSearch === 'function') {
                const shouldContinue = await options.beforeSearch(searchParams);
                if (shouldContinue === false) {
                    return null;
                }
            }

            // 검색 API 호출 - 리팩토링된 ApiUtil 사용
            const result = await ApiUtil.getWithLoading(
                options.url, 
                searchParams, 
                '검색 중...'
            );

            // 그리드 결과 표시 - 리팩토링된 GridUtil 사용
            const grid = GridUtil.getGrid(options.gridId);
            if (grid) {
                const items = Array.isArray(result) ? result : (result.data || []);
                grid.resetData(items);
            }

            // 페이지네이션 렌더링
            await renderPagination({
                pagerId: options.pagerId,
                currentPage: result.currentPage || searchParams.page || 1,
                totalPages: result.totalPages || 1,
                totalItems: result.totalItems || (Array.isArray(result.data) ? result.data.length : 0),
                pageSize: pageSize,
                onPageChange: async (page) => {
                    // 페이지 변경 시 재검색
                    await searchWithPagination({
                        ...options,
                        additionalParams: {
                            ...options.additionalParams,
                            page: page
                        }
                    });
                }
            });

            // 검색 후 콜백 실행 (있는 경우)
            if (options.afterSearch && typeof options.afterSearch === 'function') {
                await options.afterSearch(result);
            }

            return result;
        } catch (error) {
            console.error('페이지네이션 검색 중 오류 발생:', error);

            // 오류 처리
            if (window.AlertUtil) {
                await AlertUtil.showError('검색 실패', error.message);
            } else {
                alert(`검색 중 오류가 발생했습니다: ${error.message}`);
            }

            // 그리드 초기화
            const grid = GridUtil.getGrid(options.gridId);
            if (grid) {
                grid.resetData([]);
            }

            throw error;
        }
    }

    /**
     * 페이지네이션 렌더링 함수
     * 
     * @param {Object} options - 페이지네이션 옵션
     * @param {string} options.pagerId - 페이지네이션 컨테이너 ID
     * @param {number} options.currentPage - 현재 페이지 번호
     * @param {number} options.totalPages - 전체 페이지 수
     * @param {number} options.totalItems - 전체 항목 수
     * @param {number} options.pageSize - 페이지당 행 수
     * @param {Function} options.onPageChange - 페이지 변경 시 호출할 함수
     * 
     * @returns {Promise<boolean>} 성공 여부
     * @private
     */
    async function renderPagination(options) {
        try {
            const pager = document.getElementById(options.pagerId);
            if (!pager) {
                throw new Error(`ID가 '${options.pagerId}'인 페이지네이션 컨테이너를 찾을 수 없습니다.`);
            }

            const currentPage = options.currentPage;
            const totalPages = options.totalPages;

            // 페이지네이션 HTML 생성
            let html = `
                <div class="pagination">
                    <div class="pagination-info">
                        전체 ${options.totalItems}개 항목 중 ${(currentPage - 1) * options.pageSize + 1}-${Math.min(currentPage * options.pageSize, options.totalItems)}
                    </div>
                    <ul class="pagination-list">
            `;

            // 이전 페이지 버튼
            html += `<li class="page-item ${currentPage === 1 ? 'disabled' : ''}">
                        <a href="#" class="page-link" data-page="${currentPage - 1}">이전</a>
                     </li>`;

            // 페이지 번호 버튼
            const maxPageButtons = 5;
            let startPage = Math.max(1, currentPage - Math.floor(maxPageButtons / 2));
            let endPage = Math.min(totalPages, startPage + maxPageButtons - 1);

            if (endPage - startPage + 1 < maxPageButtons) {
                startPage = Math.max(1, endPage - maxPageButtons + 1);
            }

            for (let i = startPage; i <= endPage; i++) {
                html += `<li class="page-item ${i === currentPage ? 'active' : ''}">
                            <a href="#" class="page-link" data-page="${i}">${i}</a>
                         </li>`;
            }

            // 다음 페이지 버튼
            html += `<li class="page-item ${currentPage === totalPages ? 'disabled' : ''}">
                        <a href="#" class="page-link" data-page="${currentPage + 1}">다음</a>
                     </li>`;

            html += `
                    </ul>
                </div>
            `;

            // HTML 설정
            pager.innerHTML = html;

            // 이벤트 리스너 등록
            const pageLinks = pager.querySelectorAll('.page-link');
            pageLinks.forEach(link => {
                link.addEventListener('click', async function(e) {
                    e.preventDefault();

                    // disabled 클래스가 있는 경우 무시
                    if (this.parentNode.classList.contains('disabled')) {
                        return;
                    }

                    const page = parseInt(this.getAttribute('data-page'));
                    if (page !== currentPage) {
                        await options.onPageChange(page);
                    }
                });
            });

            return true;
        } catch (error) {
            console.error('페이지네이션 렌더링 중 오류:', error);
            return false;
        }
    }

    /**
     * 검색 폼 초기화 함수
     * 검색 폼의 값을 초기화하고 관련 그리드도 비웁니다.
     * 
     * @param {Object} options - 초기화 옵션
     * @param {string} options.formId - 검색 폼 ID
     * @param {string} [options.gridId] - 초기화할 그리드 ID (선택 사항)
     * @param {Array<string>} [options.excludeFields=[]] - 초기화에서 제외할 필드 이름 배열
     * @param {Function} [options.callback] - 초기화 후 실행할 콜백 함수
     * 
     * @returns {Promise<boolean>} 초기화 성공 여부를 담은 Promise
     */
    async function resetSearchForm(options) {
        try {
            if (!options.formId) {
                throw new Error('formId는 필수입니다.');
            }

            // 폼 초기화 - 리팩토링된 UIUtil 사용
            if (window.UIUtil) {
                await UIUtil.clearForm(options.formId, {
                    exclude: options.excludeFields || []
                });
            } else {
                const form = document.getElementById(options.formId);
                if (form) {
                    // 제외 필드 처리
                    if (options.excludeFields && options.excludeFields.length > 0) {
                        const originalValues = {};

                        // 제외 필드의 원래 값 저장
                        options.excludeFields.forEach(fieldName => {
                            const field = form.elements[fieldName];
                            if (field) {
                                originalValues[fieldName] = field.value;
                            }
                        });

                        // 폼 초기화
                        form.reset();

                        // 제외 필드 값 복원
                        options.excludeFields.forEach(fieldName => {
                            const field = form.elements[fieldName];
                            if (field && originalValues[fieldName] !== undefined) {
                                field.value = originalValues[fieldName];
                            }
                        });
                    } else {
                        form.reset();
                    }
                } else {
                    throw new Error(`ID가 '${options.formId}'인 폼을 찾을 수 없습니다.`);
                }
            }

            // 그리드 초기화 (옵션으로 지정된 경우) - 리팩토링된 GridUtil 사용
            if (options.gridId) {
                const grid = GridUtil.getGrid(options.gridId);
                if (grid) {
                    grid.resetData([]);
                }
            }

            // 콜백 실행 (있는 경우)
            if (options.callback && typeof options.callback === 'function') {
                await options.callback();
            }

            return true;
        } catch (error) {
            console.error('검색 폼 초기화 중 오류 발생:', error);

            if (window.AlertUtil) {
                await AlertUtil.showError('폼 초기화 실패', error.message);
            } else {
                alert(`폼 초기화 중 오류가 발생했습니다: ${error.message}`);
            }

            return false;
        }
    }

    // =============================
    // 코드/항목 검색 관련 함수
    // =============================

    /**
     * 코드 팝업 검색 함수
     * 
     * @param {Object} options - 코드 검색 옵션
     * @param {string} options.url - 코드 조회 API URL
     * @param {string} [options.title='코드 검색'] - 팝업 제목
     * @param {Object} [options.params={}] - 검색 파라미터
     * @param {Array<Object>} [options.columns] - 팝업 그리드 컬럼 정의
     * @param {Function} options.callback - 코드 선택 후 실행할 콜백 함수
     * 
     * @returns {Promise<Object>} 선택된 코드 정보
     */
    async function openCodePopup(options) {
        try {
            if (!options.url) {
                throw new Error('url은 필수입니다.');
            }

            // 기본 팝업 설정
            const popupId = `codePopup_${Date.now()}`;
            const title = options.title || '코드 검색';
            const width = options.width || 800;
            const height = options.height || 600;

            // 팝업 생성
            const popupHtml = `
                <div id="${popupId}" class="code-popup">
                    <div class="popup-header">
                        <h3>${title}</h3>
                        <button type="button" class="close-btn">&times;</button>
                    </div>
                    <div class="popup-body">
                        <div class="search-area">
                            <input type="text" id="${popupId}_keyword" placeholder="검색어 입력" class="search-input" />
                            <button type="button" id="${popupId}_searchBtn" class="search-btn">검색</button>
                        </div>
                        <div id="${popupId}_grid" class="grid-container"></div>
                    </div>
                    <div class="popup-footer">
                        <button type="button" id="${popupId}_selectBtn" class="select-btn">선택</button>
                        <button type="button" id="${popupId}_cancelBtn" class="cancel-btn">취소</button>
                    </div>
                </div>
                <div id="${popupId}_overlay" class="popup-overlay"></div>
            `;

            // 팝업 스타일
            const popupStyle = `
                <style>
                    .popup-overlay {
                        position: fixed;
                        top: 0;
                        left: 0;
                        width: 100%;
                        height: 100%;
                        background-color: rgba(0, 0, 0, 0.5);
                        z-index: 9998;
                    }
                    .code-popup {
                        position: fixed;
                        top: 50%;
                        left: 50%;
                        transform: translate(-50%, -50%);
                        width: ${width}px;
                        height: ${height}px;
                        background-color: #fff;
                        border-radius: 5px;
                        box-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
                        z-index: 9999;
                        display: flex;
                        flex-direction: column;
                    }
                    .popup-header {
                        display: flex;
                        justify-content: space-between;
                        align-items: center;
                        padding: 15px;
                        border-bottom: 1px solid #ddd;
                    }
                    .popup-header h3 {
                        margin: 0;
                    }
                    .close-btn {
                        background: none;
                        border: none;
                        font-size: 20px;
                        cursor: pointer;
                    }
                    .popup-body {
                        flex: 1;
                        padding: 15px;
                        overflow: auto;
                    }
                    .search-area {
                        display: flex;
                        margin-bottom: 15px;
                    }
                    .search-input {
                        flex: 1;
                        padding: 8px;
                        border: 1px solid #ddd;
                        border-radius: 4px;
                    }
                    .search-btn {
                        padding: 8px 15px;
                        margin-left: 10px;
                        background-color: #007bff;
                        color: white;
                        border: none;
                        border-radius: 4px;
                        cursor: pointer;
                    }
                    .grid-container {
                        height: calc(100% - 50px);
                    }
                    .popup-footer {
                        padding: 15px;
                        border-top: 1px solid #ddd;
                        text-align: right;
                    }
                    .popup-footer button {
                        padding: 8px 15px;
                        margin-left: 10px;
                        border: none;
                        border-radius: 4px;
                        cursor: pointer;
                    }
                    .select-btn {
                        background-color: #28a745;
                        color: white;
                    }
                    .cancel-btn {
                        background-color: #6c757d;
                        color: white;
                    }
                </style>
            `;

            // 팝업 추가
            const tempDiv = document.createElement('div');
            tempDiv.innerHTML = popupHtml + popupStyle;
            document.body.appendChild(tempDiv.firstElementChild); // 팝업
            document.body.appendChild(tempDiv.firstElementChild); // 오버레이

            // Promise 반환 (팝업 닫힐 때 resolve)
            return new Promise(async (resolve, reject) => {
                try {
                    // 팝업 요소 참조
                    const popup = document.getElementById(popupId);
                    const overlay = document.getElementById(`${popupId}_overlay`);
                    const keywordInput = document.getElementById(`${popupId}_keyword`);
                    const searchBtn = document.getElementById(`${popupId}_searchBtn`);
                    const selectBtn = document.getElementById(`${popupId}_selectBtn`);
                    const cancelBtn = document.getElementById(`${popupId}_cancelBtn`);
                    const closeBtn = popup.querySelector('.close-btn');

                    // 선택된 코드 정보
                    let selectedCode = null;

                    // 그리드 초기화 - 리팩토링된 GridUtil 사용
                    const gridId = `${popupId}_grid`;
                    const columns = options.columns || [{
                            name: 'code',
                            header: '코드'
                        },
                        {
                            name: 'name',
                            header: '이름'
                        }
                    ];

                    // 그리드 생성
                    GridUtil.registerGrid({
                        id: gridId,
                        columns: columns
                    });

                    // 행 더블클릭 이벤트
                    GridUtil.onDblClick(gridId, (rowData) => {
                        selectedCode = rowData;
                        closePopup(true);
                    });

                    // 행 클릭 이벤트
                    GridUtil.onRowClick(gridId, (rowData) => {
                        selectedCode = rowData;
                    });

                    // 검색 함수
                    const searchCodes = async (keyword = '') => {
                        try {
                            // 검색 파라미터
                            const params = {
                                ...options.params,
                                keyword: keyword
                            };

                            // 리팩토링된 ApiUtil 사용
                            const response = await ApiUtil.getWithLoading(
                                options.url, 
                                params, 
                                '검색 중...'
                            );

                            // 그리드 데이터 설정
                            const grid = GridUtil.getGrid(gridId);
                            if (grid) {
                                const items = Array.isArray(response) ? response : (response.data || []);
                                grid.resetData(items);
                            }

                            return response;
                        } catch (error) {
                            console.error('코드 검색 중 오류:', error);

                            if (window.AlertUtil) {
                                await AlertUtil.showError('검색 실패', error.message);
                            } else {
                                alert(`검색 중 오류가 발생했습니다: ${error.message}`);
                            }

                            // 그리드 초기화
                            const grid = GridUtil.getGrid(gridId);
                            if (grid) {
                                grid.resetData([]);
                            }

                            throw error;
                        }
                    };

                    // 팝업 닫기 함수
                    const closePopup = (isConfirmed = false) => {
                        // 선택 콜백 실행
                        if (isConfirmed && selectedCode && options.callback) {
                            options.callback(selectedCode);
                        }

                        // 그리드 제거
                        GridUtil.unregisterGrid(gridId);

                        // 팝업 제거
                        document.body.removeChild(popup);
                        document.body.removeChild(overlay);

                        // Promise 해결
                        resolve(isConfirmed ? selectedCode : null);
                    };

                    // 이벤트 리스너 등록
                    searchBtn.addEventListener('click', () => searchCodes(keywordInput.value));
                    keywordInput.addEventListener('keyup', async (e) => {
                        if (e.key === 'Enter') {
                            await searchCodes(keywordInput.value);
                        }
                    });

                    selectBtn.addEventListener('click', async () => {
                        if (!selectedCode) {
                            if (window.AlertUtil) {
                                await AlertUtil.showWarning('선택 필요', '항목을 선택해주세요.');
                            } else {
                                alert('항목을 선택해주세요.');
                            }
                            return;
                        }
                        closePopup(true);
                    });

                    cancelBtn.addEventListener('click', () => closePopup(false));
                    closeBtn.addEventListener('click', () => closePopup(false));

                    // 초기 검색 실행
                    await searchCodes('');
                } catch (error) {
                    console.error('코드 팝업 오류:', error);
                    reject(error);
                }
            });
        } catch (error) {
            console.error('코드 팝업 생성 중 오류 발생:', error);

            if (window.AlertUtil) {
                await AlertUtil.showError('팝업 생성 실패', error.message);
            } else {
                alert(`팝업 생성 중 오류가 발생했습니다: ${error.message}`);
            }

            return null;
        }
    }

    // 공개 API
    return {
        // 기본 검색 기능
        search,                  // 기본 검색
        searchWithPagination,    // 페이지네이션 검색
        resetSearchForm,         // 검색 폼 초기화

        // 코드/항목 검색
        openCodePopup            // 코드 팝업 검색
    };
})();