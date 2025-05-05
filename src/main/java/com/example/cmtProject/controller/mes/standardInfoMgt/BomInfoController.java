package com.example.cmtProject.controller.mes.standardInfoMgt;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ProcessHandle.Info;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.cmtProject.controller.mes.standardInfoMgt.commModels.BomInfoModels;
import com.example.cmtProject.dto.mes.standardInfoMgt.BomEditDTO;
import com.example.cmtProject.dto.mes.standardInfoMgt.BomInfoTotalDTO;
import com.example.cmtProject.dto.mes.standardInfoMgt.ProductTotalDTO;
import com.example.cmtProject.dto.mes.standardInfoMgt.ProductsDTO;
import com.example.cmtProject.dto.mes.standardInfoMgt.ProductsEditDTO;
import com.example.cmtProject.entity.erp.salesMgt.SalesOrder;
import com.example.cmtProject.entity.mes.standardInfoMgt.Materials;
import com.example.cmtProject.entity.mes.standardInfoMgt.ProcessInfo;
import com.example.cmtProject.entity.mes.standardInfoMgt.Products;
import com.example.cmtProject.repository.erp.saleMgt.MaterialsOrderRepository;
import com.example.cmtProject.repository.mes.standardInfoMgt.ProcessInfoRepository;
import com.example.cmtProject.repository.mes.standardInfoMgt.ProductsRepository;
import com.example.cmtProject.service.mes.standardInfoMgt.BomInfoService;
import com.example.cmtProject.service.mes.standardInfoMgt.ProductService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Controller
@RequestMapping("/bom")
public class BomInfoController {

	@Autowired
	private ProductsRepository productsRepository;
	@Autowired
	private MaterialsOrderRepository materialsOrderRepository;
	@Autowired
	private ProcessInfoRepository processInfoRepository;
	
	@Autowired
	private ProductService productsService;
	@Autowired
	private BomInfoService bomInfoService;
	
	@Autowired 
	private BomInfoModels bomInfoModels;
	
	@GetMapping("/bom-info")
	public String bomInfo(Model model) throws Exception {
		
		//삭제되면 USERYN을 사용해야 하기 때문에 REPOSITORY 사용 못 함
		//List<Products> productList = productsRepository.findAll();
		
		//products만으로는 한글을 그리드의 select박스에 출력할 수 없다. 그래서 ProductTotalDTO을 만듦
		//List<Products> productList = productsService.list();
		
		List<ProductTotalDTO> productList = productsService.getProductTotalList();
		model.addAttribute("productList", productList);
		
		List<BomInfoTotalDTO> bomList = new ArrayList<>();
		model.addAttribute("bomList", bomList);
		
		List<Materials> materialsList = materialsOrderRepository.findAll(); 
		model.addAttribute("materialsList", materialsList);
		
		List<ProcessInfo> processList = processInfoRepository.findAll();
		model.addAttribute("processList", processList);
		
		//단위 데이터 models
		bomInfoModels.commonBomInfoModels(model);
		
		//th:object에서 사용할 객체 생성
	 	model.addAttribute("ProductsDTO", new ProductsDTO());
	 	
		return "mes/standardInfoMgt/bomInfo";
	}
	
	//상품 그리드에서 선택된 제품에 해당하는 BOM 데이터 불러오기
	@ResponseBody
	@GetMapping("/bom-selected")
	//public Map<String, Object> bomSelected(@RequestParam("pdtCode") String pdtCode){
	//public void bomSelected(@RequestParam("pdtCode") String pdtCode){
	public List<BomInfoTotalDTO> bomSelected(@RequestParam("pdtCode") String pdtCode){

		List<BomInfoTotalDTO> bomtotal = bomInfoService.getBomInfoTotalList(pdtCode);
		/*
		// 데이터와 컬럼 분리
		List<Map<String, Object>> bomData = bomtotal.stream()
			    .map(b -> {
			        Map<String, Object> map = new HashMap<>();
			        map.put("BOM_NO", b.getBomNo());
			        map.put("PDT_CODE", b.getPdtCode());
					map.put("PDT_NAME", b.getPdtName()); 
					map.put("MTL_CODE", b.getMtlCode());
					map.put("MTL_NAME", b.getMtlName());  
					map.put("BOM_QTY", b.getBomQty());
					map.put("BOM_UNIT", b.getBomUnit()); 
					map.put("PDT_PRC_TYPE_NAME",b.getPdtPrcTypeName());
			        return map;
			    }).collect(Collectors.toList());
		
		//그리드에 출력시킬 컬럼 생성
		List<Map<String, Object>> columns = List.of(
				Map.of("header", "BOM 고유번호", "name", "BOM_NO"),
				Map.of("header", "제품 코드", "name", "PDT_CODE"),
				Map.of("header", "제품 이름", "name", "PDT_NAME"),
				Map.of("header", "원자재 코드", "name", "MTL_CODE"),
				Map.of("header", "원자재 이름", "name", "MTL_NAME"),
				Map.of("header", "수량", "name", "BOM_QTY"),
				Map.of("header", "단위", "name", "BOM_UNIT"),
				Map.of("header", "공정 단계", "name", "PDT_PRC_TYPE_NAME")
			);
	
		return Map.of(
				"columns", columns,
				"data", bomData
			);*/
		
		return bomtotal;
	}
	
	//----------------------------------------- 카멜 -> 스네이크 자동 적용 되어 일치 시키기 위해서 전부 스네이크로 변경해 줘야 함 ------------------------------------
	//최초 로딩시 그리드에 출력할 제품 목록
	@GetMapping("/bom-info-frgmsVersion")
	public String bomInfoFrgmsVersion(Model model) {
		
		List<Products> pdtList = productsRepository.findAll();
		
		//상단 그리드에 출력하기 위해서 List<Products> => List<Map<...>>형태로 변환
		List<Map<String, Object>> productData = pdtList.stream()
		    .map(p -> {
		        Map<String, Object> map = new HashMap<>();
		        map.put("pdtNo", p.getPdtNo());
		        map.put("pdtCode", p.getPdtCode());
		        map.put("pdtName", p.getPdtName());
		        map.put("pdtShippingPrice", p.getPdtShippingPrice());
		        map.put("pdtComments", p.getPdtComments());
		        return map;
		    }).collect(Collectors.toList());
	
		//그리드에 출력시킬 컬럼 생성
		List<Map<String, Object>> columns = List.of(
				Map.of("header", "제품코드", "name", "pdtCode"),
				Map.of("header", "제품명", "name", "pdtName"),
				Map.of("header", "규격", "name", "pdtSpecification"),
				Map.of("header", "출하단가", "name", "pdtShippingPrice"),
				Map.of("header", "비고", "name", "pdtComments")
			);
		
		model.addAttribute("pdtGridOptions", Map.of(
		    "columns", columns,
		    "data", productData
		));
		
		//BOM그리드에 최초 로딩시 공백으로 나타내기 위해 빈 List전달
		model.addAttribute("bomGridOptions", Map.of(
			    "columns", List.of(),
			    "data", List.of()
			));
		
		return "mes/standardInfoMgt/bomInfoFrgmsVersion";
	}
	
	//상단 제품에서 선택했을 때 가져올 BOM데이터
	
	//상품 그리드 선택했을 때 BOM 그리드에 선택한 상품 보여주기
	@ResponseBody
	@GetMapping ("/bom-selected-frgmsVersion")
	public Map<String, Object> bomSelectedFrgmsVersion(@RequestParam("productCode") String pdtCode){
		
		List<BomInfoTotalDTO> bomtotal = bomInfoService.getBomInfoTotalList(pdtCode);
		
		//log.info("bomtotal"+bomtotal);
		
		
		List<Map<String, Object>> bomData = bomtotal.stream()
			    .map(b -> {
			        Map<String, Object> map = new HashMap<>();
			        map.put("BOM_NO", b.getBomNo());
			        map.put("PARENT_PDT_CODE", b.getParentPdtCode());
			        map.put("CHILD_ITEM_CODE", b.getChildItemCode());
					map.put("PDT_NAME", b.getPdtName()); 
					map.put("MTL_NAME", b.getMtlName());  
					map.put("BOM_QTY", b.getBomQty());
					map.put("BOM_UNIT", b.getBomUnit()); 
					map.put("PRC_TYPE_NAME",b.getPrcTypeName());
			        return map;
			    }).collect(Collectors.toList());
		
		//그리드에 출력시킬 컬럼 생성
		List<Map<String, Object>> columns = List.of(
				Map.of("header", "BOM 고유번호", "name", "BOM_NO"),
				Map.of("header", "제품 코드", "name", "PDT_CODE"),
				Map.of("header", "제품 이름", "name", "PDT_NAME"),
				Map.of("header", "원자재 코드", "name", "MTL_CODE"),
				Map.of("header", "원자재 이름", "name", "MTL_NAME"),
				Map.of("header", "수량", "name", "BOM_QTY"),
				Map.of("header", "단위", "name", "BOM_UNIT"),
				Map.of("header", "공정 단계", "name", "PRC_TYPE_NAME")
			);
			
		return Map.of(
				"columns", columns,
				"data", bomData
			);
	}
	//----------------------------------------- 끝 ------------------------------------
	
	//엑셀 파일 다운로드
	@GetMapping("/excel-file-down")
	public void downloadExcel(HttpServletResponse response) throws IOException {
	    String fileName = "bom_form.xls";
	    String filePath = "/excel/" + fileName;

	    // /static/ 디렉토리 기준으로 파일을 읽어옴
	    //log.info("filePath:"+filePath);
	    InputStream inputStream = new ClassPathResource(filePath).getInputStream();

	    //log.info("inputStream:"+inputStream);
	    response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
	    response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

	    // 파일 내용을 응답 스트림에 복사
	    StreamUtils.copy(inputStream, response.getOutputStream());
	    response.flushBuffer();
	}
	
	//상품 그리드에서 바로 수정
	@ResponseBody
	@GetMapping("/pdteditexe")
	public int pdteditexep(@ModelAttribute ProductsEditDTO pdtEditDto) throws JsonMappingException, JsonProcessingException {
		
		//log.info(pdtEditDto.toString());
		
		int resultEdit = productsService.pdtMainUpdate(pdtEditDto); 
		
		return resultEdit;
	}
	
	//BOM 그리드에서 바로 수정
	@ResponseBody
	@GetMapping("/bomeditexe")
	public int bomeditexep(@ModelAttribute BomEditDTO bomEditDto) throws JsonMappingException, JsonProcessingException {
		
		//log.info(bomEditDto.toString());
		
		int resultEdit = bomInfoService.bomMainUpdate(bomEditDto); 
		
		//return resultEdit;
		return 1;
	}
	
	//BOM페이지에서 상품 등록 
	@PostMapping("/pdtRegister")
	public String pdtRegister(@ModelAttribute ProductsDTO productsDTO) {
		
		productsDTO.setPdtNo(null);
		productsDTO.setPdtUseyn("Y");
		ProductsDTO dto = productsDTO;
		
		//DTO를 builder를 이용해서 entity로 변환
		Products entity = dto.toEntity();
		
		productsRepository.save(entity);
		log.info(entity.toString());
		
		return "redirect:bom-info";
	}
}
