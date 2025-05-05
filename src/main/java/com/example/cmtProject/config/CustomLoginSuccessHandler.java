package com.example.cmtProject.config;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        String remember = request.getParameter("rememberId");
        String empId = request.getParameter("empId");

        if (remember != null) {
            Cookie cookie = new Cookie("empId", empId);
            cookie.setMaxAge(60 * 60 * 24 * 7); // 7일
            cookie.setPath("/");
            cookie.setSecure(false);
//            cookie.setHttpOnly(false); //JS에서도 접근 가능-> 보안취약
            response.addCookie(cookie);
        } else {
            Cookie cookie = new Cookie("empId", null);
            cookie.setMaxAge(0);
            cookie.setPath("/");
            response.addCookie(cookie);
        }

        // 로그인 성공 후, 메인 페이지로 리다이렉트
        response.sendRedirect("/");
    }
    
}