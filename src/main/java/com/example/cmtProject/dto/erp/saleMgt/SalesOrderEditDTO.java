package com.example.cmtProject.dto.erp.saleMgt;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//수주 메인버튼에서 수정을 할 때 클라이언트에서 받아올 dto
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalesOrderEditDTO {
	
	private Long soNo;
	private String columnName;
	private String value;
}
