package com.example.cmtProject.comm.response;

//응답코드 상수정의
//CM = common
//MB = Member
//PD = Product 등등..


public class ResponseCode {
    public static final String SUCCESS = "CM200";
    public static final String BAD_REQUEST = "CM400";
    public static final String UNAUTHORIZED = "CM401";
    public static final String FORBIDDEN = "CM403";
    public static final String NOT_FOUND = "CM404";
    public static final String SERVER_ERROR = "CM500";
    
    // 비즈니스 관련 에러 코드
    public static final String MEMBER_NOT_FOUND = "MB001";
    public static final String PRODUCT_NOT_FOUND = "PD001";
    // 추가 비즈니스 코드...
}
