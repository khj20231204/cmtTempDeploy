package com.example.cmtProject.dto.erp.salaries;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO { // 급여 지급 내역 DTO

    private Long payNo;              			// 지급 번호
    private String empName;			 			// 사원명
    private String empNo;			 			// 사원번호 
    private String empId;			 			// 사원번호(사번)
    private String deptName;		 			// 부서명
    private String position;     	 			// 직급
    private String empType;          			// 고용유형
    private LocalDate payDate;       			// 지급일
    
    private String payMonth; 	 	 	 		// 지급월 -- 급여 대장
    private Long empCount;				 		// 인원수 -- 급여 대장
    private Long totalPayAmount; 				// 총지급액 -- 급여 대장
    private BigDecimal totalBonusAmount; 		// 총수당액 -- 급여 대장
    private BigDecimal totalTaxAmount; 			// 총공제액 -- 급여 대장

    private Long payBasic;           			// 기본급
    private BigDecimal payBonusOvertime;   		// 야근수당
    private BigDecimal payBonusHoliday;    		// 명절수당
    private Long payBonusTotal;      			// 총수당금액

    private BigDecimal payTaxPension;      		// 국민연금
    private BigDecimal payTaxCare;         		// 장기요양보험
    private BigDecimal payTaxHealth;       		// 건강보험
    private BigDecimal payTaxEmployment;   		// 고용보험
    private BigDecimal payTaxIncome;       		// 소득세
    private BigDecimal payTaxResidence;    		// 주민세
    private Long payTaxTotal;        			// 총공제금액
    private Long payTotal;           			// 실지급액
    private String salBankName;	     			// 은행명
    private String salBankAccount;	 			// 계좌번호
    //private String payStatus;        			// 지급 상태

    
    // Builder 패턴 자동 생성
    @Builder
    public PaymentDTO(Long payNo, String empName, String empId, String deptName, String position, String empType,
    		LocalDate payDate, Long payBasic, BigDecimal payBonusOvertime, BigDecimal payBonusHoliday, Long payBonusTotal,
    		BigDecimal payTaxPension, BigDecimal payTaxCare, BigDecimal payTaxHealth, BigDecimal payTaxEmployment,
    		BigDecimal payTaxIncome, BigDecimal payTaxResidence, Long payTaxTotal, Long payTotal, String payStatus, String salBankName, String salBankAccount,
    		String payMonth, Long empCount, Long totalPayAmount, BigDecimal totalBonusAmount, BigDecimal totalTaxAmount) {
    	this.payNo = payNo;
    	this.empName = empName;
    	this.empId = empId;
    	this.deptName = deptName;
    	this.position = position;
    	this.empType = empType;
    	this.payDate = payDate;
    	this.payBasic = payBasic;
    	this.payBonusOvertime = payBonusOvertime;
    	this.payBonusHoliday = payBonusHoliday;
    	this.payBonusTotal = payBonusTotal;
    	this.payTaxPension = payTaxPension;
    	this.payTaxCare = payTaxCare;
    	this.payTaxHealth = payTaxHealth;
    	this.payTaxEmployment = payTaxEmployment;
    	this.payTaxIncome = payTaxIncome;
    	this.payTaxResidence = payTaxResidence;
    	this.payTaxTotal = payTaxTotal;
    	this.payTotal = payTotal;
    	//this.payStatus = payStatus;
    	this.salBankName = salBankName;
    	this.salBankAccount = salBankAccount;
    	this.payMonth = payMonth;
    	this.empCount = empCount;
    	this.totalPayAmount = totalPayAmount;
    	this.totalBonusAmount= totalBonusAmount;
    	this.totalTaxAmount = totalTaxAmount;
    }
    
}
