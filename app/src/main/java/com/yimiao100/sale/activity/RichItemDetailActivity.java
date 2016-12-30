package com.yimiao100.sale.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.meiqia.core.callback.OnInitCallback;
import com.meiqia.meiqiasdk.util.MQConfig;
import com.meiqia.meiqiasdk.util.MQIntentBuilder;
import com.yimiao100.sale.R;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.bean.AccountDetail;
import com.yimiao100.sale.bean.ErrorBean;
import com.yimiao100.sale.bean.RichesItemDetailBean;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.FormatUtils;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.SharePreferenceUtil;
import com.yimiao100.sale.utils.TimeUtil;
import com.yimiao100.sale.utils.Util;
import com.yimiao100.sale.view.TitleView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;


/**
 * 提现明细详情
 */
public class RichItemDetailActivity extends BaseActivity implements TitleView
        .TitleBarOnClickListener {

    @BindView(R.id.rich_item_title)
    TitleView mRichItemTitle;
    @BindView(R.id.rich_item_amount)
    TextView mRichItemAmount;
    @BindView(R.id.rich_item_time)
    TextView mRichItemTime;
    @BindView(R.id.rich_item_type)
    TextView mRichItemType;
    @BindView(R.id.rich_item_customer_name)
    TextView mRichItemCustomerName;
    @BindView(R.id.rich_item_product_name)
    TextView mRichItemProductName;
    @BindView(R.id.rich_item_dosage_name)
    TextView mRichItemDosageName;
    @BindView(R.id.rich_item_spec)
    TextView mRichItemSpec;
    @BindView(R.id.rich_item_serial_no)
    TextView mRichItemSerialNo;
    @BindView(R.id.rich_item_ll)
    LinearLayout mRichItemLl;
    @BindView(R.id.rich_item_vendor)
    TextView mRichItemVendor;

    private final String URL_ACCOUNT_DETAIL = Constant.BASE_URL + "/api/fund/account_detail";
    private final String ACCESS_TOKEN = "X-Authorization-Token";
    private final String ID = "id";

    private String mAccessToken;
    private int mId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rich_item_detail);
        ButterKnife.bind(this);

        mAccessToken = (String) SharePreferenceUtil.get(this, Constant.ACCESSTOKEN, "");

        mId = getIntent().getIntExtra("id", -1);

        initView();

        initData();
    }

    private void initView() {
        mRichItemTitle.setOnTitleBarClick(this);
    }

    private void initData() {
        OkHttpUtils.post().url(URL_ACCOUNT_DETAIL).addHeader(ACCESS_TOKEN, mAccessToken)
                .addParams(ID, mId + "").build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.Companion.d("提现明细详情E：" + e.getLocalizedMessage());
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.Companion.d("提现明细详情：" + response);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        AccountDetail accountDetail = JSON.parseObject(response,
                                RichesItemDetailBean.class).getAccountDetail();
                        //初始化显示内容
                        initDetailData(accountDetail);
                        break;
                    case "failure":
                        Util.showError(RichItemDetailActivity.this, errorBean.getReason());
                        break;
                }
            }
        });
    }

    /**
     * 初始化详情数据
     *
     * @param accountDetail
     */
    private void initDetailData(AccountDetail accountDetail) {
        mRichItemAmount.setText(FormatUtils.MoneyFormat(accountDetail.getAmount()));
        mRichItemTime.setText(TimeUtil.timeStamp2Date(accountDetail.getCreatedAt() + "",
                "yyyy年MM月dd日 HH：mm"));

        mRichItemLl.setVisibility(accountDetail.getSerialNo() == null ? View.GONE : View.VISIBLE);
        mRichItemType.setText(accountDetail.getTransactionTypeName());
        mRichItemCustomerName.setText(accountDetail.getCustomerName());
        mRichItemProductName.setText(accountDetail.getCategoryName());
        mRichItemDosageName.setText(accountDetail.getDosageForm());
        mRichItemSpec.setText(accountDetail.getSpec());
        mRichItemVendor.setText(accountDetail.getVendorName());
        mRichItemSerialNo.setText(accountDetail.getSerialNo());
    }

    @OnClick(R.id.rich_item_apply_service)
    public void onClick() {
        enterCustomerService();
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
