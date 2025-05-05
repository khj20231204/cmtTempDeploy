package com.example.cmtProject.service.erp.eapproval;

import com.example.cmtProject.constants.ApprovalStatus;
import com.example.cmtProject.constants.DocumentStatus;
import com.example.cmtProject.dto.erp.eapproval.DocumentDTO;
import com.example.cmtProject.dto.erp.eapproval.DocumentSaveRequestDTO;
import com.example.cmtProject.dto.erp.employees.EmpListPreviewDTO;
import com.example.cmtProject.dto.erp.eapproval.ApprovalLineDTO;
import com.example.cmtProject.mapper.erp.eapproval.DocumentMapper;
import com.example.cmtProject.service.erp.employees.EmployeesService;
import com.example.cmtProject.mapper.erp.eapproval.ApprovalLineMapper;
import com.example.cmtProject.comm.exception.DocumentAccessDeniedException;
import com.example.cmtProject.comm.exception.DocumentNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentService {

    private final DocumentMapper documentMapper;
    private final ApprovalLineMapper approvalLineMapper;
    private final ApprovalProcessService approvalProcessService;
    private final ObjectMapper objectMapper;
    private final EmployeesService empService;

    /**
     * DTO를 이용한 문서 저장
     * 
     * @param requestDTO 저장 요청 DTO
     * @return 저장된 문서 정보
     */
    @Transactional
    public DocumentDTO saveDocumentWithDTO(DocumentSaveRequestDTO requestDTO) throws Exception {
        log.info("문서 저장 시작: 임시저장={}, 제목={}", requestDTO.isTempSave(), requestDTO.getTitle());
        
        // 문서 DTO 생성
        DocumentDTO documentDTO = new DocumentDTO();
        
        // 문서 ID가 없으면 새로 생성
        if (requestDTO.getDocId() == null || requestDTO.getDocId().isEmpty()) {
            requestDTO.setDocId(UUID.randomUUID().toString());
        }
        
        // 요청 DTO에서 값 복사
        documentDTO.setDocId(requestDTO.getDocId());
        documentDTO.setDocNumber(requestDTO.getDocNumber());
        documentDTO.setFormId(requestDTO.getFormId());
        documentDTO.setTitle(requestDTO.getTitle());
        documentDTO.setContent(requestDTO.getContent());
        documentDTO.setDrafterId(requestDTO.getDrafterId());
        
        // 부서 정보 설정
        String draftDeptCode = getEmployeeDeptCodeByEmpId(requestDTO.getDrafterId());
        documentDTO.setDraftDept(draftDeptCode);
        
        // 결재선 정보 설정
        if (requestDTO.getApprovalLinesJson() != null && !requestDTO.getApprovalLinesJson().isEmpty()) {
            List<ApprovalLineDTO> approvalLines = objectMapper.readValue(
                requestDTO.getApprovalLinesJson(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, ApprovalLineDTO.class)
            );
            documentDTO.setApprovalLines(approvalLines);
        }
        
        // 문서 저장
        DocumentDTO savedDocument = saveDocument(documentDTO, requestDTO.isTempSave());
        
        // 완료된 문서 반환
        return getDocumentDetail(savedDocument.getDocId());
    }

    /**
     * 문서 저장 (임시저장 또는 결재요청)
     */
    @Transactional
    public DocumentDTO saveDocument(DocumentDTO documentDTO, boolean isTempSave) {
        log.info("문서 저장 시작: 임시저장={}, 제목={}", isTempSave, documentDTO.getTitle());
        
        // 문서 ID 생성
        if (documentDTO.getDocId() == null || documentDTO.getDocId().isEmpty()) {
            documentDTO.setDocId(UUID.randomUUID().toString());
            log.debug("신규 문서 ID 생성: {}", documentDTO.getDocId());
        }
        
        // 임시저장 여부 설정
        documentDTO.setIsTempSaved(isTempSave ? "Y" : "N");
        
        // 문서 상태 설정
        documentDTO.setDocStatus(isTempSave ? DocumentStatus.TEMP_SAVED : DocumentStatus.PROCESSING);
        
        // 문서 번호 생성 (신규 문서인 경우)
        if (documentDTO.getDocNumber() == null || documentDTO.getDocNumber().isEmpty()) {
            int seq = documentMapper.selectDocumentSequence();
            String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            documentDTO.setDocNumber(dateStr + "-" + documentDTO.getDraftDept() + "-" + seq);
            log.debug("문서 번호 생성: {}", documentDTO.getDocNumber());
        }
        
        // 기안일자 설정
        if (documentDTO.getDraftDate() == null) {
            documentDTO.setDraftDate(LocalDateTime.now());
        }
        
        // 문서 저장
        boolean isNewDocument = documentMapper.selectDocumentById(documentDTO.getDocId()) == null;
        if (isNewDocument) {
            log.debug("신규 문서 삽입");
            documentMapper.insertDocument(documentDTO);
        } else {
            log.debug("기존 문서 수정");
            // 기존 결재선 삭제 (재설정할 경우)
            if (!isTempSave) {
                approvalLineMapper.deleteApprovalLinesByDocId(documentDTO.getDocId());
            }
            documentMapper.updateDocument(documentDTO);
        }
        
        // 임시저장이 아닌 경우, 결재선 저장
        if (!isTempSave) {
            approvalProcessService.createApprovalLines(documentDTO.getDocId(), documentDTO.getApprovalLines());
        }
        
        log.info("문서 저장 완료: {}", documentDTO.getDocId());
        return documentMapper.selectDocumentById(documentDTO.getDocId());
    }

    /**
     * 문서 상세 조회
     */
    public DocumentDTO getDocumentDetail(String docId) {
        log.debug("문서 상세 조회: {}", docId);
        
        DocumentDTO document = documentMapper.selectDocumentById(docId);
        if (document == null) {
            throw new DocumentNotFoundException("문서를 찾을 수 없습니다: " + docId);
        }
        
        // 결재선 조회
        List<ApprovalLineDTO> approvalLines = approvalLineMapper.selectApprovalLinesByDocId(docId);
        document.setApprovalLines(approvalLines);
        log.debug("결재선 조회: {}개", approvalLines.size());
        
        return document;
    }

    /**
     * 기안자 ID로 문서 목록 조회
     */
    public List<DocumentDTO> getDrafterDocumentsByEmpId(String empId) {
        log.debug("기안자별 문서 목록 조회: {}", empId);
        return documentMapper.selectDocumentsByDrafterId(empId);
    }
    
    /**
     * 기안자 및 상태별 문서 목록 조회
     */
    public List<DocumentDTO> getDocumentsByDrafterAndStatus(String drafterId, String status) {
        log.debug("기안자 및 상태별 문서 조회: 기안자={}, 상태={}", drafterId, status);
        return documentMapper.selectDocumentsByDrafterAndStatus(drafterId, status);
    }

    /**
     * 직원 ID로 결재 대기 문서 목록 조회
     */
    public List<DocumentDTO> getPendingDocumentsByEmpId(String empId) {
        log.debug("결재 대기 문서 목록 조회: {}", empId);
        return documentMapper.selectPendingDocumentsByApproverId(empId);
    }

    /**
     * 상태별 문서 목록 조회 ---------삭제예정 모든 상태를 조회하기에  혹시나 누가 쓸까봐 나겨둠
     */
    public List<DocumentDTO> getDocumentsByStatus(String status) {
        log.debug("상태별 문서 목록 조회: {}", status);
        return documentMapper.selectDocumentsByStatus(status);
    }
    
    /**
     * 상태별 문서 목록 조회 (특정 사용자 관련 문서만)
     * 사용자가 기안자이거나 결재선에 포함된 문서만 조회
     */
    public List<DocumentDTO> getDocumentsByStatusAndRelatedUser(String status, String userId) {
        log.debug("상태별 관련 문서 목록 조회: 상태={}, 사용자={}", status, userId);
        return documentMapper.selectDocumentsByStatusAndRelatedUser(status, userId);
    }
    
    /**
     * 문서 삭제 (임시저장 문서만 삭제 가능)
     * @param docId 문서 ID
     * @param userId 요청한 사용자 ID
     * @return 삭제 성공 여부
     */
    @Transactional
    public boolean deleteDocument(String docId, String userId) {
        log.info("문서 삭제 요청: {}, 요청자: {}", docId, userId);
        
        DocumentDTO document = documentMapper.selectDocumentById(docId);
        
        if (document == null) {
            throw new DocumentNotFoundException("삭제할 문서가 존재하지 않습니다: " + docId);
        }
        
        // 문서 소유자 검증
        if (!document.getDrafterId().equals(userId)) {
            throw new DocumentAccessDeniedException("문서 삭제 권한이 없습니다");
        }
        
        if (!"Y".equals(document.getIsTempSaved())) {
            throw new DocumentAccessDeniedException("임시저장 문서만 삭제할 수 있습니다");
        }
        
        // 관련 데이터 삭제
        approvalLineMapper.deleteApprovalLinesByDocId(docId);
        log.debug("결재선 삭제 완료: {}", docId);
        
        // 문서 삭제
        int result = documentMapper.deleteDocument(docId);
        log.info("문서 삭제 완료: {}", docId);
        
        return result > 0;
    }
    
    /**
     * 직원 ID의 부서 코드 조회
     */
    public String getEmployeeDeptCodeByEmpId(String empId) {
        log.debug("부서 코드 조회: 직원ID={}", empId);
        try {
            String deptCode = documentMapper.selectEmployeeDeptCodeByEmpId(empId);
            
            if (deptCode == null || deptCode.isEmpty()) {
                log.warn("직원({})의 부서 정보를 찾을 수 없습니다. 기본값 사용", empId);
                return "DEPT001";
            }
            
            return deptCode;
        } catch (Exception e) {
            log.error("부서 코드 조회 중 오류 발생: {}", e.getMessage(), e);
            return "DEPT001"; // 오류 시 기본값 반환
        }
    }
    
    /**
     * 결재 가능한 문서 목록 조회 (결재 순서 고려)
     * 첫 번째 결재자이거나 이전 결재자가 모두 승인한 문서만 조회
     */
    public List<DocumentDTO> getProcessableDocumentsByEmpId(String empId) {
        log.debug("결재 가능한 문서 목록 조회: {}", empId);
        return documentMapper.selectProcessableDocumentsByApproverId(empId);
    }
    
    /**
     * 기안자를 제외한 결재자 목록 조회
     * @return 필터링된 결재자 목록
     */
    public List<EmpListPreviewDTO> getApproversExceptDrafter() {
        // 현재 사용자 ID 가져오기
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUserId = auth.getName();
        
        // 모든 사원 목록 조회 (기존 서비스 활용)
        List<EmpListPreviewDTO> allEmployees = empService.getEmpList();
        
        // 기안자 제외 필터링
        List<EmpListPreviewDTO> filteredEmployees = allEmployees.stream()
            .filter(emp -> !emp.getEmpId().equals(currentUserId))
            .collect(Collectors.toList());
        
        log.debug("결재자 목록 필터링: 전체 {}명, 필터링 후 {}명", 
                  allEmployees.size(), filteredEmployees.size());
        
        return filteredEmployees;
    }
}