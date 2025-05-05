package com.example.cmtProject.dto.mes.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 제품 출고별 재고 정보 DTO (FIFO 방식 재고 관리용)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductsIssueStockDTO {
    private Long issueStockNo;          // 출고 재고 관리 번호 (PK)
    private String issueCode;           // 출고 코드 (FK)
    private String pdtCode;             // 제품 코드
    private String issuedQty;           // 출고 수량
    private LocalDateTime issueDate;    // 출고 일자 (출하 시점)
    private String lotNo;               // LOT 번호 (품질관리용)
    private String createdBy;           // 생성자
    private String updatedBy;           // 수정자
    private LocalDateTime createdDate;  // 생성일자
    private LocalDateTime updatedDate;  // 수정일자
}