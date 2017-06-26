package com.yimiao100.sale.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.yimiao100.sale.R;
import com.yimiao100.sale.adapter.listview.ReconciliationDetailAdapter;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.bean.ErrorBean;
import com.yimiao100.sale.bean.Event;
import com.yimiao100.sale.bean.EventType;
import com.yimiao100.sale.bean.ReconciliationDetail;
import com.yimiao100.sale.bean.ReconciliationDetailBean;
import com.yimiao100.sale.ext.JSON;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.Util;
import com.yimiao100.sale.view.TitleView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

/**
 * 对账详情
 */
public class ReconciliationDetailActivity extends BaseActivity implements TitleView.TitleBarOnClickListener, ReconciliationDetailAdapter.onStatusClickListener {

    @BindView(R.id.reconciliation_detail_title)
    TitleView mReconciliationDetailTitle;
    @BindView(R.id.reconciliation_detail_list)
    ListView mReconciliationDetailListView;
    private ReconciliationDetailAdapter mReconciliationDetailAdapter;

    private final String BALANCE_ORDER_DETAIL = Constant.BASE_URL +"/api/order/balance_order_detail";
    private final String URL_UPDATE_TIPS = Constant.BASE_URL + "/api/tip/update_tip_status";
    //订单条目发货确认
    private final String CONFIRM_DELIVERY = Constant.BASE_URL + "/api/order/confirm_delivery";
    //订单条目回款确认
    private final String CONFIRM_PAYMENT = Constant.BASE_URL + "/api/order/confirm_payment";
    private ArrayList<ReconciliationDetail> mOrderItemList;

    private final String TIP_TYPE = "tipType";
    private String mTipType = "order_balance";
    private final String VENDOR_ID = "vendorId";
    private String mVendorId;
    private final String ORDER_ID = "orderId";
    private String mOrderId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reconciliation_detail);
        ButterKnife.bind(this);

        showLoadingProgress();

        initView();

        initData();
    }

    private void initView() {
        mReconciliationDetailTitle.setOnTitleBarClick(this);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mOrderId = bundle.getString("orderId");
        //分类
        String categoryName = bundle.getString("categoryName");
        //厂家名称
        String vendorName = bundle.getString("vendorName");
        //客户名称
        String customerName = bundle.getString("customerName");
        //产品名
        String productName = bundle.getString("productName");
        //剂型
        String dosageForm = bundle.getString("dosageForm");
        //规格
        String spec = bundle.getString("spec");
        //协议单号
        String serialNo = bundle.getString("serialNo");
        //添加头部布局
        View view = View.inflate(getApplicationContext(), R.layout.head_reconciliation_detail, null);
        //商品名
        TextView head_product_formal_name = (TextView) view.findViewById(R.id.head_product_formal_name);
        head_product_formal_name.setText(productName);
        //产品---分类名
        TextView head_product_common_name = (TextView) view.findViewById(R.id.head_product_common_name);
        head_product_common_name.setText(categoryName);
        //厂家名称
        TextView head_vendor_name = (TextView) view.findViewById(R.id.head_vendor_name);
        head_vendor_name.setText(vendorName);
        //客户名称
        TextView head_customer_name = (TextView) view.findViewById(R.id.head_customer_name);
        head_customer_name.setText(customerName);
        //剂型
        TextView head_dosage_form = (TextView) view.findViewById(R.id.head_dosage_form);
        head_dosage_form.setText("剂型：" + dosageForm);
        //规格
        TextView head_spec = (TextView) view.findViewById(R.id.head_spec);
        head_spec.setText("规格：" + spec);
        //协议单号
        TextView head_serial_no = (TextView) view.findViewById(R.id.head_serial_no);
        head_serial_no.setText("协议单号：" + serialNo);

        mReconciliationDetailListView.addHeaderView(view);

    }

    private void initData() {
        //获取数据
        OkHttpUtils.post().url(BALANCE_ORDER_DETAIL)
                .addHeader(ACCESS_TOKEN, accessToken)
                .addParams(ORDER_ID, mOrderId)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.d("对账详情E：" + e.getMessage());
                Util.showTimeOutNotice(currentContext);
                hideLoadingProgress();
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.d("对账详情：" + response);
                hideLoadingProgress();
                ReconciliationDetailBean reconciliationDetailBean = JSON.parseObject(response, ReconciliationDetailBean.class);
                switch (reconciliationDetailBean.getStatus()) {
                    case "success":
                        //订单条目列表
                        mOrderItemList = reconciliationDetailBean.getOrderItemList();
                        mReconciliationDetailAdapter = new ReconciliationDetailAdapter(getApplicationContext(), mOrderItemList);
                        mReconciliationDetailAdapter.setOnStatusClickListener(ReconciliationDetailActivity.this);
                        mReconciliationDetailListView.setAdapter(mReconciliationDetailAdapter);
                        if (mOrderItemList.size() != 0) {
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
     * 更新消息已读状态
     */
    private void updateTips() {
        mVendorId = String.valueOf(mOrderItemList.get(0).getVendorId());
        OkHttpUtils.post().url(URL_UPDATE_TIPS).addHeader(ACCESS_TOKEN, accessToken)
                .addParams(TIP_TYPE, mTipType).addParams(VENDOR_ID, mVendorId)
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


    @Override
    public void leftOnClick() {
        finish();
    }

    @Override
    public void rightOnClick() {

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
                Util.enterCustomerService(getApplicationContext());
                dialog.dismiss();
            }
        });
        //同意
        TextView agree = (TextView) dialogView.findViewById(R.id.dialog_agree);
        agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //提交数据
                //订单条目id
                int orderItemId = mReconciliationDetailAdapter.getItem(position).getId();
                OkHttpUtils.post().url(CONFIRM_DELIVERY)
                        .addHeader(ACCESS_TOKEN, accessToken)
                        .addParams("orderItemId", orderItemId + "")
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
                                mReconciliationDetailAdapter.getItem(position).setDeliveryConfirmStatus("confirmed");
                                mReconciliationDetailAdapter.getItem(position).setDeliveryConfirmStatusName("已确认");
                                mReconciliationDetailAdapter.notifyDataSetChanged();
                                break;
                            case "failure":
                                Util.showError(ReconciliationDetailActivity.this, errorBean.getReason());
                                break;
                        }
                    }
                });
                dialog.dismiss();
            }
        });
        dialog.show();
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
                int orderItemId = mReconciliationDetailAdapter.getItem(position).getId();
                OkHttpUtils.post().url(CONFIRM_PAYMENT)
                        .addHeader(ACCESS_TOKEN, accessToken)
                        .addParams("orderItemId", orderItemId + "")
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
                                mReconciliationDetailAdapter.getItem(position).setPaymentConfirmStatus("confirmed");
                                mReconciliationDetailAdapter.getItem(position).setPaymentConfirmStatusName("已确认");
                                mReconciliationDetailAdapter.notifyDataSetChanged();
                                break;
                            case "failure":
                                Util.showError(ReconciliationDetailActivity.this, errorBean.getReason());
                                break;
                        }
                        dialog.dismiss();
                    }
                });
            }
        });
        dialog.show();
    }


}
