package com.example.cmtProject.service.mes.production;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.cmtProject.dto.mes.production.LotOrderDTO;
import com.example.cmtProject.dto.mes.production.LotOriginDTO;
import com.example.cmtProject.dto.mes.production.LotStructurePathDTO;
import com.example.cmtProject.dto.mes.production.SavePRCDTO;
import com.example.cmtProject.dto.mes.production.SemiFinalBomQty;
import com.example.cmtProject.dto.mes.qualityControl.IpiDTO;
import com.example.cmtProject.dto.mes.standardInfoMgt.BomStructurePathDTO;
import com.example.cmtProject.mapper.mes.production.LotMapper;

@Service
public class LotService {

	@Autowired
	private LotMapper lotMapper;
	
	public Long getLotNo() {
		return lotMapper.getLotNo();
	}

	public LotOrderDTO getLotOrderPrcType(String todayStr, String type) {
		return lotMapper.getLotOrderPrcType(todayStr, type);
	}

	public void insertLot(LotOriginDTO lod) {
		lotMapper.insertLot(lod);
	}

	public List<BomStructurePathDTO> selectStructurePath(String pdtCode) {
		return lotMapper.selectStructurePath(pdtCode);
	}

	public List<LotStructurePathDTO> selectStructurePathAll(String woCode, String pdtCode) {
		return lotMapper.selectStructurePathAll(woCode, pdtCode);
	}

	public List<LotOriginDTO> selectLotOrigin(String woCode) {
		return lotMapper.selectLotOrigin(woCode);
	}

	public void updateLotPresentPRC(LotOriginDTO lotOrigin) {
		lotMapper.updateLotPresentPRC(lotOrigin);
	}

	public void updateLotNextPRC(Long nextLotNo, String startTime) {
		lotMapper.updateLotNextPRC(nextLotNo, startTime);
	}

	public void updateWOtoCP(String woCode) {
		lotMapper.updateWOtoCP(woCode);
	}

	public int selectCheckSavePRC() {
		return lotMapper.selectCheckSavePRC();
	}

	public String selectCheckQI(String woCode) {
		return lotMapper.selectCheckQI(woCode);
	}

	public String selectRNRowNum(String nowWoCode) {
		return lotMapper.selectRNRowNum(nowWoCode);
	}

	public List<SavePRCDTO> selectSavePRC() {
		return lotMapper.selectSavePRC();
	}

	public Integer selectRnRowNumMax(String woCode) {
		return lotMapper.selectRnRowNumMax(woCode);
	}

	public void insertSavePrc(SavePRCDTO savePrcDto) {
		lotMapper.insertSavePrc(savePrcDto);
	}

	public void deleteSavePrc() {
		lotMapper.deleteSavePrc();
	}

	public List<SemiFinalBomQty> getBomQty(String woCode) {
		return lotMapper.getBomQty(woCode);
	}

	public List<String> selectParentPdtCode(String pdtCode) {
		return lotMapper.selectParentPdtCode(pdtCode);
	}

	public Long getIpiNo() {
		return lotMapper.getIpiNo();
	}

	public void updateWoEndDate(String woCode, String today) {
		lotMapper.updateWoEndDate(woCode, today);
	}

	public void insertIpi(IpiDTO ipidto) {
		lotMapper.insertIpi(ipidto);
	}

	public void updateMfgScdStatus(String woCode, String mfgscd) {
		lotMapper.updateMfgScdStatus(woCode, mfgscd);
	}

	public void updateMfgPlanStatus(String woCode, String mfgPlan) {
		lotMapper.updateMfgPlanStatus(woCode, mfgPlan);
		
	}
}
