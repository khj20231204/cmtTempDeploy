package com.example.cmtProject.controller.erp.eapproval;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.cmtProject.constants.DocumentStatus;
import com.example.cmtProject.constants.PathConstants;
import com.example.cmtProject.dto.erp.eapproval.DocumentDTO;
import com.example.cmtProject.dto.erp.eapproval.DocFormDTO;
import com.example.cmtProject.service.erp.eapproval.DocumentService;
import com.example.cmtProject.service.erp.eapproval.DocFormService;
import com.example.cmtProject.service.erp.eapproval.ApprovalProcessService;
import com.example.cmtProject.comm.exception.DocumentAccessDeniedException;
import com.example.cmtProject.comm.exception.DocumentNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.security.Principal;
import java.util.List;

/**
 * 전자결재 화면 컨트롤러
 * 사용자에게 보여질 화면을 처리하는 컨트롤러
 */
@Controller
@RequestMapping(PathConstants.APPROVAL_BASE)
@RequiredArgsConstructor //final 필드나 @NonNull이 붙은 필드만을 포함한 생성자를 자동생성
@Slf4j
public class EapprovalController {

    private final DocumentService documentService;
    private final DocFormService docFormService;
    private final ApprovalProcessService approvalProcessService;

    /**
     * 전자결재 메인 페이지 - 문서함으로 리다이렉트
     */
    @GetMapping
    public String index() {
        return PathConstants.REDIRECT_DOCUMENT_LIST;
    }

    /**
     * 전자결재 문서함 페이지
     */
    @GetMapping(PathConstants.DOCUMENT_LIST)
    public String documentList(Model model, Principal principal) {
        
        String currentUserId = principal.getName();
        log.info("전자결재 문서함 접속: {}", currentUserId);
        
        try {
            // 내가 기안한 문서
            model.addAttribute("myDrafts", documentService.getDrafterDocumentsByEmpId(currentUserId));
            
            // 내가 결재해야 할 문서
            model.addAttribute("pendingDocs", documentService.getPendingDocumentsByEmpId(currentUserId));
            
            // 나와 관련된 진행 중 문서 (내가 기안했거나 결재선에 포함된 문서 중 진행 중인 것)
            model.addAttribute("processingDocs", documentService.getDocumentsByStatusAndRelatedUser(
                    DocumentStatus.PROCESSING, currentUserId));
            
            // 완료된 문서 (내가 관련된 것만)
            model.addAttribute("completedDocs", documentService.getDocumentsByStatusAndRelatedUser(
                    DocumentStatus.COMPLETED, currentUserId));
            
            // 반려된 문서 (내가 관련된 것만)
            model.addAttribute("rejectedDocs", documentService.getDocumentsByStatusAndRelatedUser(
                    DocumentStatus.REJECTED, currentUserId));
            
            return PathConstants.VIEW_DOCUMENT_LIST;
        } catch (Exception e) {
            log.error("문서함 조회 중 오류 발생", e);
            model.addAttribute("errorMessage", "문서 목록을 불러오는 중 오류가 발생했습니다.");
            return PathConstants.VIEW_DOCUMENT_LIST;
        }
    }

    /**
     * 문서 작성 폼 페이지
     */
    @GetMapping(PathConstants.DOCUMENT_NEW)
    public String newDocumentForm(Model model) {
        log.info("새 문서 작성 페이지 접속");
        
        try {
            // 문서 양식 목록 조회
            List<DocFormDTO> forms = docFormService.getAllDocForms();
            
            model.addAttribute("documentDTO", new DocumentDTO());
            model.addAttribute("forms", forms);
            
            return PathConstants.VIEW_DOCUMENT_FORM;
        } catch (Exception e) {
            log.error("문서 작성 폼 로딩 중 오류 발생", e);
            return PathConstants.REDIRECT_DOCUMENT_LIST;
        }
    }

    /**
     * 임시저장 문서 수정 폼 페이지
     */
    @GetMapping(PathConstants.DOCUMENT_EDIT + "/{docId}")
    public String editDocumentForm(@PathVariable("docId") String docId, Model model, Principal principal) {
        log.info("임시저장 문서 수정: {}", docId);
        
        try {
            DocumentDTO document = documentService.getDocumentDetail(docId);
            
            // 문서 접근 권한 확인 (기안자만 수정 가능)
            if (!document.getDrafterId().equals(principal.getName())) {
                throw new DocumentAccessDeniedException("문서 수정 권한이 없습니다.");
            }
            
            // 임시저장 문서만 수정 가능
            if (!"Y".equals(document.getIsTempSaved())) {
                return "redirect:" + PathConstants.APPROVAL_BASE + PathConstants.DOCUMENT_VIEW + "/" + docId;
            }
            
            // 문서 양식 목록 조회
            List<DocFormDTO> forms = docFormService.getAllDocForms();
            
            model.addAttribute("documentDTO", document);
            model.addAttribute("forms", forms);
            
            return PathConstants.VIEW_DOCUMENT_FORM;
        } catch (DocumentNotFoundException | DocumentAccessDeniedException e) {
            log.warn(e.getMessage());
            return PathConstants.REDIRECT_DOCUMENT_LIST;
        } catch (Exception e) {
            log.error("임시저장 문서 수정 폼 로딩 중 오류 발생", e);
            return PathConstants.REDIRECT_DOCUMENT_LIST;
        }
    }

    /**
     * 문서 저장 처리 (임시저장 또는 결재요청)
     * REST API를 통해 처리하므로 여기서는 폼 제출 후 리다이렉트만 처리
     */
    @PostMapping(PathConstants.DOCUMENT_SAVE)
    public String saveDocument(@RequestParam boolean isTempSave, RedirectAttributes redirectAttributes) {
        String message = isTempSave ? "문서가 임시저장되었습니다." : "결재요청이 완료되었습니다.";
        redirectAttributes.addFlashAttribute("successMessage", message);
        
        return PathConstants.REDIRECT_DOCUMENT_LIST;
    }

    /**
     * 문서 조회 페이지
     */
    @GetMapping(PathConstants.DOCUMENT_VIEW + "/{docId}")
    public String viewDocument(@PathVariable("docId") String docId, Model model, Principal principal) {
        log.info("문서 상세 조회: {}", docId);
        
        try {
            DocumentDTO document = documentService.getDocumentDetail(docId);
            String currentUserId = principal.getName();

            // 현재 사용자가 결재 대기 중인 결재자인지 확인
            boolean isCurrentApprover = approvalProcessService.isCurrentApprover(docId, currentUserId);
            
            model.addAttribute("document", document);
            model.addAttribute("isCurrentApprover", isCurrentApprover);
            model.addAttribute("approverId", currentUserId);
            
            return PathConstants.VIEW_DOCUMENT_VIEW;
        } catch (DocumentNotFoundException e) {
            log.warn(e.getMessage());
            return PathConstants.REDIRECT_DOCUMENT_LIST;
        } catch (Exception e) {
            log.error("문서 조회 중 오류 발생", e);
            return PathConstants.REDIRECT_DOCUMENT_LIST;
        }
    }

    /**
     * 결재 처리 (승인/반려)
     * REST API를 통해 처리하므로 여기서는 폼 제출 후 리다이렉트만 처리
     */
    @PostMapping(PathConstants.DOCUMENT_APPROVE)
    public String approveDocument(@PathVariable String docId, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("successMessage", "결재 처리가 완료되었습니다.");
        return "redirect:" + PathConstants.APPROVAL_BASE + PathConstants.PENDING;
    }

    /**
     * 결재 대기 문서함
     */
    @GetMapping(PathConstants.PENDING)
    public String pending(Model model, Principal principal,
                         @RequestParam(required = false) String approvalType,
                         @RequestParam(required = false) String keyword) {
        log.info("결재 대기 문서함 접속: {}", principal.getName());
        
        try {
            List<DocumentDTO> documents = documentService.getProcessableDocumentsByEmpId(principal.getName());
            model.addAttribute("documents", documents);
            model.addAttribute("approvalType", approvalType);
            model.addAttribute("keyword", keyword);
            
            return PathConstants.VIEW_PENDING_LIST;
        } catch (Exception e) {
            log.error("결재 대기 문서 조회 중 오류 발생", e);
            return PathConstants.REDIRECT_DOCUMENT_LIST;
        }
    }

    /**
     * 완료 문서함
     */
    @GetMapping(PathConstants.COMPLETED)
    public String completed(Model model, Principal principal, @RequestParam(required = false) String keyword) {
        log.info("완료 문서함 접속: {}", principal.getName());
        
        try {
            String currentUserId = principal.getName();
            List<DocumentDTO> documents = documentService.getDocumentsByStatusAndRelatedUser(
                    DocumentStatus.COMPLETED, currentUserId);
            
            model.addAttribute("documents", documents);
            model.addAttribute("keyword", keyword);
            
            return PathConstants.VIEW_COMPLETED_LIST;
        } catch (Exception e) {
            log.error("완료 문서 조회 중 오류 발생", e);
            return PathConstants.REDIRECT_DOCUMENT_LIST;
        }
    }

    /**
     * 반려 문서함
     */
    @GetMapping(PathConstants.REJECTED)
    public String rejected(Model model, Principal principal, @RequestParam(required = false) String keyword) {
        log.info("반려 문서함 접속: {}", principal.getName());
        
        try {
            String currentUserId = principal.getName();
            List<DocumentDTO> documents = documentService.getDocumentsByStatusAndRelatedUser(
                    DocumentStatus.REJECTED, currentUserId);
            
            model.addAttribute("documents", documents);
            model.addAttribute("keyword", keyword);
            
            return PathConstants.VIEW_REJECTED_LIST;
        } catch (Exception e) {
            log.error("반려 문서 조회 중 오류 발생", e);
            return PathConstants.REDIRECT_DOCUMENT_LIST;
        }
    }
}