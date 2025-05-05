package com.example.cmtProject.service.erp.salaries;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.cmtProject.dto.erp.employees.EmpListPreviewDTO;
import com.example.cmtProject.dto.erp.salaries.PayBasicDTO;
import com.example.cmtProject.dto.erp.salaries.PayCmmCodeDetailDTO;
import com.example.cmtProject.dto.erp.salaries.PayEmpListDTO;
import com.example.cmtProject.dto.erp.salaries.PaymentDTO;
import com.example.cmtProject.mapper.erp.salaries.SalariesMapper;
import com.example.cmtProject.repository.erp.salaries.SalaryRepository;

@Service
public class SalaryService { // 급여 관리 Service
	@Autowired
	private SalaryRepository salRepository;
	@Autowired
	private SalariesMapper salMapper;	
	
	// 급여 지급 내역 조회
	public List<PaymentDTO> getPayList(String empId) {
		return salMapper.getPayList(empId);
	}
	
	/*
	 * 야근 수당 계산
	 * public List<PaymentDTO> getOverTimes(PaymentDTO paymentDTO) {
	 * return salMapper.getOverTimes(paymentDTO); }
	 */

	// 직급별 기본급 조회
	public List<PayBasicDTO> getPayBasic() {
		return salMapper.getPayBasic();
	}	

	// 사원 정보 조회
	public List<PayEmpListDTO> getEmpInfo(List<String> empNoList) {
		return salMapper.getEmpInfo(empNoList);
	}

	// 급여 지급일 조회
	public String getPayDay() {
		return salMapper.getPayDay();
	}

	// 공통 코드에서 수당, 공제 계산 하기위한 컬럼 가져오기
	public List<PayCmmCodeDetailDTO> getPayCommonCodeDetails() {
		return salMapper.getPayCommonCodeDetails();
	}

	// 사원 목록
	public List<EmpListPreviewDTO> getEmpList() {
		return salMapper.getEmpList();
	}

	// 급여 이체
	public void savePaymentMap(Map<String, Object> m) {
		salMapper.savePaymentMap(m);
	}

	/*
	 * 급여 이체 
	 * public void savePaymentDto(PaymentTempDTO pdto) {
	 * salMapper.savePaymentDto(pdto); }
	 */
	
	// 미지급자 조회
	public List<PayEmpListDTO> findUnpaidEmployees(String payMonth) {
		return salMapper.findUnpaidEmployees(payMonth);
	}
	
	// 월별 급여 대장 간략 조회
	public List<PaymentDTO> getMonthlyPayrollSummaryList() {
		return salMapper.getMonthlyPayrollSummaryList();
	}
	
	// 월별 급여 대장 - 급여 현황
	public List<PaymentDTO> getMonthlyDeptPayrollList(String payMonth) {
		List<PaymentDTO> result = salMapper.getMonthlyDeptPayrollList(payMonth);
		
		return result;
	}
	
	// 월별 급여 대장 - 전 직원 급여 합계
	public Map<String, Object> getMonthlyPayrollTotalList(String payMonth) {
		return salMapper.getMonthlyPayrollTotalList(payMonth);
	}

	// 연간 급여 대장
	public List<Map<String, Object>> getYearlyPayrollList(String payYear) {
		return salMapper.selectYearlyPayrollList(payYear);
	}
	
	// 연간 급여 대장 - 연도 가져오기
	public List<Integer> getYears() {
		return salMapper.getYears();
	}
	
	
	// ---------------------------------------------------
	
	// 개인 지급내역
	public PaymentDTO getEmpPayment(String empId) {
		return salMapper.selectEmpPayment(empId);
	}

}
