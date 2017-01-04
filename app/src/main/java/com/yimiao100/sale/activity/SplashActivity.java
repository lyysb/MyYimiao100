package com.yimiao100.sale.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.yimiao100.sale.R;
import com.yimiao100.sale.base.ActivityCollector;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.bean.AppKeyBean;
import com.yimiao100.sale.bean.ErrorBean;
import com.yimiao100.sale.utils.AppUtil;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.SharePreferenceUtil;
import com.yimiao100.sale.utils.ToastUtil;
import com.yimiao100.sale.utils.Util;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jpush.android.api.JPushInterface;
import okhttp3.Call;
import okhttp3.Request;

/**
 * 欢迎界面
 */
public class SplashActivity extends BaseActivity {

    @BindView(R.id.splash)
    ImageView mSplash;

    private static final int REQUEST_CODE_INSTALL = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        if (!JPushInterface.isPushStopped(this)) {
            LogUtil.Companion.d("推送服务运行中，停止推送服务");
            //停止推送
            JPushInterface.stopPush(this);
        }

        //让界面停留2s
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //联网检查更新
                checkVersion();
            }
        }, 2000);

        //刷新地域信息
        refreshRegion();

    }

    /**
     * 刷新region信息
     */
    private void refreshRegion() {
        OkHttpUtils.get().url(Constant.BASE_URL + "/api/region/all").build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        if (response.length() > 4000) {
                            for (int i = 0; i < response.length(); i += 4000) {
                                if (i + 4000 < response.length()) {
                                    LogUtil.Companion.d(i + "获取地域信息：" + response.substring(i, i + 4000));
                                } else {
                                    LogUtil.Companion.d(i + "获取地域信息：" + response.substring(i, response.length()));
                                }
                            }
                        } else {
                            LogUtil.Companion.d("获取地域信息：" + response);
                        }
                        ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                        switch (errorBean.getStatus()) {
                            case "success":
                                //保存地域信息列表
                                SharePreferenceUtil.put(currentContext, Constant.REGION_LIST, response);
                                break;
                            case "failure":
                                Util.showError(currentContext, errorBean.getReason());
                                break;
                        }
                    }
                });
    }


    /**
     * 联网检查更新
     */
    private void checkVersion() {
        //请求网络获取数据，解析数据
        OkHttpUtils.post().url("http://www.pgyer.com/apiv1/app/getAppKeyByShortcut")
                .addParams("shortcut", "EH4S")
                .addParams("_api_key", "6160ef2a74b29ad9a9d537866936fd79")
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.Companion.d("验证APPKey短连接错误E：" + e.getLocalizedMessage());
                //访问失败，直接进入下一页
                enterNext();
                Util.showTimeOutNotice(currentContext);
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.Companion.d("验证App短链接：" + response);
                AppKeyBean.DataBean data = JSON.parseObject(response, AppKeyBean.class).getData();
                if (data != null) {
                    int appVersionNo = data.getAppVersionNo();
                    //获取当前版本号
                    int versionCode = AppUtil.Companion.getVersionCode(SplashActivity.this);
                    if (appVersionNo > versionCode) {
                        //有新版本
                        //获取版本信息
                        checkUpdateInformation(data.getAppKey());
                    } else {
                        //没有新版本，直接进入下一页
                        enterNext();
                    }
                } else {
                    //参数有误，直接进入下一页
                    LogUtil.Companion.d("验证App短连接参数有误");
                    enterNext();
                }
            }
        });

    }

    /**
     * 进入下一页，判断是否需要进入引导页
     */
    private void enterNext() {
        //获取是否是第一次登录
        boolean isFist = (boolean) SharePreferenceUtil.get(currentContext, Constant.IS_FIRST, true);
        //暂时取消引导页
        isFist = false;
        if (isFist) {
            //进入引导界面
            enterGuide();
        } else {
            //获取是否已经登录
            boolean loginStatus = (boolean) SharePreferenceUtil.get(currentContext, Constant
                    .LOGIN_STATUS, false);
            if (loginStatus) {
                //已登录，进入主界面
                enterHome();
            } else {
                //未登录，进入登录界面
                enterLogin();
            }
        }
    }

    /**
     * 检查应用版本信息
     */
    private void checkUpdateInformation(String appKey) {
        OkHttpUtils.post().url("http://www.pgyer.com/apiv1/app/view")
                .addParams("aKey", appKey).addParams("_api_key", "6160ef2a74b29ad9a9d537866936fd79")
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                e.printStackTrace();
                //进入下一页
                enterNext();
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.Companion.d("获取版本信息:" + response);
                //获取版本信息
                final AppKeyBean.DataBean data = JSON.parseObject(response, AppKeyBean.class)
                        .getData();
                if (data != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
                    builder.setTitle("发现新版本");
                    String appUpdateDescription = data.getAppUpdateDescription();
                    builder.setMessage(appUpdateDescription);
                    //判断是否是重大更新
                    if (appUpdateDescription.contains("重大更新")) {
                        //如果存在重大更新，只能更新或者退出应用
                        builder.setCancelable(false);
                        builder.setNegativeButton("退出应用", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                //退出应用
                                ActivityCollector.finishAll();
                            }
                        });
                        //有重大更新的时候重置引导页
                        SharePreferenceUtil.put(currentContext, Constant.IS_FIRST, true);
                    } else {
                        //如果不是重大更新
                        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                //取消，进入下一页
                                enterNext();
                            }
                        });
                        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                //dialog消失时候，直接进入下一页
                                enterNext();
                            }
                        });
                    }
                    builder.setPositiveButton("下载新版本", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //下载
                            //确定，下载新版本，调用安装新版本apk
                            downloadNewApp(data.getAppKey());
                            dialog.dismiss();
                        }
                    });

                    AlertDialog dialog = builder.create();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        if (SplashActivity.this.isDestroyed()) {
                            //如果Activity被销毁，则不需要弹出Dialog
                            return;
                        }
                    }
                    //显示对话框
                    dialog.show();
                } else {
                    //参数有误，直接进入下一页
                    LogUtil.Companion.d("获取APP信息参数有误");
                    enterNext();
                }
            }
        });
    }

    /**
     * 下载最新版本的Apk
     */
    private void downloadNewApp(String appKey) {
        // 判断sd卡是否正常
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            ToastUtil.showShort(getApplicationContext(), "sd卡异常");
            //进入下一页
            enterNext();
            return;
        }
        final ProgressDialog dialog = new ProgressDialog(SplashActivity.this);
        //禁止关闭进度条
        dialog.setCancelable(false);
        dialog.setTitle("正在下载，请稍后");
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        //Get请求下载Apk
        OkHttpUtils.get().url("http://www.pgyer.com/apiv1/app/install")
                .addParams("aKey", appKey).addParams("_api_key", Constant.PGYER_API_KEY)
                .build()
                .execute(new FileCallBack(Environment.getExternalStorageDirectory()
                        .getAbsolutePath(), "yimiaoquan.apk") {
                    @Override
                    public void onBefore(Request request, int id) {
                        super.onBefore(request, id);
                        LogUtil.Companion.d("开始下载");
                        //下载开始显示进度条
                        dialog.show();
                    }

                    @Override
                    public void inProgress(float progress, long total, int id) {
                        super.inProgress(progress, total, id);
                        LogUtil.Companion.d("下载中");
                        //显示进度
                        dialog.setProgress((int) (100 * progress + 0.5));
                    }

                    @Override
                    public void onAfter(int id) {
                        super.onAfter(id);
                        LogUtil.Companion.d("下载完成");
                        //进度消失
                        dialog.dismiss();
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        //下载失败，对话框消失，进入下一页
                        LogUtil.Companion.d("下载失败E：" + e.getLocalizedMessage());
                        ToastUtil.showShort(currentContext, "下载出错");
                        dialog.dismiss();
                        enterNext();
                    }

                    @Override
                    public void onResponse(File response, int id) {
                        //下载成功，安装Apk
                        dialog.dismiss();
                        installApk(response);
                    }
                });
    }

    private void installApk(File apk) {
        // 隐式调用系统的安装界面
        Intent intent = new Intent();
        // intent.setAction("android.intent.action.VIEW");
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setDataAndType(Uri.fromFile(apk),
                "application/vnd.android.package-archive");
        startActivityForResult(intent, REQUEST_CODE_INSTALL);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_INSTALL) {// 从安装页面返回
            enterNext();
        }
    }

    /**
     * 进入引导页
     */
    private void enterGuide() {
        //进入引导页
        startActivity(new Intent(this, GuideActivity.class));
        finish();
    }

    /**
     * 进入登录注册界面
     */
    private void enterLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    /**
     * 进入主页面
     */
    private void enterHome() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
