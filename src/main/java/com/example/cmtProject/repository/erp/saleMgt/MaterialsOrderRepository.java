package com.example.cmtProject.repository.erp.saleMgt;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.cmtProject.entity.mes.standardInfoMgt.Materials;

public interface MaterialsOrderRepository extends JpaRepository<Materials, Long> {

}
