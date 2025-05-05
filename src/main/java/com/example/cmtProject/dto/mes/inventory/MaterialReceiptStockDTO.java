package com.example.cmtProject.dto.mes.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 자재 입고별 재고 정보 DTO (FIFO 방식 재고 관리용)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaterialReceiptStockDTO {
    private Long receiptStockNo;         // 입고 재고 관리 번호 (PK)
    private Long receiptNo;              // 입고 번호 (FK)
    private String mtlCode;              // 자재 코드
    private String remainingQty;         // 남은 수량 (현재 해당 입고분에 남아있는 수량)
    private LocalDateTime receiptDate;   // 입고일 (FIFO 정렬 기준)
    private String createdBy;            // 생성자
    private String updatedBy;            // 수정자
    private LocalDateTime createdDate;   // 생성일자
    private LocalDateTime updatedDate;   // 수정일자
}