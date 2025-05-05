package com.example.cmtProject.dto.erp.notice;

import java.beans.ConstructorProperties;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoticeDTO {

    private Long noticeId;
    private String title;
    private String content;
    private String empId;
    private String empName;
    private String positionNo;
    private String deptNo;
    private Integer viewCount;
    private String isPinned; // 상단 고정여부 'Y' or 'N'
    private LocalDate  createdAt;
    private LocalDate  updatedAt;
    
    private String deptName;      // 부서명 (조인으로 가져옴)
    private String positionName;  // 직급명 (조인으로 가져옴)
    private String dept;
    
    
    @ConstructorProperties({"noticeId", "title", "empName", "deptName", "positionName","createdAt"})
    public NoticeDTO(Long noticeId, String title, String empName, String deptName, String positionName,LocalDate  createdAt) {
        this.noticeId = noticeId;
        this.title = title;
        this.empName = empName;
        this.deptName = deptName;
        this.positionName = positionName;
        this.createdAt = createdAt;
    }
    
    
    
    
}
