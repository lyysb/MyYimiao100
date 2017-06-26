package com.yimiao100.sale.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yimiao100.sale.R;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.DataUtil;
import com.yimiao100.sale.utils.SharePreferenceUtil;
import com.yimiao100.sale.view.TitleView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 推广主体
 */
public class BindPromotionActivity extends BaseActivity implements TitleView
        .TitleBarOnClickListener {


    @BindView(R.id.promotion_title)
    TitleView mTitle;
    @BindView(R.id.promotion_company_bank_name)
    TextView mCompanyBankName;
    @BindView(R.id.promotion_company_bank_number)
    TextView mCompanyBankNumber;
    @BindView(R.id.promotion_company_exit)
    RelativeLayout mCompanyExit;
    @BindView(R.id.promotion_company_null)
    RelativeLayout mCompanyNull;
    @BindView(R.id.promotion_personal_name)
    TextView mPersonalName;
    @BindView(R.id.promotion_personal_number)
    TextView mPersonalNumber;
    @BindView(R.id.promotion_personal_exit)
    RelativeLayout mPersonalExit;
    @BindView(R.id.promotion_personal_null)
    RelativeLayout mPersonalNull;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promotion_bind);
        ButterKnife.bind(this);
        mTitle.setOnTitleBarClick(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initData();
    }

    private void initData() {
        initCorporateData();

        initPersonalData();
        //请求网络，获取推广账户信息
        DataUtil.updateUserAccount(accessToken);
    }

    private void initCorporateData() {
        boolean corporateExit = (boolean) SharePreferenceUtil.get(currentContext, Constant.CORPORATE_EXIT, false);
        String accountName = (String) SharePreferenceUtil.get(currentContext,
                Constant.CORPORATE_ACCOUNT_NAME, "");
        String corporateBankNumber = (String) SharePreferenceUtil.get(currentContext,
                Constant.CORPORATE_ACCOUNT_NUMBER, "");
        // 主体数据的显示
        mCompanyExit.setVisibility(corporateExit ? View.VISIBLE : View.GONE);
        mCompanyNull.setVisibility(corporateExit ? View.GONE : View.VISIBLE);
        if (corporateExit) {
            mCompanyBankName.setText(accountName);
            mCompanyBankNumber.setText(corporateBankNumber);
        }
    }

    private void initPersonalData() {
        boolean personalExit = (boolean) SharePreferenceUtil.get(currentContext, Constant.PERSONAL_EXIT, false);
        String cnName = (String) SharePreferenceUtil.get(currentContext, Constant.PERSONAL_CN_NAME, "");
        String bankCardNumber = (String) SharePreferenceUtil.get(currentContext, Constant.PERSONAL_BANK_CARD_NUMBER, "");
        // 主体数据的显示
        mPersonalExit.setVisibility(personalExit ? View.VISIBLE : View.GONE);
        mPersonalNull.setVisibility(personalExit ? View.GONE : View.VISIBLE);
        if (personalExit) {
            mPersonalName.setText(cnName);
            mPersonalNumber.setText(bankCardNumber);
        }
    }


    @OnClick({R.id.promotion_company, R.id.promotion_personal})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.promotion_company:
                startActivity(new Intent(this, BindCompanyActivity.class));
                break;
            case R.id.promotion_personal:
                startActivity(new Intent(this, BindPersonalActivity.class));
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
}
