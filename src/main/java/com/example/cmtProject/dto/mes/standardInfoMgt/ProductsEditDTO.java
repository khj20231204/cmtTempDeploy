package com.example.cmtProject.dto.mes.standardInfoMgt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductsEditDTO {
	
	private Long pdtNo;
	private String columnName;
	private String value;
	
}
