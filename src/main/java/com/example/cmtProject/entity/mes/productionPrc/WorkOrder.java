//package com.example.cmtProject.entity.mes.productionPrc;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//
//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import jakarta.persistence.Id;
//import jakarta.persistence.Table;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
////@Entity
//@Table(name = "WORK_ORDER")
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//public class WorkOrder {
//
//	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	@Column(name = "WO_NO")
//	private Long woNo; 			// 작업지시 고유 번호
//	
//	@Column(name = "WO_CODE")
//    private String woCode; 		// 작업지시 코드 (ex. WO-20250412-001)
//    
//    @Column(name = "MP_NO")
//    private Long mpNo; 			// 작업 계획
//    
//    @Column(name = "PDT_CODE")
//    private String pdtCode; 	// 제품 코드
//    
//    @Column(name = "PRC_TYPE_CODE")
//    private String prcTypeCode; // 공정
//    
//    @Column(name = "LINE_CODE")
//    private String lineCode;  	// 라인 
//    
//    @Column(name = "EMP_NO")
//    private Long empNo; 		// 작업자
//    
//    @Column(name = "WO_DATE")
//    private LocalDate woDate; 	// 작업지시 일자
//    
//    @Column(name = "WO_QTY")
//    private String woQty; 		// 생산 수량
//    
//    @Column(name = "WO_START_DATE")
//    private LocalDate wo_start_date; // 작업 시작
//    
//    @Column(name = "WO_END_DATE")
//    private LocalDate woEndDate;  	//작업 종료일
//    
//    @Column(name = "STATUS")
//    private String status; 			//상태: PLANNED / IN_PROGRESS / COMPLETED
//    
//    @Column(name = "COMMENTS")
//    private String comments;		// 비고
//    
//    @Column(name = "USE_YN")
//    private String useYN; 			// 사용 여부
//}
