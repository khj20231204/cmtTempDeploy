package com.example.cmtProject.dto.erp.eapproval;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocFormDTO {
    /** 양식 ID */
    private String formId;
    
    /** 양식 내용 */
    private String formContent;
    
    /** 생성자 ID */
    private String creatorId;  
    
    /** 생성 일자 */
    private LocalDateTime createDate;
    
    /** 수정자 ID */
    private String updaterId;  
    
    /** 수정 일자 */
    private LocalDateTime updateDate;
    
    /** 행 타입 (조회:select, 등록:insert, 수정:update, 삭제:delete) */
    private String rowType;
}