package com.yimiao100.sale.ui.register;

import com.yimiao100.sale.bean.SignUpBean;
import com.yimiao100.sale.bean.TokenInfoBean;
import com.yimiao100.sale.bean.UserInfoBean;
import com.yimiao100.sale.mvpbase.IBaseModel;
import com.yimiao100.sale.mvpbase.IBasePresenter;
import com.yimiao100.sale.mvpbase.IBaseView;

/**
 * RegisterContract
 * Created by Michel on 2017/12/19.
 */

public interface RegisterContract {

    interface View extends IBaseView<Presenter>{
        void countdown();

        void showPwd(boolean isShow);

        void verifyError(Throwable throwable);

        void navigateToLogin();

        void navigateToMain();

        void setupAlias();
    }

    interface Presenter extends IBasePresenter<View>{
        void getVerificationCode(String phone);

        void register(String phone, String verificationCode, String password);

        void registerSuccess(SignUpBean registerInfo);

        void registerFailure(int reason);
    }

    interface Model extends IBaseModel{
        void registerAccount(String phone, String password);

        void clearData();

        void saveTokenInfo(TokenInfoBean tokenInfo);

        void saveUserInfo(UserInfoBean userInfo);

        void saveBudlyData(UserInfoBean userInfo);

        void saveLoginState(boolean isLogin);
    }
}
