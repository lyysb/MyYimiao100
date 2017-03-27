package com.yimiao100.sale.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.yimiao100.sale.R;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.utils.LogUtil;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class UploadActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.upload_return, R.id.upload_report, R.id.upload_grant})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.upload_return:
                finish();
                break;
            case R.id.upload_report:
                LogUtil.Companion.d("进入 不良反应申报");
                startActivity(new Intent(this, ReportDetailActivity.class));
                break;
            case R.id.upload_grant:
                LogUtil.Companion.d("进入 申请授权书");
                startActivity(new Intent(this, AuthorizationDetailActivity.class));
                break;
        }
    }
}
