package com.yimiao100.sale.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.yimiao100.sale.R;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.bean.ErrorBean;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.Util;
import com.yimiao100.sale.view.TitleView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 忘记密码-2
 * TODO 密码限制判断
 */
public class ForgetPwd2Activity extends BaseActivity implements TextWatcher, TitleView.TitleBarOnClickListener {

    @BindView(R.id.forget_pwd_back)
    TitleView mForgetPwdBack;           //标题栏，返回键
    @BindView(R.id.forget_pwd_new)
    EditText mForgetPwdNew;             //输入新密码
    @BindView(R.id.forget_pwd_confirm)
    EditText mForgetPwdConfirm;         //确认密码
    @BindView(R.id.forget_submit)
    Button mForgetSubmit;               //提交按钮

    private boolean isSame = false;
    private String mAccountNumber;

    private String RESET_PASSWORD = "/api/user/reset_password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pwd2);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mAccountNumber = intent.getStringExtra("accountNumber");


        mForgetPwdBack.setOnTitleBarClick(this);
        mForgetPwdNew.addTextChangedListener(this);
        mForgetPwdConfirm.addTextChangedListener(this);

    }

    @OnClick(R.id.forget_submit)
    public void onClick() {
        //将新密码提交到服务器，进入修改成功界面
        String url = Constant.BASE_URL + RESET_PASSWORD;
        OkHttpUtils
                .post()
                .url(url)
                .addParams("accountNumber", mAccountNumber)
                .addParams("password", mForgetPwdNew.getText().toString().trim())
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                        switch (errorBean.getStatus()) {
                            case "success":
                                //进入完成界面
                                startActivity(new Intent(ForgetPwd2Activity.this, ChangeFinishedActivity.class));
                                finish();
                                break;
                            case "failure":
                                //将错误信息进行显示
                                Util.showError(ForgetPwd2Activity.this, errorBean.getReason());
                                break;
                        }
                    }
                });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        //判断两个输入框内容是否一致，并且输入框非空
        if (TextUtils.isEmpty(mForgetPwdNew.getText().toString()) ||TextUtils.isEmpty(mForgetPwdConfirm.getText().toString())){
            isSame = false;
        }else {
            isSame = TextUtils.equals(mForgetPwdNew.getText().toString(), mForgetPwdConfirm.getText().toString());
        }
        //TODO 密码限制判断
        mForgetSubmit.setClickable(isSame);
        mForgetSubmit.setBackgroundResource(isSame ? R.drawable.shape_getcode : R.drawable.shape_getcode_loading);
    }

    @Override
    public void leftOnClick() {
        finish();
    }

    @Override
    public void rightOnClick() {

    }
}
