package com.example.cmtProject.entity.erp.employees;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class PrincipalDetails implements UserDetails {
    private final Employees user;

    public PrincipalDetails(Employees user) {
        this.user = user;
    }

    @Override
    public String getUsername() {
        return user.getEmpId(); // 사번 또는 로그인 ID
    }

    @Override
    public String getPassword() {
        return user.getEmpPassword(); // 암호화된 비밀번호
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
    	return List.of(new SimpleGrantedAuthority("ROLE_" + user.getEmpLevel()));
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }

    public Employees getUser() {
        return user;
    }
}

