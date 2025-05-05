package com.example.cmtProject.service.mes.inventory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.example.cmtProject.mapper.mes.inventory.ProductsMasterMapper;
import com.example.cmtProject.util.SecurityUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProductsMasterService {
	
	@Autowired
	private ProductsMasterMapper productsMapper;
	
	/**
	 * 제품 기준정보 목록 조회
	 * @param param 검색 조건
	 * @return 제품 목록
	 */
	public List<Map<String, Object>> productsList(Map<String, Object> param) {
		log.info("제품 기준정보 목록 조회 서비스 호출. 파라미터: {}", param);
		return productsMapper.selectProducts(param);
	}
	
	/**
	 * 제품 기준정보 목록 단건 조회
	 * @param param 조회 조건 (pdtCode 필수)
	 * @return 제품 정보
	 */
	public Map<String, Object> productsSingle(Map<String, Object> param) {
		log.info("제품 기준정보 단건 조회 서비스 호출. 파라미터: {}", param);
		return productsMapper.selectSingleProducts(param);
	}
	
	/**
	 * 제품 정보 저장 (등록/수정)
	 * @param params 저장할 제품 정보
	 * @return 처리 결과
	 */
	@Transactional
	public Map<String, Object> saveProducts(Map<String, Object> params) {
		Map<String, Object> resultMap = new HashMap<>();
		
		try {
			log.info("제품 정보 저장 시작: {}", params);
			
			// 현재 사용자 ID 가져오기
            String userId = SecurityUtil.getUserId();
            
            // 처리자 정보 설정
            params.put("updatedBy", userId);
			
			// 제품 코드로 기존 데이터 조회
			Map<String, Object> existingData = productsSingle(params);
			
			int result = 0;
			
			// 존재하면 수정, 없으면 등록
			if (existingData != null) {
				log.info("제품 정보 수정: {}", params.get("PDT_CODE"));
				result = productsMapper.updateProducts(params);
				
				if (result > 0) {
					resultMap.put("success", true);
					resultMap.put("message", "제품 정보가 수정되었습니다.");
				} else {
					resultMap.put("success", false);
					resultMap.put("message", "제품 정보 수정에 실패했습니다.");
				}
			} else {
				log.info("제품 정보 등록: {}", params.get("PDT_CODE"));
				// 생성자 정보 설정
                params.put("createdBy", userId);
                
				result = productsMapper.insertProducts(params);
				
				if (result > 0) {
					resultMap.put("success", true);
					resultMap.put("message", "제품 정보가 등록되었습니다.");
				} else {
					resultMap.put("success", false);
					resultMap.put("message", "제품 정보 등록에 실패했습니다.");
				}
			}
			
			// 저장 후 데이터 조회
			if (result > 0) {
				Map<String, Object> savedData = productsSingle(params);
				resultMap.put("data", savedData);
			}
			
		} catch (Exception e) {
			log.error("제품 정보 저장 중 오류 발생: {}", e.getMessage(), e);
			resultMap.put("success", false);
			resultMap.put("message", "오류가 발생했습니다: " + e.getMessage());
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		}
		
		return resultMap;
	}
	
	/**
	 * 제품 정보 일괄 저장
	 * @param productsList 저장할 제품 정보 목록
	 * @return 처리 결과
	 */
	@Transactional
	public Map<String, Object> saveBatch(List<Map<String, Object>> productsList) {
		Map<String, Object> resultMap = new HashMap<>();
		int successCount = 0;
		int failCount = 0;
		
		try {
			log.info("제품 정보 일괄 저장 시작: {}건", productsList.size());
			
			// 현재 사용자 ID 가져오기
            String userId = SecurityUtil.getUserId();
            
			for (Map<String, Object> productsData : productsList) {
				try {
				    // 처리자 정보 설정
				    productsData.put("updatedBy", userId);
				    productsData.put("createdBy", userId);
				    
					Map<String, Object> result = saveProducts(productsData);
					
					if ((Boolean) result.get("success")) {
						successCount++;
					} else {
						failCount++;
						log.warn("개별 제품 정보 저장 실패: {}, 사유: {}", 
								productsData.get("PDT_CODE"), result.get("message"));
					}
				} catch (Exception e) {
					failCount++;
					log.error("개별 제품 정보 저장 중 오류: {}", e.getMessage(), e);
				}
			}
			
			if (successCount > 0) {
				resultMap.put("success", true);
				resultMap.put("message", String.format("%d건 저장 완료 (%d건 실패)", successCount, failCount));
			} else {
				resultMap.put("success", false);
				resultMap.put("message", "제품 정보 저장에 실패했습니다.");
			}
			
			resultMap.put("successCount", successCount);
			resultMap.put("failCount", failCount);
			
		} catch (Exception e) {
			log.error("제품 정보 일괄 저장 중 오류 발생: {}", e.getMessage(), e);
			resultMap.put("success", false);
			resultMap.put("message", "오류가 발생했습니다: " + e.getMessage());
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		}
		
		return resultMap;
	}
	
	/**
	 * 제품 정보 삭제
	 * @param params 삭제할 제품 정보 (pdtCode 필수)
	 * @return 처리 결과
	 */
	@Transactional
	public Map<String, Object> deleteProducts(Map<String, Object> params) {
		Map<String, Object> resultMap = new HashMap<>();
		
		try {
			log.info("제품 정보 삭제 시작: {}", params);
			
			// 삭제 전 데이터 존재 여부 확인
			Map<String, Object> existingData = productsSingle(params);
			
			if (existingData == null) {
				resultMap.put("success", false);
				resultMap.put("message", "삭제할 제품 정보를 찾을 수 없습니다.");
				return resultMap;
			}
			
			// 제품 정보 삭제
			int result = productsMapper.deleteProducts(params);
			
			if (result > 0) {
				resultMap.put("success", true);
				resultMap.put("message", "제품 정보가 삭제되었습니다.");
			} else {
				resultMap.put("success", false);
				resultMap.put("message", "제품 정보 삭제에 실패했습니다.");
			}
			
		} catch (Exception e) {
			log.error("제품 정보 삭제 중 오류 발생: {}", e.getMessage(), e);
			resultMap.put("success", false);
			resultMap.put("message", "오류가 발생했습니다: " + e.getMessage());
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		}
		
		return resultMap;
	}
}