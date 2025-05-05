package com.example.cmtProject.entity.erp.employees;

import java.time.LocalDate;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@EntityListeners(AuditingEntityListener.class) //@createDate 어노테이션 사용하기위해 사용
@Table(name="departments")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Departments {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DEPT_NO")  // 부서NO (PK)
    private Long deptNo;

    @Column(name = "EMP_NO", nullable = false, unique = true)  // 사원 ID
    private String empNo;

    @Column(name = "DEPT_NAME")
    private String deptName; //부서명 인사/개발/마케팅/영업/생산 등

}
