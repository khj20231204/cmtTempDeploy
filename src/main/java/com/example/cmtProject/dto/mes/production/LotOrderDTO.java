package com.example.cmtProject.dto.mes.production;

import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LotOrderDTO {

	//LOT-20250421-PR-02 뒤에 02부분
	private String prcType;
	private Integer maxSeq = 0; //Integer는 null을 받을 수 있다
}
