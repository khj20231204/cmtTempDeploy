package com.example.cmtProject.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
    	  // 이미지 파일 경로
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:///D:/profile/images/profile/");
    	
//        registry.addResourceHandler("/images/**")
//        		.addResourceLocations("file:/usr/local/cmt/images/profile/");

        // PDF 파일 경로
        registry.addResourceHandler("/pdfs/**")
                .addResourceLocations("file:///D:/pdfs/");
    }
}
