package com.yimiao100.sale.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.yimiao100.sale.R;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.bean.ErrorBean;
import com.yimiao100.sale.bean.Tax;
import com.yimiao100.sale.bean.TaxBean;
import com.yimiao100.sale.bean.UserFundBean;
import com.yimiao100.sale.bean.UserFundNew;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.FormatUtils;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.SharePreferenceUtil;
import com.yimiao100.sale.utils.Util;
import com.yimiao100.sale.view.TitleView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;


/**
 * 财富
 */
public class RichesActivity extends BaseActivity implements TitleView.TitleBarOnClickListener {

    @BindView(R.id.riches_title)
    TitleView mRichesTitle;
    @BindView(R.id.riches_total_amount)
    TextView mRichesTotalAmount;
    @BindView(R.id.riches_integral_count)
    TextView mRichesIntegralCount;
    @BindView(R.id.riches_scholarship_count)
    TextView mRichesScholarshipCount;
    @BindView(R.id.riches_promotion_count)
    TextView mRichesPromotionCount;
    @BindView(R.id.riches_assurance_count)
    TextView mRichesAssuranceCount;


    private final String URL_USER_FUND = Constant.BASE_URL + "/api/fund/user_fund";
    private final String URL_TEX = Constant.BASE_URL + "/api/tax/default";
    private final String MODULE_TYPE = "moduleType";
    private final String EXAM_REWARD_WITHDRAWAL = "exam_reward_withdrawal"; //课程考试奖励可提现
    private final String SALE_WITHDRAWAL = "sale_withdrawal";               //销售资金可提现
    private final String DEPOSIT_WITHDRAWAL = "deposit_withdrawal";         //保证金可提现


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riches);
        ButterKnife.bind(this);

        initView();

        initTax();
    }

    private void initTax() {
        OkHttpUtils.post().url(URL_TEX).addHeader(ACCESS_TOKEN, mAccessToken)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.Companion.d("tax error is :");
                e.printStackTrace();
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.Companion.d("tax :" + response);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        Tax tax = JSON.parseObject(response, TaxBean.class).getTax();
                        double taxRate = tax.getTaxRate() / 100;
                        // 保存税率
                        SharePreferenceUtil.put(currentContext, Constant.TAX_RATE, taxRate);
                        break;
                    case "failure":
                        Util.showError(currentContext, errorBean.getReason());
                        break;
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        showLoadingProgress();
        initData();
    }

    private void initView() {
        mRichesTitle.setOnTitleBarClick(this);
    }

    private void initData() {
        OkHttpUtils.post().url(URL_USER_FUND).addHeader(ACCESS_TOKEN, mAccessToken)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.Companion.d("财富E：" + e.getMessage());
                Util.showTimeOutNotice(currentContext);
                hideLoadingProgress();
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.Companion.d("财富：" + response);
                hideLoadingProgress();
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        UserFundNew userFund = JSON.parseObject(response, UserFundBean.class)
                                .getUserFund();
                        //用户账户总金额-double
                        SharePreferenceUtil.put(getApplicationContext(), Constant.TOTAL_AMOUNT,
                                userFund.getTotalAmount());
                        //用户积分-int
                        SharePreferenceUtil.put(getApplicationContext(), Constant.INTEGRAL,
                                userFund.getIntegral());
                        //用户奖学金-double
                        SharePreferenceUtil.put(getApplicationContext(), Constant.TOTAL_EXAM_REWARD,
                                userFund.getTotalExamReward());
                        //用户推广费-double
                        SharePreferenceUtil.put(getApplicationContext(), Constant.TOTAL_SALE,
                                userFund.getTotalSale());
                        //用户保证金-double
                        SharePreferenceUtil.put(getApplicationContext(), Constant.DEPOSIT,
                                userFund.getDeposit());
                        //账户总金额
                        mRichesTotalAmount.setText("￥" + FormatUtils.MoneyFormat(userFund ==
                                null ? 0.0 : userFund.getTotalAmount()));

                        //设置我的积分
                        mRichesIntegralCount.setText(FormatUtils.NumberFormat(userFund == null ?
                                0 : userFund.getIntegral()) + "积分");
                        //设置奖学金
                        mRichesScholarshipCount.setText("￥" + FormatUtils.MoneyFormat(userFund ==
                                null ? 0.0 : userFund.getTotalExamReward()));
                        //设置推广费
                        mRichesPromotionCount.setText("￥" + FormatUtils.MoneyFormat(userFund ==
                                null ? 0.0 : userFund.getTotalSale()));
                        //设置保证金
                        mRichesAssuranceCount.setText("￥" + FormatUtils.MoneyFormat(userFund ==
                                null ? 0.0 : userFund.getDeposit()));
                        break;
                    case "failure":
                        Util.showError(RichesActivity.this, errorBean.getReason());
                        break;
                }
            }
        });
    }


    @OnClick({R.id.riches_integral, R.id.riches_scholarship, R.id.riches_promotion, R.id
            .riches_assurance})
    public void onClick(View view) {
        Intent vendorIntent = new Intent(this, VendorListActivity.class);
        switch (view.getId()) {
            case R.id.riches_integral:
                //进入我的积分
                startActivity(new Intent(this, IntegralActivity.class));
                break;
            case R.id.riches_scholarship:
                //进入奖学金厂家列表
                vendorIntent.putExtra(MODULE_TYPE, EXAM_REWARD_WITHDRAWAL);
                startActivity(vendorIntent);
                break;
            case R.id.riches_promotion:
                //进入推广费厂家列表
                vendorIntent.putExtra(MODULE_TYPE, SALE_WITHDRAWAL);
                startActivity(vendorIntent);
                break;
            case R.id.riches_assurance:
                //进入保证金厂家列表
                vendorIntent.putExtra(MODULE_TYPE, DEPOSIT_WITHDRAWAL);
                startActivity(vendorIntent);
                break;
        }
    }

    @Override
    public void rightOnClick() {
        //查看明细
        startActivity(new Intent(this, RichesDetailActivity.class));
    }

    @Override
    public void leftOnClick() {
        finish();
    }
}
