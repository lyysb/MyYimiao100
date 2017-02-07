package com.yimiao100.sale.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.yimiao100.sale.R;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.bean.ErrorBean;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.LogUtil;
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

/**
 * 忘记密码-1
 */
public class ForgetPwdActivity extends BaseActivity {

    @BindView(R.id.forget_pwd_phone)
    EditText mFogetPwdPhone;
    @BindView(R.id.forget_pwd_code)
    EditText mForgetPwdCode;
    @BindView(R.id.forget_pwd_get_code)
    Button mForgetPwdGetCode;
    @BindView(R.id.forget_pwd_next)
    ImageView mForgetPwdNext;
    private TimeCount time;
    /**
     * 用户账号验证
     */
    private final String VALIDATE_ACCOUNT_NUMBER = Constant.BASE_URL +
            "/api/user/validate_account_number";

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    String detail = msg.getData().getString("detail");
                    ToastUtil.showShort(ForgetPwdActivity.this, detail);
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
        setContentView(R.layout.activity_forget_pwd);
        ButterKnife.bind(this);

        //按钮倒计时部分
        time = new TimeCount(60000, 1000);

        //初始化SMSSDK
        SMSSDK.initSDK(this, Constant.MOB_APP_KEY, Constant.MOB_APP_SECRET);

        EventHandler eventHandler = new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {
                super.afterEvent(event, result, data);
                if (result == SMSSDK.RESULT_COMPLETE) {
                    //回调完成
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        //验证通过，进入下一步界面
                        Intent intent = new Intent(ForgetPwdActivity.this, ForgetPwd2Activity
                                .class);
                        intent.putExtra("accountNumber", mFogetPwdPhone.getText().toString().trim
                                ());
                        startActivity(intent);
                        finish();
                    } else {
                        ToastUtil.showShort(currentContext, "未知错误");
                    }
                } else if (event == SMSSDK.RESULT_ERROR) {
                    Throwable throwable = (Throwable) data;
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

                        LogUtil.Companion.d(detail);
                        //打印错误码
                        LogUtil.Companion.d(object.getString("status"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        mHandler.sendEmptyMessage(2);
                    }
                }
            }
        };
        //注册回调接口
        SMSSDK.registerEventHandler(eventHandler);
    }

    @OnClick({R.id.forget_pwd_get_code, R.id.forget_pwd_next})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.forget_pwd_get_code:
                //要求手机号不能为空
                if (TextUtils.isEmpty(mFogetPwdPhone.getText().toString().trim())) {
                    ToastUtil.showLong(this, "请输入手机号");
                    return;
                }
                //如果手机号长度不是11位，则返回
                if (mFogetPwdPhone.getText().toString().trim().length() != 11) {
                    ToastUtil.showShort(getApplicationContext(), "请输入合法的手机号");
                    return;
                }
                //验证账号，并获取验证码
                getSMSCode();
                break;
            case R.id.forget_pwd_next:
                //判空，非空才能提交
                if (TextUtils.isEmpty(mFogetPwdPhone.getText().toString().trim())) {
                    ToastUtil.showShort(this, "请输入手机号");
                    return;
                }
                if (TextUtils.isEmpty(mForgetPwdCode.getText().toString().trim())) {
                    ToastUtil.showShort(this, "请输入验证码");
                }
                //提交短信验证码，触发短信操作回调，验证短信验证码是否正确，然后和服务器进行交互
                SMSSDK.submitVerificationCode("86", mFogetPwdPhone.getText().toString().trim(),
                        mForgetPwdCode.getText().toString().trim());
                break;
        }
    }

    private void getSMSCode() {
        mForgetPwdGetCode.setEnabled(false);
        //联网进行账号验证
        OkHttpUtils.post().url(VALIDATE_ACCOUNT_NUMBER)
                .addParams("accountNumber", mFogetPwdPhone.getText().toString().trim())
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                mForgetPwdGetCode.setEnabled(true);
                LogUtil.Companion.d("忘记密码：" + e.getMessage());
                Util.showTimeOutNotice(currentContext);
            }

            @Override
            public void onResponse(String response, int id) {
                mForgetPwdGetCode.setEnabled(true);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        //验证通过，请求获取短信验证码，进入接口回调
                        SMSSDK.getVerificationCode("86", mFogetPwdPhone.getText()
                                .toString().trim());
                        //按钮倒计时
                        time.start();
                        break;
                    case "failure":
                        Util.showError(currentContext, errorBean.getReason());
                        break;
                }
            }
        });
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
            mForgetPwdGetCode.setBackgroundResource(R.drawable.shape_getcode_loading);
            mForgetPwdGetCode.setText(millisUntilFinished / 1000 + " 秒后可重新发送");
            //button的setClickable无效
            mForgetPwdGetCode.setEnabled(false);
            mForgetPwdGetCode.setTextColor(Color.parseColor("#999999"));
        }

        @Override
        public void onFinish() {
            mForgetPwdGetCode.setText("重新获取验证码");
            mForgetPwdGetCode.setTextColor(Color.parseColor("#eeeeee"));
            mForgetPwdGetCode.setEnabled(true);
            mForgetPwdGetCode.setBackgroundResource(R.drawable.shape_getcode);
        }
    }
}
