package com.example.cmtProject.controller.mes.warehouse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.cmtProject.constants.PathConstants;
import com.example.cmtProject.service.mes.warehouse.WarehouseMasterService;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping(PathConstants.WAREHOUSE_BASE)
@Slf4j
public class WarehouseMasterController {
    
    @Autowired
    private WarehouseMasterService wms;
    
    /**
     * 창고 기준정보 메인페이지
     */
    @GetMapping(PathConstants.VIEW)
    public String warehouseInfoGet(Model model) {
        log.info("창고 기준정보 메인 페이지 요청");
        
        Map<String,Object> findMap = new HashMap<>();
        List<Map<String,Object>> warehouseList = wms.warehouseList(findMap);
        model.addAttribute("warehouseList", warehouseList);
        
        log.info("창고 기준정보 조회 결과: {}건", warehouseList.size());
        
        return PathConstants.VIEW_WAREHOUSE_VIEW;
    }
}