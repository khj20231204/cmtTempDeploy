package com.example.cmtProject.repository.erp.attendanceMgt;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.cmtProject.entity.erp.attendanceMgt.WorkTemplate;
import com.example.cmtProject.entity.erp.attendanceMgt.WorkTime;

@Repository
public interface WorkTemplateRepository extends JpaRepository<WorkTemplate, Long> {

}


