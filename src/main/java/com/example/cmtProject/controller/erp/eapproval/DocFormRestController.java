package com.example.cmtProject.controller.erp.eapproval;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.example.cmtProject.comm.response.ApiResponse;
import com.example.cmtProject.comm.response.ResponseCode;
import com.example.cmtProject.constants.PathConstants;
import com.example.cmtProject.dto.erp.eapproval.DocFormDTO;
import com.example.cmtProject.service.erp.eapproval.DocFormService;
import com.example.cmtProject.comm.exception.DocFormNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;


// 현재 인증관련하여 내 모든 컨트롤러 SecurityContextHolder 와 Principal객체 방식 혼용임 통일필요

/**
 * 문서 양식 관리 REST API 컨트롤러
 * 양식 등록, 수정, 삭제, 조회 관련 API를 제공합니다.
 */
@RestController
@RequestMapping(PathConstants.API_BASE + PathConstants.API_FORMS)
@RequiredArgsConstructor
@Slf4j
public class DocFormRestController {

    private final DocFormService docFormService;

    /**
     * 모든 문서 양식 목록 조회
     */
    @GetMapping
    public ApiResponse<List<DocFormDTO>> getAllForms() {
        log.debug("모든 문서 양식 목록 조회 요청");
        try {
            List<DocFormDTO> forms = docFormService.getAllDocForms();
            return ApiResponse.success("문서 양식 목록 조회 성공", forms);
        } catch (Exception e) {
            log.error("문서 양식 목록 조회 중 오류 발생", e);
            return ApiResponse.error("문서 양식 목록 조회 중 오류가 발생했습니다.", ResponseCode.SERVER_ERROR);
        }
    }

    /**
     * 특정 문서 양식 조회
     */
    @GetMapping("/{formId}")
    public ApiResponse<DocFormDTO> getFormById(@PathVariable("formId") String formId) {
        log.debug("문서 양식 조회 요청: {}", formId);
        try {
            DocFormDTO form = docFormService.getDocFormById(formId);
            return ApiResponse.success("문서 양식 조회 성공", form);
        } catch (DocFormNotFoundException e) {
            log.warn("문서 양식 없음: {}", formId);
            return ApiResponse.error(e.getMessage(), ResponseCode.NOT_FOUND);
        } catch (Exception e) {
            log.error("문서 양식 조회 중 오류 발생", e);
            return ApiResponse.error("문서 양식 조회 중 오류가 발생했습니다.", ResponseCode.SERVER_ERROR);
        }
    }

    /**
     * 문서 양식 저장 (등록/수정)
     */
    @PostMapping
    public ApiResponse<DocFormDTO> saveForm(@RequestBody DocFormDTO formDTO) {
        log.debug("문서 양식 저장 요청: {}", formDTO.getFormId());
        try {
            // 현재 사용자 ID 설정
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String userId = auth.getName();
            
            // 새 양식 등록인 경우 생성자 설정
            if (formDTO.getCreatorId() == null || formDTO.getCreatorId().isEmpty()) {
                formDTO.setCreatorId(userId);
            } else {
                // 기존 양식 수정인 경우 수정자 설정
                formDTO.setUpdaterId(userId);
            }

            DocFormDTO savedForm = docFormService.saveDocForm(formDTO);
            return ApiResponse.success("문서 양식이 저장되었습니다.", savedForm);
        } catch (Exception e) {
            log.error("문서 양식 저장 중 오류 발생", e);
            return ApiResponse.error("문서 양식 저장 중 오류가 발생했습니다: " + e.getMessage(), ResponseCode.SERVER_ERROR);
        }
    }

    /**
     * 문서 양식 삭제
     */
    @DeleteMapping("/{formId}")
    public ApiResponse<Boolean> deleteForm(@PathVariable("formId") String formId) {
        log.debug("문서 양식 삭제 요청: {}", formId);
        try {
            boolean deleted = docFormService.deleteDocForm(formId);
            if (deleted) {
                return ApiResponse.success("문서 양식이 삭제되었습니다.", true);
            } else {
                return ApiResponse.error("문서 양식 삭제에 실패했습니다.", ResponseCode.SERVER_ERROR);
            }
        } catch (DocFormNotFoundException e) {
            log.warn("삭제할 문서 양식 없음: {}", formId);
            return ApiResponse.error(e.getMessage(), ResponseCode.NOT_FOUND);
        } catch (Exception e) {
            log.error("문서 양식 삭제 중 오류 발생", e);
            return ApiResponse.error("문서 양식 삭제 중 오류가 발생했습니다: " + e.getMessage(), ResponseCode.SERVER_ERROR);
        }
    }
}