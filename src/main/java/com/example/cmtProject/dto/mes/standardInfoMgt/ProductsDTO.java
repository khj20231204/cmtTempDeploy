package com.example.cmtProject.dto.mes.standardInfoMgt;

import com.example.cmtProject.entity.mes.standardInfoMgt.Products;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductsDTO {
	
	private Long pdtNo;
	private String pdtCode;
	private String pdtName;
	private String pdtShippingPrice;
	private String pdtComments;
    private String pdtUseyn;
    private String mtlTypeCode;
    private String pdtWeight;
    private String wtTypeCode;
    private String pdtSize;
    private String ltTypeCode;
    private String pdtType;
    
    public Products toEntity() {
        return Products.builder()
            .pdtNo(this.pdtNo)
            .pdtCode(this.pdtCode)
            .pdtName(this.pdtName)
            .pdtShippingPrice(this.pdtShippingPrice)
            .pdtComments(this.pdtComments)
            .pdtUseyn(this.pdtUseyn)
            .mtlTypeCode(this.mtlTypeCode)
            .pdtWeight(this.pdtWeight)
            .wtTypeCode(this.wtTypeCode)
            .pdtSize(this.pdtSize)
            .ltTypeCode(this.ltTypeCode)
            .pdtType(this.pdtType)
            .build();
    }
}
