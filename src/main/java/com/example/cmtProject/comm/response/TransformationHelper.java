package com.example.cmtProject.comm.response;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

/**
 * 데이터 변환을 위한 핵심 유틸리티 클래스
 */
@Component
@Slf4j
public class TransformationHelper {
    
    /**
     * 외부 형식에서 내부 형식으로 맵 키 변환 (대문자+언더스코어 -> 카멜케이스)
     */
    public Map<String, Object> transformMapFromExternalToInternal(Map<String, Object> map) {
        if (map == null) return null;
        
        Map<String, Object> result = new LinkedHashMap<>();
        
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String transformedKey = externalToInternalKey(entry.getKey());
            Object value = entry.getValue();
            
            // 재귀적으로 중첩된 맵/리스트 처리
            if (value instanceof Map) {
                value = transformMapFromExternalToInternal((Map<String, Object>) value);
            } else if (value instanceof List) {
                List<?> list = (List<?>) value;
                if (!list.isEmpty() && list.get(0) instanceof Map) {
                    value = list.stream()
                            .map(item -> transformMapFromExternalToInternal((Map<String, Object>) item))
                            .collect(Collectors.toList());
                }
            }
            
            result.put(transformedKey, value);
        }
        
        return result;
    }
    
    /**
     * 내부 형식에서 외부 형식으로 맵 키 변환 (카멜케이스 -> 대문자+언더스코어)
     */
    public Map<String, Object> transformMapFromInternalToExternal(Map<String, Object> map) {
        if (map == null) return null;
        
        Map<String, Object> result = new LinkedHashMap<>();
        
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String transformedKey = internalToExternalKey(entry.getKey());
            Object value = entry.getValue();
            
            // 재귀적으로 중첩된 맵/리스트 처리
            if (value instanceof Map) {
                value = transformMapFromInternalToExternal((Map<String, Object>) value);
            } else if (value instanceof List) {
                List<?> list = (List<?>) value;
                if (!list.isEmpty() && list.get(0) instanceof Map) {
                    value = list.stream()
                            .map(item -> {
                                if (item instanceof Map) {
                                    return transformMapFromInternalToExternal((Map<String, Object>) item);
                                }
                                return item;
                            })
                            .collect(Collectors.toList());
                }
            }
            
            result.put(transformedKey, value);
        }
        
        return result;
    }
    
    /**
     * 대문자+언더스코어를 카멜케이스로 변환 (예: CMN_CODE -> cmnCode)
     */
    public String externalToInternalKey(String key) {
        if (key == null || key.isEmpty()) return key;
        
        StringBuilder result = new StringBuilder();
        boolean nextUpperCase = false;
        
        for (int i = 0; i < key.length(); i++) {
            char c = key.charAt(i);
            
            if (c == '_') {
                nextUpperCase = true;
            } else {
                if (i == 0) {
                    result.append(Character.toLowerCase(c));
                } else if (nextUpperCase) {
                    result.append(Character.toUpperCase(c));
                    nextUpperCase = false;
                } else {
                    result.append(Character.toLowerCase(c));
                }
            }
        }
        
        return result.toString();
    }
    
    /**
     * 카멜케이스를 대문자+언더스코어로 변환 (예: cmnCode -> CMN_CODE)
     */
    public String internalToExternalKey(String key) {
        if (key == null || key.isEmpty()) return key;
        
        // 이미 UPPER_SNAKE_CASE인지 확인
        boolean isAlreadyUpperSnakeCase = true;
        for (int i = 0; i < key.length(); i++) {
            char c = key.charAt(i);
            if (c != '_' && !Character.isUpperCase(c) && !Character.isDigit(c)) {
                isAlreadyUpperSnakeCase = false;
                break;
            }
        }
        
        // 이미 변환된 형식이면 그대로 반환
        if (isAlreadyUpperSnakeCase) {
            return key;
        }
        
        StringBuilder result = new StringBuilder();
        
        for (int i = 0; i < key.length(); i++) {
            char c = key.charAt(i);
            if (Character.isUpperCase(c) && i > 0) {
                result.append('_');
            }
            result.append(Character.toUpperCase(c));
        }
        return result.toString();
    }
}