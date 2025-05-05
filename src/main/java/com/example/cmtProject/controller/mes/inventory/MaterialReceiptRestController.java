package com.example.cmtProject.controller.mes.inventory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.cmtProject.comm.response.ApiResponse;
import com.example.cmtProject.constants.PathConstants;
import com.example.cmtProject.service.mes.inventory.MaterialReceiptService;
import com.example.cmtProject.util.SecurityUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 원자재 입고관리 REST 컨트롤러
 * 원자재 입고 조회 및 처리 관련 RESTful API를 제공합니다.
 */
@RestController
@RequestMapping(PathConstants.API_MATERIALRECEIPT_BASE)
@Slf4j
public class MaterialReceiptRestController {
    
    @Autowired
    private MaterialReceiptService mrs;
    
    /**
     * 입고 목록 조회 API
     * 
     * @param keyword 검색 키워드 (선택)
     * @return 입고 목록 데이터
     */
    @GetMapping(PathConstants.LIST)
    public ApiResponse<List<Map<String, Object>>> getMaterialReceipt(
            @RequestParam(name = "keyword", required = false) String keyword) {
        
        Map<String, Object> findMap = new HashMap<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            findMap.put("keyword", keyword);
        }
        
        log.info("입고 목록 조회 요청. 검색어: {}", keyword);
        List<Map<String, Object>> mReceipt = mrs.receiptList(findMap);
        log.info("입고 목록 조회 결과: {}건", mReceipt.size());
        
        return ApiResponse.success(mReceipt);
    }
    
    /**
     * 입고 상세 정보 조회 API
     * 
     * @param receiptNo 입고 번호
     * @return 입고 상세 정보
     */
    @GetMapping(PathConstants.DETAIL + "/{receiptNo}")
    public ApiResponse<Map<String, Object>> getReceiptDetail(@PathVariable("receiptNo") Long receiptNo) {
        log.info("입고 상세 정보 조회 요청. 입고번호: {}", receiptNo);
        Map<String, Object> detail = mrs.getReceiptDetail(receiptNo);
        
        if ((Boolean) detail.get("success")) {
            return ApiResponse.success(detail);
        } else {
            return ApiResponse.error(detail.get("message").toString(), detail);
        }
    }
    
    /**
     * 입고 이력 정보 조회 API
     * 
     * @param receiptNo 입고 번호
     * @return 입고 이력 정보 목록
     */
    @GetMapping(PathConstants.HISTORY + "/{receiptNo}")
    public ApiResponse<List<Map<String, Object>>> getReceiptHistory(@PathVariable("receiptNo") Long receiptNo) {
        log.info("입고 이력 정보 조회 요청. 입고번호: {}", receiptNo);
        
        try {
            List<Map<String, Object>> historyList = mrs.getReceiptHistory(receiptNo);
            
            log.info("입고 이력 정보 조회 결과: {}건", historyList.size());
            return ApiResponse.success(historyList);
        } catch (Exception e) {
            log.error("입고 이력 정보 조회 중 오류 발생: {}", e.getMessage(), e);
            return ApiResponse.error("이력 정보 조회 중 오류가 발생했습니다.", null);
        }
    }
    
    /**
     * 선택된 발주 정보를 입고대기로 등록 처리 API
     * 
     * @param requestData 선택된 발주 정보 목록
     * @return 처리 결과
     */
    @PostMapping("/register-all")
    public ApiResponse<Map<String, Object>> registerFromPurchaseOrders(@RequestBody Map<String, Object> requestData) {
        log.info("선택된 발주 정보 기반 입고 등록 요청");
        Map<String, Object> result = mrs.createReceiptFromPurchaseOrder(requestData);
        log.info("선택된 발주 정보 기반 입고 등록 결과: {}", result);
        
        if ((Boolean) result.get("success")) {
            return ApiResponse.success(result);
        } else {
            return ApiResponse.error(result.get("message").toString(), result);
        }
    }

    /**
     * 미입고 발주 목록 조회 API
     */
    @GetMapping("/purchase-orders")
    public ApiResponse<List<Map<String, Object>>> getPurchaseOrders() {
        log.info("미입고 발주 목록 조회 요청");
        
        try {
            Map<String, Object> findMap = new HashMap<>();
            List<Map<String, Object>> purchaseOrders = mrs.puchasesList(findMap);
            
            log.info("미입고 발주 목록 조회 결과: {}건", purchaseOrders.size());
            return ApiResponse.success(purchaseOrders);
        } catch (Exception e) {
            log.error("미입고 발주 목록 조회 중 오류 발생: {}", e.getMessage(), e);
            return ApiResponse.error("발주 목록을 조회할 수 없습니다.", null);
        }
    }
    
    /**
     * 입고 확정 처리 API
     * 선택한 입고 항목의 상태를 "입고완료"로 변경하고 재고를 반영합니다.
     * 
     * @param params 입고 확정 처리할 항목 정보
     * @return 처리 결과
     */
    @PostMapping(PathConstants.CONFIRM)
    public ApiResponse<Map<String, Object>> confirmReceipt(@RequestBody Map<String, Object> params) {
        log.info("입고 확정 처리 요청: {}", params);
        
        Map<String, Object> result = mrs.confirmReceipt(params);
        
        if ((Boolean) result.get("success")) {
            log.info("입고 확정 처리 성공: {}", result.get("message"));
            return ApiResponse.success(result.get("message").toString(), result);
        } else {
            log.warn("입고 확정 처리 실패: {}", result.get("message"));
            return ApiResponse.error(result.get("message").toString(), result);
        }
    }
    
    /**
     * 검수 등록 처리 API - 다건 처리 지원
     * 선택한 입고 항목의 상태를 "검수중"으로 변경합니다.
     * 
     * @param params 검수 등록 처리할 항목 정보
     * @return 처리 결과
     */
    @PostMapping(PathConstants.INSPECTION)
    public ApiResponse<Map<String, Object>> registerInspection(@RequestBody Map<String, Object> params) {
        log.info("검수 등록 처리 요청: {}", params);
        
        // 현재 사용자 ID 설정 (이미 서비스 내에서 처리됨)
        // String userId = SecurityUtil.getUserId();
        // params.put("updatedBy", userId);
        
        Map<String, Object> result = mrs.registerInspectionMultiple(params);
        
        if ((Boolean) result.get("success")) {
            log.info("검수 등록 처리 성공: {}", result.get("message"));
            return ApiResponse.success(result.get("message").toString(), result);
        } else {
            log.warn("검수 등록 처리 실패: {}", result.get("message"));
            return ApiResponse.error(result.get("message").toString(), result);
        }
    }
    
    /**
     * 검수 정보 조회 API
     * 
     * @param receiptNo 입고 번호
     * @return 검수 정보
     */
    @GetMapping(PathConstants.INSPECTION + "/{receiptNo}")
    public ApiResponse<Map<String, Object>> getInspectionInfo(@PathVariable("receiptNo") Long receiptNo) {
        log.info("검수 정보 조회 요청. 입고번호: {}", receiptNo);
        
        try {
            Map<String, Object> inspectionInfo = mrs.getInspectionInfo(receiptNo);
            
            // 응답 형식 수정 - ByteArrayInputStream 오류 방지
            Map<String, Object> responseData = new HashMap<>();
            
            if (inspectionInfo == null) {
                // 검수 정보가 없는 경우, '검사 합격' 상태의 검수 정보를 임시로 생성합니다.
                responseData.put("RECEIPT_NO", receiptNo);
                responseData.put("INSP_RESULT", "PASS"); // 검수 결과: 합격
                responseData.put("INSP_DATE", java.time.LocalDateTime.now().toString()); // 현재 날짜
                responseData.put("hasInspection", false);
                
                log.info("검수 정보 없음. 임시 검수 정보 반환: {}", responseData);
            } else {
                // 검수 정보가 있는 경우, 응답 객체에 복사
                responseData.putAll(inspectionInfo);
                responseData.put("hasInspection", true);
                
                log.info("검수 정보 조회 결과: {}", responseData);
            }
            
            return ApiResponse.success(responseData);
            
        } catch (Exception e) {
            log.error("검수 정보 조회 중 오류 발생: {}", e.getMessage(), e);
            return ApiResponse.error("검수 정보 조회 중 오류가 발생했습니다: " + e.getMessage(), null);
        }
    }
}