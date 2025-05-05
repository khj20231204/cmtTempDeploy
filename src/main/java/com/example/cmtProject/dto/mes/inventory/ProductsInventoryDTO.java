package com.example.cmtProject.dto.mes.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 제품 재고 정보 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductsInventoryDTO {
    private Long pinvNo;                       // 제품 재고 관리번호 (PK)
    private Long pdtNo;                        // 제품 번호 (FK)
    private String pdtCode;                    // 제품 코드
    private String pdtName;                    // 제품명 (PRODUCTS 테이블에서 조인)
    private String warehouseCode;              // 창고 코드
    private String locationCode;               // 위치 코드
    private String currentQty;                 // 현재 수량 (총 재고량)
    private String allocatedQty;               // 할당 수량 (출하 계획된 수량)
    private String availableQty;               // 가용 수량 (현재수량 - 할당수량, 트리거로 자동 계산)
    private String lotNo;                      // 완제품 LOT 번호
    private LocalDateTime lastMovementDate;    // 마지막 이동일
    private LocalDateTime lastAdjustmentDate;  // 마지막 조정일자
    private String adjustmentReason;           // 재고 조정 사유
    private String safetyStockAlert;           // 안전 재고 알림 여부 (Y/N)
    private String createdBy;                  // 생성자
    private String updatedBy;                  // 수정자
    private LocalDateTime createdDate;         // 생성일시
    private LocalDateTime updatedDate;         // 수정일시
}