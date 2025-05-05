package com.example.cmtProject.entity.mes.standardInfoMgt;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "MATERIALS")
@NoArgsConstructor
@AllArgsConstructor
public class Materials {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "MTL_NO")
	private Long mtlNo;
	
	@Column(name = "MTL_CODE")
	private String mtlCode;
	
	@Column(name = "MTL_NAME")
	private String mtlName;
	
	@Column(name = "MTL_STANDARD")
	private String mtlStandard;
	
	@Column(name = "MTL_UNIT")
	private String mtlUnit;
	
	@Column(name = "MTL_BASE_PRICE")
	private Long mtlBasePrice;
	                   
	@Column(name = "MTL_PRC_TYPE")
	private String mtlPrcType;
	
	@Column(name = "MTL_CLT_CODE")
	private String mtlCltCode; 
	
	@Column(name = "MTL_USE_YN")
	private Character mtlUseYN;
	
	@Column(name = "MTL_COMMENTS")
	private String mtlComments;
}
