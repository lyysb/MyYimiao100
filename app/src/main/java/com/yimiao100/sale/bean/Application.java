package com.yimiao100.sale.bean;

import android.content.Context;
import android.support.multidex.MultiDex;

import java.util.Calendar;

/**
 * 大兄弟，不给测试机的公司你见过么？
 * Created by Michel on 2017/2/15.
 */

public class Application extends android.app.Application{

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        if (year == 2017) {
            // 初始化xxx
            MultiDex.install(this);
        }
    }

}
