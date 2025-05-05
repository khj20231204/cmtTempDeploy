package com.example.cmtProject.controller.erp.saleMgt;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hibernate.annotations.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.cmtProject.controller.erp.saleMgt.commonModel.PurchasesOrderModels;
import com.example.cmtProject.dto.erp.saleMgt.PurchasesOrderEditDTO;
import com.example.cmtProject.dto.erp.saleMgt.PurchasesOrderMainDTO;
import com.example.cmtProject.dto.erp.saleMgt.PurchasesOrderSearchDTO;
import com.example.cmtProject.dto.erp.saleMgt.SalesOrderEditDTO;
import com.example.cmtProject.dto.erp.saleMgt.SalesOrderMainDTO;
import com.example.cmtProject.dto.erp.saleMgt.SalesOrderSearchDTO;
import com.example.cmtProject.entity.erp.salesMgt.PurchasesOrder;
import com.example.cmtProject.entity.erp.salesMgt.SalesOrder;
import com.example.cmtProject.repository.erp.saleMgt.PurchasesOrderRepository;
import com.example.cmtProject.service.erp.saleMgt.PurchasesOrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/purchases")
public class PurchaseController {
	
	@Autowired
	private PurchasesOrderService purchasesOrderService;
	
	@Autowired
	private PurchasesOrderModels purchasesModels;
	
	@Autowired
	private PurchasesOrderRepository purchasesOrderRepository;
	
	//조회 페이지
	@GetMapping("/poform")
	public String soform(Model model) {
		
		//======================= 상단 container2의 메뉴 부분 가져오기 ===========
		//발주테이블에 있는 원자재 코드 목록 가져오기
		List<String> mtlCode = purchasesOrderRepository.findDistinctMtlCode();
		model.addAttribute("mtlCode", mtlCode);

		//발주테이블에 있는 공급처 목록 가져오기
		List<String> cltCode = purchasesOrderRepository.findDistinctCltCode();
		model.addAttribute("cltCode", cltCode);
		//============================== 끝 ================================
		
		//========================= 하단 메인 list부분 ========================
		List<PurchasesOrderMainDTO> poMainList = purchasesOrderService.poMainSelect();
		model.addAttribute("poMainList", poMainList);
		
		purchasesModels.commonPurchasesOrderModels(model);
		

		return "erp/salesMgt/purchasesOrderForm";
	}
	
	//상품코드에 해당하는 상품명 가져오기
	@GetMapping("/getMtlName")
	@ResponseBody
	public String getPdtName(@RequestParam("mtlCode") String mtlCode) {
		
		mtlCode = mtlCode.replaceAll("\"", ""); //따옴표 삭제
		String mtlName = purchasesOrderRepository.findByMtlName(mtlCode);
 
		return mtlName;
	}
	
	//거래처 코드에 해당하는 거래처명 가져오기
	//@PostMapping("/getCltName") //@RequestBody
	@GetMapping("/getCltName") //@RequestParam 
	@ResponseBody
	public String getCltName(@RequestParam("cltCode") String cltCode) {
		
		 cltCode = cltCode.replaceAll("\"", ""); 
		 String cltName = purchasesOrderRepository.findByCltName(cltCode);
		 
		 return cltName;
	}
		
	//발주 신규등록
	//purchases
	@GetMapping("/poRegisterForm")
	public String poRegisterForm(Model model) {
		
		purchasesModels.commonPurchasesOrderModels(model);

		//시퀀스 가져오기
		Long nextSeq = purchasesOrderRepository.getNextPurchasesOrderNextSequences();
		
		model.addAttribute("nextSeq",nextSeq); //발주번호
		
//		//th:object에서 사용할 객체 생성
	 	model.addAttribute("purchasesOrder", new PurchasesOrder());
		
		return "erp/salesMgt/poRegisterForm";
	}

	//발주코드 생성하는 부분
	@ResponseBody
	@GetMapping("/makePoCode")
	public String makePoCode(@RequestParam("data") String data) {

		//날짜 형태를 yyyyMMdd 헝태로 변경 - 오늘 날짜 가져오기
		/*LocalDate today = LocalDate.now();        
        DateTimeFormatter todayFormat = DateTimeFormatter.ofPattern("yyyyMMdd");
        String poToday = today.format(todayFormat);*/
        
        int count = purchasesOrderRepository.getNextPoCode(data);
        
		String poCode = "";
		data = data.replace("-", "");
		
		//생성되어야 할 갯수
		count++;
		
		if(count > 100) {
			poCode = "PO-" + data + "-" + count; 
		}else if(count > 10) {
			poCode = "PO-" + data + "-" + "0" + count;		
		}else if(count >= 0) {
			poCode = "PO-" + data + "-" + "00" + count;
		}else {
			poCode = "minus";
		}
		
		return poCode;
	}
	
	//발주 등록 실행
	@Transactional
	@PostMapping("/poregister")
	public String poRegister(@ModelAttribute PurchasesOrder purchasesOrder) {
		
		//주의! sequence 증가시 soNo값을 null로 줘야 insert가 제대로 동작
		purchasesOrder.setPoNo(null);
		purchasesOrder.setPoUseYn("Y");
		purchasesOrderRepository.save(purchasesOrder);
		purchasesOrderRepository.flush();

		//submit처리
		return "erp/salesMgt/submitSuccess";
	}
	
	//발주 수정 실행
	@ResponseBody
	@GetMapping("/poeditexe")
	//SalesOrderEditDTO의 soNo가 Long타입이라도 클라이어트로부터 받을 때 Stirng->Long으로 자동 변환해준다
	public String poEditExe(@ModelAttribute PurchasesOrderEditDTO poEditDto) throws JsonMappingException, JsonProcessingException {
		 
		/* 
		//json을 entity로 받는 방식
		//: 하지만 entity로 받는 경우 json값과 필드가 일치해야 하면 ManyToOne처럼 연관관계가 있거나 복잡하면 실패할 가능성이 있음
		ObjectMapper mapper = new ObjectMapper();
		List<SalesOrder> orders = mapper.readValue(json, new TypeReference<List<SalesOrder>>() {});
		
    	salesOrderRepository.saveAll(orders);
    	
    	//json을 dto로 받기
		ObjectMapper mapper = new ObjectMapper(); 
		List<SalesOrderDTO> editList = mapper.readValue(json, new TypeReference<List<SalesOrderDTO>>() {});
		
		System.out.println(editList);
		
		TypeReference : Jackson 라이브러리에서 제네릭 타입(JSON 컬렉션 등)을 역직렬화할 때 사용하는 클래스입니다.
		*/
		
		//main으로부터 empNo가 아니라 empId를 받아오기 때문에 empId를 변경한 경우 empNo를 찾아와서 SALES_ORDER테이블에서 변경(SALES_ORDER 테이블에 empNo가 있음)
		if(poEditDto.getColumnName().equals("empId")) {
			
			//empId에 해당하는 empNo를 가져오기 - JPA이용
			Long empNo = purchasesOrderRepository.findEmpNoByEmpId(poEditDto.getValue());
			
			//sono를 통해 empno를 업데이트한다
			purchasesOrderRepository.updateEmpNo(empNo, poEditDto.getPoNo());
			
		}else {
//				int updateResult = salesOrderService.soMainUpdate(soEditDto);
			int updateResult = purchasesOrderService.poMainUpdate(poEditDto);
			System.out.println("updateResult:" + updateResult);
		}

		return "success";
	}
	
	//발주 메인 화면에서 검색 버튼 클릭시 비동기 처리부분
	@GetMapping("/searchForm")
	@ResponseBody
	public List<PurchasesOrderMainDTO> searchForm(@ModelAttribute PurchasesOrderSearchDTO searchDto) {
		
		System.out.println("searchDto:"+ searchDto);
		
		List<PurchasesOrderMainDTO> mainDtoList = purchasesOrderService.poMainSearch(searchDto);
		return mainDtoList;
	}
	
	//선택돤 items 삭제
	@ResponseBody
	@PostMapping("/delItems")
	public String deleteItems(@RequestBody List<Map<String, Object>> data) {
		
		List<Integer> poNoList = new ArrayList<>();
		
		for(Map<String, Object> list : data) {
			
			Integer poNoTemp = (Integer)list.get("poNo");
			poNoList.add(poNoTemp);
		}
		
		String visibleType = "N";
		purchasesOrderRepository.updatePoVisibleByPoNo(visibleType, poNoList);
		
		return "SUCCESS";
	}
	
}
