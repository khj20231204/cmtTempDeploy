package com.example.cmtProject.entity.mes.standardInfoMgt;

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
@Table(name = "CLIENTS")
@NoArgsConstructor
@AllArgsConstructor
public class Clients {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_CLIENTS_CLT_NO")
    @SequenceGenerator(name = "SEQ_CLIENTS_CLT_NO", sequenceName = "SEQ_CLIENTS_CLT_NO", allocationSize = 1)
    @Column(name = "CLT_NO")
	private Long cltNo;  //거래처no
	
	@Column(name = "CLT_CODE")
	private String cltCode; //거래처코드
	
	@Column(name = "CLT_NAME")
	private String cltName; //거래처명
	
	@Column(name = "CLT_PHONE")
	private String cltPhone; //전화번호
	
	@Column(name = "CLT_TYPE")
	private String cltType; //비고
	
	@Column(name = "CLT_EMAIL")
	private String cltEmail; //email
	
	@Column(name = "CLT_ADDRESS")
	private String cltAddress; //주소
	
	@Column(name = "CLT_COMMENTS")
	private String cltComments; //비고
	
	@Column(name = "CLT_PRC_TYPE")
	private String cltPrcType;
}
