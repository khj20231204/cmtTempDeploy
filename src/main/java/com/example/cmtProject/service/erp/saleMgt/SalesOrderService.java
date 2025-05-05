package com.example.cmtProject.service.erp.saleMgt;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.cmtProject.dto.erp.saleMgt.SalesOrderEditDTO;
import com.example.cmtProject.dto.erp.saleMgt.SalesOrderMainDTO;
import com.example.cmtProject.dto.erp.saleMgt.SalesOrderSearchDTO;
import com.example.cmtProject.mapper.erp.saleMgt.SalesOrderMapper;

@Service
public class SalesOrderService {

	@Autowired
  	private SalesOrderMapper salesOrderMapper;
	
	// 수주 메인 SELECT
	public List<SalesOrderMainDTO> soMainSelect(){
		
		List<SalesOrderMainDTO> soMainList = salesOrderMapper.soMainSelect();
		
		return soMainList;
	}
	
	//메인 search
	public List<SalesOrderMainDTO> soMainSearch(SalesOrderSearchDTO searchDto){
		
		System.out.println("searchDto service:"+ searchDto);
		return salesOrderMapper.soMainSearch(searchDto);
	}
	
	//메인 Edit(Update)
	public int soMainUpdate(SalesOrderEditDTO editDto) {
		
		return salesOrderMapper.soMainUpdate(editDto);
	}
	
	
}
