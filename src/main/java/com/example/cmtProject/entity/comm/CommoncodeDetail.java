package com.example.cmtProject.entity.comm;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "COMMONCODE_DETAIL")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommoncodeDetail {
	
	@Id
	@Column(name = "CMN_DETAIL_CODE", nullable = false)
	private String cmnDetailCode;
	
	@Column(name = "CMN_CODE")
	private String cmnCode;
	
	@Column(name = "CMN_DETAIL_NAME", nullable = false)
	private String cmnDetailName;
	
	@Column(name = "CMN_DETAIL_SORT_ORDER")
	private Integer cmnDetailSortOrder;
	
	@Column(name = "CMN_DETAIL_CONTENT")
	private String cmnDetailContent;
	
	@Column(name = "CMN_DETAIL_CODE_IS_ACTIVE")
	private Character cmnDetailCodeIsActive;
	
	@Column(name = "CMN_DETAIL_VALUE")
	private String cmnDetailValue;
	
}
