package com.yimiao100.sale.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.yimiao100.sale.R;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.bean.ResourceListBean;
import com.yimiao100.sale.view.TitleView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 我的业务-订单所有失败状态界面
 */
public class OrderErrorActivity extends BaseActivity implements TitleView.TitleBarOnClickListener {

    @BindView(R.id.order_status_name)
    TextView mOrderStatusName;
    @BindView(R.id.order_error_reason)
    TextView mOrderErrorReason;
    @BindView(R.id.order_error_title)
    TitleView mOrderErrorTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_error);
        ButterKnife.bind(this);

        initView();

        initData();

    }

    private void initView() {
        mOrderErrorTitle.setOnTitleBarClick(this);
    }

    private void initData() {
        Intent intent = getIntent();
        ResourceListBean order = intent.getParcelableExtra("order");
        String orderStatusName = order.getOrderStatusName();
        mOrderStatusName.setText(orderStatusName);
        String invalidReason = order.getInvalidReason();
        mOrderErrorReason.setText(invalidReason);
    }

    @Override
    public void leftOnClick() {
        finish();
    }

    @Override
    public void rightOnClick() {

    }

    @OnClick(R.id.order_error_read)
    public void onClick() {
        finish();
    }
}
