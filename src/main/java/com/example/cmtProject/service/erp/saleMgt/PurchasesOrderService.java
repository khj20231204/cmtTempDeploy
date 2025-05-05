	package com.example.cmtProject.service.erp.saleMgt;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.cmtProject.dto.erp.saleMgt.PurchasesOrderEditDTO;
import com.example.cmtProject.dto.erp.saleMgt.PurchasesOrderMainDTO;
import com.example.cmtProject.dto.erp.saleMgt.PurchasesOrderSearchDTO;
import com.example.cmtProject.dto.erp.saleMgt.SalesOrderEditDTO;
import com.example.cmtProject.mapper.erp.saleMgt.PurchasesOrderMapper;

@Service
public class PurchasesOrderService {

	@Autowired
	private PurchasesOrderMapper purchasesOrderMapper;
	
	// 발주 메인 SELECT
	public List<PurchasesOrderMainDTO> poMainSelect() {
		return purchasesOrderMapper.poMainSelect();
	}

	//메인 Edit(Update)
	public int poMainUpdate(PurchasesOrderEditDTO poEditDto) {
		// TODO Auto-generated method stub
		return purchasesOrderMapper.poMainUpdate(poEditDto);
	}

	public List<PurchasesOrderMainDTO> poMainSearch(PurchasesOrderSearchDTO searchDto) {
		// TODO Auto-generated method stub
		return purchasesOrderMapper.poMainSearch(searchDto);
	}
	
	public int getNextPoCode(String codeDate) {
		return purchasesOrderMapper.getNextPoCode(codeDate);
	}
}
