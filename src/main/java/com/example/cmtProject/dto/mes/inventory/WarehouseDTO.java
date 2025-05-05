package com.example.cmtProject.dto.mes.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 창고 기본 정보 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseDTO {
    private Long whsNo;                 // 창고 번호 (PK)
    private String whsCode;             // 창고 코드
    private String whsName;             // 창고 명칭
    private String whsType;             // 창고 유형 (자재창고, 제품창고, 공정자재창고 등)
    private String whsLocation;         // 창고 위치 (물리적 위치 정보)
    private String whsCapacity;         // 창고 용량 (최대 보관 가능 용량)
    private String whsComments;         // 창고 설명
    private String currentUsage;        // 현재 사용량 (현재 사용 중인 용량)
    private String useYn;               // 사용 여부 (Y/N)
    private String createdBy;           // 생성자
    private String updatedBy;           // 수정자
    private LocalDateTime createdDate;  // 생성일자
    private LocalDateTime updatedDate;  // 수정일자
}