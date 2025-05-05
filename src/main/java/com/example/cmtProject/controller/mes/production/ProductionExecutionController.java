package com.example.cmtProject.controller.mes.production;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.cmtProject.dto.mes.production.LotDTO;
import com.example.cmtProject.dto.mes.production.WorkOrderDTO;
import com.example.cmtProject.service.mes.production.WorkOrderService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/production")
public class ProductionExecutionController {//	생산 중 실적 등록, 자재 투입, LOT 추적
	@Autowired WorkOrderService orderService;
	
	//Lot추적 트리
	@GetMapping("/lotTracking")
    public String lotTracking(Model model) throws Exception {
		List<LotDTO> orderList = orderService.getAllLotTree();
	    model.addAttribute("orderListJson", orderList);
        return "mes/production/lotTracking";
	}
	//로트 번호로 상제정보
	@GetMapping("/lotDetail")
	@ResponseBody
	public LotDTO lotDetail(@RequestParam("lotNo") Long lotNo) {
		LotDTO detail = orderService.getLotNoDetail(lotNo);
		return detail;
	}
	
	//특정 lot기준으로 그 하위공정 목록만 조회
	@GetMapping("/lotProcessHistory")
	@ResponseBody
	public List<LotDTO> lotProcessHistory(@RequestParam("childLotCode") String childLotCode, Model model) {
		return orderService.getLotProcessHistoryList(childLotCode);
	}
	
	//lotN로 품질이력 조회
	@GetMapping("/qualityHistory")
	@ResponseBody
	public LotDTO qualityHistory(@RequestParam("lotNo") Long lotNo) {
		LotDTO qualityHistory = orderService.getQualityHistory(lotNo);
//		System.out.println("품질정보 : " + qualityHistory);
		
		return qualityHistory;
	}
	
	@GetMapping("/searchLotTree")
	@ResponseBody
	public List<LotDTO> searchLotTree(@RequestParam("keyword") String keyword) {
		System.out.println("keyword = " + keyword);
		 return orderService.searchLotsByKeyword(keyword);
		
	}
}
