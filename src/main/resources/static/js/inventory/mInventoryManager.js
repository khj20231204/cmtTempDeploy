/**
 * 원자재 재고관리 - 재고 정보 관리 모듈
 * 
 * 원자재 재고 정보의 조회, 추가, 수정, 삭제 기능을 담당하는 관리 모듈입니다.
 * FIFO 기능이 추가되었습니다.
 * 
 * @version 2.3.0
 * @since 2025-04-25
 */
const MaterialInventoryManager = (function() {
    // =============================
    // 모듈 내부 변수 - 필요에 따라 변경하세요
    // =============================

    // 그리드 인스턴스 참조
    let mInventoryGrid;

    // API URL 상수 정의
	const API_URLS = {
	    LIST: '/api/materialinventory/list', // 
	    SAVE: '/api/materialinventory/save', // /api/materialInventory에서 변경  
	    DELETE: '/api/materialinventory/delete', // /api/materialInventory에서 변경
	    FIFO: '/api/materialinventory/fifo', // 이건 맞음
	    FIFO_HISTORY: '/api/materialinventory/fifo-history', // 이것도 맞음
	    EXCEL: {
	        UPLOAD: '/api/materialinventory/excel/upload', // /api/materialInventory에서 변경
	        DOWNLOAD: '/api/materialinventory/excel/download' // /api/materialInventory에서 변경 
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
            console.log('원자재 재고관리 초기화를 시작합니다.');

            // 그리드 초기화
            await initGrid();

            // 이벤트 리스너 등록
            await registerEvents();

            // 그리드 검색 초기화
            initGridSearch();

            // 엑셀 기능 초기화
            initExcelFeatures();

            console.log('원자재 재고관리 초기화가 완료되었습니다.');
        } catch (error) {
            console.error('초기화 중 오류 발생:', error);
            await AlertUtil.showError('초기화 오류', '원자재 재고관리 초기화 중 오류가 발생했습니다.');
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
                'mInventoryAppendBtn': appendRow, // 행 추가 버튼
                'mInventorySaveBtn': saveData, // 데이터 저장 버튼
                'mInventoryDeleteBtn': deleteRows, // 데이터 삭제 버튼
                'mInventorySearchBtn': searchData // 데이터 검색 버튼
                // 엑셀 버튼 이벤트는 ExcelUtil에서 별도로 처리됩니다
            });

            // 엔터키 검색 이벤트 등록
            await UIUtil.bindEnterKeySearch('mInventoryInput', searchData);
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
                gridId: 'mInventoryGrid',
                searchInputId: 'mInventoryInput',
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
                buttonId: 'mInventoryExcelDownBtn',
                gridId: 'mInventoryGrid',
                fileName: 'material-inventory-data.xlsx',
                sheetName: '원자재재고정보',
                beforeDownload: function() {
                    console.log('엑셀 다운로드 시작');
                    return true;
                },
                afterDownload: function() {
                    console.log('엑셀 다운로드 완료');
                }
            });

            // 엑셀 업로드 버튼 설정 - HTML에 입력필드와 버튼 추가 필요
            ExcelUtil.setupExcelUploadButton({
                fileInputId: 'mInventoryFileInput',
                uploadButtonId: 'mInventoryExcelUpBtn',
                gridId: 'mInventoryGrid',
                apiUrl: API_URLS.EXCEL.UPLOAD,
                headerMapping: {
                    '자재코드': 'MTL_CODE',
                    '자재명': 'MTL_NAME',
                    '창고코드': 'WAREHOUSE_CODE',
                    '위치코드': 'LOCATION_CODE',
                    '현재수량': 'CURRENT_QTY',
                    '할당수량': 'ALLOCATED_QTY',
                },
                beforeLoad: function() {
                    console.log('엑셀 업로드 시작');
                    return true;
                },
                afterLoad: function(data, saveResult) {
                    console.log('엑셀 업로드 완료, 결과:', data.length, '건, 저장:', saveResult);
                    if (saveResult) {
                        // 그리드 원본 데이터 업데이트
                        GridSearchUtil.updateOriginalData('mInventoryGrid', data);
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
     * 그리드 초기화 함수
     * 그리드를 생성하고 초기 데이터를 로드합니다.
     */
    async function initGrid() {
        try {
            console.log('그리드 초기화를 시작합니다.');

            // DOM 요소 존재 확인
            const gridElement = document.getElementById('mInventoryGrid');
            if (!gridElement) {
                throw new Error('mInventoryGrid 요소를 찾을 수 없습니다. HTML을 확인해주세요.');
            }

            // 서버에서 받은 데이터 활용
            const gridData = window.mInventoryList || [];
            console.log('초기 데이터:', gridData.length, '건');

            // 그리드 생성 - GridUtil 사용
            mInventoryGrid = GridUtil.registerGrid({
                id: 'mInventoryGrid',
                columns: [{
                        header: '자재코드',
                        name: 'MTL_CODE',
                        editor: false,
                        sortable: true
                    },
                    {
                        header: '자재명',
                        name: 'MTL_NAME',
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
                        editor: false, // 트리거로 자동 계산됨
                        sortable: true,
                        align: 'right',
                        formatter: function({
                            value
                        }) {
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
                        formatter: function({
                            value
                        }) {
                            if (!value) return '';
                            const date = new Date(value);
                            return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
                        }
                    },
					{
					    header: 'FIFO',
					    name: 'fifoAction',
					    width: 100,
					    align: 'center',
					    formatter: function({row}) {
					        const mtlCode = row.MTL_CODE;
					        return `<button class="btn btn-outline-primary btn-sm fifo-btn" 
					                data-mtl-code="${mtlCode}">
					                상세
					             </button>`;
					    }
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
                    rowHeaders: ['rowNum']
                },
                toggleRowCheckedOnClick: false // 행 클릭 시 체크박스 토글 기능 활성화
            });

            // FIFO 버튼 클릭 이벤트 처리
            mInventoryGrid.on('click', function(ev) {
                console.log('그리드 클릭 이벤트:', ev);
                
                // 다양한 방식으로 event 객체 접근 시도
                const event = ev.nativeEvent || ev.originalEvent || ev.event || ev;
                console.log('event 객체:', event);
                
                const targetElement = event.target || event.srcElement;
                console.log('targetElement:', targetElement);
                
                if (targetElement && targetElement.classList.contains('fifo-btn')) {
                    const { rowKey } = ev;
                    const row = mInventoryGrid.getRow(rowKey);
                    const mtlCode = row.MTL_CODE;
                    
                    console.log('FIFO 버튼 클릭됨:', mtlCode);
                    MaterialInventoryManager.showFIFODetail(mtlCode);
                }
            });

            // 편집 완료 이벤트 처리 - 변경된 행 추적
            mInventoryGrid.on('editingFinish', function(ev) {
                const rowKey = ev.rowKey;
                const row = mInventoryGrid.getRow(rowKey);

                // 원래 값과 변경된 값이 다른 경우에만 ROW_TYPE 업데이트
                if (row.ROW_TYPE !== 'insert' && ev.value !== ev.prevValue) {
                    mInventoryGrid.setValue(rowKey, 'ROW_TYPE', 'update');
                    console.log(`행 ${rowKey}의 ROW_TYPE을 'update'로 변경했습니다.`);
                }
            });

            // 키 컬럼 제어 설정 - PK 컬럼 편집 제어
            GridUtil.setupKeyColumnControl('mInventoryGrid', 'MTL_CODE');

            // 그리드 원본 데이터 저장 (검색 기능 위해 추가)
            GridSearchUtil.updateOriginalData('mInventoryGrid', gridData);

            console.log('그리드 초기화가 완료되었습니다.');
        } catch (error) {
            console.error('그리드 초기화 오류:', error);
            throw error;
        }
    }

    // =============================
    // FIFO 관련 함수
    // =============================

    /**
     * FIFO 상세 정보 표시
     */
	async function showFIFODetail(mtlCode) {
	    console.log('showFIFODetail 호출됨:', mtlCode);
	    console.log('API URL:', `${API_URLS.FIFO}/${mtlCode}`);
	    
	    try {
	        document.getElementById('selectedMtlCode').textContent = mtlCode;
	        console.log('selectedMtlCode 설정 완료');

	        // FIFO 상세 정보 조회
	        const response = await ApiUtil.get(`${API_URLS.FIFO}/${mtlCode}`);
	        console.log('API 응답:', response);

	        if (response.success) {
	            // 응답 데이터 구조 확인을 위해 로그 추가 - 대문자 필드명 사용
	            console.log('response.data.INVENTORY:', response.data.INVENTORY);
	            console.log('response.data.STOCK_LIST:', response.data.STOCK_LIST);
	            
	            displayFIFODetail(response.data);
	            
	            // FIFO 상세 영역 표시
	            const fifoDetailSection = document.getElementById('fifoDetailSection');
	            if (fifoDetailSection) {
	                fifoDetailSection.style.display = 'block';
	                console.log('fifoDetailSection 표시됨');
	                // 스크롤 이동
	                fifoDetailSection.scrollIntoView({
	                    behavior: 'smooth'
	                });
	            } else {
	                console.error('fifoDetailSection 요소를 찾을 수 없음');
	            }
	        } else {
	            console.log('FIFO 조회 실패:', response);
	            await AlertUtil.showError('FIFO 조회 실패', 'FIFO 정보 조회에 실패했습니다.');
	        }
	    } catch (error) {
	        console.error('showFIFODetail 에러 상세:', error);
	        console.error('Error stack:', error.stack);
	        await AlertUtil.showError('오류', 'FIFO 정보 조회 중 오류가 발생했습니다.');
	    }
	}


    /**
     * FIFO 상세 정보 표시
     */
	function displayFIFODetail(data) {
	    console.log('displayFIFODetail 실행됨, data:', data);
	    
	    // FIFO 큐 시각화
	    const queueVisual = document.getElementById('queueVisual');
	    if (!queueVisual) {
	        console.error('queueVisual 요소를 찾을 수 없음');
	        return;
	    }
	    queueVisual.innerHTML = '';

	    // 필드명이 대문자인 것을 확인 - STOCK_LIST 사용
	    if (data.STOCK_LIST && data.STOCK_LIST.length > 0) {
	        console.log('STOCK_LIST 개수:', data.STOCK_LIST.length);
	        data.STOCK_LIST.forEach((stock, index) => {
	            // 데이터 확인을 위한 로깅
	            console.log(`Stock ${index}:`, stock);
	            
	            const remaining = parseFloat(stock.REMAINING_QTY);
	            const isActive = stock.STATUS === '사용중';
	            const isNext = !isActive && remaining > 0 && index === 1;

	            const queueItem = document.createElement('div');
	            queueItem.className = `queue-item ${isActive ? 'active' : ''} ${isNext ? 'next' : ''}`;

	            queueItem.innerHTML = `
	                <div class="queue-number">${stock.FIFO_ORDER}순위</div>
	                <div class="queue-date">${formatDate(stock.RECEIPT_DATE)}</div>
	                <div class="queue-progress">
	                    <div>입고번호: ${stock.RECEIPT_NO}</div>
	                    <small class="text-muted">남은수량: ${Number(remaining).toLocaleString()}</small>
	                    <div class="progress-bar-custom">
	                        <div class="${isActive ? 'progress-fill-blue' : 'progress-fill-yellow'}" 
	                             style="width: ${remaining > 0 ? '100%' : '0%'};"></div>
	                    </div>
	                </div>
	                <div style="color: ${isActive ? '#0d6efd' : isNext ? '#ffc107' : '#6c757d'};">
	                    ${stock.STATUS}
	                </div>
	            `;

	            queueVisual.appendChild(queueItem);
	        });
	        console.log('queueVisual 업데이트 완료');
	    } else {
	        console.log('STOCK_LIST가 없거나 비어있음');
	    }

	    // 상세 테이블 표시
	    const tableBody = document.getElementById('fifoDetailTableBody');
	    if (!tableBody) {
	        console.error('fifoDetailTableBody 요소를 찾을 수 없음');
	        return;
	    }
	    tableBody.innerHTML = '';

	    if (data.STOCK_LIST && data.STOCK_LIST.length > 0) {
	        data.STOCK_LIST.forEach(stock => {
	            const row = document.createElement('tr');
	            row.innerHTML = `
	                <td>${stock.FIFO_ORDER}</td>
	                <td>${formatDate(stock.RECEIPT_DATE)}</td>
	                <td>${stock.RECEIPT_NO}</td>
	                <td>${Number(stock.REMAINING_QTY).toLocaleString()}</td>
	                <td>${Number(stock.REMAINING_QTY).toLocaleString()}</td>
	                <td>
	                    <span class="badge bg-${getBadgeColor(stock.STATUS)}">${stock.STATUS}</span>
	                </td>
	            `;
	            tableBody.appendChild(row);
	        });
	        console.log('테이블 업데이트 완료');
	    } else {
	        console.log('테이블에 표시할 데이터가 없음');
	    }
	}

    /**
     * FIFO 이력 로드
     */
    async function loadFIFOHistory(mtlCode) {
        try {
            const response = await ApiUtil.get(`${API_URLS.FIFO_HISTORY}/${mtlCode}`);

            if (response.success) {
                displayFIFOHistory(response.data);
            } else {
                await AlertUtil.showError('이력 조회 실패', 'FIFO 이력 조회에 실패했습니다.');
            }
        } catch (error) {
            console.error('FIFO 이력 조회 오류:', error);
            await AlertUtil.showError('오류', 'FIFO 이력 조회 중 오류가 발생했습니다.');
        }
    }

    /**
     * FIFO 이력 표시
     */
    function displayFIFOHistory(historyData) {
        const tableBody = document.getElementById('fifoHistoryTableBody');
        tableBody.innerHTML = '';

        if (historyData && historyData.length > 0) {
            historyData.forEach(history => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${formatDateTime(history.UPDATED_DATE)}</td>
                    <td>${history.ACTION_TYPE}</td>
                    <td>${history.ACTION_DESCRIPTION}</td>
                    <td>${history.UPDATED_BY || '-'}</td>
                `;
                tableBody.appendChild(row);
            });
        } else {
            tableBody.innerHTML = '<tr><td colspan="4" class="text-center">이력 정보가 없습니다.</td></tr>';
        }
    }

    /**
     * 탭 전환
     */
    function switchTab(tabName) {
        // 탭 버튼 상태 변경
        document.querySelectorAll('.tab-button').forEach(btn => {
            btn.classList.remove('active');
        });
        event.target.classList.add('active');

        // 탭 콘텐츠 표시
        document.getElementById('queueTab').style.display = tabName === 'queue' ? 'block' : 'none';
        document.getElementById('historyTab').style.display = tabName === 'history' ? 'block' : 'none';

        // 이력 탭으로 전환 시 데이터 로드
        if (tabName === 'history') {
            const mtlCode = document.getElementById('selectedMtlCode').textContent;
            loadFIFOHistory(mtlCode);
        }
    }

    // =============================
    // 유틸리티 함수
    // =============================

    /**
     * 날짜 포맷팅
     */
    function formatDate(dateString) {
        if (!dateString) return '-';
        const date = new Date(dateString);
        return date.toISOString().split('T')[0];
    }

    /**
     * 날짜시간 포맷팅
     */
    function formatDateTime(dateString) {
        if (!dateString) return '-';
        const date = new Date(dateString);
        return date.toLocaleString('ko-KR');
    }

    /**
     * 상태별 배지 색상 반환
     */
    function getBadgeColor(status) {
        switch (status) {
            case '사용중':
                return 'primary';
            case '대기':
                return 'warning';
            case '소진':
                return 'secondary';
            default:
                return 'light';
        }
    }

    // =============================
    // 기존 CRUD 처리 함수들...
    // =============================

    // 기존 함수들 유지 (데이터 행 추가, 검색, 저장, 삭제 등)
    async function appendRow() {
        try {
            console.log('행 추가');

            const newRowData = {
                MTL_CODE: '',
                MTL_NAME: '',
                WAREHOUSE_CODE: '',
                LOCATION_CODE: '',
                CURRENT_QTY: '0',
                ALLOCATED_QTY: '0',
                AVAILABLE_QTY: '0', // 트리거에 의해 자동 계산됨
                LAST_MOVEMENT_DATE: new Date()
                // ROW_TYPE은 GridUtil.addNewRow()에서 자동으로 추가됨
            };

            // 그리드에 새 행 추가
            await GridUtil.addNewRow('mInventoryGrid', newRowData, {
                at: 0,
                focus: true
            });
        } catch (error) {
            console.error('행 추가 중 오류:', error);
            await AlertUtil.showError('행 추가 오류', '행 추가 중 오류가 발생했습니다.');
        }
    }

    async function searchData() {
        try {
            const keyword = document.getElementById('mInventoryInput').value;
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
            const grid = GridUtil.getGrid('mInventoryGrid');
            if (grid) {
                grid.resetData(data);

                // 그리드 원본 데이터 업데이트 (검색 기능 위해 추가)
                GridSearchUtil.updateOriginalData('mInventoryGrid', data);
            }

            console.log('데이터 검색 완료. 결과:', data.length, '건');
            return data;
        } catch (error) {
            console.error('데이터 검색 중 오류:', error);
            await AlertUtil.showError('검색 오류', '데이터 검색 중 오류가 발생했습니다.');
            throw error;
        }
    }

    async function saveData() {
        try {
            console.log('데이터 저장 시작');

            const grid = GridUtil.getGrid('mInventoryGrid');
            if (!grid) {
                throw new Error('mInventoryGrid를 찾을 수 없습니다.');
            }

            // 마지막으로 입력한 셀에 대한 값 반영을 위해 포커스 해제
            grid.blur();

            // 변경된 데이터 추출
            const changes = await GridUtil.extractChangedData('mInventoryGrid');
            const modifiedData = [...changes.insert, ...changes.update];

            if (modifiedData.length === 0) {
                await AlertUtil.showWarning("알림", "수정된 내용이 없습니다.");
                return false;
            }

            // 유효성 검사
            for (const item of modifiedData) {
                if (ValidationUtil.isEmpty(item.MTL_CODE)) {
                    await AlertUtil.notifyValidationError("유효성 오류", "자재코드는 필수입니다.");
                    return false;
                }
                if (ValidationUtil.isEmpty(item.MTL_NAME)) {
                    await AlertUtil.notifyValidationError("유효성 오류", "자재명은 필수입니다.");
                    return false;
                }
                if (ValidationUtil.isEmpty(item.CURRENT_QTY)) {
                    await AlertUtil.notifyValidationError("유효성 오류", "현재수량은 필수입니다.");
                    return false;
                }
                if (ValidationUtil.isEmpty(item.ALLOCATED_QTY)) {
                    await AlertUtil.notifyValidationError("유효성 오류", "할당수량은 필수입니다.");
                    return false;
                }

                // 숫자 형식 검증
                if (isNaN(parseFloat(item.CURRENT_QTY))) {
                    await AlertUtil.notifyValidationError("유효성 오류", "현재수량은 숫자 형식이어야 합니다.");
                    return false;
                }
                if (isNaN(parseFloat(item.ALLOCATED_QTY))) {
                    await AlertUtil.notifyValidationError("유효성 오류", "할당수량은 숫자 형식이어야 합니다.");
                    return false;
                }
            }

            // API 호출 처리
            const response = await ApiUtil.processRequest(
                () => ApiUtil.post(API_URLS.SAVE, modifiedData), {
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

        } catch (error) {
            console.error('데이터 저장 오류:', error);
            await AlertUtil.notifySaveError("저장 실패", "데이터 저장 중 오류가 발생했습니다.");
            return false;
        }
    }

    async function deleteRows() {
        try {
            // 선택된 행 ID 확인
            const grid = GridUtil.getGrid('mInventoryGrid');
            const selectedRowKeys = grid.getCheckedRowKeys();

            if (selectedRowKeys.length === 0) {
                await AlertUtil.showWarning('알림', '삭제할 항목을 선택해주세요.');
                return false;
            }

            // 선택된 데이터 ID 목록 생성
            const selectedDataIds = [];
            for (const rowKey of selectedRowKeys) {
                const invNo = grid.getValue(rowKey, "INV_NO");
                const mtlCode = grid.getValue(rowKey, "MTL_CODE");

                // INV_NO가 없으면 MTL_CODE로 식별
                const identifier = invNo || mtlCode;
                if (identifier) selectedDataIds.push(identifier);
            }

            if (selectedDataIds.length === 0) {
                await AlertUtil.showWarning('알림', '유효한 재고번호 또는 자재코드를 찾을 수 없습니다.');
                return false;
            }

            // GridUtil.deleteSelectedRows 사용 (UI 측면의 삭제 확인 및 행 제거)
            const result = await GridUtil.deleteSelectedRows('mInventoryGrid', {
                confirmTitle: "삭제 확인",
                confirmMessage: "선택한 항목을 삭제하시겠습니까?",
                onBeforeDelete: async () => {
                    // 삭제 전 처리 - 여기서 true 반환해야 삭제 진행
                    return true;
                },
                onAfterDelete: async () => {
                    // 삭제 API 호출 및 처리
                    try {
                        // 일괄 삭제 요청 생성 - 한 번에 모든 데이터 삭제
                        await ApiUtil.withLoading(async () => {
                            await ApiUtil.post(API_URLS.DELETE, {
                                ids: selectedDataIds
                            });
                        }, '데이터 삭제 중...');

                        // 삭제 성공 메시지
                        await AlertUtil.notifyDeleteSuccess('삭제 완료', '데이터가 삭제되었습니다.');

                        // 목록 갱신
                        await searchData();
                    } catch (apiError) {
                        console.error('삭제 API 호출 중 오류:', apiError);
                        await AlertUtil.notifyDeleteError('삭제 실패', '데이터 삭제 중 API 오류가 발생했습니다.');
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

    function getGrid() {
        return mInventoryGrid;
    }

    async function saveSearchCondition() {
        try {
            const searchCondition = document.getElementById('mInventoryInput')?.value || '';

            localStorage.setItem('materialInventorySearchCondition', searchCondition);
            console.log('검색 조건이 저장되었습니다.');



            await AlertUtil.showSuccess("저장 완료", "검색 조건이 저장되었습니다.");
            return true;
        } catch (error) {
            console.error('검색 조건 저장 오류:', error);
            await AlertUtil.showError('저장 오류', '검색 조건 저장 중 오류가 발생했습니다.');
            return false;
        }
    }

    async function loadSearchCondition() {
        try {
            const savedCondition = localStorage.getItem('materialInventorySearchCondition');

            if (!savedCondition) {
                console.log('저장된 검색 조건이 없습니다.');
                return false;
            }

            // 검색 조건 설정
            const searchInput = document.getElementById('mInventoryInput');
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

    function performLocalSearch() {
        try {
            const keyword = document.getElementById('mInventoryInput').value.toLowerCase();

            // 원본 데이터 가져오기
            GridSearchUtil.resetToOriginalData('mInventoryGrid');
            const grid = GridUtil.getGrid('mInventoryGrid');
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

        // FIFO 관련 함수
        showFIFODetail, // FIFO 상세 정보 표시
        loadFIFOHistory, // FIFO 이력 조회
        switchTab, // FIFO 탭 전환

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
        // 원자재 재고관리 초기화
        await MaterialInventoryManager.init();

        // 저장된 검색 조건 로드 (필요 시 활성화)
        // await MaterialInventoryManager.loadSearchCondition();
    } catch (error) {
        console.error('초기화 중 오류 발생:', error);
        if (window.AlertUtil) {
            await AlertUtil.showError('초기화 오류', '원자재 재고관리 초기화 중 오류가 발생했습니다.');
        } else {
            alert('원자재 재고관리 초기화 중 오류가 발생했습니다.');
        }
    }
});