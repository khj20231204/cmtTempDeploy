package com.example.cmtProject.dto.mes.qualityControl;

import java.time.LocalDateTime;

import groovy.transform.builder.Builder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FqcDTO {
	
	public FqcDTO(Double fqcMeasuredWeightValue, Double fqcMeasuredLengthValue, String fqcInspectionResult) {
	    this.fqcMeasuredWeightValue = fqcMeasuredWeightValue;
	    this.fqcMeasuredLengthValue = fqcMeasuredLengthValue;
	    this.fqcInspectionResult = fqcInspectionResult;
	}
	
	private Long fqcNo;                // 출고 검사 NO
    private String fqcCode;           // 출고 검사 코드   
    private String empId;             // 사용자 ID
    private String empName;             // 사용자 ID
    private Long qcmNo;              // 출고 검사 기준 번호
    private String pdtName;
    private LocalDateTime fqcStartTime;    // 검사 시작 시간
    private LocalDateTime fqcEndTime;    // 검사 종료 시간
    private String woQty;			// 출고 검사 수량
    private String unitQty;				// 단위 수량
    private Double fqcMeasuredWeightValue;  // 측정값
    private Double fqcMeasuredLengthValue;  // 측정값
    private String qcmUnitWeight;   // 단위 (ex: g)
    private String qcmUnitLength;   // 단위 (ex: mm)
    private String fqcInspectionStatus; // 검사 상태 (검사전/검사중/검사완료)
    private String fqcInspectionResult; // 합격 여부 (P/F)
    private String whsName;               // 입고될 창고
    private String childLotNo;             // LOT 번호
    private String fqcRemarks;
    private String fqcVisiable;			// 삭제 여부
    
    
    // join을 위한 DTO
    private String pdtCode;               // 완제품 NO
    private String whsCode;               // 완제품 NO
    private String qcmName;
    private String qcmCode;
    private String woCode;         // 출고 코드

}
