package com.example.cmtProject.repository.erp.saleMgt;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.cmtProject.entity.erp.salesMgt.SalesOrderStatus;

@Repository
public interface SalesOrderStatusRepository extends JpaRepository<SalesOrderStatus, String> {

}
