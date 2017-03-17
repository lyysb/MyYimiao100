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

import static com.yimiao100.sale.utils.Constant.TAX_RATE;

/**
 * 推广费提现确认界面-个人
 */
public class PromotionCashConfirmPersonalActivity extends BaseActivity implements TitleView
        .TitleBarOnClickListener {

    @BindView(R.id.promotion_cash_title_personal)
    TitleView mPromotionCashTitle;
    @BindView(R.id.promotion_cash_end_personal)
    TextView mPromotionCashEnd;
    @BindView(R.id.promotion_cash_money_personal)
    TextView mPromotionCashMoney;
    @BindView(R.id.promotion_cash_phone_personal)
    TextView mPromotionCashPhone;


    private final String URL_APPLY_CASH = Constant.BASE_URL + "/api/fund/sale_cash_withdrawal";
    private final String ORDER_IDS = "orderIds";
    @BindView(R.id.promotion_cash_final_personal)
    TextView mPromotionCashFinalPersonal;

    private String mOrderIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promotion_case_confirm_personal);
        ButterKnife.bind(this);

        initView();

        initData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        boolean isExit = (boolean) SharePreferenceUtil.get(this, Constant.PERSONAL_EXIT, false);
        if (isExit) {
            //设置尾号
            String bankNumber = (String) SharePreferenceUtil.get(this, Constant
                    .PERSONAL_BANK_CARD_NUMBER, "");
            mPromotionCashEnd.setText(bankNumber.length() > 4 ? "尾号" + bankNumber.substring
                    (bankNumber.length() - 4) : bankNumber);
            //设置联系方式
            mPromotionCashPhone.setText("联系方式：" + SharePreferenceUtil.get(this, Constant
                    .PERSONAL_PHONE_NUMBER, ""));
        } else {
            DialogUtil.nonePersonal(this);
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
        //根据说率计算税后金额
        // ps 鬼知道为啥我存进去的double 取出来就是string
        double taxRate = Double.valueOf((String) SharePreferenceUtil.get(this, TAX_RATE, ""));
        if (taxRate != -1) {
            // 显示计算后金额
            mPromotionCashFinalPersonal.setText("￥" + FormatUtils.MoneyFormat(amount * (1 - taxRate)));
        } else {
            // 提示错误
            ToastUtil.showShort(this, "税率获取失败");
        }
    }

    @OnClick({R.id.promotion_cash_apply_service_personal, R.id.promotion_cash_apply_cash_personal})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.promotion_cash_apply_service_personal:
                //打开客服
                Util.enterCustomerService(this);
                break;
            case R.id.promotion_cash_apply_cash_personal:
                //申请提现确认
                showDialog();
                break;
        }
    }


    /**
     * 申请提现确定弹窗
     */
    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder
                (PromotionCashConfirmPersonalActivity.this,
                        R.style.dialog);
        View v = View.inflate(this, R.layout.dialog_confirm_promotion, null);
        builder.setView(v);
        builder.setCancelable(false);
        TextView msg = (TextView) v.findViewById(R.id.dialog_msg);

        msg.setText(getString(R.string.promotion_withdrawal_personal));
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
