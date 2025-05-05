package com.example.cmtProject.dto.mes.production;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkOrderDTO {

    private Long woNo;           // 작업지시 번호
    private String woCode;       // 작업지시 코드
    private String pdtCode;             // 제품 코드
    private String pdtName;             // 제품 이름
    private LocalDate orderDate;        // 지시 날짜
    private String woQty;            // 지시 수량
    private String soQty;            // 지시 수량
    private String status;              // 진행 상태 //안씀 woStatusCode으로 변경
    private LocalDate dueDate;          // 납기일
    private String comments;            // 비고
    private String useYn;               // 사용 여부
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate woStartDate; // 작업 시작 시간
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate woEndDate;   // 작업 종료 시간
    private Long workOrderNo;
    
    //=============================
    private String msNo;
    private String msCode;
    private String prcCode;
    private String prcTypeCode;
    private String lineCode;
    private String allocatedQty;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate msStartDate;
    private LocalDate msEndDate;
    private Long empId;
    private String empName;
    private String msStatus;
    private String woStatusCode;
    private String woStatusName;
    private String prcTypeName;
    
    //======================
    private String workDate;
    private int completeCount;

}
