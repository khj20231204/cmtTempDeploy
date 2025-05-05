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

import com.example.cmtProject.entity.mes.standardInfoMgt.LineInfo;
import com.example.cmtProject.entity.mes.standardInfoMgt.Products;
import com.example.cmtProject.repository.mes.standardInfoMgt.LineInfoRepository;
import com.example.cmtProject.repository.mes.standardInfoMgt.ProductsRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/line")
public class LineInfoController {

	@Autowired
	private LineInfoRepository lineInfoRepository;
	
	@GetMapping("/line-info")
	public String bomInfo(Model model){
		
		List<LineInfo> lineList = lineInfoRepository.findAll();
		
		//상단 그리드에 출력하기 위해서 List<Products> => List<Map<...>>형태로 변환
		List<Map<String, Object>> lineData = lineList.stream()
		    .map(l -> {
		        Map<String, Object> map = new HashMap<>();
		        map.put("lineNo", l.getLineNo());
		        map.put("lineCode", l.getLineCode());
		        map.put("lineName", l.getLineName());
		        map.put("lineLocation", l.getLineLocation());
		        map.put("lineStatus", l.getLineStatus());
		        map.put("lineComments", l.getLineComments());
		        return map;
		    }).collect(Collectors.toList());
		
		//그리드에 출력시킬 컬럼 생성
		List<Map<String, Object>> columns = List.of(
				Map.of("header", "라인코드", "name", "lineCode"),
				Map.of("header", "라인명", "name", "lineName"),
				Map.of("header", "라인 위치", "name", "lineLocation"),
				Map.of("header", "라인 상태", "name", "lineStatus"),
				Map.of("header", "비고", "name", "lineComments")
			);
		
		model.addAttribute("lineGridOptions", Map.of(
		    "columns", columns,
		    "data", lineData
		));
	 	
		return "mes/standardInfoMgt/lineInfo";
	}
}
