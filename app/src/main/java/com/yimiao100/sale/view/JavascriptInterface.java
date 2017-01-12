package com.yimiao100.sale.view;

import android.content.Context;
import android.content.Intent;

import com.yimiao100.sale.activity.ShowWebImageActivity;

/**
 * 唤醒显示图片
 * Created by Michel on 2016/12/16.
 */

public class JavascriptInterface {
    private Context context;

    public JavascriptInterface(Context context) {
        this.context = context;
    }

    @android.webkit.JavascriptInterface
    public void openImage(String img) {
        Intent intent = new Intent();
        intent.putExtra("image", img);
        intent.setClass(context, ShowWebImageActivity.class);
        context.startActivity(intent);
    }


//    @android.webkit.JavascriptInterface
//    public void openHref(String url) {
//        Intent intent = new Intent();
//        intent.setAction("android.intent.action.VIEW");
//        Uri content_url = Uri.parse(url);
//        intent.setData(content_url);
//        ActivityCollector.getTopActivity().startActivity(intent);
//    }
}
