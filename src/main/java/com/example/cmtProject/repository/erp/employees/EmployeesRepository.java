package com.example.cmtProject.repository.erp.employees;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.cmtProject.entity.erp.employees.Employees;

@Repository
public interface EmployeesRepository extends JpaRepository<Employees, Long> {
	 
	Optional<Employees> findByEmpId(String empId);
	
	@Query("""
	 		SELECT e FROM Employees e
	 		WHERE e.deptNo = :deptNo
	 		AND e.positionNo = :positionNo
	 		""")
	List<Employees> getEmpName(@Param("deptNo") Long deptNo,
			 @Param("positionNo") Long positionNo);
	
	//오늘날짜 가입된 사원수 세기
	@Query(value = "SELECT COUNT(*) FROM EMPLOYEES WHERE TO_CHAR(EMP_START_DATE, 'YYYYMMDD') = TO_CHAR(SYSDATE, 'YYYYMMDD')", nativeQuery = true)
	int countTodayEmployees();
	 
	/*
	SELECT e.empName FROM Employees e
	 		WHERE e.deptNo = :deptCode
	 		AND e.positionNo = :postCode
	 		 
	Employees : entity의 Employees.java
	empName : Employees entity의 필드명
	deptNo : Employees entity의 필드명
	positionNo : Employees entity의 필드명
	=> DB의 테이블과 컬럼명이 아님!
	*/
	 
	/*
	Employees getEmpName(@Param("deptNo") Long deptNo,
	@Param("positionNo") Long positionNo); 
	@Param("deptNo") : deptNo는 Employees entity의 필드명
	@Param("positionNo") : positionNo는 Employees entity의 필드명
	 
	Long deptNo : deptNo는 Employees entity의 필드명
	Long positionNo : positionNo는 Employees entity의 필드명
	*/
}