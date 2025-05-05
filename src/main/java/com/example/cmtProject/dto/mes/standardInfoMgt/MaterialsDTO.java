package com.example.cmtProject.dto.mes.standardInfoMgt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaterialsDTO {

    private Long mtlNo; 		// 원자재 번호 (수동 입력 or 시퀀스)
    private String mtlCode; 	// 원자재 코드
    private String mtlName; 	// 원자재 이름
    private String mtlStandard; // 규격
    private String mtlUnit; 	// 단위 (ex: KG, EA, M 등)
    private Long mtlBasePrice; 	// 기준 단가
    private String mtlPrcType; 	// 공정 과정 코드 (예: PR, WE, PA 등)
    private String mtlCltCode; 	// 공급업체 코드 (CLIENTS 테이블의 CLT_CODE 참조 가능)
    private char mtlUseYN; 		// 사용 여부
    private String mtlComments;	// 비고   
}
