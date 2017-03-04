package com.yimiao100.sale.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.yimiao100.sale.R;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.bean.ErrorBean;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.FormatUtils;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.SharePreferenceUtil;
import com.yimiao100.sale.utils.ToastUtil;
import com.yimiao100.sale.utils.Util;
import com.yimiao100.sale.view.TitleView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 保证金提现完成界面
 */
public class AssuranceCompanyActivity extends BaseActivity implements TitleView
        .TitleBarOnClickListener {

    @BindView(R.id.assurance_apply_title)
    TitleView mAssuranceApplyTitle;
    @BindView(R.id.assurance_company_account)
    TextView mAssuranceCompanyAccount;
    @BindView(R.id.assurance_company_phone)
    TextView mAssuranceCompanyPhone;
    @BindView(R.id.assurance_apply_cash)
    Button mAssuranceApplyCash;
    private final String URL_CASH_WITHDRAWAL = Constant.BASE_URL +
            "/api/fund/deposit_cash_withdrawal";

    private final String ORDER_ID = "orderIds";

    private String mOrderIds;
    private String mUserAccountType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assurance_company);
        ButterKnife.bind(this);

        initView();

        initData();
    }

    private void initView() {
        mAssuranceApplyTitle.setOnTitleBarClick(this);
    }

    private void initData() {
        Intent intent = getIntent();
        double applyNum = intent.getDoubleExtra("applyNum", -1);
        mAssuranceCompanyAccount.setText("申请推广保证金提现：" + FormatUtils.MoneyFormat(applyNum) + "元");
        mUserAccountType = intent.getStringExtra("userAccountType");
        String phone = null;
        switch (mUserAccountType) {
            case "personal":
                phone = (String) SharePreferenceUtil.get(this, Constant.PERSONAL_PHONE_NUMBER, "");
                break;
            case "corporate":
                phone = (String) SharePreferenceUtil.get(this, Constant.CORPORATION_PERSONAL_PHONE_NUMBER, "");
                break;
        }
        LogUtil.Companion.d(phone);
        mAssuranceCompanyPhone.setText("联系方式：" + phone);

        mOrderIds = intent.getStringExtra("orderIds");
        LogUtil.Companion.d("mOrderIds:" + mOrderIds);
    }

    @OnClick({R.id.assurance_apply_cash, R.id.assurance_apply_service})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.assurance_apply_cash:
                // 提示确认提现
                showDialog();
                break;
            case R.id.assurance_apply_service:
                //打开客服
                Util.enterCustomerService(this);
                break;
        }
    }


    /**
     * 提示确认提现
     */
    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AssuranceCompanyActivity.this, R
                .style.dialog);
        View v = View.inflate(this, R.layout.dialog_confirm_promotion, null);
        builder.setView(v);
        builder.setCancelable(false);
        TextView msg = (TextView) v.findViewById(R.id.dialog_msg);

        switch (mUserAccountType) {
            case "personal":
                msg.setText("您申请的提现金额，将在工作人员收到提现\n申请之后5个工作日内，退回到您原来的\n推广主体个人账号里，请注意查收。");
                break;
            case "corporate":
                msg.setText("您申请的提现金额，将在工作人员收到提现\n申请之后5个工作日内，退回到您原来的\n推广主体对公账号里，请注意查收。");
                break;
        }
        Button btn1 = (Button) v.findViewById(R.id.dialog_promotion_bt1);
        Button btn2 = (Button) v.findViewById(R.id.dialog_promotion_bt2);
        final AlertDialog dialog = builder.create();
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                applyCash();
            }
        });
        dialog.show();
    }

    /**
     * 提交申请
     */
    private void applyCash() {
        //禁止点击按钮，避免重复点击造成重复请求
        mAssuranceApplyCash.setEnabled(false);
        OkHttpUtils.post().url(URL_CASH_WITHDRAWAL).addHeader(ACCESS_TOKEN, mAccessToken)
                .addParams(ORDER_ID, mOrderIds).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                //再次设置按钮可以点击
                mAssuranceApplyCash.setEnabled(true);
                LogUtil.Companion.d("保证金提现E：" + e.getMessage() + e.getLocalizedMessage());
                Util.showTimeOutNotice(currentContext);
            }

            @Override
            public void onResponse(String response, int id) {
                //再次设置按钮可以点击
                mAssuranceApplyCash.setEnabled(true);
                LogUtil.Companion.d("保证金提现：" + response);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        ToastUtil.showShort(currentContext, "申请成功");
                        //申请提现成功，返回财富列表
                        startActivity(new Intent(getApplicationContext(), RichesActivity.class));
                        break;
                    case "failure":
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
}
