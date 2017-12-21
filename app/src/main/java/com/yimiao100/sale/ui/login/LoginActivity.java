package com.yimiao100.sale.ui.login;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.LogUtils;
import com.jakewharton.rxbinding2.view.RxView;
import com.yimiao100.sale.R;
import com.yimiao100.sale.activity.ForgetPwdActivity;
import com.yimiao100.sale.activity.MainActivity;
import com.yimiao100.sale.activity.RegisterActivity;
import com.yimiao100.sale.bean.Area;
import com.yimiao100.sale.bean.City;
import com.yimiao100.sale.bean.Province;
import com.yimiao100.sale.mvpbase.BaseActivity;
import com.yimiao100.sale.mvpbase.CreatePresenter;
import com.yimiao100.sale.service.AliasService;
import com.yimiao100.sale.utils.RegionUtil;
import com.yimiao100.sale.utils.ToastUtil;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * LoginActivity
 */
// 声明需要创建的Presenter
@CreatePresenter(LoginPresenter.class)
public class LoginActivity extends BaseActivity<LoginContract.View, LoginContract.Presenter> implements LoginContract.View {

    private EditText mEtAccount;
    private EditText mEtPwd;
    private CheckBox mCbShowPwd;

    public static void start(Context context) {
        context.startActivity(new Intent(context, LoginActivity.class));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void init() {
        super.init();
        mEtAccount = (EditText) findViewById(R.id.login_phone);
        mEtPwd = (EditText) findViewById(R.id.login_password);
        mCbShowPwd = (CheckBox) findViewById(R.id.show_password);
        // 显示密码
        mCbShowPwd.setOnClickListener(v -> {
            LogUtils.d("mCbShowPwd.isChecked()?" + mCbShowPwd.isChecked());
            showPwd(mCbShowPwd.isChecked());
        });
        // 忘记密码
        findViewById(R.id.login_forget_password).setOnClickListener(v -> navigateToForgetter());

        // 注册
        findViewById(R.id.login_register).setOnClickListener(v -> navigateToRegister());

        // 登录
        RxView.clicks(findViewById(R.id.login_login))
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(o -> {
                    String account = mEtAccount.getText().toString().trim();
                    String pwd = mEtPwd.getText().toString().trim();
                    if (TextUtils.isEmpty(account) || TextUtils.isEmpty(pwd)) {
                        ToastUtil.showShort(LoginActivity.this, "账号密码不能为空");
                        return;
                    }
                    getPresenter().login(account, pwd);
                });

    }


    @Override
    public void showPwd(boolean isShow) {
        if (isShow) {
            //如果选中，显示密码
            mEtPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            //设置光标移动到最后
            mEtPwd.setSelection(mEtPwd.getText().length());
        } else {
            //否则隐藏密码
            mEtPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
            //设置光标移动到最后
            mEtPwd.setSelection(mEtPwd.getText().length());
        }
    }


    @Override
    public void navigateToMain() {
        MainActivity.start(this);
        finish();
    }

    @Override
    public void setUpAlias() {
        startService(new Intent(this, AliasService.class));
    }

    @Override
    public void navigateToRegister() {
//        RegisterActivity.start(this);
        com.yimiao100.sale.ui.register.RegisterActivity.startAction(this);
    }

    @Override
    public void navigateToForgetter() {
        ForgetPwdActivity.start(this);
    }
}
