package com.example.cmtProject.entity.erp.employees;

import java.time.LocalDate;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@EntityListeners(AuditingEntityListener.class) //@createDate 어노테이션 사용하기위해 사용
@Table(name="employees")
@Getter
@Setter
@NoArgsConstructor
@ToString

public class Employees{
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EMP_NO")  // 사원번호 (PK)
    private Long empNo;

    @Column(name = "EMP_ID", nullable = false, unique = true)  // 사원 ID
    private String empId;

    @Column(name = "EMP_LEVEL")
    private String empLevel; // 사원 권한

    @Column(name = "EMP_PASSWORD")
    private String empPassword; // 비밀번호

    @Column(name = "EMP_NAME", nullable = false)
    private String empName; // 이름

    @Column(name = "EMP_PROFILE")
    private String empProfile; // 프로필사진 파일경로(DB저장용)

    @Column(name = "EMP_BIRTHDAY")
    private String empBirthday; // 생년월일

    @Column(name = "EMP_EMAIL")
    private String empEmail; // 이메일
    
    @Column(name = "EMP_GENDER")
    private String empGender; // 성별

    @Column(name = "DEPT_NO")
    private Long deptNo; // 부서번호 (FK)
    
    @Column(name = "POSITION_NO")
    private Long positionNo; // 직위번호 (FK)

    @Column(name = "EMP_PHONE")
    private String empPhone; // 휴대폰 번호

    @Column(name = "EMP_POST_CODE")
    private String empPostCode; // 우편번호

    @Column(name = "EMP_ADDRESS1")
    private String empAddress1; // 기본주소

    @Column(name = "EMP_ADDRESS2")
    private String empAddress2; // 상세주소

    @Column(name = "EMP_START_DATE")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate empStartDate; // 입사일

    @Column(name = "EMP_END_DATE")
    private LocalDate empEndDate; // 퇴사일

    @Column(name = "EMP_END_REASON")
    private String empEndReason; // 퇴사사유

    @Column(name = "EMPLOYMENT_TYPE")
    private String employmentType; // 채용구분

    @Column(name = "EMP_TYPE")
    private String empType; // 고용유형

    @Column(name = "EMP_STATUS")
    private String empStatus; // 재직상태

    @Column(name = "EMP_EDUCATION_LEVEL")
    private String empEducationLevel; // 학력

    @Column(name = "EMP_GRADE")
    private String empGrade; // 성과등급

    @Column(name = "EMP_MARITAL_STATUS")
    private String empMaritalStatus; // 결혼여부


    @Column(name = "EMP_APPOINTMENT_TYPE")
    private String empAppointmentType; // 발령구분

    @Column(name = "EMP_PARKING_STATUS")
    private String empParkingStatus; // 주차등록 유무

    @Column(name = "EMP_CAR_NUMBER")
    private String empCarNumber; // 차량번호
    //=========================================
    @Column(name = "PAY_NO")
    private Long payNo; // 급여번호
//    @Column(name = "SAL_NO")
//    private String salNo; // 급여번호
    
    @Column(name = "SAL_BANK_NAME")
    private String salBankName; // 은행명
    
    @Column(name = "SAL_BANK_ACCOUNT")
    private String salBankAccount; // 계좌번호

	public Employees(Long empNo) {
		super();
		this.empNo = empNo;
	}
    
    
  
}

