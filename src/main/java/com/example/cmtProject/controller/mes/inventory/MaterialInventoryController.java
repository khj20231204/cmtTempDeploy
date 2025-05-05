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
import com.example.cmtProject.service.mes.inventory.MaterialInventoryService;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping(PathConstants.MATERIALINVENTORY_BASE)
@Slf4j
public class MaterialInventoryController {
    
    @Autowired
    private MaterialInventoryService mis;
    
    /**
     * 원자재 재고 메인페이지
     */
    @GetMapping(PathConstants.VIEW)
    public String materialInventoryGet(Model model) {
        Map<String,Object> findMap = new HashMap<>();
        List<Map<String,Object>> mInventoryList = mis.inventoryList(findMap);
        model.addAttribute("mInventoryList", mInventoryList);
        
        return PathConstants.VIEW_METINVENTORY_VIEW;
    }
}