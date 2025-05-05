package com.example.cmtProject.mapper.mes.production;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.cmtProject.dto.mes.production.LotCodeDTO;
import com.example.cmtProject.dto.mes.production.WorkOrderDTO;
import com.example.cmtProject.dto.mes.standardInfoMgt.BomInfoDTO;
import com.example.cmtProject.dto.mes.standardInfoMgt.ProductTotalDTO;

@Mapper
public interface ProductionPrcMapper {

	//status가 standby인 것만 가져온다
	List<WorkOrderDTO> selectWoStandByList();

	//woCode select
	List<WorkOrderDTO> selectWoCodeList(String data);

	/*
	재귀 결과 가져오기
	PARENT_PDT_CODE CHILD_ITEM_CODE
			WIP004	 MTL-005
			WIP005	 MTL-006
			WIP009	 WIP004
			WIP009	 WIP005
			WIP009	 MTL-009
	 */
	List<BomInfoDTO> selectPdtCodeList(String data);

	/*
	재귀의 결과에 중복 제거한 pdtCode만 가져오기
	WIP009
	WIP004
	WIP005
	MTL-009
	MTL-005
	MTL-006
	*/
	List<LotCodeDTO> selectPdtCodeArray(String pdtCode);

	String getPrcType(String pdtCode);

	//BOM 계층에서 자식 제품 가져와서 중복 제거
	List<String> selectChildPdtCodeList(String pdtCode);

	//BOM 계층에서 부모 제품 가져와서 중복 제거
	List<String> selectParentdPdtCodeList(String pdtCode);

	void updateWoStatus(String woCode);

	List<ProductTotalDTO> selectProductInfo(String pdtCode);
}
