package com.example.cmtProject.mapper.mes.warehouse;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 창고관리 매퍼 인터페이스
 */
@Mapper
@Repository
public interface WarehouseMasterMapper {
    
    /**
     * 창고 기준정보 목록 조회(Map 반환)
     */
    public List<Map<String, Object>> selectWarehouses(Map<String, Object> map);
    
    /**
     * 창고 기준정보 목록 단건 조회(Map 반환)
     */
    public Map<String, Object> selectSingleWarehouse(Map<String, Object> map);
    
    /**
     * 창고 기준정보 등록(INSERT)(Map 반환)
     */
    public int insertWarehouse(Map<String, Object> map);
    
    /**
     * 창고 기준정보 수정(UPDATE)(Map 반환)
     */
    public int updateWarehouse(Map<String, Object> map);
    
    /**
     * 창고 기준정보 삭제(DELETE)(Map 반환)
     */
    public int deleteWarehouse(Map<String, Object> map);
    
    /**
     * 창고별 위치 목록 조회(Map 반환)
     */
    public List<Map<String, Object>> selectWarehouseLocations(Map<String, Object> map);
    
    /**
     * 위치정보 단건 조회(Map 반환)
     */
    public Map<String, Object> selectSingleLocation(Map<String, Object> map);
    
    /**
     * 위치정보 등록(INSERT)(Map 반환)
     */
    public int insertLocation(Map<String, Object> map);
    
    /**
     * 위치정보 수정(UPDATE)(Map 반환)
     */
    public int updateLocation(Map<String, Object> map);
    
    /**
     * 위치정보 삭제(DELETE)(Map 반환)
     */
    public int deleteLocation(Map<String, Object> map);
}