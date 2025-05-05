package com.example.cmtProject.controller.mes.qualityControl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.cmtProject.dto.mes.qualityControl.InspectionSummaryDTO;
import com.example.cmtProject.dto.mes.qualityControl.IqcDTO;
import com.example.cmtProject.entity.erp.employees.Employees;
import com.example.cmtProject.entity.erp.employees.PrincipalDetails;
import com.example.cmtProject.service.mes.qualityControl.IqcService;
import com.example.cmtProject.service.mes.qualityControl.QcmService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;



@Controller
@RequestMapping("/iqc")
@Slf4j
public class IqcController {
	
	@Autowired
	private IqcService iqcService;
	
	@Autowired
	private QcmService qcmService;
	
	
	@GetMapping("/inspection-info")
	public String getIqcInfo(Model model, @AuthenticationPrincipal PrincipalDetails principalDetails) {
		
		if (principalDetails == null) {
            return "redirect:/login"; // 로그인 페이지로 리다이렉트
        }
    	// 유저정보
    	String loginUser = principalDetails.getUsername();
    	
    	// 현재 유저 Role 넘겨주기
    	model.addAttribute("userRole", principalDetails.getAuthorities().iterator().next().getAuthority());
    	
    	// IQC 리스트
    	List<IqcDTO> iqcList = iqcService.getAllIqc();
    	model.addAttribute("iqcList", iqcList);
    	
    	return "mes/qualityControl/iqcList";
	}
	
	
	@GetMapping("/names-by-mtl")
	@ResponseBody
	public List<Map<String, Object>> getQcmNamesByMtl(@RequestParam("mtlName") String mtlName) {
	    return qcmService.getQcmNamesByMtlName(mtlName);
	}
	
	// 그리드에서 바로 수정
	@ResponseBody
	@PostMapping("/edit")
	public void qcmEditexep(@ModelAttribute IqcDTO iqcDTO) throws JsonMappingException, JsonProcessingException {
		if ("".equals(iqcDTO.getQcmName())) {
			iqcDTO.setQcmName(null);
	    }
		iqcService.iqcRemarksAndQcmNameUpdate(iqcDTO); 
	}
	
	// 삭제 메서드
    @PostMapping("/delete")
    @ResponseBody
    public ResponseEntity<String> deleteIqc(@RequestBody Map<String, List<Long>> data) {
        List<Long> ids = data.get("ids");

        // 삭제 로직 실행 (예: attendService.deleteByIds(ids))
        iqcService.isVisiableToFalse(ids);

        return ResponseEntity.ok("success");
    }
    
    // 검사전 버튼 누르면 검사중으로 바뀌고 검사중 버튼 누르면 검사완료 버튼이 된다
    @ResponseBody
    @PostMapping("/status-action")
    public ResponseEntity<?> postMethodName(Model model, 
    											@RequestBody Map<String, String> payload,
    											@AuthenticationPrincipal PrincipalDetails principalDetails) {

    	// 유저정보
    	Employees loginUser = principalDetails.getUser();
    	
    	IqcDTO iqcDTO = new IqcDTO();
    	
    	iqcDTO.setIqcCode(payload.get("iqcCode"));
        String status = payload.get("status");
        iqcDTO.setReceiptCode(payload.get("receiptCode"));

        // TODO: 상태에 따라 분기 처리
        if ("검사중".equals(status)) {
        	iqcService.updateIqcInspectionStatusProcessing(loginUser, iqcDTO);
        } else if ("검사완료".equals(status)) {
        	iqcService.updateIqcInspectionStatusComplete(iqcDTO);
        }
    	
        
        return ResponseEntity.ok(Collections.singletonMap("result", "success"));
    }
    
    // 자동 검사
    @PostMapping("/auto-inspect")
    @ResponseBody
    public ResponseEntity<?> autoInspect(@RequestBody Map<String, String> request) {
        String iqcCode = request.get("iqcCode");
        try {
            IqcDTO result = iqcService.autoInspect(iqcCode);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Map.of("message", e.getMessage()));
        }
    }
    
    // 차트 값 넘겨주기
    @GetMapping("/inspection-summary")
    @ResponseBody
    public Map<String, Integer> getInspectionSummary() {

         Map<String, Integer> result = new HashMap<>();
         InspectionSummaryDTO summary = iqcService.getSummary();
         result.put("passCount", summary.getPassCount() != null ? summary.getPassCount() : 0);
         result.put("inProgressCount", summary.getInProgressCount() != null ? summary.getInProgressCount() : 0);
         result.put("failCount", summary.getFailCount() != null ? summary.getFailCount() : 0);

         return result;
    }
    
    // ✅ 최근 7일 검사 요약 조회
    @ResponseBody
    @GetMapping("/inspection-summary-last-7-days")
    public List<InspectionSummaryDTO> getLast7DaysInspectionSummary() {
        return iqcService.getLast7DaysSummary();
    }

    
    
	
	
	
//----------------------------------------------------------------------------------------------------	
	
	
	//엑셀 파일 다운로드
	@GetMapping("/excel-file-down")
	public void downloadExcel(HttpServletResponse response) throws IOException {
	    String fileName = "bom_form.xls";
	    String filePath = "/excel/" + fileName;

	    // /static/ 디렉토리 기준으로 파일을 읽어옴
	    log.info("filePath:"+filePath);
	    InputStream inputStream = new ClassPathResource(filePath).getInputStream();

	    log.info("inputStream:"+inputStream);
	    response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
	    response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

	    // 파일 내용을 응답 스트림에 복사
	    StreamUtils.copy(inputStream, response.getOutputStream());
	    response.flushBuffer();
		}
	
	// 엑셀 파일 적용
	@PostMapping("/saveExcelData")
	@ResponseBody
	public ResponseEntity<?> saveExcelData(@RequestBody List<IqcDTO> list) {

	    for (IqcDTO dto : list) {
	        iqcService.saveExcelData(dto); // insert or update
	    }
	    return ResponseEntity.ok("엑셀 저장 완료");
	}
	
	


}
