package com.yimiao100.sale;

import android.util.DisplayMetrics;

import com.blankj.utilcode.util.Utils;
import com.bumptech.glide.annotation.GlideModule;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.footer.LoadingView;
import com.lcodecore.tkrefreshlayout.header.progresslayout.ProgressLayout;
import com.meiqia.meiqiasdk.imageloader.MQImage;
import com.meiqia.meiqiasdk.util.MQConfig;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.uuch.adlibrary.utils.DisplayUtil;
import com.yimiao100.sale.bean.Application;
import com.yimiao100.sale.glide.MQGlideImageLoader4;
import com.yimiao100.sale.other.HttpLoggingInterceptor;
import com.yimiao100.sale.utils.BuglyUtils;
import com.yimiao100.sale.utils.Constant;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.concurrent.TimeUnit;

import cn.jpush.android.api.JPushInterface;
import okhttp3.OkHttpClient;

/**
 * Created by Michel on 2016/7/27.
 */
public class MyApplication extends Application {

    {
        // 友盟初始化
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
        // 因为广告库使用了Fresco
        Fresco.initialize(this);
        initDisplayOpinion();
        initUtilCode();
        initRefreshLayout();
    }

    /**
     * TwinklingRefreshLayout
     */
    private void initRefreshLayout() {
        TwinklingRefreshLayout.setDefaultHeader(ProgressLayout.class.getName());
        TwinklingRefreshLayout.setDefaultFooter(LoadingView.class.getName());
    }

    /**
     * AndroidUtilCode初始化
     */
    private void initUtilCode() {
        Utils.init(this);
    }

    /**
     * 广告库所需设置
     */
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
        // 解决美洽对Glide4的兼容问题
        MQImage.setImageLoader(new MQGlideImageLoader4());
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
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();

        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
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
        BuglyUtils.initBugly(getApplicationContext());
    }



    /**
     * 极光推送
     */
    private void initJPush() {
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
    }

}
