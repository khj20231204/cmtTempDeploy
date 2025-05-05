package com.example.cmtProject.mapper.mes.manufacturingMgt;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.cmtProject.dto.mes.manufacturingMgt.MfgScheduleDTO;
import com.example.cmtProject.dto.mes.manufacturingMgt.MfgSchedulePlanDTO;

@Mapper
public interface MfgScheduleMapper { // 제조 계획 Mapper
	
	// 제조 계획 목록 조회
	List<MfgScheduleDTO> getMfgScheduleTotalList();

	// 제조 계획 등록
	void registMsPlan(MfgScheduleDTO mfgScheduleDTO);
	
	// 제조 계획 등록 시 생산 계획 내역 조회
	List<MfgSchedulePlanDTO> getMpList();
		
	// 제조 계획 등록 시 제조 계획 상세 데이터 등록
	void insertBomDetailFromBom(@Param("msCode") String msCode);
	
	// 제조 계획 등록 시 생산 계획 상태 업데이트
	void updateMpStatus(String mpNo);

	// 제조 계획 상세 조회
	List<Map<String, Object>> selectBomDetailByMsCode1(@Param("msCode") String msCode);

	// 제조 계획 삭제 (숨김 처리)
	void isVisibleToFalse(List<Long> msNos);

}
