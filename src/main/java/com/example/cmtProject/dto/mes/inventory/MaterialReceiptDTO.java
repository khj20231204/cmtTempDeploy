package com.example.cmtProject.dto.mes.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 자재 입고 정보 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaterialReceiptDTO {
    private Long receiptNo;              // 입고 번호 (PK)
    private String receiptCode;          // 입고 코드 (관리 코드)
    private String poCode;               // 발주 코드 (발주 정보 참조)
    private String mtlCode;              // 자재 코드
    private String mtlName;              // 자재명 (MATERIALS 테이블에서 조인)
    private String receivedQty;          // 입고 수량
    private String lotNo;                // LOT 번호 (생산/품질관리용)
    private LocalDateTime receiptDate;   // 입고일
    private String receiptStatus;        // 입고 상태 (입고대기, 검수중, 입고완료, 취소)
    private String warehouseCode;        // 창고 코드
    private String locationCode;         // 위치 코드
    private String receiver;             // 입고 담당자
    private String createdBy;            // 생성자
    private String updatedBy;            // 수정자
    private LocalDateTime createdDate;   // 생성일자
    private LocalDateTime updatedDate;   // 수정일자
}