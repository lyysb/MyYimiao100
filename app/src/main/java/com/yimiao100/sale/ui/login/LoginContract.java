package com.yimiao100.sale.ui.login;

import com.yimiao100.sale.mvpbase.IBaseModel;
import com.yimiao100.sale.mvpbase.IBasePresenter;
import com.yimiao100.sale.mvpbase.IBaseView;
import com.yimiao100.sale.bean.SignUpBean;
import com.yimiao100.sale.bean.TokenInfoBean;
import com.yimiao100.sale.bean.UserInfoBean;

/**
 * LoginContract
 * Created by michel on 2017/12/11.
 */
public interface LoginContract {

    interface View extends IBaseView<Presenter> {
        void showPwd(boolean isShow);

        void navigateToMain();

        void setUpAlias();

        void navigateToRegister();

        void navigateToForgetter();
    }

    interface Presenter extends IBasePresenter<View> {
        void login(String account, String pwd);

        void loginSuccess(SignUpBean signInfo);

        void loginFail(int reason);
    }

    interface Model extends IBaseModel{
        void login(String account, String pwd);

        void clearData();

        void saveBuglyData(UserInfoBean userInfo);

        void saveTokenInfo(TokenInfoBean tokenInfo);

        void saveUserInfo(UserInfoBean userInfo);

        void saveLoginState(boolean isLogin);
    }
}
