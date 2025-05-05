package com.example.cmtProject.dto.mes.standardInfoMgt;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BomInfoTotalDTO {
	
	//BOM
	private Long bomNo; 		// BOM 고유번호
	private int bomLevel; 		// 재귀시 순서
	private String childItemCode; 	// 상위 코드
    private String parentPdtCode; 	// 하위 코드
    private String itemType; 	//코드 유형(RAW_MATERIAL / SEMI_FINISHED)
    private String bomQty; 		// 투입 수량
    private String bomUnit; 	// 투입 단위 (예: EA, TON)
    private String bomPrcType; 	// 투입 공정 단계 (예: PR, WE, PA, SA)
    private LocalDate bomDate;  // 입력 날짜
    private String comments; 	// 비고
    private String useYN; 		// 사용 여부
    
    //Products
    private Long pdtNo;
	private String pdtName;
	private String pdtSpecification;
	private String pdtShippingPrice;
	private String pdtComments;
    
    //Materials
    private Long mtlNo; 		// 원자재 번호 (수동 입력 or 시퀀스)
    private String mtlName; 	// 원자재 이름
    private String mtlStandard; // 규격
    private String mtlUnit; 	// 단위 (ex: KG, EA, M 등)
    private Long mtlBasePrice; 	// 기준 단가
    private String mtlPrcType; 	// 공정 과정 코드 (예: PR, WE, PA 등)
    private String mtlCltCode; 	// 공급업체 코드 (CLIENTS 테이블의 CLT_CODE 참조 가능)
    private char mtlUseYN; 		// 사용 여부
    private String mtlComments; // 비고 
    
    //ProductProcessType
    private Long pdtPrcNo;
    private String prcTypeCode;
    private String prcTypeName;
    private int prcPriority;
    private LocalDate createDate;
    private String prcComment;
    
}
