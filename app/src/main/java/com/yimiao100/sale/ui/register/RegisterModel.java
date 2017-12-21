package com.yimiao100.sale.ui.register;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.Utils;
import com.yimiao100.sale.api.Api;
import com.yimiao100.sale.bean.TokenInfoBean;
import com.yimiao100.sale.bean.UserInfoBean;
import com.yimiao100.sale.utils.BuglyUtils;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.SharePreferenceUtil;

import cn.smssdk.SMSSDK;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * RegisterModel
 * Created by Michel on 2017/12/19.
 */

public class RegisterModel implements RegisterContract.Model {

    private final RegisterPresenter registerPresenter;

    public RegisterModel(RegisterPresenter registerPresenter) {
        this.registerPresenter = registerPresenter;
    }

    @Override
    public void registerAccount(String phone, String password) {
        Api.getInstance().register(phone, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(signUpBean -> {
                    switch (signUpBean.getStatus()) {
                        case "success":
                            // 注册成功
                            registerPresenter.registerSuccess(signUpBean);
                            break;
                        case "failure":
                            // 注册失败
                            registerPresenter.registerFailure(signUpBean.getReason());
                            break;
                    }
                }, throwable -> registerPresenter.onError(throwable.getMessage()));
    }

    @Override
    public void clearData() {
        SPUtils.getInstance().clear();
        SharePreferenceUtil.clear(Utils.getApp());
    }

    @Override
    public void saveTokenInfo(TokenInfoBean tokenInfo) {
        SPUtils.getInstance().put(Constant.ACCESSTOKEN, tokenInfo.getAccessToken());
    }

    @Override
    public void saveUserInfo(UserInfoBean userInfo) {
        SPUtils.getInstance().put(Constant.USER_ID, userInfo.getId());
    }

    @Override
    public void saveBudlyData(UserInfoBean userInfo) {
        BuglyUtils.putUserData(Utils.getApp(), userInfo);
    }

    @Override
    public void saveLoginState(boolean isLogin) {
        SPUtils.getInstance().put(Constant.LOGIN_STATUS, isLogin);
    }
}
