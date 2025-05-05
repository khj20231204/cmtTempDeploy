package com.example.cmtProject.dto.erp.eapproval;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
* 결재 처리 요청 DTO
* 결재 승인/반려 처리시 사용되는 요청 데이터
*/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalRequestDTO {
   /** 문서 ID */
   private String docId;
   
   /** 결재자 ID */
   private String approverId;
   
   /** 결재 결정 (승인/반려) */
   private String decision;
   
   /** 결재 의견 */
   private String comment;
}