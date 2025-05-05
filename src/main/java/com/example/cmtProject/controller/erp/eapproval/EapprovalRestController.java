package com.example.cmtProject.controller.erp.eapproval;

import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.MediaType;

import com.example.cmtProject.comm.response.ApiResponse;
import com.example.cmtProject.comm.response.ResponseCode;
import com.example.cmtProject.constants.ApprovalStatus;
import com.example.cmtProject.constants.DocumentStatus;
import com.example.cmtProject.constants.PathConstants;
import com.example.cmtProject.dto.erp.eapproval.*;
import com.example.cmtProject.dto.erp.employees.EmpListPreviewDTO;
import com.example.cmtProject.entity.erp.employees.Employees;
import com.example.cmtProject.mapper.erp.eapproval.DocumentMapper;
import com.example.cmtProject.service.erp.eapproval.*;
import com.example.cmtProject.service.erp.employees.EmployeesService;
import com.example.cmtProject.util.SecurityUtil;
import com.example.cmtProject.comm.exception.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

/**
 * 전자결재 REST API 컨트롤러 - 리팩토링 버전
 * 일관된 DTO 기반 통신을 제공하는 컨트롤러
 */
@RestController
@RequestMapping(PathConstants.API_BASE)
@RequiredArgsConstructor
@Slf4j
public class EapprovalRestController {

    private final DocumentService documentService;
    private final DocFormService docFormService;
    private final ApprovalProcessService approvalProcessService;
    private final EmployeesService empService;
    private final DocumentMapper documentMapper;
    
    /**
     * 문서 양식 조회 API
     */
    @GetMapping(PathConstants.API_FORM + "/{formId}")
    public ApiResponse<DocFormDTO> getFormContent(@PathVariable("formId") String formId) {
        log.debug("문서 양식 조회 요청: {}", formId);
        try {
            DocFormDTO form = docFormService.getDocFormById(formId);
            return ApiResponse.success(form);
        } catch (DocFormNotFoundException e) {
            return ApiResponse.error(e.getMessage(), ResponseCode.NOT_FOUND);
        } catch (Exception e) {
            log.error("양식 조회 중 오류 발생: {}", e.getMessage(), e);
            return ApiResponse.error("양식 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 문서 상세 조회 API
     */
    @GetMapping(PathConstants.API_DOCUMENT + "/{docId}")
    public ApiResponse<DocumentDTO> getDocumentDetail(@PathVariable("docId") String docId) {
        log.debug("문서 상세 조회 요청: {}", docId);
        try {
            DocumentDTO document = documentService.getDocumentDetail(docId);
            return ApiResponse.success(document);
        } catch (DocumentNotFoundException e) {
            return ApiResponse.error(e.getMessage(), ResponseCode.NOT_FOUND);
        } catch (Exception e) {
            log.error("문서 조회 중 오류 발생: {}", e.getMessage(), e);
            return ApiResponse.error("문서 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 문서 저장 API (임시저장/결재요청)
     */
    @PostMapping(value = PathConstants.API_DOCUMENT)
    public ApiResponse<DocumentDTO> saveDocument(@RequestBody DocumentSaveRequestDTO requestDTO) {
        log.debug("문서 저장 요청: {}", requestDTO);
        
        try {
            // 현재 로그인 사용자 정보 가져오기
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String currentUserId = auth.getName();
            
            // DTO에 현재 사용자 정보 설정
            requestDTO.setDrafterId(currentUserId);
            
            // 문서 저장
            DocumentDTO savedDocument = documentService.saveDocumentWithDTO(requestDTO);
            
            String message = requestDTO.isTempSave() ? 
                    "문서가 임시저장되었습니다." : "결재요청이 완료되었습니다.";
            
            return ApiResponse.success(message, savedDocument);
            
        } catch (Exception e) {
            log.error("문서 저장 중 오류 발생: {}", e.getMessage(), e);
            return ApiResponse.error("문서 저장 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 결재 처리 API (승인/반려)
     */
    @PostMapping(PathConstants.API_DOCUMENT + "/{docId}/process")
    public ApiResponse<Map<String, Object>> processApproval(
            @PathVariable("docId") String docId,
            @RequestBody ApprovalRequestDTO requestDTO) {
        
        log.debug("결재 처리 요청: 문서={}, 결재자={}, 의사결정={}", 
                docId, requestDTO.getApproverId(), requestDTO.getDecision());
        
        try {
            // DTO에 문서 ID 설정
            requestDTO.setDocId(docId);
            
            // 결재 처리 서비스 호출
            boolean isApproved = approvalProcessService.processApproval(requestDTO);
            
            String message = isApproved ? "결재가 승인되었습니다." : "결재가 반려되었습니다.";
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", message);
            result.put("decision", requestDTO.getDecision());
            
            return ApiResponse.success(message, result);
            
        } catch (DocumentNotFoundException | DocumentAccessDeniedException e) {
            return ApiResponse.error(e.getMessage(), ResponseCode.NOT_FOUND);
        } catch (ApprovalProcessException e) {
            return ApiResponse.error(e.getMessage(), ResponseCode.BAD_REQUEST);
        } catch (Exception e) {
            log.error("결재 처리 중 오류 발생: {}", e.getMessage(), e);
            return ApiResponse.error("결재 처리 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 기안 문서 목록 조회 API
     */
    @GetMapping("/drafts")
    public ApiResponse<List<DocumentDTO>> getDrafterDocuments() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUserId = auth.getName();
        
        log.debug("기안 문서 목록 조회 요청: 기안자={}", currentUserId);
        try {
            List<DocumentDTO> documents = documentService.getDrafterDocumentsByEmpId(currentUserId);
            return ApiResponse.success(documents);
        } catch (Exception e) {
            log.error("문서 목록 조회 중 오류 발생: {}", e.getMessage(), e);
            return ApiResponse.error("문서 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 결재 대기 문서 목록 조회 API
     */
    @GetMapping("/pending")
    public ApiResponse<List<DocumentDTO>> getPendingDocuments() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUserId = auth.getName();
        
        log.debug("결재 대기 문서 목록 조회 요청: 결재자={}", currentUserId);
        try {
            List<DocumentDTO> documents = documentService.getProcessableDocumentsByEmpId(currentUserId);
//            List<DocumentDTO> documents = documentService.getPendingDocumentsByEmpId(currentUserId); 삭제처리
            return ApiResponse.success(documents);
        } catch (Exception e) {
            log.error("대기 문서 목록 조회 중 오류 발생: {}", e.getMessage(), e);
            return ApiResponse.error("대기 문서 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 상태별 문서 목록 조회 API
     */
    @GetMapping(PathConstants.API_DOCUMENTS + "/status/{status}")
    public ApiResponse<List<DocumentDTO>> getDocumentsByStatus(@PathVariable("status") String status) {
        // 현재 사용자 ID 가져오기
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUserId = auth.getName();
        
        log.debug("상태별 문서 목록 조회 요청: 상태={}, 사용자={}", status, currentUserId);
        
        try {
            List<DocumentDTO> documents = documentService.getDocumentsByStatusAndRelatedUser(status, currentUserId);
            return ApiResponse.success(documents);
        } catch (Exception e) {
            log.error("문서 목록 조회 중 오류 발생: {}", e.getMessage(), e);
            return ApiResponse.error("문서 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 결재자 목록 조회 API (조직도)
     */
    @GetMapping(PathConstants.API_APPROVERS)
    public ApiResponse<List<EmpListPreviewDTO>> getApprovers() {
        log.debug("결재자 목록 조회 요청");
        try {

            List<EmpListPreviewDTO> employees = documentService.getApproversExceptDrafter();
            
            if (employees == null) {
                return ApiResponse.error("결재자 목록을 가져올 수 없습니다", ResponseCode.SERVER_ERROR);
            }

            return ApiResponse.success(employees);
        } catch (Exception e) {
            log.error("결재자 목록 조회 중 오류 발생: {}", e.getMessage(), e);
            return ApiResponse.error("결재자 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 문서 삭제 API (임시저장 문서만 삭제 가능)
     */
    @DeleteMapping(PathConstants.API_DOCUMENT + "/{docId}")
    public ApiResponse<Boolean> deleteDocument(@PathVariable("docId") String docId) {
        log.debug("문서 삭제 요청: {}", docId);
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String currentUserId = auth.getName();
            
            boolean result = documentService.deleteDocument(docId, currentUserId);
            return ApiResponse.success("문서가 삭제되었습니다.", true);
        } catch (DocumentAccessDeniedException e) {
            return ApiResponse.error(e.getMessage(), ResponseCode.FORBIDDEN);
        } catch (DocumentNotFoundException e) {
            return ApiResponse.error(e.getMessage(), ResponseCode.NOT_FOUND);
        } catch (Exception e) {
            log.error("문서 삭제 중 오류 발생: {}", e.getMessage(), e);
            return ApiResponse.error("문서 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    
    /**
     * 현재 로그인한 사용자 정보 조회 (문서 양식 플레이스홀더용)
     */
    @GetMapping("/template-data")
    public ApiResponse<Map<String, Object>> getTemplateData() {
        try {
            // 현재 로그인 사용자 정보 가져오기
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String currentUserId = auth.getName();
            
            // 사용자 정보 조회
            Map<String, Object> templateData = new HashMap<>();
            
            // 기본 정보
            templateData.put("DRAFTER_ID", currentUserId);
            
            // 사용자 이름 - SecurityUtil 사용
            String empName = SecurityUtil.getUserName();
            templateData.put("DRAFTER_NAME", empName);
            
            // 부서 정보 - documentService 사용
            String deptCode = documentService.getEmployeeDeptCodeByEmpId(currentUserId);
            templateData.put("DRAFT_DEPT", deptCode);
            
            // 부서명 조회
            String deptName = documentMapper.selectDeptNameByDeptCode(deptCode);
            templateData.put("DRAFT_DEPT_NAME", deptName);
            
            // 직위 정보 조회 - DB 조회
            Employees employee = SecurityUtil.getCurrentUser();
            Long positionNo = employee != null ? employee.getPositionNo() : null;
            String positionName = "";
            if (positionNo != null) {
                positionName = documentMapper.selectPositionNameByPositionNo(positionNo);
            }
            templateData.put("POSITION_NAME", positionName);
            
            // 날짜 관련 정보
            LocalDateTime now = LocalDateTime.now();
            String currentDate = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            templateData.put("DRAFT_DATE", currentDate);
            
            return ApiResponse.success(templateData);
        } catch (Exception e) {
            log.error("템플릿 데이터 조회 중 오류 발생: {}", e.getMessage(), e);
            return ApiResponse.error("템플릿 데이터 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}