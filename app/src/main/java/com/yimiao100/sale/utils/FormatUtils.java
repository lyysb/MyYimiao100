package com.yimiao100.sale.utils;

import java.text.DecimalFormat;

/**
 * 对数字进行格式化
 * Created by 亿苗通 on 2016/9/13.
 */
public class FormatUtils {
    public FormatUtils() {
        /* 禁止实例化 */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 金额格式化
     * @param money
     * @return
     */
    public static String MoneyFormat(double money) {
        return new DecimalFormat("###,##0.00").format(money);
    }

    /**
     * RMB格式化
     * @param money
     * @return
     */
    public static String RMBFormat(double money) {
        if (money < 0) {
            return "error";
        }else if (money == 0) {
            return "¥0.00";
        }else{
            DecimalFormat format = new DecimalFormat("###,##0.00");
            return "¥" + format.format(money);
        }
    }

    /**
     * 数字格式化
     * @param number
     * @return
     */
    public static String NumberFormat(int number) {
        return new DecimalFormat("###,##0").format(number);
    }
    /**
     * 百分比格式化
     * @param percent
     * @return
     */
    public static String PercentFormat(double percent) {
        return new DecimalFormat("###,##0").format(percent);
    }

    /**
     * 电话号加密格式化
     * @param phoneNumber
     * @return
     */
    public static String PhoneNumberFormat(String phoneNumber) {
        return phoneNumber.substring(0, 3) +
                " **** " + phoneNumber.substring(7);
    }

    public static String ScoreFormat(double score) {
        return new DecimalFormat("##.#").format(score);
    }
    /**
     * 银行卡号格式化---每四位截取
     * @param bankNumber
     * @return
     */
    public static String BankNumberFormat(String bankNumber) {
        if (bankNumber == null) {
            bankNumber = "****************";
        }
        StringBuilder builder = new StringBuilder();
        char[] chars = bankNumber.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (i < (chars.length - 4)) {
                if (i % 4 != 0) {
                    builder.append("*");
                } else {
                    builder.append(" *");
                }
            } else {
                if (i % 4 != 0) {
                    builder.append(chars[i]);
                } else {
                    builder.append(" " + chars[i]);
                }
            }
        }
        return builder.toString();
    }

}
