package com.example.cmtProject.dto.erp.salaries;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class PayEmpListDTO { // 사원 정보 DTO
	
	private String empId; 			// 사원번호
	private String empName; 		// 사원명
	private String empType; 		// 고용유형
	private Long deptNo; 			// 부서번호
	private String deptName; 		// 부서명
	private String position; 		// 직급명
	private LocalDate payDate; 		// 급여일
	private String payBasic; 		// 기본급
	private String salBankName;	    // 은행명
	private String salBankAccount;	// 계좌번호
	
}