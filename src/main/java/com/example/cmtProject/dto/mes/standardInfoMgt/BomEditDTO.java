package com.example.cmtProject.dto.mes.standardInfoMgt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BomEditDTO {

	//그리드에서 더블클릭으로 하나씩 값을 가져와서 업데이트
	private Long BomNo;
	private String columnName;
	private String value;
}
