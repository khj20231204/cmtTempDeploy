package com.example.cmtProject.dto.comm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 공통코드 상세 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommonCodeDetailDTO {
    /** 상세코드 */
    private String cmnDetailCode;
    
    /** 공통코드 */
    private String cmnCode;
    
    /** 상세코드명 */
    private String cmnDetailName;
    
    /** 상세코드 정렬 순서 */
    private Integer cmnDetailSortOrder;
    
    /** 상세코드 내용 */
    private String cmnDetailContent;
    
    /** 상세코드 값 */
    private String cmnDetailValue;
    
    /** 상세코드 값2 */
    private String cmnDetailValue2;
    
    /** 상세코드 사용 여부 */
    private String cmnDetailCodeIsActive;
    
    /** 행 타입 (조회:select, 등록:insert, 수정:update, 삭제:delete) */
    private String rowType;
} //CommonCodeDetailDTO


