package com.example.cmtProject.dto.mes.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 제품 기본 정보 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductsDTO {
    private Long pdtNo;             // 제품 번호 (PK)
    private String pdtCode;         // 제품 코드
    private String pdtName;         // 제품 이름
    private String pdtShippingPrice; // 비용
    private String pdtComments;     // 제품 설명
    private String pdtUseyn;        // 사용 여부 (Y/N)
    private String mtlTypeCode;     // 자재 유형 코드
    private String pdtWeight;       // 제품 중량
    private String wtTypeCode;      // 중량 단위 코드
    private String pdtSize;         // 제품 크기
    private String ltTypeCode;      // 리드타입 유형 코드
    private String pdtType;         // 제품 유형 (완제품, 반제품, 부품 등)
    private String pdtSpecification; // 제품 규격
}