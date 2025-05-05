package com.example.cmtProject.dto.mes.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 자재 입고 이력 정보 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaterialReceiptHistoryDTO {
    private Long historyNo;              // 이력 번호 (PK)
    private Long receiptNo;              // 입고 번호 (FK)
    private String actionType;           // 처리 유형 (입고등록, 검수시작, 입고확정 등)
    private LocalDateTime actionDate;    // 처리 일자
    private String actionDescription;    // 처리 설명 (상세 내용)
    private String actionUser;           // 처리자 (작업자)
    private String createdBy;            // 생성자
    private LocalDateTime createdDate;   // 생성일자
}