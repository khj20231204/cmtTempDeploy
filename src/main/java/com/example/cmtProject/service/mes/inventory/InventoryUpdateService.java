package com.example.cmtProject.service.mes.inventory;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.cmtProject.mapper.mes.inventory.InventoryUpdateMapper;
import com.example.cmtProject.mapper.mes.inventory.MaterialReceiptStockMapper;
import com.example.cmtProject.util.SecurityUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class InventoryUpdateService {
    
    @Autowired
    private InventoryUpdateMapper Ium;
    
    @Autowired
    private MaterialReceiptStockMapper mrsmapper;
    
    /**
     * 생산계획 대비 자재/제품 할당 수량 업데이트 (FIFO 적용)
     * @param params soCode - 판매주문 코드, soQty - 주문 수량, updatedBy - 수정자
     */
    @Transactional
    public void updateAllocatedQuantities(Map<String, Object> params) {
        String soCode = (String) params.get("soCode");
        Long soQty = (Long) params.get("soQty");
        
        // 현재 사용자 ID 가져오기
        String userId = SecurityUtil.getUserId();
        params.put("updatedBy", userId);
        
        log.info("FIFO 기반 재고 할당 시작 - 주문코드: {}, 주문수량: {}", soCode, soQty);
        
        // BOM 항목 조회
        List<Map<String, Object>> bomItems = Ium.getBomItems(params);
        log.info("BOM 항목 조회: 총 {}개", bomItems.size());
        
        // BOM 항목 로깅
        for (Map<String, Object> item : bomItems) {
            log.info("아이템: {} (타입: {}, 필요수량: {} * {} = {})", 
                item.get("PARENT_PDT_CODE"), 
                item.get("ITEM_TYPE"),
                item.get("BOM_QTY"), 
                soQty,
                Double.parseDouble(item.get("BOM_QTY").toString()) * soQty);
        }
        
        // 1. 자재 재고 업데이트 (FIFO 방식)
        allocateMaterialsFIFO(bomItems, soQty, userId);
        
        // 2. 제품 재고 업데이트 (기존 방식)
        int productUpdated = Ium.updateProductAllocatedQty(params);
        log.info("제품 재고 업데이트: {} 건", productUpdated);
    }
    
    /**
     * FIFO 방식으로 자재 재고 할당
     * @param bomItems BOM 항목 목록
     * @param soQty 주문 수량
     * @param userId 사용자 ID
     */
    private void allocateMaterialsFIFO(List<Map<String, Object>> bomItems, Long soQty, String userId) {
        for (Map<String, Object> item : bomItems) {
            String itemType = (String) item.get("ITEM_TYPE");
            if ("MATERIAL".equals(itemType)) {
                String mtlCode = (String) item.get("PARENT_PDT_CODE");
                double bomQty = Double.parseDouble(item.get("BOM_QTY").toString());
                double requiredQty = bomQty * soQty;
                
                log.info("자재 {} FIFO 할당 진행 - 필요수량: {}", mtlCode, requiredQty);
                
                // FIFO 방식으로 재고 할당
                allocateMaterialFIFO(mtlCode, requiredQty, userId);
            }
        }
    }
    
    /**
     * 단일 자재에 대한 FIFO 할당
     * @param mtlCode 자재 코드
     * @param requiredQty 필요 수량
     * @param userId 사용자 ID
     */
    private void allocateMaterialFIFO(String mtlCode, double requiredQty, String userId) {
        double remainingToAllocate = requiredQty;
        double totalAllocated = 0;
        
        // 자재의 총 가용 재고 확인
        Double totalAvailable = mrsmapper.getTotalAvailableStock(mtlCode);
        if (totalAvailable == null || totalAvailable < requiredQty) {
            log.warn("자재 {} 가용 재고 부족: 필요={}, 가용={}", 
                mtlCode, requiredQty, totalAvailable != null ? totalAvailable : 0);
        }
        
        // 자재의 입고별 재고 목록 조회 (FIFO 순서)
        List<Map<String, Object>> receiptStocks = mrsmapper.getStocksByMtlCodeOrderByDate(mtlCode);
        
        if (receiptStocks == null || receiptStocks.isEmpty()) {
            log.warn("자재 {}의 입고별 재고가 없습니다.", mtlCode);
            return;
        }
        
        for (Map<String, Object> stock : receiptStocks) {
            if (remainingToAllocate <= 0) break;
            
            Long stockNo = Long.valueOf(stock.get("RECEIPT_STOCK_NO").toString());
            double remainingQty = Double.parseDouble((String) stock.get("REMAINING_QTY"));
            
            // 할당할 수량 결정
            double qtyToAllocate = Math.min(remainingQty, remainingToAllocate);
            
            // 입고별 재고 차감
            Map<String, Object> deductParams = new HashMap<>();
            deductParams.put("receiptStockNo", stockNo);
            deductParams.put("deductQty", String.valueOf(qtyToAllocate));
            deductParams.put("updatedBy", userId);
            
            mrsmapper.deductStock(deductParams);
            
            // 할당량 누적 및 남은 할당량 업데이트
            totalAllocated += qtyToAllocate;
            remainingToAllocate -= qtyToAllocate;
            
            log.debug("입고분 {} 할당: {} (총 할당: {})", 
                stockNo, qtyToAllocate, totalAllocated);
        }
        
        // 전체 재고의 할당 수량 업데이트
        Map<String, Object> updateParams = new HashMap<>();
        updateParams.put("mtlCode", mtlCode);
        updateParams.put("allocatedQty", String.valueOf(totalAllocated));
        updateParams.put("updatedBy", userId);
        
        Ium.updateMaterialAllocatedQty(updateParams);
        
        if (remainingToAllocate > 0) {
            log.warn("자재 {} 할당 부족: {}만큼 부족", mtlCode, remainingToAllocate);
        }
    }
}