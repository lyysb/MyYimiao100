package com.yimiao100.sale.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.yimiao100.sale.R;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.bean.ErrorBean;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.Regex;
import com.yimiao100.sale.utils.SharePreferenceUtil;
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
 * 个人设置-邮箱设置
 */
public class PersonalEmailActivity extends BaseActivity implements TitleView
        .TitleBarOnClickListener {

    @BindView(R.id.email_title)
    TitleView mEmailTitle;
    @BindView(R.id.personal_email)
    EditText mPersonalEmail;
    @BindView(R.id.email_clear)
    ImageView mEmailClear;
    private final String UPDATE_EMAIL = Constant.BASE_URL + "/api/user/update_email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_email);
        ButterKnife.bind(this);
        mEmailTitle.setOnTitleBarClick(this);

        //获取用户邮箱
        String user_email = (String) SharePreferenceUtil.get(this, Constant.EMAIL, "请输入用户邮箱地址");
        mPersonalEmail.setHint(user_email);
    }

    @OnClick(R.id.email_clear)
    public void onClick() {
        mPersonalEmail.setText("");
    }

    @Override
    public void leftOnClick() {
        finish();
    }

    @Override
    public void rightOnClick() {
        //判断邮箱合法性
        if (!mPersonalEmail.getText().toString().trim().matches(Regex.email)) {
            ToastUtil.showShort(this, "邮箱地址格式不正确");
            return;
        }
        OkHttpUtils.post().url(UPDATE_EMAIL)
                .addHeader(ACCESS_TOKEN, mAccessToken)
                .addParams("email", mPersonalEmail.getText().toString().trim())
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.Companion.d("邮箱设置： " + e.getMessage());
                Util.showTimeOutNotice(currentContext);
            }

            @Override
            public void onResponse(String response, int id) {
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        //更新本地数据
                        SharePreferenceUtil.put(getApplicationContext(), Constant.EMAIL,
                                mPersonalEmail.getText().toString().trim());
                        //返回到上一层
                        Intent intent = new Intent();
                        intent.putExtra("email", mPersonalEmail.getText().toString().trim());
                        setResult(3, intent);
                        finish();
                        break;
                    case "failure":
                        Util.showError(currentContext, errorBean.getReason());
                        break;
                }
            }
        });
    }
}
