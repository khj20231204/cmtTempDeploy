package com.example.cmtProject.controller.erp.saleMgt;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.cmtProject.controller.erp.saleMgt.commonModel.SalesOrderModels;
import com.example.cmtProject.dto.erp.saleMgt.SalesOrderEditDTO;
import com.example.cmtProject.dto.erp.saleMgt.SalesOrderMainDTO;
import com.example.cmtProject.dto.erp.saleMgt.SalesOrderSearchDTO;
import com.example.cmtProject.entity.erp.employees.Employees;
import com.example.cmtProject.entity.erp.salesMgt.SalesOrder;
import com.example.cmtProject.repository.comm.CommonCodeDetailRepository;
import com.example.cmtProject.repository.erp.employees.EmployeesRepository;
import com.example.cmtProject.repository.erp.saleMgt.ClientsRepository;
import com.example.cmtProject.repository.erp.saleMgt.SalesOrderRepository;
import com.example.cmtProject.repository.erp.saleMgt.SalesOrderStatusRepository;
import com.example.cmtProject.repository.mes.standardInfoMgt.ProductsRepository;
import com.example.cmtProject.service.erp.saleMgt.SalesOrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/sales")
public class saleController {

	@Autowired
	private ProductsRepository productsRepository;
	
	@Autowired
	private SalesOrderRepository salesOrderRepository;
	
	@Autowired
	private ClientsRepository clientsRepository;
	
	@Autowired
	private EmployeesRepository employeesRepository;
	
	@Autowired
	private CommonCodeDetailRepository commoncodeDetailRepository;
	
	@Autowired
	private SalesOrderService salesOrderService;
	
	@Autowired
	private SalesOrderStatusRepository salesOrderStatusRepository;
	
	@Autowired
	private SalesOrderModels salesOrderModels;
	
	//조회 페이지
	@GetMapping("/soform")
	public String soform(Model model) {
		
		//========== 상단 container2의 제품 코드와 거래처 코드 목록 가져오기 ==========
 		//-수주 목록에 있는 제품-
 		//수주 목록에 있는 제품 코드를 가져와 중복 제거 후 제품에서 제품명 출력
 		//-- 수주테이블에 있는 제품 코드 --
 		List<String> pdtCode = salesOrderRepository.findByGetPdtCode();
 		Collections.sort(pdtCode);
 		model.addAttribute("pdtCode",pdtCode);
 		
 		//-수주 목록에 해당하는 거래처-
 		//수주 목록에 있는 거래처 코드
 		List<String> cltCode = salesOrderRepository.findByGetCltCode();
 		model.addAttribute("cltCode",cltCode);
 		//============================== 끝 ================================
 		
 		//========================= 하단 메인 list부분 ========================
 		//수주 메인 목록(clients, products, warehouses, employees 조인)
 		List<SalesOrderMainDTO> soMainList = salesOrderService.soMainSelect();
 		log.info("soMainList:"+ soMainList);
 		
 		model.addAttribute("soMainList",soMainList);
 		//salesOrderModels로 clients와 products와 warehouses 데이터를 다 가져오기 때문에 이거 사용 안함
 		
 		//수주 목록
 		//거래처명, 고객명, 사원명, 창고명이 안뜬다, code에 대한 정보만 있다
 		List<SalesOrder> allList = salesOrderRepository.findAll();
 		model.addAttribute("soModel", allList);
 		
 		//거래처명, 고객명, 사원명, 창고명 등을 가져오기 위해 전달하는 model
 		salesOrderModels.commonSalesOrderModels(model);

		return "erp/salesMgt/salesOrderForm";
	}
	
	//상품코드에 해당하는 상품명 가져오기
	@GetMapping("/getPdtName")
	@ResponseBody
	public String getPdtName(@RequestParam("pdtCode") String pdtCode) {
		
		pdtCode = pdtCode.replaceAll("\"", ""); 
		String pdtName = salesOrderRepository.findByGetPdtName(pdtCode);
 
		return pdtName;
	}
	
	//거래처 코드에 해당하는 거래처명 가져오기
	//@PostMapping("/getCltName") //@RequestBody
	@GetMapping("/getCltName") //@RequestParam 
	@ResponseBody
	public String getCltName(@RequestParam("cltCode") String cltCode) {
		
		 cltCode = cltCode.replaceAll("\"", ""); 
		 String cltName = salesOrderRepository.findByGetCltName(cltCode);
		 
		 return cltName;
	}
	
	//수주 등록 창으로 넘길 데이터들
	@GetMapping("/soregisterform")
	public String soregisterform(Model model) {
 		
		salesOrderModels.commonSalesOrderModels(model);
				
	 	//수주번호 다음 시쿼스 가져오기
		Long nextSeq = salesOrderRepository.getNextSalesOrderNextSequences();
		
	 	model.addAttribute("nextSeq", nextSeq); //수주 번호
	 	
	 	//th:object에서 사용할 객체 생성
	 	model.addAttribute("salesOrder", new SalesOrder());
	 	
		return "erp/salesMgt/soRegisterForm";
	}
	
	//수주 코드 생성하는 메서드
	@ResponseBody
	@GetMapping("/makePoCode")
	public String makePoCode(@RequestParam("data") String data) {
		
		//날짜 형태를 yyyyMMdd 헝태로 변경
		LocalDate today = LocalDate.now();        
        DateTimeFormatter todayFormat = DateTimeFormatter.ofPattern("yyyyMMdd");
        String soToday = today.format(todayFormat);
        
        int count = salesOrderRepository.getNextSoCode(data);
        
		String soCode = "";
		data = data.replace("-", "");
		
		//생성되어야 할 갯수
		count++;
		
		if(count > 100) {
			soCode = "SO-" + data + "-" + count; 
		}else if(count > 10) {
			soCode = "SO-" + data + "-" + "0" + count;		
		}else if(count >= 0) {
			soCode = "SO-" + data + "-" + "00" + count;
		}else {
			soCode = "minus";
		}
		
		return soCode;
	}
	
	@Transactional
	@PostMapping("/soregister")
	public String soRegister(@ModelAttribute SalesOrder salesOrder) {
		
		//주의! sequence 증가시 soNo값을 null로 줘야 insert가 제대로 동작
		salesOrder.setSoNo(null); 
		salesOrder.setWhsCode(null);
		salesOrder.setSoVisible("Y");
		
		//입력창에서 받는 건 sipDate밖에 없다. 이 값과 soDueDate를 같게 하기 위해 설정
		salesOrder.setSoDueDate(salesOrder.getShipDate());
		
		//log.info("salesOrder=========================:" + salesOrder);
		
		
		salesOrderRepository.save(salesOrder);
		salesOrderRepository.flush();
		
		return "erp/salesMgt/submitSuccess";
	}
	
	//수주 수정 창으로 넘길 데이터들
	@GetMapping("/soeditform")
	public String soEditForm(@RequestParam("gridCheck") String gridCheck, Model model) {
		
		//선택된 숫자 형태의 문자열을 list로 변환
		gridCheck = gridCheck.substring(1,gridCheck.length()-1);
		
		List<Integer> gridCheckList = Arrays.stream(gridCheck.split(","))
				.map(Integer::parseInt)
                .collect(Collectors.toList());

		//main그리드에서 선택된 항목들의 데이터 가져오기
 		List<SalesOrder> soEditorSelected = salesOrderRepository.findByEditorSelectedList(gridCheckList);
 		model.addAttribute("soEditorSelected", soEditorSelected);
 		
 		//거래처명, 고객명, 사원명, 창고명 등을 가져오기 위해 전달하는 model
 		salesOrderModels.commonSalesOrderModels(model);
 		
 		//수정 창에서 사용할 th:object
 		model.addAttribute("salesOrder", new SalesOrder());
 		
		return "erp/salesMgt/soEditForm";
	}
	
	//수주 수정 실행
	@ResponseBody
	@GetMapping("/soeditexe")
	//SalesOrderEditDTO의 soNo가 Long타입이라도 클라이어트로부터 받을 때 Stirng->Long으로 자동 변환해준다
	public String soEditExe(@ModelAttribute SalesOrderEditDTO soEditDto) throws JsonMappingException, JsonProcessingException {
		 
		/* 
		//json을 entity로 받는 방식
		//: 하지만 entity로 받는 경우 json값과 필드가 일치해야 하면 ManyToOne처럼 연관관계가 있거나 복잡하면 실패할 가능성이 있음
		ObjectMapper mapper = new ObjectMapper();
		List<SalesOrder> orders = mapper.readValue(json, new TypeReference<List<SalesOrder>>() {});
		
    	salesOrderRepository.saveAll(orders);
    	
    	//json을 dto로 받기
		ObjectMapper mapper = new ObjectMapper(); 
		List<SalesOrderDTO> editList = mapper.readValue(json, new TypeReference<List<SalesOrderDTO>>() {});
		
		TypeReference : Jackson 라이브러리에서 제네릭 타입(JSON 컬렉션 등)을 역직렬화할 때 사용하는 클래스입니다.
		*/
		
		//main으로부터 empNo가 아니라 empId를 받아오기 때문에 empId를 변경한 경우 empNo를 찾아와서 SALES_ORDER테이블에서 변경(SALES_ORDER 테이블에 empNo가 있음)
		if(soEditDto.getColumnName().equals("empId")) {
			
			//empId에 해당하는 empNo를 가져오기 - JPA이용
			Long empNo = salesOrderRepository.findEmpNoByEmpId(soEditDto.getValue());
			
			salesOrderRepository.updateEmpNo(empNo, soEditDto.getSoNo());
			
		}else {
			int updateResult = salesOrderService.soMainUpdate(soEditDto);
		}

		return "success";
	}
	
	//employees 테이블에서 부서와 직급에 해당하는 사원 이름 목록 가져오기
	@GetMapping("/getEmpName")
	@ResponseBody
	public List<Employees> getEmpName(@RequestParam("deptCode") Long deptNo1,
							@RequestParam("posCode") Long positionNo) {
		
		List<Employees> empList = employeesRepository.getEmpName(deptNo1, positionNo);
		
		return empList;
	}
	
	//수주 메인 화면에서 검색 버튼 클릭시 비동기 처리부분
	@GetMapping("/searchForm")
	@ResponseBody
	//public List<SalesOrderMainDTO> searchForm(@RequestBody SalesOrderSearchDTO searchDto) {
	public List<SalesOrderMainDTO> searchForm(@ModelAttribute SalesOrderSearchDTO searchDto) {
		
		List<SalesOrderMainDTO> mainDtoList = salesOrderService.soMainSearch(searchDto);
		
		return mainDtoList;
	}
	
	//선택돤 items 삭제
	@ResponseBody
	@PostMapping("/delItems")
	public String deleteItems(@RequestBody List<Map<String, Object>> data) {
		
		List<Integer> soNoList = new ArrayList<>();
		
		for(Map<String, Object> list : data) {
			
			Integer soNoTemp = (Integer)list.get("soNo");
			soNoList.add(soNoTemp);
		}
		
		String visibleType = "N";
		salesOrderRepository.updateSoVisibleBySoNo(visibleType, soNoList);
		//salesOrderRepository.updatesoUseYnBySoNo(visibleType, soNoList);
		
		return "SUCCESS";
	}
	
	@GetMapping("/poform")
	public String purchaseOrderForm() {
		
		return "erp/salesMgt/purchaseOrderForm";
	}
	
	
	@GetMapping("/po")
	public String purchaseOrder() {
		
		return "redicrect:/";
	}
	
	@GetMapping("/shipment")
	public String shipment() {
		
		return "erp/salesMgt/shipment";
	}
	
	@GetMapping("/chart")
	public String chart() {
		
		return "erp/salesMgt/chart";
	}
	
	@GetMapping("/modal")
	public String modal() {
		
		return "erp/salesMgt/modal";
	}
	
	@GetMapping("/grid")
	public String grid() {
		
		return "erp/salesMgt/grid";
	}
	
	@GetMapping("/baseSale")
	public String baseSale() {
		
		return "erp/salesMgt/baseSale";
	}
	
	@GetMapping("/myPage")
	public String myPage(Model model) throws JsonProcessingException {
		
		List<String> columnList = new ArrayList<>();
		columnList.add("1213");
		columnList.add("1214");
		
		List<String> dataList = new ArrayList<>();
		dataList.add("1213");
		dataList.add("1214");
		
		// Controller
		ObjectMapper mapper = new ObjectMapper();
		String columnsJson = mapper.writeValueAsString(columnList);
		String dataJson = mapper.writeValueAsString(dataList);

		model.addAttribute("gridTopConfig", Map.of(
		    "columns", columnsJson,
		    "data", dataJson
		));
		 
		
		return "erp/salesMgt/myPage";
	}
}

