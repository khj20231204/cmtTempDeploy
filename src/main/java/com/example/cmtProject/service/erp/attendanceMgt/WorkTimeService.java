package com.example.cmtProject.service.erp.attendanceMgt;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.cmtProject.dto.erp.attendanceMgt.WorkTemplateDTO;
import com.example.cmtProject.dto.erp.attendanceMgt.WorkTimeDTO;
import com.example.cmtProject.mapper.erp.attendanceMgt.WorkTimeMapper;
import com.example.cmtProject.repository.erp.attendanceMgt.WorkTemplateRepository;
import com.example.cmtProject.repository.erp.attendanceMgt.WorkTimeRepository;

import jakarta.persistence.TableGenerator;

@Service
public class WorkTimeService {

	@Autowired
    private WorkTimeRepository workTimeRepository;
	
	@Autowired 
	private WorkTimeMapper workTimeMapper;
	
	@Autowired
	private WorkTemplateRepository workTemplateRepository;

	// 모든 근무 시간 정보 조회
	public List<WorkTimeDTO> getAllAttends() {
		return workTimeMapper.getAllAttends();
	}
	
	// 모달창, 어드민, 이미 설정된 사원 빼고 사원 출결 정보 조회
	public List<WorkTimeDTO> getAllAttendsModal() {
		return workTimeMapper.getAllAttendsModal();
	}
	
	// 같은 부서 출결 정보 조회
	public List<WorkTimeDTO> getAttendsByDept(Long deptNo) {
		return workTimeMapper.getAttendsByDept(deptNo);
	}
	
	// 모달창, 매니저, 이미 설정된 사원 빼고 사원 출결 정보 조회
	public List<WorkTimeDTO> getAllAttendsModalByDept(Long deptNo) {
		return workTimeMapper.getAllAttendsModalByDept(deptNo);
	}

	// 사원 하나의 정보만 조회
	public List<WorkTimeDTO> getAttendsByEmpNo(Long empNo) {
		return workTimeMapper.getAttendsByEmpNo(empNo);
	}
	
	// 근무 일정 템플릿 조회
	public List<WorkTemplateDTO> getAllWorkTemplate() {
		return workTimeMapper.getAllWorkTemplate();
	}
	
	@Transactional
	public void insertWktTypeByEmpNo(WorkTimeDTO row) {
		workTimeMapper.insertWktTypeByEmpNo(row);
	}

    @Transactional
	public void updateWktTypeByEmpNo(Long empNo, String wktType) {
		workTimeMapper.updateWktTypeByEmpNo(empNo, wktType);
	}
	
    // 근무 일정 관리 저장
    @Transactional
	public void saveWorkTemplates(List<WorkTemplateDTO> templates) {
    	 for (WorkTemplateDTO dto : templates) {
    	        workTimeMapper.saveWorkTemplates(dto);
    	    }
    }
	
	// 출결 정보 삭제
    @Transactional
    public void deleteWorkTime(Long id) {
    	workTimeRepository.deleteById(id);
    }
    
    // 근무 일정 관리 삭제
    @Transactional
	public void deleteTemplatesByIds(List<Long> ids) {
    	workTemplateRepository.deleteAllById(ids);
	}






    
    

	

	
}