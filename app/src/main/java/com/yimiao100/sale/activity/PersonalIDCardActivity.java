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
 * 个人设置-身份证号码
 */
public class PersonalIDCardActivity extends BaseActivity implements TitleView
        .TitleBarOnClickListener {

    @BindView(R.id.IDCard_title)
    TitleView mIDCardTitle;
    @BindView(R.id.IDCard_idCard)
    EditText mIDCardIdCard;
    @BindView(R.id.IDCard_clear)
    ImageView mIDCardClear;

    private final String UPDATE_ID_NUMBER = Constant.BASE_URL + "/api/user/update_id_number";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_idcard);
        ButterKnife.bind(this);
        mIDCardTitle.setOnTitleBarClick(this);

        //获取用户身份证号
        String user_id_number = SPUtils.getInstance().getString(Constant.IDNUMBER, "请输入用户身份证号码");
        mIDCardIdCard.setHint(user_id_number);
    }

    @OnClick(R.id.IDCard_clear)
    public void onClick() {
        mIDCardIdCard.setText("");
    }

    @Override
    public void leftOnClick() {
        finish();
    }

    @Override
    public void rightOnClick() {
        if (mIDCardIdCard.getText().toString().trim().isEmpty()) {
            ToastUtil.showShort(this, "身份证号不能为空");
            return;
        }
        //校验身份证合法性
        if (!mIDCardIdCard.getText().toString().trim().matches(Regex.idCard)) {
            ToastUtil.showShort(this, getString(R.string.regex_id_card));
            return;
        }
        OkHttpUtils.post().url(UPDATE_ID_NUMBER)
                .addHeader(ACCESS_TOKEN, accessToken)
                .addParams("idNumber", mIDCardIdCard.getText().toString().trim())
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.d("身份证号设置： " + e.getMessage());
                Util.showTimeOutNotice(currentContext);
            }

            @Override
            public void onResponse(String response, int id) {
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        //更新本地数据
                        SPUtils.getInstance().put(Constant.IDNUMBER, mIDCardIdCard.getText().toString().trim());
                        //返回到上一层
                        Intent intent = new Intent();
                        intent.putExtra("idCard", mIDCardIdCard.getText().toString().trim());
                        setResult(4, intent);
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
