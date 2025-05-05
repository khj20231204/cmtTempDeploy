package com.example.cmtProject.entity.mes.qualityControl;

import java.time.LocalDateTime;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import groovy.transform.builder.Builder;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "IPI")
@EntityListeners(AuditingEntityListener.class)
public class Ipi {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IPI_NO")
    private Long ipiNo;

	@Column(name = "LOT_NO")
	private Long lotNo;

	@Column(name = "IPI_CODE")
    private String ipiCode;
    
    @Column(name = "WO_CODE")
    private String woCode;

    @Column(name = "EMP_ID")
    private String empId;
 
    @Column(name = "EMP_NAME")
    private String empName;

    @Column(name = "QCM_NO")
    private Long qcmNo;
    
    @Column(name = "QCM_NAME")
    private String qcmName;
    
    @Column(name = "PDT_CODE")
    private String pdtCode;

    @Column(name = "PDT_TYPE")
    private String pdtType;

    @Column(name = "PDT_NAME")
    private String pdtName;

    @Column(name = "IPI_START_TIME")
    private LocalDateTime ipiStartTime;

    @Column(name = "IPI_END_TIME")
    private LocalDateTime ipiEndTime;
    
    @Column(name = "WO_QTY")
    private String woQty;

    @Column(name = "UNIT_QTY")
    private String unitQty;

    @Column(name = "IPI_MEASURED_WEIGHT_VALUE")
    private Double ipiMeasuredWeightValue;
    
    @Column(name = "IPI_MEASURED_LENGTH_VALUE")
    private Double ipiMeasuredLengthValue;

    @Column(name = "QCM_UNIT_LENGTH")
    private String qcmUnitLength;

    @Column(name = "QCM_UNIT_WEIGHT")
    private String qcmUnitWeight;

    @Column(name = "IPI_INSPECTION_STATUS")
    private String ipiInspectionStatus; // 검사전 / 검사중 / 검사완료

    @Column(name = "IPI_INSPECTION_RESULT")
    private String ipiInspectionResult; // 합격 / 불합격 / 확인불가

    @Column(name = "WHS_CODE")
    private String whsCode;
    
    @Column(name = "WHS_NAME")
    private String whsName;

    @Column(name = "CHILD_LOT_CODE")
    private String childLotCode;
    
    @Column(name = "IPI_REMARKS")
    private String ipiRemarks;
    
    @Column(name = "IPI_VISIABLE")
    private String ipiVisiable;

}
