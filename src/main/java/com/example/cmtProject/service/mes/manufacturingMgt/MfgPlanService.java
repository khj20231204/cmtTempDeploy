package com.example.cmtProject.service.mes.manufacturingMgt;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.cmtProject.dto.mes.manufacturingMgt.MfgPlanDTO;
import com.example.cmtProject.dto.mes.manufacturingMgt.MfgPlanSalesOrderDTO;
import com.example.cmtProject.mapper.mes.manufacturingMgt.MfgPlanMapper;

import jakarta.transaction.Transactional;

@Service
public class MfgPlanService { // 생산 계획 Service

	@Autowired
	private MfgPlanMapper mfgPlanMapper;
	
	// 생산 계획 내역 조회
	public List<MfgPlanDTO> getMfgPlanTotalList(){
		return mfgPlanMapper.getMfgPlanTotalList();
	}

	// 생산 계획 등록 시 수주 내역 조회
	@Transactional
	public List<MfgPlanSalesOrderDTO> getSoList() {
		return mfgPlanMapper.getSoList();
	}

	// 생산 계획 등록
	public void insertMfgPlan(MfgPlanDTO dto) {
		mfgPlanMapper.insertMfgPlan(dto);
	}
	
	// 생산 계획 수정
	@Transactional
	public void updateMpPlan(List<MfgPlanDTO> mpList) {
		mfgPlanMapper.updateMpPlan(mpList);
	}

	// 생산 계획 삭제 (숨김 처리)
	@Transactional
	public void isVisibleToFalse(List<Long> mpNos) {
		mfgPlanMapper.isVisibleToFalse(mpNos);
	}	

}
