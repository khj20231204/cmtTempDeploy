package com.example.cmtProject.mapper.erp.attendanceMgt;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.cmtProject.dto.erp.attendanceMgt.LeaveDTO;

@Mapper
public interface LeaveMapper {

	// ADMIN은 모든 휴가정보 조회
	List<LeaveDTO> getAllLeaves();
	
	// MANAGER는 같은 부서 출결정보 조회
	List<LeaveDTO> getLeavesByDept(Long deptNo);
	
	// USER는 본인의 출결정보만 조회
	List<LeaveDTO> getLeavesByEmpId(String empId);
	
	// ADMIN은 모든 휴가 보유내역 조회
	List<LeaveDTO> getAllUsedLeftLeaves();
	
	// MANAGER은 같은 부서 휴가 보유내역 조회
	List<LeaveDTO> getUsedLeftLeavesByDept(Long deptNo);
	
	// USER는 개인 휴가 보유내역 조회
	List<LeaveDTO> getUsedLeftLeavesByEmpId(String empId);

	// 휴가 일정 관리 저장
	void insertLeave(@Param("dto") LeaveDTO dto, @Param("empId") String empId, @Param("docId") String docId);
	
	// 1년에 한번 휴가일수 추가되는 메서드
	void updatelLeaveLeftDays(@Param("empId") String empId, @Param("levLeftDays") int levLeftDays);

	// 사원 추가시 휴가일도 추가
	void insertLeaveEmp(@Param("empId") String empId, @Param("vacationDaysDays") int vacationDaysDays);

	
	
	
	//////////////////////////////////////////////////////////////////////////////
   // 결재 연동을 위한 메서드 추가
    
    // 결재 문서ID로 휴가 정보 조회
    LeaveDTO getLeaveByDocId(@Param("docId") String docId);
    
    // 결재 문서ID와 함께 휴가 저장
    void insertLeaveWithDocId(@Param("dto") LeaveDTO dto, 
                              @Param("empId") String empId, 
                              @Param("docId") String docId);
    
    // 휴가 상태 업데이트
    int updateLeaveStatus(@Param("levNo") Long levNo, 
                          @Param("status") String status, 
                          @Param("remarks") String remarks);
    
    // 휴가인지 체크
    boolean isOnLeave(@Param("empId") String empId,
            		  @Param("date") LocalDate date);

}


