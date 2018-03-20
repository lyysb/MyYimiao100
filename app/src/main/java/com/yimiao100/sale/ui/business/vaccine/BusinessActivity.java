package com.yimiao100.sale.ui.business.vaccine;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.yimiao100.sale.R;
import com.yimiao100.sale.mvpbase.BaseActivity;

/**
 * 我的业务列表
 */
public class BusinessActivity extends BaseActivity<BusinessContract.View, BusinessContract.Presenter> implements BusinessContract.View {

    public static void start(Context context, String userAccountType, String vendorId) {
        Intent intent = new Intent(context, BusinessActivity.class);
        intent.putExtra("userAccountType", userAccountType);
        intent.putExtra("vendorId", vendorId);
        context.startActivity(intent);
    }

    @Override
    protected void init() {
        super.init();

        initVariate();

        initView();
    }

    private void initVariate() {
        String userAccountType = getIntent().getStringExtra("userAccountType");
        String vendorId = getIntent().getStringExtra("vendorId");
    }

    private void initView() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_business_res;
    }
}
