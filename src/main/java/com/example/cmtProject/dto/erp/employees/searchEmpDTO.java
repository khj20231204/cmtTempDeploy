package com.example.cmtProject.dto.erp.employees;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import groovy.transform.ToString;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class searchEmpDTO {
	
	private Long empNo;        // e.emp_no
    private String empName;    // 이름
    private String empId;    // 사번
    private String dept;	//검색용
    
    private String deptName;   // 부서명
    private String deptPosition; //직급
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate empStartDate; // 입사일
	
	private LocalDate startDate;
	private LocalDate endDate;
}
