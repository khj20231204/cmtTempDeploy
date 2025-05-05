/**
 * DocumentList - 전자결재 문서함 관리 모듈
 *
 * 전자결재 문서함 페이지의 기능을 담당합니다.
 * - 탭별 문서 목록 표시 및 관리
 * - 문서 검색 및 필터링 기능
 * - 그리드 기반 문서 목록 제공
 * - 문서 관련 이벤트 처리
 *
 * @version 1.3.0
 * @since 2025-04-04
 * @update 2025-04-04 - API 응답 처리 리팩토링 및 대문자 키 일관성 개선
 */
const DocumentList = (function() {
    //===========================================================================
    // 모듈 내부 변수
    //===========================================================================
    
    /**
     * 그리드 인스턴스 객체
     * 각 탭별 그리드 인스턴스를 관리합니다.
     */
    const gridInstances = {};
    
    /**
     * 현재 활성화된 탭 ID
     * 현재 선택된 탭을 추적합니다.
     */
    let activeTabId = 'mydraft';
    
    /**
     * API URL 상수 정의
     * 문서 목록 관련 API 엔드포인트 정의
     */
    const API_URLS = {
        DRAFTS: '/api/eapproval/drafts',             // 내 기안문서 조회 API
        PENDING: '/api/eapproval/pending',           // 결재대기 문서 조회 API
		PROCESSING: '/api/eapproval/documents/status/진행중', // 진행중 문서 조회 API
        COMPLETED: '/api/eapproval/documents/status/완료',  // 완료 문서 조회 API
        REJECTED: '/api/eapproval/documents/status/반려'    // 반려 문서 조회 API
    };
    
    //===========================================================================
    // 초기화 및 이벤트 처리 함수
    //===========================================================================
    
    /**
     * 모듈 초기화 함수
     * 문서함 초기화 및 이벤트 등록을 수행합니다.
     * 
     * @returns {Promise<void>}
     */
    async function initialize() {
        try {
            console.log('DocumentList 초기화를 시작합니다.');
            
            // 탭 이벤트 초기화
            await initializeTabs();
            
            // 그리드 초기화
            await initializeGrids();
            
            // 이벤트 리스너 등록
            await setupEventListeners();
            
            // 성공 메시지 확인 (있는 경우)
            checkSuccessMessage();
            
            // 초기 활성 탭 데이터 로드
            const activeTab = document.querySelector('#documentTab button.active');
            if (activeTab) {
                const targetId = activeTab.getAttribute('data-bs-target').substring(1);
                activeTabId = targetId;
                await refreshGridData(targetId);
            }
            
            console.log('DocumentList 초기화가 완료되었습니다.');
        } catch (error) {
            console.error('초기화 중 오류 발생:', error);
            await AlertUtil.showError('초기화 오류', '페이지 초기화 중 오류가 발생했습니다.');
        }
    }
    
    /**
     * 탭 이벤트 초기화 함수
     * Bootstrap 탭 이벤트를 초기화하고 관련 핸들러를 등록합니다.
     * 
     * @returns {Promise<void>}
     */
    async function initializeTabs() {
        try {
            console.log('탭 이벤트 초기화를 시작합니다.');
            
            // Bootstrap 5 탭 이벤트 처리
            const tabElements = document.querySelectorAll('#documentTab button[data-bs-toggle="tab"]');
            
            tabElements.forEach(tab => {
                tab.addEventListener('shown.bs.tab', function(event) {
                    // 탭 ID 추출 (예: #mydraft → mydraft)
                    const targetId = event.target.getAttribute('data-bs-target').substring(1);
                    console.log(`탭 전환: ${targetId}`);
                    
                    // 활성 탭 ID 업데이트
                    activeTabId = targetId;
                    
                    // 해당 탭의 그리드를 무조건 새로고침
                    refreshGridData(targetId);
                });
            });
            
            console.log('탭 이벤트 초기화가 완료되었습니다.');
        } catch (error) {
            console.error('탭 이벤트 초기화 중 오류:', error);
            throw error;
        }
    }
    
    /**
     * 이벤트 리스너 등록 함수
     * 버튼 클릭 및 기타 이벤트 리스너를 등록합니다.
     * 
     * @returns {Promise<void>}
     */
    async function setupEventListeners() {
        try {
            console.log('이벤트 리스너 등록을 시작합니다.');
            
            // 검색 버튼
            const searchBtn = document.getElementById('searchBtn');
            if (searchBtn) {
                searchBtn.addEventListener('click', performSearch);
                console.log('검색 버튼 이벤트 등록 완료');
            }
            
            // 초기화 버튼
            const resetBtn = document.getElementById('resetBtn');
            if (resetBtn) {
                resetBtn.addEventListener('click', resetSearch);
                console.log('초기화 버튼 이벤트 등록 완료');
            }
            
            // 문서 작성 버튼
            const createBtn = document.getElementById('createBtn');
            if (createBtn) {
                createBtn.addEventListener('click', function() {
                    window.location.href = '/eapproval/document/new';
                });
                console.log('문서 작성 버튼 이벤트 등록 완료');
            }
            
            // 검색 필드 엔터키 이벤트
            const keywordInput = document.getElementById('keyword');
            if (keywordInput) {
                keywordInput.addEventListener('keypress', function(e) {
                    if (e.key === 'Enter') {
                        e.preventDefault();
                        performSearch();
                    }
                });
                console.log('검색 필드 엔터키 이벤트 등록 완료');
            }
            
            // 필터 변경 시 실시간 검색 (선택적)
            const docStatusSelect = document.getElementById('docStatus');
            if (docStatusSelect) {
                docStatusSelect.addEventListener('change', performSearch);
                console.log('상태 필터 변경 이벤트 등록 완료');
            }
            
            console.log('이벤트 리스너 등록이 완료되었습니다.');
        } catch (error) {
            console.error('이벤트 리스너 등록 중 오류:', error);
            throw error;
        }
    }
    
    /**
     * 성공 메시지 확인 및 표시 함수
     * Thymeleaf나 URL 파라미터로 전달된 성공 메시지를 확인하고 표시합니다.
     */
    function checkSuccessMessage() {
        try {
            console.log('성공 메시지 확인을 시작합니다.');
            
            // Thymeleaf에서 전달된 성공 메시지 확인
            const successMetaElement = document.querySelector('meta[name="successMessage"]');
            const successMessage = successMetaElement ? successMetaElement.content : null;
            
            if (successMessage) {
                AlertUtil.showSuccess('성공', successMessage);
                console.log(`Thymeleaf 성공 메시지 표시: ${successMessage}`);
                return;
            }
            
            // URL 파라미터에서 메시지 확인
            const urlParams = new URLSearchParams(window.location.search);
            const msgParam = urlParams.get('msg');
            
            if (msgParam) {
                AlertUtil.showSuccess('성공', decodeURIComponent(msgParam));
                console.log(`URL 파라미터 성공 메시지 표시: ${msgParam}`);
                return;
            }
            
            console.log('표시할 성공 메시지가 없습니다.');
        } catch (error) {
            console.error('성공 메시지 확인 중 오류:', error);
            // 비 중요 기능이므로 오류가 발생해도 계속 진행
        }
    }
    
    //===========================================================================
    // 그리드 관리 함수
    //===========================================================================
    
    /**
     * 그리드 초기화 함수
     * 모든 탭에 대한 그리드를 초기화합니다.
     * 
     * @returns {Promise<void>}
     */
    async function initializeGrids() {
        try {
            console.log('그리드 초기화를 시작합니다.');
            
            // 초기 활성 탭 확인
            const activeTab = document.querySelector('#documentTab button.active');
            if (activeTab) {
                const targetId = activeTab.getAttribute('data-bs-target').substring(1);
                activeTabId = targetId;
                
                const gridId = getGridIdByTabId(targetId);
                await initializeGrid(gridId, []);
            } else {
                // 기본적으로 내 기안문서 탭 초기화
                await initializeGrid('myDraftGrid', []);
            }
            
            console.log('그리드 초기화가 완료되었습니다.');
        } catch (error) {
            console.error('그리드 초기화 중 오류:', error);
            throw error;
        }
    }
    
    /**
     * 개별 그리드 초기화 함수
     * 지정된 그리드 ID에 해당하는 그리드를 초기화합니다.
     * 
     * @param {string} gridId - 그리드 요소 ID
     * @param {Array} data - 그리드 초기 데이터
     * @returns {Promise<void>}
     */
    async function initializeGrid(gridId, data) {
        try {
            console.log(`그리드 초기화 시작: ${gridId}, 초기 데이터:`, data);
            
            // 그리드 요소 확인
            const gridElement = document.getElementById(gridId);
            if (!gridElement) {
                console.warn(`그리드 요소 없음: ${gridId}`);
                return;
            }
            
            // 그리드 이미 초기화되었는지 확인
            if (gridInstances[gridId]) {
                console.log(`기존 그리드 재사용: ${gridId}`);
                gridInstances[gridId].resetData(data || []);
                return;
            }
			
            // TOAST UI Grid 열 정의
            const columns = [
                {
                    header: '문서번호',
                    name: 'DOC_NUMBER', 
                    width: 150,
                    align: 'center'
                },
                {
                    header: '양식',
                    name: 'FORM_ID', 
                    width: 120,
                    align: 'center'
                },
                {
                    header: '제목',
                    name: 'TITLE', 
                    width: 'auto',
                    minWidth: 200
                },
                {
                    header: '기안자',
                    name: 'DRAFTER_NAME', 
                    width: 100,
                    align: 'center'
                },
                {
                    header: '기안부서',
                    name: 'DRAFT_DEPT_NAME', 
                    width: 120,
                    align: 'center'
                },
                {
                    header: '기안일자',
                    name: 'DRAFT_DATE', 
                    width: 120,
                    align: 'center',
                    formatter: function(obj) {
                        if (!obj.value) return '-';
                        const date = new Date(obj.value);
                        return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
                    }
                },
                {
                    header: '문서상태',
                    name: 'DOC_STATUS', 
                    width: 100,
                    align: 'center',
                    formatter: function(obj) {
                        let statusClass = '';
                        let statusText = obj.value || '';
                        
                        switch (statusText) {
                            case '진행중':
                                statusClass = 'badge bg-primary';
                                break;
                            case '완료':
                                statusClass = 'badge bg-success';
                                break;
                            case '반려':
                                statusClass = 'badge bg-danger';
                                break;
                            case '임시저장':
                                statusClass = 'badge bg-secondary';
                                break;
                            default:
                                statusClass = 'badge bg-secondary';
                        }
                        
                        return `<span class="${statusClass}">${statusText}</span>`;
                    }
                }
            ];
            
            // 그리드 옵션
            const gridOptions = {
                el: gridElement,
                data: data || [],
                columns: columns,
                rowHeaders: ['rowNum'],
                bodyHeight: 500,
                minBodyHeight: 300,
                scrollX: true,
                scrollY: true,
                pageOptions: {
                    useClient: true,
                    perPage: 15
                },
                onGridMounted: function() {
                    console.log(`${gridId} 그리드가 마운트되었습니다.`);
                }
            };
            
            // TOAST UI Grid 인스턴스 생성
            const Grid = tui.Grid;
            const gridInstance = new Grid(gridOptions);
            
            // 행 더블클릭 이벤트 처리 - 상세 페이지로 이동
            gridInstance.on('dblclick', (ev) => {
                if (ev.rowKey === undefined || ev.rowKey === null) return;
                
                const rowData = gridInstance.getRow(ev.rowKey);
                if (rowData && rowData.DOC_ID) { 
                    window.location.href = `/eapproval/document/view/${rowData.DOC_ID}`;
                }
            });
            
            // 그리드 인스턴스 저장
            gridInstances[gridId] = gridInstance;
            
            console.log(`그리드 초기화 완료: ${gridId}`);
        } catch (error) {
            console.error(`그리드 초기화 오류(${gridId}):`, error);
            throw error;
        }
    }

    /**
     * 그리드 데이터 새로고침 함수
     * 지정된 탭에 해당하는 그리드 데이터를 새로고침합니다.
     * 
     * @param {string} tabId - 탭 ID
     * @returns {Promise<void>}
     */
    async function refreshGridData(tabId) {
        try {
            console.log(`그리드 데이터 새로고침 시작: ${tabId}`);
            
            const gridId = getGridIdByTabId(tabId);
            let grid = gridInstances[gridId];
            
            // 그리드가 없는 경우 초기화
            if (!grid) {
                console.log(`그리드 초기화 필요: ${gridId}`);
                await initializeGrid(gridId, []);
                grid = gridInstances[gridId];
                if (!grid) {
                    console.warn(`그리드 인스턴스 없음: ${gridId}`);
                    return;
                }
            }
            
            try {
                // 검색 파라미터 구성
                const searchParams = new URLSearchParams();
                
                // 문서 상태 필터
                const docStatus = document.getElementById('docStatus')?.value;
                if (docStatus) {
                    searchParams.append('docStatus', docStatus);
                }
                
                // 검색어와 검색 유형
                const searchType = document.getElementById('searchType')?.value;
                const keyword = document.getElementById('keyword')?.value;
                if (searchType && keyword) {
                    searchParams.append(searchType, keyword);
                }
                
                // API 엔드포인트 결정
                let apiUrl;
                switch (tabId) {
                    case 'mydraft':
                        apiUrl = API_URLS.DRAFTS;
                        break;
                    case 'pending':
                        apiUrl = API_URLS.PENDING;
                        break;
					case 'processing':
						apiUrl = API_URLS.PROCESSING;
						break;
                    case 'completed':
                        apiUrl = API_URLS.COMPLETED;
                        break;
                    case 'rejected':
                        apiUrl = API_URLS.REJECTED;
                        break;
                    default:
                        apiUrl = API_URLS.DRAFTS;
                }
                
                // API 호출 - 리팩토링된 ApiUtil 사용
                const response = await ApiUtil.getWithLoading(
                    `${apiUrl}?${searchParams.toString()}`,
                    null,
                    '문서 목록 로딩 중...'
                );
                
                // 응답 처리 개선
                if (!response.success) {
                    throw new Error(response.message || '문서 조회 중 오류가 발생했습니다.');
                }
                
                console.log('API 응답 데이터:', response);
                
                // 데이터 추출 - 가이드라인에 따라 대문자 키 사용
                let gridData = [];
                if (response.data) {
                    // 배열 형태인지 확인
                    if (Array.isArray(response.data)) {
                        gridData = response.data;
                    }
                }
                
                // 데이터가 없어도 빈 배열로 그리드 업데이트
                grid.resetData(gridData || []);
                console.log(`${tabId} 탭 데이터 새로고침 완료: ${gridData.length}건 로드됨`);
            } catch (error) {
                console.error('문서 목록 조회 오류:', error);
                await ApiUtil.handleApiError(error, '문서 목록 조회 실패');
                
                // 빈 데이터로 그리드 업데이트
                grid.resetData([]);
            }
        } catch (error) {
            console.error('그리드 데이터 새로고침 중 오류:', error);
            await AlertUtil.showError('조회 오류', '문서 목록 조회 중 오류가 발생했습니다.');
        }
    }
    
    //===========================================================================
    // 검색 및 필터링 함수
    //===========================================================================
    
    /**
     * 검색 수행 함수
     * 현재 활성 탭에 대한 검색을 수행합니다.
     * 
     * @returns {Promise<void>}
     */
    async function performSearch() {
        try {
            console.log('검색 수행 시작');
            
            // 유효성 검사
            const activeTab = document.querySelector('#documentTab button.active');
            if (!activeTab) {
                console.warn('활성 탭을 찾을 수 없습니다.');
                return;
            }
            
            const targetId = activeTab.getAttribute('data-bs-target').substring(1);
            console.log(`검색 수행: 탭=${targetId}`);
            
            // 그리드 데이터 새로고침
            await refreshGridData(targetId);
        } catch (error) {
            console.error('검색 수행 중 오류:', error);
            await AlertUtil.showError('검색 오류', '검색 중 오류가 발생했습니다.');
        }
    }
    
    /**
     * 검색 필드 초기화 함수
     * 검색 조건을 초기화하고 목록을 새로고침합니다.
     * 
     * @returns {Promise<void>}
     */
    async function resetSearch() {
        try {
            console.log('검색 필드 초기화 시작');
            
            // 검색 필드 초기화
            const docStatusSelect = document.getElementById('docStatus');
            if (docStatusSelect) {
                docStatusSelect.value = '';
            }
            
            const searchTypeSelect = document.getElementById('searchType');
            if (searchTypeSelect) {
                searchTypeSelect.selectedIndex = 0;
            }
            
            const keywordInput = document.getElementById('keyword');
            if (keywordInput) {
                keywordInput.value = '';
            }
            
            console.log('검색 필드 초기화 완료');
            
            // 검색 수행
            await performSearch();
        } catch (error) {
            console.error('검색 필드 초기화 중 오류:', error);
            await AlertUtil.showError('초기화 오류', '검색 필드 초기화 중 오류가 발생했습니다.');
        }
    }
    
    //===========================================================================
    // 유틸리티 함수
    //===========================================================================
    
    /**
     * 탭 ID에 해당하는 그리드 ID 반환 함수
     * 
     * @param {string} tabId - 탭 ID
     * @returns {string} 그리드 ID
     */
    function getGridIdByTabId(tabId) {
        switch (tabId) {
            case 'mydraft': return 'myDraftGrid';
            case 'pending': return 'pendingGrid';
			case 'processing': return 'processingGrid';
            case 'completed': return 'completedGrid';
            case 'rejected': return 'rejectedGrid';
            default: return 'myDraftGrid';
        }
    }
    
    /**
     * 그리드 인스턴스 반환 함수
     * 
     * @param {string} gridId - 그리드 ID
     * @returns {Object|null} 그리드 인스턴스
     */
    function getGridInstance(gridId) {
        return gridInstances[gridId] || null;
    }
    
    //===========================================================================
    // 공개 API - 외부에서 접근 가능한 메서드
    //===========================================================================
    
    return {
        // 초기화 및 기본 기능
        initialize,          // 모듈 초기화
        
        // 검색 및 필터링 함수
        performSearch,       // 검색 수행
        resetSearch,         // 검색 초기화
        
        // 그리드 관련 함수
        refreshGridData,     // 그리드 데이터 새로고침
        getGridInstance      // 그리드 인스턴스 조회
    };
})();

// DOM 로드 시 초기화
document.addEventListener('DOMContentLoaded', async function() {
    try {
        // 모듈 초기화
        await DocumentList.initialize();
    } catch (error) {
        console.error('초기화 중 오류 발생:', error);
        if (window.AlertUtil) {
            await AlertUtil.showError('초기화 오류', '페이지 초기화 중 오류가 발생했습니다.');
        } else {
            alert('페이지 초기화 중 오류가 발생했습니다.');
        }
    }
});