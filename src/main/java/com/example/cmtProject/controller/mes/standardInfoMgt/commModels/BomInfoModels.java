package com.example.cmtProject.controller.mes.standardInfoMgt.commModels;

import java.util.List;

import org.hibernate.annotations.NaturalId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.example.cmtProject.entity.mes.standardInfoMgt.LengthUnit;
import com.example.cmtProject.entity.mes.standardInfoMgt.MaterialType;
import com.example.cmtProject.entity.mes.standardInfoMgt.ProductType;
import com.example.cmtProject.entity.mes.standardInfoMgt.WeightUnit;
import com.example.cmtProject.repository.mes.standardInfoMgt.LengthUnitRepository;
import com.example.cmtProject.repository.mes.standardInfoMgt.MaterialTypeRepository;
import com.example.cmtProject.repository.mes.standardInfoMgt.ProductTypeRepository;
import com.example.cmtProject.repository.mes.standardInfoMgt.WeightUnitRepository;

@Service
public class BomInfoModels {
	
	//BOM 단일 코드 단위 
	//SELECT * FROM MATERIAL_TYPE;
	//SELECT * FROM LENGTH_UNIT;
	//SELECT * FROM WEIGHT_UNIT;
	//SELECT * FROM PRODUCT_TYPE;
	
	@Autowired
	private MaterialTypeRepository materialTypeRepository;
	
	@Autowired
	private LengthUnitRepository lengthUnitRepository;
	
	@Autowired
	private WeightUnitRepository weightUnitRepository;
	
	@Autowired
	private ProductTypeRepository productTypeRepository;
	
	public void commonBomInfoModels(Model model) {
		
		//자재 타입
		List<MaterialType> materialTypeList = materialTypeRepository.findAll();
		
		//길이 단위
		List<LengthUnit> lengthUnitList = lengthUnitRepository.findAll();
		
		//무게 단위
		List<WeightUnit> weightUnitList = weightUnitRepository.findAll();
		
		//상품 타입
		List<ProductType> productTypeList = productTypeRepository.findAll();
		
		model.addAttribute("materialTypeList", materialTypeList);
		model.addAttribute("lengthUnitList", lengthUnitList);
		model.addAttribute("weightUnitList", weightUnitList);
		model.addAttribute("productTypeList", productTypeList);
	}
}
