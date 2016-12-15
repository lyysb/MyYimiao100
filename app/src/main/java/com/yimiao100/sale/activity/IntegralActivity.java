package com.yimiao100.sale.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.meiqia.core.callback.OnInitCallback;
import com.meiqia.meiqiasdk.util.MQConfig;
import com.meiqia.meiqiasdk.util.MQIntentBuilder;
import com.yimiao100.sale.R;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.FormatUtils;
import com.yimiao100.sale.utils.SharePreferenceUtil;
import com.yimiao100.sale.utils.ToastUtil;
import com.yimiao100.sale.view.TitleView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 我的积分
 */
public class IntegralActivity extends BaseActivity implements TitleView.TitleBarOnClickListener {

    @BindView(R.id.integral_title)
    TitleView mIntegralTitle;
    @BindView(R.id.integral_count)
    TextView mIntegralCount;
    @BindView(R.id.integral_detail2)
    TextView mIntegralDetail2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_integral);
        ButterKnife.bind(this);

        initView();

        initData();
    }

    private void initView() {
        mIntegralTitle.setOnTitleBarClick(this);
    }

    private void initData() {
        //获取积分
        int integral = (int) SharePreferenceUtil.get(this, Constant.INTEGRAL, 0);
        mIntegralCount.setText(FormatUtils.NumberFormat(integral));
        mIntegralDetail2.setText(FormatUtils.NumberFormat(integral));
    }

    @OnClick({R.id.integral_detail,R.id.integral_detail_shop, R.id.integral_detail_note, R.id.integral_service,
    R.id.integral_add,R.id.integral_add2,R.id.integral_get,})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.integral_detail:
                //进入到积分明细界面
                startActivity(new Intent(this, IntegralDetailActivity.class));
                break;
            case R.id.integral_detail_shop:
                //进入到积分商城
                startActivity(new Intent(this, IntegralShopActivity.class));
                break;
            case R.id.integral_detail_note:
                //进入到兑换记录
                startActivity(new Intent(this, ExchangeNoteActivity.class));
                break;
            case R.id.integral_add:
            case R.id.integral_add2:
            case R.id.integral_get:
                ToastUtil.showShort(currentContext, "敬请期待");
                break;
            case R.id.integral_service:
                enterCustomerService();
                break;
        }
    }

    /**
     * 打开客服界面
     */
    private void enterCustomerService() {
        MQConfig.init(this, Constant.MEI_QIA_APP_KEY, new OnInitCallback() {
            @Override
            public void onSuccess(String clientId) {
                Toast.makeText(getApplicationContext(), "init success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int code, String message) {
                Toast.makeText(getApplicationContext(), "int failure", Toast.LENGTH_SHORT).show();
            }
        });
        Intent intent = new MQIntentBuilder(this)
                .build();
        startActivity(intent);
    }
    @Override
    public void leftOnClick() {
        finish();
    }

    @Override
    public void rightOnClick() {

    }
}
