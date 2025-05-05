package com.example.cmtProject.dto.mes.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 창고 위치 정보 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseLocationDTO {
    private Long locNo;                 // 위치 번호 (PK)
    private String whsCode;             // 창고 코드 (FK)
    private String locCode;             // 위치 코드
    private String locName;             // 위치 명칭
    private String locType;             // 위치 유형 (선반, 컨테이너, 파레트 등)
    private String capacity;            // 수용 용량 (해당 위치의 최대 보관 가능 용량)
    private String currentUsage;        // 현재 사용량 (현재 사용 중인 용량)
    private String useYn;               // 사용 여부 (Y/N)
    private String createdBy;           // 생성자
    private String updatedBy;           // 수정자
    private LocalDateTime createdDate;  // 생성일자
    private LocalDateTime updatedDate;  // 수정일자
}