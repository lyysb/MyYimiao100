package com.yimiao100.sale.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yimiao100.sale.R;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.bean.ErrorBean;
import com.yimiao100.sale.bean.SignUpBean;
import com.yimiao100.sale.bean.TokenInfoBean;
import com.yimiao100.sale.bean.UserInfoBean;
import com.yimiao100.sale.ext.JSON;
import com.yimiao100.sale.service.AliasService;
import com.yimiao100.sale.utils.*;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 登录界面
 */
public class LoginActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {


    @BindView(R.id.login_phone)
    EditText mLoginPhone;
    @BindView(R.id.login_password)
    EditText mLoginPassword;
    @BindView(R.id.show_password)
    CheckBox mShowPassword;
    @BindView(R.id.show_password_parent)
    RelativeLayout mShowPasswordParent;
    @BindView(R.id.login_forget_password)
    TextView mLoginForgetPassword;
    @BindView(R.id.login_register)
    Button mLoginRegister;
    @BindView(R.id.login_login)
    Button mLoginLogin;

    /**
     * 是否显示密码
     */
    private boolean pwdIsShow = false;

    private final String URL_LOGIN = Constant.BASE_URL + "/api/user/login";
    private String mAccountNumber;
    private ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        //设置复选框监听
        mShowPassword.setOnCheckedChangeListener(this);

    }

    @OnClick({R.id.show_password_parent, R.id.login_forget_password, R.id.login_register, R.id
            .login_login})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.show_password_parent:
                //切换复选框（眼睛）的选中状态
                ShowPwd();
                break;
            case R.id.login_forget_password:
                //进入忘记密码界面
                startActivity(new Intent(this, ForgetPwdActivity.class));
                break;
            case R.id.login_register:
                //进入注册界面
                startActivity(new Intent(this, RegisterActivity.class));
                break;
            case R.id.login_login:
                //登录
                Login();
                break;
        }
    }

    /**
     * 却换密码的显示状态
     */
    private void ShowPwd() {
        if (pwdIsShow) {
            pwdIsShow = false;
            mShowPassword.setChecked(true);
            mLoginPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else {
            pwdIsShow = true;
            mShowPassword.setChecked(false);
            mLoginPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }

    /**
     * 登录
     */
    private void Login() {
        mAccountNumber = mLoginPhone.getText().toString().trim();
        String password = mLoginPassword.getText().toString().trim();
        if (TextUtils.isEmpty(mAccountNumber) || TextUtils.isEmpty(password)) {
            ToastUtil.showShort(this, "账号密码不能为空");
            return;
        }
        mProgressDialog = ProgressDialogUtil.getLoadingProgress(this, "登录中");
        mProgressDialog.show();
        //向提交账号密码，根据返回结果判断是否允许进入主界面
        OkHttpUtils.post().url(URL_LOGIN)
                .addParams("accountNumber", mAccountNumber)
                .addParams("password", password)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                mProgressDialog.dismiss();
                LogUtil.d("用户登录E：" + e.getLocalizedMessage());
                Util.showTimeOutNotice(currentContext);
            }

            @Override
            public void onResponse(String response, int id) {
                mProgressDialog.dismiss();
                LogUtil.d("用户登录onResponse：" + response);
                SignUpBean signUpBean = JSON.parseObject(response, SignUpBean.class);
                //判断成功还是失败
                switch (signUpBean.getStatus()) {
                    case "success":
                        // step1: 清空所有数据
                        SharePreferenceUtil.clear(currentContext);
                        // step2：保存当前用户数据
                        saveUserData(signUpBean);
                        // step3：启动别名服务
                        startAliasService();
                        // step4：进入主界面
                        enterHome();
                        break;
                    case "failure":
                        //失败,显示错误原因
                        ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                        Util.showError(currentContext, errorBean.getReason());
                        break;
                }
            }
        });
    }

    /**
     * 保存当前用户数据
     * @param user
     */
    private void saveUserData(SignUpBean user) {
        // 保存登录状态
        SharePreferenceUtil.put(currentContext, Constant.LOGIN_STATUS, true);
        // 保存Token信息
        TokenInfoBean tokenInfo = user.getTokenInfo();
        SharePreferenceUtil.put(currentContext, Constant.ACCESSTOKEN, tokenInfo.getAccessToken());
        // 保存用户信息
        UserInfoBean userInfo = user.getUserInfo();
        // 保存Bugly账户信息
        BuglyUtils.putUserData(this, userInfo);
        //用户id
        SharePreferenceUtil.put(currentContext, Constant.USER_ID, userInfo.getId());
        //用户账号
        SharePreferenceUtil.put(currentContext, Constant.ACCOUNT_NUMBER, mAccountNumber);
        //用户姓名
        if (userInfo.getCnName() != null && !userInfo.getCnName().isEmpty()) {
            SharePreferenceUtil.put(currentContext, Constant.CNNAME, userInfo.getCnName());
        }
        //用户电话
        if (userInfo.getPhoneNumber() != null && !userInfo.getPhoneNumber().isEmpty()) {
            SharePreferenceUtil.put(currentContext, Constant.PHONENUMBER, userInfo.getPhoneNumber());
        }
        //用户邮箱
        if (userInfo.getEmail() != null && !userInfo.getEmail().isEmpty()) {
            SharePreferenceUtil.put(currentContext, Constant.EMAIL, userInfo.getEmail());
        }
        //用户地域信息
        if (userInfo.getProvinceName() != null && !userInfo.getProvinceName().isEmpty()
                && userInfo.getCityName() != null && !userInfo.getCityName().isEmpty()
                && userInfo.getAreaName() != null && !userInfo.getAreaName().isEmpty()) {
            SharePreferenceUtil.put(currentContext, Constant.REGION, userInfo.getProvinceName()
                            + "\t" + userInfo.getCityName()
                            + "\t" + userInfo.getAreaName());
        }
        //用户身份证号
        if (userInfo.getIdNumber() != null && !userInfo.getIdNumber().isEmpty()) {
            SharePreferenceUtil.put(currentContext, Constant.IDNUMBER, userInfo.getIdNumber());
        }
        //用户头像地址
        if (userInfo.getProfileImageUrl() != null && !userInfo.getProfileImageUrl().isEmpty()) {
            SharePreferenceUtil.put(currentContext, Constant.PROFILEIMAGEURL, userInfo.getProfileImageUrl());
        }
    }

    /**
     * 开启设置别名
     */
    private void startAliasService() {
        startService(new Intent(currentContext, AliasService.class));
        LogUtil.d("登录成功，启动服务，设置别名");
    }

    /**
     * 进入主界面
     */
    private void enterHome() {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

    /**
     * 设置显示或者隐藏密码
     *
     * @param buttonView
     * @param isChecked
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            //如果选中，显示密码
            mLoginPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            //设置光标移动到最后
            mLoginPassword.setSelection(mLoginPassword.getText().length());
        } else {
            //否则隐藏密码
            mLoginPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            //设置光标移动到最后
            mLoginPassword.setSelection(mLoginPassword.getText().length());
        }

    }
}
