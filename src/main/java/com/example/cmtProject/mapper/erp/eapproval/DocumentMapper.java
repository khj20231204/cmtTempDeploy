package com.example.cmtProject.mapper.erp.eapproval;

import com.example.cmtProject.dto.erp.eapproval.DocumentDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 전자결재 문서 매퍼 인터페이스
 * 문서 관련 데이터베이스 작업을 처리
 */
@Mapper
public interface DocumentMapper {
    /**
     * 문서 저장
     * @param document 저장할 문서 정보
     */
	public void insertDocument(DocumentDTO document);
    
    /**
     * 문서 정보 수정
     * @param document 수정할 문서 정보
     */
	public void updateDocument(DocumentDTO document);
    
    /**
     * 문서 상태 변경
     * @param docId 문서 ID
     * @param docStatus 변경할 문서 상태
     */
	public void updateDocumentStatus(@Param("docId") String docId, 
                              @Param("docStatus") String docStatus);
    
    /**
     * 문서 결재일자 업데이트
     * @param docId 문서 ID
     * @param approvalDate 결재일자
     */
	public void updateApprovalDate(@Param("docId") String docId, 
                           @Param("approvalDate") LocalDateTime approvalDate);
    
    /**
     * 문서 조회
     * @param docId 문서 ID
     * @return 문서 정보
     */
	public DocumentDTO selectDocumentById(String docId);
    
    /**
     * 기안자별 문서 목록 조회
     * @param drafterId 기안자 ID(사번)
     * @return 문서 목록
     */
	public List<DocumentDTO> selectDocumentsByDrafterId(String drafterId);
    
    /**
     * 기안자 및 상태별 문서 목록 조회
     * @param drafterId 기안자 ID(사번)
     * @param status 문서 상태
     * @return 문서 목록
     */
	public List<DocumentDTO> selectDocumentsByDrafterAndStatus(
            @Param("drafterId") String drafterId, 
            @Param("status") String status);
    
    /**
     * 상태별 문서 목록 조회
     * @param docStatus 문서 상태 ------사용자 없으면 삭제
     * @return 문서 목록
     */
	public List<DocumentDTO> selectDocumentsByStatus(String docStatus);
	
	
	/**
	 * 상태별 문서 목록 조회 (특정 사용자 관련 문서만)
	 * @param docStatus 문서 상태
	 * @param userId 사용자 ID
	 * @return 문서 목록
	 */
	List<DocumentDTO> selectDocumentsByStatusAndRelatedUser(
	        @Param("docStatus") String docStatus, 
	        @Param("userId") String userId);
    
    /**
     * 결재자별 대기 문서 목록 조회
     * @param approverId 결재자 ID(사번)
     * @return 문서 목록
     */
	public List<DocumentDTO> selectPendingDocumentsByApproverId(String approverId);
    
    /**
     * 문서 번호 자동 생성을 위한 시퀀스 조회
     * @return 다음 시퀀스 값
     */
	public int selectDocumentSequence();
    
    /**
     * 문서 삭제 (임시저장 문서만)
     * @param docId 문서 ID
     * @return 삭제된 행 수
     */
	public int deleteDocument(String docId);
    
    /**
     * 직원 ID(사번)의 부서 코드 조회
     * @param empId 직원 ID(사번)
     * @return 부서 코드
     */
	public String selectEmployeeDeptCodeByEmpId(String empId);
	
    /**
     * 결재 가능한 문서 목록 조회 (결재 순서 고려)
     * 첫 번째 결재자이거나 이전 결재자가 모두 승인한 문서만 조회
     * 
     * @param approverId 결재자 ID(사번)
     * @return 결재 가능한 문서 목록
     */
	public List<DocumentDTO> selectProcessableDocumentsByApproverId(String approverId);
    
    /**
     * 부서 코드로 부서명 조회
     */
	public String selectDeptNameByDeptCode(String deptCode);

    /**
     * 직위 번호로 직위명 조회 
     */
	public String selectPositionNameByPositionNo(Long positionNo);

	public int selectCount(String empId);

	public int myDraftCount(String empId);
	
	
}