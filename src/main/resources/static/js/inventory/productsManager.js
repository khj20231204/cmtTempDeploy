/**
 * 제품 기준정보 관리 모듈
 * 
 * 제품 기준정보의 조회, 추가, 수정, 삭제 기능을 담당하는 관리 모듈입니다.
 * 
 * @version 1.1.0 (dropdown 기능 추가)
 * @since 2025-05-01
 */
const ProductsManager = (function() {
    // =============================
    // 모듈 내부 변수
    // =============================

    // 그리드 인스턴스 참조
    let productsGrid;

    // 현재 선택된 제품정보
    let selectedProducts = null;

    // API URL 상수 정의
    const API_URLS = {
        LIST: '/api/products-info/list', // 데이터 목록 조회
        SINGLE: '/api/products-info/', // 단건 조회/삭제 시 사용
        SAVE: '/api/products-info', // 데이터 저장
        BATCH: '/api/products-info/batch', // 데이터 일괄 저장
        WAREHOUSES: '/api/warehouse/list', // 창고 목록 조회
        LOCATIONS: (whsCode) => `/api/warehouse/locations/${whsCode}`, // 위치 목록 조회
        EXCEL: {
            UPLOAD: '/api/products-info/excel/upload', // 엑셀 업로드 API URL
            DOWNLOAD: '/api/products-info/excel/download' // 엑셀 다운로드 API URL
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
            console.log('제품 기준정보 관리 초기화를 시작합니다.');

            // 그리드 초기화
            await initGrid();

            // 창고 데이터 로드 (필요한 경우)
            try {
                await loadWarehouseData();
            } catch (err) {
                console.warn('창고 데이터 로드 실패:', err);
                // 계속 진행 - 이 기능이 없어도 기본 기능은 작동
            }

            // 이벤트 리스너 등록
            await registerEvents();

            // 그리드 검색 초기화
            initGridSearch();

            // 엑셀 기능 초기화
            initExcelFeatures();

            console.log('제품 기준정보 관리 초기화가 완료되었습니다.');
        } catch (error) {
            console.error('초기화 중 오류 발생:', error);
            await AlertUtil.showError('초기화 오류', '제품 기준정보 관리 초기화 중 오류가 발생했습니다.');
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
                'productsAppendBtn': appendRow, // 행 추가 버튼
                'productsSaveBtn': saveData, // 데이터 저장 버튼
                'productsDeleteBtn': deleteRows, // 데이터 삭제 버튼
                'productsSearchBtn': searchData // 데이터 검색 버튼
                // 엑셀 버튼 이벤트는 ExcelUtil에서 별도로 처리됩니다
            });

            // 엔터키 검색 이벤트 등록
            await UIUtil.bindEnterKeySearch('productsInput', searchData);

            // 파일 인풋 변경 이벤트 (있는 경우)
            const fileInput = document.getElementById('productsFileInput');
            if (fileInput) {
                fileInput.addEventListener('change', function() {
                    const fileName = this.files[0]?.name || '선택된 파일 없음';
                    const fileNameDisplay = document.getElementById('productsFileName');
                    if (fileNameDisplay) {
                        fileNameDisplay.textContent = fileName;
                    }
                });
            }
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
                gridId: 'productsGrid',
                searchInputId: 'productsInput',
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

            // 엑셀 다운로드 버튼 설정
            ExcelUtil.setupExcelDownloadButton({
                buttonId: 'productsExcelDownBtn',
                gridId: 'productsGrid',
                fileName: 'products-info.xlsx',
                sheetName: '제품기준정보',
                beforeDownload: function() {
                    console.log('엑셀 다운로드 시작');
                    return true;
                },
                afterDownload: function() {
                    console.log('엑셀 다운로드 완료');
                }
            });

            // 엑셀 업로드 버튼 설정
            const uploadButton = document.getElementById('productsExcelUpBtn');
            if (uploadButton) {
                ExcelUtil.setupExcelUploadButton({
                    fileInputId: 'productsFileInput',
                    uploadButtonId: 'productsExcelUpBtn',
                    gridId: 'productsGrid',
                    apiUrl: API_URLS.EXCEL.UPLOAD,
                    headerMapping: {
                        '제품코드': 'PDT_CODE',
                        '제품명': 'PDT_NAME',
                        '창고코드': 'DEFAULT_WAREHOUSE_CODE',
                        '위치코드': 'DEFAULT_LOCATION_CODE',
                        '비용': 'PDT_SHIPPING_PRICE',
                        '제품설명': 'PDT_COMMENTS',
                        '사용여부': 'PDT_USEYN',
                        '재질코드': 'MTL_TYPE_CODE',
                        '제품중량': 'PDT_WEIGHT',
                        '중량단위': 'WT_TYPE_CODE',
                        '제품크기': 'PDT_SIZE',
                        '리드타입코드': 'LT_TYPE_CODE',
                        '제품유형': 'PDT_TYPE',
                        '제품규격': 'PDT_SPECIFICATION',
                    },
                    beforeLoad: function() {
                        console.log('엑셀 업로드 시작');
                        return true;
                    },
                    afterLoad: function(data, saveResult) {
                        console.log('엑셀 업로드 완료, 결과:', data.length, '건, 저장:', saveResult);
                        if (saveResult) {
                            // 그리드 원본 데이터 업데이트
                            GridSearchUtil.updateOriginalData('productsGrid', data);
                        }
                    }
                });
            }
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
            const gridElement = document.getElementById('productsGrid');
            if (!gridElement) {
                throw new Error('productsGrid 요소를 찾을 수 없습니다. HTML을 확인해주세요.');
            }

            // 서버에서 받은 데이터 활용
            const gridData = window.productsList || [];
            console.log('초기 데이터:', gridData.length, '건');

            // 그리드 생성 - GridUtil 사용
            productsGrid = GridUtil.registerGrid({
                id: 'productsGrid',
                columns: [{
                        header: '제품코드',
                        name: 'PDT_CODE',
                        editor: 'text',
                        sortable: true,
                        width: 120
                    },
                    {
                        header: '제품명',
                        name: 'PDT_NAME',
                        editor: 'text',
                        sortable: true,
                        width: 150
                    },
                    {
                        header: '창고코드',
                        name: 'DEFAULT_WAREHOUSE_CODE',
                        editor: createWarehouseEditor(),
                        sortable: true,
                        width: 150
                    },
                    {
                        header: '위치 코드',
                        name: 'DEFAULT_LOCATION_CODE',
                        editor: createLocationEditor(),
                        sortable: true,
                        width: 150
                    },
                    //                    {
                    //                        header: '제품규격',
                    //                        name: 'PDT_SPECIFICATION',
                    //                        editor: 'text',
                    //                        sortable: true,
                    //                        width: 150
                    //                    },
                    {
                        header: '제품유형',
                        name: 'PDT_TYPE',
                        editor: 'text',
                        sortable: true,
                        width: 120
                    },
                    {
                        header: '제품중량',
                        name: 'PDT_WEIGHT',
                        editor: 'text',
                        sortable: true,
                        width: 100
                    },
                    {
                        header: '중량단위',
                        name: 'WT_TYPE_CODE',
                        editor: 'text',
                        sortable: true,
                        width: 80
                    },
                    {
                        header: '제품크기',
                        name: 'PDT_SIZE',
                        editor: 'text',
                        sortable: true,
                        width: 100
                    },
                    {
                        header: '리드타입코드',
                        name: 'LT_TYPE_CODE',
                        editor: 'text',
                        sortable: true,
                        width: 120
                    },
                    {
                        header: '재질코드',
                        name: 'MTL_TYPE_CODE',
                        editor: 'text',
                        sortable: true,
                        width: 120
                    },
                    {
                        header: '비용',
                        name: 'PDT_SHIPPING_PRICE',
                        editor: 'text',
                        sortable: true,
                        width: 100,
                        align: 'right'
                    },
                    {
                        header: '사용여부',
                        name: 'PDT_USEYN',
                        editor: GridUtil.createYesNoEditor(),
                        sortable: true,
                        width: 80
                    },
                    {
                        header: '제품설명',
                        name: 'PDT_COMMENTS',
                        editor: 'text',
                        sortable: true,
                        width: 150
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
            productsGrid.on('editingFinish', function(ev) {
                const rowKey = ev.rowKey;
                const row = productsGrid.getRow(rowKey);

                // 원래 값과 변경된 값이 다른 경우에만 ROW_TYPE 업데이트
                if (row.ROW_TYPE !== 'insert' && ev.value !== ev.prevValue) {
                    productsGrid.setValue(rowKey, 'ROW_TYPE', 'update');
                    console.log(`행 ${rowKey}의 ROW_TYPE을 'update'로 변경했습니다.`);
                }

                // 창고 코드가 변경된 경우 위치 코드 드롭다운 업데이트
                if (ev.columnName === 'DEFAULT_WAREHOUSE_CODE' && ev.value !== ev.prevValue) {
                    updateLocationDropdown(rowKey, ev.value);
                }
            });

            productsGrid.on('click', function(ev) {
                if (ev.columnName === 'DEFAULT_LOCATION_CODE') {
                    const rowKey = ev.rowKey;
                    const row = productsGrid.getRow(rowKey);
                    const warehouseCode = row.DEFAULT_WAREHOUSE_CODE;

                    // 창고코드가 있는 경우에만 위치 드롭다운 업데이트
                    if (warehouseCode) {
                        updateLocationDropdown(rowKey, warehouseCode);
                    }
                }
            });

            // 키 컬럼 제어 설정 - 기존 데이터의 경우 PDT_CODE 편집 제한
            GridUtil.setupKeyColumnControl('productsGrid', 'PDT_CODE');

            // 그리드 원본 데이터 저장 (검색 기능 위해 추가)
            GridSearchUtil.updateOriginalData('productsGrid', gridData);

            // 행 클릭 이벤트 등록
            GridUtil.onRowClick('productsGrid', function(rowData, rowKey, columnName) {
                if (!rowData) return;
                selectedProducts = rowData;
            });

            // 초기 데이터에 대해 위치 드롭다운 설정
            const rows = productsGrid.getData();
            rows.forEach((row) => {
                if (row.DEFAULT_WAREHOUSE_CODE) {
                    updateLocationDropdown(row.rowKey, row.DEFAULT_WAREHOUSE_CODE);
                }
            });

            console.log('그리드 초기화가 완료되었습니다.');
        } catch (error) {
            console.error('그리드 초기화 오류:', error);
            throw error;
        }
    }

    /**
     * 창고 선택 에디터 생성 함수
     * 창고 선택을 위한 셀렉트 에디터를 생성합니다.
     */
    function createWarehouseEditor() {
        return {
            type: 'select',
            options: {
                listItems: [{
                    text: '창고를 선택하세요',
                    value: ''
                }]
            }
        };
    }

    /**
     * 위치 선택 에디터 생성 함수
     * 위치 선택을 위한 셀렉트 에디터를 생성합니다.
     */
    function createLocationEditor() {
        return {
            type: 'select',
            options: {
                listItems: [{
                    text: '위치를 선택하세요',
                    value: ''
                }]
            }
        };
    }

    /**
     * 데이터 행 추가 함수
     * 그리드에 새로운 행을 추가합니다.
     */
    async function appendRow() {
        try {
            console.log('행 추가');

            const newRowData = {
                PDT_CODE: '',
                PDT_NAME: '',
                PDT_SHIPPING_PRICE: '',
                PDT_COMMENTS: '',
                PDT_USEYN: 'Y',
                MTL_TYPE_CODE: '',
                PDT_WEIGHT: '',
                WT_TYPE_CODE: '',
                PDT_SIZE: '',
                LT_TYPE_CODE: '',
                PDT_TYPE: '',
                PDT_SPECIFICATION: '',
                DEFAULT_WAREHOUSE_CODE: '',
                DEFAULT_LOCATION_CODE: ''
                // ROW_TYPE은 GridUtil.addNewRow()에서 자동으로 추가됨
            };

            // 그리드에 새 행 추가
            await GridUtil.addNewRow('productsGrid', newRowData, {
                at: 0,
                focus: true
            });
        } catch (error) {
            console.error('행 추가 중 오류:', error);
            await AlertUtil.showError('행 추가 오류', '행 추가 중 오류가 발생했습니다.');
        }
    }

    /**
     * 데이터 검색 함수
     * 검색어를 이용하여 데이터를 검색하고 그리드에 결과를 표시합니다.
     */
    async function searchData() {
        try {
            const keyword = document.getElementById('productsInput').value;
            console.log('데이터 검색 시작. 검색어:', keyword);

            // API 호출
            const response = await ApiUtil.getWithLoading(
                API_URLS.LIST, {
                    keyword: keyword
                },
                '데이터 검색 중...'
            );

            // 응답 데이터 처리
            const data = Array.isArray(response) ? response : (response.data || []);

            // 그리드 데이터 설정
            const grid = GridUtil.getGrid('productsGrid');
            if (grid) {
                grid.resetData(data);

                // 그리드 원본 데이터 업데이트 (검색 기능 위해 추가)
                GridSearchUtil.updateOriginalData('productsGrid', data);
            }

            console.log('데이터 검색 완료. 결과:', data.length, '건');
            return data;
        } catch (error) {
            console.error('데이터 검색 중 오류:', error);
            await AlertUtil.showError('검색 오류', '데이터 검색 중 오류가 발생했습니다.');
            throw error;
        }
    }

    // =============================
    // CRUD 처리 함수
    // =============================

    /**
     * 데이터 저장 함수
     * 그리드의 변경된 데이터를 저장합니다.
     */
    async function saveData() {
        try {
            console.log('데이터 저장 시작');

            const grid = GridUtil.getGrid('productsGrid');
            if (!grid) {
                throw new Error('productsGrid를 찾을 수 없습니다.');
            }

            // 마지막으로 입력한 셀에 대한 값 반영을 위해 포커스 해제
            grid.blur();

            // 변경된 데이터 추출
            const changes = await GridUtil.extractChangedData('productsGrid');
            const modifiedData = [...changes.insert, ...changes.update];

            if (modifiedData.length === 0) {
                await AlertUtil.showWarning("알림", "수정된 내용이 없습니다.");
                return false;
            }

            // 유효성 검사
            for (const item of modifiedData) {
                if (ValidationUtil.isEmpty(item.PDT_CODE)) {
                    await AlertUtil.notifyValidationError("유효성 오류", "제품코드는 필수입니다.");
                    return false;
                }
                if (ValidationUtil.isEmpty(item.PDT_NAME)) {
                    await AlertUtil.notifyValidationError("유효성 오류", "제품명은 필수입니다.");
                    return false;
                }
                if (ValidationUtil.isEmpty(item.PDT_USEYN)) {
                    item.PDT_USEYN = 'Y'; // 기본값 설정
                }
            }

            // 저장할 데이터 준비 - 단건 또는 배치 방식 선택
            if (modifiedData.length === 1) {
                // 단건 저장
                const saveData = modifiedData[0];

                // API 호출
                const response = await ApiUtil.processRequest(
                    () => ApiUtil.post(API_URLS.SAVE, saveData), {
                        loadingMessage: '데이터 저장 중...',
                        successMessage: "데이터가 저장되었습니다.",
                        errorMessage: "데이터 저장 중 오류가 발생했습니다.",
                        successCallback: searchData
                    }
                );

                if (response.success) {
                    await AlertUtil.showSuccess('저장 완료', '데이터가 성공적으로 저장되었습니다.');
                    return true;
                } else {
                    return false;
                }
            } else {
                // 일괄 저장
                const response = await ApiUtil.processRequest(
                    () => ApiUtil.post(API_URLS.BATCH, modifiedData), {
                        loadingMessage: '데이터 일괄 저장 중...',
                        successMessage: "데이터가 일괄 저장되었습니다.",
                        errorMessage: "데이터 일괄 저장 중 오류가 발생했습니다.",
                        successCallback: searchData
                    }
                );

                if (response.success) {
                    await AlertUtil.showSuccess('저장 완료', '데이터가 성공적으로 저장되었습니다.');
                    return true;
                } else {
                    return false;
                }
            }

        } catch (error) {
            console.error('데이터 저장 오류:', error);
            await AlertUtil.notifySaveError("저장 실패", "데이터 저장 중 오류가 발생했습니다.");
            return false;
        }
    }

    /**
     * 데이터 삭제 함수
     * 선택된 행을 삭제합니다.
     */
    async function deleteRows() {
        try {
            // 선택된 행 ID 확인
            const grid = GridUtil.getGrid('productsGrid');
            const selectedRowKeys = grid.getCheckedRowKeys();

            if (selectedRowKeys.length === 0) {
                await AlertUtil.showWarning('알림', '삭제할 항목을 선택해주세요.');
                return false;
            }

            // 선택된 코드 목록 생성
            const selectedCodes = [];
            for (const rowKey of selectedRowKeys) {
                const pdtCode = grid.getValue(rowKey, "PDT_CODE");
                if (pdtCode) selectedCodes.push(pdtCode);
            }

            if (selectedCodes.length === 0) {
                await AlertUtil.showWarning('알림', '유효한 제품코드를 찾을 수 없습니다.');
                return false;
            }

            // GridUtil.deleteSelectedRows 사용 (UI 측면의 삭제 확인 및 행 제거)
            const result = await GridUtil.deleteSelectedRows('productsGrid', {
                confirmTitle: "삭제 확인",
                confirmMessage: "선택한 제품 정보를 삭제하시겠습니까?",
                onBeforeDelete: async () => {
                    // 삭제 전 처리 - 여기서 true 반환해야 삭제 진행
                    return true;
                },
                onAfterDelete: async () => {
                    // 삭제 API 호출 및 처리
                    try {
                        // 삭제 요청 생성
                        const deleteRequests = selectedCodes.map(pdtCode =>
                            async () => ApiUtil.del(API_URLS.SINGLE + pdtCode)
                        );

                        // 일괄 삭제 요청 실행
                        await ApiUtil.withLoading(async () => {
                            await Promise.all(deleteRequests.map(req => req()));
                        }, '데이터 삭제 중...');

                        // 삭제 성공 메시지
                        await AlertUtil.notifyDeleteSuccess('삭제 완료', '제품 정보가 삭제되었습니다.');

                        // 목록 갱신
                        await searchData();
                    } catch (apiError) {
                        console.error('삭제 API 호출 중 오류:', apiError);
                        await AlertUtil.notifyDeleteError('삭제 실패', '제품 정보 삭제 중 API 오류가 발생했습니다.');
                    }
                }
            });

            return result;
        } catch (error) {
            console.error('데이터 삭제 오류:', error);
            await AlertUtil.notifyDeleteError('삭제 실패', '데이터 삭제 중 오류가 발생했습니다.');
            return false;
        }
    }

    // =============================
    // 드롭다운 관련 함수 (추가된 기능)
    // =============================

    /**
     * 창고 데이터 로드 및 드롭다운 업데이트
     */
    async function loadWarehouseData() {
        try {
            await UIUtil.toggleLoading(true, '창고 정보를 불러오는 중...');

            const response = await ApiUtil.get(API_URLS.WAREHOUSES);

            await UIUtil.toggleLoading(false);

            if (!response || !response.success) {
                throw new Error('창고 정보를 가져오는데 실패했습니다.');
            }

            const warehouses = response.data || [];

            // 드롭다운 아이템 포맷팅
            const items = warehouses.map(warehouse => ({
                value: warehouse.WHS_CODE,
                text: `${warehouse.WHS_CODE} - ${warehouse.WHS_NAME}`
            }));

            // 빈 옵션 추가
            items.unshift({
                value: '',
                text: '선택하세요'
            });

            // 그리드 칼럼 에디터 옵션 업데이트
            const grid = GridUtil.getGrid('productsGrid');
            if (!grid) return false;

            const column = grid.getColumn('DEFAULT_WAREHOUSE_CODE');
            if (column && column.editor && column.editor.options) {
                column.editor.options.listItems = items;
            }

            return true;
        } catch (error) {
            console.error('창고 데이터 로드 오류:', error);
            await UIUtil.toggleLoading(false);
            return false;
        }
    }

    /**
     * 위치 데이터 로드 및 드롭다운 업데이트
     * 
     * @param {number} rowKey - 행 키
     * @param {string} warehouseCode - 창고 코드 
     */
    async function updateLocationDropdown(rowKey, warehouseCode) {
        try {
            const grid = GridUtil.getGrid('productsGrid');
            if (!grid) return false;

            if (!warehouseCode) {
                // 창고 코드가 없으면 위치 드롭다운 비우기
                // 위치 코드 값도 초기화
                grid.setValue(rowKey, 'DEFAULT_LOCATION_CODE', '');

                // 위치 드롭다운 옵션 초기화
                const column = grid.getColumn('DEFAULT_LOCATION_CODE');
                if (column && column.editor && column.editor.options) {
                    column.editor.options.listItems = [{
                        text: '위치를 선택하세요',
                        value: ''
                    }];
                }
                return false;
            }

            await UIUtil.toggleLoading(true, '위치 정보를 불러오는 중...');

            const response = await ApiUtil.get(API_URLS.LOCATIONS(warehouseCode));

            await UIUtil.toggleLoading(false);

            if (!response || !response.success) {
                throw new Error('위치 정보를 가져오는데 실패했습니다.');
            }

            const locations = response.data || [];

            // 위치 정보가 없으면 종료
            if (!locations || locations.length === 0) {
                console.log('위치 정보가 없습니다:', warehouseCode);
                return false;
            }

            // 드롭다운 아이템 포맷팅
            const items = locations.map(location => ({
                value: location.LOC_CODE,
                text: `${location.LOC_CODE} - ${location.LOC_NAME}`
            }));

            // 빈 옵션 추가
            items.unshift({
                value: '',
                text: '위치를 선택하세요'
            });

            // 위치 드롭다운 업데이트
            const column = grid.getColumn('DEFAULT_LOCATION_CODE');
            if (column && column.editor && column.editor.options) {
                column.editor.options.listItems = items;
            }

            // 위치 코드 값 설정 (첫 번째 위치 선택 - 원자재와 다른 점: 자동선택 안함)
            // grid.setValue(rowKey, 'DEFAULT_LOCATION_CODE', locations[0].LOC_CODE);

            return true;
        } catch (error) {
            console.error('위치 데이터 로드 오류:', error);
            await UIUtil.toggleLoading(false);
            return false;
        }
    }

    // =============================
    // 유틸리티 함수
    // =============================

    /**
     * 그리드 인스턴스 반환 함수
     * 외부에서 그리드 인스턴스에 직접 접근할 수 있습니다.
     * 
     * @returns {Object} 그리드 인스턴스
     */
    function getGrid() {
        return productsGrid;
    }

    /**
     * 검색 조건 저장 함수
     * 현재 검색 조건을 로컬 스토리지에 저장합니다.
     */
    async function saveSearchCondition() {
        try {
            const searchCondition = document.getElementById('productsInput')?.value || '';

            localStorage.setItem('productsSearchCondition', searchCondition);
            console.log('검색 조건이 저장되었습니다.');

            await AlertUtil.showSuccess("저장 완료", "검색 조건이 저장되었습니다.");
            return true;
        } catch (error) {
            console.error('검색 조건 저장 오류:', error);
            await AlertUtil.showError('저장 오류', '검색 조건 저장 중 오류가 발생했습니다.');
            return false;
        }
    }

    /**
     * 저장된 검색 조건 로드 함수
     * 로컬 스토리지에서 저장된 검색 조건을 불러와 적용합니다.
     * 
     * @returns {boolean} 로드 성공 여부
     */
    async function loadSearchCondition() {
        try {
            const savedCondition = localStorage.getItem('productsSearchCondition');

            if (!savedCondition) {
                console.log('저장된 검색 조건이 없습니다.');
                return false;
            }

            // 검색 조건 설정
            const searchInput = document.getElementById('productsInput');
            if (searchInput) {
                searchInput.value = savedCondition;
            }

            // 검색 실행
            await searchData();

            console.log('검색 조건이 로드되었습니다.');
            return true;
        } catch (error) {
            console.error('검색 조건 로드 오류:', error);
            await AlertUtil.showError('로드 오류', '검색 조건 로드 중 오류가 발생했습니다.');
            return false;
        }
    }

    /**
     * 로컬 검색 함수
     * 그리드 내 로컬 데이터를 대상으로 검색을 수행합니다.
     */
    function performLocalSearch() {
        try {
            const keyword = document.getElementById('productsInput').value.toLowerCase();

            // 원본 데이터 가져오기
            GridSearchUtil.resetToOriginalData('productsGrid');
            const grid = GridUtil.getGrid('productsGrid');
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

    // =============================
    // 공개 API - 외부에서 접근 가능한 메서드
    // =============================
    return {
        // 초기화 및 기본 기능
        init, // 모듈 초기화

        // 데이터 관련 함수
        searchData, // 데이터 검색
        appendRow, // 행 추가
        saveData, // 데이터 저장
        deleteRows, // 데이터 삭제

        // 드롭다운 관련 함수 (추가된 기능)
        loadWarehouseData, // 창고 데이터 로드
        updateLocationDropdown, // 위치 드롭다운 업데이트

        // 유틸리티 함수
        getGrid, // 그리드 인스턴스 반환
        saveSearchCondition, // 검색 조건 저장
        loadSearchCondition, // 저장된 검색 조건 로드
        performLocalSearch // 로컬 검색 실행
    };
})();

// =============================
// DOM 로드 시 초기화
// =============================
document.addEventListener('DOMContentLoaded', async function() {
    try {
        // 제품 기준정보 관리 초기화
        await ProductsManager.init();

        // 저장된 검색 조건 로드 (필요 시 활성화)
        // await ProductsManager.loadSearchCondition();
    } catch (error) {
        console.error('초기화 중 오류 발생:', error);
        if (window.AlertUtil) {
            await AlertUtil.showError('초기화 오류', '제품 기준정보 관리 초기화 중 오류가 발생했습니다.');
        } else {
            alert('제품 기준정보 관리 초기화 중 오류가 발생했습니다.');
        }
    }
});