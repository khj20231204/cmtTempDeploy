package com.example.cmtProject.mapper.mes.inventory;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 제품 출고관리 매퍼 인터페이스
 * 출고 정보 조회 및 처리를 위한 데이터베이스 접근 메서드를 정의합니다.
 */
@Mapper
@Repository
public interface ProductsIssueMapper {
	
	/**
	 * 출고 목록 조회(Map 반환)
	 * 
	 * @param map 검색 조건
	 * @return 출고 목록
	 */
	public List<Map<String, Object>> pIssueList(Map<String,Object> map);
	
	/**
	 * 제품출고정보 저장
	 * 
	 * @param params 출고 정보
	 * @return 처리 건수
	 */
	public int insertProductsIssue(Map<String, Object> params);
	
    /**
     * 마지막 저장된 출고 번호 조회
     * 
     * @return 출고 번호
     */
    public Long getLastIssueNo();
	
	/**
	 * 출고 상세 정보 조회
	 * 
	 * @param issueNo 출고 번호
	 * @return 출고 상세 정보
	 */
	public Map<String, Object> getIssueDetail(Long issueNo);
	
	/**
	 * 위치 정보 조회
	 * 
	 * @param issueNo 출고 번호
	 * @return 위치 정보 목록
	 */
	public List<Map<String, Object>> getLocationInfo(Long issueNo);
	
	/**
	 * 이력 정보 조회
	 * 
	 * @param issueNo 출고 번호
	 * @return 이력 정보 목록
	 */
	public List<Map<String, Object>> getHistoryInfo(Long issueNo);
	
	/**
	 * 출고 상태 업데이트
	 * 
	 * @param params 업데이트 정보
	 * @return 처리 건수
	 */
	public int updateIssueStatus(Map<String, Object> params);
	
	/**
	 * 출고 상태 및 출고일 업데이트
	 * 
	 * @param params 업데이트 정보
	 * @return 처리 건수
	 */
	public int updateIssueStatusAndDate(Map<String, Object> params);
	
	/**
	 * 출고 가능한 수주 목록 조회
	 * 
	 * @param map 검색 조건
	 * @return 수주 목록
	 */
	public List<Map<String, Object>> salesOrderList(Map<String, Object> map);
	
	/**
	 * 수주 상태별 목록 조회
	 * 
	 * @param map 검색 조건 (status: 수주 상태)
	 * @return 수주 목록
	 */
	public List<Map<String, Object>> salesOrdersByStatus(Map<String, Object> map);
	
	/**
	 * 수주 상태 업데이트
	 * 
	 * @param params 업데이트 정보
	 * @return 처리 건수
	 */
	public int updateSalesOrderStatus(Map<String, Object> params);
}