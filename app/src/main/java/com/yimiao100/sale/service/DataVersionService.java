package com.yimiao100.sale.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.yimiao100.sale.base.ActivityCollector;
import com.yimiao100.sale.bean.DataVersionBean;
import com.yimiao100.sale.bean.ErrorBean;
import com.yimiao100.sale.ext.JSON;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.SharePreferenceUtil;
import com.yimiao100.sale.utils.Util;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

/**
 * 校验数据版本问题
 */
public class DataVersionService extends Service {

    private final int MSG_CHECK_VERSION = 10001;
    private final String URL_VERSION = Constant.BASE_URL + "/api/version/";
    private final String VERSION_KEY = "versionKey";
    private String mVersionKey;
    private final String URL_REGION = Constant.BASE_URL + "/api/region/all";

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_CHECK_VERSION:
                    //检查数据版本
                    checkDataVersion();
                    break;
                default:
                    LogUtil.d("Unknown msg ：" + msg);
                    break;
            }
        }
    };

    public DataVersionService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.d("启动数据版本检查");
        //检查数据版本号
        checkDataVersion();
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 检查数据版本号
     */
    private void checkDataVersion() {
        mVersionKey = "region";
        OkHttpUtils.get().url(URL_VERSION + mVersionKey).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.d("数据版本链接检查超时，将于60s之后重试");
                e.printStackTrace();
                // 延时60s继续检查
                mHandler.sendEmptyMessageDelayed(MSG_CHECK_VERSION, 60000);
            }

            @Override
            public void onResponse(String response, int id) {
                DataVersionBean dataVersionBean = JSON.parseObject(response, DataVersionBean.class);
                switch (dataVersionBean.getStatus()) {
                    case "success":
                        LogUtil.d("检查成功，比较本地版本号");
                        //请求数据成功
                        onSuccess(dataVersionBean);
                        break;
                    case "failure":
                        if (ActivityCollector.getTopActivity() != null) {
                            Util.showError(ActivityCollector.getTopActivity(), dataVersionBean
                                    .getReason());
                        }
                        break;
                }
            }
        });
    }

    /**
     * 请求数据成功
     * @param dataVersionBean
     */
    private void onSuccess(DataVersionBean dataVersionBean) {
        int versionCodeNow = (int) SharePreferenceUtil.get(
                getApplicationContext(), Constant.DATA_VERSION_ + mVersionKey, -1);
        int versionCode = dataVersionBean.getVersion().getVersionCode();
        //比较版本号
        if (versionCodeNow < versionCode) {
            LogUtil.d("本地版本为" + versionCodeNow + "，将进行更新数据");
            //联网更新数据
            updateRegionData(versionCode);
        } else {
            LogUtil.d("不需要更新数据，停止服务");
            //停止服务
            stopDataVersionService();
        }
    }

    /**
     * 升级地域信息
     * @param versionCode
     */
    private void updateRegionData(final int versionCode) {
        OkHttpUtils.get().url(URL_REGION).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.d("更新Region数据链接超时");
                e.printStackTrace();
            }

            @Override
            public void onResponse(String response, int id) {
//                if (response.length() > 4000) {
//                    for (int i = 0; i < response.length(); i += 4000) {
//                        if (i + 4000 < response.length()) {
//                            LogUtil.d(i + "updateRegionData：" + response.substring(i, i + 4000));
//                        } else {
//                            LogUtil.d(i + "updateRegionData：" + response.substring(i, response
//                                    .length()));
//                        }
//                    }
//                } else {
//                    LogUtil.d("updateRegionData：" + response);
//                }
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        //保存地域信息列表
                        SharePreferenceUtil.put(getApplicationContext(), Constant.REGION_LIST, response);
                        //保存最新的版本号
                        SharePreferenceUtil.put(getApplicationContext(), Constant.DATA_VERSION_ + mVersionKey, versionCode);
                        //停止服务
                        stopDataVersionService();
                        break;
                    case "failure":
                        Util.showError(ActivityCollector.getTopActivity(), errorBean.getReason());
                        break;
                }
            }
        });
    }

    /**
     * 停止自己这个服务
     */
    private void stopDataVersionService() {
        LogUtil.d("停止数据更新服务");
        stopService(new Intent(getApplicationContext(), DataVersionService.class));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeMessages(MSG_CHECK_VERSION);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
