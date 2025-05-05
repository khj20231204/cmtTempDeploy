package com.example.cmtProject.dto.mes.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 자재 기본 정보 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaterialsDTO {
    private Long mtlNo;             // 자재 고유번호 (PK)
    private String mtlCode;         // 자재 코드 (관리 코드)
    private String mtlName;         // 자재 명칭
    private String mtlStandard;     // 자재 규격
    private String mtlUnit;         // 자재 단위 (kg, g, ea 등)
    private Double mtlBasePrice;    // 자재 기준가격
    private String mtlPrcType;      // 공정 유형 코드
    private String mtlCltCode;      // 거래처 코드 (공급업체)
    private String mtlTypeCode;     // 자재 재질 코드
    private String mtlUseYn;        // 사용 여부(Y/N)
    private String mtlComments;     // 자재 비고 (특이사항 등)
    private String mtlSuppCode;     // 공급처코드
}