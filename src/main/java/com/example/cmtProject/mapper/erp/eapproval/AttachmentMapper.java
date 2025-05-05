package com.example.cmtProject.mapper.erp.eapproval;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.cmtProject.dto.erp.eapproval.AttachmentDTO;

/**
 * 첨부파일 매퍼 인터페이스
 * 첨부파일 관련 데이터베이스 작업을 처리
 */
@Mapper
public interface AttachmentMapper {
    /**
     * 첨부파일 저장
     * @param attachment 저장할 첨부파일 정보
     */
    void insertAttachment(AttachmentDTO attachment);
    
    /**
     * 문서 ID로 첨부파일 목록 조회
     * @param docId 문서 ID
     * @return 첨부파일 목록
     */
    List<AttachmentDTO> selectAttachmentsByDocId(String docId);
    
    /**
     * 파일 번호로 첨부파일 조회
     * @param fileNo 파일 번호
     * @return 첨부파일 정보
     */
    AttachmentDTO selectAttachmentByFileNo(Long fileNo);
    
    /**
     * 문서 ID로 첨부파일 삭제
     * @param docId 문서 ID
     * @return 삭제된 파일 수
     */
    int deleteAttachmentsByDocId(String docId);
    
    /**
     * 파일 번호로 첨부파일 삭제
     * @param fileNo 파일 번호
     * @return 삭제된 파일 수
     */
    int deleteAttachment(Long fileNo);
}