package com.yimiao100.sale.utils;

/**
 * 类型转换工具（安全）
 * Created by michel on 2017/12/11.
 */
public class ConvertUtils {
    private ConvertUtils(){}

    public static int toInt(Object value, int defaultVal) {
        if (value == null || "".equals(value.toString().trim())) {
            return defaultVal;
        }
        try {
            return Integer.valueOf(value.toString());
        } catch (Exception e) {
            try {
                return Double.valueOf(value.toString()).intValue();
            } catch (Exception e1) {
                return defaultVal;
            }
        }
    }
}
