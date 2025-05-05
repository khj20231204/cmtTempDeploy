package com.example.cmtProject.mapper.common;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.stereotype.Repository;

import com.example.cmtProject.dto.comm.CommonCodeDTO;
import com.example.cmtProject.dto.comm.CommonCodeDetailDTO;
import com.example.cmtProject.dto.comm.CommonCodeDetailNameDTO;

/**
 * 공통코드 관리 매퍼 인터페이스
 */
@Mapper
@Repository
public interface CommonCodeMapper {
    
    /**
     * 공통코드 목록 조회 (Map 반환)
     */
	public List<Map<String, Object>> commonList(Map<String, Object> map);
    
    /**
     * 공통코드 상세 목록 조회 (Map 반환)
     */
	public List<Map<String, Object>> commonDetailList(Map<String, Object> map);
    
    /**
     * 공통코드 목록 조회 (DTO 반환)
     */
	public List<CommonCodeDTO> selectCommonCodes(@Param("keyword") String keyword);
    
    /**
     * 공통코드 단건 조회
     */
	public CommonCodeDTO selectCommonCode(@Param("code") String code);
    
    /**
     * 공통코드 등록
     */
	public int insertCommonCode(CommonCodeDTO dto);
    
    /**
     * 공통코드 수정
     */
	public int updateCommonCode(CommonCodeDTO dto);
    
    /**
     * 공통코드 삭제
     */
	public int deleteCommonCode(@Param("code") String code);
    
    /**
     * 상세코드 목록 조회
     */
	public List<CommonCodeDetailDTO> selectCommonCodeDetails(
	            @Param("commonCode") String commonCode,
	            @Param("keyword") String keyword);
    
    /**
     * 상세코드 단건 조회
     */
	public CommonCodeDetailDTO selectCommonCodeDetail(
	            @Param("commonCode") String commonCode,
	            @Param("detailCode") String detailCode);
    
    /**
     * 상세코드 등록
     */
	public int insertCommonCodeDetail(CommonCodeDetailDTO dto);
    
    /**
     * 상세코드 수정
     */
	public int updateCommonCodeDetail(CommonCodeDetailDTO dto);
    
    /**
     * 상세코드 삭제
     */
	public int deleteCommonCodeDetail(
	            @Param("commonCode") String commonCode,
	            @Param("detailCode") String detailCode);
    
    /**
     * 공통코드에 속한 모든 상세코드 삭제
     */
	public int deleteCommonCodeDetailsByCommonCode(@Param("commonCode") String commonCode);
	
	//공통코드 디테일리스트 불러오기 - hymm
	public List<CommonCodeDetailNameDTO> selectDetailCodeList(String groupCode);
	
	//그룹코드 가져오기
	public List<String> selectGroupList();
	
}//CommonCodeMapper
