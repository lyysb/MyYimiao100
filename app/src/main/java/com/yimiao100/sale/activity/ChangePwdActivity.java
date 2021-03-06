package com.yimiao100.sale.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import com.yimiao100.sale.R;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.bean.ErrorBean;
import com.yimiao100.sale.ext.JSON;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.ToastUtil;
import com.yimiao100.sale.utils.Util;
import com.yimiao100.sale.view.TitleView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 个人设置-修改密码
 * TODO 判断密码是否符合规则
 */
public class ChangePwdActivity extends BaseActivity implements TextWatcher, TitleView
        .TitleBarOnClickListener {

    @BindView(R.id.change_back)
    TitleView mChangeBack;              //标题栏返回上一层
    @BindView(R.id.change_now_pwd)
    EditText mChangeNowPwd;             //原密码
    @BindView(R.id.change_pwd_new)
    EditText mChangePwdNew;             //新密码
    @BindView(R.id.change_pwd_confirm)
    EditText mChangePwdConfirm;         //确认密码
    @BindView(R.id.change_submit)
    Button mChangeSubmit;               //提交

    private final String URL_UPDATE_PASSWORD = Constant.BASE_URL + "/api/user/update_password";

    private boolean isSame = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pwd);
        ButterKnife.bind(this);

        //对三个输入框进行监听
        mChangeNowPwd.addTextChangedListener(this);
        mChangePwdNew.addTextChangedListener(this);
        mChangePwdConfirm.addTextChangedListener(this);


        mChangeBack.setOnTitleBarClick(this);
    }

    @OnClick(R.id.change_submit)
    public void onClick() {
        if (mChangeNowPwd.getText().toString().trim().isEmpty()) {
            ToastUtil.showShort(this, "请输入原密码");
            return;
        }
        if (mChangePwdNew.getText().toString().trim().isEmpty()) {
            ToastUtil.showShort(this, "请输入新密码");
            return;
        }
        if (mChangePwdConfirm.getText().toString().trim().isEmpty()) {
            ToastUtil.showShort(this, "请再次输入新密码");
            return;
        }
        mChangeSubmit.setEnabled(false);
        OkHttpUtils.post().url(URL_UPDATE_PASSWORD)
                .addHeader(ACCESS_TOKEN, accessToken)
                .addParams("oldPass", mChangeNowPwd.getText().toString().trim())
                .addParams("newPass", mChangePwdNew.getText().toString().trim())
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                mChangeSubmit.setEnabled(true);
                LogUtil.d("修改密码：" + e.getMessage());
                Util.showTimeOutNotice(currentContext);
            }

            @Override
            public void onResponse(String response, int id) {
                mChangeSubmit.setEnabled(true);
                LogUtil.d("提交修改密码：" + response);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        //成功将新密码提交到服务器，进入修改成功界面
                        startActivity(new Intent(getApplicationContext(), ChangeFinishedActivity
                                .class));
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
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        //判断两个输入框内容是否一致，并且输入框非空
        if (TextUtils.isEmpty(mChangeNowPwd.getText().toString()) || TextUtils.isEmpty
                (mChangePwdNew.getText().toString()) || TextUtils.isEmpty(mChangePwdConfirm
                .getText().toString())) {
            isSame = false;
        } else {
            isSame = TextUtils.equals(mChangePwdNew.getText().toString(), mChangePwdConfirm
                    .getText().toString());
        }
        //设置按钮是否可点击，背景颜色的变化
        mChangeSubmit.setEnabled(isSame);
        mChangeSubmit.setBackgroundResource(isSame ? R.drawable.selector_button : R.drawable
                .shape_button_forbid);
        mChangeSubmit.setTextColor(isSame ? Color.WHITE : Color.GRAY);
    }

    @Override
    public void leftOnClick() {
        finish();
    }

    @Override
    public void rightOnClick() {

    }
}
