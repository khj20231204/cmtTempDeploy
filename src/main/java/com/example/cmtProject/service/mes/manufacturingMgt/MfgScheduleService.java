package com.example.cmtProject.service.mes.manufacturingMgt;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.cmtProject.dto.mes.manufacturingMgt.MfgScheduleDTO;
import com.example.cmtProject.dto.mes.manufacturingMgt.MfgSchedulePlanDTO;
import com.example.cmtProject.mapper.mes.manufacturingMgt.MfgScheduleMapper;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MfgScheduleService { // 제조 계획 Service

	@Autowired
	private MfgScheduleMapper mfgScheduleMapper;	
	
	// 제조 계획 내역 조회
	public List<MfgScheduleDTO> getMfgScheduleTotalList() {
		return mfgScheduleMapper.getMfgScheduleTotalList();
	}

	// 제조 계획 등록 시 생산 계획 내역 조회
	public List<MfgSchedulePlanDTO> getMpList() {
		return mfgScheduleMapper.getMpList();
	}

	// 제조 계획 등록
	@Transactional
	public void registMsPlan(MfgScheduleDTO mfgScheduleDTO) {
		mfgScheduleMapper.registMsPlan(mfgScheduleDTO); // 제조 계획 등록
		updateMpStatus(mfgScheduleDTO.getMpCode()); // 생산 계획 상태 변경
		
		log.info("확인: {}", mfgScheduleDTO.getMsCode());
		mfgScheduleMapper.insertBomDetailFromBom(mfgScheduleDTO.getMsCode()); // 제조 계획 상세 데이터 등록
	} 
	
	// 제조 계획 등록 시 생산 계획 상태 업데이트
	@Transactional
	public void updateMpStatus(String mpCode) {
		mfgScheduleMapper.updateMpStatus(mpCode);
	}

	// 제조 계획 상세 조회
	@Transactional
	public List<Map<String, Object>> getBomDetailByMsCode(String msCode) {
	    log.info("BOM 상세 삽입 시작: {}", msCode);
	    
	    return mfgScheduleMapper.selectBomDetailByMsCode1(msCode);
	}

	// 제조 계획 삭제 (숨김 처리)
	public void isVisibleToFalse(List<Long> msNos) {
		mfgScheduleMapper.isVisibleToFalse(msNos);
	}

}
