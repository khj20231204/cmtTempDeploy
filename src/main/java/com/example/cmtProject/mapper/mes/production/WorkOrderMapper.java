package com.example.cmtProject.mapper.mes.production;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.cmtProject.dto.mes.manufacturingMgt.MfgScheduleDTO;
import com.example.cmtProject.dto.mes.production.LotDTO;
import com.example.cmtProject.dto.mes.production.WorkOrderDTO;

@Mapper
public interface WorkOrderMapper {
	//작업지시 리스트
	List<WorkOrderDTO> selectOrderList();
	//제조계획
	List<MfgScheduleDTO> selectPlanList();
	//제조계획 -> 작업지시 insert
	void insertMsPlan(WorkOrderDTO workOrderDTO);
	//작업시작 버튼 = 날짜 업데이트&진행중
	void updateWorkStartTime(Long workOrderNo);

	//로트번호로 단일 제품 정보 들고오기
	WorkOrderDTO selectProductDetail(String lotNo);
	// Lot + workOrder
	List<LotDTO> selectLotDetail();
	//lotNo로 상품 상세정보 들고오기
	LotDTO selectLotNoDetail(Long lotNo);
	//lotNo로 품질이력 들고오기
	LotDTO selectQualityHistory(Long lotNo);
	
	//제조계획상태 변경 & 제조계획 delete
	void updateMfgStatus(String msCode);
//	void deleteMfgList(String msCode);
	//MFG_SCHEDULES - 상태 대기로 변경
	void updateMfgStatus2(String msCode);
	
	//====================================
	List<LotDTO> selectAllLotTree();
	List<LotDTO> selectLotProcessListByLotNo(String childLotCode);
	
	//그래프
	List<WorkOrderDTO> selectCompleteStatsLast7Days();
	List<LotDTO> selectTodayProcessTop5();

	//WoNo 중 최대값
	Long getWoNoMax();
	
	//가장 마지막 WoCode
	String getWoCodeLast();
	//LOT검색
	List<LotDTO> findLotsByKeyword(String string);
	
	
	

}
