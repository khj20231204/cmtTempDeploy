package com.example.cmtProject.controller.mes.manufacturingMgt;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.cmtProject.dto.mes.manufacturingMgt.MfgPlanDTO;
import com.example.cmtProject.dto.mes.manufacturingMgt.MfgPlanSalesOrderDTO;
import com.example.cmtProject.mapper.mes.manufacturingMgt.MfgPlanMapper;
import com.example.cmtProject.service.mes.inventory.InventoryUpdateService;
import com.example.cmtProject.service.mes.manufacturingMgt.MfgPlanService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/mp")
@Slf4j
public class MfgPlanController { // 생산 계획 Controller
	
	@Autowired
	private MfgPlanService mfgPlanService;
	
	@Autowired
	private MfgPlanMapper mfgPlanMapper;
	
	@Autowired
	private InventoryUpdateService ius;
	
	// 생산 계획 조회
	@GetMapping("/mfg-plan")
	public String mfgPlan(Model model) {

		// 생산 계획 목록
		List<MfgPlanDTO> mpList = mfgPlanService.getMfgPlanTotalList();
		model.addAttribute("mpList", mpList);
		
		// 수주 목록
		List<MfgPlanSalesOrderDTO> soList = mfgPlanService.getSoList();
		model.addAttribute("soList", soList);
		
		return "mes/manufacturingMgt/mfgPlan";
	}
	
	// 생산 계획 등록 조회 (수주 데이터)
	@GetMapping("/mfgPlanRegiList")
	public String mfgPlanRegiList(Model model) {
		List<MfgPlanSalesOrderDTO> soList = mfgPlanService.getSoList();
		model.addAttribute("soList", soList);
		
	    return "mes/manufacturingMgt/mfgPlan";
	}
	
	// 생산 계획 등록 시 재고 조회
	@GetMapping("/checkStock")
	@ResponseBody
	public boolean checkStock(@RequestParam("soCode") String soCode,@RequestParam("soQty") Long soQty) {
	    return mfgPlanMapper.checkStock(soCode, soQty);
	}
	
	// 생산 계획 등록
	@PostMapping("/mfgPlanRegi")
	@ResponseBody
	public String mfgPlanRegi(@RequestBody List<MfgPlanDTO> mfgPlanList) {
		
		int resultFailCount = 0;
	    for (MfgPlanDTO dto : mfgPlanList) {
	        String soCode = dto.getSoCode();
	        Long soQty = dto.getSoQty();
	        log.info("처리중: 주문코드={}, 수량={}", soCode, soQty);

	        boolean isStockEnough = mfgPlanMapper.checkStock(soCode, soQty);

	        if (!isStockEnough) {
	            resultFailCount +=1;  // 또는 어느 항목이 실패했는지 구체적으로 리턴해도 됨
	        } else {
	        	//등록 가능한거 바로 입력하자!
	        	mfgPlanService.insertMfgPlan(dto);
	        	
	        	// 생산계획 대비 자재/제품 할당 수량 업데이트
	            Map<String, Object> params = new HashMap<>();
	            params.put("soCode", soCode);
	            params.put("soQty", soQty);
	            params.put("updatedBy", "admin"); // 수정자 추후 로그인 으로 수정
	            
	            // 서비스 메서드 호출
	            ius.updateAllocatedQuantities(params);		
	        }
	    }
	    return "success";
	}

	// 생산 계획 수정
	@PostMapping("/update")
	@ResponseBody
	public ResponseEntity<String> updateMpPlan(@RequestBody List<MfgPlanDTO> mpList) {
		mfgPlanService.updateMpPlan(mpList);
		
	    return ResponseEntity.ok("success");
	}
	
	// 생산 계획 삭제 (숨김 처리)
    @PostMapping("/delete")
    @ResponseBody
    public ResponseEntity<String> deleteMpPlan(@RequestBody Map<String, List<Long>> data) {
        List<Long> mpNos = data.get("mpNos");

        // 삭제 로직 실행
        mfgPlanService.isVisibleToFalse(mpNos);

        return ResponseEntity.ok("success");
    }
		
	//----------------------------------------------------------------------------------------------------	
		
	// 엑셀 파일 다운로드
	@GetMapping("/excel-file-down")
	public void downloadExcel(HttpServletResponse response) throws IOException {
		String fileName = "mp_form.xls";
		String filePath = "/excel/" + fileName;

		// /static/ 디렉토리 기준으로 파일을 읽어옴
		log.info("filePath:" + filePath);
		InputStream inputStream = new ClassPathResource(filePath).getInputStream();

		log.info("inputStream:" + inputStream);
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

		// 파일 내용을 응답 스트림에 복사
		StreamUtils.copy(inputStream, response.getOutputStream());
		response.flushBuffer();
	}
	
}
