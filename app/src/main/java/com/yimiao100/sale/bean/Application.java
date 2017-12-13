package com.yimiao100.sale.bean;

import android.content.Context;
import android.support.multidex.MultiDex;

import java.util.Calendar;

/**
 *
 * Created by Michel on 2017/2/15.
 */

public class Application extends android.app.Application{

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        // 初始化xxx
        MultiDex.install(this);

    }

}
