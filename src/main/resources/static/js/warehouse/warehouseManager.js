/**
 * 창고 기준정보 관리 모듈
 * 
 * 창고 기준정보의 조회, 추가, 수정, 삭제 기능을 담당하는 관리 모듈입니다.
 * 마스터-디테일 구조로 창고 기준정보(마스터)와 위치정보(디테일)를 관리합니다.
 * 
 * @version 1.1.0
 * @since 2025-05-03
 */
const WarehouseManager = (function() {
    // =============================
    // 모듈 내부 변수
    // =============================

    // 그리드 인스턴스 참조
    let warehouseGrid;
    let locationGrid;
    
    // 선택된 창고 코드 저장
    let selectedWarehouseCode = '';

    // API URL 상수 정의
    const API_URLS = {
        // 창고 기준정보 API
        WAREHOUSE: {
            LIST: '/api/warehouse/list',          // 데이터 목록 조회
            SINGLE: '/api/warehouse/',            // 단건 조회/삭제 시 사용
            SAVE: '/api/warehouse',               // 데이터 저장
            BATCH: '/api/warehouse/batch',        // 데이터 일괄 저장
            EXCEL: {
                UPLOAD: '/api/warehouse/excel/upload',    // 엑셀 업로드 API URL
                DOWNLOAD: '/api/warehouse/excel/download' // 엑셀 다운로드 API URL
            }
        },
        // 위치정보 API
        LOCATION: {
            LIST: '/api/warehouse/locations/',     // 위치 목록 조회 (/{whsCode} 추가됨)
            SAVE: '/api/warehouse/location',       // 위치 저장
            BATCH: '/api/warehouse/location/batch', // 위치 일괄 저장
            DELETE: '/api/warehouse/location/'     // 위치 삭제 (/{locCode} 추가됨)
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
            console.log('창고 기준정보 관리 초기화를 시작합니다.');

            // 그리드 초기화
            await initWarehouseGrid();
            await initLocationGrid();

            // 이벤트 리스너 등록
            await registerEvents();
            
            // 그리드 검색 초기화
            initGridSearch();
            
            // 엑셀 기능 초기화
            initExcelFeatures();
            
            // 초기에는 위치정보 비활성화
            toggleLocationControls(false);

            console.log('창고 기준정보 관리 초기화가 완료되었습니다.');
        } catch (error) {
            console.error('초기화 중 오류 발생:', error);
            await AlertUtil.showError('초기화 오류', '창고 기준정보 관리 초기화 중 오류가 발생했습니다.');
        }
    }

    /**
     * 이벤트 리스너 등록 함수
     * 버튼 클릭 및 엔터키 검색 등의 이벤트 리스너를 등록합니다.
     */
    async function registerEvents() {
        try {
            // 창고 그리드 관련 이벤트 리스너 등록
            await UIUtil.registerEventListeners({
                'warehouseAppendBtn': appendWarehouseRow,        // 창고 행 추가 버튼
                'warehouseSaveBtn': saveWarehouseData,           // 창고 데이터 저장 버튼
                'warehouseDeleteBtn': deleteWarehouseRows,       // 창고 데이터 삭제 버튼
                'warehouseSearchBtn': searchWarehouseData        // 창고 데이터 검색 버튼
            });

            // 위치 그리드 관련 이벤트 리스너 등록
            await UIUtil.registerEventListeners({
                'locationAppendBtn': appendLocationRow,         // 위치 행 추가 버튼
                'locationSaveBtn': saveLocationData,            // 위치 데이터 저장 버튼
                'locationDeleteBtn': deleteLocationRows,        // 위치 데이터 삭제 버튼
                'locationSearchBtn': searchLocationData         // 위치 데이터 검색 버튼
            });

            // 엔터키 검색 이벤트 등록
            await UIUtil.bindEnterKeySearch('warehouseInput', searchWarehouseData);
            await UIUtil.bindEnterKeySearch('locationInput', searchLocationData);
            
            // 창고 그리드 행 선택 이벤트 등록
            warehouseGrid.on('click', onWarehouseRowClick);
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
            
            // 창고 그리드 검색 설정
            GridSearchUtil.setupGridSearch({
                gridId: 'warehouseGrid',
                searchInputId: 'warehouseInput',
                autoSearch: true, // 입력 시 자동 검색
                beforeSearch: function() {
                    console.log('창고 그리드 검색 시작');
                    return true;
                },
                afterSearch: function(filteredData) {
                    console.log('창고 그리드 검색 완료, 결과:', filteredData.length, '건');
                }
            });
            
            // 위치 그리드 검색 설정
            GridSearchUtil.setupGridSearch({
                gridId: 'locationGrid',
                searchInputId: 'locationInput',
                autoSearch: true, // 입력 시 자동 검색
                beforeSearch: function() {
                    console.log('위치 그리드 검색 시작');
                    return true;
                },
                afterSearch: function(filteredData) {
                    console.log('위치 그리드 검색 완료, 결과:', filteredData.length, '건');
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
            
            // 엑셀 다운로드 버튼 설정
            ExcelUtil.setupExcelDownloadButton({
                buttonId: 'warehouseExcelDownBtn', 
                gridId: 'warehouseGrid',
                fileName: 'warehouse-info.xlsx',
                sheetName: '창고기준정보',
                beforeDownload: function() {
                    console.log('엑셀 다운로드 시작');
                    return true;
                },
                afterDownload: function() {
                    console.log('엑셀 다운로드 완료');
                }
            });
            
            // 엑셀 업로드 버튼 설정
            ExcelUtil.setupExcelUploadButton({
                fileInputId: 'warehouseFileInput', 
                uploadButtonId: 'warehouseExcelUpBtn', 
                gridId: 'warehouseGrid',
                apiUrl: API_URLS.WAREHOUSE.EXCEL.UPLOAD,
                headerMapping: {
                    '창고코드': 'WHS_CODE',
                    '창고명': 'WHS_NAME',
                    '창고유형': 'WHS_TYPE',
                    '창고위치': 'WHS_LOCATION',
                    '창고용량': 'WHS_CAPACITY',
                    '현재사용량': 'CURRENT_USAGE',
                    '사용여부': 'USE_YN',
                    '비고': 'WHS_COMMENTS',
                },
                beforeLoad: function() {
                    console.log('엑셀 업로드 시작');
                    return true;
                },
                afterLoad: function(data, saveResult) {
                    console.log('엑셀 업로드 완료, 결과:', data.length, '건, 저장:', saveResult);
                    if (saveResult) {
                        // 그리드 원본 데이터 업데이트
                        GridSearchUtil.updateOriginalData('warehouseGrid', data);
                    }
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
     * 창고 그리드 초기화 함수
     * 창고 그리드를 생성하고 초기 데이터를 로드합니다.
     */
    async function initWarehouseGrid() {
        try {
            console.log('창고 그리드 초기화를 시작합니다.');

            // DOM 요소 존재 확인
            const gridElement = document.getElementById('warehouseGrid');
            if (!gridElement) {
                throw new Error('warehouseGrid 요소를 찾을 수 없습니다. HTML을 확인해주세요.');
            }

            // 서버에서 받은 데이터 활용
            const gridData = window.warehouseList || [];
            console.log('초기 데이터:', gridData.length, '건');

            // 그리드 생성 - GridUtil 사용
            warehouseGrid = GridUtil.registerGrid({
                id: 'warehouseGrid',
                columns: [
                    {
                        header: '창고코드',
                        name: 'WHS_CODE',
                        editor: 'text',
                        sortable: true,
                        width: 120
                    },
                    {
                        header: '창고명',
                        name: 'WHS_NAME',
                        editor: 'text',
                        sortable: true,
                        width: 150
                    },
                    {
                        header: '창고유형',
                        name: 'WHS_TYPE',
                        editor: 'text',
                        sortable: true,
                        width: 120
                    },
                    {
                        header: '창고위치',
                        name: 'WHS_LOCATION',
                        editor: 'text',
                        sortable: true,
                        width: 200
                    },
                    {
                        header: '창고용량',
                        name: 'WHS_CAPACITY',
                        editor: 'text',
                        sortable: true,
                        width: 100,
                        align: 'right'
                    },
                    {
                        header: '현재사용량',
                        name: 'CURRENT_USAGE',
                        editor: 'text',
                        sortable: true,
                        width: 100,
                        align: 'right'
                    },
                    {
                        header: '사용여부',
                        name: 'USE_YN',
                        editor: GridUtil.createYesNoEditor(),
                        sortable: true,
                        width: 80
                    },
                    {
                        header: '비고',
                        name: 'WHS_COMMENTS',
                        editor: 'text',
                        sortable: true,
                        width: 200
                    },
                    {
                        header: '타입',
                        name: 'ROW_TYPE',
                        hidden: true
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
                hiddenColumns: ['ROW_TYPE'],
                gridOptions: {
                    rowHeaders: ['rowNum', 'checkbox']
                },
                toggleRowCheckedOnClick: true // 행 클릭 시 체크박스 토글 기능 활성화
            });
            
            // 편집 완료 이벤트 처리 - 변경된 행 추적
            warehouseGrid.on('editingFinish', function(ev) {
                const rowKey = ev.rowKey;
                const row = warehouseGrid.getRow(rowKey);
                
                // 원래 값과 변경된 값이 다른 경우에만 ROW_TYPE 업데이트
                if (row.ROW_TYPE !== 'insert' && ev.value !== ev.prevValue) {
                    warehouseGrid.setValue(rowKey, 'ROW_TYPE', 'update');
                    console.log(`행 ${rowKey}의 ROW_TYPE을 'update'로 변경했습니다.`);
                }
            });

            // 키 컬럼 제어 설정 - 기존 데이터의 경우 WHS_CODE 편집 제한
            GridUtil.setupKeyColumnControl('warehouseGrid', 'WHS_CODE');
            
            // 그리드 원본 데이터 저장 (검색 기능 위해 추가)
            GridSearchUtil.updateOriginalData('warehouseGrid', gridData);

            console.log('창고 그리드 초기화가 완료되었습니다.');
        } catch (error) {
            console.error('창고 그리드 초기화 오류:', error);
            throw error;
        }
    }

    /**
     * 위치정보 그리드 초기화 함수
     * 위치정보 그리드를 생성합니다.
     */
    async function initLocationGrid() {
        try {
            console.log('위치정보 그리드 초기화를 시작합니다.');

            // DOM 요소 존재 확인
            const gridElement = document.getElementById('locationGrid');
            if (!gridElement) {
                throw new Error('locationGrid 요소를 찾을 수 없습니다. HTML을 확인해주세요.');
            }

            // 초기에는 빈 데이터로 시작
            const gridData = [];

            // 그리드 생성 - GridUtil 사용
            locationGrid = GridUtil.registerGrid({
                id: 'locationGrid',
                columns: [
                    {
                        header: '위치코드',
                        name: 'LOC_CODE',
                        editor: 'text',
                        sortable: true,
                        width: 120
                    },
                    {
                        header: '위치명',
                        name: 'LOC_NAME',
                        editor: 'text',
                        sortable: true,
                        width: 150
                    },
                    {
                        header: '위치유형',
                        name: 'LOC_TYPE',
                        editor: 'text',
                        sortable: true,
                        width: 120
                    },
                    {
                        header: '창고코드',
                        name: 'WHS_CODE',
                        editor: {
                            type: 'text',
                            disabled: true
                        },
                        sortable: true,
                        width: 120
                    },
                    {
                        header: '수용용량',
                        name: 'CAPACITY',
                        editor: 'text',
                        sortable: true,
                        width: 100,
                        align: 'right'
                    },
                    {
                        header: '현재사용량',
                        name: 'CURRENT_USAGE',
                        editor: 'text',
                        sortable: true,
                        width: 100,
                        align: 'right'
                    },
                    {
                        header: '사용여부',
                        name: 'USE_YN',
                        editor: GridUtil.createYesNoEditor(),
                        sortable: true,
                        width: 80
                    },
                    {
                        header: '타입',
                        name: 'ROW_TYPE',
                        hidden: true
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
                hiddenColumns: ['ROW_TYPE'],
                gridOptions: {
                    rowHeaders: ['rowNum', 'checkbox']
                },
                toggleRowCheckedOnClick: true // 행 클릭 시 체크박스 토글 기능 활성화
            });
            
            // 편집 완료 이벤트 처리 - 변경된 행 추적
            locationGrid.on('editingFinish', function(ev) {
                const rowKey = ev.rowKey;
                const row = locationGrid.getRow(rowKey);
                
                // 원래 값과 변경된 값이 다른 경우에만 ROW_TYPE 업데이트
                if (row.ROW_TYPE !== 'insert' && ev.value !== ev.prevValue) {
                    locationGrid.setValue(rowKey, 'ROW_TYPE', 'update');
                    console.log(`위치 행 ${rowKey}의 ROW_TYPE을 'update'로 변경했습니다.`);
                }
            });

            // 키 컬럼 제어 설정 - 기존 데이터의 경우 LOC_CODE 편집 제한
            GridUtil.setupKeyColumnControl('locationGrid', 'LOC_CODE');

            console.log('위치정보 그리드 초기화가 완료되었습니다.');
        } catch (error) {
            console.error('위치정보 그리드 초기화 오류:', error);
            throw error;
        }
    }
    
    /**
     * 창고 그리드 행 클릭 이벤트 핸들러
     * 클릭한 창고의 위치정보를 조회하여 위치정보 그리드에 표시
     */
    async function onWarehouseRowClick(ev) {
        try {
            // 헤더 클릭인 경우 무시
            if (ev.rowKey === null || ev.rowKey === undefined) return;
            
            const grid = GridUtil.getGrid('warehouseGrid');
            const row = grid.getRow(ev.rowKey);
            
            // 창고 코드 가져오기
            const whsCode = row.WHS_CODE;
            if (!whsCode) {
                console.warn('선택한 행에 창고 코드가 없습니다:', row);
                return;
            }
            
            // 이전에 선택한 창고와 같은 경우 중복 조회 방지
            if (selectedWarehouseCode === whsCode) return;
            
            selectedWarehouseCode = whsCode;
            
            // 선택된 창고 정보 표시
            document.getElementById('selectedWarehouseInfo').textContent = 
                `선택된 창고: ${row.WHS_NAME} (${whsCode})`;
            
            // 위치정보 컨트롤 활성화
            toggleLocationControls(true);
            
            // 위치정보 로드
            await loadLocationData(whsCode);
            
        } catch (error) {
            console.error('창고 행 클릭 처리 중 오류:', error);
            AlertUtil.showError('오류', '위치정보 로드 중 오류가 발생했습니다.');
        }
    }
    
    /**
     * 위치정보 컨트롤 활성화/비활성화 함수
     * @param {boolean} enabled 활성화 여부
     */
    function toggleLocationControls(enabled) {
        const controls = [
            document.getElementById('locationInput'),
            document.getElementById('locationSearchBtn'),
            document.getElementById('locationAppendBtn'),
            document.getElementById('locationSaveBtn'),
            document.getElementById('locationDeleteBtn')
        ];
        
        controls.forEach(control => {
            if (control) {
                control.disabled = !enabled;
            }
        });
        
        // 위치정보 섹션 스타일 조정
        const locationSection = document.getElementById('location-table');
        if (locationSection) {
            locationSection.style.opacity = enabled ? '1' : '0.6';
        }
        
        // 선택된 창고 정보 초기화 (비활성화 시)
        if (!enabled) {
            document.getElementById('selectedWarehouseInfo').textContent = '';
        }
    }

    /**
     * 위치정보 데이터 로드 함수
     * @param {string} whsCode 창고 코드
     */
    async function loadLocationData(whsCode) {
        try {
            console.log(`창고코드 ${whsCode}에 대한 위치정보 로드`);
            
            if (!whsCode) {
                locationGrid.resetData([]);
                return;
            }
            
            // 위치정보 API 호출
            const response = await ApiUtil.getWithLoading(
                API_URLS.LOCATION.LIST + whsCode, 
                {},
                '위치정보 로드 중...'
            );
            
            // 응답 데이터 처리
            const data = Array.isArray(response) ? response : (response.data || []);
            
            // 위치정보 그리드 데이터 설정
            locationGrid.resetData(data);
            
            // 그리드 원본 데이터 업데이트 (검색 기능 위해 추가)
            GridSearchUtil.updateOriginalData('locationGrid', data);
            
            console.log('위치정보 로드 완료. 결과:', data.length, '건');
            
        } catch (error) {
            console.error('위치정보 로드 중 오류:', error);
            locationGrid.resetData([]);
            AlertUtil.showError('오류', '위치정보 로드 중 오류가 발생했습니다.');
        }
    }

    // =============================
    // 데이터 처리 함수
    // =============================

    /**
     * 창고 데이터 행 추가 함수
     */
    async function appendWarehouseRow() {
        try {
            console.log('창고 행 추가');

            const newRowData = {
                WHS_CODE: '',
                WHS_NAME: '',
                WHS_TYPE: '',
                WHS_LOCATION: '',
                WHS_CAPACITY: '',
                CURRENT_USAGE: '',
                USE_YN: 'Y',
                WHS_COMMENTS: '',
                CREATED_BY: '',
                UPDATED_BY: ''
                // ROW_TYPE은 GridUtil.addNewRow()에서 자동으로 추가됨
            };

            // 그리드에 새 행 추가
            await GridUtil.addNewRow('warehouseGrid', newRowData, {
                at: 0,
                focus: true
            });
        } catch (error) {
            console.error('창고 행 추가 중 오류:', error);
            await AlertUtil.showError('행 추가 오류', '창고 행 추가 중 오류가 발생했습니다.');
        }
    }
    
    /**
     * 위치정보 데이터 행 추가 함수
     */
    async function appendLocationRow() {
        try {
            console.log('위치정보 행 추가');
            
            // 창고가 선택되어 있는지 확인
            if (!selectedWarehouseCode) {
                await AlertUtil.showWarning('알림', '위치정보를 추가할 창고를 먼저 선택해주세요.');
                return;
            }

            const newRowData = {
                LOC_CODE: '',
                LOC_NAME: '',
                LOC_TYPE: '',
                WHS_CODE: selectedWarehouseCode, // 선택된 창고 코드 자동 설정
                CAPACITY: '',
                CURRENT_USAGE: '',
                USE_YN: 'Y',
                CREATED_BY: '',
                UPDATED_BY: ''
            };

            // 그리드에 새 행 추가
            await GridUtil.addNewRow('locationGrid', newRowData, {
                at: 0,
                focus: true
            });
        } catch (error) {
            console.error('위치정보 행 추가 중 오류:', error);
            await AlertUtil.showError('행 추가 오류', '위치정보 행 추가 중 오류가 발생했습니다.');
        }
    }

    /**
     * 창고 데이터 검색 함수
     */
    async function searchWarehouseData() {
        try {
            const keyword = document.getElementById('warehouseInput').value;
            console.log('창고 데이터 검색 시작. 검색어:', keyword);

            // API 호출
            const response = await ApiUtil.getWithLoading(
                API_URLS.WAREHOUSE.LIST, 
                {
                    keyword: keyword
                },
                '창고 데이터 검색 중...'
            );

            // 응답 데이터 처리
            const data = Array.isArray(response) ? response : (response.data || []);

            // 그리드 데이터 설정
            warehouseGrid.resetData(data);
            
            // 그리드 원본 데이터 업데이트 (검색 기능 위해 추가)
            GridSearchUtil.updateOriginalData('warehouseGrid', data);
            
            // 창고 변경 시 위치정보 초기화
            locationGrid.resetData([]);
            selectedWarehouseCode = '';
            document.getElementById('selectedWarehouseInfo').textContent = '';
            
            // 위치정보 컨트롤 비활성화
            toggleLocationControls(false);

            console.log('창고 데이터 검색 완료. 결과:', data.length, '건');
            return data;
        } catch (error) {
            console.error('창고 데이터 검색 중 오류:', error);
            await AlertUtil.showError('검색 오류', '창고 데이터 검색 중 오류가 발생했습니다.');
            throw error;
        }
    }
    
    /**
     * 위치정보 데이터 검색 함수
     */
    async function searchLocationData() {
        try {
            const keyword = document.getElementById('locationInput').value;
            console.log('위치정보 검색 시작. 검색어:', keyword);
            
            // 창고가 선택되어 있는지 확인
            if (!selectedWarehouseCode) {
                await AlertUtil.showWarning('알림', '위치정보를 검색할 창고를 먼저 선택해주세요.');
                return;
            }

            // API 호출
            const response = await ApiUtil.getWithLoading(
                API_URLS.LOCATION.LIST + selectedWarehouseCode, 
                {
                    keyword: keyword
                },
                '위치정보 검색 중...'
            );

            // 응답 데이터 처리
            const data = Array.isArray(response) ? response : (response.data || []);

            // 그리드 데이터 설정
            locationGrid.resetData(data);
            
            // 그리드 원본 데이터 업데이트 (검색 기능 위해 추가)
            GridSearchUtil.updateOriginalData('locationGrid', data);

            console.log('위치정보 검색 완료. 결과:', data.length, '건');
            return data;
        } catch (error) {
            console.error('위치정보 검색 중 오류:', error);
            await AlertUtil.showError('검색 오류', '위치정보 검색 중 오류가 발생했습니다.');
            throw error;
        }
    }

    /**
     * 창고 데이터 저장 함수
     */
    async function saveWarehouseData() {
        try {
            console.log('창고 데이터 저장 시작');

            const grid = GridUtil.getGrid('warehouseGrid');
            if (!grid) {
                throw new Error('warehouseGrid를 찾을 수 없습니다.');
            }

            // 마지막으로 입력한 셀에 대한 값 반영을 위해 포커스 해제
            grid.blur();

            // 변경된 데이터 추출
            const changes = await GridUtil.extractChangedData('warehouseGrid');
            const modifiedData = [...changes.insert, ...changes.update];

            if (modifiedData.length === 0) {
                await AlertUtil.showWarning("알림", "수정된 내용이 없습니다.");
                return false;
            }

            // 유효성 검사
            for (const item of modifiedData) {
                if (ValidationUtil.isEmpty(item.WHS_CODE)) {
                    await AlertUtil.notifyValidationError("유효성 오류", "창고코드는 필수입니다.");
                    return false;
                }
                if (ValidationUtil.isEmpty(item.WHS_NAME)) {
                    await AlertUtil.notifyValidationError("유효성 오류", "창고명은 필수입니다.");
                    return false;
                }
                if (ValidationUtil.isEmpty(item.USE_YN)) {
                    await AlertUtil.notifyValidationError("유효성 오류", "사용여부는 필수입니다.");
                    return false;
                }
            }

            // 저장할 데이터 준비 - 단건 또는 배치 방식 선택
            if (modifiedData.length === 1) {
                // 단건 저장
                const saveData = modifiedData[0];
                
                // API 호출
                const response = await ApiUtil.processRequest(
                    () => ApiUtil.post(API_URLS.WAREHOUSE.SAVE, saveData), 
                    {
                        loadingMessage: '창고 데이터 저장 중...',
                        successMessage: "창고 데이터가 저장되었습니다.",
                        errorMessage: "창고 데이터 저장 중 오류가 발생했습니다.",
                        successCallback: searchWarehouseData
                    }
                );
                
                if(response.success){
                    await AlertUtil.showSuccess('저장 완료', '창고 데이터가 성공적으로 저장되었습니다.');
                    return true;
                } else {
                    return false;
                }
            } else {
                // 일괄 저장
                const response = await ApiUtil.processRequest(
                    () => ApiUtil.post(API_URLS.WAREHOUSE.BATCH, modifiedData), 
                    {
                        loadingMessage: '창고 데이터 일괄 저장 중...',
                        successMessage: "창고 데이터가 일괄 저장되었습니다.",
                        errorMessage: "창고 데이터 일괄 저장 중 오류가 발생했습니다.",
                        successCallback: searchWarehouseData
                    }
                );
                
                if(response.success){
                    await AlertUtil.showSuccess('저장 완료', '창고 데이터가 성공적으로 저장되었습니다.');
                    return true;
                } else {
                    return false;
                }
            }
            
        } catch (error) {
            console.error('창고 데이터 저장 오류:', error);
            await AlertUtil.notifySaveError("저장 실패", "창고 데이터 저장 중 오류가 발생했습니다.");
            return false;
        }
    }
    
    /**
     * 위치정보 데이터 저장 함수
     */
    async function saveLocationData() {
        try {
            console.log('위치정보 저장 시작');
            
            // 창고가 선택되어 있는지 확인
            if (!selectedWarehouseCode) {
                await AlertUtil.showWarning('알림', '위치정보를 저장할 창고를 먼저 선택해주세요.');
                return false;
            }

            const grid = GridUtil.getGrid('locationGrid');
            if (!grid) {
                throw new Error('locationGrid를 찾을 수 없습니다.');
            }

            // 마지막으로 입력한 셀에 대한 값 반영을 위해 포커스 해제
            grid.blur();

            // 변경된 데이터 추출
            const changes = await GridUtil.extractChangedData('locationGrid');
            const modifiedData = [...changes.insert, ...changes.update];

            if (modifiedData.length === 0) {
                await AlertUtil.showWarning("알림", "수정된 내용이 없습니다.");
                return false;
            }

            // 유효성 검사
            for (const item of modifiedData) {
                if (ValidationUtil.isEmpty(item.LOC_CODE)) {
                    await AlertUtil.notifyValidationError("유효성 오류", "위치코드는 필수입니다.");
                    return false;
                }
                if (ValidationUtil.isEmpty(item.LOC_NAME)) {
                    await AlertUtil.notifyValidationError("유효성 오류", "위치명은 필수입니다.");
                    return false;
                }
                if (ValidationUtil.isEmpty(item.WHS_CODE)) {
                    // 창고 코드 자동 설정
                    item.WHS_CODE = selectedWarehouseCode;
                }
                if (ValidationUtil.isEmpty(item.USE_YN)) {
                    await AlertUtil.notifyValidationError("유효성 오류", "사용여부는 필수입니다.");
                    return false;
                }
            }

            // 저장할 데이터 준비 - 단건 또는 배치 방식 선택
            if (modifiedData.length === 1) {
                // 단건 저장
                const saveData = modifiedData[0];
                
                // API 호출
                const response = await ApiUtil.processRequest(
                    () => ApiUtil.post(API_URLS.LOCATION.SAVE, saveData), 
                    {
                        loadingMessage: '위치정보 저장 중...',
                        successMessage: "위치정보가 저장되었습니다.",
                        errorMessage: "위치정보 저장 중 오류가 발생했습니다.",
                        successCallback: () => loadLocationData(selectedWarehouseCode)
                    }
                );
                
                if(response.success){
                    await AlertUtil.showSuccess('저장 완료', '위치정보가 성공적으로 저장되었습니다.');
                    return true;
                } else {
                    return false;
                }
            } else {
                // 일괄 저장
                const response = await ApiUtil.processRequest(
                    () => ApiUtil.post(API_URLS.LOCATION.BATCH, modifiedData), 
                    {
                        loadingMessage: '위치정보 일괄 저장 중...',
                        successMessage: "위치정보가 일괄 저장되었습니다.",
                        errorMessage: "위치정보 일괄 저장 중 오류가 발생했습니다.",
                        successCallback: () => loadLocationData(selectedWarehouseCode)
                    }
                );
                
                if(response.success){
                    await AlertUtil.showSuccess('저장 완료', '위치정보가 성공적으로 저장되었습니다.');
                    return true;
                } else {
                    return false;
                }
            }
            
        } catch (error) {
            console.error('위치정보 저장 오류:', error);
            await AlertUtil.notifySaveError("저장 실패", "위치정보 저장 중 오류가 발생했습니다.");
            return false;
        }
    }

    /**
     * 창고 데이터 삭제 함수
     */
    async function deleteWarehouseRows() {
        try {
            // 선택된 행 ID 확인
            const grid = GridUtil.getGrid('warehouseGrid');
            const selectedRowKeys = grid.getCheckedRowKeys();
            
            if (selectedRowKeys.length === 0) {
                await AlertUtil.showWarning('알림', '삭제할 창고를 선택해주세요.');
                return false;
            }
            
            // 선택된 코드 목록 생성
            const selectedCodes = [];
            for (const rowKey of selectedRowKeys) {
                const whsCode = grid.getValue(rowKey, "WHS_CODE");
                if (whsCode) selectedCodes.push(whsCode);
            }
            
            if (selectedCodes.length === 0) {
                await AlertUtil.showWarning('알림', '유효한 창고코드를 찾을 수 없습니다.');
                return false;
            }
            
            // GridUtil.deleteSelectedRows 사용 (UI 측면의 삭제 확인 및 행 제거)
            const result = await GridUtil.deleteSelectedRows('warehouseGrid', {
                confirmTitle: "삭제 확인",
                confirmMessage: "선택한 창고 정보를 삭제하시겠습니까?",
                onBeforeDelete: async () => {
                    // 삭제 전 처리 - 여기서 true 반환해야 삭제 진행
                    return true;
                },
                onAfterDelete: async () => {
                    // 삭제 API 호출 및 처리
                    try {
                        // 삭제 요청 생성
                        const deleteRequests = selectedCodes.map(whsCode => 
                            async () => ApiUtil.del(API_URLS.WAREHOUSE.SINGLE + whsCode)
                        );
                        
                        // 일괄 삭제 요청 실행
                        await ApiUtil.withLoading(async () => {
                            await Promise.all(deleteRequests.map(req => req()));
                        }, '창고 데이터 삭제 중...');
                        
                        // 삭제 성공 메시지
                        await AlertUtil.notifyDeleteSuccess('삭제 완료', '창고 정보가 삭제되었습니다.');
                        
                        // 목록 갱신
                        await searchWarehouseData();
                        
                        // 위치정보 초기화 (창고가 삭제되었으므로)
                        locationGrid.resetData([]);
                        selectedWarehouseCode = '';
                        document.getElementById('selectedWarehouseInfo').textContent = '';
                        
                        // 위치정보 컨트롤 비활성화
                        toggleLocationControls(false);
                    } catch (apiError) {
                        console.error('삭제 API 호출 중 오류:', apiError);
                        await AlertUtil.notifyDeleteError('삭제 실패', '창고 정보 삭제 중 API 오류가 발생했습니다.');
                    }
                }
            });
            
            return result;
        } catch (error) {
            console.error('창고 데이터 삭제 오류:', error);
            await AlertUtil.notifyDeleteError('삭제 실패', '창고 데이터 삭제 중 오류가 발생했습니다.');
            return false;
        }
    }
    
    /**
     * 위치정보 데이터 삭제 함수
     */
    async function deleteLocationRows() {
        try {
            // 창고가 선택되어 있는지 확인
            if (!selectedWarehouseCode) {
                await AlertUtil.showWarning('알림', '위치정보를 삭제할 창고를 먼저 선택해주세요.');
                return false;
            }
            
            // 선택된 행 ID 확인
            const grid = GridUtil.getGrid('locationGrid');
            const selectedRowKeys = grid.getCheckedRowKeys();
            
            if (selectedRowKeys.length === 0) {
                await AlertUtil.showWarning('알림', '삭제할 위치정보를 선택해주세요.');
                return false;
            }
            
            // 선택된 코드 목록 생성
            const selectedCodes = [];
            for (const rowKey of selectedRowKeys) {
                const locCode = grid.getValue(rowKey, "LOC_CODE");
                if (locCode) selectedCodes.push(locCode);
            }
            
            if (selectedCodes.length === 0) {
                await AlertUtil.showWarning('알림', '유효한 위치코드를 찾을 수 없습니다.');
                return false;
            }
            
            // GridUtil.deleteSelectedRows 사용 (UI 측면의 삭제 확인 및 행 제거)
            const result = await GridUtil.deleteSelectedRows('locationGrid', {
                confirmTitle: "삭제 확인",
                confirmMessage: "선택한 위치정보를 삭제하시겠습니까?",
                onBeforeDelete: async () => {
                    // 삭제 전 처리 - 여기서 true 반환해야 삭제 진행
                    return true;
                },
                onAfterDelete: async () => {
                    // 삭제 API 호출 및 처리
                    try {
                        // 삭제 요청 생성
                        const deleteRequests = selectedCodes.map(locCode => 
                            async () => ApiUtil.del(API_URLS.LOCATION.DELETE + locCode)
                        );
                        
                        // 일괄 삭제 요청 실행
                        await ApiUtil.withLoading(async () => {
                            await Promise.all(deleteRequests.map(req => req()));
                        }, '위치정보 삭제 중...');
                        
                        // 삭제 성공 메시지
                        await AlertUtil.notifyDeleteSuccess('삭제 완료', '위치정보가 삭제되었습니다.');
                        
                        // 목록 갱신
                        await loadLocationData(selectedWarehouseCode);
                    } catch (apiError) {
                        console.error('삭제 API 호출 중 오류:', apiError);
                        await AlertUtil.notifyDeleteError('삭제 실패', '위치정보 삭제 중 API 오류가 발생했습니다.');
                    }
                }
            });
            
            return result;
        } catch (error) {
            console.error('위치정보 삭제 오류:', error);
            await AlertUtil.notifyDeleteError('삭제 실패', '위치정보 삭제 중 오류가 발생했습니다.');
            return false;
        }
    }

    // 공개 API - 외부에서 접근 가능한 메서드
    return {
        // 초기화 및 기본 기능
        init,

        // 창고 관련 함수
        searchWarehouseData,
        appendWarehouseRow,
        saveWarehouseData,
        deleteWarehouseRows,

        // 위치정보 관련 함수
        loadLocationData,
        searchLocationData,
        appendLocationRow,
        saveLocationData,
        deleteLocationRows,

        // 유틸리티 함수
        getWarehouseGrid: () => warehouseGrid,
        getLocationGrid: () => locationGrid
    };
})();

// =============================
// DOM 로드 시 초기화
// =============================
document.addEventListener('DOMContentLoaded', async function() {
    try {
        // 창고 기준정보 관리 초기화
        await WarehouseManager.init();
    } catch (error) {
        console.error('초기화 중 오류 발생:', error);
        if (window.AlertUtil) {
            await AlertUtil.showError('초기화 오류', '창고 기준정보 관리 초기화 중 오류가 발생했습니다.');
        } else {
            alert('창고 기준정보 관리 초기화 중 오류가 발생했습니다.');
        }
    }
});