package com.example.cmtProject.entity.erp.attendanceMgt;


import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "LeaveEmp")
public class LeaveEmp {
	
	@Id
	@Column(name = "EMP_ID")
    private String empId; // 사원번호 (EMP_NO), 사원 테이블 참조
	
	@Column(name = "LEV_LEFT_DAYS")
    private Double levLeftDays; // 휴가 남은 일수

}
