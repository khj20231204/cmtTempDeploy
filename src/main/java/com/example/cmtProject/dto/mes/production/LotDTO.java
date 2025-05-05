package com.example.cmtProject.dto.mes.production;

import java.security.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LotDTO {
	
	private Long num;
	private Long lotNo;
	private String childLotCode;
    private String parentLotCode;
    private String childPdtCode;
    private String parentPdtCode;
    private LocalDate createDate;
    private String prcType;
    private String lineCode;
    private String eqpCode;
    private String woCode;
    private String startTime;
    private String finishTime;
    private String woStatusNo;
    private String woStatusName;
    private String woStatusCode;
    private String bomQty;
	private String bomUnit;
	private String path;
    private String useYn;
    
    //------------------
    private Long woNo;           // 작업지시 번호
    private LocalDate orderDate;        // 지시 날짜
    private String woQty;            // 지시 수량
    private String soQty;            // 지시 수량
    private String status;              // 진행 상태
    private String pdtName;              // 상품이름
    private LocalDate dueDate;          // 납기일
    private String comments;            // 비고
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate woStartDate; // 작업 시작 시간
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate woEndDate;   // 작업 종료 시간
    
    //=============================
    private String msNo;
    private String msCode;
    private String prcCode;
    private String allocatedQty;
    private LocalDate msStartDate;
    private LocalDate msEndDate;
    private String empId;
    private String empName;
    private String msStatus;
    private String prcTypeName;
    
    //=============================
    private String processName; // 공정명
    private int incompleteCount; // 미완료 수
    
    //=================================
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime  ipiStartTime; //검사 시작 시간
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime ipiEndTime; // 검사 종료시간
    private Double ipiMeasuredLengthValue;
    private Double ipiMeasuredWeightValue;
    private String ipiInspectionResult;
    private String qcmName;
    private String keyword;
	 	
}


