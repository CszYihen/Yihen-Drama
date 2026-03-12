package com.yihen.util;

import org.springframework.util.ObjectUtils;

import java.util.function.Supplier;

public class CheckUtils {

    public static <T> void validateRequeried(Supplier<T> supplier, String message) {
        if (ObjectUtils.isEmpty(supplier.get())) {
            throw new RuntimeException(message);
        }
    }
}
