package com.example.cmtProject.mapper.mes.inventory;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 제품 재고관리 매퍼 인터페이스
 * 제품 재고 정보 조회 및 처리를 위한 데이터베이스 접근 메서드를 정의합니다.
 */
@Mapper
@Repository
public interface ProductsInventoryMapper {
	
	/**
	 * 제품 재고조회(Map 반환)
	 * 
	 * @param map 검색 조건
	 * @return 재고 목록
	 */
	public List<Map<String, Object>> pInventoryList(Map<String,Object> map);
	
	/**
	 * 제품코드별 재고 정보조회
	 * 
	 * @param pdtCode 제품 코드
	 * @return 재고 정보
	 */
    public Map<String, Object> getInventoryByPdtCode(String pdtCode);
    
    /**
     * 재고 정보 병합 (추가 또는 업데이트)
     * 같은 제품/창고/위치의 재고가 있으면 수량 증가, 없으면 새로 생성
     * 
     * @param params 재고 정보 (pdtCode, warehouseCode, locationCode, receivedQty, updatedBy 포함)
     * @return 처리 건수
     */
    public int mergeInventory(Map<String, Object> params);
    
    /**
     * 재고 차감 처리
     * 
     * @param params 차감 정보 (pdtCode, consumptionQty, updatedBy 포함)
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