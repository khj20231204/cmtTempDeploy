package com.example.cmtProject.service.mes.qualityControl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.cmtProject.dto.mes.qualityControl.FqcDTO;
import com.example.cmtProject.dto.mes.qualityControl.InspectionSummaryDTO;
import com.example.cmtProject.dto.mes.qualityControl.QcmDTO;
import com.example.cmtProject.entity.erp.employees.Employees;
import com.example.cmtProject.mapper.mes.inventory.InventoryUpdateMapper;
import com.example.cmtProject.mapper.mes.qualityControl.FqcMapper;

import jakarta.transaction.Transactional;

@Service
public class FqcService {
	
	@Autowired
	private FqcMapper fqcMapper;

	@Autowired
	private InventoryUpdateMapper ium;

	// 모든 입고 검사 목록
	public List<FqcDTO> getAllFqc() {
		return fqcMapper.getAllFqc();
	}

	// 비고란 수정
	@Transactional
	public void fqcRemarksAndQcmNameUpdate(FqcDTO fqcDTO) {
		fqcMapper.fqcRemarksAndQcmNameUpdate(fqcDTO);
	}
	
	// 삭제 대신 안보이게 하기
	@Transactional
	public void isVisiableToFalse(List<Long> ids) {
		fqcMapper.isVisiableToFalse(ids);
	}

	// 엑셀 데이터 저장
	@Transactional
	public void saveExcelData(FqcDTO dto) {
		fqcMapper.saveExcelData(dto);
	}
	
	// FQC 코드 생성
	private String generateFqcCode() {
		String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		int maxSeq = fqcMapper.getMaxFqcCodeSeq(datePart);
		int nextSeq = maxSeq + 1;
		String seqStr = String.format("%03d", nextSeq);
		return "FQC-" + datePart + "-" + seqStr;
	}

	// 물건 입고시 입고 검사 검사전과 필요한 데이터 가져와서 인서트 하기
	@Transactional
	public void insertFqcInspection(Map<String, Object> updateMap) {
		List<Map<String, Object>> result = fqcMapper.getLot(updateMap);
		
		for (Map<String, Object> row : result) {
		    row.put("woCode", row.get("ISSUE_CODE"));
		    row.put("pdtCode", row.get("PDT_CODE"));
		    row.put("woQty", row.get("ISSUED_QTY"));
		    row.put("fqcCode", generateFqcCode());

			fqcMapper.insertFqcInspectionList(row);
		}
	}

	// 검사전에서 검사중으로 업데이트
	@Transactional
	public void updateFqcInspectionStatusProcessing(Employees loginUser, FqcDTO fqcDTO) {
		fqcDTO.setFqcStartTime(LocalDateTime.now());
		fqcMapper.updateFqcInspectionStatusProcessing(loginUser, fqcDTO);
	}

	@Transactional
	// 검사중에서 검사완료로 업데이트
	public void updateFqcInspectionStatusComplete(FqcDTO fqcDTO) {
		fqcDTO.setFqcEndTime(LocalDateTime.now());
		fqcMapper.updateFqcInspectionStatusComplete(fqcDTO);
		
		// InventoryUpdateMapper 에서 업데이트 하기위해서 
        String code = fqcDTO.getWoCode();
        Map<String, Object> params = new HashMap<>();
        params.put("issueCode", code);
        ium.updateIssueStatus(params);
	}

	
	// 검사완료로 넘어갈때 측정값 저장
	@Transactional
	public FqcDTO autoInspect(String fqcCode) {
	    QcmDTO qcm = fqcMapper.selectQcmInfoByFqcCode(fqcCode);

	    if (qcm == null || qcm.getQcmMinValue() == null || qcm.getQcmMaxValue() == null) {
	        throw new IllegalArgumentException("검사 기준 정보가 없습니다.");
	    }

	    double min = qcm.getQcmMinValue();
	    double max = qcm.getQcmMaxValue();

	    Double weightValue = null;
	    Double lengthValue = null;
	    String result = "";

	    // 무게 검사일 경우
	    if (qcm.getQcmUnitWeight() != null) {
	        weightValue = generateRandom(min, max);
	        result = isPass(weightValue, min, max) ? "합격" : "불합격";
	        lengthValue = 0.0;
	    }

	    // 길이 검사일 경우
	    if (qcm.getQcmUnitLength() != null) {
	        lengthValue = generateRandom(min, max);
	        result = isPass(lengthValue, min, max) ? "합격" : "불합격";
	        weightValue = 0.0;
	    }


//	    Map<String, Object> map = new HashMap<String, Object>();
//	    map.put("fqcCode", fqcCode);
//	    map.put("weightValue", weightValue);
//	    map.put("lengthValue", lengthValue);
//	    map.put("result", result);
	    
	    // 저장
	    fqcMapper.updateMeasuredValues(fqcCode, weightValue, lengthValue, result);
//	    fqcMapper.updateMeasuredValues(map);

	    return new FqcDTO(weightValue, lengthValue, result);
	}

	// 랜덤값 생성 (소수점 1자리)
	private double generateRandom(double min, double max) {
	    double extendedMin = min - 0.1;
	    double extendedMax = max + 0.1;
	    double raw = Math.random() * (extendedMax - extendedMin) + extendedMin;
	    return Math.round(raw * 100.0) / 100.0; // ✅ 2자리 반올림
	}

	// 합격 여부 판정
	private boolean isPass(double value, double min, double max) {
	    return value >= min && value <= max;
	}

	// 도넛 차트값 불러오는 메서드
	public InspectionSummaryDTO getSummary() {
		return fqcMapper.getSummary();
	}
	
	// 막대 차트값 불러오는 메서드
	public List<InspectionSummaryDTO> getLast7DaysSummary() {
		List<InspectionSummaryDTO> dbResult = fqcMapper.getLast7DaysSummary();

        // ✅ DB 결과를 날짜별 Map으로 변환
        Map<String, InspectionSummaryDTO> resultMap = new HashMap<>();
        for (InspectionSummaryDTO dto : dbResult) {
            resultMap.put(dto.getFqcDate(), dto);
        }

        List<InspectionSummaryDTO> finalResult = new ArrayList<>();

        // ✅ 오늘 기준 최근 7일 날짜 리스트 생성
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            String dateStr = date.format(formatter);

            if (resultMap.containsKey(dateStr)) {
                // ✅ DB 결과에 있으면 그대로 추가
                finalResult.add(resultMap.get(dateStr));
            } else {
                // ✅ DB 결과에 없으면 pass/inProgress/fail 전부 0으로 채운 DTO 생성
                InspectionSummaryDTO emptyDto = new InspectionSummaryDTO();
                emptyDto.setFqcDate(dateStr);
                emptyDto.setPassCount(0);
                emptyDto.setInProgressCount(0);
                emptyDto.setFailCount(0);
                finalResult.add(emptyDto);
            }
        }

        return finalResult;
    
    }


	
	
	
	

	
	

}
