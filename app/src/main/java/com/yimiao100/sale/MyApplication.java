package com.yimiao100.sale;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.alibaba.fastjson.parser.ParserConfig;
import com.meiqia.meiqiasdk.util.MQConfig;
import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.yimiao100.sale.utils.Constant;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.concurrent.TimeUnit;

import cn.jpush.android.api.JPushInterface;
import okhttp3.OkHttpClient;

/**
 *
 * Created by 亿苗通 on 2016/7/27.
 */
public class MyApplication extends Application{

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
        initFastJson();
        initJPush();
        initMQ();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

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
     *
     */
    private void initFastJson() {
        //解决FastJson报错？？待测试--米5
        ParserConfig.getGlobalInstance().setAsmEnable(false);
    }

    /**
     * 极光推送
     */
    private void initJPush() {
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
    }

}
