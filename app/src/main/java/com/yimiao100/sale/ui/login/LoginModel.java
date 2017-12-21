package com.yimiao100.sale.ui.login;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.Utils;
import com.yimiao100.sale.api.Api;
import com.yimiao100.sale.bean.TokenInfoBean;
import com.yimiao100.sale.bean.UserInfoBean;
import com.yimiao100.sale.utils.BuglyUtils;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.SharePreferenceUtil;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 在MVP模式中，Model的工作就是完成具体的业务操作，网络请求，持久化数据增删改查等任务。
 * Created by michel on 2017/12/11.
 */
public class LoginModel implements LoginContract.Model {

    private final LoginContract.Presenter mPresenter;

    public LoginModel(LoginContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void login(String account, String pwd) {
        Api.getInstance()
                .login(account, pwd)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(signUpBean -> {
                    LogUtils.d(signUpBean);
                    switch (signUpBean.getStatus()) {
                        case "success":
                            mPresenter.loginSuccess(signUpBean);
                            break;
                        case "failure":
                            mPresenter.loginFail(signUpBean.getReason());
                            break;
                    }
                }, throwable -> mPresenter.onError(throwable.getMessage()));
    }

    @Override
    public void clearData() {
        // 由于mvp和mvc两套框架同时存在，所以存取删除token的相关操作暂时需要使用两个类
        SPUtils.getInstance().clear();
        SharePreferenceUtil.clear(Utils.getApp());
    }

    @Override
    public void saveBuglyData(UserInfoBean userInfo) {
        BuglyUtils.putUserData(Utils.getApp(), userInfo);
    }

    @Override
    public void saveTokenInfo(TokenInfoBean tokenInfo) {
        SPUtils.getInstance().put(Constant.ACCESSTOKEN, tokenInfo.getAccessToken());
    }

    @Override
    public void saveUserInfo(UserInfoBean userInfo) {
        //用户id
        SPUtils.getInstance().put(Constant.USER_ID, userInfo.getId());
        //用户账号
        SPUtils.getInstance().put(Constant.ACCOUNT_NUMBER, userInfo.getAccountNumber());
        //用户姓名
        if (userInfo.getCnName() != null && !userInfo.getCnName().isEmpty()) {
            SPUtils.getInstance().put(Constant.CNNAME, userInfo.getCnName());
        }
        //用户电话
        if (userInfo.getPhoneNumber() != null && !userInfo.getPhoneNumber().isEmpty()) {
            SPUtils.getInstance().put(Constant.PHONENUMBER, userInfo.getPhoneNumber());
        }
        //用户邮箱
        if (userInfo.getEmail() != null && !userInfo.getEmail().isEmpty()) {
            SPUtils.getInstance().put(Constant.EMAIL, userInfo.getEmail());
        }
        //用户地域信息
        if (userInfo.getProvinceName() != null && !userInfo.getProvinceName().isEmpty()
                && userInfo.getCityName() != null && !userInfo.getCityName().isEmpty()
                && userInfo.getAreaName() != null && !userInfo.getAreaName().isEmpty()) {
            SPUtils.getInstance().put(Constant.REGION, userInfo.getProvinceName()
                    + "\t" + userInfo.getCityName()
                    + "\t" + userInfo.getAreaName());
        }
        //用户身份证号
        if (userInfo.getIdNumber() != null && !userInfo.getIdNumber().isEmpty()) {
            SPUtils.getInstance().put(Constant.IDNUMBER, userInfo.getIdNumber());
        }
        //用户头像地址
        if (userInfo.getProfileImageUrl() != null && !userInfo.getProfileImageUrl().isEmpty()) {
            SPUtils.getInstance().put(Constant.PROFILEIMAGEURL, userInfo.getProfileImageUrl());
        }
    }

    @Override
    public void saveLoginState(boolean isLogin) {
        SPUtils.getInstance().put(Constant.LOGIN_STATUS, isLogin);
    }

}
