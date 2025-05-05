package com.example.cmtProject.mapper.mes.qualityControl;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.cmtProject.dto.mes.qualityControl.InspectionSummaryDTO;
import com.example.cmtProject.dto.mes.qualityControl.IpiDTO;
import com.example.cmtProject.dto.mes.qualityControl.QcmDTO;
import com.example.cmtProject.entity.erp.employees.Employees;

@Mapper
public interface IpiMapper {

	List<IpiDTO> getAllIpi();

	void ipiRemarksAndQcmNameUpdate(IpiDTO ipiDTO);
	
	void isVisiableToFalse(List<Long> ids);	
	
	void saveExcelData(IpiDTO dto);

	List<Map<String, Object>> getLot(Map<String, Object> updateMap);
	
	int getMaxIpiCodeSeq(String datePart);

	void insertIpiInspectionList(Map<String, Object> row);
	
	void updateIpiInspectionStatusProcessing(@Param("emp") Employees loginUser, @Param("ipi") IpiDTO ipiDTO);

	void updateIpiInspectionStatusComplete(IpiDTO ipiDTO);

	QcmDTO selectQcmInfoByIpiCode(String ipiCode);

	void updateMeasuredValues(@Param("ipiCode") String ipiCode,
		    				  @Param("weightValue") Double weightValue,
		    				  @Param("lengthValue") Double lengthValue,
		    				  @Param("result") String result);

//	void updateMeasuredValues(Map<String, Object> param);
	
	InspectionSummaryDTO getSummary();
	
	List<InspectionSummaryDTO> getLast7DaysSummary();



	



}
