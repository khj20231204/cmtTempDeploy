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
import com.example.cmtProject.service.mes.inventory.ProductsMasterService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(PathConstants.API_PRODUCTS_INFO_BASE)
@Slf4j
public class ProductsMasterRestController {
    
    @Autowired
    private ProductsMasterService pms;
    
    /**
     * 제품 기준정보 목록 조회 API
     * 
     * @param keyword 검색 키워드 (선택사항)
     * @return 제품 목록 데이터
     */
    @GetMapping(PathConstants.LIST)
    public ApiResponse<List<Map<String, Object>>> getProductsList(
            @RequestParam(name = "keyword", required = false) String keyword) {
        
        Map<String, Object> findMap = new HashMap<>();
        // 사용자가 입력한 검색어(keyword)가 null이 아니고, 공백만으로 구성되어 있지 않은 경우에만 실행
        if (keyword != null && !keyword.trim().isEmpty()) {
            // 검색어를 Map에 추가 (이후 검색 조건으로 사용하기 위함)
            findMap.put("keyword", keyword);
        }
        
        log.info("제품 기준정보 목록 조회 요청. 검색어: {}", keyword);
        List<Map<String, Object>> productsList = pms.productsList(findMap);
        log.info("제품 기준정보 목록 조회 결과: {}건", productsList.size());
        
        return ApiResponse.success(productsList);
    }
    
    /**
     * 제품 정보 단건 조회 API
     * 
     * @param pdtCode 제품 코드
     * @return 제품 정보
     */
    @GetMapping(PathConstants.PRODUCTS_INFO_SINGLE)
    public ApiResponse<Map<String, Object>> getProducts(@PathVariable("pdtCode") String pdtCode) {
        log.info("제품 정보 단건 조회 요청. 제품 코드: {}", pdtCode);
        
        Map<String, Object> param = new HashMap<>();
        param.put("PDT_CODE", pdtCode);
        
        Map<String, Object> products = pms.productsSingle(param);
        
        if (products == null) {
            return ApiResponse.error("해당 제품 정보를 찾을 수 없습니다.");
        }
        
        return ApiResponse.success(products);
    }
    
    /**
     * 제품 정보 단건 저장 API (등록/수정)
     * 
     * @param productsData 제품 정보
     * @return 처리 결과
     */
    @PostMapping("")
    public ApiResponse<Map<String, Object>> saveProducts(@RequestBody Map<String, Object> productsData) {
        log.info("제품 정보 저장 요청. 데이터: {}", productsData);
        
        Map<String, Object> result = pms.saveProducts(productsData);
        
        if ((Boolean) result.get("success")) {
            return ApiResponse.success(result.get("message").toString(), result);
        } else {
            return ApiResponse.error(result.get("message").toString(), result);
        }
    }
    
    /**
     * 제품 정보 일괄 저장 API
     * 
     * @param requestData 저장할 데이터 목록
     * @return 처리 결과
     */
    @PostMapping(PathConstants.BATCH)
    public ApiResponse<Map<String, Object>> saveBatch(@RequestBody List<Map<String, Object>> requestData) {
        log.info("제품 정보 일괄 저장 요청. 데이터 건수: {}", requestData.size());
        
        Map<String, Object> result = pms.saveBatch(requestData);
        
        if ((Boolean) result.get("success")) {
            return ApiResponse.success(result.get("message").toString(), result);
        } else {
            return ApiResponse.error(result.get("message").toString(), result);
        }
    }
    
    /**
     * 제품 정보 삭제 API
     * 
     * @param pdtCode 제품 코드
     * @return 처리 결과
     */
    @DeleteMapping(PathConstants.PRODUCTS_INFO_SINGLE)
    public ApiResponse<Void> deleteProducts(@PathVariable("pdtCode") String pdtCode) {
        log.info("제품 정보 삭제 요청. 제품 코드: {}", pdtCode);
        
        Map<String, Object> param = new HashMap<>();
        // 프론트에서 바로 정보를 받아옴으로 인해 카멜이 아닌 스네이크로 적어야함
        param.put("PDT_CODE", pdtCode);
        
        Map<String, Object> result = pms.deleteProducts(param);
        
        if ((Boolean) result.get("success")) {
            return ApiResponse.success(result.get("message").toString(), null);
        } else {
            return ApiResponse.error(result.get("message").toString(), null);
        }
    }
}