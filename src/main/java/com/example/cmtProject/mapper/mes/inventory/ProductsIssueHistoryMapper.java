package com.example.cmtProject.mapper.mes.inventory;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 제품 출고 이력 매퍼 인터페이스
 * 출고 이력 관리를 위한 데이터베이스 접근 메서드를 정의합니다.
 */
@Mapper
@Repository
public interface ProductsIssueHistoryMapper {
    /**
     * 출고 이력 저장
     * 
     * @param params 이력 정보 (issueNo, actionType, actionDescription, actionUser, createdBy 포함)
     * @return 처리 건수
     */
    public int insertHistory(Map<String, Object> params);
    
    /**
     * 출고번호별 이력 목록 조회
     * 
     * @param issueNo 출고 번호
     * @return 이력 목록
     */
    public List<Map<String, Object>> getHistoryByIssueNo(Long issueNo);
}