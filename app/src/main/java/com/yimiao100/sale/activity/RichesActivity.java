package com.yimiao100.sale.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yimiao100.sale.R;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.bean.*;
import com.yimiao100.sale.ext.JSON;
import com.yimiao100.sale.insurance.RichInsActivity;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.FormatUtils;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.SharePreferenceUtil;
import com.yimiao100.sale.utils.Util;
import com.yimiao100.sale.vaccine.RichVaccineActivity;
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
    @BindView(R.id.riches_vaccine_count)
    TextView mRichesVaccineCount;
    @BindView(R.id.riches_insurance_count)
    TextView mRichesInsuranceCount;
    @BindView(R.id.riches_model_ins)
    RelativeLayout mRichesModelIns;


    private final String URL_USER_FUND = Constant.BASE_URL + "/api/fund/user_fund";
    private final String URL_USER_FUND_ALL = Constant.BASE_URL + "/api/fund/user_fund_all";
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

//        initTax();
    }

    private void initTax() {
        OkHttpUtils.post().url(URL_TEX).addHeader(ACCESS_TOKEN, accessToken)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.d("tax error is :");
                e.printStackTrace();
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.d("tax :" + response);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        Tax tax = JSON.parseObject(response, TaxBean.class).getTax();
                        double taxRate;
                        if (tax != null) {
                            taxRate = tax.getTaxRate() / 100;
                        } else {
                            // 错误税率
                            taxRate = -1;
                        }
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
        if (Constant.isInsurance) {
            // 开启保险
            mRichesModelIns.setVisibility(View.VISIBLE);
        } else {
            // 关闭保险
            mRichesModelIns.setVisibility(View.GONE);
        }
    }

    private void initData() {
        OkHttpUtils.post().url(URL_USER_FUND_ALL).addHeader(ACCESS_TOKEN, accessToken)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.d("财富E：" + e.getMessage());
                Util.showTimeOutNotice(currentContext);
                hideLoadingProgress();
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.d("财富：\n" + response);
                hideLoadingProgress();
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        UserFundAll userFundAll = JSON.parseObject(response, UserFundBean.class).getUserFundAll();
                        if (userFundAll != null) {
                            saveToLocal(userFundAll);
                        }
                        showDataAtView(userFundAll);
                        break;
                    case "failure":
                        Util.showError(RichesActivity.this, errorBean.getReason());
                        break;
                }
            }
        });
    }

    private void saveToLocal(UserFundAll userFundAll) {
        // 账户总额度
        SharePreferenceUtil.put(currentContext, Constant.TOTAL_AMOUNT, userFundAll.getTotalAmount());
        // 我的积分
        SharePreferenceUtil.put(currentContext, Constant.INTEGRAL, userFundAll.getIntegral());
    }

    private void showDataAtView(UserFundAll userFundAll) {
        // 显示账户总金额
        mRichesTotalAmount.setText(FormatUtils.RMBFormat(userFundAll == null ? 0.0 : userFundAll.getTotalAmount()));
        // 显示我的积分
        mRichesIntegralCount.setText(FormatUtils.NumberFormat(userFundAll == null ? 0 : userFundAll.getIntegral()) + "积分");
        // 显示疫苗可提现
        mRichesVaccineCount.setText(FormatUtils.RMBFormat(userFundAll == null ? 0.0 : userFundAll.getVaccineTotalWithdraw()));
        // 显示保险可提现
        mRichesInsuranceCount.setText(FormatUtils.RMBFormat(userFundAll == null ? 0.0 : userFundAll.getInsuranceTotalWithdraw()));
    }


    @OnClick({R.id.riches_integral, R.id.riches_vaccine, R.id.riches_insurance
            /*,R.id.riches_scholarship, R.id.riches_promotion, R.id
            .riches_assurance*/})
    public void onClick(View view) {
        Intent vendorIntent = new Intent(this, VendorListActivity.class);
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.riches_integral:
                //进入我的积分
                intent.setClass(this, IntegralActivity.class);
                startActivity(intent);
                break;
            case R.id.riches_vaccine:
                // 进入疫苗财富页面
                intent.setClass(this, RichVaccineActivity.class);
                startActivity(intent);
                break;
            case R.id.riches_insurance:
                // 进入保险财富页面
                intent.setClass(this, RichInsActivity.class);
                startActivity(intent);
                break;
            /*case R.id.riches_scholarship:
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
                break;*/
        }
    }

    @Override
    public void rightOnClick() {
        //查看明细
        startActivity(new Intent(this, RichDetailActivity.class));
    }

    @Override
    public void leftOnClick() {
        finish();
    }
}
