package com.example.cmtProject.dto.mes.manufacturingMgt;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MfgSchedulePlanDTO { // 제조 계획 등록 시 생산 계획 목록 DTO

	private String mpCode; 		   // 생산 계획 코드
	private String pdtCode; 	   // 제품 코드
	private String pdtName;		   // 제품명
	private Long soQty;		       // 주문 수량
	private String qtyUnitName;	   // 주문 단위
	private String empId; 		   // 등록 직원 사번
	private String empName; 	   // 등록 직원명
 	private String allocatedQty;   // 계획 수량
	private String mpPriority;     // 생산 우선순위
	private LocalDate mpStartDate; // 생산 시작 예정일
    private LocalDate mpEndDate;   // 생산 종료 예정일
    
}
