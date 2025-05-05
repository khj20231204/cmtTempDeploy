package com.example.cmtProject.dto.mes.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 자재 재고 정보 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaterialInventoryDTO {             
    private Long invNo;                         // 재고 관리번호
    private String mtlCode;                     // 자재 코드
    private String mtlName;                     // 자재명 (MATERIALS 테이블에서 조인)
    private String warehouseCode;               // 창고 코드
    private String locationCode;                // 위치 코드
    private String currentQty;                  // 현재 수량 (총 재고량)
    private String allocatedQty;                // 할당 수량 (계획되어 있는 수량)
    private String availableQty;                // 가용 수량 (현재수량 - 할당수량, 트리거로 자동 계산)
    private String lotNo;                       // LOT 번호
    private LocalDateTime lastMovementDate;     // 마지막 이동일 (입출고 발생 일자)
    private LocalDateTime lastAdjustmentDate;   // 마지막 조정 일자 (재고 조정 발생 일자)
    private String adjustmentReason;            // 조정 사유 (재고 조정 시 입력한 사유)
    private String safetyStockAlert;            // 안전재고 알림 여부 (Y/N)
    private String createdBy;                   // 생성자
    private String updatedBy;                   // 수정자
    private LocalDateTime createdDate;          // 생성일자
    private LocalDateTime updatedDate;          // 수정일자
    
}                                                                        