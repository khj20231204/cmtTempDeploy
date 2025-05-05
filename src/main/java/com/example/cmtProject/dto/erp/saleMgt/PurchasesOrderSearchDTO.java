package com.example.cmtProject.dto.erp.saleMgt;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class PurchasesOrderSearchDTO {

	private String poCode;
	private Long poNo;
	private String mtlCode;
	private String cltCode;
	private String poStatus;
	private String dateType;
	private LocalDate startDate;
	private LocalDate endDate;
}
