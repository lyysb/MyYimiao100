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
 * 个人设置-电话号码
 */
public class PersonalPhoneActivity extends BaseActivity implements TitleView.TitleBarOnClickListener {

    @BindView(R.id.phone_title)
    TitleView mPhoneTitle;
    @BindView(R.id.personal_phone)
    EditText mPersonalPhone;
    @BindView(R.id.phone_clear)
    ImageView mPhoneClear;

    private final String UPDATE_PHONE_NUMBER = "/api/user/update_phone_number";
    private String mAccessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_phpone);
        ButterKnife.bind(this);
        mPhoneTitle.setOnTitleBarClick(this);

        mAccessToken = (String) SharePreferenceUtil.get(this, Constant.ACCESSTOKEN, "");

        //获取用户电话
        String user_phone_number = (String) SharePreferenceUtil.get(this, Constant.PHONENUMBER, "请输入用户电话号码");
        mPersonalPhone.setHint(user_phone_number);
    }

    @OnClick(R.id.phone_clear)
    public void onClick() {
        mPersonalPhone.setText("");
    }

    @Override
    public void leftOnClick() {
        finish();
    }

    @Override
    public void rightOnClick() {
        //校验手机号
        if (mPersonalPhone.getText().toString().trim().length() != 11) {
            ToastUtil.showLong(getApplicationContext(), "请输入合法的手机号");
            return;
        }
        //保存将数据发送到服务器
        String user_phone_url = Constant.BASE_URL + UPDATE_PHONE_NUMBER;
        OkHttpUtils
                .post()
                .url(user_phone_url)
                .addHeader("X-Authorization-Token", mAccessToken)
                .addParams("phoneNumber", mPersonalPhone.getText().toString().trim())
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtil.d("电话设置： " + e.getMessage());
                        Util.showTimeOutNotice(currentContext);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                        switch (errorBean.getStatus()){
                            case "success":
                                //更新本地数据
                                SharePreferenceUtil.put(getApplicationContext(), Constant.PHONENUMBER, mPersonalPhone.getText().toString().trim());
                                //返回到上一层
                                Intent intent = new Intent();
                                intent.putExtra("phone", mPersonalPhone.getText().toString().trim());
                                setResult(2, intent);
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
