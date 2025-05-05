package com.example.cmtProject.constants;

public class MesStatusConstants {
    // 출고 상태
    public static final String ISSUE_STATUS_WAITING = "출고대기";
    public static final String ISSUE_STATUS_INSPECTING = "검수중";
    public static final String ISSUE_STATUS_COMPLETED = "출고완료";
    public static final String ISSUE_STATUS_CANCELED = "취소";
    public static final String ISSUE_STATUS_INSPECT_PASSED = "검사 합격";
    public static final String ISSUE_STATUS_INSPECT_FAILED = "검사 불합격";
    
    // 입고 상태
    public static final String RECEIPT_STATUS_WAITING = "입고대기";
    public static final String RECEIPT_STATUS_INSPECTING = "검수중";
    public static final String RECEIPT_STATUS_COMPLETED = "입고완료";
    public static final String RECEIPT_STATUS_CANCELED = "취소";
    public static final String RECEIPT_STATUS_INSPECT_PASSED = "검사 합격";
    public static final String RECEIPT_STATUS_INSPECT_FAILED = "검사 불합격";
    
    // 수주 상태
    public static final String SO_STATUS_CONFIRMED = "SO_CONFIRMED";
    public static final String SO_STATUS_PLANNED = "SO_PLANNED";
    public static final String SO_STATUS_COMPLETED = "SO_COMPLETED";
    public static final String SO_STATUS_SHIPPED = "SO_SHIPPED";
}