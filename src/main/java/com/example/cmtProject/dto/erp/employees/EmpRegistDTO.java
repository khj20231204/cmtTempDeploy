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
public class EmpRegistDTO {
	private Long empNo;        // e.emp_no
	private String empLevel;    // 권한
    private String empName;    // 이름
    private String empId;    // 사번
    private String empPassword; // 비밀번호
    private String empProfile; // 프로필사진 파일경로(DB저장용)
    private MultipartFile empProfile_file; // 프로필사진 담는곳
    private String empBirthday; // 생년월일
    private String empEmail; // 이메일
    private String empGender; // 성별
    private Long deptNo; // 부서번호 (FK)
    private String deptName;   // 부서명
    private Long positionNo; // 직위번호 (FK)
    private String deptPosition; //직급
    private String empPhone; // 휴대폰 번호
    private String empPostCode; // 우편번호
    private String empAddress1; // 기본주소
    private String empAddress2; // 상세주소
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate empStartDate; // 입사일
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate empEndDate; // 퇴사일
    private String empEndReason; // 퇴사사유
    private String employmentType; // 채용구분
    private String empType; // 고용유형
    private String empStatus; // 재직상태
    private String empEducationLevel; // 학력
    private String empGrade; // 성과등급
    private String empMaritalStatus; // 결혼여부
//    private String salNo; // 급여번호
    private Long payNo; // 급여번호
    private String empAppointmentType; // 발령구분
    private String empParkingStatus; // 주차등록 유무
    private String empCarNumber; // 차량번호
    
    private String salBankName; // 은행명
    private String salBankAccount; // 계좌번호

 	
}
