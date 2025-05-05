package com.example.cmtProject.entity.erp.attendanceMgt;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.example.cmtProject.dto.erp.attendanceMgt.LeaveDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@Table(name = "LEAVES")
public class Leave {

    @Id
    @Column(name = "LEV_NO")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long levNo; // 휴가NO
    
    @Column(name = "EMP_ID")
    private String empId; // 사원번호 (EMP_NO), 사원 테이블 참조

    @Column(name = "LEV_TYPE")
    private String levType; // 휴가 유형

    @Column(name = "LEV_START_DATE")
    private LocalDate levStartDate; // 휴가 시작일
    
    @Column(name = "LEV_END_DATE")
    private LocalDate levEndDate; // 휴가 종료일
    
    @Column(name = "LEV_DAYS")
    private Double levDays; // 휴가 신청 일수
    
    @Column(name = "LEV_USED_DAYS")
    private Double levUsedDays; // 사용한 휴가 일수
    
    @Column(name = "LEV_LEFT_DAYS")
    private Double levLeftDays; // 휴가 남은 일수
    
    @Column(name = "LEV_REASON")
    private String levReason; // 휴가 사유
    
    @Column(name = "LEV_REQ_DATE")
    private LocalDateTime levReqDate; // 휴가 신청 일시
    
    @Column(name = "LEV_APPROVAL_STATUS")
    private String levApprovalStatus; // 승인 상태
    
    @Column(name = "LEV_APPROVER")
    private String levApprover; // 승인자
    
    @Column(name = "LEV_APPROVAL_DATE")
    private LocalDateTime levApprovalDate; // 승인 일시
    
    @Column(name = "LEV_REMARKS", length = 200)
    private String levRemarks; // 비고 (WKT_REMARKS)
    
    @Column(name = "DOC_ID")
    private String docId; // 문서 아이디 
    
    public Leave toDTO() {
    	return Leave.builder()
    			.levNo(levNo)
    			.empId(empId)
    			.levType(levType)
    			.levStartDate(levStartDate)
    			.levEndDate(levEndDate)
    			.levDays(levDays)
    			.levUsedDays(levUsedDays)
    			.levLeftDays(levLeftDays)
    			.levReason(levReason)
    			.levReqDate(levReqDate)
    			.levApprovalStatus(levApprovalStatus)
    			.levApprover(levApprover)
    			.levApprovalDate(levApprovalDate)
    			.levRemarks(levRemarks)
    			.docId(docId)
    			.build();
    }
    
    @Builder
	public Leave(Long levNo, String empId,String levType, LocalDate levStartDate, LocalDate levEndDate,
			Double levDays, Double levUsedDays, Double levLeftDays, String levReason, LocalDateTime levReqDate, String levApprovalStatus, String levApprover,
			LocalDateTime levApprovalDate, String levRemarks, String docId) {
		this.levNo = levNo;
		this.levType = levType;
		this.levStartDate = levStartDate;
		this.levEndDate = levEndDate;
		this.levDays = levDays;
		this.levUsedDays = levUsedDays;
		this.levLeftDays = levLeftDays;
		this.levReason = levReason;
		this.levReqDate = levReqDate;
		this.levApprovalStatus = levApprovalStatus;
		this.levApprover = levApprover;
		this.levApprovalDate = levApprovalDate;
		this.levRemarks = levRemarks;
		this.docId = docId;
	}
}
