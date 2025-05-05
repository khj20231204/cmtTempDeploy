/**
 * DocumentForm - 통합 문서 폼 스크립트 (리팩토링 버전)
 * 
 * 기안서 작성 페이지의 모든 모듈을 통합하고 초기화하는 스크립트입니다.
 * - 각 기능별 모듈 초기화 및 연동
 * - 전체 페이지 초기화 관리
 * - 오류 처리 및 로깅
 * 
 * @version 1.2.0
 * @since 2025-04-03
 * @update 2025-04-04 - API 응답 처리 리팩토링 및 대문자 키 일관성 개선
 */

// 페이지 로드 시 초기화 - DOM 로드 완료 이벤트에 등록
document.addEventListener('DOMContentLoaded', async function() {
    try {
        console.log('기안서 작성 페이지 초기화를 시작합니다.');
        
        // 모든 모듈을 전역 스코프에 명시적으로 노출
        window.FormContentLoader = FormContentLoader;
        window.ApprovalLineManager = ApprovalLineManager;
//        window.AttachmentManager = AttachmentManager;
        window.DocumentFormManager = DocumentFormManager;
        
        console.log('모듈 전역 노출 완료');
        
        // 각 모듈 순차적 초기화
        // 1. 문서 폼 관리자 초기화
        await DocumentFormManager.initialize();
        
        // 2. 양식 내용 로더 초기화
        await FormContentLoader.initialize();
        
        // 3. 에디터 초기화
        await FormContentLoader.initializeEditor();
        
        // 4. 결재선 관리자 초기화
        // 서버 데이터에서 초기 결재선 확인 - 가이드라인에 따라 대문자 키 사용
        let initialApprovalLines = [];
        
        // window.documentData가 존재하는 경우에만 처리
        if (window.documentData) {
            // 대문자 키로 접근
            initialApprovalLines = window.documentData.APPROVAL_LINES || [];
            console.log(`서버 데이터에서 ${initialApprovalLines.length}개의 결재선을 로드했습니다.`);
        }
        
        await ApprovalLineManager.initialize(initialApprovalLines);
        
//        // 5. 첨부파일 관리자 초기화
//        if (window.AttachmentManager && typeof AttachmentManager.initialize === 'function') {
//            await AttachmentManager.initialize();
//        } else {
//            console.warn('AttachmentManager 모듈을 찾을 수 없거나 초기화 함수가 없습니다.');
//        }
        
        // 모듈 함수 접근 가능성 확인
        console.log('모듈 함수 접근 가능성 확인:');
        console.log('- ApprovalLineManager.getApprovalLines:', !!ApprovalLineManager.getApprovalLines);
        
//        if (window.AttachmentManager) {
//            console.log('- AttachmentManager.appendFilesToFormData:', !!AttachmentManager.appendFilesToFormData);
//        } else {
//            console.log('- AttachmentManager: 모듈 없음');
//        }
        
        console.log('- FormContentLoader.getEditorContent:', !!FormContentLoader.getEditorContent);
        
        console.log('기안서 작성 페이지 초기화가 완료되었습니다.');
    } catch (error) {
        console.error('페이지 초기화 중 오류 발생:', error);
        
        // AlertUtil 사용 가능 여부 확인
        if (window.AlertUtil) {
            await AlertUtil.showError('초기화 오류', '페이지를 불러오는 중 오류가 발생했습니다.');
        } else {
            alert('페이지를 불러오는 중 오류가 발생했습니다.');
        }
    }
});