package com.example.cmtProject.dto.mes.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 제품 출고 이력 정보 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductsIssueHistoryDTO {
    private Long historyNo;              // 이력 번호 (PK)
    private String issueCode;            // 출고 코드 (FK)
    private String actionType;           // 처리 유형 (출고요청, 출고처리, 출고완료 등)
    private LocalDateTime actionDate;    // 처리 일자
    private String actionDescription;    // 처리 설명 (상세 내용)
    private String actionUser;           // 처리자 (작업자)
    private String createdBy;            // 생성자
    private LocalDateTime createdDate;   // 생성일자
}