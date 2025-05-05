package com.example.cmtProject.controller.erp.attendanceMgt;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.cmtProject.dto.comm.CommonCodeDetailNameDTO;
import com.example.cmtProject.dto.erp.attendanceMgt.LeaveDTO;
import com.example.cmtProject.dto.erp.attendanceMgt.WorkTemplateDTO;
import com.example.cmtProject.dto.erp.attendanceMgt.WorkTimeDTO;
import com.example.cmtProject.dto.erp.employees.EmpListPreviewDTO;
import com.example.cmtProject.entity.erp.employees.Employees;
import com.example.cmtProject.entity.erp.employees.PrincipalDetails;
import com.example.cmtProject.service.comm.CommonService;
import com.example.cmtProject.service.erp.attendanceMgt.LeaveService;

@Controller
@RequestMapping("/leaves")
public class LeaveController {
	
	private static final Logger logger = LoggerFactory.getLogger(LeaveController.class);
	
	@Autowired
	private CommonService commonService;
	
	@Autowired
	private LeaveService leaveService;
	
	
	
	//공통코드 DetailName 불러오는 메서드
		public static void commonCodeName(Model model , CommonService commonService) {
			
			List<String> groupCodes = commonService.getAllGroupCodes();
			
			Map<String, List<CommonCodeDetailNameDTO>> commonCodeMap = new HashMap<>();
			
			for(String groupCode : groupCodes) {
				commonCodeMap.put(groupCode, commonService.getCodeListByGroup(groupCode));
			}
			model.addAttribute("commonCodeMap",commonCodeMap);
		}
	
	
	

	// 출결 정보 목록 페이지 (HTML 렌더링)
    @GetMapping("/list")
    public String showLeavePage(Model model, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        
    	if (principalDetails == null) {
            return "redirect:/login"; // 로그인 페이지로 리다이렉트
        }
    	// 유저정보
    	Employees loginUser = principalDetails.getUser();
    	LocalDate startDate = loginUser.getEmpStartDate();
    	
    	commonCodeName(model, commonService);

//    	leaveService.updateEmployeesAnnualLeaveBase();
    	
    	   	
    	
    	// 어드민은 모든정보 보기, 매니저는 자기 부서만, 사원은 자기거만 보기
    	if (principalDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))){
    		// ADMIN은 모든 휴가정보 조회
    		List<LeaveDTO> leaveList = leaveService.getAllLeaves();
    		model.addAttribute("leaveList", leaveList);
    		
    		// ADMIN은 모든 휴가 보유내역 조회
    		List<LeaveDTO> usedLeftList = leaveService.getAllUsedLeftLeaves();
    		model.addAttribute("usedLeftList" ,usedLeftList);
    		
    	}else if (principalDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_MANAGER"))) {
    		// MANAGER는 같은 부서 출결정보 조회
    		List<LeaveDTO> leaveList = leaveService.getLeavesByDept(loginUser.getDeptNo());
        	model.addAttribute("leaveList", leaveList);
        	
        	// MANAGER은 같은 부서 휴가 보유내역 조회
    		List<LeaveDTO> usedLeftList = leaveService.getUsedLeftLeavesByDept(loginUser.getDeptNo());
    		model.addAttribute("usedLeftList" ,usedLeftList);
        	
    	} else {
    		// USER는 본인의 출결정보만 조회
    		List<LeaveDTO> leaveList = leaveService.getLeavesByEmpId(loginUser.getEmpId());
    		model.addAttribute("leaveList", leaveList);
    		
    		// USER는 개인 휴가 보유내역 조회
    		List<LeaveDTO> usedLeftList = leaveService.getUsedLeftLeavesByEmpId(loginUser.getEmpId());
    		model.addAttribute("usedLeftList" ,usedLeftList);
    	}
    	
    	
    	return "erp/attendanceMgt/leaveList";

    	
    }
    
    
    

    
    
    
    
}