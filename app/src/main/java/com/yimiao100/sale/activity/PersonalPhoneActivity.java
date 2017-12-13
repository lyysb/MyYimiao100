package com.yimiao100.sale.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;

import com.blankj.utilcode.util.SPUtils;
import com.yimiao100.sale.R;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.bean.ErrorBean;
import com.yimiao100.sale.ext.JSON;
import com.yimiao100.sale.utils.*;
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
public class PersonalPhoneActivity extends BaseActivity implements TitleView
        .TitleBarOnClickListener {

    @BindView(R.id.phone_title)
    TitleView mPhoneTitle;
    @BindView(R.id.personal_phone)
    EditText mPersonalPhone;
    @BindView(R.id.phone_clear)
    ImageView mPhoneClear;

    private final String URL_UPDATE_PHONE_NUMBER = Constant.BASE_URL +
            "/api/user/update_phone_number";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_phpone);
        ButterKnife.bind(this);
        mPhoneTitle.setOnTitleBarClick(this);


        //获取用户账号
        String accountNumber = SPUtils.getInstance().getString(Constant.ACCOUNT_NUMBER, getString(R.string.unknown));
        //获取用户电话
        String user_phone_number = SPUtils.getInstance().getString(Constant.PHONENUMBER, accountNumber);
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
        if (mPersonalPhone.getText().toString().trim().isEmpty()) {
            ToastUtil.showShort(this, "手机号不能为空");
            return;
        }
        //校验手机号
        if (mPersonalPhone.getText().toString().trim().length() != 11) {
            ToastUtil.showShort(this, "请输入合法的手机号");
            return;
        }
        //保存将数据发送到服务器
        OkHttpUtils.post().url(URL_UPDATE_PHONE_NUMBER)
                .addHeader(ACCESS_TOKEN, accessToken)
                .addParams("phoneNumber", mPersonalPhone.getText().toString().trim())
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.d("电话设置： " + e.getMessage());
                Util.showTimeOutNotice(currentContext);
            }

            @Override
            public void onResponse(String response, int id) {
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        // 添加Bugly数据
                        BuglyUtils.putUserPhone(currentContext, mPersonalPhone.getText().toString().trim());
                        //更新本地数据
                        SPUtils.getInstance().put(Constant.PHONENUMBER, mPersonalPhone.getText().toString().trim());
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
