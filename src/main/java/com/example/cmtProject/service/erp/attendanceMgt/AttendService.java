package com.example.cmtProject.service.erp.attendanceMgt;

import java.sql.Date;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.cmtProject.dto.erp.attendanceMgt.AttendDTO;
import com.example.cmtProject.dto.erp.attendanceMgt.WorkTemplateDTO;
import com.example.cmtProject.entity.erp.attendanceMgt.Attend;
import com.example.cmtProject.entity.erp.employees.Employees;
import com.example.cmtProject.mapper.erp.attendanceMgt.AttendsMapper;
import com.example.cmtProject.mapper.erp.attendanceMgt.LeaveMapper;
import com.example.cmtProject.mapper.erp.attendanceMgt.WorkTimeMapper;
import com.example.cmtProject.repository.erp.attendanceMgt.AttendRepository;
import com.example.cmtProject.repository.erp.employees.EmployeesRepository;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
public class AttendService {

	private static final Logger logger = LoggerFactory.getLogger(AttendService.class);

    @Autowired
    private AttendsMapper attendsMapper;
    @Autowired
    private WorkTimeMapper workTimeMapper;
    @Autowired
    private LeaveMapper leavesMapper;
    
    // 모든 출결 정보 조회
    public List<AttendDTO> getAllAttends() {
        return attendsMapper.getAllAttends();
    }
    
    // 같은 부서 출결 정보 조회
    public List<AttendDTO> getAttendsByDept(Long deptNo) {
    	return attendsMapper.getAttendsByDept(deptNo);
    }
    
    // 사원 하나의 정보만 조회
    public List<AttendDTO> getAttendsByEmpNo(Long empNo) {
    	return attendsMapper.getAttendsByEmpNo(empNo);
    }
    

    // 출근 정보 저장
    @Transactional
    public void saveAttend(AttendDTO dto, Employees employee) {
        
    	LocalDateTime now = LocalDateTime.now();
    	LocalDate today = now.toLocalDate();

    	// DB에서 TIMESTAMP 컬럼을 LocalDateTime으로 받음
    	LocalDateTime startDateTime = workTimeMapper.getWorkTemplateByEmpNo(employee.getEmpNo());

    	// 시분초만 뽑아서 오늘 날짜와 합침
    	LocalTime baseTime = startDateTime.toLocalTime();
    	LocalDateTime workTemplate = today.atTime(baseTime);
    	LocalDateTime lateThreshold = workTemplate.plusMinutes(5);

        if (now.isBefore(lateThreshold)) {
            dto.setAtdStatus("ATS001"); // 정상 (허용시간 이내)
        } else {
            dto.setAtdStatus("ATS002"); // 지각 (5분 초과)
        }

        // 2. 출근 insert
        AttendDTO newDto = AttendDTO.builder()
            .empNo(employee.getEmpNo())
            .empName(employee.getEmpName())
            .atdDate(now)
            .atdStatus(dto.getAtdStatus() != null ? dto.getAtdStatus() : "ATS001")
            .atdType(dto.getAtdType() != null ? dto.getAtdType() : "ATT001")
            .atdRemarks(dto.getAtdRemarks())
            .build();

        attendsMapper.insertAttend(newDto, employee);

        // 3. 최근 60일 자동 결근/휴가 처리
        for (int i = 1; i <= 30; i++) {
            LocalDate checkDate = today.minusDays(i);
            DayOfWeek dow = checkDate.getDayOfWeek();
            if (dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY) continue;

            boolean attended = attendsMapper.existsAttendStatus(employee.getEmpNo(), checkDate);
            if (attended) continue;

            boolean isOnLeave = leavesMapper.isOnLeave(employee.getEmpId(), checkDate);

            AttendDTO autoDto = AttendDTO.builder()
                .empNo(employee.getEmpNo())
                .empName(employee.getEmpName())
                .atdDate(checkDate.atStartOfDay())
                .atdType(isOnLeave ? "ATT002" : "ATT005")     // ATT002 = 휴가, ATT005 = 결근
                .atdStatus(isOnLeave ? "ATS005" : "ATS004")   // ATS005 = 휴가 상태, ATS004 = 결근 상태
                .atdRemarks(isOnLeave ? "자동 휴가 처리" : "자동 결근 처리")
                .build();

            attendsMapper.insertAttend(autoDto, employee);
        }
    }
    
    
    // 퇴근 정보 저장
    @Transactional
	public void updateAttendLeave(Map<String, String> dto, Long atdNo) {
    	
		Attend attend = Attend.builder()
				.atdNo(atdNo)
				.atdLeave(LocalDateTime.now()) // 퇴근 처리 시 현재 시간 설정
				.atdType(dto.get("atdType"))
				.build();
		attendsMapper.updateAttendLeave(attend.getAtdNo(), LocalDateTime.now(), attend.getAtdType());
	}

    
    
    
    
    
    
    
    
}

