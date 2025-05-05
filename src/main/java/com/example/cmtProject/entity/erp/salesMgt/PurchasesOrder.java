package com.example.cmtProject.entity.erp.salesMgt;


import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
//에러처리
@Data
@Entity
@Table(name = "PURCHASES_ORDER")
@NoArgsConstructor
@AllArgsConstructor
public class PurchasesOrder {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_PURCHASESORDER_PO_NO" )
	@SequenceGenerator(name = "SEQ_PURCHASESORDER_PO_NO", sequenceName="SEQ_PURCHASESORDER_PO_NO", allocationSize = 1)
	@Column(name = "PO_NO")
	private Long poNo; //발주주문번호(pk)
	
	@Column(name = "PO_CODE")
	private String poCode;
	
	@Column(name = "PO_DATE") //발주일자
    @DateTimeFormat(pattern = "yy-MM-dd")
	private LocalDate poDate;
	
	@Column(name = "MTL_RCV_DATE") //입고일자
    @DateTimeFormat(pattern = "yy-MM-dd")
	private LocalDate mtlRcvDate;  
	
	@Column(name = "EMP_NO")
	private Long empNo;  //사원번호
	
	@Column(name = "WHS_CODE")
	private String whsCode; //창고 코드
	
	@Column(name = "MTL_CODE")
	private String mtlCode; //원자재 코드
	
	@Column(name = "CLT_CODE")
	private String cltCode;  //공급업체 코드
	
	@Column(name = "PO_QTY")
	private String poQty;  //수량
	
	@Column(name = "MTL_RCV_PRICE")
	private String mtlRcvPrice;  //입고단가
	
	@Column(name = "PO_VALUE")
	private Integer poValue;  //공급가액
	
	@Column(name = "PO_STATUS")
	private String poStatus;  //종결여부
	
	@Column(name = "PO_COMMENTS")
	private String poComments;  //비고
	
	@Column(name = "PO_USE_YN")
	private String poUseYn;  //사용여부
	
	@Column(name = "QTY_UNIT")
	private String qtyUnit;  //단위
	
	@Column(name = "MTL_RCV_QTY")
	private String mtlRcvQty;  //발주 입고량
	
}
