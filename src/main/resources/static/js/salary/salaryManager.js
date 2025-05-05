/**
 * SimpleGridManager - 단일 그리드 관리 모듈 기본 템플릿
 * 
 * 이 템플릿은 CRUD 기능을 가진 단일 그리드 모듈을 쉽게 구현할 수 있는 기본 골격입니다.
 * REST API와 일반 컨트롤러 방식 모두 지원합니다.
 * 
 * @version 1.1.0
 *  변경 사항 서버에서 데이터 받아오는 부분 누락으로 추가
 */
const SimpleGridManager = (function() {
    //===========================================================================
    // 모듈 내부 변수 - 필요에 맞게 수정하세요.
    //===========================================================================

    // 그리드 인스턴스 참조 - 그대로 사용하세요
    let gridInstance;

    // API URL 상수 정의 
    // [✓] 수정 필요: 프로젝트에 맞는 방식을 선택하고 URL을 변경하세요
   
    
    // 방법 2: 일반 컨트롤러 방식 (사용할 경우 주석 해제)
    const API_URLS = {
        LIST: '/salary/salaryList',       // 목록 조회 컨트롤러 URL
    };
    

    //===========================================================================
    // 초기화 및 이벤트 처리 함수 - 기본 구조는 유지하고 필요시 확장하세요
    //===========================================================================

    /**
     * 모듈 초기화 함수
     * 그리드 초기화 및 이벤트 등록을 수행합니다.
     * 
     * [✓] 수정 방법: init 함수 자체는 수정하지 않고, 호출하는 함수들을 수정하세요.
     */
    async function init() {
        try {
            console.log('모듈 초기화를 시작합니다.');

            // 그리드 초기화 - initGrid 함수에서 그리드 컬럼 등을 수정하세요
            await initGrid();

            // 이벤트 리스너 등록 - registerEvents 함수에서 필요한 이벤트를 추가하세요
            await registerEvents();

            console.log('모듈 초기화가 완료되었습니다.');
        } catch (error) {
            console.error('초기화 중 오류 발생:', error);
            await AlertUtil.showError('초기화 오류', '모듈 초기화 중 오류가 발생했습니다.');
        }
    }

    /**
     * 이벤트 리스너 등록 함수
     * 버튼 클릭 및 엔터키 검색 등의 이벤트 리스너를 등록합니다.
     * 
     * [✓] 수정 방법: 버튼 ID와 연결할 함수를 수정하세요.
     * 예: 'exportBtn': exportData와 같이 새로운 버튼과 함수를 연결할 수 있습니다.
     */
    async function registerEvents() {
        try {
            // UIUtil을 사용하여 이벤트 리스너 등록
            // HTML에 있는 버튼 ID를 왼쪽에, 연결할 함수를 오른쪽에 작성합니다.
            await UIUtil.registerEventListeners({
//                'deleteBtn': deleteRows,    // 삭제 버튼
                
            });

        } catch (error) {
            console.error('이벤트 리스너 등록 중 오류:', error);
            throw error;
        }
    }

    /**
     * 그리드 초기화 함수
     * 그리드를 생성하고 초기 데이터를 로드합니다.
     * 
     * [✓] 수정 필요: 프로젝트에 맞게 그리드 컬럼을 수정하세요.
     */
	/**
	 * 그리드 초기화 함수
	 * 그리드를 생성하고 초기 데이터를 로드합니다.
	 * 
	 * [✓] 수정 필요: 프로젝트에 맞게 그리드 컬럼을 수정하세요.
	 */
	async function initGrid() {
	    try {
	        console.log('그리드 초기화를 시작합니다.');

	        // DOM 요소 존재 확인
	        // [✓] 수정 방법: HTML의 그리드 요소 ID가 'salaryGrid'가 아니라면 여기를 변경하세요.
	        const gridElement = document.getElementById('salaryGrid');
	        if (!gridElement) {
	            throw new Error('salaryGrid 요소를 찾을 수 없습니다. HTML을 확인해주세요.');
	        }

	        // [✓] 서버에서 데이터 가져오기
	        // 서버에서 받은 데이터 활용 (Thymeleaf를 통해 설정된 전역 변수)
	        // window.dataList 변수는 HTML 페이지의 Thymeleaf 템플릿에서 설정해야 합니다
	        const gridData = window.salaryList || [];
	        console.log('초기 데이터:', gridData.length, '건');

	        // 그리드 컬럼 정의
	        // [✓] 수정 필요: 프로젝트에 필요한 컬럼으로 변경하세요.
	        const columns = [
	            {
	                header: '지급일',           // 컬럼 헤더 텍스트
	                name: '',            // 데이터 필드명 (대소문자 주의)
	                editor: 'text'           // 에디터 타입 ('text', 'checkbox', 'select' 등)
	            },
				{
					header: '지급상태',           
					name: '',            
					editor: 'text'           
				},
	            {
	                header: '사원번호',           
	                name: '',            
	                editor: 'text'           
	            },
				{
				    header: '사원명',           
				    name: '',            
				    editor: 'text'           
				},
				{
				    header: '부서',           
				    name: '',            
				    editor: 'text'           
				},
				{
				    header: '직위',           
				    name: '',            
				    editor: 'text'           
				},
				{
				    header: '기본급',           
				    name: '',            
				    editor: 'text'           
				},
				{
				    header: '야근수당',           
				    name: '',            
				    editor: 'text'           
				},
				{
				    header: '기술수당',           
				    name: '',            
				    editor: 'text'           
				},
				{
				    header: '근속수당',           
				    name: '',            
				    editor: 'text'           
				},
				{
				    header: '명절수당',           
				    name: '',            
				    editor: 'text'           
				},
				{
				    header: '휴가수당',           
				    name: '',            
				    editor: 'text'           
				},
				{
				    header: '성과급',           
				    name: '',            
				    editor: 'text'           
				},
				{
				    header: '총수당액',           
				    name: '',            
				    editor: 'text'           
				},
				{
				    header: '국민연금',           
				    name: '',            
				    editor: 'text'           
				},
				{
				    header: '장기요양보험',           
				    name: '',            
				    editor: 'text'           
				},
				{
				    header: '건강보험',           
				    name: '',            
				    editor: 'text'           
				},
				{
				    header: '고용보험',           
				    name: '',            
				    editor: 'text'           
				},
				{
				    header: '소득세',           
				    name: '',            
				    editor: 'text'           
				},
				{
				    header: '주민세',           
				    name: '',            
				    editor: 'text'           
				},
				{
				    header: '총공제액',           
				    name: '',            
				    editor: 'text'           
				},
				{
				    header: '실지급액',           
				    name: '',            
				    editor: 'text'           
				},
	            {
	                header: '타입',
	                name: 'ROW_TYPE'         // ROW_TYPE은 필수 필드입니다 (insert, update, select 구분)
	            }
	        ];

	        // 그리드 생성 - GridUtil 사용
	        // [✓] 수정 방법: 필요에 따라 옵션을 조정하세요.
	        gridInstance = GridUtil.registerGrid({
	            id: 'salaryGrid',              // HTML 그리드 요소 ID
	            columns: columns,            // 위에서 정의한 컬럼
	            data: gridData,              // 서버에서 받은 초기 데이터 사용
	            //draggable: true,             // 드래그 가능 여부 (false로 변경 가능)
	            displayColumnName: 'SORT_ORDER', // 드래그 시 자동 정렬에 사용할 컬럼명
	            hiddenColumns: ['ROW_TYPE'], // 숨길 컬럼명 배열
	            gridOptions: {
	                rowHeaders: ['checkbox'], // 행 헤더 옵션 
	                // [✓] 확장 지점: 그리드 옵션을 추가하려면 여기에 작성하세요
	                // header: {
	                //     height: 40,        // 헤더 높이
	                //     complexColumns: [] // 복합 컬럼 정의
	                // },
	                // bodyHeight: 400,       // 그리드 본문 높이 (자동 스크롤)
	                // minBodyHeight: 200     // 최소 본문 높이
	            }
	        });

	        // 초기 데이터가 없는 경우에만 API를 통해 데이터 로드
	        if (gridData.length === 0) {
	            await searchData();
	        }

	        console.log('그리드 초기화가 완료되었습니다.');
	    } catch (error) {
	        console.error('그리드 초기화 오류:', error);
	        throw error;
	    }
	}

    //===========================================================================
    // 데이터 처리 함수 - 비즈니스 로직에 맞게 수정하세요
    //===========================================================================

    /**
     * 행 삭제 함수
     * 선택된 행을 삭제합니다.
     * 
     * [✓] 수정 필요: 삭제 API 호출 부분을 프로젝트에 맞게 수정하세요.
     */
    async function deleteRows() {
        try {
            // 선택된 행 ID 확인
            const grid = GridUtil.getGrid('salaryGrid');
            const selectedRowKeys = grid.getCheckedRowKeys();
            
            if (selectedRowKeys.length === 0) {
                await AlertUtil.showWarning('알림', '삭제할 항목을 선택해주세요.');
                return false;
            }
            
            // 선택된 코드 목록 생성
            // [✓] 수정 필요: 삭제 시 사용할 키 필드명을 변경하세요. (기본값: 'CODE')
            const selectedCodes = [];
            for (const rowKey of selectedRowKeys) {
                const code = grid.getValue(rowKey, "CODE");
                if (code) selectedCodes.push(code);
            }
            
            if (selectedCodes.length === 0) {
                await AlertUtil.showWarning('알림', '유효한 코드를 찾을 수 없습니다.');
                return false;
            }
            
            // 행 삭제 UI 처리 (두 방식 모두 동일)
            const result = await GridUtil.deleteSelectedRows('salaryGrid', {
                confirmTitle: "삭제 확인",
                confirmMessage: "선택한 항목을 삭제하시겠습니까?",
                onBeforeDelete: async () => {
                    // [✓] 확장 지점: 삭제 전 검증이 필요한 경우 여기에 작성하세요
                    // 예: 사용 중인 항목 검증
                    // const usageCheck = await checkItemUsage(selectedCodes);
                    // if (usageCheck.inUse) {
                    //     await AlertUtil.showWarning('삭제 불가', '사용 중인 항목은 삭제할 수 없습니다.');
                    //     return false;
                    // }
                    return true; // 삭제 진행
                },
                onAfterDelete: async () => {
                    // [✓] 수정 필요: API 호출 코드를 프로젝트에 맞게 수정하세요
                    
                    // 방법 1: REST API 방식 (각 항목별로 DELETE 요청)
                    try {
                        const deleteRequests = selectedCodes.map(code => 
                            async () => ApiUtil.del(API_URLS.DELETE(code))
                        );
                        
                        await ApiUtil.withLoading(async () => {
                            await Promise.all(deleteRequests.map(req => req()));
                        }, '데이터 삭제 중...');
                        
                        await AlertUtil.notifyDeleteSuccess('삭제 완료', '데이터가 삭제되었습니다.');
                        await searchData();
                    } catch (apiError) {
                        console.error('삭제 API 호출 중 오류:', apiError);
                        await AlertUtil.notifyDeleteError('삭제 실패', '데이터 삭제 중 API 오류가 발생했습니다.');
                    }
                    
                    /*
                    // 방법 2: 일반 컨트롤러 방식 (여러 코드를 한 번에 전송)
                    try {
                        const response = await ApiUtil.postWithLoading(
                            API_URLS.DELETE,
                            { codeList: selectedCodes },
                            '데이터 삭제 중...'
                        );
                        
                        if (response.success) {
                            await AlertUtil.notifyDeleteSuccess('삭제 완료', '데이터가 삭제되었습니다.');
                            await searchData();
                        } else {
                            await AlertUtil.notifyDeleteError('삭제 실패', response.message || '데이터 삭제 중 오류가 발생했습니다.');
                        }
                    } catch (apiError) {
                        console.error('삭제 API 호출 중 오류:', apiError);
                        await AlertUtil.notifyDeleteError('삭제 실패', '데이터 삭제 중 API 오류가 발생했습니다.');
                    }
                    */
                }
            });
            
            return result;
        } catch (error) {
            console.error('데이터 삭제 오류:', error);
            await AlertUtil.notifyDeleteError('삭제 실패', '데이터 삭제 중 오류가 발생했습니다.');
            return false;
        }
    }

    /**
     * 그리드 인스턴스 반환 함수
     * 외부에서 그리드 인스턴스에 직접 접근할 수 있습니다.
     * 
     * [✓] 수정 방법: 이 함수는 수정할 필요가 없습니다.
     */
    function getGridInstance() {
        return gridInstance;
    }
    

    //===========================================================================
    // 공개 API - 외부에서 접근 가능한 메서드
    //===========================================================================
    // [✓] 수정 방법: 외부에 공개할 함수를 추가하거나 제거하세요.
    return {
        // 초기화 및 기본 기능
        init,                // 모듈 초기화
        
        // 데이터 처리 함수
        deleteRows,          // 행 삭제
        
        // 유틸리티 함수
        getGridInstance      // 그리드 인스턴스 반환
        
        // [✓] 확장 지점: 외부에 공개할 추가 함수를 여기에 작성하세요
        // exportToCsv          // CSV 내보내기 (구현 후 주석 해제)
    };
})();

//===========================================================================
// DOM 로드 시 초기화 - 이 부분은 수정하지 마세요
//===========================================================================
document.addEventListener('DOMContentLoaded', async function() {
    try {
        // 모듈 초기화
        await SimpleGridManager.init();
    } catch (error) {
        console.error('초기화 중 오류 발생:', error);
        if (window.AlertUtil) {
            await AlertUtil.showError('초기화 오류', '모듈 초기화 중 오류가 발생했습니다.');
        } else {
            alert('모듈 초기화 중 오류가 발생했습니다.');
        }
    }
});