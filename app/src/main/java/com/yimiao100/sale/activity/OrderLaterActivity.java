package com.yimiao100.sale.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.widget.ImageButton;
import android.widget.TextView;

import com.yimiao100.sale.R;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.view.TitleView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 我的业务-第二状态-审核中状态
 */
public class OrderLaterActivity extends BaseActivity implements TitleView.TitleBarOnClickListener {

    @BindView(R.id.order_later_title)
    TitleView mOrderLaterTitle;
    @BindView(R.id.order_later_submit_time)
    TextView mOrderLaterSubmitTime;
    @BindView(R.id.order_later_vendor_name)
    TextView mOrderLaterVendorName;
    @BindView(R.id.order_later_region)
    TextView mOrderLaterRegion;
    @BindView(R.id.order_later_spec)
    TextView mOrderLaterSpec;
    @BindView(R.id.order_later_dosage_form)
    TextView mOrderLaterDosageForm;
    @BindView(R.id.order_later_time)
    TextView mOrderLaterTime;
    @BindView(R.id.order_later_money)
    TextView mOrderLaterMoney;
    @BindView(R.id.order_later_no)
    TextView mOrderLaterNo;
    @BindView(R.id.order_later_hint)
    TextView mOrderLaterHint;
    @BindView(R.id.order_later_confirm)
    ImageButton mOrderLaterConfirm;
    @BindView(R.id.order_later_common_name)
    TextView mOrderLaterCommonName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_later);
        ButterKnife.bind(this);

        initView();

        initData();
    }

    private void initView() {
        mOrderLaterTitle.setOnTitleBarClick(this);
    }

    private void initData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        //提交日期
        String submit_time = bundle.getString("submit_time");
        mOrderLaterSubmitTime.setText(submit_time + "提交的申请推广已经收到您提交的竞标保证金，\n目前正在审核中，请您耐心等待。");
        //厂家名称
        String vendorName = bundle.getString("vendorName");
        mOrderLaterVendorName.setText(vendorName);
        //产品名称-分类名
        String categoryName = bundle.getString("categoryName");
        mOrderLaterCommonName.setText("产品：" + categoryName);
        //规格
        String spec = bundle.getString("spec");
        mOrderLaterSpec.setText("规格：" + spec);
        //剂型
        String dosageForm = bundle.getString("dosageForm");
        mOrderLaterDosageForm.setText("剂型：" + dosageForm);
        //区域
        String region = bundle.getString("region");
        mOrderLaterRegion.setText("区域：" + region);
        //时间
        String time = bundle.getString("time");
        mOrderLaterTime.setText("时间：" + time);
        //保证金
        String totalDeposit = bundle.getString("totalDeposit");
        Spanned totalMoney = Html.fromHtml("推广保证金：" + "<font color=\"#4188d2\">" + totalDeposit + "</font>" + " (人民币)");
        mOrderLaterMoney.setText(totalMoney);
        //协议单号
        String serialNo = bundle.getString("serialNo");
        mOrderLaterNo.setText("协议单号：" + serialNo);
        //竞标保证金提示
        String bidDeposit = bundle.getString("bidDeposit");
        mOrderLaterHint.setText("本次推广资源的竞标保证金为￥" + bidDeposit + "元，请于竞标截止日前转到如下账户。\n汇款转账时,必须在备注处填写协议单号。");
    }

    @OnClick(R.id.order_later_confirm)
    public void onClick() {
        finish();
    }

    @Override
    public void leftOnClick() {
        finish();
    }

    @Override
    public void rightOnClick() {

    }
}
