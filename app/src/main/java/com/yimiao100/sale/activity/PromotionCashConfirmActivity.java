package com.yimiao100.sale.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.yimiao100.sale.R;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.bean.ErrorBean;
import com.yimiao100.sale.ext.JSON;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.DialogUtil;
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
 * 推广费提现确认界面
 */
public class PromotionCashConfirmActivity extends BaseActivity implements TitleView
        .TitleBarOnClickListener {

    @BindView(R.id.promotion_cash_title)
    TitleView mPromotionCashTitle;
    @BindView(R.id.promotion_cash_end)
    TextView mPromotionCashEnd;
    @BindView(R.id.promotion_cash_money)
    TextView mPromotionCashMoney;
    @BindView(R.id.promotion_cash_phone)
    TextView mPromotionCashPhone;


    private final String URL_APPLY_CASH = Constant.BASE_URL + "/api/fund/sale_cash_withdrawal";
    private final String ORDER_IDS = "orderIds";

    private String mOrderIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promotion_case_confirm);
        ButterKnife.bind(this);

        initView();

        initData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        boolean isExit = (boolean) SharePreferenceUtil.get(this, Constant.CORPORATE_EXIT, false);
        if (isExit) {
            //设置尾号
            String bank_number = (String) SharePreferenceUtil.get(this, Constant
                    .CORPORATE_ACCOUNT_NUMBER, "");
            mPromotionCashEnd.setText(bank_number.length() > 4 ? "尾号" + bank_number.substring(bank_number.length() - 4) : bank_number);
            //设置联系方式
            mPromotionCashPhone.setText("联系方式：" + SharePreferenceUtil.get(this, Constant
                    .CORPORATION_PERSONAL_PHONE_NUMBER, ""));
        } else {
            DialogUtil.noneCorporate(this);
        }
    }

    private void initView() {
        mPromotionCashTitle.setOnTitleBarClick(this);
    }

    private void initData() {
        Intent intent = getIntent();
        //获取订单号串
        mOrderIds = intent.getStringExtra("orderIds");
        //获取提现金额
        double amount = intent.getDoubleExtra("amount", -1);
        mPromotionCashMoney.setText("￥" + FormatUtils.MoneyFormat(amount));
    }

    @OnClick({R.id.promotion_cash_apply_service, R.id.promotion_cash_apply_cash})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.promotion_cash_apply_service:
                //打开客服
                Util.enterCustomerService(this);
                break;
            case R.id.promotion_cash_apply_cash:
                //申请提现确定
                showDialog();
                break;
        }
    }


    /**
     * 申请提现确定
     */
    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PromotionCashConfirmActivity.this,
                R.style.dialog);
        View v = View.inflate(this, R.layout.dialog_confirm_promotion, null);
        builder.setView(v);
        builder.setCancelable(false);
        TextView msg = (TextView) v.findViewById(R.id.dialog_msg);
        msg.setText(getString(R.string.promotion_withdrawal_corporate));

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
     * 申请提现
     */
    private void applyCash() {
        OkHttpUtils.post().url(URL_APPLY_CASH).addHeader(ACCESS_TOKEN, mAccessToken).addParams
                (ORDER_IDS, mOrderIds).build().execute(new StringCallback() {

            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.Companion.d("推广费申请提现确认E：" + e.getLocalizedMessage());
                Util.showTimeOutNotice(currentContext);
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.Companion.d("推广费申请提现：" + response);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        ToastUtil.showShort(currentContext, "申请成功");
                        //申请提现成功，返回财富列表
                        startActivity(new Intent(getApplicationContext(), RichesActivity.class));
                        break;
                    case "failure":
                        Util.showError(PromotionCashConfirmActivity.this, errorBean.getReason());
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
