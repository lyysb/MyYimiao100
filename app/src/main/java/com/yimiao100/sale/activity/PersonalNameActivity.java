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
import com.yimiao100.sale.utils.Util;
import com.yimiao100.sale.view.TitleView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

public class PersonalNameActivity extends BaseActivity implements TitleView.TitleBarOnClickListener {

    @BindView(R.id.name_title)
    TitleView mNameTitle;
    @BindView(R.id.personal_name)
    EditText mPersonalName;
    @BindView(R.id.name_clear)
    ImageView mNameClear;

    private final String UPDATE_CN_NAME = "/api/user/update_cn_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_name);
        ButterKnife.bind(this);

        mNameTitle.setOnTitleBarClick(this);


        //获取用户姓名
        String user_name = (String) SharePreferenceUtil.get(this, Constant.CNNAME, "请输入用户姓名");
        mPersonalName.setHint(user_name);
    }

    @OnClick(R.id.name_clear)
    public void onClick() {
        //清空输入框
        mPersonalName.setText("");
    }

    @Override
    public void leftOnClick() {
        finish();
    }

    @Override
    public void rightOnClick() {
        //保存将数据发送到服务器
        String user_name_url = Constant.BASE_URL + UPDATE_CN_NAME;
        OkHttpUtils
                .post()
                .url(user_name_url)
                .addHeader("X-Authorization-Token", mAccessToken)
                .addParams("cnName", mPersonalName.getText().toString().trim())
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtil.d("姓名设置： " + e.getMessage());
                        Util.showTimeOutNotice(currentContext);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                        switch (errorBean.getStatus()){
                            case "success":
                                //更新本地数据
                                SharePreferenceUtil.put(getApplicationContext(), Constant.CNNAME, mPersonalName.getText().toString().trim());
                                //返回到上一层
                                Intent intent=new Intent();
                                intent.putExtra("name", mPersonalName.getText().toString().trim());
                                setResult(1, intent);
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
