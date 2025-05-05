package com.example.cmtProject.service.erp.eapproval;

import com.example.cmtProject.dto.erp.eapproval.AttachmentDTO;
import com.example.cmtProject.mapper.erp.eapproval.AttachmentMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 첨부파일 서비스
 * 문서 첨부파일 관리를 위한 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AttachmentService {

    private final AttachmentMapper attachmentMapper;
    
    /**
     * 첨부파일 저장
     * 
     * @param docId 문서 ID
     * @param originalName 원본 파일명
     * @param savedName 저장 파일명
     * @param filePath 파일 경로
     * @param fileSize 파일 크기
     * @param fileType 파일 타입
     * @return 저장된 첨부파일 정보
     */
    @Transactional
    public AttachmentDTO saveAttachment(String docId, String originalName, String savedName, 
                                      String filePath, long fileSize, String fileType) {
        log.debug("첨부파일 저장: 문서={}, 파일={}", docId, originalName);
        
        AttachmentDTO attachment = new AttachmentDTO();
        attachment.setDocId(docId);
        attachment.setOriginalName(originalName);
        attachment.setSavedName(savedName);
        attachment.setFilePath(filePath);
        attachment.setFileSize(fileSize);
        attachment.setFileType(fileType);
        
        attachmentMapper.insertAttachment(attachment);
        
        return attachment;
    }
    
    /**
     * 문서 ID로 첨부파일 목록 조회
     * 
     * @param docId 문서 ID
     * @return 첨부파일 목록
     */
    public List<AttachmentDTO> getAttachmentsByDocId(String docId) {
        log.debug("문서 첨부파일 목록 조회: {}", docId);
        return attachmentMapper.selectAttachmentsByDocId(docId);
    }
    
    /**
     * 파일 번호로 첨부파일 조회
     * 
     * @param fileNo 파일 번호
     * @return 첨부파일 정보
     */
    public AttachmentDTO getAttachmentByFileNo(Long fileNo) {
        log.debug("첨부파일 조회: {}", fileNo);
        return attachmentMapper.selectAttachmentByFileNo(fileNo);
    }
    
    /**
     * 문서 ID로 첨부파일 삭제
     * 
     * @param docId 문서 ID
     * @return 삭제된 첨부파일 수
     */
    @Transactional
    public int deleteAttachmentsByDocId(String docId) {
        log.debug("문서 첨부파일 삭제: {}", docId);
        return attachmentMapper.deleteAttachmentsByDocId(docId);
    }
    
    /**
     * 파일 번호로 첨부파일 삭제
     * 
     * @param fileNo 파일 번호
     * @return 삭제 성공 여부
     */
    @Transactional
    public boolean deleteAttachment(Long fileNo) {
        log.debug("첨부파일 삭제: {}", fileNo);
        return attachmentMapper.deleteAttachment(fileNo) > 0;
    }
}