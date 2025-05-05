package com.example.cmtProject.repository.mes.manufacturingMgt;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.cmtProject.entity.mes.manufacturingMgt.MfgSchedule;

@Repository
public interface MfgScheduleRepository extends JpaRepository<MfgSchedule, Long> {

}
