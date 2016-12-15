package com.yimiao100.sale.activity;

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

import com.alibaba.fastjson.JSON;
import com.yimiao100.sale.R;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.bean.ErrorBean;
import com.yimiao100.sale.bean.SignUpBean;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.SharePreferenceUtil;
import com.yimiao100.sale.utils.ToastUtil;
import com.yimiao100.sale.utils.Util;
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

    /** 是否显示密码*/
    private boolean pwdIsShow = false;

    private final String LOGIN_URL = "/api/user/login";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        //设置复选框监听
        mShowPassword.setOnCheckedChangeListener(this);
    }


    @OnClick({R.id.show_password_parent, R.id.login_forget_password, R.id.login_register, R.id.login_login})
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

    private void ShowPwd() {
        if (pwdIsShow){
            pwdIsShow = false;
            mShowPassword.setChecked(true);
            mLoginPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        }else {
            pwdIsShow = true;
            mShowPassword.setChecked(false);
            mLoginPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }

    private void Login() {
        String username = mLoginPhone.getText().toString().trim();
        String password = mLoginPassword.getText().toString().trim();
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)){
            ToastUtil.showLong(this, "账号密码不能为空");
            return;
        }
        //向提交账号密码，根据返回结果判断是否允许进入主界面
        String url = Constant.BASE_URL + LOGIN_URL;
        OkHttpUtils
                .post()
                .url(url)
                .addParams("accountNumber", username)
                .addParams("password", password)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtil.d("用户登录E：" + e.getLocalizedMessage());
                        Util.showTimeOutNotice(currentContext);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        //获得返回的json字符串
                        LogUtil.d("用户登录：" + response);
                        //解析json字符串
                        SignUpBean signUpBean = JSON.parseObject(response, SignUpBean.class);
                        //判断成功还是失败
                        switch (signUpBean.getStatus()){
                            case "success":
                                //成功，将数据保存到本地，跳转到主界面
                                //保存登录状态
                                SharePreferenceUtil.put(getApplicationContext(), Constant.LOGIN_STATUS, true);
                                //保存Token
                                SharePreferenceUtil.put(LoginActivity.this, Constant.ACCESSTOKEN, signUpBean.getTokenInfo().getAccessToken());
                                //保存用户id
                                SharePreferenceUtil.put(LoginActivity.this, Constant.USERID, signUpBean.getUserInfo().getId());

                                //保存或移除本地用户姓名
                                if (signUpBean.getUserInfo().getCnName() != null) {
                                    SharePreferenceUtil.put(getApplicationContext(), Constant.CNNAME, signUpBean.getUserInfo().getCnName());
                                } else {
                                    SharePreferenceUtil.remove(getApplicationContext(), Constant.CNNAME);
                                }
                                //保存或移除本地用户电话
                                if (signUpBean.getUserInfo().getPhoneNumber() != null) {
                                    SharePreferenceUtil.put(getApplicationContext(), Constant.PHONENUMBER, signUpBean.getUserInfo().getPhoneNumber());
                                } else {
                                    SharePreferenceUtil.remove(getApplicationContext(), Constant.PHONENUMBER);
                                }
                                //保存或移除本地用户邮箱
                                if (signUpBean.getUserInfo().getEmail() != null) {
                                    SharePreferenceUtil.put(getApplicationContext(), Constant.EMAIL, signUpBean.getUserInfo().getEmail());
                                } else {
                                    SharePreferenceUtil.remove(getApplicationContext(), Constant.EMAIL);
                                }
                                //保存或移除本地用户地域信息
                                if (signUpBean.getUserInfo().getProvinceName() != null && signUpBean.getUserInfo().getCityName() != null
                                        && signUpBean.getUserInfo().getAreaName() != null) {
                                    SharePreferenceUtil.put(getApplicationContext(), Constant.REGION, signUpBean.getUserInfo().getProvinceName()
                                            + "\t" + signUpBean.getUserInfo().getCityName()
                                            + "\t" + signUpBean.getUserInfo().getAreaName());
                                } else {
                                    SharePreferenceUtil.remove(getApplicationContext(), Constant.REGION);
                                }
                                //保存或移除本地用户身份证号
                                if (signUpBean.getUserInfo().getIdNumber() != null) {
                                    SharePreferenceUtil.put(getApplicationContext(), Constant.IDNUMBER, signUpBean.getUserInfo().getIdNumber());
                                } else {
                                    SharePreferenceUtil.remove(getApplicationContext(), Constant.IDNUMBER);
                                }
                                //保存或移除本地用户头像地址
                                if (signUpBean.getUserInfo().getProfileImageUrl() != null) {
                                    SharePreferenceUtil.put(getApplicationContext(), Constant.PROFILEIMAGEURL, signUpBean.getUserInfo().getProfileImageUrl());
                                } else {
                                    SharePreferenceUtil.remove(getApplicationContext(), Constant.PROFILEIMAGEURL);
                                }
                                //登录成功，进入主界面
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
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
     * 设置显示或者隐藏密码
     * @param buttonView
     * @param isChecked
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked){
            //如果选中，显示密码
            mLoginPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            //设置光标移动到最后
            mLoginPassword.setSelection(mLoginPassword.getText().length());
        }else{
            //否则隐藏密码
            mLoginPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            //设置光标移动到最后
            mLoginPassword.setSelection(mLoginPassword.getText().length());
        }

    }
}
