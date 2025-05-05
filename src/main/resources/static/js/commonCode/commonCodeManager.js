/**
 * 리팩토링된 CommonCodeManager - 공통코드 관리 모듈
 * 
 * 공통코드와 상세코드의 CRUD 기능을 담당하며, 리팩토링된 유틸리티 모듈(GridUtil, ApiUtil 등)을
 * 활용하여 일관된 async/await 패턴으로 구현합니다.
 * 
 * @version 1.0.0
 * @since 2025-03-24
 */
const CommonCodeManager = (function() {
    // =============================
    // 모듈 내부 변수
    // =============================

    // 그리드 인스턴스 참조
    let commonCodeGrid;
    let commonCodeDetailGrid;

    // 현재 선택된 공통코드
    let selectedCommonCode;

    // API URL 상수 정의 - 리팩토링: 일관성을 위해 상세코드 배치 저장 URL 추가
    const API_URLS = {
        COMMON_CODE: {
            LIST: '/api/common/codes',
            BATCH: '/api/common/codes/batch',
            DETAIL: (code) => `/api/common/codes/${code}/details`,
            DETAIL_BATCH: (code) => `/api/common/codes/${code}/details/batch`, // 추가: 상세코드 배치 저장 URL
            DELETE: (code) => `/api/common/codes/${code}`
        },
        DETAIL_CODE: {
            DELETE: (commonCode, detailCode) =>
                `/api/common/codes/${commonCode}/details/${detailCode}`
        }
    };
    // =============================
    // 초기화 및 이벤트 처리 함수
    // =============================

    /**
     * 모듈 초기화 함수
     * 공통코드 관리 모듈의 초기 설정 및 이벤트 바인딩을 수행합니다.
     */
    async function init() {
        try {
            console.log('공통코드 관리자 초기화를 시작합니다.');

            // 그리드 초기화
            await initGrids();

            // 이벤트 리스너 등록
            await registerEvents();

            console.log('공통코드 관리자 초기화가 완료되었습니다.');
        } catch (error) {
            console.error('초기화 중 오류 발생:', error);
            await AlertUtil.showError('초기화 오류', '공통코드 관리자 초기화 중 오류가 발생했습니다.');
        }
    }

    /**
     * 그리드 초기화 함수
     * 공통코드 그리드를 초기화합니다. 상세코드 그리드는 공통코드 선택 시 초기화됩니다.
     */
    async function initGrids() {
        // 공통코드 그리드 초기화
        await initCommonCodeGrid();
    }

    /**
     * 이벤트 리스너 등록 함수
     * 버튼 클릭 및 엔터키 검색 등의 이벤트 리스너를 등록합니다.
     */
    async function registerEvents() {
        try {
            // UIUtil을 사용하여 이벤트 리스너 등록 - 리팩토링된 UIUtil 사용
            await UIUtil.registerEventListeners({
                'commonCodeAppendBtn': appendCommonCodeRow,
                'commonCodeSaveBtn': saveCommonCodeData,
                'commonCodeDeleteBtn': deleteCommonCodeRows,
                'commonCodeSearchBtn': searchCommonCode,
                'commonCodeDetailAppendBtn': appendCommonCodeDetailRow,
                'commonCodeDetailSaveBtn': saveCommonCodeDetailData,
                'commonCodeDetailDeleteBtn': deleteCommonCodeDetailRows,
                'commonCodeDetailSearchBtn': searchCommonCodeDetail
            });

            // 엔터키 검색 이벤트 등록
            await UIUtil.bindEnterKeySearch('commonCodeInput', searchCommonCode);
            await UIUtil.bindEnterKeySearch('commonCodeDetailInput', searchCommonCodeDetail);
        } catch (error) {
            console.error('이벤트 리스너 등록 중 오류:', error);
            throw error;
        }
    }
    // =============================
    // 공통코드 그리드 관련 함수
    // =============================

    /**
     * 공통코드 그리드 초기화 함수
     * 공통코드 그리드를 생성하고 초기 데이터를 로드합니다.
     */
    async function initCommonCodeGrid() {
        try {
            console.log('공통코드 그리드 초기화를 시작합니다.');

            // DOM 요소 존재 확인
            const commonCodeElement = document.getElementById('commonCode');
            if (!commonCodeElement) {
                throw new Error('commonCode 요소를 찾을 수 없습니다. HTML을 확인해주세요.');
            }

            // 서버에서 받은 데이터 활용
            const gridData = window.commonList || [];
            console.log('공통코드 초기 데이터:', gridData.length, '건');

            // 공통코드 그리드 생성 - 리팩토링된 GridUtil 사용
            commonCodeGrid = GridUtil.registerGrid({
                id: 'commonCode',
                columns: [
					{
                        header: '코드(PK는 변경불가)',
                        name: 'CMN_CODE',
                        editor: 'text'
                    },
                    {
                        header: '코드명',
                        name: 'CMN_NAME',
                        editor: 'text'
                    },
                    {
                        header: '설명',
                        name: 'CMN_CONTENT',
                        editor: 'text'
                    },
                    {
                        header: '사용여부',
                        name: 'CMN_CODE_IS_ACTIVE',
                        editor: GridUtil.createYesNoEditor()
                    },
                    {
                        header: '타입',
                        name: 'ROW_TYPE'
                    } // 조회/추가 구분
                ],
				columnOptions: {
				  resizable: true
				},
				pageOptions: {
					useClient: true,
				    perPage: 10
				  },
                data: gridData,
                draggable: true,
                displayColumnName: 'CMN_SORT_ORDER',
                hiddenColumns: ['ROW_TYPE'],
                gridOptions: {
                    rowHeaders: ['rowNum', 'checkbox']
                }
            });

            // 행 클릭 이벤트 처리 - 리팩토링된 GridUtil 사용
			GridUtil.onRowClick('commonCode', function(rowData) {
			    // null 체크 추가
			    if (!rowData) {
			        console.warn('클릭된 행의 데이터가 존재하지 않습니다.');
			        return;
			    }
			    
			    // CMN_CODE 필드 체크
			    if (rowData.CMN_CODE === undefined || rowData.CMN_CODE === null) {
			        console.warn('행 데이터에 CMN_CODE가 정의되지 않았습니다.');
			        return;
			    }
			    
			    console.log('공통코드 행 선택:', rowData.CMN_CODE);
			    selectedCommonCode = rowData.CMN_CODE;
			    loadCommonCodeDetailGrid(selectedCommonCode);
			});
			
			commonCodeGrid.on('editingFinish', function(ev) {
			    const rowKey = ev.rowKey;
			    const row = commonCodeGrid.getRow(rowKey);
			    
			    // 원래 값과 변경된 값이 다른 경우에만 ROW_TYPE 업데이트
			    if (row.ROW_TYPE !== 'insert' && ev.value !== ev.prevValue) {
			        commonCodeGrid.setValue(rowKey, 'ROW_TYPE', 'update');
			        console.log(`공통코드 행 ${rowKey}의 ROW_TYPE을 'update'로 변경했습니다.`);
			    }
			});

            // 행 더블클릭 이벤트 처리 - 키 컬럼 제어
            GridUtil.setupKeyColumnControl('commonCode', 'CMN_CODE');

            // 첫 번째 행이 있으면 선택하여 상세코드 로드
            if (gridData.length > 0) {
                console.log('첫 번째 공통코드 행 자동 선택');
                const firstRowKey = commonCodeGrid.getRowAt(0) ?.rowKey;
                if (firstRowKey !== undefined) {
                    selectedCommonCode = commonCodeGrid.getValue(firstRowKey, "CMN_CODE");

                    // 상세코드 요소가 존재하는지 확인 후 로드 시도
                    if (document.getElementById('commonCodeDetail')) {
                        await loadCommonCodeDetailGrid(selectedCommonCode);
                    } else {
                        console.warn('commonCodeDetail 요소가 존재하지 않아 상세코드를 로드할 수 없습니다.');
                    }
                }
            }

            console.log('공통코드 그리드 초기화가 완료되었습니다.');
        } catch (error) {
            console.error('공통코드 그리드 초기화 오류:', error);
            throw error;
        }
    }

    /**
     * 공통코드 행 추가 함수
     * 공통코드 그리드에 새로운 행을 추가합니다.
     */
    async function appendCommonCodeRow() {
        try {
            console.log('공통코드 행 추가');

            const newRowData = {
                CMN_CODE: '',
                CMN_NAME: '',
                CMN_CONTENT: '',
                CMN_CODE_IS_ACTIVE: 'Y',
                CMN_SORT_ORDER: ''
                // ROW_TYPE은 리팩토링된 GridUtil.addNewRow()에서 자동으로 추가됨
            };

            // 리팩토링된 GridUtil 사용
            await GridUtil.addNewRow('commonCode', newRowData, {
                at: 0,
                focus: true
            });
        } catch (error) {
            console.error('행 추가 중 오류:', error);
            await AlertUtil.showError('행 추가 오류', '공통코드 행 추가 중 오류가 발생했습니다.');
        }
    }

    /**
     * 공통코드 검색 함수
     * 검색어를 이용하여 공통코드를 검색하고 그리드에 결과를 표시합니다.
     */
    async function searchCommonCode() {
        try {
            const keyword = document.getElementById('commonCodeInput').value;
            console.log('공통코드 검색 시작. 검색어:', keyword);

            // API 호출 - 리팩토링된 ApiUtil 사용
            const response = await ApiUtil.getWithLoading(
                API_URLS.COMMON_CODE.LIST, {
                    keyword: keyword
                },
                '공통코드 검색 중...'
            );

            // 응답 데이터 처리
            const data = Array.isArray(response) ? response : (response.data || []);

            // 그리드 데이터 설정 - 리팩토링된 GridUtil 참조
            const grid = GridUtil.getGrid('commonCode');
            if (grid) {
                grid.resetData(data);

                // 첫 번째 행 자동 선택
                if (data.length > 0) {
                    const firstRowKey = grid.getRowAt(0) ?.rowKey;
                    if (firstRowKey !== undefined) {
                        selectedCommonCode = grid.getValue(firstRowKey, "CMN_CODE");
                        await loadCommonCodeDetailGrid(selectedCommonCode);
                    }
                } else {
                    // 검색 결과가 없는 경우 상세 그리드 초기화
                    const detailGrid = GridUtil.getGrid('commonCodeDetail');
                    if (detailGrid) {
                        detailGrid.resetData([]);
                    }

                    // 선택된 공통코드 초기화
                    selectedCommonCode = null;
                }
            }

            return data;
        } catch (error) {
            console.error('공통코드 검색 중 오류:', error);
            await AlertUtil.showError('검색 오류', '공통코드 검색 중 오류가 발생했습니다.');
            throw error;
        }
    }
    // =============================
    // 상세 공통코드 그리드 관련 함수
    // =============================

    /**
     * 상세 공통코드 그리드 로드/초기화 함수
     * 선택된 공통코드에 해당하는 상세코드를 조회하여 그리드에 표시합니다.
     * 
     * @param {string} commonCode - 부모 공통코드
     */
    async function loadCommonCodeDetailGrid(commonCode) {
        if (!commonCode) {
            console.warn('공통코드가 선택되지 않았습니다.');
            return;
        }

        console.log('상세코드 로드 시작. 공통코드:', commonCode);

        try {
            // DOM 요소 확인
            const detailElement = document.getElementById('commonCodeDetail');
            if (!detailElement) {
                throw new Error('commonCodeDetail 요소를 찾을 수 없습니다. HTML에서 확인해주세요.');
            }

            // API URL 로깅
            const apiUrl = API_URLS.COMMON_CODE.DETAIL(commonCode);
            console.log('API 호출 URL:', apiUrl);

            try {
                // 리팩토링된 ApiUtil을 사용하여 데이터 조회
                const response = await ApiUtil.getWithLoading(
                    apiUrl, {
                        keyword: document.getElementById('commonCodeDetailInput') ?.value || ''
                    },
                    '상세코드 로드 중...'
                );

                // 응답 데이터 확인
                const data = Array.isArray(response) ? response : (response.data || []);

                console.log('상세코드 로드 결과:', data.length, '건');

                // 그리드 인스턴스 확인
                const detailGrid = GridUtil.getGrid('commonCodeDetail');

                if (!detailGrid) {
                    console.log('상세코드 그리드 최초 생성');
                    await initCommonCodeDetailGrid(data);
                } else {
                    console.log('기존 상세코드 그리드 데이터 갱신');
                    detailGrid.resetData(data);
                    commonCodeDetailGrid = detailGrid;
                }

                return true;
            } catch (apiError) {
                console.error('API 호출 중 오류:', apiError);
                throw apiError;
            }
        } catch (error) {
            console.error('상세코드 로드 중 오류 발생:', error);

            // 오류 시에도 빈 그리드 생성
            if (!GridUtil.getGrid('commonCodeDetail')) {
                await initCommonCodeDetailGrid([]);
            } else {
                // 이미 그리드가 있으면 빈 데이터로 갱신
                const detailGrid = GridUtil.getGrid('commonCodeDetail');
                if (detailGrid) {
                    detailGrid.resetData([]);
                }
            }

            // 오류 알림
            await AlertUtil.showError('로드 오류', '상세코드 로드 중 오류가 발생했습니다.');
            return false;
        }
    }

    /**
     * 상세 공통코드 그리드 초기화 함수
     * 상세코드 그리드를 처음 생성합니다.
     * 
     * @param {Array} data - 초기 데이터
     */
    async function initCommonCodeDetailGrid(data) {
        console.log('상세코드 그리드 초기화');

        try {
            // 데이터가 없으면 빈 배열로 초기화
            const initialData = Array.isArray(data) ? data : [];

            // 그리드 생성 - 리팩토링된 GridUtil 사용
            commonCodeDetailGrid = GridUtil.registerGrid({
                id: 'commonCodeDetail',
                columns: [{
                        header: '상세코드(PK 수정불가)',
                        name: 'CMN_DETAIL_CODE',
                        editor: 'text',
                        width: 100
                    },
                    {
                        header: '상세코드명',
                        name: 'CMN_DETAIL_NAME',
                        editor: 'text',
                        width: 100
                    },
                    {
                        header: '설명',
                        name: 'CMN_DETAIL_CONTENT',
                        editor: 'text',
                        width: 250
                    },
                    {
                        header: '값',
                        name: 'CMN_DETAIL_VALUE',
                        editor: 'text',
                        width: 250
                    },
                    {
                        header: '사용여부',
                        name: 'CMN_DETAIL_CODE_IS_ACTIVE',
                        editor: GridUtil.createYesNoEditor(),
                        width: 100
                    },
                    {
                        header: '타입',
                        name: 'ROW_TYPE'
                    } // 조회/추가 구분
                ],
				columnOptions: {
				  resizable: true
				},
                data: initialData,
                draggable: true,
                displayColumnName: 'CMN_DETAIL_SORT_ORDER',
                hiddenColumns: ['ROW_TYPE'],
                gridOptions: {
                    rowHeaders: ['rowNum', 'checkbox']
                }
            });

            // 키 컬럼 제어 설정 - 리팩토링된 GridUtil 사용
            GridUtil.setupKeyColumnControl('commonCodeDetail', 'CMN_DETAIL_CODE');
			
			commonCodeDetailGrid.on('editingFinish', function(ev) {
			    const rowKey = ev.rowKey;
			    const row = commonCodeDetailGrid.getRow(rowKey);
			    
			    if (row.ROW_TYPE !== 'insert' && ev.value !== ev.prevValue) {
			        commonCodeDetailGrid.setValue(rowKey, 'ROW_TYPE', 'update');
			        console.log(`상세코드 행 ${rowKey}의 ROW_TYPE을 'update'로 변경했습니다.`);
			    }
			});
			

            return commonCodeDetailGrid;
        } catch (error) {
            console.error('상세코드 그리드 초기화 오류:', error);
            throw error;
        }
    }

    /**
     * 상세 공통코드 검색 함수
     * 선택된 공통코드의 상세코드를 검색어를 기준으로 필터링합니다.
     */
    async function searchCommonCodeDetail() {
        try {
            if (!selectedCommonCode) {
                await AlertUtil.showWarning('알림', '공통코드를 먼저 선택해주세요');
                return false;
            }

            const keyword = document.getElementById('commonCodeDetailInput').value;
            console.log('상세코드 검색 시작. 공통코드:', selectedCommonCode, '검색어:', keyword);

            // 리팩토링된 ApiUtil 사용
            const apiUrl = API_URLS.COMMON_CODE.DETAIL(selectedCommonCode);
            const response = await ApiUtil.getWithLoading(
                apiUrl, {
                    keyword: keyword
                },
                '상세코드 검색 중...'
            );

            // 응답 데이터 처리
            const data = Array.isArray(response) ? response : (response.data || []);

            // 그리드 데이터 설정
            const detailGrid = GridUtil.getGrid('commonCodeDetail');
            if (detailGrid) {
                detailGrid.resetData(data);
                commonCodeDetailGrid = detailGrid;
            }

            console.log('상세코드 검색 완료. 결과:', data.length, '건');

            return true;
        } catch (error) {
            console.error('상세코드 검색 중 오류:', error);
            await AlertUtil.showError('검색 오류', '상세코드 검색 중 오류가 발생했습니다.');
            throw error;
        }
    }

    /**
     * 상세 공통코드 행 추가 함수
     * 상세코드 그리드에 새로운 행을 추가합니다.
     */
    async function appendCommonCodeDetailRow() {
        try {
            if (!selectedCommonCode) {
                await AlertUtil.showWarning('알림', '공통코드를 먼저 선택해주세요');
                return false;
            }

            console.log('상세코드 행 추가. 공통코드:', selectedCommonCode);

            const newRowData = {
                CMN_DETAIL_CODE: '',
                CMN_DETAIL_NAME: '',
                CMN_DETAIL_CONTENT: '',
                CMN_DETAIL_VALUE: '',
                CMN_DETAIL_CODE_IS_ACTIVE: 'Y',
                CMN_DETAIL_SORT_ORDER: ''
                // ROW_TYPE은 리팩토링된 GridUtil.addNewRow()에서 자동으로 추가됨
            };

            // 리팩토링된 GridUtil 사용
            await GridUtil.addNewRow('commonCodeDetail', newRowData, {
                at: 0,
                focus: true
            });

            return true;
        } catch (error) {
            console.error('상세코드 행 추가 중 오류:', error);
            await AlertUtil.showError('행 추가 오류', '상세코드 행 추가 중 오류가 발생했습니다.');
            return false;
        }
    }
    // =============================
    // 공통코드 CRUD 처리 함수
    // =============================

    /**
     * 공통코드 저장 함수
     * 공통코드 그리드의 변경된 데이터를 저장합니다.
     */
	async function saveCommonCodeData() {
	    try {
	        console.log('공통코드 저장 시작');

	        const grid = GridUtil.getGrid('commonCode');
	        if (!grid) {
	            throw new Error('commonCode 그리드를 찾을 수 없습니다.');
	        }

	        // 마지막으로 입력한 셀에 대한 값 반영을 위해 포커스 해제
	        grid.blur();

	        // 변경된 데이터 추출 - 리팩토링된 GridUtil 사용
	        const changes = await GridUtil.extractChangedData('commonCode');
	        const modifiedData = [...changes.insert, ...changes.update];

	        if (modifiedData.length === 0) {
	            await AlertUtil.showWarning("알림", "수정된 내용이 없습니다.");
	            return false;
	        }

	        // 저장할 데이터 준비
	        const batchData = modifiedData.map(row => ({
	            cmnCode: row.CMN_CODE,
	            cmnName: row.CMN_NAME,
	            cmnContent: row.CMN_CONTENT || '',
	            cmnCodeIsActive: row.CMN_CODE_IS_ACTIVE,
	            cmnSortOrder: row.CMN_SORT_ORDER,
	            action: row.ROW_TYPE // insert, update, delete
	        }));

	        // 유효성 검사
	        for (const item of batchData) {
	            if (ValidationUtil.isEmpty(item.cmnCode)) {
	                await AlertUtil.notifyValidationError("유효성 오류", "코드는 필수입니다.");
	                return false;
	            }
	            if (ValidationUtil.isEmpty(item.cmnName)) {
	                await AlertUtil.notifyValidationError("유효성 오류", "코드명은 필수입니다.");
	                return false;
	            }
	            if (ValidationUtil.isEmpty(item.cmnCodeIsActive)) {
	                await AlertUtil.notifyValidationError("유효성 오류", "사용여부는 필수입니다.");
	                return false;
	            }
	        }

	        // 리팩토링된 ApiUtil의 processRequest 사용
	        const response = await ApiUtil.processRequest(
	            () => ApiUtil.post(API_URLS.COMMON_CODE.BATCH, batchData), {
	                loadingMessage: '공통코드 저장 중...',
	                successMessage: "공통코드가 저장되었습니다.",
	                errorMessage: "공통코드 저장 중 오류가 발생했습니다.",
	                successCallback: searchCommonCode
	            }
	        );
			
			if(response.success){
				await AlertUtil.showSuccess('저장 완료', '데이터가 성공적으로 저장되었습니다.');
				return true;
			} else {
				return false;
			}
			
	    } catch (error) {
	        console.error('공통코드 저장 오류:', error);
	        await AlertUtil.notifySaveError("저장 실패", "공통코드 저장 중 오류가 발생했습니다.");
	        return false;
	    }
	}

    /**
     * 공통코드 삭제 함수
     * 선택된 공통코드 행을 삭제합니다.
     */
	async function deleteCommonCodeRows() {
	    try {
	        // 선택된 행 ID 확인
	        const grid = GridUtil.getGrid('commonCode');
	        const selectedRowKeys = grid.getCheckedRowKeys();
	        
	        if (selectedRowKeys.length === 0) {
	            await AlertUtil.showWarning('알림', '삭제할 항목을 선택해주세요.');
	            return false;
	        }
	        
	        // 선택된 코드 목록 생성
	        const selectedCodes = [];
	        for (const rowKey of selectedRowKeys) {
	            const cmnCode = grid.getValue(rowKey, "CMN_CODE");
	            if (cmnCode) selectedCodes.push(cmnCode);
	        }
	        
	        if (selectedCodes.length === 0) {
	            await AlertUtil.showWarning('알림', '유효한 코드를 찾을 수 없습니다.');
	            return false;
	        }
	        
	        // GridUtil.deleteSelectedRows 사용 (UI 측면의 삭제 확인 및 행 제거)
	        const result = await GridUtil.deleteSelectedRows('commonCode', {
	            confirmTitle: "삭제 확인",
	            confirmMessage: "선택한 공통코드를 삭제하시겠습니까?",
	            onBeforeDelete: async () => {
	                // 삭제 전 처리 - 여기서 true 반환해야 삭제 진행
	                return true;
	            },
	            onAfterDelete: async () => {
	                // 삭제 API 호출 및 처리
	                try {
	                    // 삭제 요청 생성
	                    const deleteRequests = selectedCodes.map(cmnCode => 
	                        async () => ApiUtil.del(API_URLS.COMMON_CODE.DELETE(cmnCode))
	                    );
	                    
	                    // 일괄 삭제 요청 실행
	                    await ApiUtil.withLoading(async () => {
	                        await Promise.all(deleteRequests.map(req => req()));
	                    }, '공통코드 삭제 중...');
	                    
	                    // 삭제 성공 메시지
	                    await AlertUtil.notifyDeleteSuccess('삭제 완료', '공통코드가 삭제되었습니다.');
	                    
	                    // 목록 갱신
	                    await searchCommonCode();
	                } catch (apiError) {
	                    console.error('삭제 API 호출 중 오류:', apiError);
	                    await AlertUtil.notifyDeleteError('삭제 실패', '공통코드 삭제 중 API 오류가 발생했습니다.');
	                }
	            }
	        });
	        
	        return result;
	    } catch (error) {
	        console.error('공통코드 삭제 오류:', error);
	        await AlertUtil.notifyDeleteError('삭제 실패', '공통코드 삭제 중 오류가 발생했습니다.');
	        return false;
	    }
	}
    /**
     * 상세코드 저장 함수
     * 상세코드 그리드의 변경된 데이터를 저장합니다.
     * 리팩토링: 공통코드 저장과 동일한 패턴으로 일관성 유지
     */
	async function saveCommonCodeDetailData() {
	    try {
	        if (!selectedCommonCode) {
	            await AlertUtil.showWarning('알림', '공통코드를 먼저 선택해주세요');
	            return false;
	        }

	        console.log('상세코드 저장 시작. 공통코드:', selectedCommonCode);

	        const grid = GridUtil.getGrid('commonCodeDetail');
	        if (!grid) {
	            throw new Error('commonCodeDetail 그리드를 찾을 수 없습니다.');
	        }

	        // 마지막으로 입력한 셀에 대한 값 반영을 위해 포커스 해제
	        grid.blur();

	        // 변경된 데이터 추출 - 리팩토링된 GridUtil 사용
	        const changes = await GridUtil.extractChangedData('commonCodeDetail');
	        const modifiedData = [...changes.insert, ...changes.update];

	        if (modifiedData.length === 0) {
	            await AlertUtil.showWarning("알림", "수정된 내용이 없습니다.");
	            return false;
	        }

	        // 저장할 데이터 준비
			const batchData = modifiedData.map(row => ({
			    cmnCode: selectedCommonCode,
			    cmnDetailCode: row.CMN_DETAIL_CODE,
			    cmnDetailName: row.CMN_DETAIL_NAME,
			    cmnDetailContent: row.CMN_DETAIL_CONTENT || '',
			    cmnDetailValue: row.CMN_DETAIL_VALUE || '',
			    cmnDetailCodeIsActive: row.CMN_DETAIL_CODE_IS_ACTIVE,
			    cmnDetailSortOrder: row.CMN_DETAIL_SORT_ORDER || 0,
			    rowType: row.ROW_TYPE  // action을 rowType으로 변경
			}));

			console.log('상세코드 배치 저장 요청 데이터:', JSON.stringify(batchData));

			

	        // 유효성 검사
	        for (const item of batchData) {
	            if (ValidationUtil.isEmpty(item.cmnDetailCode)) {
	                await AlertUtil.notifyValidationError("유효성 오류", "상세코드는 필수입니다.");
	                return false;
	            }
	            if (ValidationUtil.isEmpty(item.cmnDetailName)) {
	                await AlertUtil.notifyValidationError("유효성 오류", "상세코드명은 필수입니다.");
	                return false;
	            }
	            if (ValidationUtil.isEmpty(item.cmnDetailCodeIsActive)) {
	                await AlertUtil.notifyValidationError("유효성 오류", "사용여부는 필수입니다.");
	                return false;
	            }
	        }

	        // 배치 저장 API 사용
	        const apiUrl = API_URLS.COMMON_CODE.DETAIL_BATCH(selectedCommonCode);

	        // 리팩토링된 ApiUtil의 processRequest 사용
	        const response = await ApiUtil.processRequest(
	            () => ApiUtil.post(apiUrl, batchData), {
	                loadingMessage: '상세코드 저장 중...',
	                successMessage: "상세코드가 저장되었습니다.",
	                errorMessage: "상세코드 저장 중 오류가 발생했습니다.",
	                successCallback: () => loadCommonCodeDetailGrid(selectedCommonCode)
	            }
	        );
			if(response.success){
				await AlertUtil.showSuccess('저장 완료', '데이터가 성공적으로 저장되었습니다.');
				return true;
			}else{
				return false;
			}
	    } catch (error) {
	        console.error('상세코드 저장 오류:', error);
	        await AlertUtil.notifySaveError("저장 실패", "상세코드 저장 중 오류가 발생했습니다.");
	        return false;
	    }
	}

    /**
     * 상세코드 삭제 함수
     * 선택된 상세코드 행을 삭제합니다.
     */
	async function deleteCommonCodeDetailRows() {
	    try {
	        if (!selectedCommonCode) {
	            await AlertUtil.showWarning('알림', '공통코드를 먼저 선택해주세요');
	            return false;
	        }

	        console.log('상세코드 삭제 시작. 공통코드:', selectedCommonCode);

	        // 선택된 행 ID 확인
	        const grid = GridUtil.getGrid('commonCodeDetail');
	        const selectedRowKeys = grid.getCheckedRowKeys();
	        
	        if (selectedRowKeys.length === 0) {
	            await AlertUtil.showWarning('알림', '삭제할 항목을 선택해주세요.');
	            return false;
	        }
	        
	        // 선택된 상세코드 목록 생성
	        const selectedDetailCodes = [];
	        for (const rowKey of selectedRowKeys) {
	            const detailCode = grid.getValue(rowKey, "CMN_DETAIL_CODE");
	            if (detailCode) selectedDetailCodes.push(detailCode);
	        }
	        
	        if (selectedDetailCodes.length === 0) {
	            await AlertUtil.showWarning('알림', '유효한 상세코드를 찾을 수 없습니다.');
	            return false;
	        }
	        
	        // GridUtil.deleteSelectedRows 사용 (UI 측면의 삭제 확인 및 행 제거)
	        const result = await GridUtil.deleteSelectedRows('commonCodeDetail', {
	            confirmTitle: "삭제 확인",
	            confirmMessage: "선택한 상세코드를 삭제하시겠습니까?",
	            onBeforeDelete: async () => {
	                // 삭제 전 처리 - 여기서 true 반환해야 삭제 진행
	                return true;
	            },
	            onAfterDelete: async () => {
	                // 삭제 API 호출 및 처리
	                try {
	                    // 삭제 요청 생성
	                    const deleteRequests = selectedDetailCodes.map(detailCode => 
	                        async () => ApiUtil.del(API_URLS.DETAIL_CODE.DELETE(selectedCommonCode, detailCode))
	                    );
	                    
	                    // 일괄 삭제 요청 실행
	                    await ApiUtil.withLoading(async () => {
	                        await Promise.all(deleteRequests.map(req => req()));
	                    }, '상세코드 삭제 중...');
	                    
	                    // 삭제 성공 메시지
	                    await AlertUtil.notifyDeleteSuccess('삭제 완료', '상세코드가 삭제되었습니다.');
	                    
	                    // 목록 갱신
	                    await loadCommonCodeDetailGrid(selectedCommonCode);
	                } catch (apiError) {
	                    console.error('삭제 API 호출 중 오류:', apiError);
	                    await AlertUtil.notifyDeleteError('삭제 실패', '상세코드 삭제 중 API 오류가 발생했습니다.');
	                }
	            }
	        });
	        
	        return result;
	    } catch (error) {
	        console.error('상세코드 삭제 오류:', error);
	        await AlertUtil.notifyDeleteError('삭제 실패', '상세코드 삭제 중 오류가 발생했습니다.');
	        return false;
	    }
	}
    // =============================
    // 유틸리티 함수
    // =============================

    /**
     * 현재 선택된 공통코드 반환 함수
     * 외부에서 현재 선택된 공통코드를 조회할 수 있습니다.
     * 
     * @returns {string} 선택된 공통코드
     */
    function getSelectedCommonCode() {
        return selectedCommonCode;
    }

    /**
     * 공통코드 그리드 인스턴스 반환 함수
     * 외부에서 공통코드 그리드 인스턴스에 직접 접근할 수 있습니다.
     * 
     * @returns {Object} 공통코드 그리드 인스턴스
     */
    function getCommonCodeGrid() {
        return commonCodeGrid;
    }

    /**
     * 상세코드 그리드 인스턴스 반환 함수
     * 외부에서 상세코드 그리드 인스턴스에 직접 접근할 수 있습니다.
     * 
     * @returns {Object} 상세코드 그리드 인스턴스
     */
    function getCommonCodeDetailGrid() {
        return commonCodeDetailGrid;
    }

    /**
     * 검색 조건 저장 함수
     * 현재 검색 조건을 로컬 스토리지에 저장합니다.
     */
    async function saveSearchCondition() {
        try {
            const commonCodeCondition = document.getElementById('commonCodeInput') ?.value || '';
            const detailCodeCondition = document.getElementById('commonCodeDetailInput') ?.value || '';

            const searchCondition = {
                commonCode: commonCodeCondition,
                detailCode: detailCodeCondition,
                selectedCode: selectedCommonCode
            };

            localStorage.setItem('commonCodeSearchCondition', JSON.stringify(searchCondition));
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
            const savedData = localStorage.getItem('commonCodeSearchCondition');

            if (!savedData) {
                console.log('저장된 검색 조건이 없습니다.');
                return false;
            }

            const searchCondition = JSON.parse(savedData);

            // 검색 조건 설정
            const commonCodeInput = document.getElementById('commonCodeInput');
            const detailCodeInput = document.getElementById('commonCodeDetailInput');

            if (commonCodeInput && searchCondition.commonCode) {
                commonCodeInput.value = searchCondition.commonCode;
            }

            if (detailCodeInput && searchCondition.detailCode) {
                detailCodeInput.value = searchCondition.detailCode;
            }

            // 공통코드 검색 실행
            if (searchCondition.commonCode) {
                await searchCommonCode();
            }

            console.log('검색 조건이 로드되었습니다.');
            return true;
        } catch (error) {
            console.error('검색 조건 로드 오류:', error);
            await AlertUtil.showError('로드 오류', '검색 조건 로드 중 오류가 발생했습니다.');
            return false;
        }
    }

    // =============================
    // 공개 API - 외부에서 접근 가능한 메서드
    // =============================
    return {
        // 초기화 및 기본 기능
        init, // 모듈 초기화

        // 공통코드 관련 함수
        searchCommonCode, // 공통코드 검색
        appendCommonCodeRow, // 공통코드 행 추가
        saveCommonCodeData, // 공통코드 저장
        deleteCommonCodeRows, // 공통코드 삭제

        // 상세코드 관련 함수
        searchCommonCodeDetail, // 상세코드 검색
        appendCommonCodeDetailRow, // 상세코드 행 추가
        saveCommonCodeDetailData, // 상세코드 저장
        deleteCommonCodeDetailRows, // 상세코드 삭제

        // 유틸리티 함수
        getSelectedCommonCode, // 선택된 공통코드 반환
        getCommonCodeGrid, // 공통코드 그리드 인스턴스 반환
        getCommonCodeDetailGrid, // 상세코드 그리드 인스턴스 반환
        saveSearchCondition, // 검색 조건 저장
        loadSearchCondition // 저장된 검색 조건 로드
    };
})();

// =============================
// DOM 로드 시 초기화
// =============================
document.addEventListener('DOMContentLoaded', async function() {
    try {
        // 공통코드 관리자 초기화
        await CommonCodeManager.init();

        // 저장된 검색 조건 로드 (필요 시 활성화)
        // await CommonCodeManager.loadSearchCondition();
    } catch (error) {
        console.error('초기화 중 오류 발생:', error);
        if (window.AlertUtil) {
            await AlertUtil.showError('초기화 오류', '공통코드 관리자 초기화 중 오류가 발생했습니다.');
        } else {
            alert('공통코드 관리자 초기화 중 오류가 발생했습니다.');
        }
    }
});