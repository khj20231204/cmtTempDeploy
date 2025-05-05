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

import com.example.cmtProject.constants.MesStatusConstants;
import com.example.cmtProject.mapper.mes.inventory.MaterialInventoryMapper;
import com.example.cmtProject.mapper.mes.inventory.MaterialMasterMapper;
import com.example.cmtProject.mapper.mes.inventory.MaterialReceiptHistoryMapper;
import com.example.cmtProject.mapper.mes.inventory.MaterialReceiptMapper;
import com.example.cmtProject.mapper.mes.inventory.MaterialReceiptStockMapper;
import com.example.cmtProject.service.mes.qualityControl.IqcService;
import com.example.cmtProject.util.SecurityUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 원자재 입고 처리 서비스
 * 원자재 입고 조회 및 처리 관련 비즈니스 로직을 제공합니다.
 */
@Service
@Slf4j
public class MaterialReceiptService {
	
	@Autowired
	private MaterialReceiptMapper mRmapper;

	@Autowired
	private MaterialReceiptHistoryMapper mRhmapper;
	
	@Autowired
	private MaterialInventoryMapper mImapper;
	
	@Autowired
	private MaterialReceiptStockMapper mRsmapper;
	
	@Autowired
	private MaterialMasterMapper mmmapper;
	
	@Autowired
	private IqcService iqcService;
	
	/**
	 * 입고 목록 조회
	 * 
	 * @param map 검색 조건
	 * @return 입고 목록
	 */
	public List<Map<String, Object>> receiptList(Map<String,Object> map){
		log.info("입고 목록 조회 요청");
		return mRmapper.mReceiptList(map);
	}
	
	/**
	 * 미입고 상태인 발주 목록 조회
	 * 
	 * @param map 검색 조건
	 * @return 발주 목록
	 */
	public List<Map<String, Object>> puchasesList(Map<String,Object> map){
		log.info("미입고 발주 목록 조회 요청");
		return mRmapper.puchasesList(map);
	}

	/**
	 * 선택된 발주 정보를 기반으로 입고대기 등록
	 * 
	 * @param params 선택된 발주 정보 목록을 포함하는 파라미터
	 * @return 처리 결과
	 */
	@Transactional
	public Map<String, Object> createReceiptFromPurchaseOrder(Map<String, Object> params) {
	    Map<String, Object> resultMap = new HashMap<>();
	    int insertCount = 0;

	    try {
	        log.info("선택된 발주 정보 기반 입고 등록 시작");

	        // 선택된 발주 목록 가져오기
	        @SuppressWarnings("unchecked")
	        List<Map<String, Object>> selectedOrders = (List<Map<String, Object>>) params.get("items");
	        
	        if (selectedOrders == null || selectedOrders.isEmpty()) {
	            resultMap.put("success", false);
	            resultMap.put("message", "선택된 발주 정보가 없습니다.");
	            return resultMap;
	        }

	        log.info("선택된 발주 목록 수: {}", selectedOrders.size());

	        LocalDate now = LocalDate.now();
	        String nowStr = now.toString();

	        // 현재 사용자 ID 가져오기
	        String userId = SecurityUtil.getUserId();

	        for (Map<String, Object> po : selectedOrders) {
	            try {
	                // 자재 코드로 기준정보 조회
	                String mtlCode = (String) po.get("MTL_CODE");
	                
	                Map<String, Object> param = new HashMap<>();
	                param.put("MTL_CODE", mtlCode);
	                
	                Map<String, Object> materialInfo = mmmapper.selectSingleMaterials(param);
	                
	                // 디버깅 로그 추가
	                log.info("========= DEBUG 시작 =========");
	                log.info("발주 정보: {}", po);  // 발주 전체 정보 확인
	                log.info("자재코드: {}", mtlCode);
	                log.info("조회된 기준정보: {}", materialInfo);
	                
	                // 발주에서 직접 위치 정보가 오는지 확인
	                log.info("발주에서 가져온 창고코드: {}", po.get("WHS_CODE"));
	                log.info("발주에서 가져온 위치코드: {}", po.get("LOCATION_CODE"));
	                
	                // 창고/위치 정보 결정
	                String warehouseCode = null;
	                String locationCode = null;
	                
	                if (materialInfo != null) {
	                    warehouseCode = (String) materialInfo.get("DEFAULT_WAREHOUSE_CODE");
	                    locationCode = (String) materialInfo.get("DEFAULT_LOCATION_CODE");
	                    
	                    log.info("기준정보에서 가져온 창고코드: {}", warehouseCode);
	                    log.info("기준정보에서 가져온 위치코드: {}", locationCode);
	                } else {
	                    log.warn("자재 기준정보를 찾을 수 없음: {}", mtlCode);
	                }
	                
	                // 최종적으로 사용되는 값 확인
	                log.info("최종 저장할 창고코드: {}", warehouseCode);
	                log.info("최종 저장할 위치코드: {}", locationCode);
	                
	                // 입고 정보 맵 생성
	                Map<String, Object> receiptMap = new HashMap<>();
	                
	                receiptMap.put("receiptCode", "RC" + System.currentTimeMillis() % 10000);
	                receiptMap.put("poCode", po.get("PO_CODE"));
	                receiptMap.put("mtlCode", mtlCode);
	                receiptMap.put("receivedQty", po.get("PO_QTY"));
	                receiptMap.put("lotNo", "LOT-" + nowStr.replace("-", "") + "-" + insertCount);
	                receiptMap.put("receiptStatus", "입고대기");
	                receiptMap.put("warehouseCode", warehouseCode);
	                receiptMap.put("locationCode", locationCode);
	                receiptMap.put("receiver", userId);
	                receiptMap.put("createdBy", userId);
	                receiptMap.put("updatedBy", userId);
	                receiptMap.put("createdDate", nowStr);
	                receiptMap.put("updatedDate", nowStr);
	                
	                log.info("입고 정보 생성: {}", receiptMap);
	                log.info("========= DEBUG 끝 =========");
	                
	                // 입고 정보 저장
	                int result = mRmapper.insertMaterialReceipt(receiptMap);
	                
	                if (result > 0) {
	                    // 입고 번호 조회
	                    Long receiptNo = mRmapper.getLastReceiptNo();
	                    
	                    // 실제로 DB에 어떻게 저장되었는지 다시 조회해서 확인
	                    Map<String, Object> savedReceipt = mRmapper.getReceiptDetail(receiptNo);
	                    log.info("실제 저장된 데이터: {}", savedReceipt);
	                    log.info("저장 전: locationCode={}, 저장 후: locationCode={}", 
	                        receiptMap.get("locationCode"), savedReceipt.get("LOCATION_CODE"));
	                    
	                    // 입고 이력 저장
	                    Map<String, Object> historyMap = new HashMap<>();
	                    historyMap.put("receiptNo", receiptNo);
	                    historyMap.put("actionType", "입고등록");
	                    historyMap.put("actionDescription", "발주번호 " + po.get("PO_CODE") + "의 선택적 입고 등록");
	                    historyMap.put("actionUser", userId);
	                    historyMap.put("createdBy", userId);
	                    
	                    mRhmapper.insertHistory(historyMap);
	                    
	                    insertCount++;
	                    log.info("입고 정보 저장 성공: 입고번호={}", receiptNo);
	                } else {
	                    log.warn("입고 정보 저장 실패: {}", receiptMap);
	                }
	            } catch (Exception e) {
	                log.error("개별 입고 정보 처리 중 오류 발생: {}", e.getMessage(), e);
	            }
	        }
	        
	        resultMap.put("success", true);
	        resultMap.put("message", insertCount + "개의 발주 정보를 입고 대기 상태로 등록했습니다.");
	        resultMap.put("insertCount", insertCount);
	        
	        log.info("발주 정보 기반 입고 등록 완료: {}건", insertCount);
	        
	    } catch (Exception e) {
	        log.error("전체 입고 처리 중 오류 발생: {}", e.getMessage(), e);
	        resultMap.put("success", false);
	        resultMap.put("message", "오류가 발생했습니다: " + e.getMessage());
	        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
	    }
	    
	    return resultMap;
	}
	
	/**
	 * 입고 상세 정보 조회
	 * 
	 * @param receiptNo 입고 번호
	 * @return 입고 상세 정보
	 */
	public Map<String, Object> getReceiptDetail(Long receiptNo) {
	    log.info("입고 상세 정보 조회 요청: 입고번호={}", receiptNo);
	    
	    Map<String, Object> resultMap = new HashMap<>();
	    
	    try {
	        // 기본 입고 정보 조회
	        Map<String, Object> receiptDetail = mRmapper.getReceiptDetail(receiptNo);
	        
	        if (receiptDetail == null) {
	            resultMap.put("success", false);
	            resultMap.put("message", "해당 입고 정보를 찾을 수 없습니다.");
	            return resultMap;
	        }
	        
	        // 조회 결과 병합
	        resultMap.putAll(receiptDetail);
	        
	        // 이력 정보 조회
	        List<Map<String, Object>> historyList = mRhmapper.getHistoryByReceiptNo(receiptNo);
	        resultMap.put("historyList", historyList != null ? historyList : new ArrayList<>());
	        
	        // 검수 정보 조회 (있는 경우)

//	        Map<String, Object> inspectionData = mRmapper.getInspectionInfo(receiptNo);
//	        resultMap.put("hasInspection", inspectionData != null);
//	        if (inspectionData != null) {
//	            resultMap.put("inspectionData", inspectionData);
//	        }
	        
	        // LOT 정보 조회
//	        List<Map<String, Object>> lotData = mRmapper.getLotInfo(receiptNo);
//	        resultMap.put("lotData", lotData != null ? lotData : new ArrayList<>());
	        
	        // 위치 정보 조회
//	        List<Map<String, Object>> locationData = mRmapper.getLocationInfo(receiptNo);
//	        resultMap.put("locationData", locationData != null ? locationData : new ArrayList<>());
	        
	        resultMap.put("success", true);
	        
	        log.info("입고 상세 정보 조회 성공: 입고코드={}, 상태={}",
	            receiptDetail.get("RECEIPT_CODE"), receiptDetail.get("RECEIPT_STATUS"));
	        
	    } catch (Exception e) {
	        log.error("입고 상세 정보 조회 중 오류 발생: {}", e.getMessage(), e);
	        resultMap.put("success", false);
	        resultMap.put("message", "상세 정보 조회 중 오류가 발생했습니다: " + e.getMessage());
	    }
	    
	    return resultMap;
	}
	
	/**
	 * 입고 이력 정보 조회
	 * 
	 * @param receiptNo 입고 번호
	 * @return 이력 정보 목록
	 */
	public List<Map<String, Object>> getReceiptHistory(Long receiptNo) {
	    log.info("입고 이력 정보 조회 서비스 호출. 입고번호: {}", receiptNo);
	    
	    List<Map<String, Object>> historyList = mRhmapper.getHistoryByReceiptNo(receiptNo);
	    
	    if (historyList == null) {
	        return new ArrayList<>();
	    }
	    
	    return historyList;
	}
	
	/**
	 * 검수 정보 조회
	 * 
	 * @param receiptNo 입고 번호
	 * @return 검수 정보
	 */
	public Map<String, Object> getInspectionInfo(Long receiptNo) {
	    log.info("검수 정보 조회 서비스 호출. 입고번호: {}", receiptNo);
	    
	    Map<String, Object> inspectionInfo = mRmapper.getInspectionInfo(receiptNo);
	    
	    // 로그 추가: 검수 정보 확인
	    if (inspectionInfo != null) {
	        log.info("조회된 검수 정보: {}", inspectionInfo);
	    } else {
	        log.warn("입고번호 {}에 대한 검수 정보가 없습니다.", receiptNo);
	    }
	    
	    return inspectionInfo;
	}
	
	/**
	 * 입고 확정 처리
	 * 입고 확정 후 재고에 반영하고 FIFO 관리를 위한 입고별 재고 정보를 생성합니다.
	 * 
	 * @param params 입고 확정 처리할 항목 정보
	 * @return 처리 결과
	 */
	@Transactional
	public Map<String, Object> confirmReceipt(Map<String, Object> params) {
	    Map<String, Object> resultMap = new HashMap<>();
	    int updateCount = 0;
	    
	    try {
	        log.info("입고 확정 처리 시작: {}", params);
	        
	        // 확정 처리할 항목 목록
	        @SuppressWarnings("unchecked")
	        List<Map<String, Object>> items = (List<Map<String, Object>>) params.get("items");
	        
	        if (items == null || items.isEmpty()) {
	            resultMap.put("success", false);
	            resultMap.put("message", "확정 처리할 항목이 없습니다.");
	            return resultMap;
	        }
	        
	        // 위치 지정 입고 여부 확인
	        boolean withLocation = params.containsKey("withLocation") && (Boolean) params.get("withLocation");
	        
	        List<String> failedItems = new ArrayList<>();
	        
	        LocalDate now = LocalDate.now();
	        String nowStr = now.toString();
	        
	        // 사용자 ID 가져오기
	        String userId = SecurityUtil.getUserId();
	        
	        for (Map<String, Object> item : items) {
	            try {
	                // 입고 번호 추출
	                Long receiptNo = null;
	                if (item.get("receiptNo") instanceof Long) {
	                    receiptNo = (Long) item.get("receiptNo");
	                } else if (item.get("receiptNo") instanceof String) {
	                    receiptNo = Long.parseLong((String) item.get("receiptNo"));
	                } else if (item.get("receiptNo") instanceof Integer) {
	                    receiptNo = ((Integer) item.get("receiptNo")).longValue();
	                }
	                
	                if (receiptNo == null) {
	                    log.warn("입고 번호가 유효하지 않음: {}", item.get("receiptNo"));
	                    failedItems.add(String.valueOf(item.get("receiptCode")));
	                    continue;
	                }
	                
	                // 입고 정보 조회
	                Map<String, Object> receiptDetail = mRmapper.getReceiptDetail(receiptNo);
	                
	                if (receiptDetail == null) {
	                    log.warn("입고 정보를 찾을 수 없음: 입고번호={}", receiptNo);
	                    failedItems.add(String.valueOf(item.get("receiptCode")));
	                    continue;
	                }
	                
	                // 현재 상태 확인
	                String currentStatus = (String) receiptDetail.get("RECEIPT_STATUS");
	                
	                log.info("입고확정 검사 - 입고번호: {}, 현재상태: {}", receiptNo, currentStatus);
	                // 상태 또는 검수 결과 확인 - 주석 처리
	                /*
	                boolean isValidForConfirmation = false;
	                
	                // 1. 상태가 "검사 합격"인 경우 
	                if (MesStatusConstants.RECEIPT_STATUS_INSPECT_PASSED.equals(currentStatus)) {
	                    isValidForConfirmation = true;
	                    log.info("입고상태가 '검사 합격'이므로 확정 가능: 입고번호={}", receiptNo);
	                } 
	                // 2. 검수 정보 조회 및 결과 확인
	                else {
	                    Map<String, Object> inspInfo = getInspectionInfo(receiptNo);
	                    
	                    if (inspInfo != null && "합격".equals(inspInfo.get("INSP_RESULT"))) {
	                        isValidForConfirmation = true;
	                        log.info("검수결과가 '합격'이므로 확정 가능: 입고번호={}", receiptNo);
	                    } else {
	                        log.warn("검수 정보가 없거나 합격이 아님: 입고번호={}, 상태={}, 검수결과={}", 
	                            receiptNo, currentStatus, 
	                            (inspInfo != null ? inspInfo.get("INSP_RESULT") : "없음"));
	                    }
	                }
	                
	                if (!isValidForConfirmation) {
	                    failedItems.add(String.valueOf(item.get("receiptCode")));
	                    continue;
	                }
	                */
	                int result = 0;
	                
	                // 위치 지정 여부에 따라 처리
	                if (withLocation) {
	                    // 위치 지정 입고 - 창고/위치 코드 업데이트
	                    Map<String, Object> updateMap = new HashMap<>();
	                    updateMap.put("receiptNo", receiptNo);
	                    updateMap.put("receiptStatus", MesStatusConstants.RECEIPT_STATUS_COMPLETED);
	                    updateMap.put("warehouseCode", item.get("warehouseCode"));
	                    updateMap.put("locationCode", item.get("locationCode"));
	                    updateMap.put("updatedBy", userId);
	                    
	                    result = mRmapper.updateReceiptStatusAndLocation(updateMap);
	                } else {
	                    // 기본 입고 - 상태만 업데이트
	                    Map<String, Object> updateMap = new HashMap<>();
	                    updateMap.put("receiptNo", receiptNo);
	                    updateMap.put("receiptStatus", MesStatusConstants.RECEIPT_STATUS_COMPLETED);
	                    updateMap.put("updatedBy", userId);
	                    
	                    result = mRmapper.updateReceiptStatus(updateMap);
	                }
	                
	                if (result > 0) {
	                    updateCount++;
	                    
	                    // 2. 재고 이력 기록
	                    Map<String, Object> historyMap = new HashMap<>();
	                    historyMap.put("receiptNo", receiptNo);
	                    historyMap.put("actionType", "입고확정");
	                    
	                    if (withLocation) {
	                        // 위치 지정 입고인 경우 이력에 창고/위치 정보 포함
	                        historyMap.put("actionDescription", 
	                            "입고 확정 처리됨 (창고: " + item.get("warehouseCode") + 
	                            ", 위치: " + item.get("locationCode") + ")");
	                    } else {
	                        historyMap.put("actionDescription", "입고 확정 처리됨");
	                    }
	                    
	                    historyMap.put("actionUser", userId);
	                    historyMap.put("createdBy", userId);
	                    
	                    mRhmapper.insertHistory(historyMap);
	                    
	                    // 3. 재고 정보 업데이트 - 실제 입고 수량만큼 재고 증가
	                    Map<String, Object> inventoryParams = new HashMap<>();
	                    inventoryParams.put("mtlCode", receiptDetail.get("MTL_CODE"));
	                    
	                    // 창고/위치 정보 설정
	                    if (withLocation) {
	                        inventoryParams.put("warehouseCode", item.get("warehouseCode"));
	                        inventoryParams.put("locationCode", item.get("locationCode"));
	                    } else {
	                        // 위치 지정 입고가 아닌 경우 기준정보에서 창고/위치 가져오기
	                        Map<String, Object> param = new HashMap<>();
	                        param.put("MTL_CODE", receiptDetail.get("MTL_CODE"));
	                        Map<String, Object> materialInfo = mmmapper.selectSingleMaterials(param);
	                        
	                        if (materialInfo != null) {
	                            String warehouseCode = (String) materialInfo.get("DEFAULT_WAREHOUSE_CODE");
	                            String locationCode = (String) materialInfo.get("DEFAULT_LOCATION_CODE");
	                            
	                            inventoryParams.put("warehouseCode", warehouseCode);
	                            inventoryParams.put("locationCode", locationCode);
	                        } else {
	                            // 기본값 설정 - RECEIPT_DETAIL에서 가져오기
	                            inventoryParams.put("warehouseCode", receiptDetail.get("WAREHOUSE_CODE"));
	                            inventoryParams.put("locationCode", receiptDetail.get("LOCATION_CODE"));
	                        }
	                    }
	                    
	                    inventoryParams.put("receivedQty", receiptDetail.get("RECEIVED_QTY"));
	                    inventoryParams.put("updatedBy", userId);
	                    
	                    // 재고 업데이트 (있으면 증가, 없으면 생성)
	                    mImapper.mergeInventory(inventoryParams);
	                    
	                    // 4. 입고별 재고 정보 추가 (FIFO 관리용)
	                    Map<String, Object> stockParams = new HashMap<>();
	                    stockParams.put("receiptNo", receiptNo);
	                    stockParams.put("mtlCode", receiptDetail.get("MTL_CODE"));
	                    stockParams.put("remainingQty", receiptDetail.get("RECEIVED_QTY"));
	                    stockParams.put("receiptDate", receiptDetail.get("RECEIPT_DATE"));
	                    stockParams.put("createdBy", userId);
	                    
	                    mRsmapper.insertStock(stockParams);
	                    
	                    log.info("입고 확정 처리 완료: 입고번호={}, 자재코드={}, 수량={}",
	                            receiptNo, receiptDetail.get("MTL_CODE"), receiptDetail.get("RECEIVED_QTY"));
	                } else {
	                    failedItems.add(String.valueOf(item.get("receiptCode")));
	                }
	            } catch (Exception e) {
	                log.error("개별 입고 확정 처리 중 오류 발생: {}", e.getMessage(), e);
	                failedItems.add(String.valueOf(item.get("receiptCode")));
	            }
	        }
	        
	        if (updateCount > 0) {
	            String message = updateCount + "개 항목의 입고 확정이 완료되었습니다.";
	            if (!failedItems.isEmpty()) {
	                message += String.format(" (%d개 항목 실패)", failedItems.size());
	            }
	            
	            resultMap.put("success", true);
	            resultMap.put("message", message);
	            resultMap.put("updateCount", updateCount);
	            resultMap.put("failedItems", failedItems);
	        } else {
	            resultMap.put("success", false);
	            resultMap.put("message", "입고 확정 처리가 실패했습니다.");
	            resultMap.put("failedItems", failedItems);
	        }
	        
	    } catch (Exception e) {
	        log.error("입고 확정 처리 중 오류 발생: {}", e.getMessage(), e);
	        resultMap.put("success", false);
	        resultMap.put("message", "오류가 발생했습니다: " + e.getMessage());
	        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
	    }
	    
	    return resultMap;
	}
	
	/**
     * 검수 요청 처리 - 입고일을 검수 요청일로 설정
     * 
     * @param params 검수 요청 정보
     * @return 처리 결과
     */
    @Transactional
    public Map<String, Object> requestInspection(Map<String, Object> params) {
        Map<String, Object> resultMap = new HashMap<>();
        
        try {
            log.info("검수 등록 처리 시작: {}", params);
            
            Long receiptNo = null;
            if (params.get("receiptNo") instanceof Long) {
                receiptNo = (Long) params.get("receiptNo");
            } else if (params.get("receiptNo") instanceof String) {
                receiptNo = Long.parseLong((String) params.get("receiptNo"));
            } else if (params.get("receiptNo") instanceof Integer) {
                receiptNo = ((Integer) params.get("receiptNo")).longValue();
            }
            
            if (receiptNo == null) {
                resultMap.put("success", false);
                resultMap.put("message", "입고 번호가 유효하지 않습니다.");
                return resultMap;
            }
            
            // 현재 날짜를 입고일로 설정
            LocalDate today = LocalDate.now();
            String todayStr = today.toString();
            
            // 사용자 ID 가져오기
            String userId = SecurityUtil.getUserId();
            
            // 입고 상태 및 입고일 업데이트
            Map<String, Object> updateMap = new HashMap<>();
            updateMap.put("receiptNo", receiptNo);
            updateMap.put("receiptStatus", "검수중");
            updateMap.put("receiptDate", todayStr); // 검수 요청일을 입고일로 설정
            updateMap.put("updatedBy", userId);
            updateMap.put("updatedDate", todayStr);
            updateMap.put("receiptCode", params.get("receiptCode"));
            
            int result = mRmapper.updateReceiptStatusAndDate(updateMap);
            
            iqcService.insertIqcInspection(updateMap);
            
            if (result > 0) {
                // 이력 기록
                Map<String, Object> historyMap = new HashMap<>();
                historyMap.put("receiptNo", receiptNo);
                historyMap.put("actionType", "검수시작");
                historyMap.put("actionDescription", "검수 등록됨");
                historyMap.put("actionUser", userId);
                historyMap.put("createdBy", userId);
                
                mRhmapper.insertHistory(historyMap);
                
                log.info("검수 등록 처리 완료: 입고번호={}", receiptNo);
                
                resultMap.put("success", true);
                resultMap.put("message", "검수 등록이 완료되었습니다.");
            } else {
                log.warn("검수 등록 처리 실패: 입고번호={}", receiptNo);
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
     * 다수의 입고 항목에 대해 검수 등록을 처리합니다.
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
            
            // 다건
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
                        failedItems.add(String.valueOf(item.get("receiptCode")));
                    }
                } catch (Exception e) {
                    log.error("개별 검수 등록 처리 중 오류: {}", e.getMessage(), e);
                    failedItems.add(String.valueOf(item.get("receiptCode")));
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
     * 검수 완료 여부 확인
     * 검수가 완료되었는지 확인합니다. (합격, 조건부 합격만 OK)
     * 
     * @param receiptNo 입고 번호
     * @return 검수 완료 여부
     */
//    public boolean isInspectionCompleted(Long receiptNo) {
//        try {
//            // 검수 정보 조회
//            Map<String, Object> inspectionInfo = getInspectionInfo(receiptNo);
//            
//            if (inspectionInfo == null) {
//                return false;
//            }
//            
//            // 검수 결과 확인
//            String inspResult = (String) inspectionInfo.get("INSP_RESULT");
//            
//            // 합격 또는 조건부 합격만 처리 가능
//            return "PASS".equals(inspResult) || "CONDITIONAL_PASS".equals(inspResult);
//            
//        } catch (Exception e) {
//            log.error("검수 완료 여부 확인 중 오류: {}", e.getMessage(), e);
//            return false;
//        }
//    }
}