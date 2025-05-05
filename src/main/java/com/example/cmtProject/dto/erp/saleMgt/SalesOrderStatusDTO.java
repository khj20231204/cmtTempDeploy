package com.example.cmtProject.dto.erp.saleMgt;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalesOrderStatusDTO {

	/*
	SO_RECEIVED, // 수주 접수 - 고객의 주문을 접수한 상태
	SO_CONFIRMED, // 수주 확정 - 주문이 검토되어 확정됨
	SO_PLANNED, // 생산 계획 수립 - 수주를 기반으로 생산 계획이 수립됨
	SO_INPRODUCTION, // 생산 진행 중 - 해당 수주에 대한 제품이 생산 중인 상태
	SO_COMPLETED, // 생산 완료 - 수주된 제품의 생산이 완료됨
	SO_SHIPPED, // 출하 완료 - 고객에게 제품이 출하됨
	SO_CANCELED, // 수주 취소 - 주문이 취소됨
	*/
	
	private String statusCode;
	private String statusName;
	private String statusComment;
}
