package com.example.cmtProject.comm.response;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * 응답 데이터 변환 처리
 * 카멜케이스 형식을 대문자+언더스코어 형식으로 변환
 */
@ControllerAdvice
@Slf4j
public class ResponseTransformer implements ResponseBodyAdvice<Object> {

    private final ObjectMapper objectMapper;
    private final TransformationHelper transformHelper;

    public ResponseTransformer(ObjectMapper objectMapper, TransformationHelper transformHelper) {
        this.objectMapper = objectMapper;
        this.transformHelper = transformHelper;
        log.info("ResponseTransformer 초기화 완료");
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // 모든 응답에 대해 변환 적용
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType,
                                 MediaType selectedContentType,
                                 Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                 ServerHttpRequest request, ServerHttpResponse response) {
        
        if (body == null) return null;
        
        try {
            Object result;
            
            // ApiResponse 타입 처리
            if (body instanceof ApiResponse) {
                ApiResponse<?> apiResponse = (ApiResponse<?>) body;
                Object data = apiResponse.getData();
                
                if (data != null) {
                    Object transformedData = transformData(data);
                    result = createNewApiResponse(apiResponse, transformedData);
                } else {
                    result = body;
                }
            } 
            // 직접 반환하는 데이터 처리
            else {
                result = transformData(body);
            }
            
            log.debug("응답 데이터 변환 완료: {}", returnType.getExecutable());
            return result;
        } catch (Exception e) {
            log.error("응답 데이터 변환 실패: {}", e.getMessage(), e);
            return body;
        }
    }
    
    // 데이터 타입에 따른 변환
    private Object transformData(Object data) {
        if (data == null) return null;
        
        if (data instanceof List) {
            List<?> list = (List<?>) data;
            if (!list.isEmpty()) {
                if (list.get(0) instanceof Map) {
                    // 맵 리스트 변환
                    return list.stream()
                        .map(item -> transformHelper.transformMapFromInternalToExternal((Map<String, Object>) item))
                        .collect(Collectors.toList());
                } else if (!(list.get(0) instanceof String) && !(list.get(0) instanceof Number) && !(list.get(0) instanceof Boolean)) {
                    // DTO 리스트 변환
                    return list.stream()
                        .map(dto -> {
                            try {
                                Map<String, Object> map = objectMapper.convertValue(dto, Map.class);
                                return transformHelper.transformMapFromInternalToExternal(map);
                            } catch (Exception e) {
                                log.warn("DTO 리스트 항목 변환 실패: {}", e.getMessage());
                                return dto;
                            }
                        })
                        .collect(Collectors.toList());
                }
            }
            return list;
        } else if (data instanceof Map) {
            // 맵 변환
            return transformHelper.transformMapFromInternalToExternal((Map<String, Object>) data);
        } else if (!(data instanceof String) && !(data instanceof Number) && !(data instanceof Boolean)) {
            try {
                // DTO 변환
                Map<String, Object> map = objectMapper.convertValue(data, Map.class);
                return transformHelper.transformMapFromInternalToExternal(map);
            } catch (Exception e) {
                log.warn("DTO 변환 실패: {}", e.getMessage());
                return data;
            }
        }
        
        return data;
    }
    
    // 원본 ApiResponse의 속성을 유지하면서 새 ApiResponse 객체 생성
    private ApiResponse<?> createNewApiResponse(ApiResponse<?> original, Object transformedData) {
        if (original.isSuccess()) {
            return ApiResponse.success(original.getMessage(), transformedData);
        } else {
            return ApiResponse.error(original.getMessage(), original.getCode());
        }
    }
}

// //변환시 NULL을 반환하여 주석처리 추후 개선
//package com.example.cmtProject.comm.response;
//
//import java.lang.reflect.Type;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//import org.springframework.core.MethodParameter;
//import org.springframework.http.HttpInputMessage;
//import org.springframework.http.converter.HttpMessageConverter;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import lombok.extern.slf4j.Slf4j;
//
///**
// * 요청 데이터 변환 처리
// * 대문자+언더스코어 형식을 카멜케이스 형식으로 변환
// */
//@ControllerAdvice
//@Slf4j
//public class RequestTransformer extends RequestBodyAdviceAdapter {
//    
//    private final ObjectMapper objectMapper;
//    private final TransformationHelper transformHelper;
//    
//    public RequestTransformer(ObjectMapper objectMapper, TransformationHelper transformHelper) {
//        this.objectMapper = objectMapper;
//        this.transformHelper = transformHelper;
//        log.info("RequestTransformer 초기화 완료");
//    }
//    
//    @Override
//    public boolean supports(MethodParameter methodParameter, Type targetType, 
//                           Class<? extends HttpMessageConverter<?>> converterType) {
//        // 모든 요청에 대해 변환 적용
//        return true;
//    }
//    
//    @Override
//    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, 
//                               MethodParameter parameter, Type targetType, 
//                               Class<? extends HttpMessageConverter<?>> converterType) {
//        if (body == null) return null;
//        
//        try {
//            Object result;
//            
//            // DTO 객체 변환
//            if (!(body instanceof Map) && !(body instanceof List) && 
//                !(body instanceof String) && !(body instanceof Number) && !(body instanceof Boolean)) {
//                result = transformDto(body, parameter);
//            }
//            // Map 타입 변환
//            else if (body instanceof Map) {
//                result = transformHelper.transformMapFromExternalToInternal((Map<String, Object>) body);
//            } 
//            // List 타입 변환
//            else if (body instanceof List) {
//                List<?> list = (List<?>) body;
//                if (!list.isEmpty()) {
//                    if (list.get(0) instanceof Map) {
//                        result = list.stream()
//                                .map(item -> transformHelper.transformMapFromExternalToInternal((Map<String, Object>) item))
//                                .collect(Collectors.toList());
//                    } else if (!(list.get(0) instanceof String) && !(list.get(0) instanceof Number) && !(list.get(0) instanceof Boolean)) {
//                        // DTO 리스트 변환
//                        result = list.stream()
//                                .map(dto -> transformDto(dto, parameter))
//                                .collect(Collectors.toList());
//                    } else {
//                        result = body;
//                    }
//                } else {
//                    result = body;
//                }
//            } else {
//                result = body;
//            }
//            
//            log.debug("요청 데이터 변환 완료: {}", parameter.getExecutable());
//            return result;
//        } catch (Exception e) {
//            log.error("요청 데이터 변환 실패: {}", e.getMessage(), e);
//            return body;
//        }
//    }
//    
// // DTO 객체 변환
//    private Object transformDto(Object dto, MethodParameter parameter) {
//        try {
//            // DTO를 Map으로 변환
//            Map<String, Object> dtoMap = objectMapper.convertValue(dto, Map.class);
//            // 키 이름 변환
//            Map<String, Object> transformedMap = transformHelper.transformMapFromExternalToInternal(dtoMap);
//            
//            // 대상 클래스 결정
//            Class<?> targetClass = getTargetDtoClass(parameter, dto.getClass());
//            // 변환된 Map을 다시 DTO로 변환
//            return objectMapper.convertValue(transformedMap, targetClass);
//        } catch (Exception e) {
//            log.error("DTO 변환 실패: {}", e.getMessage(), e);
//            return dto;
//        }
//    }
//    
//    // 대상 DTO 클래스 결정
//    private Class<?> getTargetDtoClass(MethodParameter parameter, Class<?> defaultClass) {
//        if (parameter.getParameterType() != null && !Object.class.equals(parameter.getParameterType())) {
//            return parameter.getParameterType();
//        }
//        return defaultClass;
//    }
//}
