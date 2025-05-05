package com.example.cmtProject.dto.erp.salaries;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PayCmmCodeDetailDTO { // 공통코드 -> 수당 / 공제 DTO
	
	private String cmnDetailCode;     // 상세 코드
	private String cmnCode;			  // 공통 코드
	private String cmnDetailValue;	  // 상세 코드 값
	private String cmnDetailValue2;	  // 상세 코드 값 2
	
}