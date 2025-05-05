package com.example.cmtProject.mapper.mes.inventory;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface MaterialInventoryMapper {
	
	/**
	 * 원자재 재고조회(Map 반환)
	 */
	public List<Map<String, Object>> mInventoryList(Map<String,Object>map);
	
	/**
	 * 자재코드별 재고 정보조회
	 * 
	 */
    public Map<String, Object> getInventoryByMtlCode(String mtlCode);
    
    /**
     * 재고 정보 병합 (추가 또는 업데이트)
     * 같은 자재/창고/위치의 재고가 있으면 수량 증가, 없으면 새로 생성
     * 
     * @param params 재고 정보 (mtlCode, warehouseCode, locationCode, receivedQty, updatedBy 포함)
     * @return 처리 건수
     */
    public int mergeInventory(Map<String, Object> params);
    
    /**
     * 재고 차감 처리
     * 
     * @param params 차감 정보 (mtlCode, consumptionQty, updatedBy 포함)
     * @return 처리 건수
     */
    public int deductInventory(Map<String, Object> params);
    
    /**
     * 재고 정보 등록
     * 
     * @param params 등록할 재고 정보
     * @return 처리 건수
     */
    public int insertInventory(Map<String, Object> params);
    
    /**
     * 재고 정보 수정
     * 
     * @param params 수정할 재고 정보
     * @return 처리 건수
     */
    public int updateInventory(Map<String, Object> params);
    
    
}