package com.example.cmtProject.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.example.cmtProject.entity.erp.employees.Employees;
import com.example.cmtProject.entity.erp.employees.PrincipalDetails;
import com.example.cmtProject.mapper.erp.attendanceMgt.AttendsMapper;

@ControllerAdvice
public class GlobalModelAttributeAdvice {
	
	@Autowired
	AttendsMapper attendsMapper;
	
	@ModelAttribute
    public void addGlobalAttributes(Model model, Authentication authentication) {
		
		// 출퇴근 버튼 보이게 하는 전역 어트리뷰트
	    if (authentication != null && authentication.isAuthenticated()) {
	        Object principal = authentication.getPrincipal();

	        if (principal instanceof PrincipalDetails principalDetails) {
	            Employees loginUser = principalDetails.getUser();

	            LocalDateTime start = LocalDate.now().atStartOfDay();
	            LocalDateTime end = LocalDate.now().atTime(LocalTime.MAX);

	            boolean hasCheckedIn = false;
	    	    boolean hasCheckedOut = true;
	    	    
	    	    hasCheckedIn = attendsMapper.hasCheckedInToday(loginUser.getEmpNo(), start, end);
	    	    model.addAttribute("hasCheckedIn", hasCheckedIn);
	            Long latestCheckInAtdNo = attendsMapper.findLatestCheckInAtdNo(loginUser.getEmpNo());
	            if (latestCheckInAtdNo != null) {
	                hasCheckedOut = attendsMapper.hasCheckedOutToday(latestCheckInAtdNo, start, end);
	            }
	            model.addAttribute("hasCheckedOut", hasCheckedOut);
	        }
	    }
		// 출퇴근 버튼 보이게 하는 전역 어트리뷰트
		
		
	}

}
