package com.yimiao100.sale.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.alibaba.fastjson.JSON;
import com.yimiao100.sale.activity.InformationDetailActivity;
import com.yimiao100.sale.activity.MainActivity;
import com.yimiao100.sale.activity.NoticeDetailActivity;
import com.yimiao100.sale.bean.JPushBean;
import com.yimiao100.sale.utils.LogUtil;

import cn.jpush.android.api.JPushInterface;

/**
 * 自定义极光推送广播接收处理
 * Created by Michel on 2016/12/29.
 */

public class JPushReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();

        if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            LogUtil.Companion.d("收到通知");
            String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
            String content = bundle.getString(JPushInterface.EXTRA_ALERT);
            String type = bundle.getString(JPushInterface.EXTRA_EXTRA);
            LogUtil.Companion.d("通知title:" + title + "content:" + content + "type:" + type);
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            //用户打开通知
            LogUtil.Companion.d("打开通知");
            String type = bundle.getString(JPushInterface.EXTRA_EXTRA);
            //根据json数据决定打开页面
            JPushBean jPushBean = JSON.parseObject(type, JPushBean.class);
            if (jPushBean == null || jPushBean.getMessage_type() == null || jPushBean.getMessage_type().isEmpty()) {
                return;
            }
            Class clz;
            String name = "";
            switch (jPushBean.getMessage_type()) {
                case "notice":
                    clz = NoticeDetailActivity.class;
                    name = "noticeId";
                    break;
                case "news":
                    clz = InformationDetailActivity.class;
                    name = "newsId";
                    break;
                default:
                    LogUtil.Companion.d("unKnownMessageType--" + jPushBean.getMessage_type());
                    clz = MainActivity.class;
                    break;
            }
            Intent i = new Intent(context, clz);
            i.putExtra(name, jPushBean.getTarget_id());
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        } else {
            LogUtil.Companion.d("Unhandled intent - " + intent.getAction());
        }
    }
}
