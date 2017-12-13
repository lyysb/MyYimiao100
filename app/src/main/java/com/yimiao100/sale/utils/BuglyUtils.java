package com.yimiao100.sale.utils;

import android.content.Context;
import com.tencent.bugly.crashreport.CrashReport;
import com.yimiao100.sale.bean.UserInfoBean;

/**
 * Bugly相关操作
 * Created by michel on 2017/12/11.
 */
public class BuglyUtils {

    public static void initBugly(Context context) {
        CrashReport.initCrashReport(context, Constant.BUGLY_APP_ID, true);
        CrashReport.putUserData(context, "isTest", Constant.isTest +"");
    }

    public static void putUserData(Context context, UserInfoBean userInfo) {
        // UserId
        CrashReport.setUserId(context, userInfo.getId() + "");
        // 自定义参数
        // step1: 添加之前先移除之前的数据
        removeUserData(context);
        // step2: 添加所需要的数据
        // UserId
        CrashReport.putUserData(context, Constant.USER_ID, userInfo.getId() + "");
        // UserAccount
        CrashReport.putUserData(context, Constant.USER_ACCOUNT, userInfo.getAccountNumber());
        // UserName
        if (userInfo.getCnName() != null && !userInfo.getCnName().isEmpty()) {
            CrashReport.putUserData(context, Constant.USER_NAME, userInfo.getCnName());
        }
        // UserPhone
        if (userInfo.getPhoneNumber() != null && !userInfo.getPhoneNumber().isEmpty()) {
            CrashReport.putUserData(context, Constant.USER_PHONE, userInfo.getPhoneNumber());
        }
    }

    public static void putUserName(Context context, String userName) {
        CrashReport.putUserData(context, Constant.USER_NAME, userName);
    }

    public static void putUserPhone(Context context, String phoneNumber) {
        CrashReport.putUserData(context, Constant.USER_PHONE, phoneNumber);
    }

    public static void removeUserData(Context context) {
        if (CrashReport.getUserData(context, Constant.USER_ID) != null) {
            CrashReport.removeUserData(context, Constant.USER_ID);
        }
        if (CrashReport.getUserData(context, Constant.USER_ACCOUNT) != null) {
            CrashReport.removeUserData(context, Constant.USER_ACCOUNT);
        }
        if (CrashReport.getUserData(context, Constant.USER_NAME) != null) {
            CrashReport.removeUserData(context, Constant.USER_NAME);
        }
        if (CrashReport.getUserData(context, Constant.USER_PHONE) != null) {
            CrashReport.removeUserData(context, Constant.USER_PHONE);
        }
    }
}
