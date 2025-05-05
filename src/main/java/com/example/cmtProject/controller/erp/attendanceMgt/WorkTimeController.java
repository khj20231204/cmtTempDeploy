package com.example.cmtProject.controller.erp.attendanceMgt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import com.example.cmtProject.dto.erp.attendanceMgt.WorkTemplateDTO;
import com.example.cmtProject.dto.erp.attendanceMgt.WorkTimeDTO;
import com.example.cmtProject.dto.erp.employees.EmpListPreviewDTO;
import com.example.cmtProject.entity.erp.employees.Employees;
import com.example.cmtProject.entity.erp.employees.PrincipalDetails;
import com.example.cmtProject.repository.erp.attendanceMgt.WorkTimeRepository;
import com.example.cmtProject.service.comm.CommonService;
import com.example.cmtProject.service.erp.attendanceMgt.WorkTimeService;
import com.example.cmtProject.service.erp.employees.EmployeesService;

@Controller
@RequestMapping("/worktimes")
public class WorkTimeController {
	
	private static final Logger logger = LoggerFactory.getLogger(WorkTimeController.class);
	
	@Autowired
	private WorkTimeService workTimeService;
	@Autowired
	private WorkTimeRepository workTimeRepository;
	@Autowired
	private EmployeesService employeesService;
	@Autowired 
	private CommonService commonService;
	
	
	
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
    public String showWorkTimePage(Model model, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        
    	if (principalDetails == null) {
            return "redirect:/login"; // 로그인 페이지로 리다이렉트
        }
    	// 유저정보
    	Employees loginUser = principalDetails.getUser();
    	
    	commonCodeName(model, commonService);
    	
    	// 어드민은 모든정보 보기, 매니저는 자기 부서만, 사원은 자기거만 보기
    	if (principalDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))){
    		// ADMIN은 모든 출결정보 조회
    		List<WorkTimeDTO> workTimeList = workTimeService.getAllAttends();
    		model.addAttribute("workTimeList", workTimeList);
    		
    		// 모달창, 어드민, 이미 설정된 사원 빼고 사원 출결 정보 조회
    		List<WorkTimeDTO> modalList = workTimeService.getAllAttendsModal();
    		model.addAttribute("modalList", modalList);
    		
    		// ADMIN은 모든 사원 정보 조회
    		List<EmpListPreviewDTO> empList = employeesService.getEmpList();
    		model.addAttribute("empList", empList);
    		
    		// 모달창, ADMIN과 MANAGER은 근무 일정 템플릿을 조회할 수 있어야한다
    		List<WorkTemplateDTO> templateList = workTimeService.getAllWorkTemplate();
    		model.addAttribute("templateList", templateList);
    		
    	}else if (principalDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_MANAGER"))) {
    		// MANAGER는 같은 부서 출결정보 조회
    		List<WorkTimeDTO> workTimeList = workTimeService.getAttendsByDept(loginUser.getDeptNo());
        	model.addAttribute("workTimeList", workTimeList);
        	
        	// 모달창, 매니저, 이미 설정된 사원 빼고 사원 출결 정보 조회
    		List<WorkTimeDTO> modalList = workTimeService.getAllAttendsModalByDept(loginUser.getDeptNo());
    		model.addAttribute("modalList", modalList);
        	
        	// MANAGER은 같은 부서 정보 조회
        	List<EmpListPreviewDTO> empList = employeesService.getEmpListDept(loginUser.getDeptNo());
    		model.addAttribute("empList", empList);
    		
    		// 모달창, ADMIN과 MANAGER은 근무 일정 템플릿을 조회할 수 있어야한다
    		List<WorkTemplateDTO> templateList = workTimeService.getAllWorkTemplate();
    		model.addAttribute("templateList", templateList);
        	
    	} else {
    		// USER는 본인의 출결정보만 조회
    		List<WorkTimeDTO> workTimeList = workTimeService.getAttendsByEmpNo(loginUser.getEmpNo());
    		model.addAttribute("workTimeList", workTimeList);
    		
    		// USER는 같은 부서 정보 조회
        	List<EmpListPreviewDTO> empList = employeesService.getEmpListUser(loginUser.getEmpNo());
    		model.addAttribute("empList", empList);
    	}
        return "erp/attendanceMgt/workTimeList"; // templates/erp/attendanceMgt/attendList.html 렌더링
    }
    
    
    
    // 출결 정보 수정
    @PostMapping("/updateWktType")
    @ResponseBody
    public ResponseEntity<?> updateWktType(@RequestBody List<WorkTimeDTO> updatedRows) {
        try {
            for (WorkTimeDTO row : updatedRows) {
                Long empNo = row.getEmpNo();
                String wktType = row.getWktType();
                
                workTimeService.insertWktTypeByEmpNo(row);
                workTimeService.updateWktTypeByEmpNo(empNo, wktType);
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("DB 업데이트 실패");
        }
    }
    
    // 근무 일정 관리 저장
    @PostMapping("/template/save")
    @ResponseBody
    public ResponseEntity<Void> saveWorkTemplates(@RequestBody List<WorkTemplateDTO> templates) {
        workTimeService.saveWorkTemplates(templates);
        return ResponseEntity.ok().build();
    }
    
    
    
    // 출결 정보 삭제
    @PostMapping("/delete")
    @ResponseBody
    public ResponseEntity<String> deleteWorkTimes(@RequestBody Map<String, List<Long>> data) {
        List<Long> ids = data.get("ids");

        // 삭제 로직 실행 (예: attendService.deleteByIds(ids))
        workTimeRepository.deleteAllById(ids);

        return ResponseEntity.ok("success");
    }
    
    // 근무 일정 관리 삭제
    @PostMapping("/template/delete")
    @ResponseBody
    public ResponseEntity<?> deleteWorkTemplates(@RequestBody List<Long> ids) {
        try {
            workTimeService.deleteTemplatesByIds(ids);
            return ResponseEntity.ok("삭제 완료");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("삭제 실패");
        }
    }
    
	

}
