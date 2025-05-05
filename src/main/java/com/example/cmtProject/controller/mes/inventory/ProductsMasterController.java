package com.example.cmtProject.controller.mes.inventory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.cmtProject.constants.PathConstants;
import com.example.cmtProject.service.mes.inventory.ProductsMasterService;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping(PathConstants.PRODUCTS_INFO_BASE)
@Slf4j
public class ProductsMasterController {
    
    @Autowired
    private ProductsMasterService pms;
    
    /**
     * 제품 기준정보 메인페이지
     */
    @GetMapping("")
    public String productsInfoGet(Model model) {
        log.info("제품 기준정보 메인 페이지 요청");
        
        Map<String,Object> findMap = new HashMap<>();
        List<Map<String,Object>> productsList = pms.productsList(findMap);
        model.addAttribute("productsList", productsList);
        
        log.info("제품 기준정보 조회 결과: {}건", productsList.size());
        
        return PathConstants.VIEW_PRODUCTS_INFO;
    }
}