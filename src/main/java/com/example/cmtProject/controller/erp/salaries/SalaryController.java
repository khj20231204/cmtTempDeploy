package com.example.cmtProject.controller.erp.salaries;


import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JexlExpression;
import org.apache.commons.jexl3.MapContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.example.cmtProject.dto.comm.CommonCodeDetailDTO;
import com.example.cmtProject.dto.comm.CommonCodeDetailNameDTO;
import com.example.cmtProject.dto.erp.employees.EmpListPreviewDTO;
import com.example.cmtProject.dto.erp.salaries.PayBasicDTO;
import com.example.cmtProject.dto.erp.salaries.PayCmmCodeDetailDTO;
import com.example.cmtProject.dto.erp.salaries.PayEmpListDTO;
import com.example.cmtProject.dto.erp.salaries.PaymentDTO;
import com.example.cmtProject.service.comm.CommonService;
import com.example.cmtProject.service.erp.employees.EmployeesService;
import com.example.cmtProject.service.erp.salaries.SalaryService;
import com.example.cmtProject.util.PdfGenerator;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;


@Controller
@RequestMapping("/salaries")
@Slf4j
public class SalaryController { // 급여 관리 Controller
	
	@Autowired
	private SalaryService salaryService;
	
	@Autowired
	private EmployeesService employeesService;
	
	@Autowired
	private CommonService commonService;
	
	@Autowired
	private SpringTemplateEngine templateEngine;
	
	// HTML 파일 기반으로 PDF 파일 생성
	public String parseThymeleafToHtml(Map<String, Object> data) {
	    Context context = new Context();
	    context.setVariables(data);
	    
	    String html = templateEngine.process("pdf/paySlip", context);
	    
	    return html;  // html 파일 경로
	}

	// 급여 지급 내역 조회
	@GetMapping("/payList")
	public String getPayList(Model model) {

	    // 현재 로그인한 사용자 정보 가져오기
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    String currentUserId = auth.getName();

	    // ADMIN 권한 여부 확인
	    boolean isAdmin = auth.getAuthorities().stream()
	            .anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"));

	    // empId: 사원은 본인 ID, 관리자는 null (전체 조회)
	    String empIdForQuery = isAdmin ? null : currentUserId;

	    // 공통 코드 바인딩
	    commonCodeName(model, commonService);

	    // 급여 목록 조회 (권한에 따라 전체 또는 본인만)
	    List<PaymentDTO> payList = salaryService.getPayList(empIdForQuery);
	    model.addAttribute("payList", payList);

	    // 사원 목록
	    List<EmpListPreviewDTO> empList = salaryService.getEmpList();
	    model.addAttribute("empList", empList);

	    // 급여 지급일 공통 코드에서 가져오기
	    List<CommonCodeDetailDTO> payDay = commonService.getCommonCodeDetails("PAYDAY", null);
	    model.addAttribute("payDay", payDay);
	    
	    // 현재 월 (yyyy-MM) 형식으로 설정
	    String currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
	    model.addAttribute("currentMonth", currentMonth);

	    return "erp/salaries/payList";
	}
	
	// 급여 이체
	@PostMapping("/payTransfer")
	@ResponseBody
	public String payTransfer(@RequestParam("empIds") List<String> empIds, @RequestParam("payMonth") String payMonth, Model model) {	

		String paydayStr = salaryService.getPayDay(); // 공통코드에서 설정한 급여일(ex: "8")을 숫자로 변환
		int payday = Integer.parseInt(paydayStr);     // 문자열 -> 숫자 변환

		LocalDate today = LocalDate.now();            // 오늘 날짜
		int todayDay = today.getDayOfMonth();         // 오늘의 '일'
		int currentMonth = today.getMonthValue();     // 현재 '월'

		// 오늘이 급여일이면 급여 계산 + 이체 진행
		if (todayDay == payday) {
		    // 급여일이 맞는 경우
		    System.out.println("오늘은 급여일입니다.");
		    
			// 직급별 기본급 가져오기
			List<PayBasicDTO> payBasicList = salaryService.getPayBasic();
			
			// 공통 코드에서 수당, 공제 계산 하기위한 컬럼 가져오기
			List<PayCmmCodeDetailDTO> payCommList = salaryService.getPayCommonCodeDetails();
			
			// 연산 결과를 입력할 List
			List<Map<String, Object>> evaluatedResult = new ArrayList<>();
			
			// 선택된 사원의 급여 계산용 정보 조회
			List<PayEmpListDTO> payEmpList = salaryService.getEmpInfo(empIds);
			
			// 사원 정보를 Map 형태로 변환하여 데이터로 저장
			for(PayEmpListDTO p : payEmpList) {
				// 급여 계산을 위한 사원 정보 Map으로 담음
				Map<String, Object> calcularatorMap = new HashMap<>();
				
				calcularatorMap.put("deptName",  p.getDeptName());
				calcularatorMap.put("empId",  p.getEmpId());
				calcularatorMap.put("empName",  p.getEmpName());
				calcularatorMap.put("empType",  p.getEmpType());
				calcularatorMap.put("payBasic",  p.getPayBasic());
				calcularatorMap.put("salBankName",  p.getSalBankName());
				calcularatorMap.put("salBankAccount",  p.getSalBankAccount());
				calcularatorMap.put("position",  p.getPosition());
				calcularatorMap.put("payDate",  p.getPayDate());
				
				// 직급에 따라서 달라지는 기본급
				Long tempPayBasic = Long.valueOf(p.getPayBasic());
				
				// 급여 계산 시작 ===================================================
				
				// 수식 평가 반복 => 수당, 공제 계산
				for(PayCmmCodeDetailDTO payComm : payCommList) {
					String expression = payComm.getCmnDetailValue2(); 
					String columnName = payComm.getCmnDetailValue(); 
					
					// 명절 수당은 1월 또는 9월에만 계산
					if ("payBonusHoliday".equalsIgnoreCase(columnName)) {
						if (!(currentMonth == 1 || currentMonth == 9)) {
							calcularatorMap.put(columnName, BigDecimal.ZERO);
							continue;
						}
					}
					
					// 수식에서 피연산자만 추출
					String[] operandNames =  expression.split("[+\\-\\*/\\(\\)]");
					
					// 수식 평가 수행할 JexlEngine 객체 생성
					JexlEngine jexl = new JexlBuilder().create();
					
					// 문자열 수식을 JexlExpression 객체를 통해 실제 식으로 변환
					JexlExpression jexlExpression = jexl.createExpression(expression); 
					
					// 수식에 사용될 피연산자를 관리하는 JexlContext 객체 생성
					JexlContext context = new MapContext();
					context.set("PAY_BASIC", tempPayBasic);
					
					// 연산식에 피연산자 대입하여 실제 연산 수행 후 Object 타입으로 결과값 리턴
					Object result = jexlExpression.evaluate(context);
					
					if (result instanceof Number) {
					    // Number로 캐스팅 후 double로 변환하여 BigDecimal로 래핑
					    BigDecimal value = BigDecimal.valueOf(((Number) result).doubleValue());
					    calcularatorMap.put(columnName, value);
					} else {
					    // 숫자가 아니면 null 처리하거나 예외 처리
					    calcularatorMap.put(columnName, null);  // 또는 throw new IllegalArgumentException(...)
					}
				}
				evaluatedResult.add(calcularatorMap);
				// 급여 계산 끝 ======================================================
			}
			
			// 급여 계산된 결과 => DB에 저장
			for(Map<String, Object> m : evaluatedResult) {
				
				PaymentDTO pdto = new PaymentDTO();
				
				salaryService.savePaymentMap(m);
				//salaryService.savePaymentDto(pdto);

				for (Map.Entry<String, Object> entry : m.entrySet()) {
				    System.out.println(entry.getKey() + " : " + entry.getValue());
				}
				
			}
				return "success";
		    
			} else {
				// 급여일이 아닌 경우
				System.out.println("오늘은 급여일이 아닙니다.");
				return "fail";
			}

//	    //int dayOfMonth = Integer.parseInt(payday.getCmnDetailValue());
//	    LocalDate today = LocalDate.now();
//	    int todayInt = today.getDayOfMonth();
//	    String todayStr = String.valueOf(todayInt);
		
//	    int year = today.getYear();
//	    int month = today.getMonthValue();
//	    LocalDate date = LocalDate.of(year, month, 8); // 그달의 20일
	    
	    //지급일이 아닌 경우 바로 return
//	    if(!(payday.equals(todayStr) && !isHoliday(date))) {
//	    	return "fail";
//	    }
	}
	
	// 공휴일 판별하는 메서드
	public boolean isHoliday(LocalDate date) {
	    DayOfWeek day = date.getDayOfWeek();
	    
	    return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
	}
	
	// 미지급자 조회
	@GetMapping("/unpaidEmpList")
	@ResponseBody
	public List<PayEmpListDTO> getUnpaidEmployees(@RequestParam("payMonth") String payMonth) {
		return salaryService.findUnpaidEmployees(payMonth);
	}

	// 월별 급여 대장 간략 조회
	@GetMapping("/payroll")
	public String getPayroll(Model model) {
		List<PaymentDTO> payrollSummaryList = salaryService.getMonthlyPayrollSummaryList();
		model.addAttribute("payrollSummaryList", payrollSummaryList);
		
		return "erp/salaries/payroll";
	}
	
	// 월별 급여 대장 상세 조회
	@PostMapping("/payroll/month")
	@ResponseBody
	public Map<String, Object> getPayrollDetail(@RequestParam("payMonth") String payMonth) {
		Map<String, Object> result = new HashMap<>();
		result.put("payList", salaryService.getMonthlyDeptPayrollList(payMonth)); // 급여 현황
		result.put("payTotal", salaryService.getMonthlyPayrollTotalList(payMonth)); // 전 직원 급여 합계

	    return result;
	}
	
	// 연간 급여 대장 조회
	@PostMapping("/payroll/yearly")
	@ResponseBody
	public Map<String, Object> getPayrollYearly(@RequestParam("payYear") String payYear) {
		// 연간 급여대장
		List<Map<String, Object>> resultList = salaryService.getYearlyPayrollList(payYear);
		// 연도 리스트
		List<Integer> yearList = salaryService.getYears();
		
		Map<String, Object> result = new HashMap<>();
	    result.put("DATA", resultList);
	    result.put("YEARS", yearList);
	    
		return result;
	}

	//==========================================
	
	@GetMapping("insertPayForm/{empId}")
	public String insertPayForm(@PathVariable("empId") String empId,PaymentDTO paymentDTO,Model model) {
		PaymentDTO payList = salaryService.getEmpPayment(empId);
		model.addAttribute("pay",payList);
		System.out.println("개인 지급내역>>>"+payList);
		
		return"erp/salaries/insertPayForm";
	}
	
	@PostMapping("/insertPay")
	@ResponseBody
	public String insertPay(Model model) {
		return"이체가 완료 되었습니다.";
	}
	
	//----------------------------------------------------------------------------------------------------	
	
	// PDF 급여 명세서 출력
	@GetMapping("/payPrint/{empId}")
	public String patPrint(@PathVariable("empId") String empId,PaymentDTO paymentDTO,Model model) throws Exception {
		PaymentDTO payList = salaryService.getEmpPayment(empId);
		
		Map<String, Object> data = new HashMap<>();
		data.put("pay", payList);
		data.put("today", LocalDate.now());

		String html = parseThymeleafToHtml(data); // 위에서 만든 메서드
		new PdfGenerator().generatePdf(html, "D:/pdfs/payslip.pdf");
		model.addAttribute("pay", payList); 
		System.out.println(">>>>>>>>>>>뿌려질 내용" + payList);
		
		return "pdf/paySlip";
	}
	
	//----------------------------------------------------------------------------------------------------	
	
	// 엑셀 파일 다운로드
	@GetMapping("/excel-file-down")
	public void downloadExcel(HttpServletResponse response) throws IOException {
		String fileName = "payroll_form.xls";
		String filePath = "/excel/" + fileName;

		// /static/ 디렉토리 기준으로 파일을 읽어옴
		log.info("filePath:" + filePath);
		InputStream inputStream = new ClassPathResource(filePath).getInputStream();

		log.info("inputStream:" + inputStream);
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

		// 파일 내용을 응답 스트림에 복사
		StreamUtils.copy(inputStream, response.getOutputStream());
		response.flushBuffer();
	}
	
		
	//=====================================================
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
