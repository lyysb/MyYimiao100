package com.yimiao100.sale.activity;

import android.app.ProgressDialog;
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

import com.yimiao100.sale.R;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.bean.SignUpBean;
import com.yimiao100.sale.ext.JSON;
import com.yimiao100.sale.service.AliasService;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.SharePreferenceUtil;
import com.yimiao100.sale.utils.ToastUtil;
import com.yimiao100.sale.utils.Util;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import okhttp3.Call;

public class RegisterActivity extends BaseActivity implements CompoundButton
        .OnCheckedChangeListener {

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

    private final String URL_SIGN_UP = Constant.BASE_URL + "/api/user/signup";

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    String detail = msg.getData().getString("detail");
                    ToastUtil.showLong(RegisterActivity.this, detail);
                    mRegisterRegister.setEnabled(true);
                    break;
                case 2:
                    ToastUtil.showShort(getApplicationContext(), "亲可以换个手机号试试");
                    mRegisterRegister.setEnabled(true);
                    break;
            }
        }
    };
    private ProgressDialog mProgressDialog;

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
                    LogUtil.d("短信验证回调完成");
                    //回调完成
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        LogUtil.d("验证码验证通过");
                        //提交验证码成功
                        //发送数据到服务器
                        sendMsg();
                    }
                } else if (result == SMSSDK.RESULT_ERROR) {
                    LogUtil.d("短信验证回调出错");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mRegisterRegister.setEnabled(true);
                        }
                    });
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
        //验证码验证通过, 将注册信息提交到服务器
        OkHttpUtils.post().url(URL_SIGN_UP)
                .addParams("accountNumber", mRegisterPhone.getText().toString().trim())
                .addParams("password", mRegisterPassword.getText().toString().trim())
                .build().execute(new StringCallback() {

            @Override
            public void onError(Call call, Exception e, int id) {
                e.printStackTrace();
                Util.showTimeOutNotice(currentContext);
                LogUtil.d("网络请求失败");
                mRegisterRegister.setEnabled(true);
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.d("服务器返回--" + response);
                mRegisterRegister.setEnabled(true);
                SignUpBean signUpBean = JSON.parseObject(response, SignUpBean.class);
                LogUtil.d(signUpBean.getStatus());
                switch (signUpBean.getStatus()) {
                    case "success":
                        //注册成功前先清除之前本地数据
                        SharePreferenceUtil.clear(currentContext);
                        //保存Token
                        SharePreferenceUtil.put(currentContext, Constant.ACCESSTOKEN, signUpBean.getTokenInfo().getAccessToken());
                        // 设置别名需要用户id
                        SharePreferenceUtil.put(currentContext, Constant.USERID, signUpBean.getUserInfo().getId());
                        // 保存登录状态
                        SharePreferenceUtil.put(currentContext, Constant.LOGIN_STATUS, true);
                        //启动服务，设置别名
                        startService(new Intent(currentContext, AliasService.class));
                        //注册成功，进入主界面
                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                        ToastUtil.showShort(currentContext, "注册成功");
                        finish();
                        break;
                    case "failure":
                        LogUtil.d("服务器验证失败");
                        //注册失败，显示失败原因
                        Util.showError(currentContext, signUpBean.getReason());
                        break;
                }
            }
        });
    }

    @OnClick({R.id.show_password_parent, R.id.register_register, R.id.return_login, R.id
            .register_getcode})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.show_password_parent:
                //切换复选框（眼睛）的选中状态
                showPwd();
                break;
            case R.id.register_register:
                //提交注册信息
                register();
                break;
            case R.id.return_login:
                //跳回到登录界面
                finish();
                break;
            case R.id.register_getcode:
                if (TextUtils.isEmpty(mRegisterPhone.getText().toString().trim())) {
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
    private void register() {
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
        LogUtil.d("提交短信验证码");
        mRegisterRegister.setEnabled(false);
        //提交短信验证码，触发短信操作回调，验证短信验证码是否正确，然后和服务器进行交互
        SMSSDK.submitVerificationCode("86", phone, code);

    }

    /**
     * 显示密码
     */
    private void showPwd() {
        if (pwdIsShow) {
            pwdIsShow = false;
            mShowPassword.setChecked(true);
            mRegisterPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance
                    ());
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
            mRegisterPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance
                    ());
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
