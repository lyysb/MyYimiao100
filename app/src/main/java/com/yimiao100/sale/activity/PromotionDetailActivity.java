package com.yimiao100.sale.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.yimiao100.sale.R;
import com.yimiao100.sale.adapter.listview.PromotionDetailAdapter;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.bean.ReconciliationDetail;
import com.yimiao100.sale.bean.ReconciliationDetailBean;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.Util;
import com.yimiao100.sale.view.TitleView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

/**
 * 推广费提现-条目详情
 */
public class PromotionDetailActivity extends BaseActivity implements TitleView
        .TitleBarOnClickListener {

    @BindView(R.id.promotion_detail_title)
    TitleView mPromotionDetailTitle;
    @BindView(R.id.promotion_detail_list)
    ListView mPromotionDetailListView;
    private TextView mHeadPromotionProductFormalName;
    private TextView mHeadPromotionVendorName;
    private TextView mHeadPromotionCustomerName;
    private TextView mHeadPromotionProductCommonName;
    private TextView mHeadPromotionDosageForm;
    private TextView mHeadPromotionSpec;
    private TextView mHeadPromotionSerialNo;

    private final String URL_ORDER_DETAIL = Constant.BASE_URL + "/api/fund/sale_order_detail";
    private final String ORDER_ID = "orderId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promotion_detail);
        ButterKnife.bind(this);

        showLoadingProgress();

        initView();

        initData();
    }

    private void initView() {
        mPromotionDetailTitle.setOnTitleBarClick(this);

        View view = LayoutInflater.from(this).inflate(R.layout.head_promotion_detail, null);
        mPromotionDetailListView.addHeaderView(view);

        //产品名
        mHeadPromotionProductFormalName = (TextView) view.findViewById(R.id
                .head_promotion_product_formal_name);
        //厂家名
        mHeadPromotionVendorName = (TextView) view.findViewById(R.id.head_promotion_vendor_name);
        //客户名
        mHeadPromotionCustomerName = (TextView) view.findViewById(R.id
                .head_promotion_customer_name);
        //分类名
        mHeadPromotionProductCommonName = (TextView) view.findViewById(R.id
                .head_promotion_product_common_name);
        //剂型
        mHeadPromotionDosageForm = (TextView) view.findViewById(R.id.head_promotion_dosage_form);
        //规格
        mHeadPromotionSpec = (TextView) view.findViewById(R.id.head_promotion_spec);
        //订单号
        mHeadPromotionSerialNo = (TextView) view.findViewById(R.id.head_promotion_serial_no);

    }

    private void initData() {
        Intent intent = getIntent();
        mHeadPromotionProductFormalName.setText(intent.getStringExtra("productName"));
        mHeadPromotionVendorName.setText(intent.getStringExtra("vendorName"));
        mHeadPromotionCustomerName.setText(intent.getStringExtra("customerName"));
        mHeadPromotionProductCommonName.setText(intent.getStringExtra("categoryName"));
        mHeadPromotionDosageForm.setText("剂型：" + intent.getStringExtra("dosageForm"));
        mHeadPromotionSpec.setText("规格：" + intent.getStringExtra("spec"));
        mHeadPromotionSerialNo.setText("协议单号：" + intent.getStringExtra("serialNo"));

        String orderId = intent.getStringExtra("orderId");

        OkHttpUtils.post().url(URL_ORDER_DETAIL).addHeader(ACCESS_TOKEN, mAccessToken)
                .addParams(ORDER_ID, orderId)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.Companion.d("推广费提现详情E：" + e.getLocalizedMessage());
                hideLoadingProgress();
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.Companion.d("推广费提现详情：" + response);
                hideLoadingProgress();
                ReconciliationDetailBean promotionDetailBean = JSON.parseObject(response,
                        ReconciliationDetailBean.class);
                switch (promotionDetailBean.getStatus()) {
                    case "success":
                        ArrayList<ReconciliationDetail> promotionList = promotionDetailBean
                                .getOrderItemList();
                        PromotionDetailAdapter promotionDetailAdapter = new
                                PromotionDetailAdapter(promotionList);
                        mPromotionDetailListView.setAdapter(promotionDetailAdapter);
                        break;
                    case "failure":
                        Util.showError(PromotionDetailActivity.this, promotionDetailBean
                                .getReason());
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
}
