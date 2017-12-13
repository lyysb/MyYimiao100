package com.yimiao100.sale.login;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.Utils;
import com.yimiao100.sale.bean.ErrorBean;
import com.yimiao100.sale.bean.SignUpBean;
import com.yimiao100.sale.bean.TokenInfoBean;
import com.yimiao100.sale.bean.UserInfoBean;
import com.yimiao100.sale.ext.JSON;
import com.yimiao100.sale.utils.BuglyUtils;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.SharePreferenceUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import okhttp3.Call;

/**
 * 在MVP模式中，Model的工作就是完成具体的业务操作，网络请求，持久化数据增删改查等任务。
 * Created by michel on 2017/12/11.
 */
public class LoginModel implements LoginContract.Model {

    private final LoginContract.Presenter mPresenter;

    private final String URL_LOGIN = Constant.BASE_URL + "/api/user/login";

    public LoginModel(LoginContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void login(String account, String pwd) {
        OkHttpUtils.post().url(URL_LOGIN)
                .addParams("accountNumber", account)
                .addParams("password", pwd)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                mPresenter.timeOut(e);
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtils.json(response);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        mPresenter.loginSuccess(JSON.parseObject(response, SignUpBean.class));
                        break;
                    case "failure":
                        mPresenter.loginFail(errorBean.getReason());
                        break;
                }
            }
        });
    }

    @Override
    public void clearData() {
        SPUtils.getInstance().clear();
    }

    @Override
    public void saveBuglyData(UserInfoBean userInfo) {
        BuglyUtils.putUserData(Utils.getApp(), userInfo);
    }

    @Override
    public void saveTokenInfo(TokenInfoBean tokenInfo) {
        SharePreferenceUtil.put(Utils.getApp(), Constant.ACCESSTOKEN, tokenInfo.getAccessToken());
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
