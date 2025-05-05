package com.example.cmtProject.repository.erp.attendanceMgt;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.cmtProject.entity.erp.attendanceMgt.Attend;

@Repository
public interface AttendRepository extends JpaRepository<Attend, Long> {
    
    
}
