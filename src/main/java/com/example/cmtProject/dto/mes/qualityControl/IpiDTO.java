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
public class IpiDTO {
	
	public IpiDTO(Double ipiMeasuredWeightValue, Double ipiMeasuredLengthValue, String ipiInspectionResult) {
	    this.ipiMeasuredWeightValue = ipiMeasuredWeightValue;
	    this.ipiMeasuredLengthValue = ipiMeasuredLengthValue;
	    this.ipiInspectionResult = ipiInspectionResult;
	}
	
	private Long ipiNo;                // 출고 검사 NO
	private Long lotNo;	
    private String ipiCode;           // 출고 검사 코드   
    private String empId;             // 사용자 ID
    private String empName;             // 사용자 ID
    private Long qcmNo;              // 출고 검사 기준 번호
    private String pdtName;
    private String pdtType;				// 완제품 반제품 상태
    private LocalDateTime ipiStartTime;    // 검사 시작 시간
    private LocalDateTime ipiEndTime;    // 검사 종료 시간
    private String woQty;			// 출고 검사 수량
    private String unitQty;				// 단위 수량
    private Double ipiMeasuredWeightValue;  // 측정값
    private Double ipiMeasuredLengthValue;  // 측정값
    private String qcmUnitWeight;   // 단위 (ex: g)
    private String qcmUnitLength;   // 단위 (ex: mm)
    private String ipiInspectionStatus; // 검사 상태 (검사전/검사중/검사완료)
    private String ipiInspectionResult; // 합격 여부 (P/F)
    private String whsName;               // 입고될 창고
    private String childLotCode;             // LOT 번호
    private String ipiRemarks;
    private String ipiVisiable;			// 삭제 여부
    
    
    // join을 위한 DTO
    private String pdtCode;               // 완제품 NO
    private String whsCode;               // 완제품 NO
    private String qcmName;
    private String qcmCode;
    private String woCode;         // 출고 코드

}
