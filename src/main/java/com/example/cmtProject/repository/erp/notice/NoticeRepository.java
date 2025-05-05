package com.example.cmtProject.repository.erp.notice;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.cmtProject.dto.erp.notice.NoticeDTO;
import com.example.cmtProject.entity.erp.notice.Notice;
@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

	@Query("SELECT new com.example.cmtProject.dto.erp.notice.NoticeDTO(" +
	           "n.noticeId, n.title, n.empName, d.cmnDetailName, p.cmnDetailName ,n.createdAt) " +
	           "FROM Notice n " +
	           "LEFT JOIN CommoncodeDetail d ON FUNCTION('TO_CHAR', n.deptNo) = d.cmnDetailCode AND d.cmnCode = 'DEPT' " +
	           "LEFT JOIN CommoncodeDetail p ON FUNCTION('TO_CHAR', n.positionNo) = p.cmnDetailCode AND p.cmnCode = 'POSITION'"+
	           "ORDER BY n.createdAt DESC")
	    List<NoticeDTO> findAllWithDeptAndPosition();
}
