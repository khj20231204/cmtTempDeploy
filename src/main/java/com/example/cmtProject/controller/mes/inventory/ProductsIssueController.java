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
import com.example.cmtProject.service.mes.inventory.ProductsIssueService;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping(PathConstants.PRODUCTSISSUE_BASE)
@Slf4j
public class ProductsIssueController {
    
    @Autowired
    private ProductsIssueService pis;
    
    /**
     * 제품 출고 메인페이지
     */
    @GetMapping(PathConstants.VIEW)
    public String productsIssueGET(Model model) {
        Map<String, Object> findMap = new HashMap<>();
        List<Map<String, Object>> pIssueList = pis.issueList(findMap);
        model.addAttribute("pIssueList", pIssueList);
        
        return PathConstants.VIEW_PRODUCTSISSUE_VIEW;
    }
}