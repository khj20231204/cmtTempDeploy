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
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "IQC")
@EntityListeners(AuditingEntityListener.class)
public class Iqc {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IQC_NO")
    private Long iqcNo;

    @Column(name = "IQC_CODE", nullable = false)
    private String iqcCode;
    
    @Column(name = "RECEIPT_NO")
    private String receiptNo;
    
    @Column(name = "RECEIPT_CODE")
    private String receiptCode;

    @Column(name = "EMP_ID")
    private String empId;
    
    @Column(name = "EMP_NAME")
    private String empName;

    @Column(name = "QCM_NO")
    private Long qcmNo;

    @Column(name = "QCM_NAME")
    private String qcmName;

    @Column(name = "MTL_CODE")
    private String mtlCode;
    
    @Column(name = "MTL_NAME")
    private String mtlName;

    @Column(name = "IQC_START_TIME")
    private LocalDateTime iqcStartTime;

    @Column(name = "IQC_END_TIME")
    private LocalDateTime iqcEndTime;
    
    @Column(name = "RECEIVED_QTY")
    private String receivedQty;
    
    @Column(name = "UNIT_QTY")
    private String unitQty;

    @Column(name = "IQC_MEASURED_WEIGHT_VALUE")
    private Double iqcMeasuredWeightValue;

    @Column(name = "IQC_MEASURED_LENGTH_VALUE")
    private Double iqcMeasuredLengthValue;
    
    @Column(name = "QCM_UNIT_LENGTH")
    private String qcmUnitLength;

    @Column(name = "QCM_UNIT_WEIGHT")
    private String qcmUnitWeight;

    @Column(name = "IQC_INSPECTION_STATUS")
    private String iqcInspectionStatus; // 검사전 / 검사중 / 검사완료

    @Column(name = "IQC_INSPECTION_RESULT")
    private String iqcInspectionResult; // 합격 / 불합격 / 확인불가

    @Column(name = "WHS_CODE")
    private String whsCode;
    
    @Column(name = "WHS_NAME")
    private String whsName;

    @Column(name = "LOT_NO")
    private String lotNo;
    
    @Column(name = "IQC_REMARKS")
    private String iqcRemarks;
    
    @Column(name = "IQC_VISIABLE")
    private String iqcVisiable;

}
