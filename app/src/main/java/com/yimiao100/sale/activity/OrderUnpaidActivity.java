package com.yimiao100.sale.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.widget.TextView;

import com.yimiao100.sale.R;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.bean.ResourceListBean;
import com.yimiao100.sale.utils.TimeUtil;
import com.yimiao100.sale.view.TitleView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 我的业务第一状态-未付款
 */
public class OrderUnpaidActivity extends BaseActivity implements TitleView.TitleBarOnClickListener {

    @BindView(R.id.order_unpaid_title)
    TitleView mTitle;
    @BindView(R.id.order_unpaid_submit_time)
    TextView mTime;
    @BindView(R.id.order_unpaid_vendor_name)
    TextView mVendorName;
    @BindView(R.id.order_unpaid_common_name)
    TextView mCommonName;
    @BindView(R.id.order_unpaid_region)
    TextView mRegion;
    @BindView(R.id.order_unpaid_spec)
    TextView mSpec;
    @BindView(R.id.order_unpaid_dosage_form)
    TextView mDosageForm;
    @BindView(R.id.order_unpaid_time)
    TextView mOrderTime;
    @BindView(R.id.order_unpaid_money)
    TextView mMoney;
    @BindView(R.id.order_unpaid_no)
    TextView mNo;
    @BindView(R.id.order_unpaid_hint)
    TextView mHint;
    @BindView(R.id.order_unpaid_expired_at)
    TextView mExpiredAt;
    private ResourceListBean mOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_unpaid);
        ButterKnife.bind(this);

        initView();

        initData();
    }

    private void initView() {
        mTitle.setOnTitleBarClick(this);
    }

    private void initData() {
        Intent intent = getIntent();
        mOrder = intent.getParcelableExtra("order");
        //提交日期
        String submit_time = TimeUtil.timeStamp2Date(mOrder.getCreatedAt() + "", "yyyy年MM月dd日");
        mTime.setText(submit_time + "提交的申请推广已经收到您提交的竞标保证金，\n目前正在审核中，请您耐心等待。");
        //厂家名称
        String vendorName = mOrder.getVendorName();
        mVendorName.setText(vendorName);
        //产品名称-分类名
        String categoryName = mOrder.getCategoryName();
        mCommonName.setText("产品：" + categoryName);
        //规格
        String spec = mOrder.getSpec();
        mSpec.setText("规格：" + spec);
        //剂型
        String dosageForm = mOrder.getDosageForm();
        mDosageForm.setText("剂型：" + dosageForm);
        //区域
        String region = mOrder.getProvinceName() + "\t" + mOrder.getCityName() + "\t" + mOrder
                .getAreaName();
        mRegion.setText("区域：" + region);
        //时间
        String time = TimeUtil.timeStamp2Date(mOrder.getCreatedAt() + "", "yyyy.MM.dd");
        mOrderTime.setText("时间：" + time);
        //保证金
        String totalDeposit = mOrder.getSaleDeposit() + "";
        Spanned totalMoney = Html.fromHtml("推广保证金：" + "<font color=\"#4188d2\">" + totalDeposit +
                "</font>" + " (人民币)");
        mMoney.setText(totalMoney);
        //协议单号
        String serialNo = mOrder.getSerialNo();
        mNo.setText("协议单号：" + serialNo);
        //竞标保证金提示
        String bidDeposit = mOrder.getBidDeposit() + "";
        mHint.setText("本次推广资源的竞标保证金为￥" + bidDeposit + "元，请于竞标截止日前尽快提交。");
        //竞标截止时间
        long bidExpiredAt = mOrder.getBidExpiredAt();
        String expire = TimeUtil.timeStamp2Date(bidExpiredAt + "", "yyyy年MM月dd日");
        mExpiredAt.setText(Html.fromHtml("（<font color=\"#4188d2\">注意：</font>本资源竞标时间截止日为\t" + expire + "）"));
    }

    @OnClick(R.id.order_unpaid_submit)
    public void onClick() {
        //进入支付界面
        Intent intent = new Intent(this, SubmitPromotionActivity.class);
        intent.putExtra("order", mOrder);
        intent.putExtra("mark", "order");
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
