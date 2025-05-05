package com.example.cmtProject.service.erp.attendanceMgt;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.cmtProject.dto.erp.attendanceMgt.LeaveDTO;
import com.example.cmtProject.dto.erp.employees.EmpDTO;
import com.example.cmtProject.dto.erp.employees.EmpListPreviewDTO;
import com.example.cmtProject.dto.erp.employees.EmpRegistDTO;
import com.example.cmtProject.entity.erp.employees.Employees;
import com.example.cmtProject.mapper.erp.attendanceMgt.LeaveMapper;
import com.example.cmtProject.mapper.erp.employees.EmployeesMapper;

@Service
public class LeaveService {

	private static final Logger logger = LoggerFactory.getLogger(LeaveService.class);
	
	@Autowired
	private LeaveMapper leaveMapper;
	@Autowired
	private EmployeesMapper employeesMapper;
	
	// 년차에 따른 연차 갯수 구하는 함수
	public int calculateAnnualLeaveDays(int yearsOfService) {
	    if (yearsOfService <= 0) {
	        return 12;
	    }

	    int leaveDays = 15 + (yearsOfService - 1) / 2;

	    return Math.min(leaveDays, 25);  // 최대 25일 제한
	}
	

	// ADMIN은 모든 휴가정보 조회
	public List<LeaveDTO> getAllLeaves() {
		return leaveMapper.getAllLeaves();
	}

	// MANAGER는 같은 부서 출결정보 조회
	public List<LeaveDTO> getLeavesByDept(Long deptNo) {
		return leaveMapper.getLeavesByDept(deptNo);
	}
	
	// USER는 본인의 출결정보만 조회
	public List<LeaveDTO> getLeavesByEmpId(String empId) {
		return leaveMapper.getLeavesByEmpId(empId);
	}    
	
	// ADMIN은 모든 휴가 보유내역 조회
	public List<LeaveDTO> getAllUsedLeftLeaves() {
		return leaveMapper.getAllUsedLeftLeaves();
	}
	
	// ADMIN은 모든 휴가 보유내역 조회
	public List<LeaveDTO> getUsedLeftLeavesByDept(Long deptNo) {
		return leaveMapper.getUsedLeftLeavesByDept(deptNo);
	}
	
	// ADMIN은 모든 휴가 보유내역 조회
	public List<LeaveDTO> getUsedLeftLeavesByEmpId(String empId) {
		return leaveMapper.getUsedLeftLeavesByEmpId(empId);
	}

	// 휴가 일정 관리 저장
	@Transactional
	public void insertLeave(LeaveDTO dto, EmpDTO loginUser, String docId) {

	    double levDays = dto.getLevDays() != null ? dto.getLevDays() : 0.0;
	    double levLeftDays = dto.getLevLeftDays() != null ? dto.getLevLeftDays() : 0.0;
	    double levUsedDays = dto.getLevUsedDays() != null ? dto.getLevUsedDays() : 0.0;

	    int yearsOfService = Period.between(loginUser.getEmpStartDate(), LocalDate.now()).getYears();
		int vacationDaysDays = calculateAnnualLeaveDays(yearsOfService);
	    
	    if (vacationDaysDays - levUsedDays - levDays < 0) {
	        throw new IllegalArgumentException("남은 휴가일수는 음수일 수 없습니다.");
	    }

	    dto.setLevUsedDays(levUsedDays + levDays); // ✅ 사용일 계산 미리
	    dto.setLevLeftDays(levLeftDays);           // null 방지 후 세팅
	    dto.setEmpId(loginUser.getEmpId());        // 필요시 DTO에도 담음

	    leaveMapper.insertLeave(dto, dto.getEmpId(), docId);
	}

	
	@Transactional
	public void insertLeaveEmp(EmpRegistDTO emp) {
		int yearsOfService = Period.between(emp.getEmpStartDate(), LocalDate.now()).getYears();
		int vacationDaysDays = calculateAnnualLeaveDays(yearsOfService);
		leaveMapper.insertLeaveEmp(emp.getEmpId(), vacationDaysDays);
	}
	
//	@Transactional
//	public void updateEmployeesAnnualLeaveBase() {
//	    List<EmpListPreviewDTO> allEmployees = employeesMapper.selectEmpList(); // 전체 직원 조회
//
//	    for (EmpListPreviewDTO emp : allEmployees) {
//	        LocalDate startDate = emp.getEmpStartDate();
//	        if (startDate == null) continue; // 방어 처리
//
//	        int yearsOfService = Period.between(startDate, LocalDate.now()).getYears();
//	        int levLeftDays = calculateAnnualLeaveDays(yearsOfService);
//	        
//	        leaveMapper.insertLeaveEmp(emp.getEmpId(), levLeftDays);
//	        leaveMapper.updatelLeaveLeftDays(emp.getEmpId(), levLeftDays);
//	    }
//	}

	
	
	
	
	
	
// 결재 연동을 위한 메서드(파싱 테스트
    
    /**
     * 결재 문서ID로 휴가 정보 조회
     */
    public LeaveDTO getLeaveByDocId(String docId) {
        logger.info("결재 문서ID로 휴가 정보 조회: {}", docId);
        return leaveMapper.getLeaveByDocId(docId);
    }
    
    /**
     * 결재 문서ID와 함께 휴가 저장
     */
    @Transactional
    public void insertLeaveWithDocId(LeaveDTO dto, Employees employee, String docId) {
        logger.info("결재 문서ID와 함께 휴가 저장: 직원={}, 문서ID={}", employee.getEmpId(), docId);
        
        // 이미 처리된 휴가가 있는지 확인
        LeaveDTO existingLeave = leaveMapper.getLeaveByDocId(docId);
        if (existingLeave != null) {
            logger.info("이미 처리된 휴가 신청: {}", docId);
            return;
        }
        
        // 휴가 정보 저장
        leaveMapper.insertLeaveWithDocId(dto, employee.getEmpId(), docId);
        
        logger.info("휴가 정보 저장 완료: {}", employee.getEmpId());
    }
    
    /**
     * 휴가 상태 업데이트
     */
    @Transactional
    public boolean updateLeaveStatus(Long levNo, String status, String remarks) {
        logger.info("휴가 상태 업데이트: 휴가번호={}, 상태={}", levNo, status);
        int result = leaveMapper.updateLeaveStatus(levNo, status, remarks);
        return result > 0;
    }


	

    
}
    
    
    
    
    

