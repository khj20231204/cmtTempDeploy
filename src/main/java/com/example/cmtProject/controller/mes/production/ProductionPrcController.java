package com.example.cmtProject.controller.mes.production;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.cmtProject.dto.mes.production.LotOrderDTO;
import com.example.cmtProject.dto.mes.production.LotOriginDTO;
import com.example.cmtProject.dto.mes.production.LotUpdateDTO;
import com.example.cmtProject.dto.mes.production.SavePRCDTO;
import com.example.cmtProject.dto.mes.production.SemiFinalBomQty;
import com.example.cmtProject.dto.mes.production.WorkOrderDTO;
import com.example.cmtProject.dto.mes.qualityControl.IpiDTO;
import com.example.cmtProject.dto.mes.standardInfoMgt.BomInfoDTO;
import com.example.cmtProject.dto.mes.standardInfoMgt.ProductTotalDTO;
import com.example.cmtProject.mapper.mes.qualityControl.IpiMapper;
import com.example.cmtProject.service.mes.production.LotService;
import com.example.cmtProject.service.mes.production.ProductionPrcService;
import com.example.cmtProject.service.mes.qualityControl.IpiService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/production")
public class ProductionPrcController {
	
	/*
	 * - 공정 상황 설명 -
	 * 
	 * 대기(SB) -> 공정 작업 대기(PS) -> 진행중(공정 작업 중)(RN) -> 완료(CP)
	 * 
		작업 등록을 클릭하면 모달창이 뜨고 작업 지시 등록을 누르면 SB
		하단 작업 시작 버튼을 클릭하면 PS : 이때 공정 현황의 셀렉트 박스 목록에 나타난다
		공정 현황에서 공정 작업 등록 버튼을 누르면 셀렉트 박스가 나타나고 시작을 클릭하면 RN으로 상태 변환
		=> RN으로 돼 버리면 이후 셀렉트 박스 목록에는 나타나지 않는다
		두 번째 하단 그리드에서 작업 완료 버튼을 클릭하면 해당 작업만 CP로 변환
		
		*작업 지시서에는 완제품만 있고 LOT 테이블에는 반제품과 완제품이 있어서 상태를 따로 둔다        
		
		-- 작업지시서에 PS만 공정 현황 셀렉트박스에 노출
		SELECT * FROM WORK_ORDER;
		UPDATE WORK_ORDER SET WO_STATUS_CODE = 'PS' WHERE WO_CODE = 'MSC001'  -- 넘어온 데이터 작업을 위해 PS로 변경
		
		-- 공정 현황에서 셀렉트 박스를 선택한 작업만 상태가 RN으로 변경 된다. 
		SELECT * FROM LOT;
		UPDATE LOT SET WO_STATUS_NO = 'PS' -- 넘어온 데이터 작업을 위해 PS로 변경
		
		DELETE SAVE_PRC -- 현재 공정 테이블 내용 삭제
	*/
	
	/*
	 * - 권한과 공정에 따른 버튼 활성화/비활성화 -
	 * 
	 * 공정 작업 중 테이블 생성 -> 테이블에 데이터가 있으면 '공정 작업 등록' 버튼 막고, 해당 데이터를 불러오고 
	 * 					->	없으면 '공정 작업 등록 버튼' 클릭 가능 
	 * 
	 * 데이터가 있는 경우 처리 
	 * server:
	 * 	- 상단 그리드 : 작업 코드에 해당하는 데이터 불러오기 
	 * 	- 두번째 하단 그리드 : db에서 상태를 가져온다
	 * 
	 * client:
	 * 	- parentPdtCodeList(첫번째 하단 왼쪽 트리 값들) : db에서 lot의 cp인 parentPdtCode
	 * 	- presentBtnRow(두번째 하단 그리드 버튼) : 최초 로딩시 / 클릭시 db에서 불러온다
	 * 		=> grid의 rowKey일치 시켜야 한다. rowKey는 0부터 시작
	 * 		=> 현재 상태가 RN 중 가장 ROWNUM이 큰 것
	 * 
	 * 공정 상황을 저장하기 위해 SAVE_PRC 테이블 생성
	 * 저장할 데이터 : woCode(상단 그리드), pdtCode, parentPdtCode
	 * 
	 * 두번째 하단 그리드에 버튼을 클릭했을 때 저장 값들 
	 * : FINISH_TIME, WO_STATUS_NO, BOM_QTY, START_TIME은 LOT 테이블에 업데이트
	 * : WO_CODE, PDT_CODE, PARENT_LOT_CODE을 SAVE_PRC 테이블에 저장
	 * : WO_STATUS_NO 상태 값을 RN -> CP로 변경, 기존값이 있는 재로딩시 RN 기준으로 불러오기 때문에 다음 값을 제대로 가져올 수 있다
	 * 	 * 
	 * 수량 전송을 하면 SAVE_PRC 테이블을 DELETE
	 * 
	 * 		CREATE TABLE SAVE_PRC(
	 *		    -- 두번째 하단 그리드를 그르기 위한 데이터
	 *		    WO_CODE VARCHAR(20),
	 *		    -- 상단 그리드를 그리기 위한 데이터
	 *		    PDT_CODE VARCHAR(100),
	 *		    -- 첫번째 하단 왼쪽 트리 상태를 나타내기 위한 데이터
	 * 		    PARENT_LOT_COCE VARCHAR(200)
	 *		)
	 *		
	 *		
	 * - 권한 : 도중에 불량이 났거나 잘못 생산 되었다고 가정 -
	 * 기존 생산품은 품질로 보내고, 작업지시서는 없어지고, lot테이블은 그대로 유지
	 * save_prc테이블 delete, admin 권한의 수량 데이터 전송 버튼 활성화, 공정 작업 등록 버튼 활성화 
	 * */

	@Autowired
	private ProductionPrcService productionPrcService;
	
	@Autowired
	private LotService lotService;
	
	@Autowired
	private IpiService ipiService;
	
	
	@Autowired
	private IpiMapper ipiMapper;
	
	//공정 현황 메인 페이지
	@GetMapping("/productionPrc")
	public String getMethodName(Model model) {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName(); // 로그인한 아이디 (admin, user 등)
		//username:971114 - user
		//username:981114 - manager
		//username:991114 - admin
		
		//작업상태가 StandBy인 것만 가져온다(모달 셀렉트 박스에서 workOrderSBList 사용)
		List<WorkOrderDTO> workOrderSBList = productionPrcService.selectWoStandByList();
		//log.info("workOrderList:"+workOrderSBList);
		
		model.addAttribute("workOrderSBList", workOrderSBList);
		
		//위에 데이터는 다 넘겨주고 클라이언트에서 저장 유무 판단
		//SAVE_PRC테이블에 데이터가 있다는 것만으로 현재 작업 중 임을 알 수 있다
		
		int checkSavePRC = lotService.selectCheckSavePRC();
		
		String nowWoCode = "";  
		String nowPdtCode = ""; 
		
		/*
		 * rnRowNum은 클라이언트의 버튼 formatter의 변수 presentBtnRow에서 사용하기 위해서 항상 넘겨줘야 하는데
		 * if(checkSavePRC > 0)로 현재 작업 중 이면 WO_STATUS_NO가 RN중 가장 큰 ROWNUM의 값을
		 * 만약 작업중이 아니라 최초 작업 시작이라면 "모달의 시작 버튼을 누를 때" 총 데이터 ROWNUM 중 가장 큰 값을 넘겨준다 
		 * => 모달 시작 버튼을 누를 때 LOT테이블이 바로 만들어진 직후라 값을 못 받아온다
		 * 그래서, 하단 "작업 현황 버튼"을 눌렀을 때 ROWNUM을 받아온다
		 * 
		 * :rnRowNum은 완제품이 나오고 나면 null반환
		 */
		String rnRowNum = ""; //LOT 테이블의 WO_STATUS_NO에 RN상태 ROWNUM 가져오기 
		
		/*
		 * nowWoCode에 데이터가 없으면 상태가 전부 CP이기 때문에 rnRowNum은 = 0 이 된다.
		 * nowWoCode에 데이터가 있으면 rnRowNum은 > 0 이 된다.
		 * 
		 * 모달의 시작 버튼과 두번째 하단의 수량 전송에서 한꺼번에 업데이트 진행.
		 * */
		
		if(checkSavePRC > 0) { //현재 작업 중 이면
			List<SavePRCDTO> nowSavePrc = lotService.selectSavePRC();
			/*	woCode	 pdtCode  parentPdtCode
			 * 'MSC005','WIP009', 'WIP005'
			 * 'MSC005','WIP009', 'WIP004'
			 * 'MSC005','WIP009', 'MTL002'
			 * */
			
			nowWoCode = nowSavePrc.get(0).getWoCode();
			nowPdtCode = nowSavePrc.get(0).getPdtCode();
			//작업 중이면 RN중 가장 큰 rownum
			rnRowNum = lotService.selectRNRowNum(nowWoCode);
			
			//nowSavePrc에서 parentPdtCode만 빼서 list로 만들기
			List<String> parentPdtCodeList = nowSavePrc.stream()
				    .map(SavePRCDTO::getParentPdtCode)
				    .distinct()
				    .collect(Collectors.toList());
			
			Map<String, String> savePRC = new HashMap<>();
			savePRC.put("nowWoCode", nowWoCode); //작업 중인 woCode
			savePRC.put("nowPdtCode", nowPdtCode); //작업 중이 pdtCode
			savePRC.put("rnRowNum", rnRowNum); // LOT테이블에서 RN 중 가장 큰 rownum
			
			model.addAttribute("savePRC", savePRC);
			model.addAttribute("parentPdtCodeList", parentPdtCodeList); //하단 왼쪽 트리 밑 줄그을 데이터	
		}

		model.addAttribute("username", username);
		
		return "mes/production/productionPrc";
	}
	
	//모달 셀렉트 박스에서 실행
	@GetMapping("/woCodeSelected")
	@ResponseBody
	public List<WorkOrderDTO> woCodeSelected(@RequestParam("data") String data) {
		
		//모달 셀렉트 박스에서 선택된 woCode에 해당하는 데이터를 가져온다 
		List<WorkOrderDTO> selectWoCodeList = productionPrcService.selectWoCodeList(data);
		
		//log.info("selectWoCodeList:"+selectWoCodeList);
		return selectWoCodeList;
	}
	
	//모달 시작 버튼에서 실행
	@GetMapping("/pdtCodeSelected")
	@ResponseBody
	public List<BomInfoDTO> pdtCodeSelected(@RequestParam("pdtCode") String pdtCode, @RequestParam("woCode") String woCode, @RequestParam("woQty") String woQty) {
	//public LotBomPathBindingDTO pdtCodeSelected(@RequestParam("pdtCode") String pdtCode,  @RequestParam("woCode") String woCode) {
		
		//PARENT_PDT_CODE => 앞 과정에서 투입되는 제품
		//CHILD_ITEM_CODE => 현재 제품/최종 제품
		
		/*
		- BOM테이블에서 path를 가져와서 해당 정보를 바탕으로 Lot을 생성 후 Lot테이블에 입력하고
		상단 그리드에는 BOM테이블에서 가져온 정보를 출력한다 -
		  
		BOM테이블 결과 => 
		PARENT_PDT_CODE CHILD_ITEM_CODE
				WIP004	 MTL-005
				WIP005	 MTL-006
				WIP009	 WIP004
				WIP009	 WIP005
				WIP009	 MTL-009
		
		BOM 테이블에서
		1	1	FP002	WIP014	SEMI_FINISHED	AS	1	EA	FP002
		2	2	WIP014	WIP011	SEMI_FINISHED	AS	1	EA	FP002 ← WIP014
		3	3	WIP011	MTL-011	RAW_MATERIAL	PR	1	EA	FP002 ← WIP014 ← WIP011
		4	3	WIP011	WIP007	SEMI_FINISHED	PA	1	EA	FP002 ← WIP014 ← WIP011
		5	4	WIP007	MTL-007	RAW_MATERIAL	PR	1	EA	FP002 ← WIP014 ← WIP011 ← WIP007
		6	4	WIP007	WIP002	SEMI_FINISHED	WE	1	EA	FP002 ← WIP014 ← WIP011 ← WIP007
		7	5	WIP002	MTL-002	RAW_MATERIAL	PR	1	EA	FP002 ← WIP014 ← WIP011 ← WIP007 ← WIP002
		8	4	WIP007	WIP003	SEMI_FINISHED	WE	1	EA	FP002 ← WIP014 ← WIP011 ← WIP007
		9	5	WIP003	MTL-004	RAW_MATERIAL	PR	1	EA	FP002 ← WIP014 ← WIP011 ← WIP007 ← WIP003 
		*/
		List<BomInfoDTO> selectPdtCodeList = productionPrcService.selectPdtCodeList(pdtCode);
		/*
		
		결과 => 재귀의 결과에 중복 제거한 pdtCode만 가져오기
		WIP009
		WIP004
		WIP005
		MTL-009
		MTL-005
		MTL-006
		
		위에서 가져온 pdtCode에서 일률적으로 LOT번호를 생성 후 해당 데이터에 LOT번호를 삽입 
		
		CHILD_ITEM_CODE와 PARENT_PDT_CODE 각각 중복 제거 후 set으로 합치기
		*/
		List<String> childPdtCodeList = productionPrcService.selectChildPdtCodeList(pdtCode);
		List<String> parentdPdtCodeList = productionPrcService.selectParentdPdtCodeList(pdtCode);
		
		Set<String> setCodeArray = new HashSet<>();
		//다음과 같이 list를 set에 입력하면 자동 중복 제거
		setCodeArray.addAll(childPdtCodeList);
		setCodeArray.addAll(parentdPdtCodeList);

		//log.info("Merged Set: " + setCodeArray);
		//[MTL-003, MTL-002, MTL-001, WIP006, FP001, WIP013, WIP002, MTL-010, WIP001, WIP010]
		
		//루프를 돌면서 insert를 해도 바로 적용된 order값을 가져오지 못하기 때문에 내가 직접 order를 증가시킨다.
		LocalDate today = LocalDate.now(); //2025-04-21
		String todayStr = today.format(DateTimeFormatter.ofPattern("yyyyMMdd")); //20250421
		
		//3)LOT테이블에서 오늘 날짜의 해당 공정에 해당하는 ORDER순서 가져오기
		//LOT-20250421-PR-02 이 형식에서 벗어나면 안된다, 값이 없는 경우 null 발생
		/*
		SELECT
			SUBSTR(CHILD_LOT_CODE, 14, 2) AS PRC_TYPE,
			NVL(MAX(TO_NUMBER(SUBSTR(CHILD_LOT_CODE, 17))),0) AS MAX_SEQ
		FROM LOT
		WHERE CHILD_LOT_CODE LIKE 'LOT-' || #{todayStr} || '-%'
		  AND SUBSTR(CHILD_LOT_CODE, 14, 2) = #{type}
		GROUP BY SUBSTR(CHILD_LOT_CODE, 14, 2)
		*/
		LotOrderDTO lotOrderFP = lotService.getLotOrderPrcType(todayStr, "FP");
		LotOrderDTO lotOrderPR = lotService.getLotOrderPrcType(todayStr, "PR");
		LotOrderDTO lotOrderWE = lotService.getLotOrderPrcType(todayStr, "WE");
		LotOrderDTO lotOrderPA = lotService.getLotOrderPrcType(todayStr, "PA");
		LotOrderDTO lotOrderAS = lotService.getLotOrderPrcType(todayStr, "AS");
	
		int fpInt = lotOrderFP != null ?  lotOrderFP.getMaxSeq() : 0;
		int prInt = lotOrderPR != null ?  lotOrderPR.getMaxSeq() : 0;
		int weInt = lotOrderWE != null ?  lotOrderWE.getMaxSeq() : 0;
		int paInt = lotOrderPA != null ?  lotOrderPA.getMaxSeq() : 0;
		int asInt = lotOrderAS != null ?  lotOrderAS.getMaxSeq() : 0;
		int mtlInt = 0;

		Map<String, String> pdTCodeLotMapping = new HashMap<String, String>();
		/* 
		- lot새로 생성 -
		
		setCodeArray를 돌면서 MTL, FP, PR, WE, PA, AS 인지 검색
		int 값을 각각 1번씩만 증가 후 입력
		*/
		
		for(String s : setCodeArray) {

			int orderNum = 0;
			//prcType을 가져온다
			String prcType = productionPrcService.getPrcType(s);
			
			//prcType이 null인 경우 MTL이 된다.
			if(prcType == null) {
				prcType = "IN";
				orderNum = ++mtlInt;
			} else if(prcType.equals("FP")) {
				orderNum = ++fpInt;
			}else if(prcType.equals("PR")) {
				orderNum = ++prInt;
			}else if(prcType.equals("WE")) {
				orderNum = ++weInt;
			}else if(prcType.equals("PA")) {
				orderNum = ++paInt;
			}else if(prcType.equals("AS")) {
				orderNum = ++asInt;
			}else {
				log.error("Parent 목록에 없는 공정 타입 입니다.");
			}
		
			pdTCodeLotMapping.put(s, makeLotCode(prcType, todayStr, orderNum));
		}
		
		Map<String, String> checkLot = new HashMap<>();
		LotOriginDTO lod = new LotOriginDTO();
		
		int checkLast = 0; //startTime 때문에 사용
		
		//MFG_SCHEDULES_DETAIL 에서 수량 가져오기
		List<SemiFinalBomQty> bomQtyList = lotService.getBomQty(woCode);
		//MFG_SCHEDULES_DETAIL과 수량을 맞추기 위한 PARENT_PDT_CODE
		//List<String> parentPdtCodeList = lotService.selectParentPdtCode(pdtCode);
		
		for(BomInfoDTO b : selectPdtCodeList) {
			
			//================ 부모 컬럼 lot생성=========================================================
			
			//위에서 만든 MAP에서 부모 코드에 해당하는 LOT 부여
			String parentLot = pdTCodeLotMapping.get(b.getParentPdtCode());
		    
			//================ 자식 컬럼 lot생성=========================================================
			
			//위에서 만든 MAP에서 자식 코드에 해당하는 LOT 부여
			String childLot = pdTCodeLotMapping.get(b.getChildItemCode());
			
			//========================= 위에서 생성한 lot를 lot테이블에 입력 ============================================= 
			Long lotNoMax = lotService.getLotNo(); 
			
			lotNoMax++;
			Long lotNo = lotNoMax;
			String lotCode = parentLot; //이후 단계 
			
			String parentPdtCode = b.getParentPdtCode();
			String childPdtCode = b.getChildItemCode();
			
			String createDate = String.valueOf(today);
		
			String prcType = b.getBomPrcType();
			String bomQty = b.getBomQty();
			String bomUnit = b.getBomUnit();

			String childLotCode = childLot;
			
			LocalTime time = LocalTime.now();
			String startTime = "00:00:00";
			//startTime : 데이터가 order 된 상태로 넘어오기 때문에 가장 마지막 데이터가 가장 첫번째 작업으로 온다
			//가장 마지막 데이터(첫번째로 이루어질 작업)만 startTime을 현재 시간으로 입력한다
			if (checkLast == selectPdtCodeList.size() - 1) {
				startTime = time.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
		    }
			
			String woStatusNo = "RN";
			String useYN = "Y";
			
			lod.setLotNo(Long.valueOf(lotNo));
			lod.setChildLotCode(childLot);
			lod.setParentLotCode(parentLot);
			lod.setChildPdtCode(childPdtCode);
			lod.setParentPdtCode(parentPdtCode);
			lod.setCreateDate(today);
			
			String input = "00/01/01"; // 00/00/00 형식 입력
	        // 포맷터 생성
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy/MM/dd");
	        // 문자열 → LocalDate 변환
	        LocalDate date = LocalDate.parse(input, formatter);
			lod.setEndDate(date);
			
			lod.setPrcType(prcType);
			
			// -- 제조계획 수량 일치 --
			//MFG_SCHEDULES
			//모달에서 제조계획을 클릭해서 작업지시서가 만들어질 때 woQty가 입력되야한다 
			lod.setWoQty(woQty);  // -- 완제품 수량
			
			for(SemiFinalBomQty sfb : bomQtyList) {
				//log.info("work_order_detail ParentPdtCode()"+sfb.getParentPdtCode() + " : lot테이블 ParentPdtCode:"+ b.getParentPdtCode());
				if(sfb.getParentPdtCode().equals(b.getParentPdtCode())) {
					lod.setBomQty(sfb.getMsQty()); // -- 반제픔 수량
				}
			}
			
			lod.setBomUnit(bomUnit);
			lod.setLineCode("");
			lod.setEqpCode("");
			lod.setWoCode(woCode);
			lod.setStartTime(startTime);
			lod.setFinishTime("00:00:00");
			lod.setWoStatusNo(woStatusNo);
			lod.setUseYn(useYN);
			
			//---------------------------------- 하단 그리드 작업 중으로 주석 처리
			lotService.insertLot(lod);
			
			checkLast++;
		}//for(BomInfoDTO b : selectPdtCodeList) {
		
		//MFG 상태 업데이트
		String mfgscd = "제조중";
		String mfgPlan = "생산중";
		lotService.updateMfgScdStatus(woCode, mfgscd);
		lotService.updateMfgPlanStatus(woCode, mfgPlan);
		
		//---------------------------------- 하단 그리드 작업 중으로 주석 처리
		//작업지시에서 받아온 작업 RN으로 변경
		productionPrcService.updateWoStatus(woCode);
		
		return selectPdtCodeList;
	}

	
	private String makeLotCode(String prcType, String todayStr, int orderNum) {
				
		String lot = "";
		
		//LOT-20250421-PR-02 
		if(orderNum > 100) {
			lot = "LOT-" + todayStr + "-" + prcType + "-" + orderNum;
		}else if(orderNum >= 10) {
			lot = "LOT-" + todayStr + "-" + prcType + "-0" + orderNum;
		}else if(orderNum >= 1) {
			lot = "LOT-" + todayStr + "-" + prcType + "-00" + orderNum;
		} /* == 0 조건이 있으면 값이 이상하게 들어간다
			 * else if(orderNum == 0) { lot = "LOT-" + todayStr + "-" + prcType + "-01" +
			 * orderNum; }
			 */
		else{
			log.error("orderType가 0과 음수가 나옴");
		}
		
		return lot;
				
	}
	
	//첫번째 하단의 왼쪽 트리에서 제품 정보를 클릭시 비동기
	@PostMapping("/pdtInfo")
	@ResponseBody
	public List<ProductTotalDTO> pdtInfo(@RequestParam("pdtCode") String pdtCode) {
		
		List<ProductTotalDTO> productTotalDto = productionPrcService.selectProductInfo(pdtCode);
		
		return productTotalDto;
		
	}
	
	//두번째 하단 작업 현황 버튼 클릭시 비동기
	@PostMapping("/prcBoard")
	@ResponseBody
	public List<LotOriginDTO> prcBoard(@RequestParam("woCode") String woCode) {
		
		List<LotOriginDTO> selectLotOrigin = lotService.selectLotOrigin(woCode);
		//log.info("/prcBoard의 selectLotOrigin:" + selectLotOrigin);
		
		return selectLotOrigin;
	}
	
	//두번째 하단 그리드 안에 버튼 클릭시
	@PostMapping("/jobCmpl")
	@ResponseBody
	public List<LotOriginDTO> jobCmpl(@RequestBody LotUpdateDTO lotUpdateDTO) {
		
		/*
		parentPdtCode가 소모되어 childPdtCode가 되므로 parentPdtCode가 소모품, 소모량
		log.info(lotUpdateDTO.toString()); //LotUpdateDTO(lotNo=163, bomQty=1, childPdtCode=WIP002, parentPdtCode=MTL-002, woCode=MSC001, pdtCode=FP001)

		//FINISH_TIME, WO_STATUS_NO, BOM_QTY 업데이트
		if(childPdtCode가 PDT_CODE와 일치하지 않는 경우){
			lotNo - 1 => START_TIME 업데이트
		}
		
		if(childPdtCode가 PDT_CODE와 일치하는 경우){
			작업지시서의 WO_CODE와 일치하는 PDT_CODE이면 WORK_ORDER의 WO_STATUS_CODE도 CP로 업데이트 
		} 
		*/
		
		Long lotNo = Long.valueOf(lotUpdateDTO.getLotNo()); 
	
		LotOriginDTO lotOrigin = new LotOriginDTO();
		
		//LOT_NO
		lotOrigin.setLotNo(lotNo);
		
		//END_DATE
		LocalDate today = LocalDate.now(); //2025-04-21
		lotOrigin.setEndDate(today);
		
		//FINISH_TIME
		LocalTime time = LocalTime.now();
		String finishTime = time.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
		lotOrigin.setFinishTime(finishTime);
		
		//WO_STATUS_NO
		lotOrigin.setWoStatusNo("CP"); //CP : 완료  ------------------------------------------ LOT 그리드 버튼 클릭시 변경 - 작업중일 땐 사용x
		
		//BOM_QTY
		lotOrigin.setBomQty(lotUpdateDTO.getBomQty());
		
		//WO_CODE
		lotOrigin.setWoCode(lotUpdateDTO.getWoCode());
		
		lotService.updateLotPresentPRC(lotOrigin);
		
		if(!lotUpdateDTO.getNum().equals("0")) { //공정의 끝인지 아닌지 파악
			
			//LOT_NO - 1 에 START_TIME 등록
			//LOT테이블의 다음 작업에 startTime 업데이트 => 전 공정의 finishTime이 이후 공정의 startTime
			Long nextLotNo = lotNo - 1;
			lotService.updateLotNextPRC(nextLotNo, finishTime);
			
		}else {
			//WORK_ORDER 테이블의 WO_STATUS_CODE도 CP로 업데이트 
			lotService.updateWOtoCP(lotUpdateDTO.getWoCode());
		}
		
		// ----------- IPI 테이블에 insert -----------
		//품질로 데이터 전송
		//IPI_INSPECTION_RESLT : 예정
		//IPI_INSPECTION_STATUS : 검사시작
		//PDT_CODE, PDT_NAME, WO_CODE, WO_QTY, LOT_NO = NULL, PDT_TYPE(반제품, 완제품)

		IpiDTO ipidto = new IpiDTO();
		// UNIT_QTY, WO_CODE, WO_QTY, PDT_TYPE)
		//IPI_NO 입력
		//Long ipiNo = lotService.getIpiNo();
		//ipidto.setIpiNo(ipiNo + 1);
		
		//LOT_NO
		ipidto.setLotNo(Long.valueOf(lotUpdateDTO.getLotNo()));
		
		//childLotCode
		ipidto.setChildLotCode(lotUpdateDTO.getChildLotCode());
		
		//InspectionResult
		//ipidto.setIpiInspectionResult("예정");
		
		//IpiInspectionStatus
		//ipidto.setIpiInspectionStatus("검사시작");
		
		//pdtCode
		ipidto.setPdtCode(lotUpdateDTO.getChildPdtCode());
		
		//pdtName 가져오기 위한 코드
		List<ProductTotalDTO> pdtTotalDto = productionPrcService.selectProductInfo(lotUpdateDTO.getPdtCode());
		ipidto.setPdtName(pdtTotalDto.get(0).getPdtName());
		
		//pdtType
		ipidto.setPdtType(pdtTotalDto.get(0).getPdtType());
		
		//wo_code
		ipidto.setWoCode(lotUpdateDTO.getWoCode());
		
		//wo_qty
		ipidto.setWoQty(lotUpdateDTO.getBomQty());
		
		ipidto.setIpiCode(generateIpiCode());
		
		//UPDATE
		lotService.insertIpi(ipidto);
		
		// ----------- grid를 다시 그려주기 위해서 새로 데이터를 읽어와서 넘겨준다 -----------
		List<LotOriginDTO> selectLotOrigin = lotService.selectLotOrigin(lotUpdateDTO.getWoCode());
		//log.info("/jobCmpl의 selectLotOrigin:" + selectLotOrigin);
		
		return selectLotOrigin;
	}
	
	// IPI 코드 생성
	private String generateIpiCode() {
		String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		int maxSeq = ipiMapper.getMaxIpiCodeSeq(datePart);
		int nextSeq = maxSeq + 1;
		String seqStr = String.format("%03d", nextSeq);
		return "IPI-" + datePart + "-" + seqStr;
	}
	
	//SAVE_PRC테이블에 데이터 입력(위에 jobCmpl 작업 실행 내부에서 연달아 실행)
	@PostMapping("/insertSavePrc")
	@ResponseBody
	public String insertSavePrc(@RequestBody SavePRCDTO savePrcDto) {
		
		//현재 상태 SAVE_PRC 테이블 입력
		lotService.insertSavePrc(savePrcDto);
		
		//log.info("savePrcDto:"+savePrcDto);	
		
		return "success";
	}
	
	//SAVE_PRC테이블에 데이터가 있는 경우 최초 로딩시 상단 그리드에 출력
	@PostMapping("/savePRCwoGrid")
	@ResponseBody
	public List<BomInfoDTO> savePRCwoGrid(@RequestParam("pdtCode") String pdtCode, Model model) {
		
		List<BomInfoDTO> selectPdtCodeList = productionPrcService.selectPdtCodeList(pdtCode);
		
		model.addAttribute("selectPdtCodeList", selectPdtCodeList); 
		
		return selectPdtCodeList;
	}
	
	//SAVE_PRC테이블에 데이터가 있는 경우 최초 로딩시 두번째 하단 그리드에 출력
	@PostMapping("/savePRCprcGrid")
	@ResponseBody
	public List<LotOriginDTO> savePRCprcGrid(@RequestParam("woCode") String woCode, Model model) {
		
		List<LotOriginDTO> selectLotOrigin = lotService.selectLotOrigin(woCode);
		
		model.addAttribute("selectLotOrigin", selectLotOrigin); 

		return selectLotOrigin;
	}
	
	//작업 현황 버튼을 눌렀을 때 받아올 rnRowNum의 max값
	@GetMapping("/getRnRowNumMax")
	@ResponseBody
	public Integer getRnRowNumMax(@RequestParam("woCode") String woCode, Model model) {
		
		Integer rnRowNumMax = lotService.selectRnRowNumMax(woCode);
		
		//log.info("/getRnRowNumMax의 rnRowNumMax:" + rnRowNumMax);
		
		return rnRowNumMax;
	}
	
	
	
	//작업 완료 버튼 클릭
	@PostMapping("/deleteSavePrc")
	@ResponseBody
	public String deleteSavePrc(@RequestParam("woCode") String woCode) {
		
		//saveprc의 모든 데이터 삭제
		lotService.deleteSavePrc();
		
		//작업지시서 완료 날짜 업데이트
		LocalDate today = LocalDate.now(); //2025-04-21
		lotService.updateWoEndDate(woCode ,String.valueOf(today));
		
		//MFG 상태 업데이트
		String mfgscd = "완료";
		String mfgPlan = "완료";
		lotService.updateMfgScdStatus(woCode, mfgscd);
		lotService.updateMfgPlanStatus(woCode, mfgPlan);
		
		
		//IPI로 완제품 정보 넘기기 - 현재 작업 한거까지의 데이터는 품질로 바로 전송된 상태, 완제품 정보 넘길 필요x
		
		return "success";
	}
	
}


