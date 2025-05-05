//package com.example.cmtProject.comm.response;
//
//import java.lang.reflect.ParameterizedType;
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
//                                MethodParameter parameter, Type targetType,
//                                Class<? extends HttpMessageConverter<?>> converterType) {
//        if (body == null) return null;
//
//        try {
//            Object result;
//
//            if (!(body instanceof Map) && !(body instanceof List) &&
//                !(body instanceof String) && !(body instanceof Number) && !(body instanceof Boolean)) {
//                result = transformDto(body, parameter);
//            } else if (body instanceof Map) {
//                result = transformHelper.transformMapFromExternalToInternal((Map<String, Object>) body);
//            } else if (body instanceof List) {
//                List<?> list = (List<?>) body;
//                if (!list.isEmpty()) {
//                    if (list.get(0) instanceof Map) {
//                        Class<?> elementType = getListElementType(parameter);
//                        result = list.stream()
//                                .map(item -> {
//                                    Map<String, Object> transformed = transformHelper.transformMapFromExternalToInternal((Map<String, Object>) item);
//                                    return objectMapper.convertValue(transformed, elementType);
//                                })
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
//    
//    private Class<?> getListElementType(MethodParameter parameter) {
//        try {
//            Type type = parameter.getGenericParameterType();
//            if (type instanceof ParameterizedType) {
//                Type actual = ((ParameterizedType) type).getActualTypeArguments()[0];
//                if (actual instanceof Class<?>) {
//                    return (Class<?>) actual;
//                }
//            }
//        } catch (Exception e) {
//            log.warn("리스트 내부 타입 추출 실패: {}", e.getMessage());
//        }
//        return Object.class;
//    }
//    
//    
//}