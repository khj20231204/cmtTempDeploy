package com.example.cmtProject.repository.mes.qualityControl;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.cmtProject.dto.mes.qualityControl.IqcDTO;
import com.example.cmtProject.entity.mes.qualityControl.Fqc;
import com.example.cmtProject.entity.mes.qualityControl.Iqc;

public interface IpiRepository extends JpaRepository<Fqc, Long> {
	
	List<Fqc> findByFqcVisiable(String FqcVisiable); 

}
