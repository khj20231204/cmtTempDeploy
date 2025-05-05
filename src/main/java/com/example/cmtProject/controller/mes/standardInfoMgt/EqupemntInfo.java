package com.example.cmtProject.controller.mes.standardInfoMgt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.cmtProject.repository.mes.standardInfoMgt.EqupemntInfoRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/equpemnt")
public class EqupemntInfo {


	@Autowired
	private EqupemntInfoRepository equpemntRepository;
	
	
	@GetMapping("/equpemnt-info")
	public String bomInfo(Model model){
		
		List<com.example.cmtProject.entity.mes.standardInfoMgt.EqupemntInfo> equpemntList = equpemntRepository.findAll();
		
		//상단 그리드에 출력하기 위해서 List<Products> => List<Map<...>>형태로 변환
		List<Map<String, Object>> equpemntData = equpemntList.stream()
		    .map(l -> {
		        Map<String, Object> map = new HashMap<>();
		        map.put("eqpNo", l.getEqpNo());
		        map.put("eqpCode", l.getEqpCode());
		        map.put("eqpName", l.getEqpName());
		        map.put("eqpType", l.getEqpType());
		        map.put("eqpPrcType", l.getEqpPrcType());
		        map.put("eqpModel", l.getEqpModel());
		        map.put("eqpManufacturer", l.getEqpManufacturer());
		        map.put("eqpPurchaseDate", l.getEqpPurchaseDate());
		        map.put("eqpStatus", l.getEqpStatus());
		        map.put("eqpLocation", l.getEqpLocation());
		        map.put("eqpComments", l.getEqpComments());
		        
		        return map;
		    }).collect(Collectors.toList());
		
		//그리드에 출력시킬 컬럼 생성
		List<Map<String, Object>> columns = List.of(
				Map.of("header", "설비코드", "name", "eqpCode"),
				Map.of("header", "설비명", "name", "eqpName"),
				Map.of("header", "설비 타입", "name", "eqpType"),
				Map.of("header", "설비 상태 타입", "name", "eqpPrcType"),
				Map.of("header", "설비 모델", "name", "eqpModel"),
				Map.of("header", "설비 제조", "name", "eqpManufacturer"),
				Map.of("header", "설비 구입 날짜", "name", "eqpPurchaseDate"),
				Map.of("header", "설비 위치", "name", "eqpLocation")
			);
		
		model.addAttribute("eqpGridOptions", Map.of(
		    "columns", columns,
		    "data", equpemntData
		));
	 	
		return "mes/standardInfoMgt/equpemntInfo";
	}
}
