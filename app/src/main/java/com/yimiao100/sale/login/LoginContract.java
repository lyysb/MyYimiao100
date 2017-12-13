package com.yimiao100.sale.login;

import com.yimiao100.sale.base.BasePresenter;
import com.yimiao100.sale.base.BaseView;
import com.yimiao100.sale.bean.SignUpBean;
import com.yimiao100.sale.bean.TokenInfoBean;
import com.yimiao100.sale.bean.UserInfoBean;

/**
 * Created by michel on 2017/12/11.
 */
public interface LoginContract {

    interface View extends BaseView {
        void showPwd(boolean isShow);

        void navigateToMain();

        void setUpAlias();

        void navigateToRegister();

        void navigateToForgetter();
    }

    interface Presenter extends BasePresenter{
        void login(String account, String pwd);

        void loginSuccess(SignUpBean signInfo);

        void loginFail(int reason);

        void timeOut(Exception e);
    }

    interface Model {
        void login(String account, String pwd);

        void clearData();

        void saveBuglyData(UserInfoBean userInfo);

        void saveTokenInfo(TokenInfoBean tokenInfo);

        void saveUserInfo(UserInfoBean userInfo);

        void saveLoginState(boolean isLogin);

    }
}
