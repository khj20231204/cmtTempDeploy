package com.example.cmtProject.repository.comm;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.cmtProject.dto.comm.CommonCodeDetailDTO;
import com.example.cmtProject.entity.comm.CommoncodeDetail;

@Repository
public interface CommonCodeDetailRepository extends JpaRepository<CommoncodeDetail, String> {

	CommonCodeDetailDTO findByCmnDetailCodeAndCmnCode(String group, String code);
	
	//공통코드에서 부서명, 직급명 가져오기
	List<CommoncodeDetail> findByCmnCode(String cmnCode);
}
