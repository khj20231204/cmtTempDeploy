package com.example.cmtProject.entity.erp.attendanceMgt;

import java.time.LocalDateTime;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.example.cmtProject.dto.erp.attendanceMgt.AttendDTO;

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
@Table(name = "WORK_TEMPLATES")
public class WorkTemplate {
	
	@Id
    @Column(name = "WT_NO")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long wtNo; // 근무 템플릿 NO 
  
    @Column(name = "WT_NAME", nullable = false)
    private String wtName; // 근무 템플릿 이름 
    
    @Column(name = "WT_START_TIME", nullable = false)
    private LocalDateTime wtStartTime; // 근무 시작 시간
    
    @Column(name = "WT_END_TIME", nullable = false)
    private LocalDateTime wtEndTime; // 근무 종료 시간
    
    @Column(name = "WT_TYPE", unique = true)
    private String wtType; // 기준 근무 유형 

    @Column(name = "WT_WORK_TIME", nullable = false)
    private int wtWorkTime; // 근로 시간 

    @Column(name = "WT_BREAK_TIME")
    private Integer wtBreakTime; // 휴식 시간 

    @Column(name = "WT_REMARK", length = 200)
    private String wtRemarks; // 비고 
    
    
    
    public WorkTemplate toEntity() {
        return WorkTemplate.builder()
            .wtNo(wtNo)
            .wtName(wtName)
            .wtStartTime(wtStartTime)
            .wtEndTime(wtEndTime)
            .wtType(wtType)
            .wtWorkTime(wtWorkTime)
            .wtBreakTime(wtBreakTime)
            .wtRemarks(wtRemarks)
            .build();
    }

    
    @Builder
	public WorkTemplate(Long wtNo, String wtName, LocalDateTime wtStartTime, LocalDateTime wtEndTime, String wtType,
			int wtWorkTime, int wtBreakTime, String wtRemarks) {
		super();
		this.wtNo = wtNo;
		this.wtName = wtName;
		this.wtStartTime = wtStartTime;
		this.wtEndTime = wtEndTime;
		this.wtType = wtType;
		this.wtWorkTime = wtWorkTime;
		this.wtBreakTime = wtBreakTime;
		this.wtRemarks = wtRemarks;
	}

    
    
    

	
	

}
