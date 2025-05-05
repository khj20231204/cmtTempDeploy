package com.example.cmtProject.controller.erp.eapproval;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.cmtProject.constants.PathConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 문서 양식 관리 컨트롤러
 * 양식 관리 화면 처리
 */
@Controller
@RequestMapping(PathConstants.APPROVAL_BASE + PathConstants.FORM_BASE)
@RequiredArgsConstructor
@Slf4j
public class DocFormController {

    /**
     * 문서 양식 목록 페이지
     */
    @GetMapping
    public String formList() {
        log.info("문서 양식 목록 페이지 접속");
        return PathConstants.VIEW_FORM_LIST;
    }

    /**
     * 문서 양식 등록 페이지
     */
    @GetMapping(PathConstants.FORM_NEW)
    public String newForm() {
        log.info("문서 양식 등록 페이지 접속");
        return PathConstants.VIEW_FORM_EDIT;
    }

    /**
     * 문서 양식 수정 페이지
     */
    @GetMapping(PathConstants.FORM_EDIT + "/{formId}")
    public String editForm(@PathVariable("formId") String formId, Model model) {
        log.info("문서 양식 수정 페이지 접속: {}", formId);
        model.addAttribute("formId", formId);
        return PathConstants.VIEW_FORM_EDIT;
    }

    /**
     * 문서 양식 상세 조회 페이지
     */
    @GetMapping(PathConstants.FORM_VIEW + "/{formId}")
    public String viewForm(@PathVariable("formId") String formId, Model model) {
        log.info("문서 양식 상세 조회 페이지 접속: {}", formId);
        model.addAttribute("formId", formId);
        return PathConstants.VIEW_FORM_VIEW;
    }
}