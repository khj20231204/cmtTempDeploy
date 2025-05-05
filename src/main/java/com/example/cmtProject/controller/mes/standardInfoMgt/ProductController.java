package com.example.cmtProject.controller.mes.standardInfoMgt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.cmtProject.entity.mes.standardInfoMgt.Products;
import com.example.cmtProject.repository.mes.standardInfoMgt.ProductsRepository;
import com.example.cmtProject.service.mes.standardInfoMgt.ProductService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/pdt")
public class ProductController {

	@Autowired
	private ProductService productService;
	
	@Autowired
	private ProductsRepository productRepository;
	
	@GetMapping("")
	public String main() {
		return "mes/standardInfoMgt/product/pdt";
	}
	
//	@GetMapping("/list")
//    public String list(Model model) throws Exception {
//		
//        List<Products> productList = productService.list();
//        System.out.println("productList:" + productList);
//        model.addAttribute("productList", productList);
//        
//        return "mes/standardInfoMgt/product/list";      
//    }
	
	@PostMapping("/delItems")
	@ResponseBody
	public String pdtDelete(@RequestBody List<Integer> data) {
		
		String visibleType = "N";
		productRepository.updateVisibleByPdtNo(visibleType, data);
		
		return "SUCCESS";
	}
}
