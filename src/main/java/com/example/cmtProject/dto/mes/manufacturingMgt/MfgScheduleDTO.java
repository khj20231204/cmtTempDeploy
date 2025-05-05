package com.example.cmtProject.dto.mes.manufacturingMgt;

import java.time.LocalDate;

import groovy.transform.builder.Builder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MfgScheduleDTO { // 제조 계획 DTO

    private Long msNo;             // 제조 계획 번호
    private String msCode;		   // 제조 계획 코드
    private Long mpNo;		       // 생산 계획 번호
    private String mpCode;         // 생산 계획 코드
    private String pdtCode;        // 제품 코드
    private String pdtName;        // 제품 이름
    private String empId;          // 등록 직원 사번
    private String empName; 	   // 등록 직원명
    private String soQty;   	   // 수주 수량
    private String qtyUnitName;	   // 주문 단위
    private String msStatus;       // 제조 계획 상태
    private String mpPriority;     // 생산 우선순위
    private LocalDate msCreatedAt; // 등록일자
    private LocalDate msUpdatedAt; // 수정일자
    
}
