package com.example.cmtProject.entity.mes.standardInfoMgt;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Table(name = "PRODUCTS")
@NoArgsConstructor
@AllArgsConstructor
@Builder 
public class Products {
	
	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_PRODUCT_PDT_NO")
    @SequenceGenerator(name = "SEQ_PRODUCT_PDT_NO", sequenceName = "SEQ_PRODUCT_PDT_NO")
    @Column(name = "PDT_NO")
	private Long pdtNo;

    @Column(name = "PDT_CODE")
	private String pdtCode;
	
    @Column(name = "PDT_NAME")
	private String pdtName;
	
    @Column(name = "PDT_SHIPPING_PRICE")
	private String pdtShippingPrice;
	
    @Column(name = "PDT_COMMENTS")
	private String pdtComments;
    
    @Column(name = "PDT_USEYN")
    private String pdtUseyn;
    
    @Column(name = "MTL_TYPE_CODE")
    private String mtlTypeCode;
    
    @Column(name = "PDT_WEIGHT")
    private String pdtWeight;
    
    @Column(name = "WT_TYPE_CODE")
    private String wtTypeCode;
    
    @Column(name = "PDT_SIZE")
    private String pdtSize;
    
    @Column(name = "LT_TYPE_CODE")
    private String ltTypeCode;
    
    @Column(name = "PDT_TYPE")
    private String pdtType;
        
}


