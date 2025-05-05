package com.example.cmtProject.dto.erp.attendanceMgt;

import java.time.LocalDateTime;

import com.example.cmtProject.entity.erp.attendanceMgt.Attend;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AttendDTO {
	
    private Long atdNo; // 출결NO
	
    private Long empNo; // 사원번호
    
    private String empName; // 사원이름
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime atdDate; // 출근일자
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime atdLeave; // 퇴근일자

    private String atdType; // 출결유형
    
    private String atdStatus; // 출결상태

    private String atdRemarks; // 비고
    
    
    // join매핑을 위한 dto
    private String statusLink; // 출결 상태 공통코드
    private String typeLink; // 출결 타입 공통코드
    private String deptName; // 부서명 공통코드
    
    
    
    
    public Attend toEntity() {
        return Attend.builder()
        	.atdNo(atdNo)
            .empNo(empNo)
            .empName(empName)
            .atdDate(atdDate)
            .atdLeave(atdLeave)
            .atdType(atdType)
            .atdStatus(atdStatus)
            .atdRemarks(atdRemarks)
            .build();
    }

    @Builder
	public AttendDTO(Long atdNo, Long empNo, String empName, LocalDateTime atdDate, LocalDateTime atdLeave, 
			String atdType, String atdStatus, String atdRemarks) {
    	this.atdNo = atdNo;
		this.empNo = empNo;
		this.empName = empName;
		this.atdDate = atdDate;
		this.atdLeave = atdLeave;
		this.atdType = atdType;
		this.atdStatus = atdStatus;
		this.atdRemarks = atdRemarks;
	}
    


    
}
















