/**
 * docFormEdit.js - 문서 양식 편집 스크립트
 * 
 * 문서 양식 등록 및 수정 기능을 제공합니다.
 * - 양식 정보 입력 및 유효성 검사
 * - 에디터를 이용한 양식 내용 편집
 * - 양식 저장 기능
 * 
 * @version 1.0.0
 */

// 문서 양식 편집 모듈
const DocFormEdit = (function() {
    // 양식 ID
    let formId = '';
    
    // 수정 모드 여부
    let isEditMode = false;
    
    // 에디터 인스턴스
    let editor = null;
    
    // API URL 정의
    const API_URL = '/api/eapproval/forms';
    
    // 초기화 함수
    function initialize() {
        console.log('DocFormEdit 초기화');
        
        // 양식 ID 가져오기
        formId = $('#formId').val();
        isEditMode = formId && formId.length > 0;
        
        // 페이지 제목 설정
        $('#pageTitle').text(isEditMode ? '문서 양식 수정' : '문서 양식 등록');
        
        // 수정 모드에 따른 UI 조정
        if (isEditMode) {
            $('#formIdInput').prop('readonly', true);
            $('#formIdHelp').hide();
        }
        
        // 이벤트 리스너 등록
        setupEventListeners();
        
        // 에디터 초기화
        initEditor();
        
        // 수정 모드면 데이터 로드
        if (isEditMode) {
            loadFormData();
        }
    }
    
    // 이벤트 리스너 설정
    function setupEventListeners() {
        // 취소 버튼
        $('#cancelBtn').on('click', function() {
            confirmCancel();
        });
        
        // 저장 버튼
        $('#saveBtn').on('click', function() {
            saveForm();
        });
    }
    
    // 에디터 초기화
    function initEditor() {
        $('#formContentEditor').summernote({
            height: 500,
            toolbar: [
                ['style', ['style']],
                ['font', ['bold', 'underline', 'clear']],
                ['color', ['color']],
                ['para', ['ul', 'ol', 'paragraph']],
                ['table', ['table']],
                ['insert', ['link']],
                ['view', ['fullscreen', 'codeview', 'help']]
            ],
            placeholder: '양식 내용을 작성하세요...',
            callbacks: {
                onInit: function() {
                    editor = this;
                }
            }
        });
    }
    
    // 양식 데이터 로드 (수정 모드)
    function loadFormData() {
        showLoading(true);
        
        $.ajax({
            url: `${API_URL}/${formId}`,
            type: 'GET',
            dataType: 'json',
            success: function(response) {
                showLoading(false);
                
                if (response.success && response.data) {
                    displayFormData(response.data);
                } else {
                    showMessage('error', response.message || '양식 정보를 불러오는데 실패했습니다.');
                    setTimeout(() => {
                        location.href = '/eapproval/forms';
                    }, 2000);
                }
            },
            error: function(xhr, status, error) {
                showLoading(false);
                console.error('API Error:', error);
                showMessage('error', '서버 통신 오류가 발생했습니다.');
            }
        });
    }
    
    // 양식 데이터 표시 (수정 모드)
    function displayFormData(data) {
        // 기본 정보 표시
        $('#formIdInput').val(data.FORM_ID || '');
        
        // 생성 정보 표시
        if (data.CREATOR_ID) {
            $('#creatorId').val(data.CREATOR_ID);
            $('#createDate').val(formatDate(data.CREATE_DATE));
            $('#creatorInfoSection').show();
        }
        
        // 에디터에 내용 설정
        if (data.FORM_CONTENT) {
            $('#formContentEditor').summernote('code', data.FORM_CONTENT);
        }
    }
    
    // 양식 저장
    function saveForm() {
        // 유효성 검사
        if (!validateForm()) {
            return;
        }
        
        // 저장할 데이터 수집
        const formData = {
            formId: $('#formIdInput').val(),
            formContent: $('#formContentEditor').summernote('code'),
            creatorId: $('#creatorId').val() || null // 기존 생성자 정보 유지 (수정 모드)
        };
        
        showLoading(true);
        
        $.ajax({
            url: API_URL,
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(formData),
            dataType: 'json',
            success: function(response) {
                showLoading(false);
                
                if (response.success) {
                    showMessage('success', isEditMode ? '양식이 수정되었습니다.' : '새 양식이 등록되었습니다.');
                    
                    // 성공 후 상세 페이지로 이동
                    setTimeout(() => {
                        location.href = `/eapproval/forms/view/${formData.formId}?successMsg=${encodeURIComponent('양식이 저장되었습니다.')}`;
                    }, 1500);
                } else {
                    showMessage('error', response.message || '양식 저장에 실패했습니다.');
                }
            },
            error: function(xhr, status, error) {
                showLoading(false);
                console.error('API Error:', error);
                showMessage('error', '서버 통신 오류가 발생했습니다.');
            }
        });
    }
    
    // 폼 유효성 검사
    function validateForm() {
        const formId = $('#formIdInput').val().trim();
        const formContent = $('#formContentEditor').summernote('code').trim();
        
//        // 양식 ID 검사
//        if (!formId) {
//            showMessage('error', '양식 ID를 입력해주세요.');
//            $('#formIdInput').focus();
//            return false;
//        }
//        
//        // 양식 ID 형식 검사 (영문, 숫자만 허용)
//        if (!/^[a-zA-Z0-9_]+$/.test(formId)) {
//            showMessage('error', '양식 ID는 영문, 숫자, 언더스코어(_)만 사용할 수 있습니다.');
//            $('#formIdInput').focus();
//            return false;
//        }
//        
//        // 양식 내용 검사
//        if (!formContent || formContent === '<p><br></p>') {
//            showMessage('error', '양식 내용을 입력해주세요.');
//            $('#formContentEditor').summernote('focus');
//            return false;
//        }
        
        return true;
    }
    
    // 취소 확인
    function confirmCancel() {
        if (confirm('작성 중인 내용이 저장되지 않습니다. 취소하시겠습니까?')) {
            location.href = '/eapproval/forms';
        }
    }
    
    // 메시지 표시
    function showMessage(type, message) {
        if (type === 'success' || type === 'info') {
            $('#successMessage').text(message);
            $('#successAlert').removeClass('d-none').show();
        } else {
            $('#errorMessage').text(message);
            $('#errorAlert').removeClass('d-none').show();
        }
    }
    
    // 로딩 표시
    function showLoading(show) {
        if (window.AlertUtil && typeof AlertUtil.showLoading === 'function') {
            if (show) {
                window.loadingInstance = AlertUtil.showLoading('데이터 ' + (isEditMode ? '수정' : '등록') + ' 중...');
            } else if (window.loadingInstance) {
                window.loadingInstance.close();
            }
        }
    }
    
    // 날짜 포맷팅
    function formatDate(dateString) {
        if (!dateString) return '';
        
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
    DocFormEdit.initialize();
});