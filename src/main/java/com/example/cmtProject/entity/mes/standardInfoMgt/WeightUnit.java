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
@Table(name = "WEIGHT_UNIT")
@NoArgsConstructor
@AllArgsConstructor
public class WeightUnit {

	@Id
	@Column(name = "WT_TYPE_CODE")
	private String wtTypeCode;
	
	@Column(name = "WT_TYPE_NAME")
	private String wtTypeName;
	
	@Column(name = "WT_TYPE_SYMBOL")
	private String wtTypeSymbol;
	
	@Column(name = "WT_TYPE_COMMENT")
	private String wtTypeComment;
	
	@Column(name = "WT_TYPE_USERYN")
	private String wtTypeUseryn;
}
