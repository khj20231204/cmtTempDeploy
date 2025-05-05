package com.example.cmtProject.dto.erp.saleMgt;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchasesOrderMainDTO {

	//Purchases entity
	private Long poNo; //발주주문번호(pk)
	private String poCode;  //발주코드
	private LocalDate poDate; //발주일자
	private LocalDate rcvDate; //입고일자
	private int poQuantity;  //수량
	private int mtlReceivingPrice;  //입고단가
	private int poValue;  //공급가액
	private String poStatus;  //종결여부
	private String poComments;  //비고
	
	//Clients entity
	private Long cltNo;  //거래처no
	private String cltCode; //거래처코드
	private String cltName; //거래처명
	
	//Material entity
	private Long mtlNo;
	private String mtlCode;
	private String mtlName;
	
	//Warehouses entity
	private Long whsNo;  //창고no(pk)
	private String whsCode; //창고코드
	private String whsName; //창고명
	
	//Employess entity
	private Long empNo;
    private String empId;
    private String empName; 
    
	//Status entity
	private String statusCode;
	private String statusName;
	
	//새로 전부 만든 필드
	private String poUseYn;
	private String poVisible;
	private String qtyUnit;
	private String poQty;
	private String mtlRcvQty;
	private String mtlRcvPrice;
	private LocalDate mtlRcvDate;
}
