package com.example.cmtProject.entity.erp.attendanceMgt;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.example.cmtProject.dto.erp.attendanceMgt.WorkTimeDTO;
import com.example.cmtProject.entity.erp.employees.Employees;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@Table(name = "WORK_TIMES")
public class WorkTime {

    @Id
    @Column(name = "WKT_NO")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long wktNo; // 근무시간NO (WKT_NO)

    @Column(name = "EMP_NO", unique = true)
    private Long empNo; // 사원번호 (EMP_NO), 사원 테이블 참조

    @Column(name = "WKT_DATE")
    private LocalDateTime wktDate; // 근무일자 (WKT_DATE)

    @Column(name = "WKT_START_TIME")
    private LocalDateTime wktStartTime; // 출근시간 (WKT_START_TIME)

    @Column(name = "WKT_END_TIME")
    private LocalDateTime wktEndTime; // 퇴근시간 (WKT_END_TIME)

    @Column(name = "WKT_STATUS")
    private String wktStatus; // 근무상태 (WKT_STATUS)

    @Column(name = "WKT_TYPE")
    private String wktType; // 기준근무유형 (WKT_TYPE)

    @Column(name = "WKT_REMARKS", length = 200)
    private String wktRemarks; // 비고 (WKT_REMARKS)
    
    public WorkTime toDto() {
        return WorkTime.builder()
        	.wktNo(wktNo)
            .empNo(empNo)
            .wktDate(wktDate)
            .wktStartTime(wktStartTime)
            .wktEndTime(wktEndTime)
            .wktStatus(wktStatus)
            .wktType(wktType)
            .wktRemarks(wktRemarks)
            .build();
    }
    
    @Builder
    public WorkTime(Long wktNo, Long empNo, LocalDateTime wktDate,
                    LocalDateTime wktStartTime, LocalDateTime wktEndTime,
                    String wktStatus, String wktType, String wktRemarks) {
        this.wktNo = wktNo;
        this.empNo = empNo;
        this.wktDate = wktDate;
        this.wktStartTime = wktStartTime;
        this.wktEndTime = wktEndTime;
        this.wktStatus = wktStatus;
        this.wktType = wktType;
        this.wktRemarks = wktRemarks;
    }

}
