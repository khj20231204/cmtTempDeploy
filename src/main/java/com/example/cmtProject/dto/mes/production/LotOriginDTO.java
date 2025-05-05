package com.example.cmtProject.dto.mes.production;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LotOriginDTO {
	
	private Long num;
	private Long lotNo;
	private String childLotCode;
    private String parentLotCode;
    private String childPdtCode;
    private String parentPdtCode;
    private LocalDate createDate;
    private LocalDate endDate;
    private String prcType;
    private String bomQty;
    private String bomUnit;
    private String lineCode;
    private String eqpCode;
    private String woCode;
    private String woQty;
    private String startTime;
    private String finishTime;
    private String woStatusNo;
    private String useYn;
}
