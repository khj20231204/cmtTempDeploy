package com.example.cmtProject.dto.mes.qualityControl;

import lombok.Data;

@Data
public class InspectionSummaryDTO {
	
	private String iqcDate;
	private String fqcDate;
	private String ipiDate;
	private Integer passCount;
	private Integer inProgressCount;
	private Integer failCount;
	

}
