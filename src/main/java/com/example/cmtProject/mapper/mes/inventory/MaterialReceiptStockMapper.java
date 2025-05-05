package com.example.cmtProject.mapper.mes.inventory;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface MaterialReceiptStockMapper {
    
    /**
     * 입고별 재고 정보 저장
     * 
     * @param params 재고 정보 (receiptNo, mtlCode, remainingQty, receiptDate, createdBy 포함)
     * @return 처리 건수
     */
    public int insertStock(Map<String, Object> params);
    
    /**
     * 자재코드별 입고 재고 목록 조회 (입고일 순)
     * 
     * @param mtlCode 자재 코드
     * @return 입고별 재고 목록
     */
    public List<Map<String, Object>> getStocksByMtlCodeOrderByDate(@Param("mtlCode") String mtlCode);
    
    /**
     * 입고별 재고 차감
     * 
     * @param params 차감 정보 (receiptStockNo, deductQty, updatedBy 포함)
     * @return 처리 건수
     */
    public int deductStock(Map<String, Object> params);
    
    /**
     * 자재의 총 가용 재고 조회
     * 
     * @param mtlCode 자재 코드
     * @return 총 가용 재고
     */
    public Double getTotalAvailableStock(@Param("mtlCode") String mtlCode);
}