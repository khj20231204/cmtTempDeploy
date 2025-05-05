package com.example.cmtProject.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Jackson JSON 변환 설정
 * 
 * 이 클래스는 애플리케이션 전체에서 사용되는 ObjectMapper 설정을 제공합니다.
 * - JavaTimeModule: 날짜/시간 타입의 직렬화/역직렬화 지원
 * - FAIL_ON_UNKNOWN_PROPERTIES=false: 알 수 없는 속성이 있어도 역직렬화 실패하지 않음
 * - WRITE_DATES_AS_TIMESTAMPS=false: 날짜를 타임스탬프가 아닌 ISO-8601 형식으로 출력
 * 
 * 참고: RequestTransformer, ResponseTransformer와 함께 사용될 때 
 * 기본 설정값이 누락되면 요청/응답 변환 과정에서 오류가 발생할 수 있음
 * 
 * 카멜 - 스네이크 / 스네이크 - 카멜 변경하는 Transformer 과 충돌로 인해 아래와 같이 변경하였습니다.
 * 
 */

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        return mapper;
    }
}


// 아래는 기존 설정

//	@Bean
//	public ObjectMapper objectMapper() {
//		
//		return new ObjectMapper().registerModule(new JavaTimeModule()); 
//		
//	}//package com.example.cmtProject.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//
//@Configuration
//public class JacksonConfig {
//
//	@Bean
//	public ObjectMapper objectMapper() {
//		
//		return new ObjectMapper().registerModule(new JavaTimeModule()); 
//		
//	}
//}
