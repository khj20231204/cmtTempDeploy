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
import com.example.cmtProject.service.mes.inventory.MaterialMasterService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(PathConstants.API_MATERIAL_INFO_BASE)
@Slf4j
public class MaterialMasterRestController {
    
    @Autowired
    private MaterialMasterService mms;
    
    /**
     * 원자재 기준정보 목록 조회 API
     * 
     * @param keyword 검색 키워드 (선택사항)
     * @return 원자재 목록 데이터
     */
    @GetMapping(PathConstants.LIST)
    public ApiResponse<List<Map<String, Object>>> getMaterialList(
            @RequestParam(name = "keyword", required = false) String keyword) {
        
        Map<String, Object> findMap = new HashMap<>();
        // 사용자가 입력한 검색어(keyword)가 null이 아니고, 공백만으로 구성되어 있지 않은 경우에만 실행
        if (keyword != null && !keyword.trim().isEmpty()) {
            // 검색어를 Map에 추가 (이후 검색 조건으로 사용하기 위함)
            findMap.put("keyword", keyword);
        }
        
        log.info("원자재 기준정보 목록 조회 요청. 검색어: {}", keyword);
        List<Map<String, Object>> materialList = mms.materialList(findMap);
        log.info("원자재 기준정보 목록 조회 결과: {}건", materialList.size());
        
        return ApiResponse.success(materialList);
    }
    
    /**
     * 원자재 정보 단건 조회 API
     * 
     * @param mtlCode 원자재 코드
     * @return 원자재 정보
     */
    @GetMapping(PathConstants.MATERIAL_INFO_SINGLE)
    public ApiResponse<Map<String, Object>> getMaterial(@PathVariable("mtlCode") String mtlCode) {
        log.info("원자재 정보 단건 조회 요청. 원자재 코드: {}", mtlCode);
        
        Map<String, Object> param = new HashMap<>();
        param.put("MTL_CODE", mtlCode);
        
        Map<String, Object> material = mms.materialSingle(param);
        
        if (material == null) {
            return ApiResponse.error("해당 원자재 정보를 찾을 수 없습니다.");
        }
        
        return ApiResponse.success(material);
    }
    
    /**
     * 원자재 정보 단건 저장 API (등록/수정)
     * 
     * @param materialData 원자재 정보
     * @return 처리 결과
     */
    @PostMapping("")
    public ApiResponse<Map<String, Object>> saveMaterial(@RequestBody Map<String, Object> materialData) {
        log.info("원자재 정보 저장 요청. 데이터: {}", materialData);
        
        Map<String, Object> result = mms.saveMaterial(materialData);
        
        if ((Boolean) result.get("success")) {
            return ApiResponse.success(result.get("message").toString(), result);
        } else {
            return ApiResponse.error(result.get("message").toString(), result);
        }
    }
    
    /**
     * 원자재 정보 일괄 저장 API
     * 
     * @param requestData 저장할 데이터 목록
     * @return 처리 결과
     */
    @PostMapping(PathConstants.BATCH)
    public ApiResponse<Map<String, Object>> saveBatch(@RequestBody List<Map<String, Object>> requestData) {
        log.info("원자재 정보 일괄 저장 요청. 데이터 건수: {}", requestData.size());
        
        Map<String, Object> result = mms.saveBatch(requestData);
        
        if ((Boolean) result.get("success")) {
            return ApiResponse.success(result.get("message").toString(), result);
        } else {
            return ApiResponse.error(result.get("message").toString(), result);
        }
    }
    
    /**
     * 원자재 정보 삭제 API
     * 
     * @param mtlCode 원자재 코드
     * @return 처리 결과
     */
    @DeleteMapping(PathConstants.MATERIAL_INFO_SINGLE)
    public ApiResponse<Void> deleteMaterial(@PathVariable("mtlCode") String mtlCode) {
        log.info("원자재 정보 삭제 요청. 원자재 코드: {}", mtlCode);
        
        Map<String, Object> param = new HashMap<>();
        // 프론트에서 바로 정보를 받아옴으로 인해 카멜이 아닌 스네이크로 적어야함
        param.put("MTL_CODE", mtlCode);
        
        Map<String, Object> result = mms.deleteMaterial(param);
        
        if ((Boolean) result.get("success")) {
            return ApiResponse.success(result.get("message").toString(), null);
        } else {
            return ApiResponse.error(result.get("message").toString(), null);
        }
    }
}