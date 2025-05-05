package com.example.cmtProject.util;

import com.example.cmtProject.entity.erp.employees.Employees;
import com.example.cmtProject.entity.erp.employees.PrincipalDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 인증 관련 유틸리티 클래스
 * 현재 로그인한 사용자 정보를 쉽게 가져오기 위한 정적 메서드 제공
 */
@Slf4j
public class SecurityUtil {

    /**
     * 현재 로그인한 사용자의 Employees 객체를 반환
     */
    public static Employees getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof PrincipalDetails) {
            PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
            return principalDetails.getUser();
        }
        return null;
    }

    /**
     * 현재 로그인한 사용자의 EMP_ID를 반환 (VARCHAR 타입)
     */
    public static String getUserId() {
        Employees currentUser = getCurrentUser();
        if (currentUser != null) {
            return currentUser.getEmpId();
        }
        return null;
    }

    /**
     * 현재 로그인한 사용자의 EMP_NO를 반환 (NUMBER 타입)
     */
//    public static Integer getUserNo() {
//        Employees currentUser = getCurrentUser();
//        if (currentUser != null && currentUser.getEmpNo() != null) {
//            try {
//                return Integer.valueOf(currentUser.getEmpNo());
//            } catch (NumberFormatException e) {
//                log.error("사용자 번호를 숫자로 변환할 수 없습니다: {}", currentUser.getEmpNo());
//            }
//        }
//        return null;
//    }

    /**
     * 현재 로그인한 사용자의 부서 코드 반환
     */
//    public static String getUserDept() {
//        Employees currentUser = getCurrentUser();
//        if (currentUser != null) {
//            return currentUser.getDeptNo();
//        }
//        return null;
//    }

    /**
     * 현재 로그인한 사용자의 이름 반환
     */
    public static String getUserName() {
        Employees currentUser = getCurrentUser();
        if (currentUser != null) {
            return currentUser.getEmpName();
        }
        return null;
    }
}