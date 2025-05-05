package com.example.cmtProject.mapper.mes.inventory;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 
 *  원자재관리 매퍼 인터페이스
 * 
 */

@Mapper
@Repository
public interface MaterialMasterMapper {
	
	/**
	 * 원자재 기준정보 목록 조회(Map 반환)
	 */
	public List<Map<String, Object>> selectMaterials(Map<String, Object> map);
	
	/**
	 * 원자재 기준정보 목록 단건 조회(Map 반환)
	 */
	public Map<String, Object> selectSingleMaterials(Map<String, Object> map);
	
	/**
	 * 원자재 기준정보 등록(INSERT)(Map 반환)
	 */
	public int insertMaterials(Map<String, Object> map);
	
	/**
	 * 원자재 기준정보 수정(UPDATE)(Map 반환)
	 */
	public int updateMaterials(Map<String, Object> map);
	
	/**
	 * 원자재 기준정보 삭제(DELETE)(Map 반환)
	 */
	public int deleteMaterials(Map<String, Object> map);
	
	
	
}
