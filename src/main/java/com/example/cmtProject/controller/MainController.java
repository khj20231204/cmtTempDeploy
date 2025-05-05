 package com.example.cmtProject.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.cmtProject.dto.erp.eapproval.DocumentDTO;
import com.example.cmtProject.dto.erp.employees.EmpRegistDTO;
import com.example.cmtProject.dto.erp.employees.EmpDTO;
import com.example.cmtProject.dto.erp.notice.NoticeDTO;
import com.example.cmtProject.entity.erp.employees.Employees;
import com.example.cmtProject.entity.erp.employees.PrincipalDetails;
import com.example.cmtProject.repository.erp.employees.EmployeesRepository;
import com.example.cmtProject.service.erp.eapproval.DocFormService;
import com.example.cmtProject.service.erp.employees.EmployeesService;
import com.example.cmtProject.service.erp.notice.NoticeService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/")
public class MainController {
	
	@Autowired private EmployeesRepository empRepository;
	@Autowired private DocFormService docFormService;
	@Autowired private NoticeService noticeService;
	@Autowired private EmployeesService empService;
	
	@Autowired
	private BCryptPasswordEncoder bCrypPasswordEncoder;
	
	@GetMapping({"","/"})

	public String main(@AuthenticationPrincipal PrincipalDetails principal , Model model
						, RedirectAttributes redirectAttributes
						,DocumentDTO documentDTO , NoticeDTO noticeDTO) {
		
		if (principal ==null) {
			return "redirect:/login";
		}
		//결재대기중인 문서 갯수
		String empId = principal.getUser().getEmpId();
		int pendingApprovalCount = docFormService.countPendingDocumentsByEmpId(empId);
		int myDraftCount = docFormService.myDraftCount(empId);
		model.addAttribute("pendingApprovalCount",pendingApprovalCount);
		model.addAttribute("myDraftCount",myDraftCount);
		
		//공지사항
		List<NoticeDTO> noticeList = noticeService.getAllNoticesWithNames();
		model.addAttribute("noticeList",noticeList);
		
		EmpRegistDTO emp = empService.getMyEmpList(empId);
		model.addAttribute("emp",emp);
		
		EmpDTO loginUser = empService.getEmpList(empId);
		model.addAttribute("loginUser", loginUser);

		return "home";
	}
	
	@GetMapping("/login") //로그인폼
	public String loginForm(HttpServletRequest request, Model model) {
		String empId = null;
	    if (request.getCookies() != null) {
	        for (Cookie cookie : request.getCookies()) {
	            if ("empId".equals(cookie.getName())) {
	                empId = cookie.getValue();
	                break;
	            }
	        }
	    }
	    model.addAttribute("savedEmpId", empId);
	    return "login";
	}
	
	@GetMapping("/loginSuccess")
	public  String loginSucess() {
		
		System.out.println("login 성공");
		
		return "redirect:/";
//		return "<script>alert('로그인 성공'); location.href='/';</script>";
	}
	
	@GetMapping("/loginFail")
	public @ResponseBody String loginFail() {
		
		System.out.println("login 실패");
		return "<script>alert('[ 로그인 실패 ] \\n아이디와 비밀번호를 다시 확인해주세요'); location.href='/';</script>";
	}
	
	@GetMapping("/joinForm") //회원가입폼
	public String joinForm() {
		return "joinForm";
	}
	
	@PostMapping("/join")
	public String join(@ModelAttribute Employees emp) {
		
		emp.setEmpLevel("ROLE_"+emp.getEmpLevel()); //db에 입력데이터에 ROLE_ 이 붙는다
		System.out.println(emp);
		
		//암호화를 하지 않으면 security로 로그인 할 수 없음
		emp.setEmpPassword(bCrypPasswordEncoder.encode(emp.getEmpPassword()));
		empRepository.save(emp);
        
		return "join";
	}
	
	@GetMapping("/user")
	@ResponseBody
	public String user() {
		
		return "user";
	}
	
	@GetMapping("/manager")
	@ResponseBody
	public String manager() {
		
		return "manager";
	}
	
	@GetMapping("/admin")
	@ResponseBody
	public String admin() {
		
		return "admin";
	}
	
	@Secured("ROLE_ADMIN")
	@GetMapping("/onlyadmin")
	public @ResponseBody String onlyAdmin() {
		
		return "onlyAdmin";
	}
	
	@Secured("ROLE_MANAGER")
	@GetMapping("/onlymanager")
	public @ResponseBody String onlyManager() {
		
		return "onlyManager";
	}
	
	@PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
	@GetMapping("/useradmin")
	@ResponseBody
	public String useradmin() {
		
		return "user or admin";
	}
	
	@PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
	@GetMapping("/manageradmin")
	@ResponseBody
	public String manageradmin() {
		
		return "manager or admin";
	}
}
