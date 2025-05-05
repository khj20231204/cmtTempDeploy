package com.example.cmtProject.mapper.erp.saleMgt;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.cmtProject.dto.erp.saleMgt.SalesOrderEditDTO;
import com.example.cmtProject.dto.erp.saleMgt.SalesOrderMainDTO;
import com.example.cmtProject.dto.erp.saleMgt.SalesOrderSearchDTO;

@Mapper
public interface SalesOrderMapper {

	// 수주 메인 SELECT
	List<SalesOrderMainDTO> soMainSelect();
	
	//메인 search
 	List<SalesOrderMainDTO> soMainSearch(SalesOrderSearchDTO searchDto);
 	
 	//메인 Edit(Update)
 	int soMainUpdate(SalesOrderEditDTO soEditDto);
}
