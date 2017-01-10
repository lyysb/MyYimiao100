package com.yimiao100.sale.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.widget.ImageButton;
import android.widget.TextView;

import com.yimiao100.sale.R;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.bean.ResourceListBean;
import com.yimiao100.sale.utils.FormatUtils;
import com.yimiao100.sale.utils.TimeUtil;
import com.yimiao100.sale.view.TitleView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 我的业务-第一步-竞标中
 */
public class OrderSubmitActivity extends BaseActivity implements TitleView.TitleBarOnClickListener {

    @BindView(R.id.order_submit_title)
    TitleView mOrderSubmitTitle;
    @BindView(R.id.order_submit_submit_time)
    TextView mOrderSubmitSubmitTime;
    @BindView(R.id.order_submit_vendor_name)
    TextView mOrderSubmitVendorName;
    @BindView(R.id.order_submit_common_name)
    TextView mOrderSubmitCommonName;
    @BindView(R.id.order_submit_region)
    TextView mOrderSubmitRegion;
    @BindView(R.id.order_submit_spec)
    TextView mOrderSubmitSpec;
    @BindView(R.id.order_submit_dosage_form)
    TextView mOrderSubmitDosageForm;
    @BindView(R.id.order_submit_time)
    TextView mOrderSubmitTime;
    @BindView(R.id.order_submit_money)
    TextView mOrderSubmitMoney;
    @BindView(R.id.order_submit_no)
    TextView mOrderSubmitNo;
    @BindView(R.id.order_submit_confirm)
    ImageButton mOrderSubmitConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_submit);
        ButterKnife.bind(this);
        initView();

        initData();
    }
    private void initView() {
        mOrderSubmitTitle.setOnTitleBarClick(this);
    }

    private void initData() {
        Intent intent = getIntent();
        ResourceListBean order = intent.getParcelableExtra("order");
        //提交日期
        String submit_time = TimeUtil.timeStamp2Date(order.getCreatedAt() + "", "yyyy年MM月dd日");
        mOrderSubmitSubmitTime.setText(submit_time + "您提交的申请推广,\n已经收到提交的竞标保证金,\n目前正在审核中,请耐心等待。");
        //厂家名称
        String vendorName = order.getVendorName();
        mOrderSubmitVendorName.setText(vendorName);
        //产品名称-分类名
        String categoryName = order.getCategoryName();
        mOrderSubmitCommonName.setText("产品：" + categoryName);
        //规格
        String spec = order.getSpec();
        mOrderSubmitSpec.setText("规格：" + spec);
        //剂型
        String dosageForm = order.getDosageForm();
        mOrderSubmitDosageForm.setText("剂型：" + dosageForm);
        //区域
        String region = order.getProvinceName() + "\t" + order.getCityName() + "\t" + order.getAreaName();
        mOrderSubmitRegion.setText("区域：" + region);
        //时间
        String time = TimeUtil.timeStamp2Date(order.getCreatedAt() + "", "yyyy.MM.dd");
        mOrderSubmitTime.setText("时间：" + time);
        //保证金
        String totalDeposit = FormatUtils.MoneyFormat(order.getSaleDeposit());
        Spanned totalMoney = Html.fromHtml("推广保证金：" + "<font color=\"#4188d2\">" + totalDeposit + "</font>" + " (人民币)");
        mOrderSubmitMoney.setText(totalMoney);
        //协议单号
        String serialNo = order.getSerialNo();
        mOrderSubmitNo.setText("协议单号：" + serialNo);
    }



    @Override
    public void leftOnClick() {
        finish();
    }

    @Override
    public void rightOnClick() {

    }

    @OnClick(R.id.order_submit_confirm)
    public void onClick() {
        finish();
    }
}
