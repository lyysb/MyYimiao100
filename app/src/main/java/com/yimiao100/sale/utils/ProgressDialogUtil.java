package com.yimiao100.sale.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.view.KeyEvent;

/**
 * 显示加载中
 * todo 改成单利模式
 * Created by Michel on 2017/1/7.
 */

public class ProgressDialogUtil {


    private ProgressDialogUtil() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }
    public static ProgressDialog getLoadingProgress(Activity activity) {
        return getLoadingProgress(activity, "加载中");
    }
    public static ProgressDialog getLoadingProgress(final Activity activity, String msg) {
        final ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(msg);
        progressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        activity.finish();
                        return false;
                    }
                }
                return false;
            }
        });
        return progressDialog;
    }

}
