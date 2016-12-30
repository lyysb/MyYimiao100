package com.yimiao100.sale.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.yimiao100.sale.R;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.bean.ErrorBean;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.FormatUtils;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.SharePreferenceUtil;
import com.yimiao100.sale.utils.ToastUtil;
import com.yimiao100.sale.utils.Util;
import com.yimiao100.sale.view.TitleView;
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
 * 推广主体-绑定个人银行卡
 * TODO 手机号有效性检验
 */
public class BindPersonalActivity extends BaseActivity implements TitleView.TitleBarOnClickListener {

    @BindView(R.id.bind_personal_name)
    EditText mBindPersonalName;
    @BindView(R.id.bind_personal_phone)
    EditText mBindPersonalPhone;
    @BindView(R.id.bind_personal_getCode)
    Button mBindPersonalGetCode;
    @BindView(R.id.bind_personal_code)
    EditText mBindPersonalCode;
    @BindView(R.id.bind_personal_bank_card)
    EditText mBindPersonalBankCard;
    @BindView(R.id.bind_personal_bank_name)
    EditText mBindPersonalBankName;
    @BindView(R.id.bind_personal_submit)
    ImageView mBindPersonalSubmit;
    @BindView(R.id.bind_personal_title)
    TitleView mBindPersonalTitle;
    private TimeCount time;

    private final String POST_PERSONAL_USER_ACCOUNT = "/api/user/post_personal_user_account";

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    String detail = msg.getData().getString("detail");
                    ToastUtil.showLong(getApplicationContext(), detail);
                    break;
            }
        }
    };
    private String mAccessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_personal);
        ButterKnife.bind(this);

        mAccessToken = (String) SharePreferenceUtil.get(this, Constant.ACCESSTOKEN, "");

        mBindPersonalTitle.setOnTitleBarClick(this);

        initData();

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
                        //验证通过，提交数据到服务器
                        submitPersonalAccount();
                    }
                } else {
                    Throwable throwable = (Throwable) data;
                    try {
                        JSONObject object = new JSONObject(throwable.getMessage());
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
                    }
                }
            }
        };
        //注册回调接口
        SMSSDK.registerEventHandler(eventHandler);
    }

    private void initData() {
        boolean isExit = (boolean) SharePreferenceUtil.get(getApplicationContext(),
                Constant.PERSONAL_EXIT, false);
        String account_name = (String) SharePreferenceUtil.get(getApplicationContext(),
                Constant.PERSONAL_CN_NAME, "");
        String phone_number = (String) SharePreferenceUtil.get(getApplicationContext(),
                Constant.PERSONAL_PHONE_NUMBER, "");
        String bank_number = (String) SharePreferenceUtil.get(getApplicationContext(),
                Constant.PERSONAL_BANK_ACCOUNT_NUMBER, "");
        String bank_name = (String) SharePreferenceUtil.get(getApplicationContext(),
                Constant.PERSONAL_BANK_NAME, "");
        if (isExit) {
            //如果存在个人账户信息
            mBindPersonalName.setHint(account_name);
            mBindPersonalPhone.setHint(FormatUtils.PhoneNumberFormat(phone_number));
            mBindPersonalBankCard.setHint(bank_number);
            mBindPersonalBankName.setHint(bank_name);

        }
    }

    @OnClick({R.id.bind_personal_getCode, R.id.bind_personal_submit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bind_personal_getCode:
                //TODO 手机号有效性检验
                //手机号不为空的时候允许发送验证码
                if (mBindPersonalPhone.getText().toString().trim().isEmpty()) {
                    ToastUtil.showLong(getApplicationContext(), "手机号不允许为空");
                    return;
                }
                //如果手机号长度不是11位，则返回
                if (mBindPersonalPhone.getText().toString().trim().length() != 11) {
                    ToastUtil.showLong(getApplicationContext(), "请输入合法的手机号");
                    return;
                }
                //提交手机号，获得验证码
                SMSSDK.getVerificationCode("86", mBindPersonalPhone.getText().toString().trim());
                //倒计时，按钮禁止点击
                time.start();
                break;
            case R.id.bind_personal_submit:
                if (mBindPersonalName.getText().toString().trim().isEmpty() ||
                        mBindPersonalPhone.getText().toString().trim().isEmpty() ||
                        mBindPersonalCode.getText().toString().trim().isEmpty() ||
                        mBindPersonalBankCard.getText().toString().trim().isEmpty() ||
                        mBindPersonalBankName.getText().toString().trim().isEmpty()) {
                    //提示非空
                    ToastUtil.showLong(getApplicationContext(), "请填写完整信息");
                    return;
                }
                //提交短信验证码，触发短信操作回调，验证短信验证码是否正确，然后和服务器进行交互
                SMSSDK.submitVerificationCode("86", mBindPersonalPhone.getText().toString().trim(), mBindPersonalCode.getText().toString().trim());
                break;
        }
    }

    private void submitPersonalAccount() {
        String personal_url = Constant.BASE_URL + POST_PERSONAL_USER_ACCOUNT;
        OkHttpUtils
                .post()
                .url(personal_url)
                .addHeader("X-Authorization-Token", mAccessToken)
                .addParams("cnName", mBindPersonalName.getText().toString().trim())
                .addParams("phoneNumber", mBindPersonalPhone.getText().toString().trim())
                .addParams("bankName", mBindPersonalBankName.getText().toString().trim())
                .addParams("bankAccountNumber", mBindPersonalBankCard.getText().toString().trim())
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtil.Companion.d("绑定银行卡E：" + e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        LogUtil.Companion.d("绑定银行卡：" + response);
                        ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                        switch (errorBean.getStatus()) {
                            case "success":
                                //记录个人账户
                                //个人账户存在
                                SharePreferenceUtil.put(getApplicationContext(),
                                        Constant.PERSONAL_EXIT, true);
                                //推广人
                                SharePreferenceUtil.put(getApplicationContext(),
                                        Constant.PERSONAL_CN_NAME,
                                        mBindPersonalName.getText().toString().trim());
                                //手机号码
                                SharePreferenceUtil.put(getApplicationContext(),
                                        Constant.PERSONAL_PHONE_NUMBER,
                                        mBindPersonalPhone.getText().toString().trim());
                                //银行卡号
                                SharePreferenceUtil.put(getApplicationContext(),
                                        Constant.PERSONAL_BANK_ACCOUNT_NUMBER,
                                        mBindPersonalBankCard.getText().toString().trim());
                                //开户银行
                                SharePreferenceUtil.put(getApplicationContext(),
                                        Constant.PERSONAL_BANK_NAME,
                                        mBindPersonalBankName.getText().toString().trim());
                                //成功将个人账户信息提交到服务器，进入修改成功界面
                                startActivity(new Intent(getApplicationContext(), ChangeFinishedActivity.class));
                                finish();
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

    @Override
    public void leftOnClick() {
        finish();
    }

    @Override
    public void rightOnClick() {

    }

    class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            mBindPersonalGetCode.setBackgroundResource(R.drawable.shape_getcode_loading);
            mBindPersonalGetCode.setText(millisUntilFinished / 1000 + " 秒后可重新发送");
            //button的setClickable无效
            mBindPersonalGetCode.setEnabled(false);
            mBindPersonalGetCode.setTextColor(Color.parseColor("#999999"));
        }

        @Override
        public void onFinish() {
            mBindPersonalGetCode.setText("重新获取验证码");
            mBindPersonalGetCode.setTextColor(Color.parseColor("#eeeeee"));
            mBindPersonalGetCode.setEnabled(true);
            mBindPersonalGetCode.setBackgroundResource(R.drawable.shape_getcode);
        }
    }
}
