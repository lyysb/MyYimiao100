package com.yimiao100.sale.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.yimiao100.sale.R;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.bean.UserBean;
import com.yimiao100.sale.utils.Constant;
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
 * 推广主体
 */
public class BindPromotionActivity extends BaseActivity implements TitleView
        .TitleBarOnClickListener {

    @BindView(R.id.promotion_title)
    TitleView mPromotionTitle;
    @BindView(R.id.promotion_bind_company)
    ImageView mPromotionBindCompany;
    @BindView(R.id.promotion_company_bank_name)
    TextView mPromotionCompanyBankName;
    @BindView(R.id.promotion_company)
    TextView mPromotionCompany;
    @BindView(R.id.promotion_company_bank_number)
    TextView mPromotionCompanyBankNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promotion_bind);
        ButterKnife.bind(this);

        mPromotionTitle.setOnTitleBarClick(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        initData();
    }

    private void initData() {

        boolean corporate_exit = (boolean) SharePreferenceUtil.get(getApplicationContext(),
                Constant.CORPORATE_EXIT, false);
        String accountName = (String) SharePreferenceUtil.get(getApplicationContext(),
                Constant.CORPORATE_ACCOUNT_NAME, "");
        String corporate_bank_number = (String) SharePreferenceUtil.get(getApplicationContext(),
                Constant.CORPORATE_ACCOUNT_NUMBER, "");
        if (corporate_exit) {
            //如果已经存在对公账户
            mPromotionBindCompany.setBackgroundResource(R.mipmap.ico_bank_card_activation);
            mPromotionCompanyBankName.setText(accountName);
            mPromotionCompanyBankNumber.setText(corporate_bank_number);
        } else {
            mPromotionBindCompany.setBackgroundResource(R.mipmap.ico_bank_card_default);
        }

        mPromotionCompanyBankName.setVisibility(corporate_exit ? View.VISIBLE : View.INVISIBLE);
        mPromotionCompany.setVisibility(corporate_exit ? View.VISIBLE : View.INVISIBLE);
        mPromotionCompanyBankNumber.setVisibility(corporate_exit ? View.VISIBLE : View.INVISIBLE);
        //重新请求网络，获取推广账户信息
        OkHttpUtils.post().url(Constant.BASE_URL + "/api/user/get_user_account")
                .addHeader("X-Authorization-Token",
                        (String) SharePreferenceUtil.get(getApplicationContext(), Constant
                                .ACCESSTOKEN, ""))
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.d("推广主体E：" + e.getLocalizedMessage());
                Util.showTimeOutNotice(currentContext);
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.d("推广主体E：" + response);
                UserBean userBean = JSON.parseObject(response, UserBean.class);
                switch (userBean.getStatus()) {
                    case "success":
                        if (userBean.getUserAccount().getCorporate() != null) {
                            //更新本地对公账户的记录
                            refreshCorporate(userBean);
                        }
                        break;
                    case "failure":
                        Util.showError(currentContext, userBean.getReason());
                        break;
                }
            }
        });
    }

    /**
     * 刷新账户本地保存信息
     * @param userBean
     */
    private void refreshCorporate(UserBean userBean) {
        //记录对公账户存在
        SharePreferenceUtil.put(getApplicationContext(),
                Constant.CORPORATE_EXIT, true);
        //对公账户-开户名称
        SharePreferenceUtil.put(getApplicationContext(),
                Constant.CORPORATE_ACCOUNT_NAME,
                userBean.getUserAccount().getCorporate().getAccountName());
        //对公账户-公司账号
        SharePreferenceUtil.put(getApplicationContext(),
                Constant.CORPORATE_ACCOUNT_NUMBER,
                userBean.getUserAccount().getCorporate().getCorporateAccount
                        ());
        //对公账户-开户银行
        SharePreferenceUtil.put(getApplicationContext(),
                Constant.CORPORATE_BANK_NAME,
                userBean.getUserAccount().getCorporate().getBankName());
        //对公账户-公司电话号码
        SharePreferenceUtil.put(getApplicationContext(),
                Constant.CORPORATE_PHONE_NUMBER,
                userBean.getUserAccount().getCorporate().getCorporatePhoneNumber());

        //对公账户-企业营业执照地址
        SharePreferenceUtil.put(getApplicationContext(),
                Constant.CORPORATE_BIZ_LICENCE_URL,
                userBean.getUserAccount().getCorporate().getBizLicenceUrl());

        //对公账户-姓名
        SharePreferenceUtil.put(getApplicationContext(),
                Constant.CORPORATE_CN_NAME,
                userBean.getUserAccount().getCorporate().getCnName());
        //对公账户-身份证号
        SharePreferenceUtil.put(getApplicationContext(),
                Constant.CORPORATE_ID_NUMBER,
                userBean.getUserAccount().getCorporate().getIdNumber());
        //对公账户-电话
        SharePreferenceUtil.put(getApplicationContext(),
                Constant.CORPORATION_PERSONAL_PHONE_NUMBER,
                userBean.getUserAccount().getCorporate()
                        .getPersonalPhoneNumber());
        //对公账户-QQ
        SharePreferenceUtil.put(getApplicationContext(), Constant.CORPORATE_QQ, userBean.getUserAccount().getCorporate().getQq());
        //对公账户-邮箱
        SharePreferenceUtil.put(getApplicationContext(), Constant.CORPORATE_EMAIL,
                userBean.getUserAccount().getCorporate().getEmail());
        //对公账户-证件照1
        SharePreferenceUtil.put(getApplicationContext(), Constant.CORPORATE_PERSONAL_URL,
                userBean.getUserAccount().getCorporate().getPersonalPhotoUrl());
        //对公账户-证件照2
        SharePreferenceUtil.put(getApplicationContext(), Constant.CORPORATE_ID_URL,
                userBean.getUserAccount().getCorporate().getIdPhotoUrl());

        //对公账户-审核状态
        SharePreferenceUtil.put(getApplicationContext(), Constant.CORPORATE_ACCOUNT_STATUS,
                userBean.getUserAccount().getCorporate().getAccountStatus());
    }


    @OnClick({R.id.promotion_bind_company})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.promotion_bind_company:
                startActivity(new Intent(this, BindCompanyActivity.class));
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
