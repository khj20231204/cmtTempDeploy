package com.example.cmtProject.dto.erp.employees;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
@NoArgsConstructor
public class EmpDTO {
	private Long empNo;        // e.emp_no
	private String empLevel;    // 권한
    private String empName;    // 이름
    private String empId;    // 사번
    private String empBirthday; // 생년월일
    private String empEmail; // 이메일
    private String empGender; // 성별
    private Long deptNo; // 부서번호 (FK)
    private String deptName;   // 부서명
    private Long positionNo; // 직위번호 (FK)
    private String deptPosition; //직급
    private LocalDate empStartDate;

 	
}
