package com.example.cmtProject.mapper.mes.inventory;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface MaterialReceiptMapper {
    
    /**
     * 발주 리스트 조회(Map 반환)
     * 
     * @param map 검색 조건
     * @return 발주 리스트
     */
    public List<Map<String, Object>> puchasesList(Map<String,Object> map);
    
    /**
     * 입고 리스트 조회(Map 반환)
     * 
     * @param map 검색 조건
     * @return 입고 리스트
     */
    public List<Map<String, Object>> mReceiptList(Map<String,Object> map);
    
    /**
     * 입고정보 저장
     * 
     * @param params 입고 정보
     * @return 처리 건수
     */
    public int insertMaterialReceipt(Map<String, Object> params);
    
    /**
     * 마지막 저장된 입고 번호 조회
     * 
     * @return 입고 번호
     */
    public Long getLastReceiptNo();
    
    /**
     * 입고 상세 정보 조회
     * 
     * @param receiptNo 입고 번호
     * @return 입고 상세 정보
     */
    public Map<String, Object> getReceiptDetail(Long receiptNo);
    
    /**
     * 검수 정보 조회
     * 
     * @param receiptNo 입고 번호
     * @return 검수 정보
     */
    public Map<String, Object> getInspectionInfo(Long receiptNo);
    
    /**
     * LOT 정보 조회
     * 
     * @param receiptNo 입고 번호
     * @return LOT 정보 목록
     */
    public List<Map<String, Object>> getLotInfo(Long receiptNo);
    
    /**
     * 위치 정보 조회
     * 
     * @param receiptNo 입고 번호
     * @return 위치 정보 목록
     */
    public List<Map<String, Object>> getLocationInfo(Long receiptNo);
    
    /**
     * 이력 정보 조회
     * 
     * @param receiptNo 입고 번호
     * @return 이력 정보 목록
     */
    public List<Map<String, Object>> getHistoryInfo(Long receiptNo);
    
    /**
     * 입고 상태 업데이트
     * 
     * @param params 업데이트 정보
     * @return 처리 건수
     */
    public int updateReceiptStatus(Map<String, Object> params);
    
    /**
     * 입고 상태 및 입고일 업데이트
     * 
     * @param params 업데이트 정보
     * @return 처리 건수
     */
    public int updateReceiptStatusAndDate(Map<String, Object> params);
    
    /**
     * 입고 상태 및 창고/위치 정보 업데이트
     * 
     * @param params 업데이트 정보
     * @return 처리 건수
     */
    public int updateReceiptStatusAndLocation(Map<String, Object> params);
    
    /**
     * 입고 이력 정보 삽입
     * 
     * @param params 이력 정보
     * @return 처리 건수
     */
    public int insertReceiptHistory(Map<String, Object> params);
    
    /**
     * 재고 정보 업데이트
     * 
     * @param params 재고 정보
     * @return 처리 건수
     */
    public int updateMaterialInventory(Map<String, Object> params);
}