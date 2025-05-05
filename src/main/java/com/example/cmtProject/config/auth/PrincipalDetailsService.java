//package com.example.cmtProject.config.auth;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//import com.example.cmtProject.entity.Member;
//import com.example.cmtProject.repository.MainRepository;
//
//@Service
//public class PrincipalDetailsService implements UserDetailsService {
//
//	@Autowired
//	private MainRepository mainRepository;
//
//	/*
//	 * <input type="text" name="empName" placeholder="Username" />
//	 * 의 name에 있는 empName과 밑에 매개변수 loadUserByUsername(String empName)의 empName이 일치해야 되고,
//	 * SecurityConfig에 .usernameParameter("empName") 추가
//	 * 만약, username으로 일치시킨다면 
//	 * <input type="text" name="username" placeholder="Username" />
//	 * loadUserByUsername(String username)
//	 * SecurityConfig를 수정하지 않아도됨 username이 Seucrity의 default변수명이기 때문
//	 * 
//	 * ------------------
//	 * 
//	 * Security의 Session내부(Authentication의 내부(UserDetails))
//	 */
//	@Override
//	public UserDetails loadUserByUsername(String empNo) throws UsernameNotFoundException {
//		
//		Member member = mainRepository.findByEmpNo(empNo);
//		System.out.println("member : " + member);
//		
//		if (member == null) {
//		    throw new UsernameNotFoundException("User not found");
//		}
//
//		return new PrincipalDetails(member);
//   }
//}
