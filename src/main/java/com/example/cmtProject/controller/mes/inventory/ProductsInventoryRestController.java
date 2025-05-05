package com.example.cmtProject.controller.mes.inventory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.cmtProject.comm.response.ApiResponse;
import com.example.cmtProject.constants.PathConstants;
import com.example.cmtProject.service.mes.inventory.ProductsInventoryService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(PathConstants.API_PRODUCTSINVENTORY_BASE)
@Slf4j
public class ProductsInventoryRestController {
    
    @Autowired
    private ProductsInventoryService pis;
    
    /**
     * 제품 재고 목록 조회 API
     * 
     * @param keyword 검색 키워드 (선택사항)
     * @return 재고 목록 데이터
     */
    @GetMapping(PathConstants.LIST)
    public ApiResponse<List<Map<String, Object>>> getProductsInventory(
            @RequestParam(name = "keyword", required = false) String keyword) {
        
        Map<String, Object> findMap = new HashMap<>();
        // 사용자가 입력한 검색어(keyword)가 null이 아니고, 공백만으로 구성되어 있지 않은 경우에만 실행
        if (keyword != null && !keyword.trim().isEmpty()) {
            // 검색어를 Map에 추가 (이후 검색 조건으로 사용하기 위함)
            findMap.put("keyword", keyword);
        }
        log.info("재고 목록 조회 요청. 검색어: {}", keyword);
        List<Map<String, Object>> pInventory = pis.pInventoryList(findMap);
        log.info("재고 목록 조회 결과: {}건", pInventory.size());
        
        return ApiResponse.success(pInventory);
    }
    
    /**
     * 제품 재고 차감 API (FIFO 적용)
     * 출고에서 사용된 제품의 재고를 FIFO 방식으로 차감합니다.
     * 
     * @param params 차감 정보 (pdtCode: 제품코드, consumptionQty: 소비량, updatedBy: 처리자)
     * @return 처리 결과
     */
    @PostMapping(PathConstants.CONSUME)
    public ApiResponse<Map<String, Object>> consumeProduct(@RequestBody Map<String, Object> params) {
        log.info("제품 재고 차감 요청: {}", params);
        
        Map<String, Object> result = pis.consumeProductFIFO(params);
        
        if ((Boolean) result.get("success")) {
            log.info("제품 재고 차감 성공: {}", result.get("message"));
            return ApiResponse.success(result);
        } else {
            log.warn("제품 재고 차감 실패: {}", result.get("message"));
            return ApiResponse.error(result.get("message").toString(), result);
        }
    }
    
    /**
     * 재고 정보 저장 API
     * 재고 정보를 저장합니다. 가용수량은 트리거에서 자동 계산됩니다.
     * 
     * @param inventoryList 저장할 재고 정보 목록
     * @return 처리 결과
     */
    @PostMapping(PathConstants.SAVE)
    public ApiResponse<Map<String, Object>> saveInventory(@RequestBody List<Map<String, Object>> inventoryList) {
        log.info("재고 정보 저장 요청: {}건", inventoryList.size());
        
        Map<String, Object> result = pis.saveInventory(inventoryList);
        
        if ((Boolean) result.get("success")) {
            log.info("재고 정보 저장 성공: {}", result.get("message"));
            return ApiResponse.success(result);
        } else {
            log.warn("재고 정보 저장 실패: {}", result.get("message"));
            return ApiResponse.error(result.get("message").toString(), result);
        }
    }
}