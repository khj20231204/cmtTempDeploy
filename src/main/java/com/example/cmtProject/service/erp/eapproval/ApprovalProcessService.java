package com.example.cmtProject.service.erp.eapproval;

import com.example.cmtProject.constants.ApprovalStatus;
import com.example.cmtProject.constants.DocumentStatus;
import com.example.cmtProject.dto.erp.eapproval.ApprovalLineDTO;
import com.example.cmtProject.dto.erp.eapproval.ApprovalRequestDTO;
import com.example.cmtProject.dto.erp.eapproval.DocumentDTO;
import com.example.cmtProject.mapper.erp.eapproval.ApprovalLineMapper;
import com.example.cmtProject.mapper.erp.eapproval.DocumentMapper;
import com.example.cmtProject.comm.exception.ApprovalProcessException;
import com.example.cmtProject.service.erp.eapproval.processor.ApprovalPostProcessManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
/**
 * 결재 프로세스 처리 서비스
 * 결재 승인/반려 등 결재 프로세스와 관련된 비즈니스 로직을 담당
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ApprovalProcessService {

    private final DocumentMapper documentMapper;
    private final ApprovalLineMapper approvalLineMapper;
    private final ApprovalPostProcessManager postProcessManager;
    
    /**
     * 결재 처리 (승인/반려)
     */
    @Transactional
    public boolean processApproval(ApprovalRequestDTO requestDTO) {
        log.debug("결재 처리: 문서={}, 결재자={}, 결정={}", 
                requestDTO.getDocId(), requestDTO.getApproverId(), requestDTO.getDecision());
        
        // 결재자의 결재라인 조회
        ApprovalLineDTO approvalLine = approvalLineMapper.selectApprovalLineByDocIdAndApproverId(
                requestDTO.getDocId(), requestDTO.getApproverId());
        
        if (approvalLine == null) {
            throw new ApprovalProcessException("결재 권한이 없습니다");
        }
        
        // 이미 처리된 결재인지 확인
        if (!ApprovalStatus.PENDING.equals(approvalLine.getApprovalStatus())) {
            throw new ApprovalProcessException("이미 처리된 결재입니다");
        }
        
        if (ApprovalStatus.REJECTED.equals(requestDTO.getDecision())) {
            // 반려 시 의견 필수 확인
            if (requestDTO.getComment() == null || requestDTO.getComment().trim().isEmpty()) {
                throw new ApprovalProcessException("반려 시 의견을 입력해야 합니다");
            }
            
            // 결재 처리
            approvalLine.setApprovalStatus(ApprovalStatus.REJECTED);
            approvalLine.setApprovalComment(requestDTO.getComment());
            approvalLine.setApprovalDate(LocalDateTime.now());
            approvalLineMapper.updateApprovalLine(approvalLine);
            
            // 문서 상태 변경
            documentMapper.updateDocumentStatus(requestDTO.getDocId(), DocumentStatus.REJECTED);
            log.info("문서 반려 처리 완료: {}", requestDTO.getDocId());
            
            // 반려 후처리 호출 (별도 트랜잭션으로 처리)
            processPostProcessing(requestDTO.getDocId(), DocumentStatus.REJECTED);
            
            return false; // 반려
        } else {
            // 승인 처리
            approvalLine.setApprovalStatus(ApprovalStatus.APPROVED);
            approvalLine.setApprovalComment(requestDTO.getComment());
            approvalLine.setApprovalDate(LocalDateTime.now());
            approvalLineMapper.updateApprovalLine(approvalLine);
            
            // 다음 결재자 처리
            processNextApprover(requestDTO.getDocId());
            
            return true; // 승인
        }
    }
    
    /**
     * 다음 결재자 처리
     */
    private void processNextApprover(String docId) {
        // 다음 결재자 확인
        List<ApprovalLineDTO> nextApprovers = approvalLineMapper.selectNextApproversByDocId(docId);
        
        // 다음 결재자가 없으면 결재 완료 처리
        if (nextApprovers.isEmpty()) {
            documentMapper.updateDocumentStatus(docId, DocumentStatus.COMPLETED);
            documentMapper.updateApprovalDate(docId, LocalDateTime.now());
            log.info("문서 최종 승인 완료: {}", docId);
            
            // 완료 후처리 호출 (별도 트랜잭션으로 처리)
            processPostProcessing(docId, DocumentStatus.COMPLETED);
        } else {
            log.debug("다음 결재자 대기 중: {}명", nextApprovers.size());
        }
    }
    
    // 후처리 로직을 별도 트랜잭션으로 분리하여 실행
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processPostProcessing(String docId, String status) {
        try {
            DocumentDTO document = documentMapper.selectDocumentById(docId);
            if (document != null) {
                log.info("문서 후처리 시작: {}, 상태: {}", docId, status);
                boolean result = postProcessManager.executePostProcessing(document);
                log.info("문서 후처리 완료: {}, 성공: {}", docId, result);
            } else {
                log.warn("후처리를 위한 문서를 찾을 수 없음: {}", docId);
            }
        } catch (Exception e) {
            log.error("후처리 중 오류 발생 (무시됨): {}", e.getMessage(), e);
            // 후처리 실패해도 메인 트랜잭션은 영향 없음
        }
    }

    
    /**
     * 현재 결재자 여부 확인
     * 특정 결재자가 해당 문서의 현재 결재자인지 확인
     * 이전 결재자가 모두 승인한 경우에만 현재 결재자로 간주
     */
    public boolean isCurrentApprover(String docId, String approverId) {
        // 1. 해당 문서의 결재선 조회
        List<ApprovalLineDTO> approvalLines = approvalLineMapper.selectApprovalLinesByDocId(docId);
        
        // 2. 해당 결재자의 결재선 정보 확인
        ApprovalLineDTO currentApproval = null;
        int currentApproverIndex = -1;
        
        for (int i = 0; i < approvalLines.size(); i++) {
            ApprovalLineDTO line = approvalLines.get(i);
            if (line.getApproverId().equals(approverId)) {
                currentApproval = line;
                currentApproverIndex = i;
                break;
            }
        }
        
        // 결재선에 해당 결재자가 없는 경우
        if (currentApproval == null || !ApprovalStatus.PENDING.equals(currentApproval.getApprovalStatus())) {
            return false;
        }
        
        // 첫 번째 결재자인 경우 바로 처리 가능
        if (currentApproverIndex == 0) {
            return true;
        }
        
        // 이전 결재자들이 모두 승인한 경우에만 현재 결재자로 간주
        for (int i = 0; i < currentApproverIndex; i++) {
            ApprovalLineDTO prevLine = approvalLines.get(i);
            // 이전 결재자 중 대기 상태가 있으면 현재 결재자가 아님
            if (!ApprovalStatus.APPROVED.equals(prevLine.getApprovalStatus())) {
                return false;
            }
        }
        
        // 이전 결재자가 모두 승인했으면 현재 결재자임
        return true;
    }
    
    /**
     * 결재선 생성
     * 문서에 결재선을 추가하는 로직
     */
    @Transactional
    public void createApprovalLines(String docId, List<ApprovalLineDTO> approvalLines) {
        if (approvalLines == null || approvalLines.isEmpty()) {
            log.warn("결재선이 비어있습니다: {}", docId);
            return;
        }
        
        log.debug("결재선 생성: 문서={}, 결재선 수={}", docId, approvalLines.size());
        
        // 이전 결재선 삭제
        approvalLineMapper.deleteApprovalLinesByDocId(docId);
        
        // 새 결재선 생성
        for (ApprovalLineDTO line : approvalLines) {
            line.setDocId(docId);
            line.setApprovalStatus(ApprovalStatus.PENDING);
            approvalLineMapper.insertApprovalLine(line);
        }
    }
}