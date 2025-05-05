package com.example.cmtProject.controller.erp.notice;

import java.security.Principal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.cmtProject.dto.comm.CommonCodeDetailNameDTO;
import com.example.cmtProject.dto.erp.employees.EmpDTO;
import com.example.cmtProject.dto.erp.employees.EmpRegistDTO;
import com.example.cmtProject.dto.erp.employees.searchEmpDTO;
import com.example.cmtProject.dto.erp.notice.NoticeDTO;
import com.example.cmtProject.entity.erp.notice.Notice;
import com.example.cmtProject.service.comm.CommonService;
import com.example.cmtProject.service.erp.employees.EmployeesService;
import com.example.cmtProject.service.erp.notice.NoticeService;

import jakarta.websocket.Session;

@Controller
public class NoticeController {
	@Autowired NoticeService noticeService;
	@Autowired EmployeesService empService;
	@Autowired CommonService commonService;
	
	@GetMapping("/noticeList")
	public String noticeList(Model model,NoticeDTO noticeDTO) {
		commonCodeName(model, commonService);
		List<NoticeDTO> noticeList = noticeService.getAllNoticesWithNames();
		model.addAttribute("noticeList",noticeList);
		model.addAttribute("searchEmpDTO", new searchEmpDTO());
		
		return "erp/notice/noticeList";
	}
	
	@GetMapping("/noticeForm")
	public String noticeForm(Model model, Principal principal,EmpDTO empDTO,NoticeDTO noticeDTO) {
		String empId = principal.getName();
		EmpDTO loginUser = empService.getEmpList(empId);
		model.addAttribute("loginUser", loginUser);
		model.addAttribute("today", LocalDate.now());
		
		return "erp/notice/noticeForm";
	}
	
	@PostMapping("/noticeForm")
	public String registNotice(@ModelAttribute NoticeDTO noticeDTO) {
		int notice = noticeService.regiNoti(noticeDTO);
		if(notice > 0) {
			return "redirect:/noticeList";
		} else {
			return "erp/notice/noticeList";
		}
		
	}
	
	@GetMapping("/notice/detail/{id}")
	public String noticeDetail(@PathVariable("id") Long  id,Model model) {
		NoticeDTO noticeList = noticeService.getNoticeDetail(id);
	        model.addAttribute("notice", noticeList);
	        
	        return "erp/notice/noticeDetail";
	    }
	
	@PostMapping("/deleteNotice/{noticeId}")
	@ResponseBody
	public String deleteNotice(@PathVariable("noticeId") Long noticeId,Model model,Principal principal) {
		int result = noticeService.deleteById(noticeId);
		 Map<String, Object> response = new HashMap<>();
		 String empId = principal.getName(); //empid
		 
		 return result > 0 ? "response" : "";
	}
	
	///****사원 셀랙트박스****/
	@PostMapping("/notice/searchEmp")
	public String searchDept(@ModelAttribute searchEmpDTO searchEmpDTO,Model model)throws Exception {
		commonCodeName(model, commonService);
		List<NoticeDTO> noticeList = noticeService.getAllNoticesWithNames();
		model.addAttribute("noticeList",noticeList);
		model.addAttribute("searchEmpDTO", new searchEmpDTO());
		
		return "erp/notice/noticeList";
	}
	
	//=========================
	//공통코드 DetailName 불러오는 메서드
		public static void commonCodeName(Model model , CommonService commonService) {
			
			List<String> groupCodes = commonService.getAllGroupCodes();
			System.out.println("그룹코드 리스트 :::::"+groupCodes);
//			String[] groupCodes = {"GENDER","DEPT","EDUCATION","EMP_STATUS","EMP_TYPE","MARITAL","PARKING","POSITION","USER_ROLE"};
			//공통코드 추가시 "NEW_CODE" 추가
			
			Map<String, List<CommonCodeDetailNameDTO>> commonCodeMap = new HashMap<>();
			
			for(String groupCode : groupCodes) {
				commonCodeMap.put(groupCode, commonService.getCodeListByGroup(groupCode));
			}
			model.addAttribute("commonCodeMap",commonCodeMap);
		}
}
