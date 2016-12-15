package com.yimiao100.sale.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.meiqia.core.callback.OnInitCallback;
import com.meiqia.meiqiasdk.util.MQConfig;
import com.meiqia.meiqiasdk.util.MQIntentBuilder;
import com.yimiao100.sale.R;
import com.yimiao100.sale.adapter.listview.ReconciliationDetailAdapter;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.bean.ErrorBean;
import com.yimiao100.sale.bean.ReconciliationDetail;
import com.yimiao100.sale.bean.ReconciliationDetailBean;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.SharePreferenceUtil;
import com.yimiao100.sale.utils.Util;
import com.yimiao100.sale.view.TitleView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

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
    private String mOrderId;
    private String mAccessToken;

    private final String BALANCE_ORDER_DETAIL = "/api/order/balance_order_detail";

    //订单条目发货确认
    private final String CONFIRM_DELIVERY = "/api/order/confirm_delivery";
    //订单条目回款确认
    private final String CONFIRM_PAYMENT = "/api/order/confirm_payment";
    private ArrayList<ReconciliationDetail> mOrderItemList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reconciliation_detail);
        ButterKnife.bind(this);


        initView();

        initData();
    }

    private void initView() {
        mReconciliationDetailTitle.setOnTitleBarClick(this);
        mAccessToken = (String) SharePreferenceUtil.get(getApplicationContext(), Constant.ACCESSTOKEN, "");

        Intent intent = getIntent();
        mOrderId = intent.getStringExtra("orderId");
        Bundle bundle = intent.getExtras();
        String categoryName = bundle.getString("categoryName");
        String productName = bundle.getString("productName");
        String vendorName = bundle.getString("vendorName");
        String customerName = bundle.getString("customerName");
        String dosageForm = bundle.getString("dosageForm");
        String spec = bundle.getString("spec");
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
        OkHttpUtils.post().url(Constant.BASE_URL + BALANCE_ORDER_DETAIL)
                .addHeader("X-Authorization-Token", mAccessToken)
                .addParams("orderId", mOrderId)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.d("对账详情E：" + e.getMessage());
                Util.showTimeOutNotice(currentContext);
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.d("对账详情：" + response);
                ReconciliationDetailBean reconciliationDetailBean = JSON.parseObject(response, ReconciliationDetailBean.class);
                switch (reconciliationDetailBean.getStatus()) {
                    case "success":
                        //订单条目列表
                        mOrderItemList = reconciliationDetailBean.getOrderItemList();
                        mReconciliationDetailAdapter = new ReconciliationDetailAdapter(getApplicationContext(), mOrderItemList);
                        mReconciliationDetailAdapter.setOnStatusClickListener(ReconciliationDetailActivity.this);
                        mReconciliationDetailListView.setAdapter(mReconciliationDetailAdapter);
                        break;
                    case "failure":
                        Util.showError(ReconciliationDetailActivity.this, reconciliationDetailBean.getReason());
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
                enterCustomerService();
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
                String confirm_delivery_url = Constant.BASE_URL + CONFIRM_DELIVERY;
                OkHttpUtils.post().url(confirm_delivery_url)
                        .addHeader("X-Authorization-Token", mAccessToken)
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
                enterCustomerService();
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
                String confirm_payment_url = Constant.BASE_URL + CONFIRM_PAYMENT;
                OkHttpUtils.post().url(confirm_payment_url)
                        .addHeader("X-Authorization-Token", mAccessToken)
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

}
