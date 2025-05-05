package com.example.cmtProject.service.erp.employees;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.cmtProject.dto.erp.employees.EmpCountDTO;
import com.example.cmtProject.dto.erp.employees.EmpDTO;
import com.example.cmtProject.dto.erp.employees.EmpListPreviewDTO;
import com.example.cmtProject.dto.erp.employees.EmpRegistDTO;
import com.example.cmtProject.dto.erp.employees.searchEmpDTO;
import com.example.cmtProject.mapper.erp.employees.EmployeesMapper;
import com.example.cmtProject.repository.erp.employees.EmployeesRepository;

import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmployeesService {
	@Autowired private EmployeesMapper empMapper;
	@Autowired private EmployeesRepository empRepository;
	@Autowired private BCryptPasswordEncoder passwordEncoder;
	@Value("${uploadBaseLocation}") private String uploadBaseLocation;
	@Value("${ProfileImgLocation}") private String itemImgLocation;
	
	// ADMIN 전원 사원리스트 조회
	public List<EmpListPreviewDTO> getEmpList() {
		return empMapper.selectEmpList();
	}
	
	// MANAGER 같은 부서 사원 리스트 조회
	public List<EmpListPreviewDTO> getEmpListDept(Long deptNo) {
		return empMapper.getEmpListDept(deptNo);
	}
	
	// USER 본인 사원만 조회
	public List<EmpListPreviewDTO> getEmpListUser(Long empNo) {
		return empMapper.getEmpListUser(empNo);
	}
	
	
	
	
	//사원검색
	public List<searchEmpDTO> getSearchDept(searchEmpDTO searchEmpDTO) {
		return empMapper.selectDept(searchEmpDTO);
	}
	//사원추가
	public int insertEmp(EmpRegistDTO empRegistDTO,MultipartFile empProfileFile) throws Exception {
		String pw = passwordEncoder.encode(empRegistDTO.getEmpPassword());
		empRegistDTO.setEmpPassword(pw);
		
		if(empProfileFile != null && !empProfileFile.isEmpty()) {
			
			// 파일 이름 설정 (uuid + 원본파일명)
	        String uuid = UUID.randomUUID().toString();
	        String fileName = uuid + "_" + empProfileFile.getOriginalFilename();
	        
			//파일 저장할 경로 생성
			Path uploadDir = Paths.get(uploadBaseLocation,itemImgLocation);
			
			if(!Files.exists(uploadDir)) { //존재하지 않으면
				Files.createDirectories(uploadDir);
			}
			//디렉토리와 파일명을 결합하여 Path 객체 생성
			//기존경로 문자열로 변환 후 파일명 전달
			Path uploadPath = Paths.get(uploadDir.toString(),fileName);
			System.out.println("업로드 할 파일 경로 >>" + uploadPath);
			
			//파일을 실제 경로에 업로드
			empProfileFile.transferTo(new File(uploadPath.toString()));
			//DTO에 파일명 저장
			empRegistDTO.setEmpProfile(fileName);
		}
		
		return empMapper.insertEmp(empRegistDTO);
	}
	//사원수정
	public int updateEmp(EmpRegistDTO dto, MultipartFile empProfileFile) throws Exception {
		if(empProfileFile != null && !empProfileFile.isEmpty()) {
			
			// 파일 이름 설정 (uuid + 원본파일명)
	        String uuid = UUID.randomUUID().toString();
	        String fileName = uuid + "_" + empProfileFile.getOriginalFilename();
	        
	        //log.info("fileName:"+fileName);
	        
			//파일 저장할 경로 생성
			Path uploadDir = Paths.get(uploadBaseLocation,itemImgLocation);
			
			log.info("uploadDir:"+uploadDir);
			
			if(!Files.exists(uploadDir)) { //존재하지 않으면
				Files.createDirectories(uploadDir);
			}
			//디렉토리와 파일명을 결합하여 Path 객체 생성
			//기존경로 문자열로 변환 후 파일명 전달
			Path uploadPath = Paths.get(uploadDir.toString(),fileName);
			System.out.println("업로드 할 파일 경로 >>" + uploadPath);
			
			//파일을 실제 경로에 업로드
			empProfileFile.transferTo(new File(uploadPath.toString()));
			//DTO에 파일명 저장
			dto.setEmpProfile(fileName);
		}
		log.info("dto:" + dto);
		int result = empMapper.updateEmp(dto);
		log.info("result:"+ result);
		
		return 1; 
	}
	
	//멤버 리스트에서 사원조회
	public EmpRegistDTO getEmpDetail(String id) {
		return empMapper.selectEmpDetail(id);
	}
	//나의 리스트에서 사원조회
	public EmpRegistDTO getMyEmpList(String empId) {
		return empMapper.selectMyEmpList(empId);
	}
	
	//관리자가 사원수정
	public int updateEmpDetail(String id) {
		// TODO Auto-generated method stub
		return empMapper.updateEmpDetail(id);
	}
	
	//아이디 중복검사
	public boolean checkId(String empId) {
		return empMapper.selectEmpId(empId) > 0;
	}
	//아이디 찾기
	public String getEmpId(Map<String, String> map) {
		// TODO Auto-generated method stub
		return empMapper.selectId(map);
	}
	
	//사원 현황
	public EmpCountDTO getEmpCount(EmpCountDTO countDTO) {
		return empMapper.selectCount(countDTO);
	}
	public List<EmpCountDTO> getdeptCount(EmpCountDTO countDTO) {
		return empMapper.selectDeptCount(countDTO);
	}

	public Map<String, Object> getMonthlyStatus() {
		List<Map<String, Object>> joiners = empMapper.getJoinersPerMonth();
		List<Map<String, Object>> leavers = empMapper.getLeaversPerMonth();
		
		List<String> labels = new ArrayList<>(); //1월,2월
		List<Integer> joinerData = new ArrayList<>(); //입사자 담는 리스트
		List<Integer> leaverData = new ArrayList<>();
		
        for (int i = 1; i <= 12; i++) {
            String month = String.format("%02d", i); // 01, 02, ...
            labels.add(i + "월");
            joinerData.add(getCountByMonth(joiners, month));
            leaverData.add(getCountByMonth(leavers, month));
        }

        Map<String, Object> map = new HashMap<>();
        map.put("labels", labels);
        map.put("joiners", joinerData);
        map.put("leavers", leaverData);
        return map;
    }

    private int getCountByMonth(List<Map<String, Object>> list, String month) {
        for (Map<String, Object> row : list) {
            if (row.get("MONTH").equals(month)) {
                return ((Number) row.get("COUNT")).intValue();
            }
        }
        return 0;
    }
    //로그인한 유저의 정보
	public EmpDTO getEmpList(String empId) {
		return empMapper.selectLoginUser(empId);
	}
}