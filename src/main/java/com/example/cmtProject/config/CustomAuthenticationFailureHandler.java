package com.example.cmtProject.config;

import java.io.IOException;
import java.io.PrintWriter;

import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
	                                     AuthenticationException exception) throws IOException, ServletException {

	    String errorMessage = "아이디 또는 비밀번호가 틀렸습니다.";

	    Throwable cause = exception.getCause(); // 원인 파악

	    if (cause instanceof DisabledException) {
	        errorMessage = cause.getMessage();
	    }

	    response.setContentType("text/html;charset=UTF-8");
	    PrintWriter out = response.getWriter();
	    out.println("<script>alert('" + errorMessage + "'); location.href='/login';</script>");
	    out.flush();
	}
}
