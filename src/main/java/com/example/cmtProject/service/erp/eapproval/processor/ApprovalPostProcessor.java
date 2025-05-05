package com.example.cmtProject.service.erp.eapproval.processor;

import com.example.cmtProject.dto.erp.eapproval.DocumentDTO;

/**
 * 결재 후처리 인터페이스
 * 결재 완료/반려 시 추가 처리를 위한 공통 인터페이스
 */
public interface ApprovalPostProcessor {
    /**
     * 결재 완료 후 처리
     * @param document 완료된 결재 문서
     * @return 처리 성공 여부
     */
	public boolean processApproved(DocumentDTO document);
    
    /**
     * 결재 반려 후 처리
     * @param document 반려된 결재 문서
     * @return 처리 성공 여부
     */
	public boolean processRejected(DocumentDTO document);
    
    /**
     * 이 프로세서가 처리할 수 있는 양식인지 확인
     * @param formId 양식 ID
     * @return 처리 가능 여부
     */
	public boolean canProcess(String formId);
}