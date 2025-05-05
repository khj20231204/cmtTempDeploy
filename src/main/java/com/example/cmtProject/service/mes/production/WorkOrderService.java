package com.example.cmtProject.service.mes.production;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.cmtProject.dto.mes.manufacturingMgt.MfgScheduleDTO;
import com.example.cmtProject.dto.mes.production.LotDTO;
import com.example.cmtProject.dto.mes.production.WorkOrderDTO;
import com.example.cmtProject.mapper.mes.production.WorkOrderMapper;

import jakarta.transaction.Transactional;

@Service
public class WorkOrderService {
	@Autowired WorkOrderMapper orderMapper;
	//작업지시 리스트
	public List<WorkOrderDTO> getOrderList() {
		return orderMapper.selectOrderList();
	}
	//제조계획
	public List<MfgScheduleDTO> getPlanList() {
		return orderMapper.selectPlanList();
	}
	
	@Transactional
	public void registMsPlan(WorkOrderDTO workOrderDTO) {
		orderMapper.insertMsPlan(workOrderDTO); // 작업지시 등록
	}
	
	//작업시작 버튼 = 날짜 업데이트&진행중
	public void startWork(Long workOrderNo) {
		orderMapper.updateWorkStartTime(workOrderNo);
	}

	@Transactional
	public void updateMfgStatus(String woCode) {
		orderMapper.updateMfgStatus(woCode);// 작업지시서 상태 변경 - workOrder
		orderMapper.updateMfgStatus2(woCode); //status '대기'로 변경 - MFG_SCHEDULES 
	}
//	//작업지시 등록시 제거됨
//	private void deleteMfgList(String msCode) {
//		orderMapper.deleteMfgList(msCode);
//	}
	
	//MFG_SCHEDULES 상태변경 
	private void updateMfgStatus2(String msCode) {
		orderMapper.updateMfgStatus2(msCode);
	}
	//로트번호로 단일제품정보 들고오기
	public WorkOrderDTO getProductDetail(String lotNo) {
		return orderMapper.selectProductDetail(lotNo);
	}
	
	//lot + workOrder 테이블
	public List<LotDTO> getLotDetail() {
		return orderMapper.selectLotDetail();
	}
	//lotNo로 상품 상세정보 들고오기
	public LotDTO getLotNoDetail(Long lotNo) {
		return orderMapper.selectLotNoDetail(lotNo);
	}
	
	//=====================================================
	public List<LotDTO> getAllLotTree() {
		return orderMapper.selectAllLotTree();
	}
	//특정 lot기준으로 그 하위공정 목록만 조회
	public List<LotDTO> getLotProcessHistoryList(String childLotCode) {
//		System.out.println("넘어온 lotCode : "+childLotCode);
		return orderMapper.selectLotProcessListByLotNo(childLotCode);
	}
	
	//그래프
	public List<WorkOrderDTO> getCompleteStatsLast7Days() {
	    return orderMapper.selectCompleteStatsLast7Days();
	}
	//오늘 기준 미완료 공정 Top 5
	public List<LotDTO> getIncompleteTop5Today() {
		return orderMapper.selectTodayProcessTop5();
	}
	
	//WO_NO 컬럼 값을 적접 입력하기 위해 이전 WO_NO 중 최대값
	public Long getWoNoMax() {
		return orderMapper.getWoNoMax();
	}
	
	//가장 큰 WO_NO에 해당하는 WO_CODE
	public String getWoCodeLast() {
		return orderMapper.getWoCodeLast();
	}
	//로트에 해당하는 qualityHistory
	public LotDTO getQualityHistory(Long lotNo) {
		return orderMapper.selectQualityHistory(lotNo);
	}
	public List<LotDTO> searchLotsByKeyword(String keyword) {
		return orderMapper.findLotsByKeyword("%" + keyword + "%");
	}
	
}
