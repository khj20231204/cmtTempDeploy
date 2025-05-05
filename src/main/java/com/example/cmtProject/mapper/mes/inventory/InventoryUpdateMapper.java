package com.example.cmtProject.mapper.mes.inventory;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface InventoryUpdateMapper {
    
    /**
     *입고 상태 업데이트
     */
    public int updateReceiptStatus(Map<String, Object> params);
    
    /**
     *출고 상태 업데이트 
     */
    public int updateIssueStatus(Map<String, Object> params);
    
    /**
     * 판매주문에 따른 BOM 항목 목록 조회
     * @param params soCode - 판매주문 코드
     * @return BOM 항목 목록 (PARENT_PDT_CODE, BOM_QTY, ITEM_TYPE)
     */
    public List<Map<String, Object>> getBomItems(Map<String, Object> params);
    
    /**
     * 자재 재고 할당 수량 업데이트
     * @param params mtlCode - 자재 코드, allocatedQty - 할당할 수량, updatedBy - 수정자
     * @return 업데이트된 행 수
     */
    public int updateMaterialAllocatedQty(Map<String, Object> params);
    
    /**
     * 제품 재고 할당 수량 업데이트
     * @param params soCode - 판매주문 코드, soQty - 주문 수량, updatedBy - 수정자
     * @return 업데이트된 행 수
     */
    public int updateProductAllocatedQty(Map<String, Object> params);
    
    
    
    /**
     * IpiService 에 updateIpiInspectionStatusComplete 메서드에 추가해주세요
     * params.put("pdtName", ipiDTO.getPdtName()); -  반제품 및 완제품 이름
        params.put("pdtCode", ipiDTO.getPdtCode()); - 반제품 및 완제품 코드
        params.put("woQty", ipiDTO.getWoQty()); - 개수
        params.put("childLotCode", ipiDTO.getChildLotCode()); - LOT 넘버

     */
}