package com.yimiao100.sale;

import android.util.DisplayMetrics;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.meiqia.meiqiasdk.util.MQConfig;
import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.uuch.adlibrary.utils.DisplayUtil;
import com.yimiao100.sale.bean.Application;
import com.yimiao100.sale.utils.Constant;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.log.LoggerInterceptor;

import java.util.concurrent.TimeUnit;

import cn.jpush.android.api.JPushInterface;
import okhttp3.OkHttpClient;

/**
 * Created by Michel on 2016/7/27.
 */
public class MyApplication extends Application {

    {
        //微信 appId appSecret
        PlatformConfig.setWeixin(Constant.WX_APP_ID, Constant.WX_APP_SECRET);
        //新浪微博 appKey appSecret
        PlatformConfig.setSinaWeibo(Constant.SINA_APP_KEY, Constant.SINA_APP_SECRET);
        // QQ和QZone appId appKey
        PlatformConfig.setQQZone(Constant.QQ_APP_ID, Constant.QQ_APP_KEY);
    }
    @Override
    public void onCreate() {
        super.onCreate();
        initUMShare();
        initOkHttpUtils();
        initBugly();
        initJPush();
        initMQ();
//        Picasso.with(this).setIndicatorsEnabled(true);
        Fresco.initialize(this);
        initDisplayOpinion();
    }

    private void initDisplayOpinion() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        DisplayUtil.density = dm.density;
        DisplayUtil.densityDPI = dm.densityDpi;
        DisplayUtil.screenWidthPx = dm.widthPixels;
        DisplayUtil.screenhightPx = dm.heightPixels;
        DisplayUtil.screenWidthDip = DisplayUtil.px2dip(getApplicationContext(), dm.widthPixels);
        DisplayUtil.screenHightDip = DisplayUtil.px2dip(getApplicationContext(), dm.heightPixels);
    }

    /**
     * 美洽
     */
    private void initMQ() {
        //显示客户头像
        MQConfig.isShowClientAvatar = true;

    }

    /**
     * 友盟分享
     */
    private void initUMShare() {
        UMShareAPI.get(this);
        //设置微博分享url
        Config.REDIRECT_URL = Constant.SINA_SHARE_URL;
    }

    /**
     * OkHttp
     */
    private void initOkHttpUtils() {
        //初始化OKHttp
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new LoggerInterceptor("TAG"))
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                //其他配置
                .build();
        OkHttpUtils.initClient(okHttpClient);
    }

    /**
     * 腾讯bugly
     */
    private void initBugly() {
        //腾讯bugly设置
        CrashReport.initCrashReport(getApplicationContext(), "900059080", true);
    }



    /**
     * 极光推送
     */
    private void initJPush() {
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
    }

}
