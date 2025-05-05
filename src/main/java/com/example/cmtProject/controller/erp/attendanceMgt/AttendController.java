package com.example.cmtProject.controller.erp.attendanceMgt;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

import com.example.cmtProject.dto.erp.attendanceMgt.AttendDTO;
import com.example.cmtProject.entity.erp.employees.Employees;
import com.example.cmtProject.entity.erp.employees.PrincipalDetails;
import com.example.cmtProject.mapper.erp.attendanceMgt.AttendsMapper;
import com.example.cmtProject.repository.erp.attendanceMgt.AttendRepository;
import com.example.cmtProject.service.erp.attendanceMgt.AttendService;

@Controller
@RequestMapping("/attends")
public class AttendController {
	
	private static final Logger logger = LoggerFactory.getLogger(AttendController.class);
	
	@Autowired
    private AttendService attendService;
	
	@Autowired
	private AttendRepository attendRepository;
	
	@Autowired
	private AttendsMapper attendsMapper;

    // 출결 정보 목록 페이지 (HTML 렌더링)
    @GetMapping("/list")
    public String showAttendPage(Model model, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        
    	if (principalDetails == null) {
            return "redirect:/login"; // 로그인 페이지로 리다이렉트
        }
    	// 유저정보
    	Employees loginUser = principalDetails.getUser();
    	
    	
    	
        // 출근하면 퇴근 버튼만 보이게하기, 퇴근하면 출근 버튼만 보이게 하기
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = LocalDate.now().atTime(LocalTime.MAX);
        boolean hasCheckedIn = attendsMapper.hasCheckedInToday(loginUser.getEmpNo(), start, end);
        model.addAttribute("hasCheckedIn", hasCheckedIn);
        
        Long findLatestCheckInAtdNo = attendsMapper.findLatestCheckInAtdNo(loginUser.getEmpNo());
        boolean hasCheckedOut = true;
        if (findLatestCheckInAtdNo != null) {
            hasCheckedOut = attendsMapper.hasCheckedOutToday(findLatestCheckInAtdNo, start, end);
        }
        model.addAttribute("hasCheckedOut", hasCheckedOut);
       
        // 어드민은 모든정보 보기, 매니저는 자기 부서만, 사원은 자기거만 보기
    	if (principalDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))){
    		// ADMIN은 모든 출결정보 조회
    		List<AttendDTO> attendList = attendService.getAllAttends();
    		model.addAttribute("attendList", attendList);
    		
    	}else if (principalDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_MANAGER"))) {
    		// MANAGER는 같은 부서 출결정보 조회
    		List<AttendDTO> attendList = attendService.getAttendsByDept(loginUser.getDeptNo());
        	model.addAttribute("attendList", attendList);
        	
    	} else {
    		// USER는 본인의 출결정보만 조회
    		List<AttendDTO> attendList = attendService.getAttendsByEmpNo(loginUser.getEmpNo());
    		model.addAttribute("attendList", attendList);
    	}
        return "erp/attendanceMgt/attendList"; // templates/erp/attendanceMgt/attendList.html 렌더링
    }
    

    // 출결 정보 등록
    @PostMapping("/check-in")
    @ResponseBody
    public ResponseEntity<String> createAttend(@RequestBody AttendDTO dto, @AuthenticationPrincipal PrincipalDetails principalDetails) {
    	
    	// 로그인한 사용자의 아이디 가져오기
    	Employees loginUser = principalDetails.getUser();
    	
    	attendService.saveAttend(dto, loginUser);

        return ResponseEntity.ok("success");
    }
    
    // 퇴근 정보 등록
    @PostMapping("/check-out")
    @ResponseBody
//    public ResponseEntity<String> updateAttendLeave(@ModelAttribute("AttendDTO") AttendDTO dto, @AuthenticationPrincipal PrincipalDetails principalDetails) {
    public ResponseEntity<String> updateAttendLeave(@RequestBody Map<String, String> dto, @AuthenticationPrincipal PrincipalDetails principalDetails) {
//         로그인한 사용자의 정보 가져오기
        Employees loginUser = principalDetails.getUser();
        // 퇴근 처리 서비스 호출
        Long findLatestCheckInAtdNo = attendsMapper.findLatestCheckInAtdNo(loginUser.getEmpNo());
        attendService.updateAttendLeave(dto, findLatestCheckInAtdNo);

        return ResponseEntity.ok("success");
    }

    // 출결 정보 삭제
    @PostMapping("/delete")
    @ResponseBody
    public ResponseEntity<String> deleteAttends(@RequestBody Map<String, List<Long>> data) {
        List<Long> ids = data.get("ids");

        // 삭제 로직 실행 (예: attendService.deleteByIds(ids))
        attendRepository.deleteAllById(ids);

        return ResponseEntity.ok("success");
    }
    
    
    
    
    
    
    
    
    
    
}