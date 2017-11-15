package com.yimiao100.sale.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yimiao100.sale.R;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.bean.AccountDetail;
import com.yimiao100.sale.bean.ErrorBean;
import com.yimiao100.sale.bean.RichesItemDetailBean;
import com.yimiao100.sale.ext.JSON;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.FormatUtils;
import com.yimiao100.sale.utils.LogUtil;
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
    @BindView(R.id.rich_item_2)
    LinearLayout mRichItemL2;
    @BindView(R.id.rich_item_customer)
    LinearLayout customerView;

    @BindView(R.id.rich_item_1)
    TextView mRichItem1;
    @BindView(R.id.rich_item_3)
    TextView mRichItem3;

    @BindView(R.id.rich_item_vendor)
    TextView mRichItemVendor;

    private final String URL_ACCOUNT_DETAIL = Constant.BASE_URL + "/api/fund/account_detail";
    private final String ID = "id";

    private int mId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rich_item_detail);
        ButterKnife.bind(this);

        showLoadingProgress();

        mId = getIntent().getIntExtra("id", -1);

        initView();

        initData();
    }

    private void initView() {
        mRichItemTitle.setOnTitleBarClick(this);
    }

    private void initData() {
        OkHttpUtils.post().url(URL_ACCOUNT_DETAIL).addHeader(ACCESS_TOKEN, accessToken)
                .addParams(ID, mId + "").build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.d("提现明细详情E：" + e.getLocalizedMessage());
                hideLoadingProgress();
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.d("提现明细详情：" + response);
                hideLoadingProgress();
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
        mRichItemAmount.setText(FormatUtils.MoneyFormat(accountDetail.getAmount()) + "元");
        mRichItemTime.setText(TimeUtil.timeStamp2Date(accountDetail.getCreatedAt() + "",
                "yyyy年MM月dd日 HH：mm"));

        mRichItemLl.setVisibility(accountDetail.getSerialNo() == null ? View.GONE : View.VISIBLE);
        switch (accountDetail.getBizType()) {
            case "insurance":
                mRichItem1.setText("保险公司：");
                mRichItemL2.setVisibility(View.GONE);
                mRichItem3.setText("保险名称：");
                mRichItemProductName.setText(accountDetail.getProductName());
                mRichItemVendor.setText(accountDetail.getCompanyName());
                break;
            case "vaccine":
                mRichItem1.setText("厂家：");
                mRichItemL2.setVisibility(View.VISIBLE);
                mRichItem3.setText("产品");
                mRichItemProductName.setText(accountDetail.getCategoryName());
                mRichItemVendor.setText(accountDetail.getVendorName());
                break;
            default:
                LogUtil.d("unknownType");
                mRichItemLl.setVisibility(View.GONE);
                break;
        }
        mRichItemType.setText(accountDetail.getTransactionTypeName());
        if (accountDetail.getCustomerName() == null) {
            customerView.setVisibility(View.GONE);
        } else {
            customerView.setVisibility(View.VISIBLE);
            mRichItemCustomerName.setText(accountDetail.getCustomerName());
        }
        mRichItemDosageName.setText(accountDetail.getDosageForm());
        mRichItemSpec.setText(accountDetail.getSpec());
        mRichItemSerialNo.setText(accountDetail.getSerialNo());
    }

    @OnClick(R.id.rich_item_apply_service)
    public void onClick() {
        Util.enterCustomerService(this);
    }


    @Override
    public void leftOnClick() {
        finish();
    }

    @Override
    public void rightOnClick() {

    }
}
