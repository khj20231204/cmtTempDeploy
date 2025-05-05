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
@Table(name="positions")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Position {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "POSITION_NO")  // 직위NO (PK)
    private Long positionNo;

    @Column(name = "DEPT_NO", nullable = false, unique = true)  // 부서 NO(FK)
    private String deptNo;

    @Column(name = "DEPT_POSITION")
    private String deptPosition; //직위명 "사원/주임/대리/팀장/부장/차장/이사/대표이사
    

}
