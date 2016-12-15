package com.yimiao100.sale.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
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
import com.yimiao100.sale.bean.SignUpBean;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.SharePreferenceUtil;
import com.yimiao100.sale.utils.ToastUtil;
import com.yimiao100.sale.utils.Util;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import okhttp3.Call;
import okhttp3.Response;

public class RegisterActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {

    @BindView(R.id.register_phone)
    EditText mRegisterPhone;
    @BindView(R.id.register_code)
    EditText mRegisterCode;
    @BindView(R.id.register_password)
    EditText mRegisterPassword;
    @BindView(R.id.show_password)
    CheckBox mShowPassword;
    @BindView(R.id.show_password_parent)
    RelativeLayout mShowPasswordParent;
    @BindView(R.id.register_password_re)
    EditText mRegisterPasswordRe;
    @BindView(R.id.register_register)
    Button mRegisterRegister;
    @BindView(R.id.return_login)
    TextView mReturnLogin;
    @BindView(R.id.register_getcode)
    Button mRegisterGetcode;

    /**
     * 是否显示密码
     */
    private boolean pwdIsShow = false;
    private TimeCount time;

    private final String SIGN_UP_URL = "/api/user/signup";

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    String detail = msg.getData().getString("detail");
                    ToastUtil.showLong(RegisterActivity.this, detail);
                    break;
                case 2:
                    Util.showTimeOutNotice(currentContext);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        //监听复选框状态，设置密码显示或隐藏
        mShowPassword.setOnCheckedChangeListener(this);
        //按钮倒计时
        time = new TimeCount(60000, 1000);

        //短信验证
        SMSCodeCheck();
    }

    private void SMSCodeCheck() {
        //初始化SMSSDK
        SMSSDK.initSDK(this, Constant.MOB_APP_KEY, Constant.MOB_APP_SECRET);

        EventHandler eventHandler = new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {
                super.afterEvent(event, result, data);
                if (result == SMSSDK.RESULT_COMPLETE) {
                    //回调完成
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        //提交验证码成功
                        //发送数据到服务器
                        sendMsg();
                    }
                } else if (result == SMSSDK.RESULT_ERROR) {
                    //错误情况
                    Throwable throwable = (Throwable) data;
                    throwable.printStackTrace();
                    JSONObject object;
                    try {
                        object = new JSONObject(throwable.getMessage());
                        String detail = object.getString("detail");
                        //将错误信息发送到UI线程
                        Message message = new Message();
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        bundle.putString("detail", detail);
                        message.setData(bundle);
                        mHandler.sendMessage(message);

                        LogUtil.d(detail);
                        //打印错误码
                        LogUtil.d(object.getString("status"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        //发送消息，甩锅
                        mHandler.sendEmptyMessage(2);
                    }
                }
            }
        };
        //注册回调接口
        SMSSDK.registerEventHandler(eventHandler);
    }

    /**
     * 发送注册信息到服务器
     */
    private void sendMsg() {
        //提交验证码成功, 将注册信息提交到服务器
        String url = Constant.BASE_URL + SIGN_UP_URL;
        OkHttpUtils
                .post()
                .url(url)
                .addParams("accountNumber", mRegisterPhone.getText().toString().trim())
                .addParams("password", mRegisterPassword.getText().toString().trim())
                .build()
                .execute(new Callback() {
                    @Override
                    public Object parseNetworkResponse(Response response, int id) throws Exception {
                        //针对返回的Json数据进行处理
                        String json = response.body().string();
                        LogUtil.d(json);
                        SignUpBean signUpBean = JSON.parseObject(json, SignUpBean.class);
                        LogUtil.d(signUpBean.getStatus());
                        switch (signUpBean.getStatus()){
                            case "success":
                                //保存Token
                                SharePreferenceUtil.put(RegisterActivity.this, Constant.ACCESSTOKEN, signUpBean.getTokenInfo().getAccessToken());
                                //注册成功，进入主界面
                                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                finish();
                                break;
                            case "failure":
                                //注册失败，显示失败原因
                                Util.showError(currentContext, signUpBean.getReason());
                                break;
                        }
                        return null;
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {

                        Util.showTimeOutNotice(currentContext);
                    }

                    @Override
                    public void onResponse(Object response, int id) {

                    }
                });
    }

    @OnClick({R.id.show_password_parent, R.id.register_register, R.id.return_login, R.id.register_getcode})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.show_password_parent:
                //切换复选框（眼睛）的选中状态
                ShowPwd();
                break;
            case R.id.register_register:
                //提交注册信息
                Register();
                break;
            case R.id.return_login:
                //跳回到登录界面
                finish();
                break;
            case R.id.register_getcode:
                if (TextUtils.isEmpty(mRegisterPhone.getText().toString().trim())){
                    ToastUtil.showLong(this, "请输入手机号");
                    return;
                }
                //如果手机号长度不是11位，则返回
                if (mRegisterPhone.getText().toString().trim().length() != 11) {
                    ToastUtil.showLong(getApplicationContext(), "请输入合法的手机号");
                    return;
                }
                //请求获取短信验证码，进入接口回调
                SMSSDK.getVerificationCode("86", mRegisterPhone.getText().toString().trim());
                //开始验证码点击按钮倒计时
                time.start();
                break;
        }
    }

    /**
     * 提交注册信息
     */
    private void Register() {
        String phone = mRegisterPhone.getText().toString().trim();
        String code = mRegisterCode.getText().toString().trim();
        String password = mRegisterPassword.getText().toString().trim();
        String password_re = mRegisterPasswordRe.getText().toString().trim();
        // 判空
        if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(code)
                || TextUtils.isEmpty(password) || TextUtils.isEmpty(password_re)) {
            ToastUtil.showShort(this, "所有信息不能为空");
            return;
        }
        // 校验密码一致性
        if (!TextUtils.equals(password, password_re)) {
            ToastUtil.showShort(this, "两次密码必须一致");
            return;
        }
        if (password.length() < 6) {
            ToastUtil.showShort(this, "密码长度不得少于6位");
            return;
        }
        //提交短信验证码，触发短信操作回调，验证短信验证码是否正确，然后和服务器进行交互
        SMSSDK.submitVerificationCode("86", phone, code);

    }

    /**
     * 显示密码
     */
    private void ShowPwd() {
        if (pwdIsShow) {
            pwdIsShow = false;
            mShowPassword.setChecked(true);
            mRegisterPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else {
            pwdIsShow = true;
            mShowPassword.setChecked(false);
            mRegisterPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }

    /**
     * 修改密码输入框的显示和隐藏
     *
     * @param buttonView
     * @param isChecked
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            //如果选中，显示密码
            mRegisterPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            //设置光标移动到最后
            mRegisterPassword.setSelection(mRegisterPassword.getText().length());
        } else {
            //否则隐藏密码
            mRegisterPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            //设置光标移动到最后
            mRegisterPassword.setSelection(mRegisterPassword.getText().length());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //注销回调接口
        SMSSDK.unregisterAllEventHandler();
    }

    class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            mRegisterGetcode.setBackgroundResource(R.drawable.shape_getcode_loading);
            mRegisterGetcode.setText(millisUntilFinished / 1000 + " 秒后可重新发送");
            //button的setClickable无效
            mRegisterGetcode.setEnabled(false);
            mRegisterGetcode.setTextColor(Color.parseColor("#999999"));
        }

        @Override
        public void onFinish() {
            mRegisterGetcode.setText("重新获取验证码");
            mRegisterGetcode.setTextColor(Color.parseColor("#eeeeee"));
            mRegisterGetcode.setEnabled(true);
            mRegisterGetcode.setBackgroundResource(R.drawable.shape_getcode);
        }
    }
}
