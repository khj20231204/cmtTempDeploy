package com.example.cmtProject.repository.mes.standardInfoMgt;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.cmtProject.entity.mes.standardInfoMgt.ProcessInfo;

@Repository
public interface ProcessInfoRepository extends JpaRepository<ProcessInfo, Long> {

}
