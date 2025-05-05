package com.example.cmtProject.dto.mes.standardInfoMgt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BomStructurePathDTO {

	//BOM테이블에서 PATH가져오기 위해 만듦
	private String idx;
	private String bomLevel;
    private String childItemCode;
    private String parentPdtCode;
    private String itemType;
    private String bomPrcType;
    private String bomQty;
    private String bomUnit;
    private String path;
}
