package com.example.cmtProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.cmtProject.entity.Member;

@Repository
public interface MainRepository extends JpaRepository<Member, Long>{

	public Member findByEmpNo(Long empNo);
}
