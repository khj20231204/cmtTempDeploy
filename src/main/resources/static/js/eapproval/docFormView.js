/**
 * docFormView.js - 문서 양식 상세 조회 스크립트
 * 
 * 문서 양식 상세 정보 표시 및 관리 기능을 제공합니다.
 * - 양식 정보 조회
 * - 수정 및 삭제 기능
 * 
 * @version 1.0.0
 */

// 문서 양식 상세 조회 모듈
const DocFormView = (function() {
    // 양식 ID
    let formId = '';

    // API URL 정의
    const API_URL = '/api/eapproval/forms';

    // 초기화 함수
    function initialize() {
        console.log('DocFormView 초기화');

        // 양식 ID 가져오기
        formId = $('#formId').val();

        if (!formId) {
            showMessage('error', '양식 ID가 제공되지 않았습니다.');
            setTimeout(() => {
                location.href = '/eapproval/forms';
            }, 2000);
            return;
        }

        // 이벤트 리스너 등록
        setupEventListeners();

        // 양식 데이터 로드
        loadFormData();
    }

    // 이벤트 리스너 설정
    function setupEventListeners() {
        // 목록으로 버튼
        $('#backBtn').on('click', function() {
            location.href = '/eapproval/forms';
        });

        // 수정 버튼
        $('#editBtn').on('click', function() {
            location.href = `/eapproval/forms/edit/${formId}`;
        });

        // 삭제 버튼
        $('#deleteBtn').on('click', function() {
            deleteForm();
        });
    }

    // 양식 데이터 로드
    function loadFormData() {
        // 로딩 표시
        showLoading(true);

        $.ajax({
            url: `${API_URL}/${formId}`,
            type: 'GET',
            dataType: 'json',
            success: function(response) {
                // 로딩 숨김
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
                // 로딩 숨김
                showLoading(false);

                // 오류 처리
                console.error('API Error:', error);
                showMessage('error', '서버 통신 오류가 발생했습니다.');
                setTimeout(() => {
                    location.href = '/eapproval/forms';
                }, 2000);
            }
        });
    }

    // 양식 데이터 표시
    function displayFormData(data) {
        // 기본 정보 표시
        $('#viewFormId').text(data.FORM_ID || '-');
        $('#viewCreatorId').text(data.CREATOR_ID || '-');
        $('#viewCreateDate').text(formatDate(data.CREATE_DATE) || '-');

        // 수정 정보 표시 (있는 경우)
        if (data.UPDATER_ID) {
            $('#viewUpdaterId').text(data.UPDATER_ID);
            $('#updaterRow').show();
        }

        if (data.UPDATE_DATE) {
            $('#viewUpdateDate').text(formatDate(data.UPDATE_DATE));
            $('#updateDateRow').show();
        }

        // 양식 내용 표시
        $('#viewFormContent').html(data.FORM_CONTENT || '');
    }

    // 양식 삭제
    function deleteForm() {
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

                    // 잠시 후 목록 페이지로 이동
                    setTimeout(() => {
                        location.href = `/eapproval/forms?successMsg=${encodeURIComponent('양식이 삭제되었습니다.')}`;
                    }, 1500);
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
        } else {
            $('#errorMessage').text(message);
            $('#errorAlert').removeClass('d-none').show();
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
    DocFormView.initialize();
});