package com.yimiao100.sale.ui.register;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxCompoundButton;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.yimiao100.sale.R;
import com.yimiao100.sale.activity.MainActivity;
import com.yimiao100.sale.mvpbase.BaseActivity;
import com.yimiao100.sale.mvpbase.CreatePresenter;
import com.yimiao100.sale.mvpbase.IBaseView;
import com.yimiao100.sale.service.AliasService;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.ToastUtil;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import cn.smssdk.SMSSDK;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * RegisterActivity
 */
// 声明需要创建的Presenter
@CreatePresenter(RegisterPresenter.class)
public class RegisterActivity extends BaseActivity<RegisterContract.View, RegisterContract.Presenter> implements RegisterContract.View {

    private EditText etPhone;
    private Button btGetCode;
    private Disposable disposable;
    private CheckBox ckShowPwd;
    private EditText etPwd1;
    private EditText etPwd2;

    public static void startAction(Activity activity) {
        activity.startActivity(new Intent(activity, RegisterActivity.class));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_register;
    }

    @Override
    protected void init() {
        super.init();

        initSMSSDK();

        initView();
    }

    private void initSMSSDK() {
        // 初始化SMSSDK
        SMSSDK.initSDK(this, Constant.MOB_APP_KEY, Constant.MOB_APP_SECRET);
    }

    private void initView() {
        // 手机号
        etPhone = (EditText) findViewById(R.id.register_phone);
        // 获取验证码
        btGetCode = (Button) findViewById(R.id.register_getcode);
        RxView.clicks(btGetCode)
                .throttleFirst(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> {
                            // 校验手机号
                            String phone = etPhone.getText().toString().trim();
                            if (TextUtils.isEmpty(phone)) {
                                ToastUtil.showShort(Utils.getApp(), "请输入手机号码");
                                return;
                            }
                            if (phone.length() != 11) {
                                ToastUtil.showShort(Utils.getApp(), "请输入合法的手机号码");
                                return;
                            }
                            // 请求验证码
                            getPresenter().getVerificationCode(phone);
                        }
                );
        // 验证码
        EditText etVerificationCode = (EditText) findViewById(R.id.register_code);

        // 第一次输入密码
        etPwd1 = (EditText) findViewById(R.id.register_password);
        // 第二次输入密码
        etPwd2 = (EditText) findViewById(R.id.register_password_re);
        // 显示密码
        ckShowPwd = (CheckBox) findViewById(R.id.show_password);
        ckShowPwd.setOnClickListener(v -> showPwd(ckShowPwd.isChecked()));

        // 提交注册信息
        RxView.clicks(findViewById(R.id.register_register))
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(o -> {
                    String phone = etPhone.getText().toString();
                    String verificationCode = etVerificationCode.getText().toString();
                    String pwd1 = etPwd1.getText().toString().trim();
                    String pwd2 = etPwd2.getText().toString().trim();
                    if (localVerifySuccess(phone, verificationCode, pwd1, pwd2)) {
                        // 注册账号
                        getPresenter().register(phone, verificationCode, pwd1);

                    }
                });
        // 回到登录界面
        findViewById(R.id.return_login).setOnClickListener(v -> navigateToLogin());
    }

    private boolean localVerifySuccess(String phone, String verificationCode, String pwd1, String pwd2) {
        if (phone.length() != 11) {
            ToastUtil.showShort(Utils.getApp(), "请输入正确手机号码");
            return false;
        }
        if (TextUtils.isEmpty(verificationCode)) {
            ToastUtil.showShort(Utils.getApp(), "请输入验证码");
            return false;
        }
        if (TextUtils.isEmpty(pwd1)) {
            ToastUtil.showShort(Utils.getApp(), "请输入密码");
            return false;
        }
        if (pwd1.length() < 6) {
            ToastUtil.showShort(Utils.getApp(), "密码长度不得少于6位");
            return false;
        }
        if (TextUtils.isEmpty(pwd2)) {
            ToastUtil.showShort(Utils.getApp(), "请确认密码");
            return false;
        }
        if (!TextUtils.equals(pwd1, pwd2)) {
            ToastUtil.showShort(Utils.getApp(), "两次密码输入不一致");
            return false;
        }
        return true;

    }

    @Override
    public void countdown() {
        // 显示倒计时
        Observable.interval(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .take(60)
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        LogUtils.d("subscribe - onSubscribe ");
                        disposable = d;
                        try {
                            RxTextView.text(btGetCode).accept("剩余" + 60 + "秒");
                            RxView.enabled(btGetCode).accept(false);
                            RxTextView.color(btGetCode).accept(Color.parseColor("#999999"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onNext(Long aLong) {
                        LogUtils.d("subscribe - onNext " + aLong);
                        try {
                            RxTextView.text(btGetCode)
                                    .accept("剩余" + (60 - aLong - 1) + "秒");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        try {
                            RxTextView.text(btGetCode).accept("重新获取验证码");
                            RxView.enabled(btGetCode).accept(true);
                            RxTextView.color(btGetCode).accept(Color.parseColor("#eeeeee"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public void showPwd(boolean isShow) {
        if (isShow) {
            //如果选中，显示密码
            etPwd1.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            etPwd2.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            //设置光标移动到最后
            etPwd1.setSelection(etPwd1.getText().length());
            etPwd2.setSelection(etPwd2.getText().length());
        } else {
            //否则隐藏密码
            etPwd1.setTransformationMethod(PasswordTransformationMethod.getInstance());
            etPwd2.setTransformationMethod(PasswordTransformationMethod.getInstance());
            //设置光标移动到最后
            etPwd1.setSelection(etPwd1.getText().length());
            etPwd2.setSelection(etPwd2.getText().length());
        }
    }

    /**
     * 验证码没通过验证
     *
     * @param throwable
     */
    @Override
    public void verifyError(Throwable throwable) {
        try {
            throwable.printStackTrace();
            JSONObject object = new JSONObject(throwable.getMessage());
            String des = object.optString("detail");//错误描述
            int status = object.optInt("status");//错误代码
            LogUtils.d("des is " + des);
            LogUtils.d("status is " + status);
            if (status > 0 && !TextUtils.isEmpty(des)) {
                ToastUtil.showShort(this, des);
            }
        } catch (Exception e) {
            //do something
            ToastUtil.showShort(this, "短信验证服务器异常");
        }
    }

    @Override
    public void navigateToLogin() {
        finish();
    }

    @Override
    public void navigateToMain() {
        MainActivity.start(this);
    }

    @Override
    public void setupAlias() {
        startService(new Intent(this, AliasService.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 解除订阅
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }
}
