package com.example.cmtProject.dto.mes.standardInfoMgt;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BomInfoDTO {

	// BOM
    private Long bomNo; 		// BOM 고유번호
	private int bomLevel; 		// 재귀시 순서
    private String parentPdtCode; 	// 부모 코드
    private String childItemCode; 	// 자식 코드
    private String itemType; 	//코드 유형(RAW_MATERIAL / SEMI_FINISHED)
    private String bomQty; 		// 투입 수량
    private String bomUnit; 	// 투입 단위 (예: EA, TON)
    private String bomPrcType; 	// 투입 공정 단계 (예: PR, WE, PA, SA)
    private LocalDate bomDate;  // 입력 날짜
    private String comments; 	// 비고
    private String useYN; 		// 사용 여부
    private String path;     	//하단 왼쪽에 경로를 보여주기 위해서 path값 따로 추가 
		
}

