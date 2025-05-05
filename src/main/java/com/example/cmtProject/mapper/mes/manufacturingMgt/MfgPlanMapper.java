package com.example.cmtProject.mapper.mes.manufacturingMgt;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.cmtProject.dto.mes.manufacturingMgt.MfgPlanDTO;
import com.example.cmtProject.dto.mes.manufacturingMgt.MfgPlanSalesOrderDTO;

@Mapper
public interface MfgPlanMapper { // 생산 계획 Mapper

	// 생산 계획 목록 조회
	List<MfgPlanDTO> getMfgPlanTotalList();
	
	// 생산 계획 등록 시 수주 목록 조회
	List<MfgPlanSalesOrderDTO> getSoList();

	// 생산 계획 등록
	void insertMfgPlan(MfgPlanDTO dto);

	// 생산 계획 등록 시 재고 조회
	boolean checkStock(@Param("soCode") String soCode, @Param("soQty") Long soQty);
	
	// 생산 계획 수정
	void updateMpPlan(List<MfgPlanDTO> mpList);

	// 생산 계획 삭제 (숨김 처리)
	void isVisibleToFalse(List<Long> mpNos);

}
