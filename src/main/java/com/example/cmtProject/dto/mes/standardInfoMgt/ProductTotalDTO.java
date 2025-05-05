package com.example.cmtProject.dto.mes.standardInfoMgt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductTotalDTO {

	//products
	private Long pdtNo;
	private String pdtCode;
	private String pdtName;
	private String pdtShippingPrice;
	private String pdtComments;
    private String pdtUseyn;
    private String pdtWeight;
    private String pdtSize;
    private String pdtType;
	
	//MATERIAL_TYPE
    private String mtlTypeCode;
    private String mtlTypeName;
    private String mtlTypeSymbol;
    private String mtlTypeComment;
    private String mtlTypeUseryn;
	
	//WEIGHT_UNIT
    private String wtTypeCode;
	private String wtTypeName;
	private String wtTypeSymbol;
	private String wtTypeComment;
	private String wtTypeUseryn;
    
	//LENGTH_UNIT
	private String ltTypeCode;
    private String ltTypeName;
    private String ltTypeSymbol;
    private String ltTypeComment;
    private String ltTypeUseryn;
	
	//PRODUCT_TYPE
    private String pdtTypeCode;
	private String pdtTypeName;
	private String pdtTypeUseyn;
}
