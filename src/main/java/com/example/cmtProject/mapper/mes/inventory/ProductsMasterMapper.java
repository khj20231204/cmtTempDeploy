package com.example.cmtProject.mapper.mes.inventory;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 
 *  제품관리 매퍼 인터페이스
 * 
 */

@Mapper
@Repository
public interface ProductsMasterMapper {
	
	/**
	 * 제품 기준정보 목록 조회(Map 반환)
	 */
	public List<Map<String, Object>> selectProducts(Map<String, Object> map);
	
	/**
	 * 제품 기준정보 목록 단건 조회(Map 반환)
	 */
	public Map<String, Object> selectSingleProducts(Map<String, Object> map);
	
	/**
	 * 제품 기준정보 등록(INSERT)(Map 반환)
	 */
	public int insertProducts(Map<String, Object> map);
	
	/**
	 * 제품 기준정보 수정(UPDATE)(Map 반환)
	 */
	public int updateProducts(Map<String, Object> map);
	
	/**
	 * 제품 기준정보 삭제(DELETE)(Map 반환)
	 */
	public int deleteProducts(Map<String, Object> map);
}