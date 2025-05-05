package com.example.cmtProject.dto.mes.manufacturingMgt;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MfgPlanSalesOrderDTO { // 생산 계획 등록 시 수주 목록 DTO

	private String soCode; 			// 수주코드
	private String pdtCode; 		// 제품코드
	private String pdtName; 		// 제품명
	private Long soQty; 			// 주문량
	private String qtyUnitName; 	// 주문단위명
	private Long leadTime;  		// 소요시간
	private String cltName; 		// 거래처명
	private LocalDate soDate; 		// 수주일자
	private LocalDate soDueDate; 	// 납품기한
	private LocalDate mpStartDate;  // 생산 시작 예정일
	private LocalDate mpEndDate;    // 생산 종료 예정일
	private String mpPriority; 		// 우선순위
}
