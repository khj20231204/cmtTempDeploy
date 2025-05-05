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
public class IqcDTO {
	
	public IqcDTO(Double iqcMeasuredWeightValue, Double iqcMeasuredLengthValue, String iqcInspectionResult) {
	    this.iqcMeasuredWeightValue = iqcMeasuredWeightValue;
	    this.iqcMeasuredLengthValue = iqcMeasuredLengthValue;
	    this.iqcInspectionResult = iqcInspectionResult;
	}
	private Long iqcNo;                 // 입고 검사 NO
    private String iqcCode;             // 입고 검사 코드
    private String empId;               // 사용자 ID
    private String empName;               // 사용자 ID
    private String qcmName;             // 입고 검사 기준 번호
    private String mtlName;			    // 원자재 코드
    private LocalDateTime iqcStartTime; // 검사 시작 시간
    private LocalDateTime iqcEndTime;   // 검사 종료 시간  
    private String receivedQty;			// 출고 검사 수량
    private String unitQty;				// 단위 수량
    private Double iqcMeasuredWeightValue;    // 측정값
    private Double iqcMeasuredLengthValue;    // 측정값
    private String qcmUnitWeight;       // 단위 (ex: g)
    private String qcmUnitLength;       // 단위 (ex: mm)
    private String iqcInspectionStatus; // 검사 상태 (검사전/검사중/검사완료)
    private String iqcInspectionResult; // 합격 여부 (P/F)
    private String whsName;             // 입고될 창고
    private String lotNo;               // LOT번호
    private String iqcRemarks;
    private String iqcVisiable;			// 삭제 여부
    
    
    // join을 위한 DTO
    private String mtlCode;             // 원자재 코드
    private String whsCode;				// 창고 코드
    private Long qcmNo;				// qcmNO
    private String qcmCode;
    private String receiptCode;         // 입고 코드
    private String receiptNo;         // 입고 코드

}
