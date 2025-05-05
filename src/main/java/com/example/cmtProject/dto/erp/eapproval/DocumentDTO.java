package com.example.cmtProject.dto.erp.eapproval;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDTO {
	
	/** 문서 ID */
    private String docId;
    
    /** 문서 번호 */
    private String docNumber;
    
    /** 양식 ID */
    private String formId;
    
    /** 제목 */
    private String title;
    
    /** 내용 */
    private String content;
    
    /** 기안자 ID */
    private String drafterId;
    
    /** 기안자 이름 (조회용) */
    private String drafterName;
    
    /** 기안 부서 코드 */
    private String draftDept;
    
    /** 기안 부서명 (조회용) */
    private String draftDeptName;
    
    /** 기안 일자 */
    private LocalDateTime draftDate;
    
    /** 문서 상태 (임시저장, 진행중, 완료, 반려) */
    private String docStatus;
    
    /** 최종 결재 일자 */
    private LocalDateTime approvalDate;
    
    /** 임시저장 여부 (Y/N) */
    private String isTempSaved;
    
    /** 수정 일자 */
    private LocalDateTime updateDate;
    
    /** 결재선 목록 */
    private List<ApprovalLineDTO> approvalLines = new ArrayList<>();
    
    /** 행 타입 (조회:select, 등록:insert, 수정:update, 삭제:delete) */
    private String rowType;
    
    /** 첨부파일 목록 */
    private List<AttachmentDTO> attachments = new ArrayList<>();

} //DocumentDTO
