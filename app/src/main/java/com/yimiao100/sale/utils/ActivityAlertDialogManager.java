package com.yimiao100.sale.utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

import com.yimiao100.sale.activity.LoginActivity;
import com.yimiao100.sale.base.ActivityCollector;

/**
 * Activity单一AlertDialog管理工具类---专用于重新登录
 * Created by 亿苗通 on 2016/9/23.
 */
public class ActivityAlertDialogManager {

    private static AlertDialog sAlertDialog;                        // 一个Activity下只产生一个AlertDialog实例
    private static AlertDialog.Builder sBuilder;                        // 一个Activity下只产生一个AlertDialog.Builder实例
    private static Activity sLastActivity = null;

    /**
     * 获得每个Activity中唯一一个Dialog
     * @param activity
     * @return
     */
    public static AlertDialog getDialog(@NonNull Activity activity){
        // 通过AlertDialog.Builder这个类来实例化我们的一个AlertDialog的对象
        AlertDialog.Builder builder = getBuilder(activity);
        AlertDialog dialog;
        if (sAlertDialog != null) {
            dialog = sAlertDialog;
        } else {
            dialog = builder.create();
            sAlertDialog = dialog;
        }
        return dialog;
    }

    @NonNull
    private static AlertDialog.Builder getBuilder(@NonNull final Activity activity) {
        AlertDialog.Builder builder;
        if (activity == sLastActivity) {
            if (sBuilder != null) {
                builder = sBuilder;
            } else {
                builder = createNewBuilder(activity);
            }
        } else {
            reset();
            builder = createNewBuilder(activity);
            sLastActivity = activity;
            sBuilder = builder;
        }

        return builder;
    }

    @NonNull
    private static AlertDialog.Builder createNewBuilder(@NonNull final Activity activity) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(activity);

        setBuilder(activity, builder);

        sBuilder = builder;
        return builder;
    }

    private static void setBuilder(@NonNull final Activity activity, AlertDialog.Builder builder) {
        builder.setTitle("下线通知");
        builder.setMessage("您的账号已在另一台设备登录。如非本人操作，则密码可能已经泄露，建议修改密码。");
        builder.setCancelable(false);
        builder.setNegativeButton("退出应用", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                ActivityCollector.finishAll();
            }
        });
        builder.setPositiveButton("重新登录", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                ActivityCollector.finishAll();
//                activity.startActivity(new Intent(activity, LoginActivity.class));
                com.yimiao100.sale.login.LoginActivity.start(activity);
            }
        });
    }

    private static void reset() {
        sBuilder = null;
        sAlertDialog = null;
        sLastActivity = null;
    }
}
