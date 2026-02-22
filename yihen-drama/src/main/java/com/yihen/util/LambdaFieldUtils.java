package com.yihen.util;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;

public class LambdaFieldUtils {

    @FunctionalInterface
    public interface SFunction<T, R> extends Serializable {
        R apply(T source);
    }

    public static <T> String resolveFieldName(SFunction<T, ?> fn) {
        try {
            Method method = fn.getClass().getDeclaredMethod("writeReplace");
            method.setAccessible(true);
            SerializedLambda lambda = (SerializedLambda) method.invoke(fn);

            String methodName = lambda.getImplMethodName(); // getChapterCount
            return methodToField(methodName);
        } catch (Exception e) {
            throw new RuntimeException("无法解析字段名", e);
        }
    }

    private static String methodToField(String methodName) {
        if (methodName.startsWith("get")) {
            methodName = methodName.substring(3);
        }
        return Character.toLowerCase(methodName.charAt(0)) + methodName.substring(1);
    }
}
