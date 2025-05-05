package com.example.cmtProject.entity;

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
@Table(name="depertments")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Depertments {
	@Id
	@Column(name = "DEPT_NO")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long deptNo; // 부서코드(컬럼명 : dept_no)
	
	 @Column(name = "EMP_NAME", nullable = false)  // 부서이름
	 private String deptName;
}
