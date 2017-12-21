package com.yimiao100.sale.ui.register;

import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.yimiao100.sale.bean.SignUpBean;
import com.yimiao100.sale.mvpbase.BasePresenter;

import org.json.JSONObject;

import java.util.HashMap;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;

/**
 * RegisterPresenter
 * Created by Michel on 2017/12/19.
 */

public class RegisterPresenter extends BasePresenter<RegisterContract.View> implements RegisterContract.Presenter {

    private final RegisterModel registerModel;
    private String password;

    public RegisterPresenter() {
        registerModel = new RegisterModel(this);
    }

    @Override
    public void onCreatePresenter(@Nullable Bundle savedState) {
        super.onCreatePresenter(savedState);
        // 注册短信回调接口
        EventHandler eventHandler = new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {
                super.afterEvent(event, result, data);
                // 注意。该回调不是在主线程中。
                switch (result) {
                    case SMSSDK.RESULT_COMPLETE:
                        // 操作成功
                        if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                            // 验证码成功
                            HashMap<String, Object> phoneMap = (HashMap<String, Object>) data;
                            LogUtils.d("phoneMap = " + phoneMap);
                            // 提交注册信息
                            registerModel.registerAccount((String) phoneMap.get("phone"), password);
                        }
                        break;
                    case SMSSDK.RESULT_ERROR:
                        // 操作失败
                        // 切回主线程，交由View处理错误信息
                        Observable.empty()
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnComplete(() -> getView().verifyError((Throwable) data))
                                .subscribe();
                        break;
                }
            }
        };
        SMSSDK.registerEventHandler(eventHandler);
    }

    @Override
    public void onDestroyPresenter() {
        super.onDestroyPresenter();
        // 反注册回调接口
        SMSSDK.unregisterAllEventHandler();
    }

    @Override
    public void getVerificationCode(String phone) {
        // 开启倒计时
        getView().countdown();
        // 获取验证码
        SMSSDK.getVerificationCode("86", phone);
    }

    @Override
    public void register(String phone, String verificationCode, String password) {
        // step1： 先验证验证码
        SMSSDK.submitVerificationCode("86", phone, verificationCode);
        this.password = password;
    }

    @Override
    public void registerSuccess(SignUpBean registerInfo) {
        // 注册成功
        getView().hideProgress();
        // 清除之前以后数据
        registerModel.clearData();
        // 保存token
        registerModel.saveTokenInfo(registerInfo.getTokenInfo());
        // 保存用户id
        registerModel.saveUserInfo(registerInfo.getUserInfo());
        // 保存Bugle用户信息
        registerModel.saveBudlyData(registerInfo.getUserInfo());
        // 保存用户登录状态
        registerModel.saveLoginState(true);
        // 设置别名
        getView().setupAlias();
        // 进入主界面
        getView().navigateToMain();

    }

    @Override
    public void registerFailure(int reason) {
        // 注册失败
        getView().hideProgress();
        // 显示失败信息
        getView().showFailureInfo(reason);
    }

}
