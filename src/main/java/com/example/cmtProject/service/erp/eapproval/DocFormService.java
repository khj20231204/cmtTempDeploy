package com.example.cmtProject.service.erp.eapproval;

import com.example.cmtProject.dto.erp.eapproval.DocFormDTO;
import com.example.cmtProject.dto.erp.eapproval.DocumentDTO;
import com.example.cmtProject.mapper.erp.eapproval.DocFormMapper;
import com.example.cmtProject.mapper.erp.eapproval.DocumentMapper;
import com.example.cmtProject.comm.exception.DocFormNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocFormService {

    private final DocFormMapper docFormMapper;
    private final DocumentMapper documentMapper;
    
    /**
     * 전체 양식 목록 조회
     */
    public List<DocFormDTO> getAllDocForms() {
        log.debug("전체 양식 목록 조회");
        return docFormMapper.selectAllDocForms();
    }
    
    /**
     * 특정 양식 조회
     */
    public DocFormDTO getDocFormById(String formId) {
        log.debug("양식 조회: {}", formId);
        
        if (formId == null || formId.isEmpty()) {
            throw new IllegalArgumentException("양식 ID가 유효하지 않습니다.");
        }
        
        DocFormDTO formDTO = docFormMapper.selectDocFormById(formId);
        
        if (formDTO == null) {
            throw new DocFormNotFoundException("양식을 찾을 수 없습니다: " + formId);
        }
        
        return formDTO;
    }
    
    /**
     * 양식 저장/수정
     */
    @Transactional
    public DocFormDTO saveDocForm(DocFormDTO docFormDTO) {
        log.info("양식 저장: {}", docFormDTO.getFormId());
        try {
            // formId가 null이거나 비어있으면 새로 생성
            if (docFormDTO.getFormId() == null || docFormDTO.getFormId().trim().isEmpty()) {
                // UUID 또는 다른 방식으로 고유 ID 생성
                docFormDTO.setFormId("FORM_" + UUID.randomUUID().toString().substring(0, 8));
                log.debug("신규 양식 ID 생성: {}", docFormDTO.getFormId());
            }
            
            boolean isNewForm = docFormMapper.selectDocFormById(docFormDTO.getFormId()) == null;
            
            if (isNewForm) {
                log.debug("신규 양식 저장");
                // 생성 시간, 생성자 ID 설정
                docFormDTO.setCreateDate(LocalDateTime.now());
                docFormMapper.insertDocForm(docFormDTO);
            } else {
                log.debug("기존 양식 수정");
                // 수정 시간, 수정자 ID 설정
                docFormDTO.setUpdateDate(LocalDateTime.now());
                docFormMapper.updateDocForm(docFormDTO);
            }
            
            return docFormMapper.selectDocFormById(docFormDTO.getFormId());
        } catch (Exception e) {
            log.error("양식 저장 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("양식 저장 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }
    
    /**
     * 양식 삭제
     * 
     * @return 삭제 성공 여부
     */
    @Transactional
    public boolean deleteDocForm(String formId) {
        log.info("양식 삭제: {}", formId);
        
        if (formId == null || formId.isEmpty()) {
            throw new IllegalArgumentException("양식 ID가 유효하지 않습니다.");
        }
        
        DocFormDTO existingForm = docFormMapper.selectDocFormById(formId);
        if (existingForm == null) {
            throw new DocFormNotFoundException("삭제할 양식이 존재하지 않습니다: " + formId);
        }
        
        try {
            docFormMapper.deleteDocForm(formId);
            
            // 삭제 확인
            boolean isDeleted = docFormMapper.selectDocFormById(formId) == null;
            log.debug("양식 삭제 결과: {}", isDeleted ? "성공" : "실패");
            
            return isDeleted;
        } catch (Exception e) {
            log.error("양식 삭제 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("양식 삭제 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

	public int countPendingDocumentsByEmpId(String empId) {
		return documentMapper.selectCount(empId);
	}
	
	public int myDraftCount(String empId) {
		return documentMapper.myDraftCount(empId);
	}

}