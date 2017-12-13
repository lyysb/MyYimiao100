package com.yimiao100.sale.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.widget.ImageView;

import com.blankj.utilcode.util.SPUtils;
import com.yimiao100.sale.R;
import com.yimiao100.sale.base.ActivityCollector;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.bean.AppKeyBean;
import com.yimiao100.sale.ext.JSON;
import com.yimiao100.sale.service.DataVersionService;
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
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Request;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 欢迎界面
 */
public class SplashActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks{

    @BindView(R.id.splash)
    ImageView mSplash;

    private static final int REQUEST_CODE_INSTALL = 100;
    private static final int RC_STORAGE_PERM = 124;
    private final String URL_GET_APP_KEY = "http://www.pgyer.com/apiv1/app/getAppKeyByShortcut";
    private final String URL_INSTALL = "http://www.pgyer.com/apiv1/app/install";
    private final String SHORTCUT = "shortcut";
    private final String API_KEY = "_api_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        //开启服务，检查相关数据（区域信息）版本信息
        checkDataVersion();

        // 检查权限
        checkPermissions();
    }

    private void doNext() {
        //让界面停留2s；
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //联网检查更新
                checkVersion();
            }
        }, 2000);
    }

    @AfterPermissionGranted(RC_STORAGE_PERM)
    private void checkPermissions() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            LogUtil.d("已获取存储空间权限");
            // do next
            doNext();
        } else {
            // 申请权限
            LogUtil.d("没有权限，申请权限");
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_storage), RC_STORAGE_PERM, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }


    // 成功
    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        LogUtil.d("申请成功：" + perms);
    }

    // 失败
    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        LogUtil.d("申请失败：" + perms);
        if (EasyPermissions.somePermissionDenied(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            LogUtil.d("用户拒绝了存储空间权限");
            // 如果用户拒绝了权限，则进入权限设置界面进行设置
            showSettingDialog();
        }
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            LogUtil.d("用户用户点击了“不再询问”");
            // 如果用户点击了“不再询问”
            showSettingDialog();
        }
    }



    /**
     * 开启服务，检查相关数据（区域信息）版本信息
     */
    private void checkDataVersion() {
        startService(new Intent(this, DataVersionService.class));
    }


    /**
     * 联网检查更新
     */
    private void checkVersion() {
        //请求网络获取数据，解析数据
        OkHttpUtils.post().url(URL_GET_APP_KEY)
                .addParams(SHORTCUT, Constant.SHORTCUT)
                .addParams(API_KEY, Constant.UPDATE_API_KEY)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.d("验证APPKey短连接错误E：" + e.getLocalizedMessage());
                //访问失败，直接进入下一页
                enterNext();
                Util.showTimeOutNotice(currentContext);
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.d("验证App短链接：" + response);
                AppKeyBean.DataBean data = JSON.parseObject(response, AppKeyBean.class).getData();
                if (data != null) {
                    //获取服务器版本号
                    int appVersionNo = data.getAppVersionNo();
                    //获取当前版本号
                    int versionCode = AppUtil.getVersionCode(SplashActivity.this);
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
                    LogUtil.d("验证App短连接参数有误");
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
            boolean loginStatus = SPUtils.getInstance().getBoolean(Constant.LOGIN_STATUS);
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
                .addParams("aKey", appKey).addParams(API_KEY, Constant.UPDATE_API_KEY)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                e.printStackTrace();
                //进入下一页
                enterNext();
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.d("获取版本信息:" + response);
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
                    LogUtil.d("获取APP信息参数有误");
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
        dialog.setTitle(getString(R.string.download_progress_dialog_title));
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        //Get请求下载Apk
        OkHttpUtils.get().url(URL_INSTALL)
                .addParams("aKey", appKey).addParams(API_KEY, Constant.PGYER_API_KEY)
                .build()
                .execute(new FileCallBack(Util.getApkPath(), "VaccineCircle.apk") {
                    @Override
                    public void onBefore(Request request, int id) {
                        super.onBefore(request, id);
                        LogUtil.d("开始下载");
                        //下载开始显示进度条
                        dialog.show();
                    }

                    @Override
                    public void inProgress(float progress, long total, int id) {
                        super.inProgress(progress, total, id);
                        LogUtil.d("下载中");
                        //显示进度
                        dialog.setProgress((int) (100 * progress + 0.5));
                    }

                    @Override
                    public void onAfter(int id) {
                        super.onAfter(id);
                        LogUtil.d("下载完成");
                        //进度消失
                        dialog.dismiss();
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        //下载失败，对话框消失，进入下一页
                        LogUtil.d("下载失败E：" + e.getLocalizedMessage());
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
        Uri apkUri;
        if (Build.VERSION.SDK_INT >= 24) {
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            apkUri = FileProvider.getUriForFile(this, "com.yimiao100.sale.fileprovider", apk);
        } else {
            apkUri = Uri.fromFile(apk);
        }
        intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        startActivityForResult(intent, REQUEST_CODE_INSTALL);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_INSTALL) {
            // 从安装页面返回
            if (resultCode == RESULT_CANCELED) {
                ActivityCollector.finishAll();
                return;
            }
            enterNext();
        } else if (requestCode == RC_SETTINGS_SCREEN) {
            // 从设置权限界面返回
            LogUtil.d("从权限设置界面返回");
            checkPermissions();
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
//        startActivity(new Intent(this, LoginActivity.class));
        com.yimiao100.sale.login.LoginActivity.start(this);
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
