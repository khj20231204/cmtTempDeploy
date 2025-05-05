package com.example.cmtProject.comm.response;

import lombok.Getter;


//기본 응답 래퍼 클래스
@Getter
public class ApiResponse<T> {
	private boolean success;
    private String message;
    private T data;
    private String code;

    // 프라이빗 생성자
    private ApiResponse(boolean success, String message, T data, String code) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.code = code;
    }

    // 성공 응답 생성 메서드
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "Success", data, ResponseCode.SUCCESS);
    }

    // 성공 응답 (메시지 포함)
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data, ResponseCode.SUCCESS);
    }

    // 실패 응답 생성 메서드
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null, ResponseCode.SERVER_ERROR);
    }

    // 실패 응답 (코드 포함)
    public static <T> ApiResponse<T> error(String message, String errorCode) {
        return new ApiResponse<>(false, message, null, errorCode);
    }
    
 // 에러 응답 (데이터 포함)
    public static <T> ApiResponse<T> error(String message, T data) {
        return new ApiResponse<>(false, message, data, ResponseCode.SERVER_ERROR);
    }

    // 에러 응답 (데이터 및 코드 포함)
    public static <T> ApiResponse<T> error(String message, T data, String errorCode) {
        return new ApiResponse<>(false, message, data, errorCode);
    }
}
	

