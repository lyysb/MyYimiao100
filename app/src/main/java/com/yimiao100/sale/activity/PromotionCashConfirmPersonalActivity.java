package com.yimiao100.sale.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.yimiao100.sale.R;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.bean.ErrorBean;
import com.yimiao100.sale.bean.PersonalBean;
import com.yimiao100.sale.bean.Tax;
import com.yimiao100.sale.bean.TaxBean;
import com.yimiao100.sale.ext.JSON;
import com.yimiao100.sale.utils.*;
import com.yimiao100.sale.vaccine.RichVaccineActivity;
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
        .TitleBarOnClickListener, CheckUtil.PersonalPassedListener {

    @BindView(R.id.promotion_cash_title_personal)
    TitleView mPromotionCashTitle;
    @BindView(R.id.promotion_cash_end_personal)
    TextView mPromotionCashEnd;
    @BindView(R.id.promotion_cash_money_personal)
    TextView mPromotionCashMoney;
    @BindView(R.id.promotion_cash_phone_personal)
    TextView mPromotionCashPhone;


    private final String URL_APPLY_CASH = Constant.BASE_URL + "/api/fund/sale_cash_withdrawal";
    private final String URL_TEX = Constant.BASE_URL + "/api/tax/default";
    private final String ORDER_IDS = "orderIds";
    private final String RECONCILIATION = "reconciliation";                 // 从对账过来的
    private String mFrom;
    @BindView(R.id.promotion_cash_final_personal)
    TextView mPromotionCashFinalPersonal;

    private String mOrderIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promotion_case_confirm_personal);
        ButterKnife.bind(this);

        mFrom = getIntent().getStringExtra("from");

        initView();

        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        CheckUtil.checkPersonal(this, this);
    }

    @Override
    public void handlePersonal(PersonalBean personal) {
        String bankCardNumber = personal.getBankCardNumber();
        mPromotionCashEnd.setText(bankCardNumber.length() > 4 ? "尾号" + bankCardNumber.substring(bankCardNumber.length() - 4) : bankCardNumber);
        //设置联系方式
        mPromotionCashPhone.setText("联系方式：" + personal.getPersonalPhoneNumber());
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
        mPromotionCashMoney.setText(FormatUtils.RMBFormat(amount));
        // 获取实际金额
        double actualAmount = intent.getDoubleExtra("actual", 0.0);
        mPromotionCashFinalPersonal.setText(FormatUtils.RMBFormat(actualAmount));

//        //根据说率计算税后金额
//        OkHttpUtils.post().url(URL_TEX).addHeader(ACCESS_TOKEN, accessToken)
//                .build().execute(new StringCallback() {
//            @Override
//            public void onError(Call call, Exception e, int id) {
//                LogUtil.d("tax error is :");
//                e.printStackTrace();
//            }
//
//            @Override
//            public void onResponse(String response, int id) {
//                LogUtil.d("tax :" + response);
//                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
//                switch (errorBean.getStatus()) {
//                    case "success":
//                        Tax tax = JSON.parseObject(response, TaxBean.class).getTax();
//                        double taxRate;
//                        if (tax != null) {
//                            taxRate = tax.getTaxRate() / 100;
//                            mPromotionCashFinalPersonal.setText(FormatUtils.RMBFormat(amount * (1 - taxRate)));
//                        } else {
//                            // 错误税率
//                            errorTax();
//                        }
//                        break;
//                    case "failure":
//                        Util.showError(currentContext, errorBean.getReason());
//                        break;
//                }
//            }
//        });
    }

    private void errorTax() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PromotionCashConfirmPersonalActivity.this);
        builder.setMessage(getString(R.string.dialog_tax_error));
        builder.setCancelable(false);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
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
        OkHttpUtils.post().url(URL_APPLY_CASH).addHeader(ACCESS_TOKEN, accessToken).addParams
                (ORDER_IDS, mOrderIds).build().execute(new StringCallback() {

            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.d("推广费申请提现确认E：" + e.getLocalizedMessage());
                Util.showTimeOutNotice(currentContext);
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.d("推广费申请提现：" + response);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        ToastUtil.showShort(currentContext, "申请成功");
                        if (TextUtils.equals(mFrom, RECONCILIATION)) {
                            startActivity(new Intent(currentContext, ReconciliationDetailActivity.class));
                        } else {
                            //申请提现成功，返回财富列表
                            startActivity(new Intent(currentContext, RichVaccineActivity.class));
                        }
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
