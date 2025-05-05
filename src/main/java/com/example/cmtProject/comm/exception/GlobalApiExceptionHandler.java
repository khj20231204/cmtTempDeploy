package com.example.cmtProject.comm.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.example.cmtProject.comm.response.ApiResponse;
import com.example.cmtProject.comm.response.ResponseCode;

import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalApiExceptionHandler extends ResponseEntityExceptionHandler {

    // 일반적인 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleAllExceptions(Exception ex, WebRequest request) {
        ApiResponse<Object> response = ApiResponse.error(
            "서버 오류가 발생했습니다: " + ex.getMessage(),
            ResponseCode.SERVER_ERROR
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    // 데이터 없음 예외 처리
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiResponse<Object>> handleNoSuchElementException(NoSuchElementException ex, WebRequest request) {
        ApiResponse<Object> response = ApiResponse.error(
            "요청한 데이터를 찾을 수 없습니다: " + ex.getMessage(),
            ResponseCode.NOT_FOUND
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
    
    // IllegalArgumentException 처리
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        ApiResponse<Object> response = ApiResponse.error(
            "잘못된 요청 파라미터입니다: " + ex.getMessage(),
            ResponseCode.BAD_REQUEST
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    
//    // 커스텀 비즈니스 예외 처리 - 필요에 따라 추가
//    @ExceptionHandler(BusinessException.class)
//    public ResponseEntity<ApiResponse<Object>> handleBusinessException(BusinessException ex, WebRequest request) {
//        ApiResponse<Object> response = ApiResponse.error(
//            ex.getMessage(),
//            ex.getErrorCode()
//        );
//        return new ResponseEntity<>(response, ex.getHttpStatus());
//    }
}