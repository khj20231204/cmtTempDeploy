package com.example.cmtProject.entity.mes.manufacturingMgt;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "MFG_SCHEDULES_DETAIL")
@NoArgsConstructor
@AllArgsConstructor
public class MfgScheduleDetail { // 제조 계획 상세 Entity

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MSD_NO")
	private Long msdNo;  				// 제조 계획 디테일 번호
	
	@Column(name = "MS_CODE")
	private String msCode;  			// 제조 계획 코드
	
	@Column(name = "PARENT_PDT_CODE")
	private String parentPdtCode;  		// 제품 코드
	
	@Column(name = "ITEM_TYPE")
	private String itemType; 			// 제품 코드 유형
	
	@Column(name = "MS_QTY")
	private String msQty; 				// 계획 수량(SO_QTY(수주 수량) * BOM_QTY(투입 수량))
	
}
