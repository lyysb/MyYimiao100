package com.yimiao100.sale.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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
 * 奖学金提现确认--个人
 */
public class ScholarshipCashConfirmPersonalActivity extends BaseActivity implements TitleView
        .TitleBarOnClickListener, CheckUtil.PersonalPassedListener {

    @BindView(R.id.scholarship_cash_title_personal)
    TitleView mScholarshipCashTitle;
    @BindView(R.id.scholarship_cash_end_personal)
    TextView mScholarshipCashEnd;
    @BindView(R.id.scholarship_cash_money_personal)
    TextView mScholarshipCashMoney;
    @BindView(R.id.scholarship_cash_phone_personal)
    TextView mScholarshipCashPhone;

    private final String URL_APPLY_CASH = Constant.BASE_URL +
            "/api/fund/exam_reward_cash_withdrawal";
    private final String URL_TEX = Constant.BASE_URL + "/api/tax/default";
    private final String ORDER_IDS = "courseExamItemIds";
    @BindView(R.id.scholarship_cash_final_personal)
    TextView mScholarshipCashFinalPersonal;

    private String mOrderIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scholarship_cash_confirm_personal);
        ButterKnife.bind(this);

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
        mScholarshipCashEnd.setText(bankCardNumber.length() > 4 ? "尾号" + bankCardNumber.substring(bankCardNumber.length() - 4) : bankCardNumber);
        //设置联系方式
        mScholarshipCashPhone.setText("联系方式：" + personal.getPersonalPhoneNumber());
    }

    private void initView() {
        mScholarshipCashTitle.setOnTitleBarClick(this);
    }

    private void initData() {
        Intent intent = getIntent();
        //获取订单号串
        mOrderIds = intent.getStringExtra("orderIds");
        LogUtil.d("mOrderIds are :" + mOrderIds);
        //获取提现金额
        final double amount = intent.getDoubleExtra("amount", -1);
        mScholarshipCashMoney.setText(FormatUtils.RMBFormat(amount));
        //根据说率计算税后金额
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
                            mScholarshipCashFinalPersonal.setText(FormatUtils.RMBFormat(amount * (1 - taxRate)));
                        } else {
                            // 错误税率
                            errorTax();
                        }
                        break;
                    case "failure":
                        Util.showError(currentContext, errorBean.getReason());
                        break;
                }
            }
        });

    }

    private void errorTax() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ScholarshipCashConfirmPersonalActivity.this);
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

    @OnClick({R.id.scholarship_cash_apply_service_personal, R.id
            .scholarship_cash_apply_cash_personal})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.scholarship_cash_apply_service_personal:
                //打开客服
                Util.enterCustomerService(this);
                break;
            case R.id.scholarship_cash_apply_cash_personal:
                //提示确认
                showDialog();
                break;
        }
    }

    /**
     * 申请提现成功弹窗
     */
    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ScholarshipCashConfirmPersonalActivity.this, R.style.dialog);
        View v = View.inflate(this, R.layout.dialog_confirm_promotion, null);
        builder.setView(v);
        builder.setCancelable(false);
        TextView msg = (TextView) v.findViewById(R.id.dialog_msg);

        msg.setText(getString(R.string.exam_withdrawal_personal));

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
                LogUtil.d("mOrderIds" + mOrderIds);
                LogUtil.d("推广费申请提现：" + response);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        ToastUtil.showShort(currentContext, "申请成功");
                        //申请提现成功，返回财富列表
                        startActivity(new Intent(getApplicationContext(), RichVaccineActivity.class));
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
