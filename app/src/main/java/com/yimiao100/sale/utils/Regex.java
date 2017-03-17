package com.yimiao100.sale.utils;

/**
 * 正则
 * Created by Michel on 2017/3/15.
 */

public interface Regex {
//    String idCard = "^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}(\\d|x|X)$";
    String email = "[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}";
    String idCard = "^(\\d{14}|\\d{17})(\\d|[xX])$";
    String name = "[\u4e00-\u9fa5\\w]+";
}
