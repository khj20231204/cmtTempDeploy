package com.example.cmtProject.service.mes.warehouse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.example.cmtProject.mapper.mes.warehouse.WarehouseMasterMapper;
import com.example.cmtProject.util.SecurityUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WarehouseMasterService {
    
    @Autowired
    private WarehouseMasterMapper warehouseMapper;
    
    /**
     * 창고 기준정보 목록 조회
     * @param param 검색 조건
     * @return 창고 목록
     */
    public List<Map<String, Object>> warehouseList(Map<String, Object> param) {
        log.info("창고 기준정보 목록 조회 서비스 호출. 파라미터: {}", param);
        return warehouseMapper.selectWarehouses(param);
    }
    
    /**
     * 창고 기준정보 목록 단건 조회
     * @param param 조회 조건 (whsCode 필수)
     * @return 창고 정보
     */
    public Map<String, Object> warehouseSingle(Map<String, Object> param) {
        log.info("창고 기준정보 단건 조회 서비스 호출. 파라미터: {}", param);
        return warehouseMapper.selectSingleWarehouse(param);
    }
    
    /**
     * 창고 정보 저장 (등록/수정)
     * @param params 저장할 창고 정보
     * @return 처리 결과
     */
    @Transactional
    public Map<String, Object> saveWarehouse(Map<String, Object> params) {
        Map<String, Object> resultMap = new HashMap<>();
        
        try {
            log.info("창고 정보 저장 시작: {}", params);
            
            // 현재 사용자 ID 가져오기
            String userId = SecurityUtil.getUserId();
            
            // 처리자 정보 설정
            params.put("updatedBy", userId);
            
            // 창고 코드로 기존 데이터 조회
            Map<String, Object> existingData = warehouseSingle(params);
            
            int result = 0;
            
            // 존재하면 수정, 없으면 등록
            if (existingData != null) {
                log.info("창고 정보 수정: {}", params.get("WHS_CODE"));
                result = warehouseMapper.updateWarehouse(params);
                
                if (result > 0) {
                    resultMap.put("success", true);
                    resultMap.put("message", "창고 정보가 수정되었습니다.");
                } else {
                    resultMap.put("success", false);
                    resultMap.put("message", "창고 정보 수정에 실패했습니다.");
                }
            } else {
                log.info("창고 정보 등록: {}", params.get("WHS_CODE"));
                // 생성자 정보 설정
                params.put("createdBy", userId);
                
                result = warehouseMapper.insertWarehouse(params);
                
                if (result > 0) {
                    resultMap.put("success", true);
                    resultMap.put("message", "창고 정보가 등록되었습니다.");
                } else {
                    resultMap.put("success", false);
                    resultMap.put("message", "창고 정보 등록에 실패했습니다.");
                }
            }
            
            // 저장 후 데이터 조회
            if (result > 0) {
                Map<String, Object> savedData = warehouseSingle(params);
                resultMap.put("data", savedData);
            }
            
        } catch (Exception e) {
            log.error("창고 정보 저장 중 오류 발생: {}", e.getMessage(), e);
            resultMap.put("success", false);
            resultMap.put("message", "오류가 발생했습니다: " + e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        
        return resultMap;
    }
    
    /**
     * 창고 정보 일괄 저장
     * @param warehouseList 저장할 창고 정보 목록
     * @return 처리 결과
     */
    @Transactional
    public Map<String, Object> saveBatch(List<Map<String, Object>> warehouseList) {
        Map<String, Object> resultMap = new HashMap<>();
        int successCount = 0;
        int failCount = 0;
        
        try {
            log.info("창고 정보 일괄 저장 시작: {}건", warehouseList.size());
            
            // 현재 사용자 ID 가져오기
            String userId = SecurityUtil.getUserId();
            
            for (Map<String, Object> warehouseData : warehouseList) {
                try {
                    // 처리자 정보 설정
                    warehouseData.put("updatedBy", userId);
                    warehouseData.put("createdBy", userId);
                    
                    Map<String, Object> result = saveWarehouse(warehouseData);
                    
                    if ((Boolean) result.get("success")) {
                        successCount++;
                    } else {
                        failCount++;
                        log.warn("개별 창고 정보 저장 실패: {}, 사유: {}", 
                                warehouseData.get("WHS_CODE"), result.get("message"));
                    }
                } catch (Exception e) {
                    failCount++;
                    log.error("개별 창고 정보 저장 중 오류: {}", e.getMessage(), e);
                }
            }
            
            if (successCount > 0) {
                resultMap.put("success", true);
                resultMap.put("message", String.format("%d건 저장 완료 (%d건 실패)", successCount, failCount));
            } else {
                resultMap.put("success", false);
                resultMap.put("message", "창고 정보 저장에 실패했습니다.");
            }
            
            resultMap.put("successCount", successCount);
            resultMap.put("failCount", failCount);
            
        } catch (Exception e) {
            log.error("창고 정보 일괄 저장 중 오류 발생: {}", e.getMessage(), e);
            resultMap.put("success", false);
            resultMap.put("message", "오류가 발생했습니다: " + e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        
        return resultMap;
    }
    
    /**
     * 창고 정보 삭제
     * @param params 삭제할 창고 정보 (whsCode 필수)
     * @return 처리 결과
     */
    @Transactional
    public Map<String, Object> deleteWarehouse(Map<String, Object> params) {
        Map<String, Object> resultMap = new HashMap<>();
        
        try {
            log.info("창고 정보 삭제 시작: {}", params);
            
            // 삭제 전 데이터 존재 여부 확인
            Map<String, Object> existingData = warehouseSingle(params);
            
            if (existingData == null) {
                resultMap.put("success", false);
                resultMap.put("message", "삭제할 창고 정보를 찾을 수 없습니다.");
                return resultMap;
            }
            
            // 창고 정보 삭제
            int result = warehouseMapper.deleteWarehouse(params);
            
            if (result > 0) {
                resultMap.put("success", true);
                resultMap.put("message", "창고 정보가 삭제되었습니다.");
            } else {
                resultMap.put("success", false);
                resultMap.put("message", "창고 정보 삭제에 실패했습니다.");
            }
            
        } catch (Exception e) {
            log.error("창고 정보 삭제 중 오류 발생: {}", e.getMessage(), e);
            resultMap.put("success", false);
            resultMap.put("message", "오류가 발생했습니다: " + e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        
        return resultMap;
    }
    
    /**
     * 창고별 위치 목록 조회
     * @param param 검색 조건 (whsCode 필수)
     * @return 위치 목록
     */
    public List<Map<String, Object>> warehouseLocationList(Map<String, Object> param) {
        log.info("창고별 위치 목록 조회 서비스 호출. 파라미터: {}", param);
        return warehouseMapper.selectWarehouseLocations(param);
    }
    
    /**
     * 위치정보 저장 (등록/수정)
     * @param params 저장할 위치정보
     * @return 처리 결과
     */
    @Transactional
    public Map<String, Object> saveLocation(Map<String, Object> params) {
        Map<String, Object> resultMap = new HashMap<>();
        
        try {
            log.info("위치정보 저장 시작: {}", params);
            
            // 현재 사용자 ID 가져오기
            String userId = SecurityUtil.getUserId();
            
            // 처리자 정보 설정
            params.put("updatedBy", userId);
            
            // 위치코드로 기존 데이터 조회
            Map<String, Object> findMap = new HashMap<>();
            findMap.put("LOC_CODE", params.get("LOC_CODE"));
            
            Map<String, Object> existingData = warehouseMapper.selectSingleLocation(findMap);
            
            int result = 0;
            
            // 존재하면 수정, 없으면 등록
            if (existingData != null) {
                log.info("위치정보 수정: {}", params.get("LOC_CODE"));
                result = warehouseMapper.updateLocation(params);
                
                if (result > 0) {
                    resultMap.put("success", true);
                    resultMap.put("message", "위치정보가 수정되었습니다.");
                } else {
                    resultMap.put("success", false);
                    resultMap.put("message", "위치정보 수정에 실패했습니다.");
                }
            } else {
                log.info("위치정보 등록: {}", params.get("LOC_CODE"));
                // 생성자 정보 설정
                params.put("createdBy", userId);
                
                result = warehouseMapper.insertLocation(params);
                
                if (result > 0) {
                    resultMap.put("success", true);
                    resultMap.put("message", "위치정보가 등록되었습니다.");
                } else {
                    resultMap.put("success", false);
                    resultMap.put("message", "위치정보 등록에 실패했습니다.");
                }
            }
            
            // 저장 후 데이터 조회
            if (result > 0) {
                Map<String, Object> savedData = warehouseMapper.selectSingleLocation(findMap);
                resultMap.put("data", savedData);
            }
            
        } catch (Exception e) {
            log.error("위치정보 저장 중 오류 발생: {}", e.getMessage(), e);
            resultMap.put("success", false);
            resultMap.put("message", "오류가 발생했습니다: " + e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        
        return resultMap;
    }
    
    /**
     * 위치정보 일괄 저장
     * @param locationList 저장할 위치정보 목록
     * @return 처리 결과
     */
    @Transactional
    public Map<String, Object> saveLocationBatch(List<Map<String, Object>> locationList) {
        Map<String, Object> resultMap = new HashMap<>();
        int successCount = 0;
        int failCount = 0;
        
        try {
            log.info("위치정보 일괄 저장 시작: {}건", locationList.size());
            
            // 현재 사용자 ID 가져오기
            String userId = SecurityUtil.getUserId();
            
            for (Map<String, Object> locationData : locationList) {
                try {
                    // 처리자 정보 설정
                    locationData.put("updatedBy", userId);
                    locationData.put("createdBy", userId);
                    
                    Map<String, Object> result = saveLocation(locationData);
                    
                    if ((Boolean) result.get("success")) {
                        successCount++;
                    } else {
                        failCount++;
                        log.warn("개별 위치정보 저장 실패: {}, 사유: {}", 
                                locationData.get("LOC_CODE"), result.get("message"));
                    }
                } catch (Exception e) {
                    failCount++;
                    log.error("개별 위치정보 저장 중 오류: {}", e.getMessage(), e);
                }
            }
            
            if (successCount > 0) {
                resultMap.put("success", true);
                resultMap.put("message", String.format("%d건 저장 완료 (%d건 실패)", successCount, failCount));
            } else {
                resultMap.put("success", false);
                resultMap.put("message", "위치정보 저장에 실패했습니다.");
            }
            
            resultMap.put("successCount", successCount);
            resultMap.put("failCount", failCount);
            
        } catch (Exception e) {
            log.error("위치정보 일괄 저장 중 오류 발생: {}", e.getMessage(), e);
            resultMap.put("success", false);
            resultMap.put("message", "오류가 발생했습니다: " + e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        
        return resultMap;
    }
    
    /**
     * 위치정보 삭제
     * @param params 삭제할 위치정보 (locCode 필수)
     * @return 처리 결과
     */
    @Transactional
    public Map<String, Object> deleteLocation(Map<String, Object> params) {
        Map<String, Object> resultMap = new HashMap<>();
        
        try {
            log.info("위치정보 삭제 시작: {}", params);
            
            // 삭제 전 데이터 존재 여부 확인
            Map<String, Object> existingData = warehouseMapper.selectSingleLocation(params);
            
            if (existingData == null) {
                resultMap.put("success", false);
                resultMap.put("message", "삭제할 위치정보를 찾을 수 없습니다.");
                return resultMap;
            }
            
            // 위치정보 삭제
            int result = warehouseMapper.deleteLocation(params);
            
            if (result > 0) {
                resultMap.put("success", true);
                resultMap.put("message", "위치정보가 삭제되었습니다.");
            } else {
                resultMap.put("success", false);
                resultMap.put("message", "위치정보 삭제에 실패했습니다.");
            }
            
        } catch (Exception e) {
            log.error("위치정보 삭제 중 오류 발생: {}", e.getMessage(), e);
            resultMap.put("success", false);
            resultMap.put("message", "오류가 발생했습니다: " + e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        
        return resultMap;
    }
}