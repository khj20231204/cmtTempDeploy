/**
 * GridSearchUtil - 그리드 검색 기능 공통 라이브러리
 * 
 * TOAST UI 그리드 검색 및 필터링 기능을 제공하는 공통 모듈
 * 
 * @version 1.0.0
 * @since 2025-04-21
 */

const GridSearchUtil = (function() {
    // =============================
    // 내부 변수
    // =============================
    
    // 그리드별 원본 데이터 저장소
    const gridOriginalDataMap = new Map();
    
    // =============================
    // 기본 검색 관련 함수
    // =============================
    
    /**
     * 그리드에 검색 기능 연결
     * 
     * @param {Object} options - 검색 옵션
     * @param {string} options.gridId - 대상 그리드 ID
     * @param {string} options.searchInputId - 검색 입력 필드 ID
     * @param {Function} [options.customFilter] - 사용자 정의 필터 함수 (선택사항)
     * @param {boolean} [options.autoSearch=true] - 입력 시 자동 검색 여부
     * @param {string} [options.searchBtnId] - 검색 버튼 ID (자동 검색이 false일 때 필수)
     * @param {Function} [options.beforeSearch] - 검색 전 실행할 함수
     * @param {Function} [options.afterSearch] - 검색 후 실행할 함수
     * 
     * @returns {boolean} 검색 기능 연결 성공 여부
     */
    function setupGridSearch(options) {
        try {
            console.log('그리드 검색 설정 시작:', options.gridId);
            
            // 필수 옵션 검증
            if (!options.gridId || !options.searchInputId) {
                console.error('필수 옵션이 누락되었습니다: gridId, searchInputId');
                return false;
            }
            
            // 그리드 참조 가져오기
            const grid = GridUtil.getGrid(options.gridId);
            if (!grid) {
                console.error(`ID가 '${options.gridId}'인 그리드를 찾을 수 없습니다.`);
                return false;
            }
            
            // 검색 입력 필드 참조 가져오기
            const searchInput = document.getElementById(options.searchInputId);
            if (!searchInput) {
                console.error(`ID가 '${options.searchInputId}'인 검색 입력 필드를 찾을 수 없습니다.`);
                return false;
            }
            
            // 원본 데이터 저장
            const originalData = grid.getData();
            gridOriginalDataMap.set(options.gridId, originalData);
            
            // 검색 함수 정의
            const performSearch = function() {
                // 검색 전 콜백 실행
                if (options.beforeSearch && typeof options.beforeSearch === 'function') {
                    const shouldContinue = options.beforeSearch();
                    if (shouldContinue === false) {
                        return;
                    }
                }
                
                const keyword = searchInput.value.toLowerCase();
                const originalData = gridOriginalDataMap.get(options.gridId);
                
                let filteredData;
                
                // 사용자 정의 필터가 있으면 사용
                if (options.customFilter && typeof options.customFilter === 'function') {
                    filteredData = options.customFilter(originalData, keyword);
                } else {
                    // 기본 필터 로직
                    filteredData = originalData.filter(row => {
                        return Object.values(row).some(value => {
                            if (value === null || value === undefined) return false;
                            return String(value).toLowerCase().includes(keyword);
                        });
                    });
                }
                
                // 필터링된 데이터로 그리드 업데이트
                grid.resetData(filteredData);
                
                // 검색 후 콜백 실행
                if (options.afterSearch && typeof options.afterSearch === 'function') {
                    options.afterSearch(filteredData);
                }
            };
            
            // 이벤트 연결
            const autoSearch = options.autoSearch !== false; // 기본값은 true
            
            if (autoSearch) {
                // 입력 시 자동 검색
                searchInput.addEventListener('input', performSearch);
            } else if (options.searchBtnId) {
                // 버튼 클릭 시 검색
                const searchBtn = document.getElementById(options.searchBtnId);
                if (searchBtn) {
                    searchBtn.addEventListener('click', performSearch);
                } else {
                    console.error(`ID가 '${options.searchBtnId}'인 검색 버튼을 찾을 수 없습니다.`);
                    return false;
                }
                
                // 엔터키 검색 지원
                searchInput.addEventListener('keyup', function(e) {
                    if (e.key === 'Enter') {
                        performSearch();
                    }
                });
            } else {
                console.error('자동 검색이 비활성화된 경우 searchBtnId가 필요합니다.');
                return false;
            }
            
            console.log('그리드 검색 설정 완료:', options.gridId);
            return true;
        } catch (error) {
            console.error('그리드 검색 설정 중 오류 발생:', error);
            return false;
        }
    }
    
    /**
     * 날짜 범위 검색 및 키워드 검색을 함께 설정
     * 
     * @param {Object} options - 검색 옵션
     * @param {string} options.gridId - 대상 그리드 ID
     * @param {string} options.searchInputId - 키워드 검색 입력 필드 ID
     * @param {string} options.startDateId - 시작 날짜 입력 필드 ID
     * @param {string} options.endDateId - 종료 날짜 입력 필드 ID
     * @param {string} options.dateSearchBtnId - 날짜 검색 버튼 ID
     * @param {string} options.dateColumnName - 날짜 컬럼 이름
     * @param {boolean} [options.autoKeywordSearch=true] - 키워드 입력 시 자동 검색 여부
     * @param {Function} [options.beforeSearch] - 검색 전 실행할 함수
     * @param {Function} [options.afterSearch] - 검색 후 실행할 함수
     * 
     * @returns {boolean} 검색 기능 연결 성공 여부
     */
    function setupDateRangeSearch(options) {
        try {
            console.log('날짜 범위 및 키워드 검색 설정 시작:', options.gridId);
            
            // 필수 옵션 검증
            if (!options.gridId || !options.searchInputId || 
                !options.startDateId || !options.endDateId || 
                !options.dateSearchBtnId || !options.dateColumnName) {
                console.error('필수 옵션이 누락되었습니다.');
                return false;
            }
            
            // 그리드 참조 가져오기
            const grid = GridUtil.getGrid(options.gridId);
            if (!grid) {
                console.error(`ID가 '${options.gridId}'인 그리드를 찾을 수 없습니다.`);
                return false;
            }
            
            // 각 요소 참조 가져오기
            const searchInput = document.getElementById(options.searchInputId);
            const startDateInput = document.getElementById(options.startDateId);
            const endDateInput = document.getElementById(options.endDateId);
            const dateSearchBtn = document.getElementById(options.dateSearchBtnId);
            
            if (!searchInput || !startDateInput || !endDateInput || !dateSearchBtn) {
                console.error('검색 관련 HTML 요소를 찾을 수 없습니다.');
                return false;
            }
            
            // 원본 데이터 저장
            const originalData = grid.getData();
            gridOriginalDataMap.set(options.gridId, originalData);
            
            // 통합 검색 함수 정의
            const applyFilter = function() {
                // 검색 전 콜백 실행
                if (options.beforeSearch && typeof options.beforeSearch === 'function') {
                    const shouldContinue = options.beforeSearch();
                    if (shouldContinue === false) {
                        return;
                    }
                }
                
                const keyword = searchInput.value.toLowerCase();
                const start = startDateInput.value;
                const end = endDateInput.value;
                const originalData = gridOriginalDataMap.get(options.gridId);
                
                let filteredData = originalData;
                
                // 날짜 범위 필터링
                if (start && end) {
                    const startDate = new Date(start);
                    const endDate = new Date(end);
                    endDate.setHours(23, 59, 59, 999); // 종료일은 해당 일자의 마지막 시간으로 설정
                    
                    filteredData = filteredData.filter(row => {
                        const rowDate = new Date(row[options.dateColumnName]);
                        return rowDate >= startDate && rowDate <= endDate;
                    });
                }
                
                // 키워드 필터링
                if (keyword) {
                    filteredData = filteredData.filter(row => {
                        return Object.values(row).some(value => {
                            if (value === null || value === undefined) return false;
                            return String(value).toLowerCase().includes(keyword);
                        });
                    });
                }
                
                // 필터링된 데이터로 그리드 업데이트
                grid.resetData(filteredData);
                
                // 검색 후 콜백 실행
                if (options.afterSearch && typeof options.afterSearch === 'function') {
                    options.afterSearch(filteredData);
                }
            };
            
            // 이벤트 연결
            dateSearchBtn.addEventListener('click', applyFilter);
            
            // 키워드 자동 검색 설정 (기본값은 true)
            const autoKeywordSearch = options.autoKeywordSearch !== false;
            if (autoKeywordSearch) {
                searchInput.addEventListener('input', applyFilter);
            } else {
                // 엔터키 검색 지원
                searchInput.addEventListener('keyup', function(e) {
                    if (e.key === 'Enter') {
                        applyFilter();
                    }
                });
            }
            
            console.log('날짜 범위 및 키워드 검색 설정 완료:', options.gridId);
            return true;
        } catch (error) {
            console.error('날짜 범위 검색 설정 중 오류 발생:', error);
            return false;
        }
    }
    
    /**
     * 원본 데이터 다시 가져오기
     * 검색 및 필터링 초기화를 위해 그리드의 원본 데이터를 복원합니다.
     * 
     * @param {string} gridId - 그리드 ID
     * @returns {boolean} 성공 여부
     */
    function resetToOriginalData(gridId) {
        try {
            const grid = GridUtil.getGrid(gridId);
            if (!grid) {
                console.error(`ID가 '${gridId}'인 그리드를 찾을 수 없습니다.`);
                return false;
            }
            
            const originalData = gridOriginalDataMap.get(gridId);
            if (!originalData) {
                console.warn(`ID가 '${gridId}'인 그리드의 원본 데이터가 없습니다.`);
                return false;
            }
            
            grid.resetData(originalData);
            return true;
        } catch (error) {
            console.error('원본 데이터 복원 중 오류 발생:', error);
            return false;
        }
    }
    
    /**
     * 원본 데이터 업데이트
     * API 등에서 새 데이터가 로드된 경우 원본 데이터를 업데이트합니다.
     * 
     * @param {string} gridId - 그리드 ID
     * @param {Array} [newData] - 새 원본 데이터 (제공되지 않으면 현재 그리드 데이터 사용)
     * @returns {boolean} 성공 여부
     */
    function updateOriginalData(gridId, newData) {
        try {
            const grid = GridUtil.getGrid(gridId);
            if (!grid) {
                console.error(`ID가 '${gridId}'인 그리드를 찾을 수 없습니다.`);
                return false;
            }
            
            // 새 데이터가 없으면 현재 그리드 데이터 사용
            const dataToStore = newData || grid.getData();
            gridOriginalDataMap.set(gridId, dataToStore);
            
            return true;
        } catch (error) {
            console.error('원본 데이터 업데이트 중 오류 발생:', error);
            return false;
        }
    }
    
    // 공개 API
    return {
        setupGridSearch,          // 기본 그리드 검색 설정
        setupDateRangeSearch,     // 날짜 범위 검색 설정
        resetToOriginalData,      // 원본 데이터로 복원
        updateOriginalData        // 원본 데이터 업데이트
    };
})();