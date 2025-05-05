package com.example.cmtProject.dto.erp.saleMgt;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.example.cmtProject.entity.erp.salesMgt.SalesOrder;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalesOrderDTO {
	
	private Long soNo; //수주주문번호(pk)
	private String soCode;	//수주코드
	private LocalDate soDate;  //수주일자
	private LocalDate shipDate;  //출하일자
	private Long empNo; //사원코드
	private String whsCode;  //창고코드
	private String pdtCode;  //제품코드
	private String cltCode;	 //고객코드
	private Integer soQty;  //수량
	private Integer pdtPrice; //출고단가
	private Integer soValue;  //공급가액
	private String soStatus; //종결여부
	private String soComments;  //비고
}
