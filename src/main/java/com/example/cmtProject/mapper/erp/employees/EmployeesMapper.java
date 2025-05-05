package com.example.cmtProject.mapper.erp.employees;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.multipart.MultipartFile;

import com.example.cmtProject.dto.erp.employees.EmpCountDTO;
import com.example.cmtProject.dto.erp.employees.EmpDTO;
import com.example.cmtProject.dto.erp.employees.EmpListPreviewDTO;
import com.example.cmtProject.dto.erp.employees.EmpRegistDTO;
import com.example.cmtProject.dto.erp.employees.searchEmpDTO;

@Mapper
public interface EmployeesMapper {
	
	// ADMIN 전원 사원리스트 조회
	List<EmpListPreviewDTO> selectEmpList();
	
	// MANAGER 같은 부서 사원 리스트 조회
	List<EmpListPreviewDTO> getEmpListDept(@Param("deptNo") Long deptNo);

	// USER 본인 사원만 조회
	List<EmpListPreviewDTO> getEmpListUser(@Param("empNo") Long empNo);

	List<searchEmpDTO> selectDept(searchEmpDTO searchEmpDTO);

	int insertEmp(EmpRegistDTO empRegistDTO);


	int updateEmp(EmpRegistDTO dto);

	EmpRegistDTO  selectEmpDetail(String id);


	EmpRegistDTO selectMyEmpList(String empId);

	int updateEmpDetail(String id);
	//아이디 중복체크
	int selectEmpId(String empId);
	//아이디 찾기
	String selectId(Map<String, String> map);
	//사원 현황
	EmpCountDTO selectCount(EmpCountDTO countDTO);
	//부서별 현황
	List<EmpCountDTO> selectDeptCount(EmpCountDTO countDTO);
	
	//입퇴사자
	List<Map<String, Object>> getJoinersPerMonth();
	List<Map<String, Object>> getLeaversPerMonth();
	//로그인한 유저의 정보
	EmpDTO selectLoginUser(String empId);



}
