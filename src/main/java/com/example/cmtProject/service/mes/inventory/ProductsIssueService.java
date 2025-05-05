package com.example.cmtProject.service.mes.inventory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.example.cmtProject.mapper.mes.inventory.ProductsInventoryMapper;
import com.example.cmtProject.mapper.mes.inventory.ProductsIssueHistoryMapper;
import com.example.cmtProject.mapper.mes.inventory.ProductsIssueMapper;
import com.example.cmtProject.mapper.mes.inventory.ProductsIssueStockMapper;
import com.example.cmtProject.mapper.mes.inventory.ProductsMasterMapper;
import com.example.cmtProject.service.mes.qualityControl.FqcService;
import com.example.cmtProject.util.SecurityUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 제품 출고 처리 서비스
 * 제품 출고 조회 및 처리 관련 비즈니스 로직을 제공합니다.
 */
@Service
@Slf4j
public class ProductsIssueService {
	
	@Autowired
	private ProductsIssueMapper pImapper;

	@Autowired
	private ProductsIssueHistoryMapper pIhmapper;
	
	@Autowired
	private ProductsInventoryMapper pInvMapper;
	
	@Autowired
	private ProductsIssueStockMapper pIsmapper;
	
	@Autowired
	private ProductsMasterMapper pmmapper;
	
	@Autowired
	private FqcService fqcService;
	
	// 출고 상태 상수 정의
	private static final String STATUS_WAITING = "출고대기";
	private static final String STATUS_INSPECTING = "검수중";
	private static final String STATUS_COMPLETED = "출고완료";
	private static final String STATUS_CANCELED = "취소";
	private static final String STATUS_INSPECT_PASSED = "검사 합격";
	private static final String STATUS_INSPECT_FAILED = "검사 불합격";
	
	/**
	 * 출고 목록 조회
	 * 
	 * @param map 검색 조건
	 * @return 출고 목록
	 */
	public List<Map<String, Object>> issueList(Map<String, Object> map) {
		log.info("출고 목록 조회 요청");
		return pImapper.pIssueList(map);
	}
	
	/**
	 * 출고 가능한 수주 목록 조회
	 * 
	 * @param map 검색 조건
	 * @return 수주 목록
	 */
	public List<Map<String, Object>> getSalesOrderList(Map<String, Object> map) {
	    log.info("출고 가능한 수주 목록 조회 요청");
	    return pImapper.salesOrderList(map);
	}
	
	/**
	 * 수주 정보를 바탕으로 출고 요청 생성
	 * 
	 * @param soData 수주 데이터
	 * @return 처리 결과
	 */
	@Transactional
	public Map<String, Object> createIssueRequestFromSalesOrder(Map<String, Object> soData) {
	    Map<String, Object> resultMap = new HashMap<>();
	    
	    try {
	        // 필수 파라미터 검증
	        if (soData == null || !soData.containsKey("SO_CODE") || !soData.containsKey("PDT_CODE") 
	                || !soData.containsKey("SO_QTY")) {
	            resultMap.put("success", false);
	            resultMap.put("message", "필수 파라미터가 누락되었습니다. (수주코드, 제품코드, 수량)");
	            return resultMap;
	        }
	        
	        // 현재 날짜 정보
	        LocalDate now = LocalDate.now();
	        String nowStr = now.toString();
	        
	        // 현재 사용자 ID 가져오기
	        String userId = SecurityUtil.getUserId();
	        
	        // 제품코드로 기준정보 조회
	        String pdtCode = (String) soData.get("PDT_CODE");
	        Map<String, Object> param = new HashMap<>();
	        param.put("PDT_CODE", pdtCode);
	        
	        Map<String, Object> productInfo = pmmapper.selectSingleProducts(param);
	        
	        // 창고/위치 정보 결정 - 기준정보의 값을 그대로 사용
	        String warehouseCode = null;
	        String locationCode = null;
	        
	        // 기준정보에서 창고/위치 코드 가져오기
	        if (productInfo != null) {
	            warehouseCode = (String) productInfo.get("DEFAULT_WAREHOUSE_CODE");
	            locationCode = (String) productInfo.get("DEFAULT_LOCATION_CODE");
	        }
	        
	        // 출고 정보 맵 생성
	        Map<String, Object> issueMap = new HashMap<>();
	        
	        // 기본 정보 설정
	        issueMap.put("issueCode", "IS" + System.currentTimeMillis() % 10000);
	        issueMap.put("pdtCode", soData.get("PDT_CODE"));
	        issueMap.put("requestQty", soData.get("SO_QTY"));
	        issueMap.put("issuedQty", "0"); // 초기 출고 수량은 0
	        issueMap.put("lotNo", "LOT-" + nowStr.replace("-", "") + "-" + soData.get("SO_CODE"));
	        issueMap.put("requestDate", nowStr);
	        issueMap.put("issueDate", null); // 출고일은 아직 미정
	        issueMap.put("issueStatus", "출고대기");
	        issueMap.put("warehouseCode", warehouseCode);
	        issueMap.put("locationCode", locationCode);  
	        issueMap.put("issuer", userId);
	        issueMap.put("createdBy", userId);
	        issueMap.put("updatedBy", userId);
	        issueMap.put("createdDate", nowStr);
	        issueMap.put("updatedDate", nowStr);
	        // 수주코드 추가
	        issueMap.put("soCode", soData.get("SO_CODE"));
	        
	        // 출고 정보 저장
	        int result = pImapper.insertProductsIssue(issueMap);
	        
	        if (result > 0) {
	            // 출고 번호 조회
	            Long issueNo = pImapper.getLastIssueNo();
	            
	            // 출고 이력 저장
	            Map<String, Object> historyMap = new HashMap<>();
	            historyMap.put("issueNo", issueNo);
	            historyMap.put("actionType", "출고요청");
	            historyMap.put("actionDescription", "수주번호 " + soData.get("SO_CODE") + "의 출고 요청 등록");
	            historyMap.put("actionUser", userId);
	            historyMap.put("createdBy", userId);
	            
	            pIhmapper.insertHistory(historyMap);
	            
	            resultMap.put("success", true);
	            resultMap.put("message", "출고 요청이 등록되었습니다.");
	            resultMap.put("issueNo", issueNo);
	        } else {
	            resultMap.put("success", false);
	            resultMap.put("message", "출고 요청 등록에 실패했습니다.");
	        }
	        
	    } catch (Exception e) {
	        resultMap.put("success", false);
	        resultMap.put("message", "오류가 발생했습니다: " + e.getMessage());
	        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
	    }
	    
	    return resultMap;
	}
	
	/**
	 * 여러 수주 정보를 바탕으로 출고 요청 일괄 생성
	 * 
	 * @param salesOrders 수주 데이터 목록
	 * @return 처리 결과
	 */
	@Transactional
	public Map<String, Object> createIssueRequestsBatch(List<Map<String, Object>> salesOrders) {
	    Map<String, Object> resultMap = new HashMap<>();
	    int successCount = 0;
	    List<String> failedItems = new ArrayList<>();
	    
	    try {
	        log.info("일괄 출고 요청 생성 시작: {} 건", salesOrders.size());
	        
	        for (Map<String, Object> soData : salesOrders) {
	            try {
	                Map<String, Object> result = createIssueRequestFromSalesOrder(soData);
	                
	                if ((Boolean) result.get("success")) {
	                    successCount++;
	                } else {
	                    failedItems.add(String.valueOf(soData.get("SO_CODE")));
	                }
	            } catch (Exception e) {
	                log.error("개별 출고 요청 생성 중 오류: {}", e.getMessage(), e);
	                failedItems.add(String.valueOf(soData.get("SO_CODE")));
	            }
	        }
	        
	        if (successCount > 0) {
	            String message = successCount + "개 항목의 출고 요청이 생성되었습니다.";
	            if (!failedItems.isEmpty()) {
	                message += String.format(" (%d개 항목 실패)", failedItems.size());
	            }
	            
	            resultMap.put("success", true);
	            resultMap.put("message", message);
	            resultMap.put("successCount", successCount);
	            resultMap.put("failedCount", failedItems.size());
	            resultMap.put("failedItems", failedItems);
	            
	            log.info("일괄 출고 요청 생성 완료: 성공={}, 실패={}", successCount, failedItems.size());
	        } else {
	            resultMap.put("success", false);
	            resultMap.put("message", "모든 출고 요청 생성에 실패했습니다.");
	            resultMap.put("failedItems", failedItems);
	            
	            log.warn("일괄 출고 요청 생성 실패: 모든 항목 실패");
	        }
	        
	    } catch (Exception e) {
	        log.error("일괄 출고 요청 생성 중 오류 발생: {}", e.getMessage(), e);
	        resultMap.put("success", false);
	        resultMap.put("message", "오류가 발생했습니다: " + e.getMessage());
	        resultMap.put("failedItems", failedItems);
	        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
	    }
	    
	    return resultMap;
	}
	
	/**
	 * 출고 상세 정보 조회
	 * 
	 * @param issueNo 출고 번호
	 * @return 출고 상세 정보
	 */
	public Map<String, Object> getIssueDetail(Long issueNo) {
	    log.info("출고 상세 정보 조회 요청: 출고번호={}", issueNo);
	    
	    Map<String, Object> resultMap = new HashMap<>();
	    
	    try {
	        // 기본 출고 정보 조회
	        Map<String, Object> issueDetail = pImapper.getIssueDetail(issueNo);
	        
	        if (issueDetail == null) {
	            resultMap.put("success", false);
	            resultMap.put("message", "해당 출고 정보를 찾을 수 없습니다.");
	            return resultMap;
	        }
	        
	        // 조회 결과 병합
	        resultMap.putAll(issueDetail);
	        
	        // 이력 정보 조회
	        List<Map<String, Object>> historyList = pIhmapper.getHistoryByIssueNo(issueNo);
	        resultMap.put("historyList", historyList != null ? historyList : new ArrayList<>());
	        
	        resultMap.put("success", true);
	        
	        log.info("출고 상세 정보 조회 성공: 출고코드={}, 상태={}",
		            issueDetail.get("ISSUE_CODE"), issueDetail.get("ISSUE_STATUS"));
		        
		    } catch (Exception e) {
		        log.error("출고 상세 정보 조회 중 오류 발생: {}", e.getMessage(), e);
		        resultMap.put("success", false);
		        resultMap.put("message", "상세 정보 조회 중 오류가 발생했습니다: " + e.getMessage());
		    }
		    
		    return resultMap;
		}
		
		/**
		 * 출고 이력 정보 조회
		 * 
		 * @param issueNo 출고 번호
		 * @return 이력 정보 목록
		 */
		public List<Map<String, Object>> getIssueHistory(Long issueNo) {
		    log.info("출고 이력 정보 조회 서비스 호출. 출고번호: {}", issueNo);
		    
		    List<Map<String, Object>> historyList = pIhmapper.getHistoryByIssueNo(issueNo);
		    
		    if (historyList == null) {
		        return new ArrayList<>();
		    }
		    
		    return historyList;
		}
		
		/**
		 * 검수 요청 처리 - 출고 상태를 검수중으로 변경
		 * 
		 * @param params 검수 요청 정보
		 * @return 처리 결과
		 */
		@Transactional
		public Map<String, Object> requestInspection(Map<String, Object> params) {
		    Map<String, Object> resultMap = new HashMap<>();
		    
		    try {
		        log.info("검수 등록 처리 시작: {}", params);
		        
		        Long issueNo = null;
		        if (params.get("issueNo") instanceof Long) {
		            issueNo = (Long) params.get("issueNo");
		        } else if (params.get("issueNo") instanceof String) {
		            issueNo = Long.parseLong((String) params.get("issueNo"));
		        } else if (params.get("issueNo") instanceof Integer) {
		            issueNo = ((Integer) params.get("issueNo")).longValue();
		        }
		        
		        if (issueNo == null) {
		            resultMap.put("success", false);
		            resultMap.put("message", "출고 번호가 유효하지 않습니다.");
		            return resultMap;
		        }
		        
		        // 출고 정보 조회 - 검수 처리 가능 상태 확인
		        Map<String, Object> issueDetail = pImapper.getIssueDetail(issueNo);
		        if (issueDetail == null) {
		            resultMap.put("success", false);
		            resultMap.put("message", "해당 출고 정보를 찾을 수 없습니다.");
		            return resultMap;
		        }
		        
		        // 상태 확인 - 출고대기 상태만 검수 처리 가능
		        String currentStatus = (String) issueDetail.get("ISSUE_STATUS");
		        if (!STATUS_WAITING.equals(currentStatus)) {
		            resultMap.put("success", false);
		            resultMap.put("message", "출고대기 상태인 항목만 검수 처리가 가능합니다. 현재 상태: " + currentStatus);
		            return resultMap;
		        }
		        
		        // 현재 날짜
		        LocalDate today = LocalDate.now();
		        String todayStr = today.toString();
		        
		        // 현재 사용자 ID 가져오기
		        String userId = SecurityUtil.getUserId();
		        
		        // 출고 상태 업데이트
		        Map<String, Object> updateMap = new HashMap<>();
		        updateMap.put("issueNo", issueNo);
		        updateMap.put("issueStatus", STATUS_INSPECTING);
		        updateMap.put("updatedBy", userId);
		        updateMap.put("woCode", params.get("issueCode"));
		        updateMap.put("pdtCode", params.get("pdtCode"));
		        
		        
		        int result = pImapper.updateIssueStatus(updateMap);
		        
		        if (result > 0) {
		            // 이력 기록
		            Map<String, Object> historyMap = new HashMap<>();
		            historyMap.put("issueNo", issueNo);
		            historyMap.put("actionType", "검수시작");
		            historyMap.put("actionDescription", "검수 등록됨");
		            historyMap.put("actionUser", userId);
		            historyMap.put("createdBy", userId);
		            
		            pIhmapper.insertHistory(historyMap);
		            log.info("444444444444444444444444444444"+updateMap);
		            // FQC 검수 등록 호출 - 나중에 구현
		            // TODO: FQC 서비스를 통한 검수 등록 처리
		            fqcService.insertFqcInspection(updateMap);
		            
		            log.info("검수 등록 처리 완료: 출고번호={}", issueNo);
		            
		            resultMap.put("success", true);
		            resultMap.put("message", "검수 등록이 완료되었습니다.");
		        } else {
		            log.warn("검수 등록 처리 실패: 출고번호={}", issueNo);
		            resultMap.put("success", false);
		            resultMap.put("message", "검수 등록 처리가 실패했습니다.");
		        }
		    } catch (Exception e) {
		        log.error("검수 등록 처리 중 오류 발생: {}", e.getMessage(), e);
		        resultMap.put("success", false);
		        resultMap.put("message", "오류가 발생했습니다: " + e.getMessage());
		        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		    }
		    
		    return resultMap;
		}
		
		/**
		 * 다건 검수 등록 처리 
		 * 다수의 출고 항목에 대해 검수 등록을 처리합니다.
		 * 
		 * @param params 검수 요청 정보 (items: 검수 등록할 항목 목록)
		 * @return 처리 결과
		 */
		@Transactional
		public Map<String, Object> registerInspectionMultiple(Map<String, Object> params) {
		    Map<String, Object> resultMap = new HashMap<>();
		    int successCount = 0;
		    List<String> failedItems = new ArrayList<>();
		    
		    try {
		        log.info("다건 검수 등록 처리 시작: {}", params);
		        
		        // 다건 처리 지원
		        @SuppressWarnings("unchecked")
		        List<Map<String, Object>> items = (List<Map<String, Object>>) params.get("items");
		        
		        if (items == null || items.isEmpty()) {
		            resultMap.put("success", false);
		            resultMap.put("message", "검수 등록 처리할 항목이 없습니다.");
		            return resultMap;
		        }
		        
		        // 각 항목에 대해 처리
		        for (Map<String, Object> item : items) {
		            try {
		                // 개별 항목 처리
		                Map<String, Object> itemResult = requestInspection(item);
		                
		                if ((Boolean) itemResult.get("success")) {
		                    successCount++;
		                } else {
		                    failedItems.add(String.valueOf(item.get("issueCode")));
		                }
		            } catch (Exception e) {
		                log.error("개별 검수 등록 처리 중 오류: {}", e.getMessage(), e);
		                failedItems.add(String.valueOf(item.get("issueCode")));
		            }
		        }
		        
		        // 결과 집계
		        resultMap.put("totalCount", items.size());
		        resultMap.put("successCount", successCount);
		        resultMap.put("failedCount", failedItems.size());
		        resultMap.put("failedItems", failedItems);
		        
		        if (successCount > 0) {
		            String message = String.format("%d개 항목의 검수 등록이 완료되었습니다.", successCount);
		            if (!failedItems.isEmpty()) {
		                message += String.format(" (%d개 항목 실패)", failedItems.size());
		            }
		            
		            resultMap.put("success", true);
		            resultMap.put("message", message);
		            
		            log.info("다건 검수 등록 처리 완료: {}", message);
		        } else {
		            resultMap.put("success", false);
		            resultMap.put("message", "검수 등록 처리에 실패했습니다.");
		            
		            log.warn("다건 검수 등록 처리 실패: 모든 항목 처리 실패");
		        }
		        
		    } catch (Exception e) {
		        log.error("다건 검수 등록 처리 중 오류 발생: {}", e.getMessage(), e);
		        resultMap.put("success", false);
		        resultMap.put("message", "오류가 발생했습니다: " + e.getMessage());
		        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		    }
		    
		    return resultMap;
		}
		
		/**
		 * 출고 처리 - 재고에서 출고 처리하고 이력 기록
		 * 
		 * @param params 출고 처리 정보
		 * @return 처리 결과
		 */
		@Transactional
		public Map<String, Object> processIssue(Map<String, Object> params) {
		    Map<String, Object> resultMap = new HashMap<>();
		    
		    try {
		        log.info("출고 처리 시작: {}", params);
		        
		        // 출고 번호 검증
		        Long issueNo = null;
		        if (params.get("issueNo") instanceof Long) {
		            issueNo = (Long) params.get("issueNo");
		        } else if (params.get("issueNo") instanceof String) {
		            issueNo = Long.parseLong((String) params.get("issueNo"));
		        } else if (params.get("issueNo") instanceof Integer) {
		            issueNo = ((Integer) params.get("issueNo")).longValue();
		        }
		        
		        if (issueNo == null) {
		            resultMap.put("success", false);
		            resultMap.put("message", "출고 번호가 유효하지 않습니다.");
		            return resultMap;
		        }
		        
		        // 출고 정보 조회
		        Map<String, Object> issueDetail = pImapper.getIssueDetail(issueNo);
		        
		        if (issueDetail == null) {
		            resultMap.put("success", false);
		            resultMap.put("message", "해당 출고 정보를 찾을 수 없습니다.");
		            return resultMap;
		        }
		        
		        // 출고 상태 확인
		        String issueStatus = (String) issueDetail.get("ISSUE_STATUS");
		        if (STATUS_COMPLETED.equals(issueStatus) || STATUS_CANCELED.equals(issueStatus)) {
		            resultMap.put("success", false);
		            resultMap.put("message", "이미 처리된 출고입니다: " + issueStatus);
		            return resultMap;
		        }
		        
		        // 검수완료(합격) 상태인지 확인 - 검수 합격만 출고 처리 가능
		        if (!STATUS_INSPECT_PASSED.equals(issueStatus) && !STATUS_WAITING.equals(issueStatus)) {
		            resultMap.put("success", false);
		            resultMap.put("message", "검수 완료된 항목(합격)만 출고 처리할 수 있습니다. 현재 상태: " + issueStatus);
		            return resultMap;
		        }
		        
		        // 현재 날짜
		        LocalDate now = LocalDate.now();
		        String nowStr = now.toString();
		        
		        // 현재 사용자 ID 가져오기
		        String userId = SecurityUtil.getUserId();
		        
		        // 출고 수량 설정 (요청 수량 전체 출고)
		        String issuedQty = (String) issueDetail.get("REQUEST_QTY");
		        
		        // 1. 출고 상태 및 출고일 업데이트
		        Map<String, Object> updateMap = new HashMap<>();
		        updateMap.put("issueNo", issueNo);
		        updateMap.put("issueStatus", STATUS_COMPLETED);
		        updateMap.put("issuedQty", issuedQty); // 출고 수량 설정
		        updateMap.put("issueDate", nowStr);
		        updateMap.put("updatedBy", userId);
		        
		        int updateResult = pImapper.updateIssueStatusAndDate(updateMap);
		        
		        if (updateResult <= 0) {
		            resultMap.put("success", false);
		            resultMap.put("message", "출고 상태 업데이트에 실패했습니다.");
		            return resultMap;
		        }
		        
		        // 2. 재고 처리
		        String pdtCode = (String) issueDetail.get("PDT_CODE");
		        Map<String, Object> inventoryParams = new HashMap<>();
		        inventoryParams.put("pdtCode", pdtCode);
		        inventoryParams.put("consumptionQty", issuedQty);
		        inventoryParams.put("updatedBy", userId);
		        
		        // 재고 차감
		        int deductResult = pInvMapper.deductInventory(inventoryParams);
		        
		        if (deductResult <= 0) {
		            resultMap.put("success", false);
		            resultMap.put("message", "재고 차감에 실패했습니다. 재고가 충분한지 확인하세요.");
		            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		            return resultMap;
		        }
		        
		        // 3. 출고 재고 정보 저장
		        Map<String, Object> stockParams = new HashMap<>();
		        stockParams.put("issueNo", issueNo);
		        stockParams.put("pdtCode", pdtCode);
		        stockParams.put("issuedQty", issuedQty);
		        stockParams.put("issueDate", nowStr);
		        stockParams.put("lotNo", issueDetail.get("LOT_NO"));
		        stockParams.put("createdBy", userId);
		        
		        pIsmapper.insertStock(stockParams);
		        
		        // 4. 출고 이력 저장
		        Map<String, Object> historyMap = new HashMap<>();
		        historyMap.put("issueNo", issueNo);
		        historyMap.put("actionType", "출고완료");
		        historyMap.put("actionDescription", issuedQty + " 수량 출고 처리됨");
		        historyMap.put("actionUser", userId);
		        historyMap.put("createdBy", userId);
		        
		        pIhmapper.insertHistory(historyMap);
		        
		        // 5. 수주 상태 업데이트 - 출고완료로 변경
		        // 수주코드를 알고 있다면 수주 상태 업데이트 처리
		        if (params.containsKey("soCode") && params.get("soCode") != null) {
		            try {
		                // 수주상태 업데이트 처리
		                Map<String, Object> soParams = new HashMap<>();
		                soParams.put("soCode", params.get("soCode"));
		                soParams.put("soStatus", "SO_SHIPPED"); // 출고완료 상태코드
		                soParams.put("updatedBy", userId);
		                
		                pImapper.updateSalesOrderStatus(soParams);
		                
		                log.info("수주 상태 업데이트: 코드={}, 상태=SO_SHIPPED", params.get("soCode"));
		            } catch (Exception e) {
		                log.warn("수주 상태 업데이트 중 오류 발생: {}", e.getMessage());
		                // 수주 상태 업데이트 실패해도 출고 자체는 완료 처리
		            }
		        }
		        
		        resultMap.put("success", true);
		        resultMap.put("message", "출고 처리가 완료되었습니다.");
		        resultMap.put("issuedQty", issuedQty);
		        
		        log.info("출고 처리 완료: 출고번호={}, 제품코드={}, 수량={}", 
		            issueNo, pdtCode, issuedQty);
		        
		    } catch (Exception e) {
		        log.error("출고 처리 중 오류 발생: {}", e.getMessage(), e);
		        resultMap.put("success", false);
		        resultMap.put("message", "오류가 발생했습니다: " + e.getMessage());
		        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		    }
		    
		    return resultMap;
		}

		/**
		 * 다건 출고 처리
		 * 여러 출고 항목을 한 번에 처리합니다.
		 * 
		 * @param params 출고 처리 정보 (items: 처리할 항목 목록)
		 * @return 처리 결과
		 */
		@Transactional
		public Map<String, Object> processIssueMultiple(Map<String, Object> params) {
		    Map<String, Object> resultMap = new HashMap<>();
		    int successCount = 0;
		    List<String> failedItems = new ArrayList<>();
		    
		    try {
		        log.info("다건 출고 처리 시작: {}", params);
		        
		        // 다건 처리 항목 검증
		        @SuppressWarnings("unchecked")
		        List<Map<String, Object>> items = (List<Map<String, Object>>) params.get("items");
		        
		        if (items == null || items.isEmpty()) {
		            resultMap.put("success", false);
		            resultMap.put("message", "출고 처리할 항목이 없습니다.");
		            return resultMap;
		        }
		        
		        // 각 항목에 대해 처리
		        for (Map<String, Object> item : items) {
		            try {
		                // 개별 항목 처리
		                Map<String, Object> itemResult = processIssue(item);
		                
		                if ((Boolean) itemResult.get("success")) {
		                    successCount++;
		                } else {
		                    failedItems.add(String.valueOf(item.get("issueNo")));
		                }
		            } catch (Exception e) {
		                log.error("개별 출고 처리 중 오류: {}", e.getMessage(), e);
		                failedItems.add(String.valueOf(item.get("issueNo")));
		            }
		        }
		        
		        // 결과 집계
		        if (successCount > 0) {
		            String message = successCount + "개 항목의 출고 처리가 완료되었습니다.";
		            if (!failedItems.isEmpty()) {
		                message += String.format(" (%d개 항목 실패)", failedItems.size());
		            }
		            
		            resultMap.put("success", true);
		            resultMap.put("message", message);
		            resultMap.put("successCount", successCount);
		            resultMap.put("failedCount", failedItems.size());
		            resultMap.put("failedItems", failedItems);
		            
		            log.info("다건 출고 처리 완료: 성공={}, 실패={}", successCount, failedItems.size());
		        } else {
		            resultMap.put("success", false);
		            resultMap.put("message", "모든 출고 처리에 실패했습니다.");
		            resultMap.put("failedItems", failedItems);
		            
		            log.warn("다건 출고 처리 실패: 모든 항목 실패");
		        }
		        
		    } catch (Exception e) {
		        log.error("다건 출고 처리 중 오류 발생: {}", e.getMessage(), e);
		        resultMap.put("success", false);
		        resultMap.put("message", "오류가 발생했습니다: " + e.getMessage());
		        resultMap.put("failedItems", failedItems);
		        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		    }
		    
		    return resultMap;
		}

		/**
		 * 출고 취소 처리
		 * 
		 * @param params 취소 처리 정보
		 * @return 처리 결과
		 */
		@Transactional
		public Map<String, Object> cancelIssue(Map<String, Object> params) {
		    Map<String, Object> resultMap = new HashMap<>();
		    
		    try {
		        log.info("출고 취소 처리 시작: {}", params);
		        
		        // 출고 번호 검증
		        Long issueNo = null;
		        if (params.get("issueNo") instanceof Long) {
		            issueNo = (Long) params.get("issueNo");
		        } else if (params.get("issueNo") instanceof String) {
		            issueNo = Long.parseLong((String) params.get("issueNo"));
		        } else if (params.get("issueNo") instanceof Integer) {
		            issueNo = ((Integer) params.get("issueNo")).longValue();
		        }
		        
		        if (issueNo == null) {
		            resultMap.put("success", false);
		            resultMap.put("message", "출고 번호가 유효하지 않습니다.");
		            return resultMap;
		        }
		        
		        // 출고 정보 조회
		        Map<String, Object> issueDetail = pImapper.getIssueDetail(issueNo);
		        
		        if (issueDetail == null) {
		            resultMap.put("success", false);
		            resultMap.put("message", "해당 출고 정보를 찾을 수 없습니다.");
		            return resultMap;
		        }
		        
		        // 출고 상태 확인 - 출고대기 상태만 취소 가능
		        String issueStatus = (String) issueDetail.get("ISSUE_STATUS");
		        if (!STATUS_WAITING.equals(issueStatus)) {
		            resultMap.put("success", false);
		            resultMap.put("message", "출고대기 상태의 항목만 취소할 수 있습니다. 현재 상태: " + issueStatus);
		            return resultMap;
		        }
		        
		        // 현재 사용자 ID 가져오기
		        String userId = SecurityUtil.getUserId();
		        
		        // 출고 상태 업데이트
		        Map<String, Object> updateMap = new HashMap<>();
		        updateMap.put("issueNo", issueNo);
		        updateMap.put("issueStatus", STATUS_CANCELED);
		        updateMap.put("updatedBy", userId);
		        
		        int result = pImapper.updateIssueStatus(updateMap);
		        
		        if (result > 0) {
		            // 이력 기록
		            Map<String, Object> historyMap = new HashMap<>();
		            historyMap.put("issueNo", issueNo);
		            historyMap.put("actionType", "출고취소");
		            historyMap.put("actionDescription", "출고 요청이 취소되었습니다.");
		            historyMap.put("actionUser", userId);
		            historyMap.put("createdBy", userId);
		            
		            pIhmapper.insertHistory(historyMap);
		            
		            resultMap.put("success", true);
		            resultMap.put("message", "출고 요청이 취소되었습니다.");
		            
		            log.info("출고 취소 처리 완료: 출고번호={}", issueNo);
		        } else {
		            resultMap.put("success", false);
		            resultMap.put("message", "출고 취소 처리에 실패했습니다.");
		            
		            log.warn("출고 취소 처리 실패: 출고번호={}", issueNo);
		        }
		        
		    } catch (Exception e) {
		        log.error("출고 취소 처리 중 오류 발생: {}", e.getMessage(), e);
		        resultMap.put("success", false);
		        resultMap.put("message", "오류가 발생했습니다: " + e.getMessage());
		        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		    }
		    
		    return resultMap;
		}

		/**
		 * 수주 상태별 출고 요청 생성
		 * 특정 상태의 수주 정보를 바탕으로 출고 요청을 생성합니다.
		 * 
		 * @param params 검색 조건 (status: 수주 상태)
		 * @return 처리 결과
		 */
		@Transactional
		public Map<String, Object> createIssueRequestsByStatus(Map<String, Object> params) {
		    Map<String, Object> resultMap = new HashMap<>();
		    
		    try {
		        log.info("수주 상태별 출고 요청 생성 시작: {}", params);
		        
		        // 수주 상태 검증
		        String status = (String) params.get("status");
		        if (status == null || status.isEmpty()) {
		            resultMap.put("success", false);
		            resultMap.put("message", "수주 상태가 지정되지 않았습니다.");
		            return resultMap;
		        }
		        
		        // 허용된 상태 확인 (수주확정, 출하계획 등만 처리 가능)
		        List<String> allowedStatus = List.of("SO_CONFIRMED", "SO_PLANNED", "SO_COMPLETED");
		        boolean validStatus = false;
		        
		        for (String allowedState : allowedStatus) {
		            if (status.contains(allowedState)) {
		                validStatus = true;
		                break;
		            }
		        }
		        
		        if (!validStatus) {
		            resultMap.put("success", false);
		            resultMap.put("message", "지원되지 않는 수주 상태입니다. 확정, 계획, 완료 상태만 출고 요청이 가능합니다.");
		            return resultMap;
		        }
		        
		        // 수주 정보 조회
		        Map<String, Object> queryParams = new HashMap<>();
		        queryParams.put("status", status);
		        List<Map<String, Object>> salesOrders = pImapper.salesOrdersByStatus(queryParams);
		        
		        if (salesOrders.isEmpty()) {
		            resultMap.put("success", false);
		            resultMap.put("message", "해당 상태의 수주 정보가 없습니다: " + status);
		            return resultMap;
		        }
		        
		        // 출고 요청 일괄 생성
		        return createIssueRequestsBatch(salesOrders);
		    } catch (Exception e) {
		        log.error("수주 상태별 출고 요청 생성 중 오류 발생: {}", e.getMessage(), e);
		        resultMap.put("success", false);
		        resultMap.put("message", "오류가 발생했습니다: " + e.getMessage());
		        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		    }
		    
		    return resultMap;
		}
	}