package com.example.cmtProject.dto.erp.employees;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor

public class EmpCountDTO {
	private Long empNo;        // e.emp_no
    private String empGender; // 성별
    private Long deptNo; // 부서번호 (FK)
    private String deptName;   // 부서명
    private Long positionNo; // 직위번호 (FK)
    private String deptPosition; //직급
    private String empStatus; // 재직상태
    
    
    private Integer total;
    private Integer maleCount;
    private Integer femaleCount;
    private Integer active;
    private Integer retired;
    private Integer onLeave;
    
    private Integer empCount;
    
    
}
