package com.example.cmtProject.entity.mes.standardInfoMgt;

import java.time.LocalDate;

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
@Entity
@Table(name = "EQUIPMENTS")
@NoArgsConstructor
@AllArgsConstructor
public class EqupemntInfo {


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_EQUIPMENTS_EQP_NO")
    @SequenceGenerator(name = "SEQ_EQUIPMENTS_EQP_NO", sequenceName = "SEQ_EQUIPMENTS_EQP_NO", allocationSize = 1)
    @Column(name = "EQP_NO")
	private Long eqpNo;  
	
	@Column(name = "EQP_CODE")
	private String eqpCode; 
	
	@Column(name = "EQP_NAME")
	private String eqpName; 
	
	@Column(name = "EQP_TYPE")
	private String eqpType; 
	
	@Column(name = "EQP_PRC_TYPE")
	private String eqpPrcType; 
	
	@Column(name = "EQP_MODEL")
	private String eqpModel;
	
	@Column(name = "EQP_MANUFACTURER")
	private String eqpManufacturer;
	
	@Column(name = "EQP_PURCHASE_DATE")
	private LocalDate eqpPurchaseDate;
	
	@Column(name = "EQP_STATUS")
	private String eqpStatus;
	
	@Column(name = "EQP_LOCATION")
	private String eqpLocation;
	
	@Column(name = "EQP_COMMENTS")
	private String eqpComments;
}
