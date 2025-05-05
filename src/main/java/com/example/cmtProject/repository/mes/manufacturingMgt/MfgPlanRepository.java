package com.example.cmtProject.repository.mes.manufacturingMgt;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.cmtProject.entity.mes.manufacturingMgt.MfgPlan;

@Repository
public interface MfgPlanRepository extends JpaRepository<MfgPlan, Long> {

}
