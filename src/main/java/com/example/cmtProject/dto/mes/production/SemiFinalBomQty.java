package com.example.cmtProject.dto.mes.production;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SemiFinalBomQty {
	
	private String msCode;
	private String parentPdtCode;
	private String msQty;
	
}
