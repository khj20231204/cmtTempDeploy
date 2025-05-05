package com.example.cmtProject.service.erp.eapproval.processor.processorImpl;

import com.example.cmtProject.dto.erp.attendanceMgt.LeaveDTO;
import com.example.cmtProject.dto.erp.eapproval.ApprovalLineDTO;
import com.example.cmtProject.dto.erp.eapproval.DocumentDTO;
import com.example.cmtProject.dto.erp.employees.EmpDTO;
import com.example.cmtProject.entity.erp.employees.Employees;
import com.example.cmtProject.entity.erp.employees.PrincipalDetails;
import com.example.cmtProject.service.erp.attendanceMgt.LeaveService;
import com.example.cmtProject.service.erp.eapproval.processor.ApprovalPostProcessor;
import com.example.cmtProject.service.erp.eapproval.processor.FormDataExtractor;
import com.example.cmtProject.service.erp.employees.EmployeesService;
import com.example.cmtProject.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;

/**
 * 휴가 신청 결재 후처리기
 * 결재 완료 시 휴가 신청 처리, 반려 시 휴가 취소 처리
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LeaveApprovalProcessor implements ApprovalPostProcessor {

    private final FormDataExtractor formDataExtractor;
    private final LeaveService leaveService;
    @Autowired
    private EmployeesService employeesService;
    
    // 휴가 신청서 양식 ID - 실제 양식 ID로 변경 필요
    private static final String LEAVE_FORM_ID = "휴가신청서"; 
    
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean processApproved(DocumentDTO document) {
        log.info("휴가 신청 결재 완료 처리 시작: 문서ID={}, 기안자={}", document.getDocId(), document.getDrafterId());
        
        try {
            // 이미 처리된 휴가가 있는지 확인
            LeaveDTO existingLeave = leaveService.getLeaveByDocId(document.getDocId());
            if (existingLeave != null) {
                log.info("이미 처리된 휴가 신청: {}", document.getDocId());
                return true;
            }
            
            // HTML 파싱
            Document htmlDoc = formDataExtractor.parseHtml(document);
            
            // 휴가 신청 정보 추출
            LeaveDTO leaveDTO = extractLeaveData(htmlDoc, document);
            
            // 문서ID 설정
            leaveDTO.setDocId(document.getDocId());
            
            // 휴가 승인 상태로 설정 - 명시적으로 설정
            leaveDTO.setLevApprovalStatus("승인");
            leaveDTO.setLevApprovalDate(LocalDateTime.now());
            leaveDTO.setLevRemarks("결재 승인 처리");
            
            // 휴가 유형 코드 변환
            convertLeaveTypeToCode(leaveDTO);
            
            // 중요: 기안자와 결재자 구분 명확하게 처리
            // 1. 결재자 ID 설정 (승인한 사람)
            Employees currentUser = SecurityUtil.getCurrentUser();
            if (currentUser != null) {
                // 결재자 ID를 levApprover 필드에 설정
                leaveDTO.setLevApprover(currentUser.getEmpId());
                log.debug("결재자 정보 설정: {}", currentUser.getEmpId());
            } else if (leaveDTO.getLevApprover() == null) {
                // 최종 결재자 정보가 없고, 현재 사용자 정보도 없는 경우 fallback
                // 결재선에서 마지막 결재자 추출 로직은 이미 extractLeaveData에 있음
                log.warn("결재자 정보를 설정할 수 없습니다.");
            }

            
            // 2. 기안자(휴가 신청자) 정보 생성 - 중요: document.getDrafterId() 사용
            Employees drafterUser = new Employees();
            drafterUser.setEmpId(document.getDrafterId());
            
            EmpDTO loginUser = employeesService.getEmpList(drafterUser.getEmpId());
            
            // 3. 휴가 정보 저장 - 기안자 ID 사용 (중요: 기안자 ID로 저장)
            leaveService.insertLeave(leaveDTO, loginUser, document.getDocId());
            log.info("휴가 신청 정보 저장 완료: 기안자={}, 결재자={}, 문서ID={}", 
                   document.getDrafterId(), leaveDTO.getLevApprover(), document.getDocId());
            
            return true;
        } catch (Exception e) {
            log.error("휴가 신청 처리 중 오류 발생: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean processRejected(DocumentDTO document) {
        log.info("휴가 신청 결재 반려 처리 시작: 문서ID={}, 기안자={}", document.getDocId(), document.getDrafterId());
        
        try {
            // 이미 처리된 휴가가 있는지 확인
            LeaveDTO existingLeave = leaveService.getLeaveByDocId(document.getDocId());
            if (existingLeave != null) {
                log.info("이미 처리된 휴가 반려: {}", document.getDocId());
                return true;
            }
            
            // HTML 파싱
            Document htmlDoc = formDataExtractor.parseHtml(document);
            
            // 휴가 신청 정보 추출
            LeaveDTO leaveDTO = extractLeaveData(htmlDoc, document);
            
            // 문서ID 설정
            leaveDTO.setDocId(document.getDocId());
            
            // 휴가 반려 상태로 설정 - 명시적으로 설정
            leaveDTO.setLevApprovalStatus("반려");
            leaveDTO.setLevApprovalDate(LocalDateTime.now());
            leaveDTO.setLevRemarks("결재 반려 처리");
            
            // 휴가 유형 코드 변환
            convertLeaveTypeToCode(leaveDTO);
            
            // 중요: 기안자와 결재자 구분 명확하게 처리
            // 1. 결재자 ID 설정 (반려한 사람)
            Employees currentUser = SecurityUtil.getCurrentUser();
            if (currentUser != null) {
                // 결재자 ID를 levApprover 필드에 설정
                leaveDTO.setLevApprover(currentUser.getEmpId());
                log.debug("결재자 정보 설정: {}", currentUser.getEmpId());
            } else if (leaveDTO.getLevApprover() == null) {
                // 최종 결재자 정보가 없고, 현재 사용자 정보도 없는 경우 fallback
                log.warn("결재자 정보를 설정할 수 없습니다.");
            }
            

            // 2. 기안자(휴가 신청자) 정보 생성 - 중요: document.getDrafterId() 사용
            Employees drafterUser = new Employees();
            drafterUser.setEmpId(document.getDrafterId());
            
            EmpDTO loginUser = employeesService.getEmpList(drafterUser.getEmpId());

            // 3. 휴가 정보 저장 - 기안자 ID 사용 (중요: 기안자 ID로 저장)
            leaveService.insertLeave(leaveDTO, loginUser, document.getDocId());
            log.info("휴가 반려 정보 저장 완료: 기안자={}, 결재자={}, 문서ID={}", 
                   document.getDrafterId(), leaveDTO.getLevApprover(), document.getDocId());

            
            return true;
        } catch (Exception e) {
            log.error("휴가 반려 처리 중 오류 발생: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean canProcess(String formId) {
        boolean result = LEAVE_FORM_ID.equals(formId);
        log.debug("휴가 양식 처리 가능 여부: formId={}, 결과={}", formId, result);
        return result;
    }
    
    /**
     * 휴가 유형을 코드로 변환
     */
    private void convertLeaveTypeToCode(LeaveDTO leaveDTO) {
        if (leaveDTO.getLevType() == null) return;
        
        switch(leaveDTO.getLevType()) {
            case "일반휴가":
                leaveDTO.setLevType("VCN001");
                break;
            case "연차":
                leaveDTO.setLevType("VCN002");
                break;
            case "병가":
                leaveDTO.setLevType("VCN003");
                break;
            case "출산휴가":
                leaveDTO.setLevType("VCN004");
                break;
            case "반차":
                leaveDTO.setLevType("VCN007");
                break;
            case "경조사":
                leaveDTO.setLevType("VCN006");
                break;
            default:
                // 코드가 이미 설정된 경우 또는 알 수 없는 유형은 유지
                break;
        }
    }
    
    /**
     * HTML 문서에서 휴가 신청 정보 추출
     */
    private LeaveDTO extractLeaveData(Document htmlDoc, DocumentDTO document) {
        log.debug("휴가 신청 정보 추출 시작");
        
        LeaveDTO leaveDTO = new LeaveDTO();
        
        // 중요: 기안자 정보 명확히 설정 - 휴가 신청자는 기안자
        leaveDTO.setEmpId(document.getDrafterId());
        
        log.info("휴가 신청자(기안자) ID: {}", document.getDrafterId());
        
        // 휴가 유형 추출
        String leaveType = extractLeaveType(htmlDoc);
        leaveDTO.setLevType(leaveType);
        
        // 휴가 일자 추출
        LocalDateTime startDate = extractStartDate(htmlDoc);
        LocalDateTime endDate = extractEndDate(htmlDoc, startDate);
        
        leaveDTO.setLevStartDate(startDate);
        leaveDTO.setLevEndDate(endDate);
        
        // 휴가 일수 추출
        Double days = (double) extractLeaveDays(htmlDoc, startDate, endDate);
        leaveDTO.setLevDays(days);
        
        // 휴가 사유 추출
        String reason = extractLeaveReason(htmlDoc);
        leaveDTO.setLevReason(reason);
        
        // 신청일시 - 문서 기안일로 설정
        leaveDTO.setLevReqDate(document.getDraftDate());
        
        // 최종 결재자 ID 설정
        if(document.getApprovalLines() != null && !document.getApprovalLines().isEmpty()) {
            ApprovalLineDTO lastApprover = document.getApprovalLines()
                    .stream()
                    .max(Comparator.comparing(ApprovalLineDTO::getApprovalOrder))
                    .orElse(null);
            
            if (lastApprover != null) {
                leaveDTO.setLevApprover(lastApprover.getApproverId());
                log.debug("결재선에서 최종 결재자 설정: {}", lastApprover.getApproverId());
            }
        }
        
        // 상태 설정
        leaveDTO.setLevApprovalStatus(document.getDocStatus());
        
        log.debug("휴가 신청 정보 추출 완료: {}", leaveDTO);
        return leaveDTO;
    }
    
    /**
     * 휴가 유형 추출
     */
    private String extractLeaveType(Document htmlDoc) {
        // 현재 양식에 맞는 선택자들
        String[] selectors = {
            "#leaveType", 
            "select[id='leaveType']",
            "select.form-select",
            "select[name='leaveType']"
        };
        
        for (String selector : selectors) {
            String value = formDataExtractor.extractField(htmlDoc, selector);
            log.debug("휴가 유형 추출 시도 ({}): '{}'", selector, value);
            
            if (value != null && !value.trim().isEmpty()) {
                return value;
            }
        }
        
        // 기본값 반환
        log.debug("휴가 유형을 찾을 수 없어 기본값 사용: '연차'");
        return "연차";
    }
    
    /**
     * 시작 날짜 추출
     */
    private LocalDateTime extractStartDate(Document htmlDoc) {
        // 현재 양식에 맞는 선택자들
        String[][] selectorFormats = {
            {"#startDate", "yyyy-MM-dd"},
            {"input[id='startDate']", "yyyy-MM-dd"},
            {"#leaveStartDate", "yyyy-MM-dd"},
            {"input[name='leaveStartDate']", "yyyy-MM-dd"}
        };
        
        for (String[] pair : selectorFormats) {
            LocalDateTime date = formDataExtractor.extractDateField(htmlDoc, pair[0], pair[1]);
            log.debug("시작일 추출 시도 ({}): {}", pair[0], date);
            
            if (date != null) {
                return date;
            }
        }
        
        // 기본값 반환 (현재 날짜)
        log.debug("시작일을 찾을 수 없어 현재 날짜 사용");
        return LocalDateTime.now();
    }
    
    /**
     * 종료 날짜 추출
     */
    private LocalDateTime extractEndDate(Document htmlDoc, LocalDateTime defaultDate) {
        // 현재 양식에 맞는 선택자들
        String[][] selectorFormats = {
            {"#endDate", "yyyy-MM-dd"},
            {"input[id='endDate']", "yyyy-MM-dd"},
            {"#leaveEndDate", "yyyy-MM-dd"},
            {"input[name='leaveEndDate']", "yyyy-MM-dd"}
        };
        
        for (String[] pair : selectorFormats) {
            LocalDateTime date = formDataExtractor.extractDateField(htmlDoc, pair[0], pair[1]);
            log.debug("종료일 추출 시도 ({}): {}", pair[0], date);
            
            if (date != null) {
                return date;
            }
        }
        
        // 기본값 반환 (시작일과 동일)
        log.debug("종료일을 찾을 수 없어 시작일과 동일하게 설정");
        return defaultDate;
    }
    
    /**
     * 휴가 사유 추출
     */
    private String extractLeaveReason(Document htmlDoc) {
        // 현재 양식에 맞는 선택자들
        String[] selectors = {
            "#leaveReason", 
            "textarea[id='leaveReason']",
            "textarea[name='leaveReason']",
            "textarea.form-control"
        };
        
        for (String selector : selectors) {
            String value = formDataExtractor.extractField(htmlDoc, selector);
            log.debug("휴가 사유 추출 시도 ({}): '{}'", selector, value);
            
            if (value != null && !value.trim().isEmpty()) {
                return value;
            }
        }
        
        // 기본값 반환
        return "휴가 신청";
    }
    
    /**
     * 휴가일수 추출 - 자동 계산된 값 활용
     */
    private double extractLeaveDays(Document htmlDoc, LocalDateTime startDate, LocalDateTime endDate) {
        // 자동 계산된 휴가일수 추출 시도
        String leaveDaysStr = formDataExtractor.extractField(htmlDoc, "#leaveDays");
        log.debug("자동 계산된 휴가일수 추출 시도: '{}'", leaveDaysStr);
        
        if (leaveDaysStr != null && !leaveDaysStr.trim().isEmpty()) {
            try {
                // 숫자로 파싱 시도
                return Double.valueOf(leaveDaysStr);
            } catch (NumberFormatException e) {
                log.debug("휴가일수 파싱 실패: {}", leaveDaysStr);
            }
        }
        
        // 기본값
        return 1.0;
    }
}