package com.yimiao100.sale.login;

import com.blankj.utilcode.util.LogUtils;
import com.yimiao100.sale.bean.SignUpBean;

/**
 * Created by michel on 2017/12/11.
 */
public class LoginPresenter implements LoginContract.Presenter {

    private final LoginContract.View mLoginView;
    private final LoginModel mLoginModel;

    public LoginPresenter(LoginContract.View view) {
        mLoginView = view;
        mLoginModel = new LoginModel(this);
    }

    @Override
    public void login(String account, String pwd) {
        mLoginModel.login(account, pwd);
        mLoginView.showProgress();
    }

    @Override
    public void loginSuccess(SignUpBean signInfo) {
        mLoginView.hideProgress();
        // 清除本地数据
        mLoginModel.clearData();
        // 保存新的数据
        mLoginModel.saveBuglyData(signInfo.getUserInfo());
        mLoginModel.saveTokenInfo(signInfo.getTokenInfo());
        mLoginModel.saveUserInfo(signInfo.getUserInfo());
        mLoginModel.saveLoginState(true);
        // 设置推送别名
        mLoginView.setUpAlias();
        mLoginView.navigateToMain();
        LogUtils.d("loginSuccess");
    }

    @Override
    public void loginFail(int reason) {
        mLoginView.hideProgress();
        mLoginView.showErrorInfo(reason);
        LogUtils.d("loginFail" + reason);
    }


    @Override
    public void timeOut(Exception e) {
        mLoginView.hideProgress();
        mLoginView.timeOut(e);
    }

    @Override
    public void onDestroy() {
        // 取消网络请求
    }
}
