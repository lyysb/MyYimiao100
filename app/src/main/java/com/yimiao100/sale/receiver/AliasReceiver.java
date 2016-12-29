package com.yimiao100.sale.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.yimiao100.sale.service.AliasService;

/**
 * JPush设置别名的广播接收者
 * Created by Michel on 2016/12/29.
 */

public class AliasReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //收到广播之后启动服务，设置别名
        context.startService(new Intent(context, AliasService.class));
    }
}
