package com.yimiao100.sale.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.meiqia.core.callback.OnInitCallback;
import com.meiqia.meiqiasdk.util.MQConfig;
import com.meiqia.meiqiasdk.util.MQIntentBuilder;
import com.yimiao100.sale.R;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.bean.ErrorBean;
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
    private final String URL_CASH_WITHDRAWAL = Constant.BASE_URL +
            "/api/fund/deposit_cash_withdrawal";

    private final String ORDER_ID = "orderIds";

    private String mOrderIds;


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
        String phone = (String) SharePreferenceUtil.get(this, Constant.CORPORATION_PERSONAL_PHONE_NUMBER, "");
        LogUtil.d(phone);
        mAssuranceCompanyPhone.setText("联系方式：" + phone);

        mOrderIds = intent.getStringExtra("orderIds");
        LogUtil.d("mOrderIds:" + mOrderIds);
    }

    @OnClick({R.id.assurance_apply_cash, R.id.assurance_apply_service})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.assurance_apply_cash:
                showApplyDialog();
                break;
            case R.id.assurance_apply_service:
                //打开客服
                enterCustomerService();
                break;
        }
    }

    /**
     * 打开客服界面
     */
    private void enterCustomerService() {
        MQConfig.init(this, Constant.MEI_QIA_APP_KEY, new OnInitCallback() {
            @Override
            public void onSuccess(String clientId) {
                Toast.makeText(getApplicationContext(), "init success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int code, String message) {
                Toast.makeText(getApplicationContext(), "int failure", Toast.LENGTH_SHORT).show();
            }
        });
        Intent intent = new MQIntentBuilder(this)
                .build();
        startActivity(intent);
    }

    /**
     * 提交申请
     */
    private void showApplyDialog() {
        OkHttpUtils.post().url(URL_CASH_WITHDRAWAL).addHeader(ACCESS_TOKEN, mAccessToken)
                .addParams(ORDER_ID, mOrderIds).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.d("保证金提现E：" + e.getMessage() + e.getLocalizedMessage());
                Util.showTimeOutNotice(currentContext);
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.d("保证金提现：" + response);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        showDialog();
                        break;
                    case "failure":
                        Util.showError(currentContext, errorBean.getReason());
                        break;
                }
            }
        });
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AssuranceCompanyActivity.this, R
                .style.dialog);
        View v = View.inflate(this, R.layout.dialog_confirm_promotion, null);
        builder.setView(v);
        builder.setCancelable(false);
        TextView tv1 = (TextView) v.findViewById(R.id.dialog_tv1);
        TextView tv2 = (TextView) v.findViewById(R.id.dialog_tv2);

        tv1.setText("温馨提示");
        tv2.setText("您申请的提现金额会在5个工作日\n之内到达您得账户，请注意查收。");

        TextView submit = (TextView) v.findViewById(R.id.dialog_confirm_promotion);
        submit.setText("好的");
        final AlertDialog dialog = builder.create();
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //直接跳回到财富首页
                startActivity(new Intent(getApplicationContext(), RichesActivity.class));
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void leftOnClick() {
        finish();
    }

    @Override
    public void rightOnClick() {

    }
}
