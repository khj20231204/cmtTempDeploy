/**
 * docFormList.js - 문서 양식 관리 스크립트
 * 
 * 문서 양식 목록 조회 및 관리 기능을 제공합니다.
 * - 양식 목록 표시 및 관리
 * - 조회, 등록, 수정, 삭제 기능
 * - 검색 기능
 * 
 * @version 1.0.0
 */

// 문서 양식 목록 관리 모듈
const DocFormList = (function() {
    // 그리드 인스턴스
    let grid = null;

    // API URL 정의
    const API_URL = '/api/eapproval/forms';

    // 초기화 함수
    function initialize() {
        console.log('DocFormList 초기화');
        
        // 그리드 초기화
        initGrid();
        
        // 이벤트 리스너 등록
        setupEventListeners();
        
        // URL 메시지 확인
        checkUrlMessages();
        
        // 초기 데이터 로드
        loadFormList();
    }

    // 이벤트 리스너 설정
    function setupEventListeners() {
        // 새로고침 버튼
        $('#refreshBtn').on('click', function() {
            loadFormList();
        });
        
        // 양식 등록 버튼
        $('#createBtn').on('click', function() {
            location.href = '/eapproval/forms/new';
        });
        
        // 검색 버튼
        $('#searchBtn').on('click', function() {
            performSearch();
        });
        
        // 초기화 버튼
        $('#resetBtn').on('click', function() {
            resetSearch();
        });
        
        // 검색어 입력 필드 엔터키 이벤트
        $('#keyword').on('keypress', function(e) {
            if (e.which === 13) {
                e.preventDefault();
                performSearch();
            }
        });
    }

    // 그리드 초기화
    function initGrid() {
        const gridContainer = document.getElementById('docFormGrid');
        
        grid = new tui.Grid({
            el: gridContainer,
            scrollX: false,
            scrollY: true,
            rowHeaders: ['rowNum'],
            pageOptions: {
                useClient: true,
                perPage: 10
            },
            columns: [
                {
                    header: '양식 ID',
                    name: 'FORM_ID',
                    sortable: true,
                    width: 150
                },
                {
                    header: '생성자',
                    name: 'CREATOR_ID',
                    sortable: true,
                    width: 120
                },
                {
                    header: '생성일자',
                    name: 'CREATE_DATE',
                    sortable: true,
                    width: 150,
                    formatter: function(value) {
                        if (!value.value) return '-';
                        return formatDate(value.value);
                    }
                },
                {
                    header: '최종 수정일자',
                    name: 'UPDATE_DATE',
                    sortable: true,
                    width: 150,
                    formatter: function(value) {
                        if (!value.value) return '-';
                        return formatDate(value.value);
                    }
                },
                {
                    header: '관리',
                    name: 'actions',
                    width: 150,
                    formatter: function(value) {
                        return `
                            <div class="d-flex justify-content-center">
                                <button type="button" class="btn btn-sm btn-outline-primary me-1 btn-view" data-form-id="${value.row.FORM_ID}">
                                    <i class="bi bi-eye"></i>
                                </button>
                                <button type="button" class="btn btn-sm btn-outline-warning me-1 btn-edit" data-form-id="${value.row.FORM_ID}">
                                    <i class="bi bi-pencil"></i>
                                </button>
                                <button type="button" class="btn btn-sm btn-outline-danger btn-delete" data-form-id="${value.row.FORM_ID}">
                                    <i class="bi bi-trash"></i>
                                </button>
                            </div>
                        `;
                    }
                }
            ]
        });
        
        // 그리드 클릭 이벤트
        grid.on('click', function(ev) {
            const { columnName } = ev;
            
            if (columnName === 'actions') {
                const target = ev.nativeEvent.target.closest('button');
                if (!target) return;
                
                const formId = target.getAttribute('data-form-id');
                
                if (target.classList.contains('btn-view')) {
                    viewForm(formId);
                } else if (target.classList.contains('btn-edit')) {
                    editForm(formId);
                } else if (target.classList.contains('btn-delete')) {
                    deleteForm(formId);
                }
            } else if (columnName === 'FORM_ID') {
                const formId = grid.getValue(ev.rowKey, 'FORM_ID');
                viewForm(formId);
            }
        });
        
        // 그리드 더블클릭 이벤트
        grid.on('dblclick', function(ev) {
            if (ev.columnName !== 'actions') {
                const formId = grid.getValue(ev.rowKey, 'FORM_ID');
                viewForm(formId);
            }
        });
    }

    // 양식 목록 로드
    function loadFormList() {
        // 로딩 표시
        showLoading(true);
        
        $.ajax({
            url: API_URL,
            type: 'GET',
            dataType: 'json',
            success: function(response) {
                // 로딩 숨김
                showLoading(false);
                
                if (response.success && response.data) {
                    // 그리드 데이터 설정
                    grid.resetData(response.data);
                    
                    if (response.data.length === 0) {
                        showMessage('info', '등록된 양식이 없습니다.');
                    }
                } else {
                    showMessage('error', response.message || '양식 목록을 불러오는데 실패했습니다.');
                }
            },
            error: function(xhr, status, error) {
                // 로딩 숨김
                showLoading(false);
                
                // 오류 처리
                console.error('API Error:', error);
                showMessage('error', '서버 통신 오류가 발생했습니다.');
            }
        });
    }

    // 검색 실행
    function performSearch() {
        const searchType = $('#searchType').val();
        const keyword = $('#keyword').val().trim();
        
        if (!keyword) {
            showMessage('warning', '검색어를 입력하세요.');
            $('#keyword').focus();
            return;
        }
        
        // 검색 요청
        showLoading(true);
        
        $.ajax({
            url: API_URL,
            type: 'GET',
            dataType: 'json',
            success: function(response) {
                showLoading(false);
                
                if (response.success && response.data) {
                    // 클라이언트 측 필터링 (서버에서 필터링 API가 제공되지 않을 경우)
                    const filteredData = response.data.filter(item => {
                        const fieldValue = String(item[searchType] || '').toLowerCase();
                        return fieldValue.includes(keyword.toLowerCase());
                    });
                    
                    grid.resetData(filteredData);
                    
                    if (filteredData.length === 0) {
                        showMessage('info', '검색 결과가 없습니다.');
                    }
                } else {
                    showMessage('error', response.message || '검색에 실패했습니다.');
                }
            },
            error: function(xhr, status, error) {
                showLoading(false);
                console.error('API Error:', error);
                showMessage('error', '서버 통신 오류가 발생했습니다.');
            }
        });
    }

    // 검색 초기화
    function resetSearch() {
        $('#searchType').val('FORM_ID');
        $('#keyword').val('');
        loadFormList();
    }

    // 양식 상세 보기
    function viewForm(formId) {
        location.href = `/eapproval/forms/view/${formId}`;
    }

    // 양식 수정
    function editForm(formId) {
        location.href = `/eapproval/forms/edit/${formId}`;
    }

    // 양식 삭제
    function deleteForm(formId) {
        if (!confirm(`양식 ID '${formId}'을(를) 삭제하시겠습니까?\n삭제된 양식은 복구할 수 없습니다.`)) {
            return;
        }
        
        showLoading(true);
        
        $.ajax({
            url: `${API_URL}/${formId}`,
            type: 'DELETE',
            dataType: 'json',
            success: function(response) {
                showLoading(false);
                
                if (response.success) {
                    showMessage('success', '양식이 삭제되었습니다.');
                    loadFormList();
                } else {
                    showMessage('error', response.message || '양식 삭제에 실패했습니다.');
                }
            },
            error: function(xhr, status, error) {
                showLoading(false);
                console.error('API Error:', error);
                showMessage('error', '서버 통신 오류가 발생했습니다.');
            }
        });
    }

    // 메시지 표시
    function showMessage(type, message) {
        if (type === 'success' || type === 'info') {
            $('#successMessage').text(message);
            $('#successAlert').removeClass('d-none').show();
            setTimeout(() => $('#successAlert').fadeOut(), 3000);
        } else {
            $('#errorMessage').text(message);
            $('#errorAlert').removeClass('d-none').show();
            setTimeout(() => $('#errorAlert').fadeOut(), 5000);
        }
    }

    // 로딩 표시
    function showLoading(show) {
        if (window.AlertUtil && typeof AlertUtil.showLoading === 'function') {
            if (show) {
                window.loadingInstance = AlertUtil.showLoading('데이터 로딩 중...');
            } else if (window.loadingInstance) {
                window.loadingInstance.close();
            }
        }
    }

    // URL 메시지 확인
    function checkUrlMessages() {
        const urlParams = new URLSearchParams(window.location.search);
        const successMsg = urlParams.get('successMsg');
        const errorMsg = urlParams.get('errorMsg');
        
        if (successMsg) {
            showMessage('success', decodeURIComponent(successMsg));
        }
        
        if (errorMsg) {
            showMessage('error', decodeURIComponent(errorMsg));
        }
        
        // 메시지 파라미터 제거 (URL 정리)
        if (successMsg || errorMsg) {
            const newUrl = window.location.pathname;
            history.replaceState(null, '', newUrl);
        }
    }

    // 날짜 포맷팅
    function formatDate(dateString) {
        if (!dateString) return '-';
        
        const date = new Date(dateString);
        if (isNaN(date.getTime())) return dateString;
        
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        const hours = String(date.getHours()).padStart(2, '0');
        const minutes = String(date.getMinutes()).padStart(2, '0');
        
        return `${year}-${month}-${day} ${hours}:${minutes}`;
    }

    // 모듈 공개 API
    return {
        initialize: initialize
    };
})();

// 문서 로드 완료 시 초기화
$(document).ready(function() {
    DocFormList.initialize();
});