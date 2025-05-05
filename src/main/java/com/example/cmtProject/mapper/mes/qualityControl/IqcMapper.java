package com.example.cmtProject.mapper.mes.qualityControl;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.cmtProject.dto.mes.qualityControl.InspectionSummaryDTO;
import com.example.cmtProject.dto.mes.qualityControl.IqcDTO;
import com.example.cmtProject.dto.mes.qualityControl.QcmDTO;
import com.example.cmtProject.entity.erp.employees.Employees;

@Mapper
public interface IqcMapper {

	List<IqcDTO> getAllIqc();

	void iqcRemarksAndQcmNameUpdate(IqcDTO iqcDTO);
	
	void isVisiableToFalse(List<Long> ids);	
	
	void saveExcelData(IqcDTO dto);

	List<Map<String, Object>> getMaterialReceipts(Map<String, Object> updateMap);
	
	int getMaxIqcCodeSeq(String datePart);

	void insertIqcInspectionList(Map<String, Object> row);
	
	void updateIqcInspectionStatusProcessing(@Param("emp") Employees loginUser, @Param("iqc") IqcDTO iqcDTO);

	void updateIqcInspectionStatusComplete(IqcDTO iqcDTO);

	QcmDTO selectQcmInfoByIqcCode(String iqcCode);

	void updateMeasuredValues(@Param("iqcCode") String iqcCode,
		    				  @Param("weightValue") Double weightValue,
		    				  @Param("lengthValue") Double lengthValue,
		    				  @Param("result") String result);

	InspectionSummaryDTO getSummary();
	
	List<InspectionSummaryDTO> getLast7DaysSummary();

//	void updateMeasuredValues(Map<String, Object> param);



	



}
