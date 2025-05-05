package com.example.cmtProject.entity.erp.salesMgt;

public enum ShipmentStatus {
	SHIP_READY, // 출하 준비 완료 - 제품이 출하 준비 완료됨
	SHIP_INPROGRESS, // 출하 진행 중 - 물류센터에서 제품을 포장 및 출하하는 중
	SHIP_DISPATCHED, // 출하 완료 - 제품이 물류 창고에서 출발함
	SHIP_INTRANSIT, // 배송 중 - 제품이 고객에게 이동 중
	SHIP_DELIVERED, // 납품 완료 - 고객에게 제품이 정상적으로 도착함
	SHIP_RETURNED, // 반품 처리 중 - 고객이 제품 반품을 요청한 상태
	SHIP_CANCELED // 출하 취소 - 출하가 취소됨
}

