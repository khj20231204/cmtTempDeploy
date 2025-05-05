package com.example.cmtProject.mapper.erp.salaries;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.cmtProject.dto.erp.employees.EmpListPreviewDTO;
import com.example.cmtProject.dto.erp.salaries.PayBasicDTO;
import com.example.cmtProject.dto.erp.salaries.PayCmmCodeDetailDTO;
import com.example.cmtProject.dto.erp.salaries.PayEmpListDTO;
import com.example.cmtProject.dto.erp.salaries.PaymentDTO;

@Mapper
public interface SalariesMapper { // 급여 관리 Mapper
	
	// 급여 지급 내역 조회
	List<PaymentDTO> getPayList(@Param("empId") String empId);
	
	/*
	 * 야근 수당 계산
	 * List<PaymentDTO> getOverTimes(PaymentDTO paymentDTO);
	 */
	
	// 직급별 기본급 조회
	List<PayBasicDTO> getPayBasic();

	// 사원 정보 조회
	List<PayEmpListDTO> getEmpInfo(@Param("empNoList") List<String> empNoList);

	// 급여 지급일 조회
	String getPayDay();

	// 공통 코드에서 수당, 공제 계산 하기위한 컬럼 가져오기
	List<PayCmmCodeDetailDTO> getPayCommonCodeDetails();

	// 사원 목록
	List<EmpListPreviewDTO> getEmpList();

	// 급여 이체
	void savePaymentMap(Map<String, Object> m);

	/*
	 * 급여 이체
	 * void savePaymentDto(PaymentTempDTO pdto);
	 */
	
	// 미지급자 조회
	List<PayEmpListDTO> findUnpaidEmployees(String payMonth);
	
	// 월별 급여 대장 간략 조회
	List<PaymentDTO> getMonthlyPayrollSummaryList();

	// 월별 급여 대장 - 급여 현황
	List<PaymentDTO> getMonthlyDeptPayrollList(String payMonth);

	// 월별 급여 대장 - 전 직원 급여 합계
	Map<String, Object> getMonthlyPayrollTotalList(String payMonth);

	// 연간 급여 대장
	List<Map<String, Object>> selectYearlyPayrollList(String payYear);

	// 연간 급여 대장 - 연도 가져오기
	List<Integer> getYears();
	
	
	// ---------------------------------------------------
	
	// 개인 지급내역
	PaymentDTO selectEmpPayment(String empId);
	
}
