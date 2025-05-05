package com.example.cmtProject.service.mes.inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.example.cmtProject.mapper.mes.inventory.MaterialInventoryMapper;
import com.example.cmtProject.mapper.mes.inventory.MaterialReceiptStockMapper;
import com.example.cmtProject.util.SecurityUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MaterialInventoryService {
    
    @Autowired
    private MaterialInventoryMapper mImapper;
    
    @Autowired
    private MaterialReceiptStockMapper rSmapper;
    
    /**
     * 재고 목록 조회
     * @param map 검색 조건
     * @return 재고 목록
     */
    public List<Map<String,Object>> inventoryList(Map<String, Object>map){
        return mImapper.mInventoryList(map);
    }
    
    /**
     * 재고 정보 저장
     * @param inventoryList 저장할 재고 목록
     * @return 처리 결과
     */
    @Transactional
    public Map<String, Object> saveInventory(List<Map<String, Object>> inventoryList) {
        Map<String, Object> resultMap = new HashMap<>();
        int updateCount = 0;
        int insertCount = 0;
        
        try {
            log.info("재고 정보 저장 시작: {}건", inventoryList.size());
            
            // 현재 사용자 ID 가져오기
            String userId = SecurityUtil.getUserId();
            
            for (Map<String, Object> item : inventoryList) {
                // 처리자 설정 (현재 로그인한 사용자 ID)
                item.put("updatedBy", userId);
                
                // 재고번호 있으면 업데이트, 없으면 신규 등록
                if (item.get("INV_NO") != null && !item.get("INV_NO").toString().isEmpty()) {
                    int result = mImapper.updateInventory(item);
                    if (result > 0) {
                        updateCount++;
                    }
                } else {
                    int result = mImapper.insertInventory(item);
                    if (result > 0) {
                        insertCount++;
                    }
                }
            }
            
            resultMap.put("success", true);
            resultMap.put("message", String.format("저장 완료(%d건 등록, %d건 수정)", insertCount, updateCount));
            resultMap.put("insertCount", insertCount);
            resultMap.put("updateCount", updateCount);
            
            log.info("재고 정보 저장 완료: {}건 등록, {}건 수정", insertCount, updateCount);
            
        } catch (Exception e) {
            log.error("재고 정보 저장 중 오류 발생: {}", e.getMessage(), e);
            resultMap.put("success", false);
            resultMap.put("message", "재고 정보 저장 중 오류가 발생했습니다: " + e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        
        return resultMap;
    }
    
    /**
     * FIFO 방식으로 재고 차감
     * 가장 오래된 입고분부터 순차적으로 재고를 차감합니다.
     * 
     * @param params 차감 정보 (mtlCode, consumptionQty, updatedBy 포함)
     * @return 처리 결과
     */
    @Transactional
    public Map<String, Object> consumeMaterialFIFO(Map<String, Object> params) {
        Map<String, Object> resultMap = new HashMap<>();
        
        try {
            String mtlCode = (String) params.get("mtlCode");
            String consumptionQty = (String) params.get("consumptionQty");
            
            // 현재 사용자 ID 가져오기
            String userId = SecurityUtil.getUserId();
            
            log.info("FIFO 재고 차감 시작: 자재코드={}, 차감수량={}", mtlCode, consumptionQty);
            
            // 1. 해당 자재의 총 재고 확인
            Map<String, Object> inventoryInfo = mImapper.getInventoryByMtlCode(mtlCode);
            
            if (inventoryInfo == null || 
                Double.parseDouble((String) inventoryInfo.get("CURRENT_QTY")) < Double.parseDouble(consumptionQty)) {
                log.warn("재고 부족: 요청={}, 현재재고={}", 
                    consumptionQty, 
                    inventoryInfo != null ? inventoryInfo.get("CURRENT_QTY") : "0");
                resultMap.put("success", false);
                resultMap.put("message", "재고가 부족합니다.");
                return resultMap;
            }
            
            // 2. 가장 오래된 입고분부터 차례로 조회
            List<Map<String, Object>> stockList = 
                    rSmapper.getStocksByMtlCodeOrderByDate(mtlCode);
            
            log.info("FIFO 차감 대상 입고재고 조회: {}건", stockList.size());
            
            double remainingToConsume = Double.parseDouble(consumptionQty);
            List<Map<String, Object>> consumptionDetails = new ArrayList<>(); // 확장용
            
            // 3. FIFO로 재고 차감
            for (Map<String, Object> stock : stockList) {
                if (remainingToConsume <= 0) break;
                
                Long stockNo = Long.valueOf(stock.get("RECEIPT_STOCK_NO").toString());
                double remainingQty = Double.parseDouble((String) stock.get("REMAINING_QTY"));
                
                // 차감할 수량 결정
                double qtyToDeduct = Math.min(remainingQty, remainingToConsume);
                
                log.debug("입고분 차감: 입고재고번호={}, 차감수량={}, 남은수량={}",
                    stockNo, qtyToDeduct, remainingQty - qtyToDeduct);
                
                // 입고별 재고 차감
                Map<String, Object> deductParams = new HashMap<>();
                deductParams.put("receiptStockNo", stockNo);
                deductParams.put("deductQty", String.valueOf(qtyToDeduct));
                deductParams.put("updatedBy", userId);
                
                rSmapper.deductStock(deductParams);
                
                // 차감 상세 정보 저장 (확장용)
                Map<String, Object> consumptionDetail = new HashMap<>();
                consumptionDetail.put("receiptNo", stock.get("RECEIPT_NO"));
                consumptionDetail.put("receiptDate", stock.get("RECEIPT_DATE"));
                consumptionDetail.put("consumedQty", qtyToDeduct);
                consumptionDetail.put("remainingQty", remainingQty - qtyToDeduct);
                consumptionDetail.put("timestamp", java.time.LocalDateTime.now().toString());
                consumptionDetails.add(consumptionDetail);
                
                // 차감할 남은 수량 갱신
                remainingToConsume -= qtyToDeduct;
            }
            
            // 4. 총 재고에서도 차감
            Map<String, Object> inventoryParams = new HashMap<>();
            inventoryParams.put("mtlCode", mtlCode);
            inventoryParams.put("consumptionQty", consumptionQty);
            inventoryParams.put("updatedBy", userId);
            
            mImapper.deductInventory(inventoryParams);
            
            log.info("FIFO 재고 차감 완료: 자재코드={}, 차감수량={}", mtlCode, consumptionQty);
            
            resultMap.put("success", true);
            resultMap.put("message", "재고가 FIFO 원칙에 따라 성공적으로 차감되었습니다.");
            resultMap.put("consumptionDetails", consumptionDetails); // 확장용
            
        } catch (Exception e) {
            log.error("FIFO 재고 차감 중 오류 발생: " + e.getMessage(), e);
            resultMap.put("success", false);
            resultMap.put("message", "재고 차감 중 오류가 발생했습니다: " + e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        
        return resultMap;
    }
    
    /**
     * FIFO 상세 정보 조회
     */
    public Map<String, Object> getFIFODetail(String mtlCode) {
        Map<String, Object> result = new HashMap<>();
        
        // 전체 재고 정보
        Map<String, Object> inventory = mImapper.getInventoryByMtlCode(mtlCode);
        
        // 입고별 재고 목록 (FIFO 순서)
        List<Map<String, Object>> stockList = rSmapper.getStocksByMtlCodeOrderByDate(mtlCode);
        
        // FIFO 순번 및 상태 추가
        int order = 1;
        for (Map<String, Object> stock : stockList) {
            stock.put("FIFO_ORDER", order++);
            double remaining = Double.parseDouble((String) stock.get("REMAINING_QTY"));
            stock.put("STATUS", remaining > 0 ? (order == 1 ? "사용중" : "대기") : "소진");
            
            // 확장 가능성: 필요한 경우 추가 정보 계산
            if (inventory != null && order == 1 && remaining > 0) {
                double currentQty = Double.parseDouble((String) inventory.get("CURRENT_QTY"));
                double allocatedQty = Double.parseDouble((String) inventory.get("ALLOCATED_QTY"));
                stock.put("CONSUMPTION_AVAILABLE", currentQty - allocatedQty);
            }
        }
        
        result.put("inventory", inventory);
        result.put("stockList", stockList);
        
        return result;
    }

    /**
     * FIFO 소모 이력 조회
     */
    public List<Map<String, Object>> getFIFOHistory(String mtlCode) {
        List<Map<String, Object>> result = new ArrayList<>();
        
        // 현재는 MATERIAL_RECEIPT_STOCK에서 소진된 항목 조회
        List<Map<String, Object>> stockList = rSmapper.getStocksByMtlCodeOrderByDate(mtlCode);
        
        for (Map<String, Object> stock : stockList) {
            double remaining = Double.parseDouble((String) stock.get("REMAINING_QTY"));
            if (remaining == 0) {
                Map<String, Object> history = new HashMap<>();
                history.put("RECEIPT_NO", stock.get("RECEIPT_NO"));
                history.put("RECEIPT_DATE", stock.get("RECEIPT_DATE"));
                history.put("UPDATED_DATE", stock.get("UPDATED_DATE"));
                history.put("ACTION_TYPE", "소진 완료");
                history.put("ACTION_DESCRIPTION", "FIFO 방식으로 전체 소진됨");
                history.put("UPDATED_BY", stock.get("UPDATED_BY"));
                result.add(history);
            } else {
                // 확장 가능성: 부분 소모 이력 추적 로직 추가 공간 현재는 시간이없어서...
                // TODO: 부분 소모 이력 테이블 연동 시 이 부분에 추가
            }
        }
        
        return result;
    }

    /**
     * FIFO 상태 통계 조회 (확장용)
     */
    public Map<String, Object> getFIFOStatistics(String mtlCode) {
        Map<String, Object> stats = new HashMap<>();
        
        List<Map<String, Object>> stockList = rSmapper.getStocksByMtlCodeOrderByDate(mtlCode);
        
        int activeCount = 0;
        int waitingCount = 0;
        int consumedCount = 0;
        
        for (Map<String, Object> stock : stockList) {
            double remaining = Double.parseDouble((String) stock.get("REMAINING_QTY"));
            if (remaining > 0) {
                if (activeCount == 0) activeCount++;
                else waitingCount++;
            } else {
                consumedCount++;
            }
        }
        
        stats.put("activeCount", activeCount);
        stats.put("waitingCount", waitingCount);
        stats.put("consumedCount", consumedCount);
        
        return stats;
    }
}