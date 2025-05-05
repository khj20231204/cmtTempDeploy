package com.example.cmtProject.entity.mes.qualityControl;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@Table(name = "QCM")  // 테이블 ID = IQCM
public class Qcm {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "QCM_NO")
    private Long qcmNo;

    @Column(name = "QCM_CODE", nullable = false)
    private String qcmCode;

    @Column(name = "QCM_NAME")
    private String qcmName;

    @Column(name = "MTL_CODE")
    private String mtlCode;

    @Column(name = "MTL_NAME")
    private String mtlName;

    @Column(name = "PDT_CODE")
    private String pdtCode;

    @Column(name = "PDT_NAME")
    private String pdtName;
    
    @Column(name = "QCM_TARGET_VALUE", nullable = false)
    private Double qcmTargetValue;

    @Column(name = "QCM_MAX_VALUE", nullable = false)
    private Double qcmMaxValue;

    @Column(name = "QCM_MIN_VALUE", nullable = false)
    private Double qcmMinValue;

    @Column(name = "QCM_UNIT_LENGTH")
    private String qcmUnitLength;

    @Column(name = "QCM_UNIT_WEIGHT")
    private String qcmUnitWeight;

    @Column(name = "QCM_METHOD")
    private String qcmMethod;

    
    
}
