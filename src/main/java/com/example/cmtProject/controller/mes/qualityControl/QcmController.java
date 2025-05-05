package com.example.cmtProject.controller.mes.qualityControl;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
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
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.cmtProject.dto.comm.CommonCodeDetailDTO;
import com.example.cmtProject.dto.mes.qualityControl.QcmDTO;
import com.example.cmtProject.entity.erp.employees.Employees;
import com.example.cmtProject.entity.erp.employees.PrincipalDetails;
import com.example.cmtProject.entity.mes.standardInfoMgt.Materials;
import com.example.cmtProject.entity.mes.standardInfoMgt.Products;
import com.example.cmtProject.mapper.mes.qualityControl.QcmMapper;
import com.example.cmtProject.repository.erp.saleMgt.MaterialsOrderRepository;
import com.example.cmtProject.repository.mes.qualityControl.QcmRepository;
import com.example.cmtProject.repository.mes.standardInfoMgt.ProductsRepository;
import com.example.cmtProject.service.mes.qualityControl.QcmService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;


@Controller
@RequestMapping("/qcm")
@Slf4j
public class QcmController {
	
	@Autowired
	private ProductsRepository productsRepository;
	
	@Autowired
	private MaterialsOrderRepository materialsOrderRepository;
	
	@Autowired
	private QcmRepository qcmRepository;
	
	@Autowired
	private QcmService qcmService;
	
	
	@GetMapping("quality-info")
	public String getQcmInfo(Model model, @AuthenticationPrincipal PrincipalDetails principalDetails) {
		
		if (principalDetails == null) {
            return "redirect:/login"; // 로그인 페이지로 리다이렉트
        }
    	// 유저정보
    	Employees loginUser = principalDetails.getUser();
    	
    	// 현재 유저 Role 넘겨주기
    	model.addAttribute("userRole", principalDetails.getAuthorities().iterator().next().getAuthority());
		
    	// QCM 리스트
    	List<QcmDTO> qcmList = qcmService.getAllQcm();
    	model.addAttribute("qcmList", qcmList);
    	
    	// PRODUCTS 리스트
    	List<Products> productsList = productsRepository.findAll();
    	model.addAttribute("productsList", productsList);
    	
    	// MATERIALS 리스트
    	List<Materials> materialsList = materialsOrderRepository.findAll(); 
		model.addAttribute("materialsList", materialsList);
		
		// 커먼코드 디테일 단위길이 리스트
		List<CommonCodeDetailDTO> unitLengthList = qcmService.getUnitLengthList();
		model.addAttribute("unitLengthList", unitLengthList);
		
		// 커먼코드 디테일 단위무게 리스트
		List<CommonCodeDetailDTO> unitWeightList = qcmService.getUnitWeightList();
		model.addAttribute("unitWeightList", unitWeightList);

    	
		return "mes/qualityControl/qcmList";
	}
	
	
	// 그리드에서 바로 수정
	@ResponseBody
	@PostMapping("/edit")
	public void qcmEditexep(@ModelAttribute QcmDTO qcmDTO) throws JsonMappingException, JsonProcessingException {
		log.info("" + qcmDTO);
		if(!qcmDTO.getQcmCode().equals(qcmService.existsByQcmCode(qcmDTO.getQcmNo()))) {
			qcmService.qcmInsert(qcmDTO);
		} else {			
			qcmService.qcmUpdate(qcmDTO); 
		}
	}
	
	// 삭제 메서드
    @PostMapping("/delete")
    @ResponseBody
    public ResponseEntity<String> deleteQcm(@RequestBody Map<String, List<Long>> data) {
        List<Long> ids = data.get("ids");

        // 삭제 로직 실행 (예: attendService.deleteByIds(ids))
        qcmRepository.deleteAllById(ids);

        return ResponseEntity.ok("success");
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
	public ResponseEntity<?> saveExcelData(@RequestBody List<QcmDTO> list) {

	    for (QcmDTO dto : list) {
	        qcmService.saveExcelData(dto); // insert or update
	    }
	    return ResponseEntity.ok("엑셀 저장 완료");
	}
	

}
