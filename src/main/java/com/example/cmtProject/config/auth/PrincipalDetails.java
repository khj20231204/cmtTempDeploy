//package com.example.cmtProject.config.auth;
////
//import java.util.Collection;
//import java.util.List;
//
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//import com.example.cmtProject.entity.Member;
//import com.example.cmtProject.entity.erp.employees.Employees;
//
////시큐리티 session을 사용하기 위한 클래스
////Security Session -> Authenctication -> UserDetails(PrincipalDetails)
//public class PrincipalDetails implements UserDetails {
//
//	private Employees employees;
//
//	public PrincipalDetails(Employees employees){
//       this.employees = employees;
//    }
//
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//       return List.of(new SimpleGrantedAuthority("ROLE_" + employees.getEmpLevel())); //db에 ROLE_가 없는 데이터를 가져와서 security로 넘기기전에 ROLE_를 붙인다
////       return List.of(new SimpleGrantedAuthority(member.getEmpLevel()));
//    }
//
//    @Override
//    public String getPassword() {
//       return employees.getEmpPassword();
//    }
//
//    @Override
//    public String getUsername() {
//       return employees.getEmpName();
//    }
//}
