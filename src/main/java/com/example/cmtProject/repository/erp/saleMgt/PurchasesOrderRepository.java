package com.example.cmtProject.repository.erp.saleMgt;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import com.example.cmtProject.entity.comm.CommoncodeDetail;
import com.example.cmtProject.entity.erp.employees.Employees;
import com.example.cmtProject.entity.erp.salesMgt.PurchasesOrder;
import com.example.cmtProject.entity.erp.salesMgt.SalesOrderStatus;
import com.example.cmtProject.entity.mes.standardInfoMgt.Clients;
import com.example.cmtProject.entity.mes.standardInfoMgt.Products;

@Repository
public interface PurchasesOrderRepository extends JpaRepository<PurchasesOrder, Long> {

	@Query("""
			SELECT DISTINCT p.mtlCode FROM PurchasesOrder p ORDER BY p.mtlCode
			""")
	List<String> findDistinctMtlCode();

	@Query("""
			SELECT DISTINCT p.cltCode  FROM PurchasesOrder p ORDER BY p.cltCode
			""")
	List<String> findDistinctCltCode();

	@Query(value = "SELECT EMP_NO FROM EMPLOYEES WHERE EMP_ID = :empid", nativeQuery = true)
	Long findEmpNoByEmpId(@Param("empid") String empid);
	
	@Modifying
	@Transactional
	@Query(value = "UPDATE PURCHASES_ORDER SET EMP_NO = :empNo WHERE PO_NO = :poNo", nativeQuery = true)
	int updateEmpNo(@Param("empNo") Long empNo, @Param("poNo") Long poNo);

	//- 발주 목록에 있는 제품명 -
	@Query("""
			SELECT m.mtlName
			FROM Materials m
			WHERE m.mtlCode = :mtlCode
			""")
	String findByMtlName(@Param("mtlCode") String mtlCode);
	
	//- 선택된 거래처명 가져오기 -
	@Query("SELECT c.cltName " +
	       "FROM Clients c " +
	       "WHERE c.cltCode = :cltCode")
     String findByCltName(@Param("cltCode") String cltCode);
	
	@Query(value = "SELECT COUNT(PO_DATE) FROM PURCHASES_ORDER WHERE TRUNC(PO_DATE) = TO_DATE(:data, 'YYYY-MM-DD')", nativeQuery = true)
	int getNextPoCode(@Param("data") String data);
	
	@Query(value = "SELECT SEQ_PURCHASESORDER_PO_NO.NEXTVAL FROM DUAL", nativeQuery = true)
	Long getNextPurchasesOrderNextSequences();
	
	@Transactional
	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("UPDATE PurchasesOrder p SET p.poUseYn = :visibleType WHERE p.poNo IN :poNoList")
	void updatePoVisibleByPoNo(@Param("visibleType") String visibleType, @Param("poNoList") List<Integer> poNoList);
}
