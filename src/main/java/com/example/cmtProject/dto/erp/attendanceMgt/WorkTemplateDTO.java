package com.example.cmtProject.dto.erp.attendanceMgt;

import java.time.LocalDateTime;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
public class WorkTemplateDTO {
	
    private Long wtNo; // 근무 템플릿 NO
    private String wtName; // 근무 템플릿 이름
    @JsonFormat(pattern = "HH:mm")
    private LocalTime wtStartTime; // 근무 시작 시간
    @JsonFormat(pattern = "HH:mm")
    private LocalTime wtEndTime; // 근무 종료 시간
    private String wtType; // 기준 근무 유형
    private int wtWorkTime; // 근로 시간
    private int wtBreakTime; // 휴식 시간
    private String wtRemarks; // 비고
    
    
    public WorkTemplateDTO toEntity() {
        return WorkTemplateDTO.builder()
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
	public WorkTemplateDTO(Long wtNo, String wtName, LocalTime wtStartTime, LocalTime wtEndTime, String wtType,
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
