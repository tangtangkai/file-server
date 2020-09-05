package com.ttk.file.server.utils;

import org.springframework.util.StringUtils;

public class GetSuffixUtil {
    public static String getSuffix(String fileName) {
        if(StringUtils.isEmpty(fileName)){
            return null;
        }
        int i = fileName.lastIndexOf(".");
        return fileName.substring(i);
    }
}
