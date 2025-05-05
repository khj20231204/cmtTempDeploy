package com.example.cmtProject.mapper.erp.saleMgt;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.cmtProject.dto.erp.saleMgt.PurchasesOrderEditDTO;
import com.example.cmtProject.dto.erp.saleMgt.PurchasesOrderMainDTO;
import com.example.cmtProject.dto.erp.saleMgt.PurchasesOrderSearchDTO;

@Mapper
public interface PurchasesOrderMapper {

	// 발주 메인 SELECT
	List<PurchasesOrderMainDTO> poMainSelect();

	// 발주 메인 UPDATE
	int poMainUpdate(PurchasesOrderEditDTO poEditDto);

	// 발주 메인 SEARCH
	List<PurchasesOrderMainDTO> poMainSearch(PurchasesOrderSearchDTO searchDto);
	
	// 발주 코드 생성하기 위한 날짜에 해당하는 갯수
	int getNextPoCode(String codeDate);
}
