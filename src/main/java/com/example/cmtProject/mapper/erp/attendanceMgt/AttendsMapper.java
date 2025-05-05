package com.example.cmtProject.mapper.erp.attendanceMgt;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.cmtProject.dto.erp.attendanceMgt.AttendDTO;
import com.example.cmtProject.dto.erp.attendanceMgt.WorkTemplateDTO;
import com.example.cmtProject.entity.erp.employees.Employees;

@Mapper
public interface AttendsMapper {
	
	// 어드민 모든 출결 정보 조회
	List<AttendDTO> getAllAttends();
	
	// 매니저 같은 부서 출결 정보 조회
	List<AttendDTO> getAttendsByDept(@Param("deptNo") Long deptNo);
	
	// 유저 개인 출결 정보 조회
	List<AttendDTO> getAttendsByEmpNo(@Param("empNo") Long empNo);
	
	 // 출근 했을시 출근 버튼 숨기기
    boolean hasCheckedInToday(@Param("empNo") Long empNo,
                              @Param("start") LocalDateTime start,
                              @Param("end") LocalDateTime end);

    // 가장 최근 출근 버튼 ATD_NO 출력
    Long findLatestCheckInAtdNo(@Param("empNo") Long empNo);
    
    // 퇴근 했을시 퇴근 버튼 숨기기
    boolean hasCheckedOutToday(@Param("atdNo") Long atdNo,
                               @Param("start") LocalDateTime start,
                               @Param("end") LocalDateTime end);

    // 퇴근 시간 업데이트
    int updateAttendLeave(@Param("atdNo") Long atdNo,
                          @Param("atdLeave") LocalDateTime atdLeave,
                          @Param("atdType") String atdType);

	void insertAttend(@Param("dto") AttendDTO dto, @Param("emp") Employees emp);

	// 결근 체크
	boolean existsAttendStatus(@Param("empNo") Long empNo,
            				   @Param("date") LocalDate checkDate);
	

}
