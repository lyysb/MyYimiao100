package com.yimiao100.sale.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.SharePreferenceUtil;

import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * 设置别名的服务
 * create by Michel
 */
public class AliasService extends Service {

    private static final int MSG_SET_ALIAS = 1001;
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SET_ALIAS:
                    String alias = (String) msg.obj;
                    LogUtil.Companion.d("通过Handler设置别名--" + alias);
                    //调用JPush接口来设置别名
                    JPushInterface.setAlias(getApplicationContext(), alias, mAliasCallback);
                    break;
                default:
                    LogUtil.Companion.d("Unhandled msg - " + msg.what);
                    break;
            }
        }
    };

    public AliasService() {
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.Companion.d("onStartCommand");
        String alias = "";
        // 根据登录状态判断启动推送？停止推送？
        boolean loginStatus = (boolean) SharePreferenceUtil.get(this, Constant.LOGIN_STATUS, false);
        if (loginStatus) {
            LogUtil.Companion.d("已登录");
            //恢复推送
            boolean pushStopped = JPushInterface.isPushStopped(this);
            if (pushStopped) {
                //启动服务设置别名
                LogUtil.Companion.d("推送服务被停止，启动推送服务");
                JPushInterface.resumePush(this);
            }
            // 别名= “jpush_user_alias_加上用户id”
            int userId = (int) SharePreferenceUtil.get(this, Constant.USERID, -1);
            alias = "jpush_user_alias_" + userId;
            //设置别名
            setAlias(alias);
        } else {
            //退出应用
            //如果服务没有被停止，则停止推送
            if (!JPushInterface.isPushStopped(this)) {
                LogUtil.Companion.d("推送服务运行中，停止推送服务");
                //停止推送
                JPushInterface.stopPush(this);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 设置成功时，往 SharePreference 里写状态，以后不必再设置
     * 遇到 6002 超时，则稍延迟重试。
     */
    private void setAlias(String alias) {
        // 调用 Handler 来异步设置别名
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_ALIAS, alias));
    }

    private final TagAliasCallback mAliasCallback = new TagAliasCallback() {

        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            switch (code) {
                case 0:
                    //成功
                    LogUtil.Companion.d("设置别名成功，停止别名设置服务");
                    //停止服务
                    stopService(new Intent(getApplicationContext(), AliasService.class));
                    break;
                case 6002:
                    LogUtil.Companion.d("设置别名超时，将在60s后重试");
                    // 延迟 60 秒来调用 Handler 设置别名
                    mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ALIAS, alias), 1000 * 60);
                    break;
                default:
                    LogUtil.Companion.d("设置失败errorCode = " + code);
                    break;
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
