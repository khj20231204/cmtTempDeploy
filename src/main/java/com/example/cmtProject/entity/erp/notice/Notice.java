package com.example.cmtProject.entity.erp.notice;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notice")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notice {

	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    @Column(name = "NOTICE_ID")
	    private Long noticeId;

	    @Column(name = "TITLE", nullable = false, length = 200)
	    private String title;

	    @Lob
	    @Column(name = "CONTENT", nullable = false)
	    private String content;

	    @Column(name = "EMP_ID", length = 50)
	    private String empId;

	    @Column(name = "EMP_NAME", length = 100)
	    private String empName;

	    @Column(name = "DEPT_NO")
	    private Long deptNo; // 부서번호 (FK)
	    
	    @Column(name = "POSITION_NO")
	    private Long positionNo; // 직위번호 (FK)
	    
	    @Column(name = "VIEW_COUNT")
	    private Integer viewCount = 0;

	    @Column(name = "IS_PINNED", length = 1)
	    private String isPinned = "N"; // 'Y' or 'N'

	    @Column(name = "CREATED_AT")
	    private LocalDate createdAt;

	    @Column(name = "UPDATED_AT")
	    private LocalDate updatedAt;

	    @PrePersist
	    public void onCreate() {
	        this.createdAt = LocalDate.now();
	    }

	    @PreUpdate
	    public void onUpdate() {
	        this.updatedAt = LocalDate.now();
	    }
	}
