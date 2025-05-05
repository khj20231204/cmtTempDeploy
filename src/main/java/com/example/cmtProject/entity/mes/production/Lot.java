//package com.example.cmtProject.entity.mes.production;
//
//import java.time.LocalDate;
//
//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import jakarta.persistence.Id;
//import jakarta.persistence.Table;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//@Entity
//@Data
//@Table(name = "LOT")
//public class Lot {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "LOT_NO")
//    private Long lotNo;
//
//    @Column(name = "LOT_CODE")
//    private String lotCode;
//
//    @Column(name = "PDT_CODE")
//    private String pdtCode;
//
//    @Column(name = "CREATE_DATE")
//    private LocalDate createDate;
//
//    @Column(name = "PRC_TYPE")
//    private String prcType;
//
//    @Column(name = "LINE_CODE")
//    private String lineCode;
//
//    @Column(name = "EQP_CODE")
//    private String eqpCode;
//
//    @Column(name = "WO_CODE")
//    private String woCode;
//
//    @Column(name = "CHILD_LOT_CODE")
//    private String childLotCode;
//    
//    @Column(name = "PARENT_LOT_CODE")
//    private String parentLotCode;
//
//    @Column(name = "START_TIME")
//    private LocalDate startTime;
//
//    @Column(name = "FINISH_TIME")
//    private LocalDate finishTime;
//
//    @Column(name = "WORK_ORDER_STATUS")
//    private String workOrderStatus;
//
//    @Column(name = "USE_YN")
//    private String useYn;
//}