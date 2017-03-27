package com.yimiao100.sale.utils;


import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 亿苗通 on 2016/8/12.
 */
public class TimeUtil {
    private TimeUtil() {
        /* 禁止实例化*/
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 毫秒格式化
     *
     * @param milliseconds
     * @param format
     * @return
     */
    public static String timeStamp2Date(String milliseconds, String format) {
        if (milliseconds == null || milliseconds.isEmpty() || milliseconds.equals("null")) {
            return "";
        }
        if (format == null || format.isEmpty())
            format = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(Long.valueOf(milliseconds)));
    }

    /**
     * 通过和10天的毫秒值比较，10天以上返回日期，10天以下返回几天前
     *
     * @param milliseconds
     * @param format
     * @return
     */
    public static String time2Date(long milliseconds, String format) {
        if (milliseconds <= 0) {
            return "";
        }
        if (format == null || format.isEmpty())
            format = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        if (System.currentTimeMillis() - milliseconds <= 86400000) {
            return "今天";
        } else if (System.currentTimeMillis() - milliseconds <= 86400000 * 2) {
            return "昨天";
        } else if (System.currentTimeMillis() - milliseconds < 864000000) {
            //如果小于十天，返回几天前
            return ((System.currentTimeMillis() - milliseconds) /  86400000 + 1) + "天前";
        } else {
            return sdf.format(new Date(milliseconds));
        }
    }

    /**
     * @param date
     * @return 默认格式yyyy年MM月dd日
     */
    public static String getTime(Date date) {
        return getTime(date, "yyyy年MM月dd日");
    }

    public static String getTime(Date date, String format) {
        return new SimpleDateFormat(format).format(date);
    }


}
