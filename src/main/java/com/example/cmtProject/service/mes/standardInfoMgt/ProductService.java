package com.example.cmtProject.service.mes.standardInfoMgt;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.cmtProject.dto.mes.standardInfoMgt.ProductTotalDTO;
import com.example.cmtProject.dto.mes.standardInfoMgt.ProductsEditDTO;
import com.example.cmtProject.entity.mes.standardInfoMgt.Products;
import com.example.cmtProject.mapper.mes.standardInfoMgt.ProductMapper;

@Service
public class ProductService {

	@Autowired
	private ProductMapper productMapper;
	
	// 전체 조회
	public List<Products> list() throws Exception{
		
		List<Products> productList = productMapper.list();
		return productList;
	}
	
    // 선택 조회
	public Products select(String pdtCode) throws Exception{
		
		Products product = productMapper.select(pdtCode);
		return product;
	}
	
    // 등록
	public int insert(Products product) throws Exception{
		
		int result = productMapper.insert(product);
		return result;
	}
	
    // 수정
    public int update(Products product) throws Exception{
    	
    	int result = productMapper.update(product);
		return result;
    }
    
    // 삭제
    public int delete(int pdtCode) throws Exception{
    	
    	int result = productMapper.delete(pdtCode);
		return result;
    }

	public int pdtMainUpdate(ProductsEditDTO pdtEditDto) {
		
		return productMapper.pdtMainUpdate(pdtEditDto);
	}

	public List<ProductTotalDTO> getProductTotalList() {
		
		return productMapper.getProductTotalList();
	}
}
