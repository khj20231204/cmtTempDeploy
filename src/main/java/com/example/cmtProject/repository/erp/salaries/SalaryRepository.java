package com.example.cmtProject.repository.erp.salaries;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.cmtProject.entity.erp.salaries.Payment;

@Repository
public interface SalaryRepository extends JpaRepository<Payment, Long> {
	
}
