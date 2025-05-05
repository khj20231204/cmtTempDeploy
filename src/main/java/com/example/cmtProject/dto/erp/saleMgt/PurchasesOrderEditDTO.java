package com.example.cmtProject.dto.erp.saleMgt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchasesOrderEditDTO {

	private Long poNo;
	private String columnName;
	private String value;
}
