package com.example.cmtProject.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "AUTH")
@Data
@ToString
@NoArgsConstructor
public class Auth {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int authNo;
	
	@Column(nullable = false)
	private String empNo;
	
	@Column(nullable = false)
	private String auth;
}
