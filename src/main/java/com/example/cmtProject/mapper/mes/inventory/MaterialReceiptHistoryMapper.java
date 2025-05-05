package com.example.cmtProject.mapper.mes.inventory;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface MaterialReceiptHistoryMapper {
    /**
     * 입고 이력 저장
     * 
     * @param params 이력 정보 (receiptNo, actionType, actionDescription, actionUser, createdBy 포함)
     * @return 처리 건수
     */
    public int insertHistory(Map<String, Object> params);
    
    /**
     * 입고번호별 이력 목록 조회
     * 
     * @param receiptNo 입고 번호
     * @return 이력 목록
     */
    public List<Map<String, Object>> getHistoryByReceiptNo(Long receiptNo);
    
}
