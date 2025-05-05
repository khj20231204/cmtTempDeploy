package com.example.cmtProject.dto.erp.eapproval;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 첨부파일 DTO
 * 문서 첨부파일 정보를 담는 DTO 클래스
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentDTO {
    /** 파일 번호 */
    private Long fileNo;
    
    /** 문서 ID */
    private String docId;
    
    /** 원본 파일명 */
    private String originalName;
    
    /** 저장 파일명 */
    private String savedName;
    
    /** 파일 경로 */
    private String filePath;
    
    /** 파일 크기 */
    private Long fileSize;
    
    /** 파일 타입 */
    private String fileType;
    
    /** 업로드 일시 */
    private LocalDateTime uploadDate;
    
    /** 행 타입 (조회:select, 등록:insert, 수정:update, 삭제:delete) */
    private String rowType;
}