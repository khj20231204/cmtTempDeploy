package com.example.cmtProject.service.erp.eapproval.processor.processorImpl;

import com.example.cmtProject.dto.erp.eapproval.DocumentDTO;
import com.example.cmtProject.service.erp.eapproval.processor.FormDataExtractor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * 결재 문서 데이터 추출 구현체
 */
@Component
@Slf4j
public class DefaultFormDataExtractor implements FormDataExtractor {

    @Override
    public Document parseHtml(DocumentDTO document) {
        try {
            String content = document.getContent();
            if (content == null || content.trim().isEmpty()) {
                log.warn("문서 내용이 비어있습니다: {}", document.getDocId());
                return Jsoup.parse("<html><body></body></html>");
            }
            
            Document doc = Jsoup.parse(content);
            
            // 기본 문서 구조 로깅
            log.debug("문서 HTML 파싱 완료 - 제목: '{}', 폼 요소 수: {}", 
                     doc.title(), 
                     doc.select("input, select, textarea").size());
            
            // 폼 요소 기본 정보 로깅
            if (log.isDebugEnabled()) {
                log.debug("문서의 기본 폼 요소 정보:");
                doc.select("input, select, textarea").forEach(element -> {
                    log.debug(" - 요소: {}, ID: {}, NAME: {}, TYPE: {}", 
                             element.tagName(), 
                             element.id(), 
                             element.attr("name"),
                             element.attr("type"));
                });
            }
            
            return doc;
        } catch (Exception e) {
            log.error("HTML 파싱 중 오류 발생: {}", e.getMessage(), e);
            return Jsoup.parse("<html><body></body></html>");
        }
    }

    @Override
    public String extractField(Document htmlDoc, String selector) {
        try {
            Elements elements = htmlDoc.select(selector);
            if (elements.isEmpty()) {
                log.warn("선택자로 요소를 찾을 수 없습니다: {}", selector);
                return "";
            }
            
            String result = "";
            
            // input, select 등 폼 요소 처리
            if (elements.is("input[type=text], input[type=hidden], input:not([type]), input[type=date]")) {
                result = elements.val();
                log.debug("입력 필드 추출 ({}): '{}'", selector, result);
            } else if (elements.is("input[type=checkbox], input[type=radio]")) {
                result = elements.hasAttr("checked") ? elements.val() : "";
                log.debug("체크박스/라디오 추출 ({}): '{}'", selector, result);
            } else if (elements.is("select")) {
                Elements selectedOptions = elements.select("option[selected]");
                if (selectedOptions.isEmpty()) {
                    // select는 있지만 selected 속성이 없는 경우
                    log.debug("선택된 옵션 없음 ({}), 첫 번째 옵션 또는 값 시도", selector);
                    result = elements.val();  // 선택된 옵션이 없으면 현재 value 시도
                    
                    // 여전히 값이 없으면 첫 번째 옵션의 value 시도
                    if (result == null || result.isEmpty()) {
                        Elements firstOption = elements.select("option:first-child");
                        if (!firstOption.isEmpty()) {
                            result = firstOption.val();
                            log.debug("첫 번째 옵션 값 사용 ({}): '{}'", selector, result);
                        }
                    }
                } else {
                    result = selectedOptions.val();
                    log.debug("선택 필드 추출 ({}): '{}'", selector, result);
                }
            } else if (elements.is("textarea")) {
                result = elements.val();
                log.debug("텍스트 영역 추출 ({}): '{}'", selector, result);
            } else {
                // 일반 요소의 경우 텍스트 반환
                result = elements.text();
                log.debug("일반 요소 텍스트 추출 ({}): '{}'", selector, result);
            }
            
            return result;
        } catch (Exception e) {
            log.error("필드 추출 중 오류 발생: {}", e.getMessage(), e);
            return "";
        }
    }

    @Override
    public LocalDateTime extractDateField(Document htmlDoc, String selector, String pattern) {
        try {
            String dateStr = extractField(htmlDoc, selector);
            if (dateStr == null || dateStr.trim().isEmpty()) {
                log.debug("날짜 필드가 비어있음 ({})", selector);
                return null;
            }
            
            log.debug("날짜 문자열 파싱 시도 ({}): '{}', 패턴: '{}'", selector, dateStr, pattern);
            
            // 날짜 포맷이 맞지 않는 경우를 확인
            if (dateStr.length() != pattern.replace("'", "").length()) {
                log.warn("날짜 문자열 길이가 패턴과 일치하지 않음 ({}): '{}', 패턴: '{}'", selector, dateStr, pattern);
            }
            
            // 날짜에 시간 부분이 없는 경우 처리
            if (pattern.equals("yyyy-MM-dd") && !dateStr.contains(" ")) {
                dateStr = dateStr + " 00:00:00";
                pattern = "yyyy-MM-dd HH:mm:ss";
                log.debug("날짜에 시간 추가: '{}'", dateStr);
            }
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            LocalDateTime result;
            
            try {
                // LocalDateTime으로 직접 파싱 시도
                result = LocalDateTime.parse(dateStr, formatter);
            } catch (DateTimeParseException e) {
                // 다양한 형식 시도
                log.debug("기본 형식 파싱 실패, 대체 형식 시도");
                
                if (dateStr.length() == 10) {  // yyyy-MM-dd 형식인 경우
                    try {
                        result = LocalDateTime.of(
                            java.time.LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                            java.time.LocalTime.of(0, 0, 0)
                        );
                    } catch (Exception ex) {
                        log.error("대체 날짜 파싱도 실패 ({}): '{}'", selector, dateStr);
                        throw e;  // 원래 예외를 다시 던짐
                    }
                } else {
                    // 다른 형식 파싱 실패
                    throw e;
                }
            }
            
            log.debug("날짜 파싱 성공 ({}): {}", selector, result);
            return result;
        } catch (DateTimeParseException e) {
            log.error("날짜 필드 파싱 중 오류 발생 ({}): '{}', 오류: {}", 
                      selector, extractField(htmlDoc, selector), e.getMessage());
            return null;
        }
    }
}