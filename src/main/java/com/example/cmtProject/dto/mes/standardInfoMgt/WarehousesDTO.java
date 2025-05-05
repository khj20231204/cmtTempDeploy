package com.example.cmtProject.dto.mes.standardInfoMgt;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WarehousesDTO {
	
	private Long whsNo;  		//창고no(pk)
	private String whsCode; 	//창고코드
	private String whsName; 	//창고명
	private String whsLocation; //창고위치
	private String whsUsed; 	//창고사용
	private String whsComments; //비고   
}


