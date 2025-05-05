package com.example.cmtProject.entity.mes.standardInfoMgt;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "PRODUCT_TYPE")
@NoArgsConstructor
@AllArgsConstructor
public class ProductType {

	@Id
	@Column(name = "PDT_TYPE_CODE")
	private String pdtTypeCode;
	 
	@Column(name = "PDT_TYPE_NAME")
	private String pdtTypeName;
	 
	@Column(name = "PDT_TYPE_USEYN")
	private String pdtTypeUseyn;
}
