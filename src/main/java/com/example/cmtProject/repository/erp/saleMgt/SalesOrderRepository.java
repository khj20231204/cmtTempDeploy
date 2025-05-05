package com.example.cmtProject.repository.erp.saleMgt;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.cmtProject.dto.erp.saleMgt.SalesOrderMainDTO;
import com.example.cmtProject.entity.erp.salesMgt.SalesOrder;
import com.example.cmtProject.entity.mes.standardInfoMgt.Clients;

@Repository
public interface SalesOrderRepository extends JpaRepository<SalesOrder, Long> {
	
	
	//연관관계가 설정되어 있지 않다면 jpql에서 조인을 사용할 수 없다
	/*
	@Query("""
		    SELECT new com.example.cmtProject.dto.erp.saleMgt.SalesOrderDTO(
		    so.soNo, so.soCode, so.soDate, so.shipDate, so.soQuantity
		    ,so.pdtShippingPrice, so.soValue, so.soStatus, so.soComments
		    ,c.cltCode, c.cltName
		    ,p.pdtCode, p.pdtName
		    ,w.whsCode, w.whsName
		    ,e.empNo, e.empName )
		    FROM SalesOrder so
		    JOIN so.clients c
		    JOIN so.employees e
		    JOIN so.products p
		    JOIN so.warehouses w
		""")
	List<SalesOrderDTO> getSalesOrderMainList();
	*/
	
	//- 수주 목록에 있는 거래처코드 -
	@Query("""
			SELECT DISTINCT s.cltCode FROM SalesOrder s ORDER BY s.cltCode
			""")
	List<String> findByGetCltCode();
	
	//- 선택된 거래처명 가져오기 -
	@Query("SELECT c.cltName " +
	       "FROM Clients c " +
	       "WHERE c.cltCode = :cltCode")
     String findByGetCltName(@Param("cltCode") String cltCode);
	
	//- 수주 목록에 있는 제품코드 -
	@Query("""
			SELECT DISTINCT(s.pdtCode) from SalesOrder s order by s.pdtCode
			""")
	List<String> findByGetPdtCode();
	
	//- 수주 목록에 있는 제품명 -
	@Query("""
			SELECT p.pdtName
			FROM Products p
			WHERE p.pdtCode = :pdtCode
			""")
	String findByGetPdtName(@Param("pdtCode") String pdtCode);
	
	@Query(value = "SELECT SEQ_SALES_ORDER_SO_NO.NEXTVAL FROM DUAL", nativeQuery = true)
	Long getNextSalesOrderNextSequences();
	
	@Query(value = "SELECT COUNT(SO_DATE) FROM SALES_ORDER WHERE TRUNC(SO_DATE) = TO_DATE(:data, 'YYYY-MM-DD')", nativeQuery = true)
	int getNextSoCode(@Param("data") String data);
	
	@Query(value = "SELECT * FROM SALES_ORDER WHERE SO_NO IN :gridCheckList", nativeQuery = true)
	List<SalesOrder> findByEditorSelectedList(@Param("gridCheckList") List<Integer> gridCheckList);
	
	@Query(value = "SELECT EMP_NO FROM EMPLOYEES WHERE EMP_ID = :empid", nativeQuery = true)
	Long findEmpNoByEmpId(@Param("empid") String empid);
	
	@Modifying
	@Transactional
	@Query(value = "UPDATE SALES_ORDER SET EMP_NO = :empNo WHERE SO_NO = :soNo", nativeQuery = true)
	int updateEmpNo(@Param("empNo") Long empNo, @Param("soNo") Long soNo);

	@Transactional
	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("UPDATE SalesOrder s SET s.soVisible = :visibleType, s.soUseYn = :visibleType WHERE s.soNo IN :soNoList")
	void updateSoVisibleBySoNo(@Param("visibleType") String visibleType, @Param("soNoList") List<Integer> soNoList);
}

/*
	 Clients는 엔티티명으로 앞에 대문자
	 c.cltName 객체로 접근해야 하므로 cltName이 아니라 c.cltName
	 = :변수명 -> = 와 : 는 띄운다
	 
	 
	 
	 SELECT e FROM Employees e
	 		WHERE e.deptNo = :deptCode
	 		AND e.positionNo = :postCode
	 		 
	  Employees : entity의 Employees.java
	  deptNo : Employees entity의 필드명
	  positionNo : Employees entity의 필드명
	  DB의 테이블과 컬럼명이 아님 
 */
