package com.example.cmtProject.dto.mes.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 제품 출고 정보 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductsIssueDTO {
    private Long issueNo;                // 출고 관리번호 (PK)
    private String issueCode;            // 출고 코드 (관리 코드)
    private String pdtCode;              // 제품 코드
    private String pdtName;              // 제품명 (PRODUCTS 테이블에서 조인)
    private String requestQty;           // 요청 수량
    private String issuedQty;            // 출고 수량
    private String lotNo;                // LOT 번호
    private LocalDateTime requestDate;   // 요청일
    private LocalDateTime issueDate;     // 출고일
    private String issueStatus;          // 출고 상태 (출고대기, 출고처리중, 출고완료, 취소)
    private String warehouseCode;        // 창고 코드
    private String issuer;               // 출고 담당자
    private String createdBy;            // 생성자
    private String updatedBy;            // 수정자
    private LocalDateTime createdDate;   // 생성일자
    private LocalDateTime updatedDate;   // 수정일자
}