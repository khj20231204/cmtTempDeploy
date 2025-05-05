package com.example.cmtProject.repository.mes.standardInfoMgt;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.cmtProject.entity.mes.standardInfoMgt.Products;

@Repository
public interface ProductsRepository extends JpaRepository<Products, Long> {
	 
	@Transactional
	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("UPDATE Products p SET p.pdtUseyn = :visibleType WHERE pdtNo IN :data")
	void updateVisibleByPdtNo(@Param("visibleType") String visibleType, @Param("data") List<Integer> data);
}
