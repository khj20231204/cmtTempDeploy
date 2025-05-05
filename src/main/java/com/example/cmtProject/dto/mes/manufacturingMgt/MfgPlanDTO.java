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
public class MfgPlanDTO { // 생산 계획 DTO

    private Long mpNo;              // 생산 계획 번호
    private String mpCode;			// 생산 계획 코드
    private String soCode;          // 수주 코드
    private Long soQty;				// 수주 수량
    private String pdtCode;			// 제품 코드
    private String pdtName;			// 제품명
    private String qtyUnitName;		// 주문 단위명
    private String empId;           // 등록 직원 사번
    private String empName;			// 등록 직원명
    private String mpStatus;        // 생산 계획 상태
    private String mpPriority;      // 우선순위
    private LocalDate mpCreatedAt;  // 등록일자
    private LocalDate mpUpdatedAt;  // 수정일자
    private LocalDate mpStartDate;  // 생산 시작 예정일
    private LocalDate mpEndDate;    // 생산 종료 예정일
    private String mpVisible;		// 삭제 시 숨김 처리
    
}
