package com.example.cmtProject.service.erp.eapproval.processor;

import com.example.cmtProject.constants.DocumentStatus;
import com.example.cmtProject.dto.erp.eapproval.DocumentDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
/**
 * 결재 후처리 관리자
 * 결재 완료/반려 시 적절한 후처리기를 찾아 실행
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ApprovalPostProcessManager {

    private final List<ApprovalPostProcessor> processors;
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean executePostProcessing(DocumentDTO document) {
        if (document == null) {
            log.warn("후처리 요청 문서가 null입니다.");
            return false;
        }
        
        String docStatus = document.getDocStatus();
        String formId = document.getFormId();
        
        log.info("결재 문서 후처리 시작: 문서ID={}, 상태={}, 양식={}", 
                document.getDocId(), docStatus, formId);
        
        // 처리 가능한 프로세서 찾기
        ApprovalPostProcessor processor = findProcessor(formId);
        if (processor == null) {
            log.info("문서 양식에 맞는 후처리기를 찾을 수 없습니다: {}", formId);
            return false;
        }
        
        // 문서 상태에 따라 처리
        try {
            if (DocumentStatus.COMPLETED.equals(docStatus)) {
                log.info("결재 완료 후처리 실행: {}", document.getDocId());
                return processor.processApproved(document);
            } else if (DocumentStatus.REJECTED.equals(docStatus)) {
                log.info("결재 반려 후처리 실행: {}", document.getDocId());
                return processor.processRejected(document);
            } else {
                log.warn("처리 대상이 아닌 문서 상태입니다: {}", docStatus);
                return false;
            }
        } catch (Exception e) {
            log.error("결재 후처리 중 오류 발생: {}", e.getMessage(), e);
            return false;
        }
    }
    /**
     * 양식에 맞는 후처리기 찾기
     */
    private ApprovalPostProcessor findProcessor(String formId) {
        return processors.stream()
                .filter(processor -> processor.canProcess(formId))
                .findFirst()
                .orElse(null);
    }
}