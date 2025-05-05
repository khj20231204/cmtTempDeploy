/**
 * 제품 재고관리 - 재고 정보 관리 모듈
 * 
 * 제품 재고 정보의 조회 기능을 담당하는 관리 모듈입니다.
 * 
 * @version 1.0.0
 * @since 2025-04-29
 */
const ProductsInventoryManager = (function() {
    // =============================
    // 모듈 내부 변수 - 필요에 따라 변경하세요
    // =============================

    // 그리드 인스턴스 참조
    let pInventoryGrid;

    // API URL 상수 정의
    const API_URLS = {
        LIST: '/api/productsInventory/list',            // 데이터 목록 조회
        SAVE: '/api/productsInventory/save',            // 데이터 저장
        DELETE: '/api/productsInventory/delete',        // 데이터 삭제
        EXCEL: {
            UPLOAD: '/api/productsInventory/excel/upload',  // 엑셀 업로드 API URL
            DOWNLOAD: '/api/productsInventory/excel/download' // 엑셀 다운로드 API URL
        }
    };

    // =============================
    // 초기화 및 이벤트 처리 함수
    // =============================

    /**
     * 모듈 초기화 함수
     * 관리 모듈의 초기 설정 및 이벤트 바인딩을 수행합니다.
     */
    async function init() {
        try {
            console.log('제품 재고관리 초기화를 시작합니다.');

            // 그리드 초기화
            await initGrid();

            // 이벤트 리스너 등록
            await registerEvents();
            
            // 그리드 검색 초기화
            initGridSearch();
            
            // 엑셀 기능 초기화
            initExcelFeatures();

            console.log('제품 재고관리 초기화가 완료되었습니다.');
        } catch (error) {
            console.error('초기화 중 오류 발생:', error);
            await AlertUtil.showError('초기화 오류', '제품 재고관리 초기화 중 오류가 발생했습니다.');
        }
    }

    /**
     * 이벤트 리스너 등록 함수
     * 버튼 클릭 및 엔터키 검색 등의 이벤트 리스너를 등록합니다.
     */
    async function registerEvents() {
        try {
            // UIUtil을 사용하여 이벤트 리스너 등록
            await UIUtil.registerEventListeners({
                'pInventorySearchBtn': searchData              // 데이터 검색 버튼
                // 엑셀 버튼 이벤트는 ExcelUtil에서 별도로 처리됩니다
            });

            // 엔터키 검색 이벤트 등록
            await UIUtil.bindEnterKeySearch('pInventoryInput', searchData);
        } catch (error) {
            console.error('이벤트 리스너 등록 중 오류:', error);
            throw error;
        }
    }
    
    /**
     * 그리드 검색 초기화 함수
     * GridSearchUtil을 사용하여 그리드 검색 기능을 설정합니다.
     */
    function initGridSearch() {
        try {
            console.log('그리드 검색 초기화');
            
            // 그리드 검색 설정
            GridSearchUtil.setupGridSearch({
                gridId: 'pInventoryGrid',
                searchInputId: 'pInventoryInput',
                autoSearch: true, // 입력 시 자동 검색
                beforeSearch: function() {
                    console.log('그리드 검색 시작');
                    return true;
                },
                afterSearch: function(filteredData) {
                    console.log('그리드 검색 완료, 결과:', filteredData.length, '건');
                }
            });
        } catch (error) {
            console.error('그리드 검색 초기화 중 오류:', error);
        }
    }
    
    /**
     * 엑셀 기능 초기화 함수
     * ExcelUtil을 사용하여 엑셀 다운로드/업로드 기능을 설정합니다.
     */
    function initExcelFeatures() {
        try {
            console.log('엑셀 기능 초기화');
            
            // 엑셀 다운로드 버튼 설정 - HTML에 버튼 추가 필요
            ExcelUtil.setupExcelDownloadButton({
                buttonId: 'pInventoryExcelDownBtn', 
                gridId: 'pInventoryGrid',
                fileName: 'products-inventory-data.xlsx',
                sheetName: '제품재고정보',
                beforeDownload: function() {
                    console.log('엑셀 다운로드 시작');
                    return true;
                },
                afterDownload: function() {
                    console.log('엑셀 다운로드 완료');
                }
            });
        } catch (error) {
            console.error('엑셀 기능 초기화 중 오류:', error);
        }
    }

    // =============================
    // 그리드 관련 함수
    // =============================

    /**
     * 그리드 초기화 함수
     * 그리드를 생성하고 초기 데이터를 로드합니다.
     */
    async function initGrid() {
        try {
            console.log('그리드 초기화를 시작합니다.');

            // DOM 요소 존재 확인
            const gridElement = document.getElementById('pInventoryGrid');
            if (!gridElement) {
                throw new Error('pInventoryGrid 요소를 찾을 수 없습니다. HTML을 확인해주세요.');
            }

            // 서버에서 받은 데이터 활용
            const gridData = window.pInventoryList || [];
            console.log('초기 데이터:', gridData.length, '건');

            // 그리드 생성 - GridUtil 사용
            pInventoryGrid = GridUtil.registerGrid({
                id: 'pInventoryGrid',
                columns: [
                    {
                        header: '제품코드',
                        name: 'PDT_CODE',
                        editor: false,
                        sortable: true
                    },
                    {
                        header: '제품명',
                        name: 'PDT_NAME',
                        editor: false,
                        sortable: true
                    },
                    {
                        header: '창고코드',
                        name: 'WAREHOUSE_CODE',
                        editor: false,
                        sortable: true
                    },
                    {
                        header: '위치코드',
                        name: 'LOCATION_CODE',
                        editor: false,
                        sortable: true
                    },
                    {
                        header: '현재수량',
                        name: 'CURRENT_QTY',
                        editor: false,
                        sortable: true,
                        align: 'right'
                    },
                    {
                        header: '할당수량',
                        name: 'ALLOCATED_QTY',
                        editor: false,
                        sortable: true,
                        align: 'right'
                    },
                    {
                        header: '가용수량',
                        name: 'AVAILABLE_QTY',
                        editor: false, // 자동 계산되므로 편집 불가
                        sortable: true,
                        align: 'right',
                        formatter: function({ value }) {
                          const isLow = parseInt(value) <= 100;
                          const style = isLow ? 'color:red; font-weight:bold;' : 'color:black;';
                          return `<span style="${style}">${value}</span>`;
                        }
                    },
                    {
                        header: '마지막이동일',
                        name: 'LAST_MOVEMENT_DATE',
                        editor: false,
                        sortable: true,
                        formatter: function({ value }) {
                            if (!value) return '';
                            const date = new Date(value);
                            return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
                        }
                    }
                ],
                columnOptions: {
                    resizable: true
                },
                pageOptions: {
                    useClient: true,
                    perPage: 10
                },
                data: gridData,
                draggable: false,
                gridOptions: {
                    rowHeaders: ['rowNum']
                }
            });
            
            // 그리드 원본 데이터 저장 (검색 기능 위해 추가)
            GridSearchUtil.updateOriginalData('pInventoryGrid', gridData);

            console.log('그리드 초기화가 완료되었습니다.');
        } catch (error) {
            console.error('그리드 초기화 오류:', error);
            throw error;
        }
    }

    /**
     * 데이터 검색 함수
     * 검색어를 이용하여 데이터를 검색하고 그리드에 결과를 표시합니다.
     */
    async function searchData() {
        try {
            const keyword = document.getElementById('pInventoryInput').value;
            console.log('데이터 검색 시작. 검색어:', keyword);

            // API 호출
            const response = await ApiUtil.getWithLoading(
                API_URLS.LIST, 
                {
                    keyword: keyword
                },
                '데이터 검색 중...'
            );

            // 응답 데이터 처리
            const data = Array.isArray(response) ? response : (response.data || []);

            // 그리드 데이터 설정
            const grid = GridUtil.getGrid('pInventoryGrid');
            if (grid) {
                grid.resetData(data);
                
                // 그리드 원본 데이터 업데이트 (검색 기능 위해 추가)
                GridSearchUtil.updateOriginalData('pInventoryGrid', data);
            }

            console.log('데이터 검색 완료. 결과:', data.length, '건');
            return data;
        } catch (error) {
            console.error('데이터 검색 중 오류:', error);
            await AlertUtil.showError('검색 오류', '데이터 검색 중 오류가 발생했습니다.');
            throw error;
        }
    }

    /**
     * 로컬 검색 함수
     * 그리드 내 로컬 데이터를 대상으로 검색을 수행합니다.
     */
    function performLocalSearch() {
        try {
            const keyword = document.getElementById('pInventoryInput').value.toLowerCase();
            
            // 원본 데이터 가져오기
            GridSearchUtil.resetToOriginalData('pInventoryGrid');
            const grid = GridUtil.getGrid('pInventoryGrid');
            const originalData = grid.getData();
            
            // 필터링
            const filtered = originalData.filter(row => {
                return Object.values(row).some(val => {
                    if (val == null) return false;
                    return String(val).toLowerCase().includes(keyword);
                });
            });
            
            // 그리드 업데이트
            grid.resetData(filtered);
            console.log('로컬 검색 완료, 결과:', filtered.length, '건');
            
            return filtered;
        } catch (error) {
            console.error('로컬 검색 중 오류:', error);
            return [];
        }
    }
    
    /**
     * 그리드 인스턴스 반환 함수
     * 외부에서 그리드 인스턴스에 직접 접근할 수 있습니다.
     * 
     * @returns {Object} 그리드 인스턴스
     */
    function getGrid() {
        return pInventoryGrid;
    }

    // =============================
    // 공개 API - 외부에서 접근 가능한 메서드
    // =============================
    return {
        // 초기화 및 기본 기능
        init,           // 모듈 초기화

        // 데이터 관련 함수
        searchData,     // 데이터 검색
        
        // 유틸리티 함수
        getGrid,               // 그리드 인스턴스 반환
        performLocalSearch     // 로컬 검색 실행
    };
})();

// =============================
// DOM 로드 시 초기화
// =============================
document.addEventListener('DOMContentLoaded', async function() {
    try {
        // 제품 재고관리 초기화
        await ProductsInventoryManager.init();
    } catch (error) {
        console.error('초기화 중 오류 발생:', error);
        if (window.AlertUtil) {
            await AlertUtil.showError('초기화 오류', '제품 재고관리 초기화 중 오류가 발생했습니다.');
        } else {
            alert('제품 재고관리 초기화 중 오류가 발생했습니다.');
        }
    }
});