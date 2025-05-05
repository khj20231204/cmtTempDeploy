package com.example.cmtProject.controller.mes.warehouse;

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
import com.example.cmtProject.service.mes.warehouse.WarehouseMasterService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(PathConstants.API_WAREHOUSE_BASE)
@Slf4j
public class WarehouseMasterRestController {
    
    @Autowired
    private WarehouseMasterService wms;
    
    /**
     * 창고 기준정보 목록 조회 API
     * 
     * @param keyword 검색 키워드 (선택사항)
     * @return 창고 목록 데이터
     */
    @GetMapping(PathConstants.LIST)
    public ApiResponse<List<Map<String, Object>>> getWarehouseList(
            @RequestParam(name = "keyword", required = false) String keyword) {
        
        Map<String, Object> findMap = new HashMap<>();
        // 사용자가 입력한 검색어(keyword)가 null이 아니고, 공백만으로 구성되어 있지 않은 경우에만 실행
        if (keyword != null && !keyword.trim().isEmpty()) {
            // 검색어를 Map에 추가 (이후 검색 조건으로 사용하기 위함)
            findMap.put("keyword", keyword);
        }
        
        log.info("창고 기준정보 목록 조회 요청. 검색어: {}", keyword);
        List<Map<String, Object>> warehouseList = wms.warehouseList(findMap);
        log.info("창고 기준정보 목록 조회 결과: {}건", warehouseList.size());
        
        return ApiResponse.success(warehouseList);
    }
    
    /**
     * 창고 정보 단건 조회 API
     * 
     * @param whsCode 창고 코드
     * @return 창고 정보
     */
    @GetMapping(PathConstants.WAREHOUSE_SINGLE)
    public ApiResponse<Map<String, Object>> getWarehouse(@PathVariable("whsCode") String whsCode) {
        log.info("창고 정보 단건 조회 요청. 창고 코드: {}", whsCode);
        
        Map<String, Object> param = new HashMap<>();
        param.put("WHS_CODE", whsCode);
        
        Map<String, Object> warehouse = wms.warehouseSingle(param);
        
        if (warehouse == null) {
            return ApiResponse.error("해당 창고 정보를 찾을 수 없습니다.");
        }
        
        return ApiResponse.success(warehouse);
    }
    
    /**
     * 창고별 위치 목록 조회 API
     * 
     * @param whsCode 창고 코드
     * @param keyword 검색 키워드 (선택)
     * @return 위치 목록 데이터
     */
    @GetMapping("/locations/{whsCode}")
    public ApiResponse<List<Map<String, Object>>> getLocationList(
            @PathVariable("whsCode") String whsCode,
            @RequestParam(name = "keyword", required = false) String keyword) {
        
        Map<String, Object> findMap = new HashMap<>();
        findMap.put("whsCode", whsCode);
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            findMap.put("keyword", keyword);
        }
        
        log.info("위치 목록 조회 요청. 창고 코드: {}, 검색어: {}", whsCode, keyword);
        
        List<Map<String, Object>> locations = wms.warehouseLocationList(findMap);
        
        log.info("위치 목록 조회 결과: {}건", locations.size());
        
        return ApiResponse.success(locations);
    }
    
    /**
     * 창고 정보 단건 저장 API (등록/수정)
     * 
     * @param warehouseData 창고 정보
     * @return 처리 결과
     */
    @PostMapping("")
    public ApiResponse<Map<String, Object>> saveWarehouse(@RequestBody Map<String, Object> warehouseData) {
        log.info("창고 정보 저장 요청. 데이터: {}", warehouseData);
        
        Map<String, Object> result = wms.saveWarehouse(warehouseData);
        
        if ((Boolean) result.get("success")) {
            return ApiResponse.success(result.get("message").toString(), result);
        } else {
            return ApiResponse.error(result.get("message").toString(), result);
        }
    }
    
    /**
     * 창고 정보 일괄 저장 API
     * 
     * @param requestData 저장할 데이터 목록
     * @return 처리 결과
     */
    @PostMapping(PathConstants.BATCH)
    public ApiResponse<Map<String, Object>> saveBatch(@RequestBody List<Map<String, Object>> requestData) {
        log.info("창고 정보 일괄 저장 요청. 데이터 건수: {}", requestData.size());
        
        Map<String, Object> result = wms.saveBatch(requestData);
        
        if ((Boolean) result.get("success")) {
            return ApiResponse.success(result.get("message").toString(), result);
        } else {
            return ApiResponse.error(result.get("message").toString(), result);
        }
    }
    
    /**
     * 창고 정보 삭제 API
     * 
     * @param whsCode 창고 코드
     * @return 처리 결과
     */
    @DeleteMapping(PathConstants.WAREHOUSE_SINGLE)
    public ApiResponse<Void> deleteWarehouse(@PathVariable("whsCode") String whsCode) {
        log.info("창고 정보 삭제 요청. 창고 코드: {}", whsCode);
        
        Map<String, Object> param = new HashMap<>();
        param.put("WHS_CODE", whsCode);
        
        Map<String, Object> result = wms.deleteWarehouse(param);
        
        if ((Boolean) result.get("success")) {
            return ApiResponse.success(result.get("message").toString(), null);
        } else {
            return ApiResponse.error(result.get("message").toString(), null);
        }
    }
    
    /**
     * 위치정보 저장 API
     * 
     * @param locationData 위치정보 데이터
     * @return 처리 결과
     */
    @PostMapping("/location")
    public ApiResponse<Map<String, Object>> saveLocation(@RequestBody Map<String, Object> locationData) {
        log.info("위치정보 저장 요청. 데이터: {}", locationData);
        
        Map<String, Object> result = wms.saveLocation(locationData);
        
        if ((Boolean) result.get("success")) {
            return ApiResponse.success(result.get("message").toString(), result);
        } else {
            return ApiResponse.error(result.get("message").toString(), result);
        }
    }

    /**
     * 위치정보 일괄 저장 API
     * 
     * @param requestData 저장할 위치정보 목록
     * @return 처리 결과
     */
    @PostMapping("/location/batch")
    public ApiResponse<Map<String, Object>> saveLocationBatch(@RequestBody List<Map<String, Object>> requestData) {
        log.info("위치정보 일괄 저장 요청. 데이터 건수: {}", requestData.size());
        
        Map<String, Object> result = wms.saveLocationBatch(requestData);
        
        if ((Boolean) result.get("success")) {
            return ApiResponse.success(result.get("message").toString(), result);
        } else {
            return ApiResponse.error(result.get("message").toString(), result);
        }
    }

    /**
     * 위치정보 삭제 API
     * 
     * @param locCode 위치 코드
     * @return 처리 결과
     */
    @DeleteMapping("/location/{locCode}")
    public ApiResponse<Void> deleteLocation(@PathVariable("locCode") String locCode) {
        log.info("위치정보 삭제 요청. 위치 코드: {}", locCode);
        
        Map<String, Object> param = new HashMap<>();
        param.put("LOC_CODE", locCode);
        
        Map<String, Object> result = wms.deleteLocation(param);
        
        if ((Boolean) result.get("success")) {
            return ApiResponse.success(result.get("message").toString(), null);
        } else {
            return ApiResponse.error(result.get("message").toString(), null);
        }
    }
}