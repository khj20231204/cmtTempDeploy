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
public class LotCodeDTO {

	//LOT번호 생성을 위해 pdtCode에 해당하는 공정타입(PR,WE,PA,SA)
	private String code;
	private String bomPrcType;
	
	
}
