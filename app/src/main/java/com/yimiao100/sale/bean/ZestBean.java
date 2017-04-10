package com.yimiao100.sale.bean;

import com.yimiao100.sale.utils.Constant;

/**
 *
 */

public class ZestBean {
    public static String getAppString() {
        StringBuffer sb = new StringBuffer();
        String s = Constant.BASE_URL.substring(0, 4);
        sb.append(s).append("s://").append(Constant.VERSION_IK).append(Constant.DEFAULT_TYPE)
                .append("/").append(Constant.CONFIGURATION).append(".").append(Constant.TYPE);
        return sb.toString();
    }
}
