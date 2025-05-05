package com.example.cmtProject.service.mes.qualityControl;

import java.lang.module.ModuleDescriptor.Builder;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.cmtProject.dto.comm.CommonCodeDetailDTO;
import com.example.cmtProject.dto.mes.qualityControl.QcmDTO;
import com.example.cmtProject.mapper.mes.qualityControl.QcmMapper;

import jakarta.transaction.Transactional;

@Service
public class QcmService {
	
	@Autowired
	private QcmMapper qcmMapper;

	// QCM 모든 정보 출력
	public List<QcmDTO> getAllQcm() {
		return qcmMapper.getAllQcm();
	}
	
	@Transactional
	public void qcmUpdate(QcmDTO qcmDTO) {
		qcmMapper.qcmUpdate(qcmDTO);
	}
	
	@Transactional
	public void qcmInsert(QcmDTO qcmDTO) {
		qcmMapper.qcmInsert(qcmDTO);
	}
	
	public String existsByQcmCode(Long qcmNo) {
		return qcmMapper.existsByQcmCode(qcmNo);
	}

	public List<CommonCodeDetailDTO> getUnitLengthList() {
		return qcmMapper.getUnitLengthList();
	}

	public List<CommonCodeDetailDTO> getUnitWeightList() {
		return qcmMapper.getUnitWeightList();
	}
	
	public List<Map<String, Object>> getQcmNamesByMtlName(String mltName){
		return qcmMapper.getQcmNamesByMtlName(mltName);
	}
	
	public List<Map<String, Object>> getQcmNamesByPdtName(String pdtName){
		return qcmMapper.getQcmNamesByPdtName(pdtName);
	}

	@Transactional
	public void saveExcelData(QcmDTO dto) {
		qcmMapper.saveExcelData(dto);
		
	}

	

	
	

}
