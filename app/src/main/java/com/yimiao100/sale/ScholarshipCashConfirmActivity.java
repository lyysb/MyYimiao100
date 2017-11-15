package com.yimiao100.sale;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.yimiao100.sale.activity.RichesActivity;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.bean.CorporateBean;
import com.yimiao100.sale.bean.ErrorBean;
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

/**
 * 鬼知道我当时怎么就放在了包外面。
 * 奖学金提现确认
 */
public class ScholarshipCashConfirmActivity extends BaseActivity implements TitleView
        .TitleBarOnClickListener, CheckUtil.CorporatePassedListener {

    @BindView(R.id.scholarship_cash_title)
    TitleView mScholarshipCashTitle;
    @BindView(R.id.scholarship_cash_end)
    TextView mScholarshipCashEnd;
    @BindView(R.id.scholarship_cash_money)
    TextView mScholarshipCashMoney;
    @BindView(R.id.scholarship_cash_phone)
    TextView mScholarshipCashPhone;

    private final String URL_APPLY_CASH = Constant.BASE_URL + "/api/fund/exam_reward_cash_withdrawal";
    private final String ORDER_IDS = "courseExamItemIds";

    private String mOrderIds;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scholarship_cash_confirm);
        ButterKnife.bind(this);

        initView();

        initData();
    }
    @Override
    protected void onResume() {
        super.onResume();
        CheckUtil.checkCorporate(this,this);
    }

    @Override
    public void handleCorporate(CorporateBean corporate) {
        String corporateAccount = corporate.getCorporateAccount();
        mScholarshipCashEnd.setText(corporateAccount.length() > 4 ? "尾号" + corporateAccount.substring(corporateAccount.length() - 4) : corporateAccount);
        //设置联系方式
        mScholarshipCashPhone.setText("联系方式：" + corporate.getPersonalPhoneNumber());
    }
    private void initView() {
        mScholarshipCashTitle.setOnTitleBarClick(this);
    }
    private void initData() {
        Intent intent = getIntent();
        //获取订单号串
        mOrderIds = intent.getStringExtra("orderIds");
        LogUtil.d("mOrderIds:" + mOrderIds);
        //获取提现金额
        double amount = intent.getDoubleExtra("amount", -1);
        mScholarshipCashMoney.setText(FormatUtils.RMBFormat(amount));
    }
    @OnClick({R.id.scholarship_cash_apply_service, R.id.scholarship_cash_apply_cash})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.scholarship_cash_apply_service:
                //打开客服
                Util.enterCustomerService(this);
                break;
            case R.id.scholarship_cash_apply_cash:
                // 确认提现
                showDialog();
                break;
        }
    }
    /**
     * 申请提现确认弹窗
     */
    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ScholarshipCashConfirmActivity.this,
                R.style.dialog);
        View v = View.inflate(this, R.layout.dialog_confirm_promotion, null);
        builder.setView(v);
        builder.setCancelable(false);
        TextView msg = (TextView) v.findViewById(R.id.dialog_msg);

        msg.setText(getString(R.string.exam_withdrawal_corporate));

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
                        //申请提现成功，返回疫苗财富列表
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
