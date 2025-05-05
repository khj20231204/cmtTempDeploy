package com.example.cmtProject.service.erp.eapproval.processor;

import com.example.cmtProject.dto.erp.eapproval.DocumentDTO;
import org.jsoup.nodes.Document;

/**
 * 결재 문서 데이터 추출 인터페이스
 * 결재 문서의 HTML 내용에서 업무 처리에 필요한 데이터를 추출
 */
public interface FormDataExtractor {
    /**
     * HTML 문서에서 데이터 추출
     * @param document 결재 문서 DTO
     * @return 파싱된 HTML 문서 객체
     */
	public Document parseHtml(DocumentDTO document);
    
    /**
     * 지정된 필드의 값 추출
     * @param htmlDoc 파싱된 HTML 문서
     * @param selector CSS 선택자
     * @return 추출된 필드 값
     */
	public String extractField(Document htmlDoc, String selector);
    
    /**
     * 지정된 필드의 날짜 값 추출
     * @param htmlDoc 파싱된 HTML 문서
     * @param selector CSS 선택자
     * @param pattern 날짜 형식
     * @return 추출된 날짜 값
     */
	public java.time.LocalDateTime extractDateField(Document htmlDoc, String selector, String pattern);
}