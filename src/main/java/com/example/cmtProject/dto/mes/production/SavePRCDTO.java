package com.example.cmtProject.dto.mes.production;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SavePRCDTO {
	
	private String woCode; // 두번째 하단 그리드를 그르기 위한 데이터
	private String pdtCode; //상단 그리드를 그리기 위한 데이터
	private String parentPdtCode; //첫번째 하단 왼쪽 트리 상태를 나타내기 위한 데이터
}
