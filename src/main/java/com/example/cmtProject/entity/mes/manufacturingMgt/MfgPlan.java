package com.example.cmtProject.entity.mes.manufacturingMgt;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "MFG_PLANS")
@NoArgsConstructor
@AllArgsConstructor
public class MfgPlan { // 생산 계획 Entity

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MP_NO")
	private Long mpNo;  				// 생산 계획 번호
	
    @Column(name = "MP_CODE")
	private String mpCode;  			// 생산 계획 코드
	
    @Column(name = "SO_CODE")
	private String soCode;  			// 수주 코드

    @Column(name = "EMP_ID")
	private String empId;  				// 등록 직원 사번

    @Column(name = "MP_STATUS")
	private String mpStatus;  			// 생산 계획 상태

    @Column(name = "MP_PRIORITY")
	private String mpPriority;  		// 생산 우선순위

    @Column(name = "MP_CREATED_AT")
	private LocalDate mpCreatedAt;  	// 등록일자

    @Column(name = "MP_UPDATED_AT")
	private LocalDate mpUpdatedAt;  	// 수정일자

    @Column(name = "MP_START_DATE")
	private LocalDate mpStartDate;  	// 생산 시작 예정일

    @Column(name = "MP_END_DATE")
	private LocalDate mpEndDate;  		// 생산 종료 예정일
    
    @Column(name = "MP_VISIBLE")
	private String mpVisible;  			// 삭제 시 숨김 처리

}
