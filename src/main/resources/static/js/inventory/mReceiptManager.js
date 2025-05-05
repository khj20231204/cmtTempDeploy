/**
 * 원자재 입고관리 - 입고 정보 관리 모듈
 * 
 * 원자재 입고정보의 조회, 확정, 검수 기능을 담당하는 관리 모듈입니다.
 * 
 * @version 3.1.1
 * @since 2025-05-03
 */
const MaterialReceiptManager = (function() {
    // =============================
    // 모듈 내부 변수 - 필요에 따라 변경하세요
    // =============================

    // 그리드 인스턴스 참조
    let mReceiptGrid;
    let purchaseOrderGrid;

    // 현재 선택된 입고정보
    let selectedReceipt = null;

    // 탭 컨트롤러 참조
    let tabController = null;

    // API URL 상수 정의
    const API_URLS = {
        LIST: '/api/materialreceipt/list', // 입고 목록 조회
        DETAIL: (receiptNo) => `/api/materialreceipt/detail/${receiptNo}`, // 상세 정보 조회
        HISTORY: (receiptNo) => `/api/materialreceipt/history/${receiptNo}`, // 이력 정보 조회
        INSPECTION: `/api/materialreceipt/inspection`, // 검수 등록
        INSPECTION_INFO: (receiptNo) => `/api/materialreceipt/inspection/${receiptNo}`, // 검수 정보 조회
        CONFIRM: '/api/materialreceipt/confirm', // 입고 확정
        WAREHOUSES: '/api/warehouse/list', // 창고 목록 조회
        LOCATIONS: (whsCode) => `/api/warehouse/locations/${whsCode}`, // 위치 목록 조회
        PURCHASE_ORDERS: '/api/materialreceipt/purchase-orders', // 미입고 발주 목록 조회
        PURCHASE_PROCESS: '/api/materialreceipt/register-all', // 발주입고처리
        EXCEL: {
            DOWNLOAD: '/api/materialreceipt/excel/download' // 엑셀 다운로드 API URL
        }
    };

    // 입고 상태 정의
    const RECEIPT_STATUS = {
        WAITING: '입고대기',
        INSPECTING: '검수중',
        INSPECT_PASSED: '검사 합격',
        INSPECT_FAILED: '검사 불합격',
        COMPLETED: '입고완료',
        CANCELED: '취소'
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
            console.log('원자재 입고관리 초기화를 시작합니다.');

            // 그리드 초기화
            await initGrid();

            // 이벤트 리스너 등록
            await registerEvents();

            // 그리드 검색 초기화
            initGridSearch();

            // 엑셀 기능 초기화
            initExcelFeatures();

            // 발주 그리드 초기화
            initPurchaseOrderGrid();

            console.log('원자재 입고관리 초기화가 완료되었습니다.');
        } catch (error) {
            console.error('초기화 중 오류 발생:', error);
            await AlertUtil.showError('초기화 오류', '원자재 입고관리 초기화 중 오류가 발생했습니다.');
        }
    }

    /**
     * 이벤트 리스너 등록 함수
     * 버튼 클릭 및 엔터키 검색 등의 이벤트 리스너를 등록합니다.
     */
    async function registerEvents() {
        try {
            // UIUtil을 사용하여 이벤트 리스너 등록 - 버튼 이벤트
            await UIUtil.registerEventListeners({
                'mReceiptConfirmBtn': confirmReceipt, // 기본 입고확정 버튼
                'mReceiptConfirmWithLocationBtn': openConfirmWithLocationModal, // 위치지정 입고확정 버튼
                'mReceiptInspectionBtn': openInspectionModal, // 검수등록 버튼
                'mReceiptSearchBtn': searchData, // 데이터 검색 버튼
                'mReceiptTestBtn': openPurchaseProcessModal, // 발주입고 처리 모달 버튼
                'confirmReceiptBtn': processConfirmWithLocation, // 모달 내 확정 버튼
                'processPurchaseOrderBtn': processPurchaseOrderBtn // 발주입고처리 버튼
            });

            // 엔터키 검색 이벤트 등록
            await UIUtil.bindEnterKeySearch('mReceiptInput', searchData);

            // 창고 선택 이벤트 추가
            const warehouseSelect = document.getElementById('warehouseSelect');
            if (warehouseSelect) {
                warehouseSelect.addEventListener('change', function() {
                    loadLocationData(this.value);
                });
            }

            // 탭 이벤트 등록 - 이력정보 탭
            const historyTab = document.getElementById('history-tab');
            if (historyTab) {
                historyTab.addEventListener('shown.bs.tab', function(e) {
                    if (selectedReceipt) {
                        loadHistoryData(selectedReceipt.RECEIPT_NO);
                    }
                });
            }
        } catch (error) {
            console.error('이벤트 리스너 등록 중 오류:', error);
            throw error;
        }
    }

    /**
     * 발주 그리드 초기화 함수
     * 발주입고처리 모달에서 사용할 발주 그리드를 초기화합니다.
     */
    function initPurchaseOrderGrid() {
        try {
            console.log('발주 그리드 초기화');

            // DOM 요소 확인
            const gridElement = document.getElementById('purchaseOrderGrid');
            if (!gridElement) {
                console.warn('purchaseOrderGrid 요소를 찾을 수 없습니다.');
                return;
            }

            // 그리드 생성
            purchaseOrderGrid = GridUtil.registerGrid({
                id: 'purchaseOrderGrid',
                columns: [{
                        header: '발주 코드',
                        name: 'PO_CODE',
                        sortable: true
                    },
                    {
                        header: '원자재 코드',
                        name: 'MTL_CODE',
                        sortable: true
                    },
                    {
                        header: '주문 수량',
                        name: 'PO_QTY',
                        sortable: true
                    },
                    {
                        header: '발주일',
                        name: 'PO_DATE',
                        sortable: true,
                        formatter: function(obj) {
                            if (!obj.value) return '-';
                            return formatDate(obj.value);
                        }
                    },
                    {
                        header: '창고 코드',
                        name: 'WHS_CODE',
                        sortable: true
                    },
                    {
                        header: '상태',
                        name: 'PO_STATUS',
                        sortable: true
                    }
                ],
                data: [],
                pageOptions: {
                    useClient: true,
                    perPage: 5
                },
                gridOptions: {
                    rowHeaders: ['checkbox']
                }
            });

            console.log('발주 그리드 초기화 완료');
        } catch (error) {
            console.error('발주 그리드 초기화 중 오류:', error);
        }
    }

    /**
     * 발주입고처리 모달 열기
     */
    async function openPurchaseProcessModal() {
        try {
            console.log('발주입고처리 모달 열기');

            // 모달 표시
            const modal = new bootstrap.Modal(document.getElementById('purchaseToReceiptModal'));
            modal.show();

            // 미입고 발주 목록 로드
            await loadPurchaseOrders();

            return true;
        } catch (error) {
            console.error('발주입고처리 모달 열기 중 오류:', error);
            await AlertUtil.showError('오류', '발주입고처리 모달을 열 수 없습니다.');
            return false;
        }
    }

    /**
     * 미입고 발주 목록 로드
     */
    async function loadPurchaseOrders() {
        try {
            console.log('미입고 발주 목록 로드');
            await UIUtil.toggleLoading(true, '발주 정보를 불러오는 중...');

            // API 호출 - 미입고 발주 목록 조회
            const response = await ApiUtil.get(API_URLS.PURCHASE_ORDERS);

            await UIUtil.toggleLoading(false);

            if (!response.success) {
                throw new Error('발주 목록을 가져오는데 실패했습니다: ' + response.message);
            }

            const purchaseOrders = response.data || [];

            // 그리드에 데이터 표시
            if (purchaseOrderGrid) {
                purchaseOrderGrid.resetData(purchaseOrders);

                // 그리드가 제대로 표시되지 않는 문제 해결을 위한 딜레이 후 레이아웃 갱신
                setTimeout(() => {
                    purchaseOrderGrid.refreshLayout();
                }, 100);
            }

            console.log('발주 목록 로드 완료:', purchaseOrders.length, '건');
            return true;
        } catch (error) {
            console.error('발주 목록 로드 중 오류:', error);
            await UIUtil.toggleLoading(false);
            await AlertUtil.showError('조회 오류', '발주 목록을 불러오는데 실패했습니다.');
            return false;
        }
    }

    /**
     * 발주입고처리 실행
     */
	async function processPurchaseOrderBtn() {
	    try {
	        console.log('발주입고처리 실행');

	        // 발주 상세 그리드에서 선택된 항목 확인
	        if (!purchaseOrderGrid) {
	            await AlertUtil.showWarning('알림', '발주 그리드가 초기화되지 않았습니다.');
	            return false;
	        }

	        const checkedRows = purchaseOrderGrid.getCheckedRows();

	        if (checkedRows.length === 0) {
	            await AlertUtil.showWarning('알림', '입고 등록할 발주를 선택해주세요.');
	            return false;
	        }

	        // 처리할 요청 목록 구성
	        const requestItems = checkedRows.map(row => ({
	            PO_CODE: row.PO_CODE,
	            MTL_CODE: row.MTL_CODE,
	            PO_QTY: row.PO_QTY
	        }));

	        // 입고 등록 처리
	        const confirmed = await AlertUtil.showConfirm({
	            title: '발주입고처리',
	            text: `선택한 ${requestItems.length}건의 발주에 대해 입고 등록하시겠습니까?`,
	            icon: 'question'
	        });

	        if (!confirmed) {
	            return false;
	        }

	        // API 호출
	        await UIUtil.toggleLoading(true, '발주입고 처리 중...');

	        const response = await ApiUtil.post(API_URLS.PURCHASE_PROCESS, {
	            items: requestItems  // 선택된 발주 정보 전송
	        });

	        await UIUtil.toggleLoading(false);

	        if (!response.success) {
	            throw new Error('발주입고처리에 실패했습니다: ' + response.message);
	        }

	        // 성공 처리
	        await AlertUtil.showSuccess('발주입고 완료', response.message || '선택한 발주 정보를 입고 대기 상태로 등록했습니다.');

	        // 모달 닫기
	        const modal = bootstrap.Modal.getInstance(document.getElementById('purchaseToReceiptModal'));
	        if (modal) {
	            modal.hide();
	        }

	        // 입고 그리드 갱신
	        await searchData();

	        return true;
	    } catch (error) {
	        console.error('발주입고처리 중 오류:', error);
	        await UIUtil.toggleLoading(false);
	        await AlertUtil.showError('처리 오류', '발주입고처리 중 오류가 발생했습니다: ' + error.message);
	        return false;
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
            const gridElement = document.getElementById('mReceiptGrid');
            if (!gridElement) {
                throw new Error('mReceiptGrid 요소를 찾을 수 없습니다. HTML을 확인해주세요.');
            }

            // 서버에서 받은 데이터 활용
            const gridData = window.mReceipt || [];
            console.log('초기 데이터:', gridData ? gridData.length : 0, '건');

            // 그리드 생성 - GridUtil 사용
            mReceiptGrid = GridUtil.registerGrid({
                id: 'mReceiptGrid',
                columns: [{
                        header: '입고 코드',
                        name: 'RECEIPT_CODE',
                        sortable: true
                    },
                    {
                        header: '발주 코드',
                        name: 'PO_CODE',
                        sortable: true
                    },
                    {
                        header: '자재 코드',
                        name: 'MTL_CODE',
                        sortable: true
                    },
                    {
                        header: '자재명',
                        name: 'MTL_NAME',
                        sortable: true
                    },
                    {
                        header: '입고일',
                        name: 'RECEIPT_DATE',
                        sortable: true,
                        formatter: function(obj) {
                            if (!obj.value) return '-';
                            const date = new Date(obj.value);
                            return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
                        }
                    },
                    {
                        header: '입고 수량',
                        name: 'RECEIVED_QTY',
                        sortable: true
                    },
                    {
                        header: '입고 상태',
                        name: 'RECEIPT_STATUS',
                        sortable: true,
                        formatter: function(obj) {
                            const status = obj.value || RECEIPT_STATUS.WAITING;
                            let badgeClass = 'badge-waiting';

                            if (status === RECEIPT_STATUS.INSPECTING) {
                                badgeClass = 'badge-inspecting';
                            } else if (status === RECEIPT_STATUS.COMPLETED) {
                                badgeClass = 'badge-completed';
                            } else if (status === RECEIPT_STATUS.INSPECT_PASSED) {
                                badgeClass = 'badge-inspectPass'
                            } else if (status === RECEIPT_STATUS.INSPECT_FAILED) {
                                badgeClass = 'badge-canceled';
                            } else if (status === RECEIPT_STATUS.CANCELED) {
                                badgeClass = 'badge-canceled';
                            }

                            return `<span class="badge ${badgeClass}">${status}</span>`;
                        }
                    },
                    {
                        header: '창고 코드',
                        name: 'WAREHOUSE_CODE',
                        sortable: true
                    },
                    {
                        header: '위치 코드',
                        name: 'LOCATION_CODE',
                        sortable: true
                    },
                    {
                        header: '담당자',
                        name: 'RECEIVER',
                        sortable: true
                    },
                    {
                        header: '입고번호',
                        name: 'RECEIPT_NO',
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
                gridOptions: {
                    rowHeaders: ['rowNum', 'checkbox']
                }
            });

            // 그리드 데이터 정리 - 빈 상태 값 초기화
            const rows = mReceiptGrid.getData();
            rows.forEach((row, index) => {
                if (!row.RECEIPT_STATUS) {
                    mReceiptGrid.setValue(row.rowKey, 'RECEIPT_STATUS', RECEIPT_STATUS.WAITING);
                }
            });

            // 행 클릭 이벤트 등록 - 상세 정보 표시
            GridUtil.onRowClick('mReceiptGrid', async function(rowData, rowKey, columnName) {
                if (!rowData) return;

                // 현재 선택된 입고 정보 저장
                selectedReceipt = rowData;

                // 상세 정보 조회 및 표시
                await loadReceiptDetail(rowData.RECEIPT_NO);
            });

            // 그리드 원본 데이터 저장 (검색 기능 위해 추가)
            GridSearchUtil.updateOriginalData('mReceiptGrid', gridData);

            console.log('그리드 초기화가 완료되었습니다.');
        } catch (error) {
            console.error('그리드 초기화 오류:', error);
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
                gridId: 'mReceiptGrid',
                searchInputId: 'mReceiptInput',
                autoSearch: true, // 입력 시 자동 검색
                beforeSearch: function() {
                    console.log('그리드 검색 시작');
                    return true;
                },
                afterSearch: function(filteredData) {
                    console.log('그리드 검색 완료, 결과:', filteredData.length, '건');

                    // 상세 정보 영역 초기화
                    hideDetailSection();
                }
            });
        } catch (error) {
            console.error('그리드 검색 초기화 중 오류:', error);
        }
    }

    /**
     * 엑셀 기능 초기화 함수
     * ExcelUtil을 사용하여 엑셀 다운로드 기능을 설정합니다.
     */
    function initExcelFeatures() {
        try {
            console.log('엑셀 기능 초기화');

            // 엑셀 다운로드 버튼 설정
            ExcelUtil.setupExcelDownloadButton({
                buttonId: 'mReceiptExcelDownBtn',
                gridId: 'mReceiptGrid',
                fileName: 'material-receipt-data.xlsx',
                sheetName: '원자재입고정보',
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
    // 상세 정보 관련 함수
    // =============================

    /**
     * 입고 상세 정보 로드 함수
     * 선택된 입고 정보의 상세 내용을 조회하고 표시합니다.
     * 
     * @param {number} receiptNo - 입고 번호
     */
    async function loadReceiptDetail(receiptNo) {
        try {
            // 로딩 표시
            await UIUtil.toggleLoading(true, '상세 정보를 불러오는 중...');

            // API 호출
            const response = await ApiUtil.get(API_URLS.DETAIL(receiptNo));

            // 로딩 종료
            await UIUtil.toggleLoading(false);

            if (!response.success) {
                throw new Error('상세 정보를 가져오는데 실패했습니다: ' + response.message);
            }

            const detailData = response.data;

            // 상세 정보 표시
            displayDetailInfo(detailData);

            // 상세 정보 영역 표시
            showDetailSection();

            return true;
        } catch (error) {
            console.error('상세 정보 로드 중 오류:', error);
            await UIUtil.toggleLoading(false);

            // 오류 알림
            await AlertUtil.showError('조회 오류', '상세 정보를 불러오는데 실패했습니다.');

            return false;
        }
    }

    /**
     * 상세 정보 표시 함수
     * 
     * @param {Object} detailData - 입고 상세 정보 데이터
     */
    function displayDetailInfo(detailData) {
        // 기본 정보 탭 데이터 설정
        document.getElementById('receiptNo').textContent = detailData.RECEIPT_NO || '';
        document.getElementById('receiptCode').textContent = detailData.RECEIPT_CODE || '';
        document.getElementById('mtlCode').textContent = detailData.MTL_CODE || '';
        document.getElementById('mtlName').textContent = detailData.MTL_NAME || '';
        document.getElementById('receivedQty').textContent = detailData.RECEIVED_QTY || '';

        // 날짜 포맷팅
        let receiptDate = '-';
        if (detailData.RECEIPT_DATE) {
            const date = new Date(detailData.RECEIPT_DATE);
            receiptDate = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
        }
        document.getElementById('receiptDate').textContent = receiptDate;

        // 상태에 따라 배지 스타일 적용
        const status = detailData.RECEIPT_STATUS || RECEIPT_STATUS.WAITING;
        let badgeClass = 'badge-waiting';

        if (status === RECEIPT_STATUS.INSPECTING) {
            badgeClass = 'badge-inspecting';
        } else if (status === RECEIPT_STATUS.COMPLETED) {
            badgeClass = 'badge-completed';
        } else if (status === RECEIPT_STATUS.INSPECT_PASSED) {
            badgeClass = 'badge-inspectPass';
        } else if (status === RECEIPT_STATUS.INSPECT_FAILED) {
            badgeClass = 'badge-canceled';
        } else if (status === RECEIPT_STATUS.CANCELED) {
            badgeClass = 'badge-canceled';
        }

        document.getElementById('receiptStatus').innerHTML = `<span class="badge ${badgeClass}">${status}</span>`;
        document.getElementById('poCode').textContent = detailData.PO_CODE || '';
        document.getElementById('warehouseInfo').textContent =
            `${detailData.WAREHOUSE_CODE || ''} / ${detailData.LOCATION_CODE || ''}`;
        document.getElementById('receiver').textContent = detailData.RECEIVER || '';
    }

    /**
     * 이력 정보 로드 함수
     * 선택된 입고의 이력 정보를 로드하고 표시합니다.
     * 
     * @param {number} receiptNo - 입고 번호
     * @returns {Promise<boolean>} 로드 성공 여부
     */
    async function loadHistoryData(receiptNo) {
        try {
            // 로딩 표시
            await UIUtil.toggleLoading(true, '이력 정보를 불러오는 중...');

            // API 호출
            const response = await ApiUtil.get(API_URLS.HISTORY(receiptNo));

            // 로딩 종료
            await UIUtil.toggleLoading(false);

            if (!response.success) {
                throw new Error('이력 정보를 가져오는데 실패했습니다: ' + response.message);
            }

            const historyData = response.data || [];

            // 이력 정보 표시
            displayHistoryInfo(historyData);

            return true;
        } catch (error) {
            console.error('이력 정보 로드 중 오류:', error);
            await UIUtil.toggleLoading(false);

            // 오류 시 빈 테이블 표시
            displayHistoryInfo([]);

            return false;
        }
    }

    /**
     * 이력 정보 표시 함수
     * 
     * @param {Array} historyData - 이력 정보 데이터 배열
     */
    function displayHistoryInfo(historyData) {
        const historyInfoBody = document.getElementById('historyInfoBody');

        if (!historyInfoBody) {
            console.error('이력 정보 테이블 요소를 찾을 수 없습니다.');
            return;
        }

        if (!historyData || historyData.length === 0) {
            historyInfoBody.innerHTML = `
               <tr>
                   <td colspan="4" class="text-center">이력 정보가 없습니다.</td>
               </tr>
           `;
            return;
        }

        // 이력 정보 HTML 구성
        let html = '';
        historyData.forEach(history => {
            html += `
               <tr>
                   <td>${formatDateTime(history.ACTION_DATE)}</td>
                   <td>${history.ACTION_TYPE || ''}</td>
                   <td>${history.ACTION_DESCRIPTION || ''}</td>
                   <td>${history.ACTION_USER || ''}</td>
               </tr>
           `;
        });

        historyInfoBody.innerHTML = html;
    }

    /**
     * 상세 정보 영역 표시 함수
     */
    function showDetailSection() {
        const detailSection = document.getElementById('detailSection');
        if (detailSection) {
            detailSection.style.display = 'block';

            try {
                // Bootstrap 5 방식으로 첫 번째 탭 활성화
                const firstTab = document.querySelector('#basic-info-tab');
                if (firstTab && window.bootstrap) {
                    const tab = new bootstrap.Tab(firstTab);
                    tab.show();
                    console.log('기본 탭이 활성화되었습니다.');
                }
            } catch (error) {
                console.error('탭 초기화 오류:', error);
            }
        }
    }

    /**
     * 상세 정보 영역 숨김 함수
     */
    function hideDetailSection() {
        const detailSection = document.getElementById('detailSection');
        if (detailSection) {
            detailSection.style.display = 'none';
        }

        // 선택된 입고 정보 초기화
        selectedReceipt = null;
    }

    // =============================
    // 업무 처리 함수
    // =============================

    /**
     * 데이터 검색 함수
     * 검색어를 이용하여 데이터를 검색하고 그리드에 결과를 표시합니다.
     */
    async function searchData() {
        try {
            const keyword = document.getElementById('mReceiptInput').value;
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
            const grid = GridUtil.getGrid('mReceiptGrid');
            if (grid) {
                grid.resetData(data);

                // 그리드 원본 데이터 업데이트 (검색 기능 위해 추가)
                GridSearchUtil.updateOriginalData('mReceiptGrid', data);
            }

            // 상세 정보 영역 숨기기
            hideDetailSection();

            console.log('데이터 검색 완료. 결과:', data.length, '건');
            return data;
        } catch (error) {
            console.error('데이터 검색 중 오류:', error);
            await AlertUtil.showError('검색 오류', '데이터 검색 중 오류가 발생했습니다.');
            throw error;
        }
    }

    /**
     * 창고 데이터 초기화 함수
     * 창고 목록을 가져와 드롭다운에 표시합니다.
     */
    async function initWarehouseData() {
        try {
            await UIUtil.toggleLoading(true, '창고 정보를 불러오는 중...');

            const response = await ApiUtil.get(API_URLS.WAREHOUSES);

            await UIUtil.toggleLoading(false);

            if (!response || !response.success) {
                throw new Error('창고 정보를 가져오는데 실패했습니다.');
            }

            const warehouses = response.data || [];

            // 창고 선택 드롭다운 구성
            const warehouseSelect = document.getElementById('warehouseSelect');
            if (warehouseSelect) {
                // 기본 옵션
                warehouseSelect.innerHTML = '<option value="">창고를 선택하세요</option>';

                // 창고 목록 추가
                warehouses.forEach(warehouse => {
                    const option = document.createElement('option');
                    option.value = warehouse.WHS_CODE;
                    option.textContent = `${warehouse.WHS_CODE} - ${warehouse.WHS_NAME}`;
                    warehouseSelect.appendChild(option);
                });
            }

            return true;
        } catch (error) {
            console.error('창고 데이터 초기화 중 오류:', error);
            await UIUtil.toggleLoading(false);
            return false;
        }
    }

    /**
     * 위치 데이터 로드 함수
     * 선택한 창고의 위치 목록을 가져와 드롭다운에 표시합니다.
     * 
     * @param {string} warehouseCode - 창고 코드
     */
    async function loadLocationData(warehouseCode) {
        try {
            if (!warehouseCode) {
                const locationSelect = document.getElementById('locationSelect');
                if (locationSelect) {
                    locationSelect.innerHTML = '<option value="">위치를 선택하세요</option>';
                    locationSelect.disabled = true;
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

            // 위치 선택 드롭다운 구성
            const locationSelect = document.getElementById('locationSelect');
            if (locationSelect) {
                // 기본 옵션
                locationSelect.innerHTML = '<option value="">위치를 선택하세요</option>';

                // 위치 목록 추가
                locations.forEach(location => {
                    const option = document.createElement('option');
                    option.value = location.LOC_CODE;
                    option.textContent = `${location.LOC_CODE} - ${location.LOC_NAME}`;
                    locationSelect.appendChild(option);
                });

                // 드롭다운 활성화
                locationSelect.disabled = false;
            }

            return true;
        } catch (error) {
            console.error('위치 데이터 로드 중 오류:', error);
            await UIUtil.toggleLoading(false);
            return false;
        }
    }

	/**
	 * 기본 입고 확정 함수
	 * 기존 로직을 사용하여 입고 확정 처리합니다.
	 */
	async function confirmReceipt() {
	    try {
	        // 선택된 행 ID 확인
	        const grid = GridUtil.getGrid('mReceiptGrid');
	        const selectedRowKeys = grid.getCheckedRowKeys();

	        if (selectedRowKeys.length === 0) {
	            await AlertUtil.showWarning('알림', '확정할 입고 항목을 선택해주세요.');
	            return false;
	        }

	        // 선택된 입고 정보 수집 및 유효성 검증
	        const validItems = [];
	        const invalidItems = [];

	        for (const rowKey of selectedRowKeys) {
	            const receiptData = grid.getRow(rowKey);

	            // 이미 확정된 항목 제외
	            if (receiptData.RECEIPT_STATUS === RECEIPT_STATUS.COMPLETED) {
	                invalidItems.push({
	                    code: receiptData.RECEIPT_CODE,
	                    reason: '이미 입고 완료된 항목'
	                });
	                continue;
	            }

	            // 취소된 항목 제외
	            if (receiptData.RECEIPT_STATUS === RECEIPT_STATUS.CANCELED) {
	                invalidItems.push({
	                    code: receiptData.RECEIPT_CODE,
	                    reason: '취소된 항목'
	                });
	                continue;
	            }

	            // 프론트엔드에서 검수 상태 확인 - 검사 합격 상태만 처리 가능
	            if (receiptData.RECEIPT_STATUS !== RECEIPT_STATUS.INSPECT_PASSED) {
	                invalidItems.push({
	                    code: receiptData.RECEIPT_CODE,
	                    reason: '검사 합격 상태가 아닌 항목'
	                });
	                continue;
	            }

	            validItems.push({
	                receiptNo: receiptData.RECEIPT_NO,
	                receiptCode: receiptData.RECEIPT_CODE,
	                receiptStatus: RECEIPT_STATUS.COMPLETED,
	            });
	        }

	        if (validItems.length === 0) {
	            // 선택된 모든 항목이 유효하지 않은 경우
	            let errorMessage = '다음 이유로 입고 확정할 수 없습니다:\n\n';
	            invalidItems.forEach(item => {
	                errorMessage += `- ${item.code}: ${item.reason}\n`;
	            });

	            await AlertUtil.showWarning('알림', errorMessage);
	            return false;
	        }

	        // 일부 항목이 유효하지 않은 경우
	        if (invalidItems.length > 0) {
	            let warningMessage = '다음 항목은 입고 확정이 불가능합니다:\n\n';
	            invalidItems.forEach(item => {
	                warningMessage += `- ${item.code}: ${item.reason}\n`;
	            });

	            warningMessage += '\n유효한 항목만 확정 처리하시겠습니까?';

	            const confirmed = await AlertUtil.showConfirm({
	                title: "입고 확정",
	                text: warningMessage,
	                icon: "warning"
	            });

	            if (!confirmed) {
	                return false;
	            }
	        }

	        // 확인 대화상자 표시
	        const confirmed = await AlertUtil.showConfirm({
	            title: "입고 확정",
	            text: `선택한 ${validItems.length}개 항목을 입고 확정 처리하시겠습니까?`,
	            icon: "question"
	        });

	        if (!confirmed) {
	            return false;
	        }

	        // API 호출 처리
	        await UIUtil.toggleLoading(true, '입고 확정 처리 중...');
	        
	        const response = await ApiUtil.post(API_URLS.CONFIRM, {
	            items: validItems
	        });
	        
	        await UIUtil.toggleLoading(false);

	        if (!response.success) {
	            throw new Error(response.message || '입고 확정 처리에 실패했습니다.');
	        }

	        // 성공 알림 추가
//	        await AlertUtil.showSuccess('입고 확정 완료', response.message || `${validItems.length}개 항목이 입고 확정되었습니다.`);
	        await AlertUtil.showSuccess('입고 확정 완료', response.data?.message || `${validItems.length}개 항목이 입고 확정되었습니다.`);
	        // 목록 갱신
	        await searchData();

	        // 상세 정보 영역 숨기기
	        hideDetailSection();

	        return true;
	    } catch (error) {
	        console.error('입고 확정 처리 오류:', error);
	        await UIUtil.toggleLoading(false);
	        await AlertUtil.showError('처리 오류', '입고 확정 처리 중 오류가 발생했습니다.');
	        return false;
	    }
	}

    /**
     * 위치지정 입고확정 모달 열기 함수
     * 위치지정 입고확정을 위한 모달창을 엽니다.
     */
    async function openConfirmWithLocationModal() {
        try {
            // 선택된 행 ID 확인
            const grid = GridUtil.getGrid('mReceiptGrid');
            const selectedRowKeys = grid.getCheckedRowKeys();

            if (selectedRowKeys.length === 0) {
                await AlertUtil.showWarning('알림', '확정할 입고 항목을 선택해주세요.');
                return false;
            }

            // 선택된 입고 정보 수집 및 유효성 검증
            const validItems = [];
            const invalidItems = [];

            for (const rowKey of selectedRowKeys) {
                const receiptData = grid.getRow(rowKey);

                // 이미 확정된 항목 제외
                if (receiptData.RECEIPT_STATUS === RECEIPT_STATUS.COMPLETED) {
                    invalidItems.push({
                        code: receiptData.RECEIPT_CODE,
                        reason: '이미 입고 완료된 항목'
                    });
                    continue;
                }

                // 취소된 항목 제외
                if (receiptData.RECEIPT_STATUS === RECEIPT_STATUS.CANCELED) {
                    invalidItems.push({
                        code: receiptData.RECEIPT_CODE,
                        reason: '취소된 항목'
                    });
                    continue;
                }

                // 프론트엔드에서 검수 상태 확인 - 검사 합격 상태만 처리 가능
                if (receiptData.RECEIPT_STATUS !== RECEIPT_STATUS.INSPECT_PASSED) {
                    invalidItems.push({
                        code: receiptData.RECEIPT_CODE,
                        reason: '검사 합격 상태가 아닌 항목'
                    });
                    continue;
                }

                validItems.push(receiptData);
            }

            if (validItems.length === 0) {
                // 선택된 모든 항목이 유효하지 않은 경우
                let errorMessage = '다음 이유로 입고 확정할 수 없습니다:\n\n';
                invalidItems.forEach(item => {
                    errorMessage += `- ${item.code}: ${item.reason}\n`;
                });

                await AlertUtil.showWarning('알림', errorMessage);
                return false;
            }

            // 일부 항목이 유효하지 않은 경우
            if (invalidItems.length > 0) {
                let warningMessage = '다음 항목은 입고 확정이 불가능합니다:\n\n';
                invalidItems.forEach(item => {
                    warningMessage += `- ${item.code}: ${item.reason}\n`;
                });

                warningMessage += '\n유효한 항목만 확정 처리하시겠습니까?';

                const confirmed = await AlertUtil.showConfirm({
                    title: "위치지정 입고확정",
                    text: warningMessage,
                    icon: "warning"
                });

                if (!confirmed) {
                    return false;
                }
            }

            // 모달에 확정할 항목 목록 표시
            const confirmItemsBody = document.getElementById('confirmItemsBody');
            if (confirmItemsBody) {
                let html = '';
                validItems.forEach(item => {
                    html += `
                       <tr data-receipt-no="${item.RECEIPT_NO}">
                           <td>${item.RECEIPT_CODE}</td>
                           <td>${item.MTL_CODE}</td>
                           <td>${item.MTL_NAME || '-'}</td>
                           <td>${item.RECEIVED_QTY}</td>
                           <td><span class="badge badge-inspectPass">${item.RECEIPT_STATUS}</span></td>
                       </tr>
                   `;
                });
                confirmItemsBody.innerHTML = html;
            }

            // 창고 정보 초기화
            await initWarehouseData();

            // 모달 표시
            const modal = new bootstrap.Modal(document.getElementById('confirmWithLocationModal'));
            modal.show();

            return true;
        } catch (error) {
            console.error('위치지정 입고확정 모달 열기 오류:', error);
            await AlertUtil.showError('처리 오류', '위치지정 입고확정 처리 중 오류가 발생했습니다.');
            return false;
        }
    }

    /**
     * 위치지정 입고확정 처리 함수
     * 모달에서 입력된 창고/위치 정보로 입고를 확정합니다.
     */
    async function processConfirmWithLocation() {
        try {
            // 창고/위치 선택 값 확인
            const warehouseCode = document.getElementById('warehouseSelect').value;
            const locationCode = document.getElementById('locationSelect').value;

            if (!warehouseCode) {
                await AlertUtil.showWarning('알림', '창고를 선택해주세요.');
                return false;
            }

            if (!locationCode) {
                await AlertUtil.showWarning('알림', '위치를 선택해주세요.');
                return false;
            }

            // 확정할 항목 수집
            const confirmItems = [];
            const rows = document.querySelectorAll('#confirmItemsBody tr');

            for (const row of rows) {
                const receiptNo = row.getAttribute('data-receipt-no');
                if (receiptNo) {
                    confirmItems.push({
                        receiptNo: receiptNo,
                        warehouseCode: warehouseCode,
                        locationCode: locationCode,
                        receiptStatus: RECEIPT_STATUS.COMPLETED
                    });
                }
            }

            if (confirmItems.length === 0) {
                await AlertUtil.showWarning('알림', '확정할 항목이 없습니다.');
                return false;
            }

            // 확인 대화상자 표시
            const confirmed = await AlertUtil.showConfirm({
                title: "입고 확정",
                text: `선택한 ${confirmItems.length}개 항목을 입고 확정 처리하시겠습니까?\n\n창고: ${warehouseCode}\n위치: ${locationCode}`,
                icon: "question"
            });

            if (!confirmed) {
                return false;
            }

            // API 호출 처리
            const response = await ApiUtil.processRequest(
                () => ApiUtil.post(API_URLS.CONFIRM, {
                    items: confirmItems,
                    withLocation: true
                }), {
                    loadingMessage: '입고 확정 처리 중...',
                    successMessage: `${confirmItems.length}개 항목이 입고 확정되었습니다.`,
                    errorMessage: "입고 확정 처리 중 오류가 발생했습니다.",
                    successCallback: async () => {
                        // 모달 닫기
                        const modal = bootstrap.Modal.getInstance(document.getElementById('confirmWithLocationModal'));
                        if (modal) {
                            modal.hide();
                        }

                        // 목록 갱신
                        await searchData();

                        // 상세 정보 영역 숨기기
                        hideDetailSection();
                    }
                }
            );

            return response.success;
        } catch (error) {
            console.error('위치지정 입고확정 처리 오류:', error);
            await AlertUtil.showError('처리 오류', '위치지정 입고확정 처리 중 오류가 발생했습니다.');
            return false;
        }
    }

    /**
     * 검수 등록 함수 - 다건 등록 지원
     * 선택한 입고 항목들을 검수 등록합니다.
     */
    async function openInspectionModal() {
        try {
            // 선택된 행 ID 확인
            const grid = GridUtil.getGrid('mReceiptGrid');
            const selectedRowKeys = grid.getCheckedRowKeys();

            if (selectedRowKeys.length === 0) {
                await AlertUtil.showWarning('알림', '검수 등록할 입고 항목을 선택해주세요.');
                return false;
            }

            // 다건 등록 지원
            // 모든 선택된 항목의 유효성 검증
            const invalidItems = [];
            const validItems = [];

            for (const rowKey of selectedRowKeys) {
                const receiptData = grid.getRow(rowKey);

                // 입고 완료된 항목 체크
                if (receiptData.RECEIPT_STATUS === RECEIPT_STATUS.COMPLETED) {
                    invalidItems.push({
                        code: receiptData.RECEIPT_CODE,
                        reason: '이미 입고 완료된 항목'
                    });
                    continue;
                }

                // 취소된 항목 체크
                if (receiptData.RECEIPT_STATUS === RECEIPT_STATUS.CANCELED) {
                    invalidItems.push({
                        code: receiptData.RECEIPT_CODE,
                        reason: '취소된 항목'
                    });
                    continue;
                }

                // 검수중인 항목 체크
                if (receiptData.RECEIPT_STATUS === RECEIPT_STATUS.INSPECTING) {
                    invalidItems.push({
                        code: receiptData.RECEIPT_CODE,
                        reason: '이미 검수중인 항목'
                    });
                    continue;
                }

                // 유효한 항목은 별도 목록에 추가
                validItems.push({
                    receiptNo: receiptData.RECEIPT_NO,
                    receiptCode: receiptData.RECEIPT_CODE,
                    receiptStatus: RECEIPT_STATUS.INSPECTING
                });
            }

            // 유효하지 않은 항목이 있는 경우 알림
            if (invalidItems.length > 0) {
                let errorMessage = '다음 항목은 검수 등록이 불가능합니다:\n\n';
                invalidItems.forEach(item => {
                    errorMessage += `- ${item.code}: ${item.reason}\n`;
                });

                if (validItems.length === 0) {
                    await AlertUtil.showWarning('알림', errorMessage);
                    return false;
                } else {
                    errorMessage += '\n나머지 유효한 항목만 검수 등록을 진행하시겠습니까?';
                    const confirmed = await AlertUtil.showConfirm({
                        title: "검수 등록",
                        text: errorMessage,
                        icon: "question"
                    });

                    if (!confirmed) {
                        return false;
                    }
                }
            }

            if (validItems.length === 0) {
                await AlertUtil.showWarning('알림', '검수 등록 가능한 항목이 없습니다.');
                return false;
            }

            // 검수 항목 확인 대화상자
            let confirmMessage = `선택한 ${validItems.length}개 항목을 검수 등록하시겠습니까?`;

            const confirmed = await AlertUtil.showConfirm({
                title: "검수 등록",
                text: confirmMessage,
                icon: "question"
            });

            if (!confirmed) {
                return false;
            }

            // 검수 등록 API 호출 (다건 처리)
            // API 호출 처리
            const response = await ApiUtil.processRequest(
                () => ApiUtil.post(API_URLS.INSPECTION, {
                    items: validItems
                }), {
                    loadingMessage: '검수 등록 중...',
                    successMessage: `${validItems.length}개 항목의 검수 등록이 완료되었습니다. 검수 담당자가 검사를 진행합니다.`,
                    errorMessage: "검수 등록 중 오류가 발생했습니다.",
                    successCallback: async () => {
                        // 목록 갱신
                        await searchData();

                        // 상세 정보 영역 숨기기
                        hideDetailSection();
                    }
                }
            );

            return response.success;
        } catch (error) {
            console.error('검수 등록 오류:', error);
            await AlertUtil.showError('처리 오류', '검수 등록 중 오류가 발생했습니다.');
            return false;
        }
    }

    // =============================
    // 유틸리티 함수
    // =============================

    /**
     * 날짜 포맷팅 함수
     * 
     * @param {string|Date} date - 날짜 문자열 또는 Date 객체
     * @returns {string} 포맷팅된 날짜 문자열
     */
    function formatDate(date) {
        if (!date) return '-';

        try {
            const dateObj = new Date(date);
            return `${dateObj.getFullYear()}-${String(dateObj.getMonth() + 1).padStart(2, '0')}-${String(dateObj.getDate()).padStart(2, '0')}`;
        } catch (error) {
            return '-';
        }
    }

    /**
     * 날짜/시간 포맷팅 함수
     * 
     * @param {string|Date} datetime - 날짜/시간 문자열 또는 Date 객체
     * @returns {string} 포맷팅된 날짜/시간 문자열
     */
    function formatDateTime(datetime) {
        if (!datetime) return '-';

        try {
            const dateObj = new Date(datetime);
            return `${dateObj.getFullYear()}-${String(dateObj.getMonth() + 1).padStart(2, '0')}-${String(dateObj.getDate()).padStart(2, '0')} ${String(dateObj.getHours()).padStart(2, '0')}:${String(dateObj.getMinutes()).padStart(2, '0')}`;
        } catch (error) {
            return '-';
        }
    }

    /**
     * 그리드 인스턴스 반환 함수
     * 외부에서 그리드 인스턴스에 직접 접근할 수 있습니다.
     * 
     * @returns {Object} 그리드 인스턴스
     */
    function getGrid() {
        return mReceiptGrid;
    }

    // =============================
    // 공개 API - 외부에서 접근 가능한 메서드
    // =============================
    return {
        // 초기화 및 기본 기능
        init, // 모듈 초기화

        // 데이터 관련 함수
        searchData, // 데이터 검색
        confirmReceipt, // 기본 입고확정
        openConfirmWithLocationModal, // 위치지정 입고확정 모달 열기
        openInspectionModal, // 검수 등록 모달 
        loadHistoryData, // 이력 정보 로드
        openPurchaseProcessModal, // 발주입고처리 모달 열기
        processPurchaseOrderBtn, // 발주입고처리 실행

        // 유틸리티 함수
        getGrid, // 그리드 인스턴스 반환
    };
})();

// =============================
// DOM 로드 시 초기화
// =============================
document.addEventListener('DOMContentLoaded', async function() {
    try {
        // 원자재 입고관리 모듈 초기화
        await MaterialReceiptManager.init();
    } catch (error) {
        console.error('초기화 중 오류 발생:', error);
        if (window.AlertUtil) {
            await AlertUtil.showError('초기화 오류', '원자재 입고관리 초기화 중 오류가 발생했습니다.');
        } else {
            alert('원자재 입고관리 초기화 중 오류가 발생했습니다.');
        }
    }
});