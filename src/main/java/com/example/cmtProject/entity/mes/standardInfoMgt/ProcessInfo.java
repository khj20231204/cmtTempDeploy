package com.example.cmtProject.entity.mes.standardInfoMgt;

import java.time.LocalDate;

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
@Table(name = "PROCESS_INFO")
@NoArgsConstructor
@AllArgsConstructor
public class ProcessInfo {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "PRC_NO")
	private Long prcNo;
	
	@Column(name = "PRC_TYPE_CODE")
	private String prcTypeCode;
	
	@Column(name = "PRC_TYPE_NAME")
	private String prcTypeName;

	@Column(name = "PRC_PRIORITY")
	private String prcPriority; 
	
	@Column(name = "CREATE_DATE")
	private LocalDate createDate;
	
	@Column(name = "PRC_COMMENT")
	private String prcComment;
}
