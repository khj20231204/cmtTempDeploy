package com.example.cmtProject.dto.erp.eapproval;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalLineDTO {
    /** 결재선 번호 */
    private Integer approvalNo;
    
    /** 문서 ID */
    private String docId;
    
    /** 결재자 ID (사번) */
    private String approverId;  // Integer approverNo -> String approverId로 변경
    
    /** 결재자 이름 (조회용) */
    private String approverName;
    
    /** 결재자 직위 (조회용) */
    private String approverPosition;
    
    /** 결재 순서 */
    private Integer approvalOrder;
    
    /** 결재 상태 (대기, 승인, 반려) */
    private String approvalStatus;
    
    /** 결재 일자 */
    private LocalDateTime approvalDate;
    
    /** 결재 의견 */
    private String approvalComment;
    
    /** 결재 유형 (결재, 합의, 참조) */
    private String approvalType;
    
    /** 행 타입 (조회:select, 등록:insert, 수정:update, 삭제:delete) */
    private String rowType;
    
} //ApprovalLineDTO
