package com.example.cmtProject.entity.erp.salaries;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.cmtProject.dto.erp.salaries.PaymentDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "PAYMENTS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment { // 급여 지급 내역 Entity

    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_PAYMENTS_PAY_NO" )
	@SequenceGenerator(name = "SEQ_PAYMENTS_PAY_NO", sequenceName="SEQ_PAYMENTS_PAY_NO", allocationSize = 1)
    @Column(name = "PAY_NO")
    private Long payNo; 					// 지급 번호
    
    @Column(name = "EMP_NAME")
    private String empName;			 		// 사원명
    
    @Column(name = "EMP_ID")
    private String empId; 					// 사원 번호

    @Column(name = "DEPT_NAME")
    private String deptName;		 		// 부서명
    
    @Column(name = "POSITION")
    private String position;     	 		// 직급
    
    @Column(name = "EMP_TYPE")
    private String empType;         		// 고용유형
    
    @Column(name = "PAY_DATE")
    private LocalDate payDate; 				// 지급일

    @Column(name = "PAY_BASIC")
    private Long payBasic; 					// 기본급

    @Column(name = "PAY_BONUS_OVERTIME")
    private BigDecimal payBonusOvertime; 	// 야근수당

    @Column(name = "PAY_BONUS_HOLIDAY")
    private BigDecimal payBonusHoliday; 	// 명절수당

    @Column(name = "PAY_BONUS_TOTAL")
    private Long payBonusTotal; 			// 총수당금액

    @Column(name = "PAY_TAX_PENSION")
    private BigDecimal payTaxPension; 		// 국민연금

    @Column(name = "PAY_TAX_CARE")
    private BigDecimal payTaxCare; 			// 장기요양보험

    @Column(name = "PAY_TAX_HEALTH")
    private BigDecimal payTaxHealth; 		// 건강보험

    @Column(name = "PAY_TAX_EMPLOYMENT")
    private BigDecimal payTaxEmployment; 	// 고용보험

    @Column(name = "PAY_TAX_INCOME")
    private BigDecimal payTaxIncome; 		// 소득세

    @Column(name = "PAY_TAX_RESIDENCE")
    private BigDecimal payTaxResidence; 	// 주민세

    @Column(name = "PAY_TAX_TOTAL")
    private Long payTaxTotal; 				// 총공제금액

    @Column(name = "PAY_TOTAL")
    private Long payTotal; 					// 실수령액
    
    @Column(name = "SAL_BANK_NAME")
    private String salBankName;  			// 은행명
    
    @Column(name = "SAL_BANK_ACCOUNT")
    private String salBankAccount;  		// 계좌번호
    
    
    // Payment -> PaymentDTO 로 변환하는 toDto() 메서드
    public PaymentDTO toDto() {
        return PaymentDTO.builder()
                .payNo(payNo)
                .empName(empName)
                .empId(empId)
                .deptName(deptName)
                .position(position)
                .empType(empType)
                .payDate(payDate)
                .payBasic(payBasic)
                .payBonusOvertime(payBonusOvertime)
                .payBonusHoliday(payBonusHoliday)
                .payBonusTotal(payBonusTotal)
                .payTaxPension(payTaxPension)
                .payTaxCare(payTaxCare)
                .payTaxHealth(payTaxHealth)
                .payTaxEmployment(payTaxEmployment)
                .payTaxIncome(payTaxIncome)
                .payTaxResidence(payTaxResidence)
                .payTaxTotal(payTaxTotal)
                .payTotal(payTotal)
                .salBankName(salBankName)        
                .salBankAccount(salBankAccount)
                .build();
    }

}   