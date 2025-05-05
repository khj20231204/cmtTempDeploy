package com.example.cmtProject.dto.erp.employees;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class EmpListPreviewDTO {
	private Long empNo; //사원번호(PK)
	private String empId;// 사원 ID = 사번
	private String empName; // 이름
	private String deptName; //부서명
	private String deptPosition; //직급
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate empStartDate; // 입사일
	
}
