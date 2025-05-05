package com.example.cmtProject.dto.erp.eapproval;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 문서 저장 요청 DTO
 * 문서 저장 API에서 사용되는 요청 파라미터를 담는 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentSaveRequestDTO {
    /** 문서 ID */
    private String docId;
    
    /** 문서 번호 */
    private String docNumber;
    
    /** 양식 ID */
    private String formId;
    
    /** 제목 */
    private String title;
    
    /** 내용 */
    private String content;
    
    /** 기안자 ID */
    private String drafterId;
    
    /** 임시저장 여부 */
    @JsonProperty("isTempSave")
    private boolean isTempSave;
    
    /** 결재선 JSON 문자열 */
    private String approvalLinesJson;
}