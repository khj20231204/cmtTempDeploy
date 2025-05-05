package com.example.cmtProject.dto.mes.qualityControl;

import com.example.cmtProject.entity.erp.attendanceMgt.Attend;
import com.example.cmtProject.entity.mes.qualityControl.Qcm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QcmDTO {
	
	private Long qcmNo;             // 입고 검사 기준 NO
    private String qcmCode;         // 입고 검사 기준 코드
    private String qcmName;         // 입고 검사 이름
    private String mtlCode;			// 원자재 CODE
    private String pdtCode;			// 완제품 CODE
    private Double qcmTargetValue;  // 목표값
    private Double qcmMaxValue;     // 상한값
    private Double qcmMinValue;     // 하한값
    private String qcmUnitWeight;   // 단위 (ex: g)
    private String qcmUnitLength;   // 단위 (ex: mm)
    private String qcmMethod;       // 검사방법
    
    
    // join을 위한 DTO
    private String mtlName;           // 원자재 Name (외래키)
    private String pdtName;           // 완제품 Name (외래키)
    
    // 수정을 위한 value와 columnName
    private String value;
    private String columnName;
    
    
    public Qcm toEntity() {
    	return Qcm.builder()
    			.qcmCode(qcmCode)
    			.qcmName(qcmName)
    			.mtlCode(mtlCode)
    			.pdtCode(pdtCode)
    			.qcmTargetValue(qcmTargetValue)
    			.qcmMaxValue(qcmMaxValue)
    			.qcmMinValue(qcmMinValue)
    			.qcmUnitWeight(qcmUnitWeight)
    			.qcmUnitLength(qcmUnitLength)
    			.qcmMethod(qcmMethod)
    			.build();
    			
    }
    
    

}
