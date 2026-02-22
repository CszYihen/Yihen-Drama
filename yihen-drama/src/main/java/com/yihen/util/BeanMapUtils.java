package com.yihen.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.HashMap;
import java.util.Map;

public class BeanMapUtils {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.registerModule(new JavaTimeModule());
        MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    /**
     * Bean -> Map<String, Object>
     */
    public static Map<String, Object> beanToObjectMap(Object bean) {
        if (bean == null) return null;
        return MAPPER.convertValue(bean, new TypeReference<Map<String, Object>>() {});
    }

    /**
     * Bean -> Map<String, String>
     * 适合 Redis Hash
     */
    public static Map<String, String> beanToStringMap(Object bean) {
        if (bean == null) return null;

        Map<String, Object> objectMap = beanToObjectMap(bean);
        Map<String, String> stringMap = new HashMap<>();

        objectMap.forEach((k, v) -> {
            if (v != null) {
                stringMap.put(k, String.valueOf(v));
            }
        });

        return stringMap;
    }

    /**
     * Map -> Bean
     */
    public static <T> T mapToBean(Map<?, ?> map, Class<T> clazz) {
        if (map == null || map.isEmpty()) return null;

        Map<String, Object> newMap = new HashMap<>();
        map.forEach((k, v) -> newMap.put(String.valueOf(k), v));

        return MAPPER.convertValue(newMap, clazz);
    }

    /**
     * Map<String,String> -> Bean
     */
    public static <T> T stringMapToBean(Map<String, String> map, Class<T> clazz) {
        if (map == null || map.isEmpty()) return null;

        return MAPPER.convertValue(map, clazz);
    }
}
