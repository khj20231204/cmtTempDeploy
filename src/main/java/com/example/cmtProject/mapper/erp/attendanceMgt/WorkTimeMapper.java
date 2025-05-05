package com.example.cmtProject.mapper.erp.attendanceMgt;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.cmtProject.dto.erp.attendanceMgt.WorkTemplateDTO;
import com.example.cmtProject.dto.erp.attendanceMgt.WorkTimeDTO;
import com.example.cmtProject.entity.erp.employees.Employees;

@Mapper
public interface WorkTimeMapper {
	
	// 어드민 모든 출결 정보 조회
	List<WorkTimeDTO> getAllAttends();
	
	// 모달창, 어드민, 이미 설정된 사원 빼고 사원 출결 정보 조회
	List<WorkTimeDTO> getAllAttendsModal();
	
	// 매니저 같은 부서 출결 정보 조회
	List<WorkTimeDTO> getAttendsByDept(@Param("deptNo") Long deptNo);
	
	// 모달창, 매니저, 이미 설정된 사원 빼고 사원 출결 정보 조회
	List<WorkTimeDTO> getAllAttendsModalByDept(@Param("deptNo") Long deptNo);
	
	// 유저 개인 출결 정보 조회
	List<WorkTimeDTO> getAttendsByEmpNo(@Param("empNo") Long empNo);
	
	// 근무 일정 템플릿 조회
	List<WorkTemplateDTO> getAllWorkTemplate();
	
	// 사원 근무 시간 입력
	void insertWktTypeByEmpNo(WorkTimeDTO row);

	// 사원 근무 시간 수정
	void updateWktTypeByEmpNo(@Param("empNo") Long empNo, @Param("wktType") String wktType);

	// 사원 근무 목록 저장
	void saveWorkTemplates(WorkTemplateDTO templates);
	
	// 회사원의 근무일정 시간 추출
	LocalDateTime getWorkTemplateByEmpNo(Long empNo);

	



	
	
	

}
