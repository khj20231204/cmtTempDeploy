package com.example.cmtProject.dto.comm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 공통코드 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommonCodeDTO {
    /** 공통코드 */
    private String cmnCode;
    
    /** 공통코드명 */
    private String cmnName;
    
    /** 공통코드 내용 */
    private String cmnContent;
    
    /** 공통코드 사용 여부 */
    private String cmnCodeIsActive;
    
    /** 공통코드 정렬 순서 */
    private Integer cmnSortOrder;
    
    /** 행 타입
     * - select: 조회된 기존 데이터
	 * - insert: 신규 추가 데이터
	 * - update: 수정된 데이터
	 * - delete: 삭제 대상 데이터
	 * 주로 그리드 UI와의 데이터 상태 동기화에 사용되며 DB에는 저장되지 않음
     *  */
    private String rowType;

} //CommonCodeDTO
