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

import com.example.cmtProject.dto.mes.qualityControl.InspectionSummaryDTO;
import com.example.cmtProject.dto.mes.qualityControl.IpiDTO;
import com.example.cmtProject.dto.mes.qualityControl.QcmDTO;
import com.example.cmtProject.entity.erp.employees.Employees;
import com.example.cmtProject.mapper.mes.qualityControl.IpiMapper;

import jakarta.transaction.Transactional;

@Service
public class IpiService {
	
	@Autowired
	private IpiMapper ipiMapper;

	// 모든 입고 검사 목록
	public List<IpiDTO> getAllIpi() {
		return ipiMapper.getAllIpi();
	}

	// 비고란 수정
	@Transactional
	public void ipiRemarksAndQcmNameUpdate(IpiDTO ipiDTO) {
		ipiMapper.ipiRemarksAndQcmNameUpdate(ipiDTO);
	}
	
	// 삭제 대신 안보이게 하기
	@Transactional
	public void isVisiableToFalse(List<Long> ids) {
		ipiMapper.isVisiableToFalse(ids);
	}

	// 엑셀 데이터 저장
	@Transactional
	public void saveExcelData(IpiDTO dto) {
		ipiMapper.saveExcelData(dto);
	}
	
	// IPI 코드 생성
	private String generateIpiCode() {
		String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		int maxSeq = ipiMapper.getMaxIpiCodeSeq(datePart);
		int nextSeq = maxSeq + 1;
		String seqStr = String.format("%03d", nextSeq);
		return "IPI-" + datePart + "-" + seqStr;
	}

	// 물건 입고시 입고 검사 검사전과 필요한 데이터 가져와서 인서트 하기
	@Transactional
	public void insertIpiInspection(Map<String, Object> updateMap) {
		List<Map<String, Object>> result = ipiMapper.getLot(updateMap);
		
		for (Map<String, Object> row : result) {
		    row.put("woCode", row.get("WO_CODE"));
		    row.put("childPdtCode", row.get("CHILD_PDT_CODE"));
		    row.put("woQty", row.get("WO_QTY"));
		    row.put("ipiCode", generateIpiCode());

			ipiMapper.insertIpiInspectionList(row);
		}
	}

	// 검사전에서 검사중으로 업데이트
	@Transactional
	public void updateIpiInspectionStatusProcessing(Employees loginUser, IpiDTO ipiDTO) {
		ipiDTO.setIpiStartTime(LocalDateTime.now());
		ipiMapper.updateIpiInspectionStatusProcessing(loginUser, ipiDTO);
	}

	@Transactional
	// 검사중에서 검사완료로 업데이트
	public void updateIpiInspectionStatusComplete(IpiDTO ipiDTO) {
		ipiDTO.setIpiEndTime(LocalDateTime.now());
		ipiMapper.updateIpiInspectionStatusComplete(ipiDTO);
		
        Map<String, Object> params = new HashMap<>();
        params.put("pdtName", ipiDTO.getPdtName());
        params.put("pdtCode", ipiDTO.getPdtCode());
        params.put("woQty", ipiDTO.getWoQty());
        params.put("childLotCode", ipiDTO.getChildLotCode());


	}

	
	// 검사완료로 넘어갈때 측정값 저장
	@Transactional
	public IpiDTO autoInspect(String ipiCode) {
	    QcmDTO qcm = ipiMapper.selectQcmInfoByIpiCode(ipiCode);

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
//	    map.put("ipiCode", ipiCode);
//	    map.put("weightValue", weightValue);
//	    map.put("lengthValue", lengthValue);
//	    map.put("result", result);
	    
	    // 저장
	    ipiMapper.updateMeasuredValues(ipiCode, weightValue, lengthValue, result);
//	    ipiMapper.updateMeasuredValues(map);

	    return new IpiDTO(weightValue, lengthValue, result);
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
		return ipiMapper.getSummary();
	}
	
	// 막대 차트값 불러오는 메서드
	public List<InspectionSummaryDTO> getLast7DaysSummary() {
		List<InspectionSummaryDTO> dbResult = ipiMapper.getLast7DaysSummary();

        // ✅ DB 결과를 날짜별 Map으로 변환
        Map<String, InspectionSummaryDTO> resultMap = new HashMap<>();
        for (InspectionSummaryDTO dto : dbResult) {
            resultMap.put(dto.getIpiDate(), dto);
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
                emptyDto.setIpiDate(dateStr);
                emptyDto.setPassCount(0);
                emptyDto.setInProgressCount(0);
                emptyDto.setFailCount(0);
                finalResult.add(emptyDto);
            }
        }

        return finalResult;
    
    }

}

