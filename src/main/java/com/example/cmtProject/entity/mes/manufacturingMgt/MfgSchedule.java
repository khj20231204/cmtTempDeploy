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
@Table(name = "MFG_SCHEDULES")
@NoArgsConstructor
@AllArgsConstructor
public class MfgSchedule { // 제조 계획 Entity

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MS_NO")
	private Long msNo;  				// 제조 계획 번호
	
    @Column(name = "MS_CODE")
	private String msCode;  			// 제조 계획 코드
	
    @Column(name = "MP_CODE")
	private String mpCode;  			// 생산 계획 코드
    
    @Column(name = "PDT_CODE")
	private String pdtCode;  			// 제품 코드

    @Column(name = "EMP_ID")
	private String empId;  				// 등록 직원 사번

    @Column(name = "SO_QTY")
	private String soQty;  				// 수주 수량

    @Column(name = "MS_STATUS")
	private String msStatus;  			// 제조 계획 상태

    @Column(name = "MP_PRIORITY")
	private String mpPriority;  		// 생산 우선순위

    @Column(name = "MS_CREATED_AT")
	private LocalDate msCreatedAt;  	// 등록일자
    
    @Column(name = "MS_UPDATED_AT")
	private LocalDate msUpdatedAt;  	// 수정일자
    
    @Column(name = "MS_VISIBLE")
	private String msVisible;  			// 삭제 시 숨김 처리
    
}
