package com.example.cmtProject.controller.comm;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.cmtProject.comm.response.ApiResponse;
import com.example.cmtProject.dto.comm.CommonCodeDTO;
import com.example.cmtProject.dto.comm.CommonCodeDetailDTO;
import com.example.cmtProject.service.comm.CommonService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * 공통코드 관리 REST API 컨트롤러
 * 현재 프론트에서 배치로 보내기에 단건조회는 사용하지 않으나 테스트등을 위해 남겨둠
 * 추후 삭제는 생각이 필요함.
 * 
 */
@RestController
@RequestMapping("/api/common/codes")
@Slf4j
public class CommonCodeRestController {
	
	@Autowired
	private CommonService commonService;
	
//    @Autowired
//    @Qualifier("basicObjectMapper")
//    private ObjectMapper objectMapper;


    /**
     * 공통코드 목록 조회
     * 
     * @param keyword 검색어 (선택)
     * @return 공통코드 목록
     */
    @GetMapping
    public ApiResponse<List<CommonCodeDTO>> getCommonCodes(
            @RequestParam(name = "keyword", required = false) String keyword) {
        log.info("공통코드 목록 조회 요청. 검색어: {}", keyword);
        
        List<CommonCodeDTO> commonCodes = commonService.getCommonCodes(keyword);
        
        return ApiResponse.success(commonCodes);
    }

    /**
     * 특정 공통코드 조회
     * 
     * @param code 공통코드
     * @return 공통코드 정보
     */
    @GetMapping("/{code}")
    public ApiResponse<CommonCodeDTO> getCommonCode(@PathVariable("code") String code) {
        log.info("공통코드 단건 조회 요청. 코드: {}", code);
        
        CommonCodeDTO commonCode = commonService.getCommonCode(code);
        
        if (commonCode == null) {
            return ApiResponse.error("공통코드를 찾을 수 없습니다: " + code);
        }
        
        return ApiResponse.success(commonCode);
    }

    /**
     * 공통코드 단건 저장 (등록/수정)
     * 
     * @param dto 공통코드 정보
     * @return 처리 결과
     */
    @PostMapping
    public ApiResponse<CommonCodeDTO> saveCommonCode(@RequestBody CommonCodeDTO dto) {
        log.info("공통코드 저장 요청. 데이터: {}", dto);
        
        boolean result = commonService.saveCommonCode(dto);
        
        if (result) {
            // 저장 후 최신 데이터 조회하여 반환
            CommonCodeDTO savedCode = commonService.getCommonCode(dto.getCmnCode());
            return ApiResponse.success("공통코드가 저장되었습니다.", savedCode);
        } else {
            return ApiResponse.error("공통코드 저장 중 오류가 발생했습니다.");
        }
    }

    /**
     * 공통코드 일괄 저장 API
     * 
     * @param requestData 저장할 데이터 목록
     * @return 처리 결과
     */
    @PostMapping("/batch")
    public ApiResponse<Void> saveBatch(@RequestBody List<Map<String, Object>> requestData) {
        log.info("일괄 저장 요청. 데이터 건수: {}", requestData.size());
        
        // 요청 데이터의 상세 내용 로깅
        for (int i = 0; i < requestData.size(); i++) {
            log.info("요청 데이터 [{}]: {}", i, requestData.get(i));
        }
        
        boolean result = commonService.saveBatch(requestData);
        
        if (result) {
            return ApiResponse.success("일괄 저장이 완료되었습니다.", null);
        } else {
            return ApiResponse.error("일괄 저장 중 오류가 발생했습니다.");
        }
    }

    /**
     * 공통코드 삭제
     * 
     * @param code 공통코드
     * @return 처리 결과
     */
    @DeleteMapping("/{code}")
    public ApiResponse<Void> deleteCommonCode(@PathVariable("code") String code) {
        log.info("공통코드 삭제 요청. 코드: {}", code);
        
        // 삭제 전 코드 존재 여부 확인
        CommonCodeDTO commonCode = commonService.getCommonCode(code);
        if (commonCode == null) {
            return ApiResponse.error("공통코드를 찾을 수 없습니다: " + code);
        }
        
        boolean result = commonService.deleteCommonCode(code);
        
        if (result) {
            return ApiResponse.success("공통코드가 삭제되었습니다.", null);
        } else {
            return ApiResponse.error("공통코드 삭제 중 오류가 발생했습니다.");
        }
    }

    /**
     * 상세코드 목록 조회
     * 
     * @param code 공통코드
     * @param keyword 검색어 (선택)
     * @return 상세코드 목록
     */
    @GetMapping("/{code}/details")
    public ApiResponse<List<CommonCodeDetailDTO>> getCommonCodeDetails(
            @PathVariable("code") String code,
            @RequestParam(name = "keyword", required = false) String keyword) {
        log.info("상세코드 목록 조회 요청. 공통코드: {}, 검색어: {}", code, keyword);
        
        // 공통코드 존재 여부 확인
        CommonCodeDTO commonCode = commonService.getCommonCode(code);
        if (commonCode == null) {
            return ApiResponse.error("공통코드를 찾을 수 없습니다: " + code);
        }
        
        List<CommonCodeDetailDTO> details = commonService.getCommonCodeDetails(code, keyword);
        
        return ApiResponse.success(details);
    }

    /**
     * 특정 상세코드 조회
     * 
     * @param code 공통코드
     * @param detailCode 상세코드
     * @return 상세코드 정보
     */
    @GetMapping("/{code}/details/{detailCode}")
    public ApiResponse<CommonCodeDetailDTO> getCommonCodeDetail(
            @PathVariable("code") String code,
            @PathVariable("detailCode") String detailCode) {
        log.info("상세코드 단건 조회 요청. 공통코드: {}, 상세코드: {}", code, detailCode);
        
        CommonCodeDetailDTO detail = commonService.getCommonCodeDetail(code, detailCode);
        
        if (detail == null) {
            return ApiResponse.error("상세코드를 찾을 수 없습니다: " + code + "/" + detailCode);
        }
        
        return ApiResponse.success(detail);
    }

    /**
     * 상세코드 단건 저장 (등록/수정)
     * 
     * @param code 공통코드
     * @param dto 상세코드 정보
     * @return 처리 결과
     */
    @PostMapping("/{code}/details")
    public ApiResponse<CommonCodeDetailDTO> saveCommonCodeDetail(
            @PathVariable("code") String code,
            @RequestBody CommonCodeDetailDTO dto) {
        log.info("상세코드 저장 요청. 공통코드: {}, 데이터: {}", code, dto);
        
        // 공통코드 존재 여부 확인
        CommonCodeDTO commonCode = commonService.getCommonCode(code);
        if (commonCode == null) {
            return ApiResponse.error("공통코드를 찾을 수 없습니다: " + code);
        }
        
        // 요청된 공통코드와 DTO의 공통코드가 일치하는지 확인
        if (!code.equals(dto.getCmnCode())) {
            dto.setCmnCode(code); // 경로의 코드로 덮어쓰기
        }
        
        boolean result = commonService.saveCommonCodeDetail(dto);
        
        if (result) {
            // 저장 후 최신 데이터 조회하여 반환
            CommonCodeDetailDTO savedDetail = commonService.getCommonCodeDetail(code, dto.getCmnDetailCode());
            return ApiResponse.success("상세코드가 저장되었습니다.", savedDetail);
        } else {
            return ApiResponse.error("상세코드 저장 중 오류가 발생했습니다.");
        }
    }

    /**
     * 상세코드 일괄 저장 API (새로 추가)
     * 
     * @param code 공통코드
     * @param dtoList 상세코드 정보 리스트
     * @return 처리 결과
     */
    @PostMapping("/{code}/details/batch")
    public ApiResponse<Void> saveCommonCodeDetailBatch(
            @PathVariable("code") String code,
            @RequestBody List<CommonCodeDetailDTO> dtoList) {
        log.info("상세코드 일괄 저장 요청. 공통코드: {}, 데이터 건수: {}", code, dtoList.size());
        
        // 공통코드 존재 여부 확인
        CommonCodeDTO commonCode = commonService.getCommonCode(code);
        if (commonCode == null) {
            return ApiResponse.error("공통코드를 찾을 수 없습니다: " + code);
        }
        
        // 요청 데이터의 상세 내용 로깅
        for (int i = 0; i < dtoList.size(); i++) {
            log.info("요청 데이터 [{}]: {}", i, dtoList.get(i));
        }
        
        boolean allSuccess = true;
        
        // 각 상세코드 저장 처리
        for (CommonCodeDetailDTO dto : dtoList) {
            // 공통코드 자동 설정
            dto.setCmnCode(code);
            
            try {
                boolean result = commonService.saveCommonCodeDetail(dto);
                if (!result) {
                    allSuccess = false;
                }
            } catch (Exception e) {
                log.error("상세코드 저장 중 오류 발생: {}", dto, e);
                allSuccess = false;
            }
        }
        
        if (allSuccess) {
            return ApiResponse.success("상세코드가 일괄 저장되었습니다.", null);
        } else {
            return ApiResponse.error("일부 상세코드 저장 중 오류가 발생했습니다.");
        }
    }

    /**
     * 상세코드 삭제
     * 
     * @param code 공통코드
     * @param detailCode 상세코드
     * @return 처리 결과
     */
    @DeleteMapping("/{code}/details/{detailCode}")
    public ApiResponse<Void> deleteCommonCodeDetail(
            @PathVariable("code") String code,
            @PathVariable("detailCode") String detailCode) {
        log.info("상세코드 삭제 요청. 공통코드: {}, 상세코드: {}", code, detailCode);
        
        // 상세코드 존재 여부 확인
        CommonCodeDetailDTO detail = commonService.getCommonCodeDetail(code, detailCode);
        if (detail == null) {
            return ApiResponse.error("상세코드를 찾을 수 없습니다: " + code + "/" + detailCode);
        }
        
        boolean result = commonService.deleteCommonCodeDetail(code, detailCode);
        
        if (result) {
            return ApiResponse.success("상세코드가 삭제되었습니다.", null);
        } else {
            return ApiResponse.error("상세코드 삭제 중 오류가 발생했습니다.");
        }
    }
}