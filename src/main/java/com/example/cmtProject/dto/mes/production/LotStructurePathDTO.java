package com.example.cmtProject.dto.mes.production;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LotStructurePathDTO {

	private Long idx;
	private Long lotLevel;
	private Long lotNo;       
	private String parentLotCode; 
	private String childLotCode; 
	private String parentPdtCode; 
	private String childPdtCode; 
	private LocalDate createDate;         
	private String prcType; 
	private String lineCode; 
	private String eqpCode; 
	private String woCode; 
	private String startTime;         
	private String finishTime;         
	private String woStatusNo; 
	private String bomQty;
	private String bomUnit;
	private String woQty;
	private String path;
	private String useYn; 
}
