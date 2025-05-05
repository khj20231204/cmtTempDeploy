package com.example.cmtProject.controller.mes.inventory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.cmtProject.comm.response.ApiResponse;
import com.example.cmtProject.constants.PathConstants;
import com.example.cmtProject.service.mes.inventory.ProductsIssueService;
import com.example.cmtProject.util.SecurityUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 제품 출고관리 REST 컨트롤러
 * 제품 출고 조회 및 처리 관련 RESTful API를 제공합니다.
 */
@RestController
@RequestMapping(PathConstants.API_PRODUCTSISSUE_BASE)
@Slf4j
public class ProductsIssueRestController {
    
    @Autowired
    private ProductsIssueService pIssueService;
    
    /**
     * 출고 목록 조회 API
     * 
     * @param keyword 검색 키워드 (선택)
     * @return 출고 목록 데이터
     */
    @GetMapping(PathConstants.LIST)
    public ApiResponse<List<Map<String, Object>>> getProductsIssue(
            @RequestParam(name = "keyword", required = false) String keyword) {
        
        Map<String, Object> findMap = new HashMap<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            findMap.put("keyword", keyword);
        }
        
        log.info("출고 목록 조회 요청. 검색어: {}", keyword);
        List<Map<String, Object>> pIssue = pIssueService.issueList(findMap);
        log.info("출고 목록 조회 결과: {}건", pIssue.size());
        
        return ApiResponse.success(pIssue);
    }
    
    /**
     * 출고 가능한 수주 목록 조회 API
     * 
     * @param keyword 검색 키워드 (선택)
     * @return 수주 목록 데이터
     */
    @GetMapping(PathConstants.SALES_ORDERS)
    public ApiResponse<List<Map<String, Object>>> getSalesOrders(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "status", required = false) String status) {
        
        Map<String, Object> findMap = new HashMap<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            findMap.put("keyword", keyword);
        }
        
        if (status != null && !status.trim().isEmpty()) {
            findMap.put("status", status);
        } else {
            // 기본 상태 - 확정, 계획, 완료 상태
            findMap.put("status", "SO_CONFIRMED,SO_PLANNED,SO_COMPLETED");
        }
        
        log.info("출고 가능한 수주 목록 조회 요청. 검색어: {}, 상태: {}", keyword, findMap.get("status"));
        List<Map<String, Object>> salesOrders = pIssueService.getSalesOrderList(findMap);
        log.info("수주 목록 조회 결과: {}건", salesOrders.size());
        
        return ApiResponse.success(salesOrders);
    }
    
    /**
     * 출고 상세 정보 조회 API
     * 
     * @param issueNo 출고 번호
     * @return 출고 상세 정보
     */
    @GetMapping(PathConstants.DETAIL + "/{issueNo}")
    public ApiResponse<Map<String, Object>> getIssueDetail(@PathVariable("issueNo") Long issueNo) {
        log.info("출고 상세 정보 조회 요청. 출고번호: {}", issueNo);
        Map<String, Object> detail = pIssueService.getIssueDetail(issueNo);
        
        if ((Boolean) detail.get("success")) {
            return ApiResponse.success(detail);
        } else {
            return ApiResponse.error(detail.get("message").toString(), detail);
        }
    }
    
    /**
     * 출고 이력 정보 조회 API
     * 
     * @param issueNo 출고 번호
     * @return 출고 이력 정보 목록
     */
    @GetMapping(PathConstants.HISTORY + "/{issueNo}")
    public ApiResponse<List<Map<String, Object>>> getIssueHistory(@PathVariable("issueNo") Long issueNo) {
        log.info("출고 이력 정보 조회 요청. 출고번호: {}", issueNo);
        
        try {
            List<Map<String, Object>> historyList = pIssueService.getIssueHistory(issueNo);
            
            log.info("출고 이력 정보 조회 결과: {}건", historyList.size());
            return ApiResponse.success(historyList);
        } catch (Exception e) {
            log.error("출고 이력 정보 조회 중 오류 발생: {}", e.getMessage(), e);
            return ApiResponse.error("이력 정보 조회 중 오류가 발생했습니다.", null);
        }
    }
    
    /**
     * 수주 정보를 바탕으로 출고 요청 생성 API
     * 
     * @param soData 수주 데이터
     * @return 처리 결과
     */
    @PostMapping(PathConstants.REQUEST)
    public ApiResponse<Map<String, Object>> createIssueRequest(@RequestBody Map<String, Object> soData) {
        log.info("수주 정보 기반 출고 요청 생성. 수주코드: {}", soData.get("SO_CODE"));
        
        Map<String, Object> result = pIssueService.createIssueRequestFromSalesOrder(soData);
        
        if ((Boolean) result.get("success")) {
            return ApiResponse.success(result.get("message").toString(), result);
        } else {
            return ApiResponse.error(result.get("message").toString(), result);
        }
    }
    
    /**
     * 다건 출고 요청 생성 API
     * 
     * @param params 여러 수주 데이터 목록
     * @return 처리 결과
     */
    @PostMapping(PathConstants.REQUEST + PathConstants.BATCH_SUFFIX)
    public ApiResponse<Map<String, Object>> createIssueRequestBatch(@RequestBody Map<String, Object> params) {
        log.info("다건 출고 요청 생성 요청");
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> salesOrders = (List<Map<String, Object>>) params.get("items");
        
        if (salesOrders == null || salesOrders.isEmpty()) {
            return ApiResponse.error("출고 요청 생성할 항목이 없습니다.", null);
        }
        
        Map<String, Object> result = pIssueService.createIssueRequestsBatch(salesOrders);
        
        if ((Boolean) result.get("success")) {
            return ApiResponse.success(result.get("message").toString(), result);
        } else {
            return ApiResponse.error(result.get("message").toString(), result);
        }
    }
    
    /**
     * 검수 등록 API - 출고 상태를 검수중으로 변경
     * 
     * @param params 검수 등록 정보
     * @return 처리 결과
     */
    @PostMapping(PathConstants.INSPECTION)
    public ApiResponse<Map<String, Object>> registerInspection(@RequestBody Map<String, Object> params) {
        log.info("검수 등록 요청: {}", params);
        
        Map<String, Object> result = pIssueService.requestInspection(params);
        
        if ((Boolean) result.get("success")) {
            log.info("검수 등록 처리 성공: {}", result.get("message"));
            return ApiResponse.success(result.get("message").toString(), result);
        } else {
            log.warn("검수 등록 처리 실패: {}", result.get("message"));
            return ApiResponse.error(result.get("message").toString(), result);
        }
    }
    
    /**
     * 다건 검수 등록 API
     * 
     * @param params 검수 등록 정보 (여러 건)
     * @return 처리 결과
     */
    @PostMapping(PathConstants.INSPECTION + PathConstants.BATCH_SUFFIX)
    public ApiResponse<Map<String, Object>> registerInspectionMultiple(@RequestBody Map<String, Object> params) {
        log.info("다건 검수 등록 요청");
        
        Map<String, Object> result = pIssueService.registerInspectionMultiple(params);
        
        if ((Boolean) result.get("success")) {
            return ApiResponse.success(result.get("message").toString(), result);
        } else {
            return ApiResponse.error(result.get("message").toString(), result);
        }
    }
    
    /**
     * 출고 처리 API - 출고 확정 및 재고 차감
     * 
     * @param params 출고 처리 정보
     * @return 처리 결과
     */
    @PostMapping(PathConstants.PROCESS)
    public ApiResponse<Map<String, Object>> processIssue(@RequestBody Map<String, Object> params) {
        log.info("출고 처리 요청: {}", params);
        
        Map<String, Object> result = pIssueService.processIssue(params);
        
        if ((Boolean) result.get("success")) {
            return ApiResponse.success(result.get("message").toString(), result);
        } else {
            return ApiResponse.error(result.get("message").toString(), result);
        }
    }
    
    /**
     * 다건 출고 처리 API
     * 
     * @param params 출고 처리 정보 (여러 건)
     * @return 처리 결과
     */
    @PostMapping(PathConstants.PROCESS + PathConstants.BATCH_SUFFIX)
    public ApiResponse<Map<String, Object>> processIssueBatch(@RequestBody Map<String, Object> params) {
        log.info("다건 출고 처리 요청");
        
        Map<String, Object> result = pIssueService.processIssueMultiple(params);
        
        if ((Boolean) result.get("success")) {
            return ApiResponse.success(result.get("message").toString(), result);
        } else {
            return ApiResponse.error(result.get("message").toString(), result);
        }
    }
    
    /**
     * 출고 취소 API
     * 
     * @param params 취소 처리 정보
     * @return 처리 결과
     */
    @PostMapping(PathConstants.CANCEL)
    public ApiResponse<Map<String, Object>> cancelIssue(@RequestBody Map<String, Object> params) {
        log.info("출고 취소 요청: {}", params);
        
        Map<String, Object> result = pIssueService.cancelIssue(params);
        
        if ((Boolean) result.get("success")) {
            return ApiResponse.success(result.get("message").toString(), result);
        } else {
            return ApiResponse.error(result.get("message").toString(), result);
        }
    }
    
    /**
     * 수주 상태별 출고 요청 생성 API
     * 
     * @param params 상태 조건
     * @return 처리 결과
     */
    @PostMapping(PathConstants.REQUEST + PathConstants.BY_STATUS)
    public ApiResponse<Map<String, Object>> createIssueRequestsByStatus(@RequestBody Map<String, Object> params) {
        log.info("수주 상태별 출고 요청 생성 요청: {}", params);
        
        Map<String, Object> result = pIssueService.createIssueRequestsByStatus(params);
        
        if ((Boolean) result.get("success")) {
            return ApiResponse.success(result.get("message").toString(), result);
        } else {
            return ApiResponse.error(result.get("message").toString(), result);
        }
    }
}