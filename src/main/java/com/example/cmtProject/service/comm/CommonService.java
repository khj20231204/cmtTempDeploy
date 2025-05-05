package com.example.cmtProject.service.comm;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.cmtProject.dto.comm.CommonCodeDTO;
import com.example.cmtProject.dto.comm.CommonCodeDetailDTO;
import com.example.cmtProject.dto.comm.CommonCodeDetailNameDTO;
import com.example.cmtProject.mapper.common.CommonCodeMapper;
import com.example.cmtProject.repository.comm.CommonCodeDetailRepository;
import com.fasterxml.jackson.databind.ObjectMapper;


import lombok.extern.slf4j.Slf4j;

/**
 * 공통코드 관리 서비스
 */
@Slf4j
@Service
public class CommonService {
	
	@Autowired
	private CommonCodeMapper commonCodeMapper;
	
//    @Autowired
//    @Qualifier("basicObjectMapper")
//    private ObjectMapper objectMapper;
//    
    
    @Autowired
    private CommonCodeDetailRepository commonCodeDetailRepository;
    
    
    /*공통코드 디테일값(디테일코드/네임) 불러오기*/
    public List<CommonCodeDetailNameDTO> getCodeListByGroup(String groupCode) {
		return commonCodeMapper.selectDetailCodeList(groupCode);
	}
    
    
  //공통코드 그룹 가져오기
  	public List<String> getAllGroupCodes() {
  		// TODO Auto-generated method stub
  		return commonCodeMapper.selectGroupList();
  	}
  	
    
    /**
     * 공통코드 목록 조회 (Map 반환)
     * 기존 API와의 호환성을 위한 메서드
     */
    public List<Map<String, Object>> commonList(Map<String, Object> map) {
        log.info("CommonService: commonList 호출. 파라미터: {}", map);
        return commonCodeMapper.commonList(map);
    }
    
    /**
     * 공통코드 상세 목록 조회 (Map 반환)
     * 기존 API와의 호환성을 위한 메서드
     */
    public List<Map<String, Object>> commonDetailList(Map<String, Object> map) {
        log.info("CommonService: commonDetailList 호출. 파라미터: {}", map);
        return commonCodeMapper.commonDetailList(map);
    }
    
    /**
     * 공통코드 목록 조회 (DTO 반환)
     */
    public List<CommonCodeDTO> getCommonCodes(String keyword) {
        log.info("공통코드 목록 조회. 검색어: {}", keyword);
        return commonCodeMapper.selectCommonCodes(keyword);
    }
    
    /**
     * 공통코드 상세 조회
     */
    public CommonCodeDTO getCommonCode(String code) {
        log.info("공통코드 상세 조회. 코드: {}", code);
        return commonCodeMapper.selectCommonCode(code);
    }
    
    /**
     * 상세코드 목록 조회
     */
    public List<CommonCodeDetailDTO> getCommonCodeDetails(String commonCode, String keyword) {
        log.info("상세코드 목록 조회 공통코드: {}, 검색어: {}", commonCode, keyword);
        return commonCodeMapper.selectCommonCodeDetails(commonCode, keyword);
    }
    
    /**
     * 상세코드 단건 조회
     */
    public CommonCodeDetailDTO getCommonCodeDetail(String commonCode, String detailCode) {
        log.info("상세코드 단건 조회. 공통코드: {}, 상세코드: {}", commonCode, detailCode);
        return commonCodeMapper.selectCommonCodeDetail(commonCode, detailCode);
    }
    
    /**
     * 공통코드 등록/수정
     */
    @Transactional
    public boolean saveCommonCode(CommonCodeDTO dto) {
        log.info("공통코드 저장. 데이터: {}", dto);
        
        try {
            // 기존 데이터 확인
            CommonCodeDTO existingCode = commonCodeMapper.selectCommonCode(dto.getCmnCode());
            
            if (existingCode == null) {
                // 등록
                commonCodeMapper.insertCommonCode(dto);
                log.info("공통코드 등록 완료: {}", dto.getCmnCode());
            } else {
                // 수정
                commonCodeMapper.updateCommonCode(dto);
                log.info("공통코드 수정 완료: {}", dto.getCmnCode());
            }
            return true;
        } catch (Exception e) {
            log.error("공통코드 저장 중 오류 발생", e);
            throw e; // 트랜잭션 롤백을 위해 예외 재발생
        }
    }
    
    /**
     * 공통코드 삭제
     */
    @Transactional
    public boolean deleteCommonCode(String code) {
        log.info("공통코드 삭제. 코드: {}", code);
        
        try {
            // 관련 상세코드 모두 삭제 (CASCADE가 설정되어 있어도 명시적으로 수행)
            commonCodeMapper.deleteCommonCodeDetailsByCommonCode(code);
            log.info("공통코드 관련 상세코드 삭제 완료: {}", code);
            
            // 공통코드 삭제
            commonCodeMapper.deleteCommonCode(code);
            log.info("공통코드 삭제 완료: {}", code);
            
            return true;
        } catch (Exception e) {
            log.error("공통코드 삭제 중 오류 발생", e);
            throw e; // 트랜잭션 롤백을 위해 예외 재발생
        }
    }
    
    /**
     * 상세코드 등록/수정
     */
    @Transactional
    public boolean saveCommonCodeDetail(CommonCodeDetailDTO dto) {
        log.info("상세코드 저장. 데이터: {}", dto);
        
        try {
            // 기존 데이터 확인
            CommonCodeDetailDTO existingDetail = commonCodeMapper.selectCommonCodeDetail(
                    dto.getCmnCode(), dto.getCmnDetailCode());
            
            if (existingDetail == null) {
                // 등록
                commonCodeMapper.insertCommonCodeDetail(dto);
                log.info("상세코드 등록 완료: {}-{}", dto.getCmnCode(), dto.getCmnDetailCode());
            } else {
                // 수정
                commonCodeMapper.updateCommonCodeDetail(dto);
                log.info("상세코드 수정 완료: {}-{}", dto.getCmnCode(), dto.getCmnDetailCode());
            }
            return true;
        } catch (Exception e) {
            log.error("상세코드 저장 중 오류 발생", e);
            throw e; // 트랜잭션 롤백을 위해 예외 재발생
        }
    }
    
    /**
     * 상세코드 삭제
     */
    @Transactional
    public boolean deleteCommonCodeDetail(String commonCode, String detailCode) {
        log.info("상세코드 삭제. 공통코드: {}, 상세코드: {}", commonCode, detailCode);
        
        try {
            commonCodeMapper.deleteCommonCodeDetail(commonCode, detailCode);
            log.info("상세코드 삭제 완료: {}-{}", commonCode, detailCode);
            return true;
        } catch (Exception e) {
            log.error("상세코드 삭제 중 오류 발생", e);
            throw e; // 트랜잭션 롤백을 위해 예외 재발생
        }
    }
    
    /**
     * 일괄 저장 처리
     */
    @Transactional
    public boolean saveBatch(List<Map<String, Object>> requestData) {
        log.info("일괄 저장 처리. 데이터 건수: {}", requestData.size());
        
        try {
            for (Map<String, Object> data : requestData) {
                log.info("처리할 데이터: {}", data);
                
                // action 필드 확인 (대소문자 구분 없이)
                String action = (String) getFieldCaseInsensitive(data, "action");
                if (action == null) {
                    log.warn("action 필드가 없는 데이터: {}", data);
                    continue;
                }
                
                // 데이터 구조로 유형 판단 (대소문자 구분 없이)
                boolean hasCmnCode = hasFieldCaseInsensitive(data, "cmnCode");
                boolean hasCmnDetailCode = hasFieldCaseInsensitive(data, "cmnDetailCode");
                
                if (hasCmnCode && !hasCmnDetailCode) {
                    // 공통코드
                    processCommonCode(data, action);
                } else if (hasCmnDetailCode) {
                    // 상세코드
                    processCommonCodeDetail(data, action);
                } else {
                    log.warn("처리할 수 없는 데이터 형식: {}", data);
                }
            }
            return true;
        } catch (Exception e) {
            log.error("일괄 저장 중 오류 발생", e);
            throw e; // 트랜잭션 롤백을 위해 예외 재발생
        }
    }
    
    /**
     * 공통코드 처리 (등록/수정/삭제)
     */
    private void processCommonCode(Map<String, Object> data, String action) {
        log.info("공통코드 처리 시작. 액션: {}", action);
        
        String commonCode = (String) getFieldCaseInsensitive(data, "cmnCode");
        
        if (commonCode == null) {
            log.warn("공통코드가 없는 데이터: {}", data);
            return;
        }
        
        if ("delete".equals(action)) {
            // 삭제
            deleteCommonCode(commonCode);
        } else {
            // 등록/수정
            CommonCodeDTO dto = CommonCodeDTO.builder()
                    .cmnCode(commonCode)
                    .cmnName((String) getFieldCaseInsensitive(data, "cmnName"))
                    .cmnContent((String) getFieldCaseInsensitive(data, "cmnContent"))
                    .cmnCodeIsActive((String) getFieldCaseInsensitive(data, "cmnCodeIsActive"))
                    .build();
            
            // 정렬 순서 처리
            Object sortOrder = getFieldCaseInsensitive(data, "cmnSortOrder");
            if (sortOrder != null) {
                if (sortOrder instanceof Integer) {
                    dto.setCmnSortOrder((Integer) sortOrder);
                } else if (sortOrder instanceof String) {
                    try {
                        if (!((String) sortOrder).isEmpty()) {
                            dto.setCmnSortOrder(Integer.parseInt((String) sortOrder));
                        }
                    } catch (NumberFormatException e) {
                        log.warn("정렬 순서 변환 실패: {}", sortOrder);
                    }
                }
            }
            
            log.info("저장할 공통코드 DTO: {}", dto);
            saveCommonCode(dto);
        }
    }
    
    /**
     * 상세코드 처리 (등록/수정/삭제)
     */
    private void processCommonCodeDetail(Map<String, Object> data, String action) {
        log.info("상세코드 처리 시작. 액션: {}", action);
        
        String commonCode = (String) getFieldCaseInsensitive(data, "cmnCode");
        String detailCode = (String) getFieldCaseInsensitive(data, "cmnDetailCode");
        
        if (commonCode == null || detailCode == null) {
            log.warn("공통코드 또는 상세코드가 없는 데이터: {}", data);
            return;
        }
        
        if ("delete".equals(action)) {
            // 삭제
            deleteCommonCodeDetail(commonCode, detailCode);
        } else {
            // 등록/수정
            CommonCodeDetailDTO dto = CommonCodeDetailDTO.builder()
                    .cmnCode(commonCode)
                    .cmnDetailCode(detailCode)
                    .cmnDetailName((String) getFieldCaseInsensitive(data, "cmnDetailName"))
                    .cmnDetailContent((String) getFieldCaseInsensitive(data, "cmnDetailContent"))
                    .cmnDetailCodeIsActive((String) getFieldCaseInsensitive(data, "cmnDetailCodeIsActive"))
                    .build();
            
            // 정렬 순서 처리
            Object sortOrder = getFieldCaseInsensitive(data, "cmnDetailSortOrder");
            if (sortOrder != null) {
                if (sortOrder instanceof Integer) {
                    dto.setCmnDetailSortOrder((Integer) sortOrder);
                } else if (sortOrder instanceof String) {
                    try {
                        if (!((String) sortOrder).isEmpty()) {
                            dto.setCmnDetailSortOrder(Integer.parseInt((String) sortOrder));
                        }
                    } catch (NumberFormatException e) {
                        log.warn("정렬 순서 변환 실패: {}", sortOrder);
                    }
                }
            }
            
            log.info("저장할 상세코드 DTO: {}", dto);
            saveCommonCodeDetail(dto);
        }
    }
    
    /**
     * 대소문자 구분 없이 맵에서 필드값 가져오기
     * 
     * @param map 데이터 맵
     * @param fieldName 필드명
     * @return 필드값 (없으면 null)
     */
    private Object getFieldCaseInsensitive(Map<String, Object> map, String fieldName) {
        String lowerFieldName = fieldName.toLowerCase();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getKey().toLowerCase().equals(lowerFieldName)) {
                return entry.getValue();
            }
        }
        return null;
    }
    
    /**
     * 대소문자 구분 없이 맵에 필드가 있는지 확인
     * 
     * @param map 데이터 맵
     * @param fieldName 필드명
     * @return 필드 존재 여부
     */
    private boolean hasFieldCaseInsensitive(Map<String, Object> map, String fieldName) {
        String lowerFieldName = fieldName.toLowerCase();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getKey().toLowerCase().equals(lowerFieldName)) {
                return true;
            }
        }
        return false;
    }
    
    
    public CommonCodeDetailDTO getDefaultCode(String group, String code) {
        CommonCodeDetailDTO entity = commonCodeDetailRepository.findByCmnDetailCodeAndCmnCode(group, code);

        return CommonCodeDetailDTO.builder()
            .cmnDetailCode(entity.getCmnDetailCode())
            .cmnCode(entity.getCmnCode())
            .cmnDetailName(entity.getCmnDetailName())
            .build();
    }

    

	
}