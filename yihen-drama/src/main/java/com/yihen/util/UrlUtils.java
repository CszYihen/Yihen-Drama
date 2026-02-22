package com.yihen.util;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlUtils {


    public static String extractFileExtension(String url) {
        if (url == null || url.isBlank()) {
            return null;
        }

        // 1) 去掉 query 和 fragment
        String clean = url;
        int q = clean.indexOf('?');
        if (q >= 0) clean = clean.substring(0, q);
        int f = clean.indexOf('#');
        if (f >= 0) clean = clean.substring(0, f);

        // 2) 取最后一个 '/'
        int slash = clean.lastIndexOf('/');
        String filename = (slash >= 0) ? clean.substring(slash + 1) : clean;

        // 3) 取扩展名
        int dot = filename.lastIndexOf('.');
        if (dot < 0 || dot == filename.length() - 1) {
            return null;
        }

        // 4) 返回小写扩展名
        return filename.substring(dot + 1).toLowerCase(Locale.ROOT);
    }


}
