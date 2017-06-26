package com.yimiao100.sale.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.meiqia.core.MQManager;
import com.meiqia.core.bean.MQMessage;
import com.meiqia.core.callback.OnGetMessageListCallback;
import com.yimiao100.sale.R;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.bean.Event;
import com.yimiao100.sale.bean.ResourceListBean;
import com.yimiao100.sale.utils.FormatUtils;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.TimeUtil;
import com.yimiao100.sale.utils.Util;
import com.yimiao100.sale.view.TitleView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

/**
 * 我的业务-第一步-竞标中
 */
public class OrderSubmitActivity extends BaseActivity implements TitleView.TitleBarOnClickListener {

    @BindView(R.id.order_submit_title)
    TitleView mOrderSubmitTitle;
    @BindView(R.id.order_submit_service)
    ImageView mService;
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
    @BindView(R.id.order_submit_customer)
    TextView mOrderSubmitCustomer;
    @BindView(R.id.order_submit_no)
    TextView mOrderSubmitNo;
    @BindView(R.id.order_submit_confirm)
    ImageButton mOrderSubmitConfirm;
    private Badge mBadge;

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
        mBadge = new QBadgeView(this).bindTarget(mService)
                .setBadgePadding(4, true)
                .setGravityOffset(9, true)
                .setShowShadow(false);
        MQManager.getInstance(this).getUnreadMessages(new OnGetMessageListCallback() {
            @Override
            public void onSuccess(List<MQMessage> list) {
                if (list.size() != 0) {
                    mBadge.setBadgeNumber(-1);
                }
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
    }

    private void initData() {
        Intent intent = getIntent();
        ResourceListBean order = intent.getParcelableExtra("order");
        //提交日期
        String submit_time = TimeUtil.timeStamp2Date(order.getCreatedAt() + "", "yyyy年MM月dd日");
        mOrderSubmitSubmitTime.setText(submit_time + getString(R.string.order_submit_notice));
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
        // 客户
        if (order.getCustomerName() != null) {
            mOrderSubmitCustomer.setText("客户：" + order.getCustomerName());
        }
        //协议单号
        String serialNo = order.getSerialNo();
        mOrderSubmitNo.setText("协议单号：" + serialNo);
    }

    @Override
    public void onEventMainThread(@NotNull Event event) {
        super.onEventMainThread(event);
        switch (Event.eventType) {
            case RECEIVE_MSG:
                // 收到客服消息，显示小圆点
                mBadge.setBadgeNumber(-1);
                break;
            case READ_MSG:
                // 设置小圆点为0
                mBadge.setBadgeNumber(0);
                break;
            default:
                LogUtil.d("unknown event type is " + Event.eventType);
                break;
        }
    }


    @Override
    public void leftOnClick() {
        finish();
    }

    @Override
    public void rightOnClick() {

    }

    @OnClick({R.id.order_submit_service, R.id.order_submit_confirm})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.order_submit_service:
                //联系客服
                Util.enterCustomerService(this);
                break;
            case R.id.order_submit_confirm:
                finish();
                break;
        }
    }
}
