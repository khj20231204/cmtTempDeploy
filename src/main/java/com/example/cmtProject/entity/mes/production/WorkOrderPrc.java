//package com.example.cmtProject.entity.mes.production;
//
//import jakarta.persistence.*;
//import lombok.*;
//import java.time.LocalDate;
//
//@Entity
//@Table(name = "WORK_ORDER")
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class WorkOrderPrc {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "WO_NO")
//    private Long woNo; // 작업지시 고유번호
//
//    @Column(name = "WO_CODE", length = 100)
//    private String woCode;
//
//    @Column(name = "MP_NO")
//    private Long mpNo;
//
//    @Column(name = "PDT_CODE", length = 50)
//    private String pdtCode;
//    
//    @Column(name = "PDT_NAME", length = 50)
//    private String pdtName;
//
//    @Column(name = "PRC_CODE", length = 100)
//    private String prcCode;
//
//    @Column(name = "LINE_CODE", length = 100)
//    private String lineCode;
//
//    @Column(name = "EMP_ID")
//    private Long empId;
//    
//    @Column(name = "WO_DATE")
//    private LocalDate woDate;
//
//    @Column(name = "WO_QTY", length = 100)
//    private String woQty;
//
//    @Column(name = "WO_START_DATE")
//    private LocalDate woStartDate;
//
//    @Column(name = "WO_END_DATE")
//    private LocalDate woEndDate;
//
//    @Column(name = "STATUS", length = 20)
//    private String status;
//
//    @Column(name = "COMMENTS", length = 500)
//    private String comments;
//
//    @Column(name = "USE_YN", length = 1)
//    private String useYn;
//
//    @Column(name = "WORK_ORDER_NO")
//    private Long workOrderNo;
//
//    @Column(name = "WORK_END_DATE")
//    private LocalDate workEndDate;
//
//    @Column(name = "DUE_DATE")
//    private LocalDate dueDate;
//
//    @Column(name = "ORDER_DATE")
//    private LocalDate orderDate;
//
//    @Column(name = "WORK_ORDER_CODE", length = 100)
//    private String workOrderCode;
//
//    @Column(name = "WORK_START_DATE")
//    private LocalDate workStartDate;
//
//    @Column(name = "ORDER_QTY", length = 255)
//    private String orderQty;
//}
//
