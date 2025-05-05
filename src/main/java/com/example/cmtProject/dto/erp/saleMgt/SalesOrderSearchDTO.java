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
public class SalesOrderSearchDTO {
	
	private String soCode;
	private Long soNo;
	private String pdtCode;
	private String cltCode;
	private String soStatus;
	private String dateType;
	private LocalDate startDate;
	private LocalDate endDate;
}
