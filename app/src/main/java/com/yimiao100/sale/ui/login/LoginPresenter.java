package com.yimiao100.sale.ui.login;

import com.blankj.utilcode.util.LogUtils;
import com.yimiao100.sale.bean.SignUpBean;
import com.yimiao100.sale.mvpbase.BasePresenter;

/**
 * LoginPresenter
 * Created by michel on 2017/12/11.
 */
public class LoginPresenter extends BasePresenter<LoginContract.View> implements LoginContract.Presenter {

    private final LoginModel mLoginModel;

    public LoginPresenter() {
        mLoginModel = new LoginModel(this);
    }

    @Override
    public void login(String account, String pwd) {
        mLoginModel.login(account, pwd);
        getView().showProgress();
    }

    @Override
    public void loginSuccess(SignUpBean signInfo) {
        getView().hideProgress();
        // 清除本地数据
        mLoginModel.clearData();
        // 保存新的数据
        mLoginModel.saveBuglyData(signInfo.getUserInfo());
        mLoginModel.saveTokenInfo(signInfo.getTokenInfo());
        mLoginModel.saveUserInfo(signInfo.getUserInfo());
        mLoginModel.saveLoginState(true);
        // 设置推送别名
        getView().setUpAlias();
        getView().navigateToMain();
        LogUtils.d("loginSuccess");
    }

    @Override
    public void loginFail(int reason) {
        getView().hideProgress();
        getView().showFailureInfo(reason);
        LogUtils.d("loginFail" + reason);
    }

}
