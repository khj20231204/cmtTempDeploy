package com.example.cmtProject.comm.response;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class ErrorResponse {
	// 에러 응답 전용 클래스
    private final LocalDateTime timestamp = LocalDateTime.now();
    private final String message;
    private final String code;
    private final String path;

    public ErrorResponse(String message, String code, String path) {
        this.message = message;
        this.code = code;
        this.path = path;
    }

}
