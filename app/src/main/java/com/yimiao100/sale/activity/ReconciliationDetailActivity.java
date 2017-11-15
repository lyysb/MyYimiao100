package com.yimiao100.sale.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.yimiao100.sale.R;
import com.yimiao100.sale.adapter.listview.ReconciliationDetailAdapter;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.base.BaseActivitySingleList2;
import com.yimiao100.sale.bean.*;
import com.yimiao100.sale.ext.JSON;
import com.yimiao100.sale.utils.*;
import com.yimiao100.sale.view.TitleView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import org.jetbrains.annotations.NotNull;

/**
 * 对账详情
 * update by 2017年11月2日 - 替换父类
 */
public class ReconciliationDetailActivity extends BaseActivitySingleList2 implements ReconciliationDetailAdapter.onStatusClickListener {

    private ReconciliationDetailAdapter mAdapter;

    private final String URL_USER_FUND_ALL = Constant.BASE_URL + "/api/fund/user_fund_all";
    private final String BALANCE_ORDER_DETAIL = Constant.BASE_URL +"/api/order/balance_order_detail";
    private final String URL_UPDATE_TIPS = Constant.BASE_URL + "/api/tip/update_tip_status";
    //订单条目发货确认
    private final String CONFIRM_DELIVERY = Constant.BASE_URL + "/api/order/confirm_delivery";
    // 订单垫款操作
    private final String CONFIRM_OVERDUE = Constant.BASE_URL + "/api/advance/apply";
    //订单条目回款确认
    private final String CONFIRM_PAYMENT = Constant.BASE_URL + "/api/order/confirm_payment";
    private ArrayList<ReconciliationDetail> mList;

    private final String TIP_TYPE = "tipType";
//    private String mTipType = "order_balance";
    private String mTipType = "vaccine_order_balance";  // 2017年9月4日 调整接口参数
//    private final String VENDOR_ID = "vendorId";
//    private String mVendorId;
    private final String BIZ_ID = "bizId";
    private String mBizId;
    private final String ORDER_ID = "orderId";
    private String mOrderId;
    private final String ORDER_ITEM_ID = "orderItemId";
    private String mOrderItemId;
    private String mCategoryName;
    private String mVendorName;
    private String mCustomerName;
    private String mProductName;
    private String mDosageForm;
    private String mSpec;
    private String mSerialNo;
    private TextView mTvOverdueAmount;
    private String mUserAccountType;
    private int mVendorId;
    private String mLogoImageUrl;

    public static void start(Context context, String orderId, String categoryName, String productName,
                             String vendorName, String customerName, String dosageForm, String spec,
                             String serialNo, String userAccountType, int vendorId, String logoImageUrl) {
        Intent intent = new Intent(context, ReconciliationDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("orderId", orderId);
        //产品名-分类名
        bundle.putString("categoryName", categoryName);
        //商品名
        bundle.putString("productName", productName);
        //厂家名称
        bundle.putString("vendorName", vendorName);
        //客户名称
        bundle.putString("customerName", customerName);
        //剂型
        bundle.putString("dosageForm", dosageForm);
        //规格
        bundle.putString("spec", spec);
        //协议单号
        bundle.putString("serialNo", serialNo);
        // 账户类型
        bundle.putString("userAccountType", userAccountType);
        // VendorID
        bundle.putInt("vendorId", vendorId);
        // LogoURL
        bundle.putString("logoImageUrl", logoImageUrl);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    public void initVariate() {
        super.initVariate();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mOrderId = bundle.getString("orderId");
        //分类
        mCategoryName = bundle.getString("categoryName");
        //厂家名称
        mVendorName = bundle.getString("vendorName");
        //客户名称
        mCustomerName = bundle.getString("customerName");
        //产品名
        mProductName = bundle.getString("productName");
        //剂型
        mDosageForm = bundle.getString("dosageForm");
        //规格
        mSpec = bundle.getString("spec");
        //协议单号
        mSerialNo = bundle.getString("serialNo");
        // 账户类型
        mUserAccountType = bundle.getString("userAccountType");
        // VendorId
        mVendorId = bundle.getInt("vendorId");
        // logoUrl
        mLogoImageUrl = bundle.getString("logoImageUrl");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // 充值成功后返回该界面，重新刷新可用垫款
        refreshOverdueData();
    }

    @Override
    public void initView() {
        super.initView();

        initRefreshLayoutParams();

        initHeaderView();
    }

    private void initRefreshLayoutParams() {
        refreshLayout.setBackgroundColor(android.R.color.white);
        refreshLayout.setBackgroundResource(R.mipmap.ico_reconciliation_background);
    }

    private void initHeaderView() {
        //添加头部布局
        View view = View.inflate(getApplicationContext(), R.layout.head_reconciliation_detail, null);
        //商品名
        TextView head_product_formal_name = (TextView) view.findViewById(R.id.head_product_formal_name);
        head_product_formal_name.setText(mProductName);
        //产品---分类名
        TextView head_product_common_name = (TextView) view.findViewById(R.id.head_product_common_name);
        head_product_common_name.setText(mCategoryName);
        //厂家名称
        TextView head_vendor_name = (TextView) view.findViewById(R.id.head_vendor_name);
        head_vendor_name.setText(mVendorName);
        //客户名称
        TextView head_customer_name = (TextView) view.findViewById(R.id.head_customer_name);
        head_customer_name.setText(mCustomerName);
        //剂型
        TextView head_dosage_form = (TextView) view.findViewById(R.id.head_dosage_form);
        head_dosage_form.setText("剂型：" + mDosageForm);
        //规格
        TextView head_spec = (TextView) view.findViewById(R.id.head_spec);
        head_spec.setText("规格：" + mSpec);
        //协议单号
        TextView head_serial_no = (TextView) view.findViewById(R.id.head_serial_no);
        head_serial_no.setText("协议单号：" + mSerialNo);

        // 可用垫款及充值按钮
        mTvOverdueAmount = (TextView) view.findViewById(R.id.head_overdue_amount);
        view.findViewById(R.id.head_overdue_pay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 进入充值界面
                toOverduePay();
            }
        });
        listView.setPadding(
                DensityUtil.dp2px(this,10),
                DensityUtil.dp2px(this,8),
                DensityUtil.dp2px(this,10),
                0
        );
        listView.setDividerHeight(0);
        listView.addHeaderView(view, null, false);
    }

    @Override
    public void onRefreshData(@NotNull final TwinklingRefreshLayout refreshLayout) {
        // 刷新账户数据
        refreshOverdueData();
        //获取数据
        OkHttpUtils.post().url(BALANCE_ORDER_DETAIL)
                .addHeader(ACCESS_TOKEN, accessToken)
                .addParams(ORDER_ID, mOrderId)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.d("对账详情E：" + e.getMessage());
                Util.showTimeOutNotice(currentContext);
                refreshLayout.finishRefreshing();
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.d("对账详情：\n" + response);
                refreshLayout.finishRefreshing();
                ReconciliationDetailBean reconciliationDetailBean = JSON.parseObject(response, ReconciliationDetailBean.class);
                switch (reconciliationDetailBean.getStatus()) {
                    case "success":
                        //订单条目列表
                        mList = reconciliationDetailBean.getOrderItemList();
                        mAdapter = new ReconciliationDetailAdapter(getApplicationContext(), mList);
                        mAdapter.setOnStatusClickListener(ReconciliationDetailActivity.this);
                        listView.setAdapter(mAdapter);
                        handleEmptyView(mList);
                        if (mList.size() != 0) {
                            // 设置消息已读
                            updateTips();
                        }
                        break;
                    case "failure":
                        Util.showError(ReconciliationDetailActivity.this, reconciliationDetailBean.getReason());
                        break;
                }
            }
        });

    }


    /**
     * 刷新垫款数据
     */
    private void refreshOverdueData() {
        OkHttpUtils.post().url(URL_USER_FUND_ALL).addHeader(ACCESS_TOKEN, accessToken)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.d("用户账户信息-对账：" + e.getMessage());
                Util.showTimeOutNotice(currentContext);
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.d("用户账户信息-对账：\n" + response);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        UserFundAll userFundAll = JSON.parseObject(response, UserFundBean.class).getUserFundAll();
                        if (userFundAll != null) {
                            switch (mUserAccountType) {
                                case "corporate":
                                    mTvOverdueAmount.setText(
                                            FormatUtils.RMBFormat(userFundAll.getVaccineCorporateAdvance()) + "元"
                                    );
                                    break;
                                case "personal":
                                    mTvOverdueAmount.setText(
                                            FormatUtils.RMBFormat(userFundAll.getVaccinePersonalAdvance()) + "元"
                                    );
                                    break;
                            }
                        } else {
                            mTvOverdueAmount.setText("¥0.00");
                        }
                        break;
                    case "failure":
                        Util.showError(ReconciliationDetailActivity.this, errorBean.getReason());
                        break;
                }
            }
        });
    }


    /**
     * 选择充值，进入垫款订单界面
     */
    private void toOverduePay() {
        PromotionActivity.start(this, "reconciliation", mVendorId, mUserAccountType,
                mLogoImageUrl, mVendorName);
    }

    /**
     * 更新消息已读状态
     */
    private void updateTips() {
//        mVendorId = String.valueOf(mList.get(0).getVendorId());
        mBizId = String.valueOf(mList.get(0).getVendorId());
        OkHttpUtils.post().url(URL_UPDATE_TIPS).addHeader(ACCESS_TOKEN, accessToken)
                .addParams(TIP_TYPE, mTipType).addParams(BIZ_ID, mBizId)
                .addParams(ORDER_ID, mOrderId).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.d("update tips error");
                e.printStackTrace();
                Util.showTimeOutNotice(currentContext);
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.d("update tips result is  " + response);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        LogUtil.d("update tips success");
                        // 发布事件
                        Event event = new Event();
                        Event.eventType = EventType.ORDER_BALANCE;
                        EventBus.getDefault().post(event);
                        break;
                    case "failure":
                        LogUtil.d("update tips failure");
                        Util.showError(currentContext, errorBean.getReason());
                        break;
                }
            }
        });
    }

    /**
     * 订单条目发货确认
     *
     * @param position
     */
    @Override
    public void onDeliveryClick(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.dialog);
        View dialogView = View.inflate(this, R.layout.dialog_confirm_reconciliation, null);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();
        //有异议
        TextView disagree = (TextView) dialogView.findViewById(R.id.dialog_disagree);
        disagree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //进入客服
                Util.enterCustomerService(currentContext);
                dialog.dismiss();
            }
        });
        //同意
        TextView agree = (TextView) dialogView.findViewById(R.id.dialog_agree);
        agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //提交数据
                submitDeliveryAgree(position, dialog);
            }
        });
        dialog.show();
    }

    /**
     * 点击发货确认
     * @param position
     * @param dialog
     */
    private void submitDeliveryAgree(final int position, AlertDialog dialog) {
        //订单条目id
        mOrderItemId = mAdapter.getItem(position).getId() + "";
        OkHttpUtils.post().url(CONFIRM_DELIVERY)
                .addHeader(ACCESS_TOKEN, accessToken)
                .addParams(ORDER_ITEM_ID, mOrderItemId)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.d("订单条目发货确认E：" + e.getMessage());
                Util.showTimeOutNotice(currentContext);
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.d("订单条目发货确认：" + response);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        //将发货确认状态次改成已确认
                        mAdapter.getItem(position).setDeliveryConfirmStatus("confirmed");
                        mAdapter.getItem(position).setDeliveryConfirmStatusName("已确认");
                        mAdapter.notifyDataSetChanged();
                        break;
                    case "failure":
                        Util.showError(ReconciliationDetailActivity.this, errorBean.getReason());
                        break;
                }
            }
        });
        dialog.dismiss();
    }

    @Override
    public void onOverdueClick(final int position) {
        new AlertDialogManager.Builder(this).setMsg("是否进行逾期垫款？").setOnPositiveClickListener(new AlertDialogManager.PositiveClickListener() {
            @Override
            public void onPositiveClick() {
                overdueOrder(position);
            }
        }).build().show();

    }

    /**
     * 提交垫款操作
     * @param position
     */
    private void overdueOrder(final int position) {
        final ProgressDialog loadingProgress = ProgressDialogUtil.getLoadingProgress(this, "提交中");
        loadingProgress.show();
        mOrderItemId = mAdapter.getItem(position).getId() + "";
        OkHttpUtils.post().url(CONFIRM_OVERDUE).addHeader(ACCESS_TOKEN, accessToken)
                .addParams(ORDER_ITEM_ID, mOrderItemId)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                e.printStackTrace();
                Util.showTimeOutNotice(currentContext);
                loadingProgress.dismiss();
            }

            @Override
            public void onResponse(String response, int id) {
                loadingProgress.dismiss();
                LogUtil.d("对账垫款：\n" + response);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        mAdapter.getItem(position).setAdvanceAt(System.currentTimeMillis() + "");
                        mAdapter.notifyDataSetChanged();
                        // 刷新垫款金额
                        refreshOverdueData();
                        break;
                    case "failure":
                        Util.showError(currentContext, errorBean.getReason());
                        break;
                }
            }
        });
    }


    /**
     * 订单条目回款确认
     *
     * @param position
     */
    @Override
    public void onPaymentClick(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.dialog);
        View dialogView = View.inflate(this, R.layout.dialog_confirm_reconciliation, null);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();
        //有异议
        TextView disagree = (TextView) dialogView.findViewById(R.id.dialog_disagree);
        disagree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //进入客服
                Util.enterCustomerService(getApplicationContext());
                dialog.dismiss();
            }
        });
        //同意
        TextView agree = (TextView) dialogView.findViewById(R.id.dialog_agree);
        agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //订单条目id
                submitPaymentAgree(position, dialog);
            }
        });
        dialog.show();
    }

    /**
     * 提交回款确认
     * @param position
     * @param dialog
     */
    private void submitPaymentAgree(final int position, final AlertDialog dialog) {
        mOrderItemId = mAdapter.getItem(position).getId() + "";
        OkHttpUtils.post().url(CONFIRM_PAYMENT)
                .addHeader(ACCESS_TOKEN, accessToken)
                .addParams(ORDER_ITEM_ID, mOrderItemId)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.d("订单条目回款确认E：" + e.getMessage());
                Util.showTimeOutNotice(currentContext);
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.d("订单条目回款确认：" + response);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        //将回款确认状态次改成已确认
                        mAdapter.getItem(position).setPaymentConfirmStatus("confirmed");
                        mAdapter.getItem(position).setPaymentConfirmStatusName("已确认");
                        mAdapter.notifyDataSetChanged();
                        break;
                    case "failure":
                        Util.showError(ReconciliationDetailActivity.this, errorBean.getReason());
                        break;
                }
                dialog.dismiss();
            }
        });
    }

    @NotNull
    @Override
    public String getTitleText() {
        return "对账";
    }

    @Override
    public void onLoadMoreData(@NotNull TwinklingRefreshLayout refreshLayout) {
        refreshLayout.finishLoadmore();
    }

    @Override
    public void onListItemClick(int position) {

    }
}
