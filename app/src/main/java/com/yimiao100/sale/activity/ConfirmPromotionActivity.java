package com.yimiao100.sale.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.yimiao100.sale.R;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.bean.ErrorBean;
import com.yimiao100.sale.bean.ResourceListBean;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.FormatUtils;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.SharePreferenceUtil;
import com.yimiao100.sale.utils.TimeUtil;
import com.yimiao100.sale.utils.Util;
import com.yimiao100.sale.view.TitleView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

/**
 * 资源推广-推广确认
 */
public class ConfirmPromotionActivity extends BaseActivity implements TitleView.TitleBarOnClickListener, View.OnClickListener {

    @BindView(R.id.confirm_promotion)
    TitleView mConfirmPromotion;
    @BindView(R.id.confirm_promotion_money)
    TextView mConfirmPromotionMoney;            //竞标保证金
    @BindView(R.id.confirm_promotion_time)
    TextView mConfirmPromotionTime;             //截止日期
    @BindView(R.id.confirm_promotion_confirm)
    Button mConfirmPromotionConfirm;
    private AlertDialog mDialog;

    private final String PLACE_ORDER = "/api/order/place_order";

    private String mAccessToken;
    private String mBidQty;
    private ResourceListBean mResourceInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_promotion);
        ButterKnife.bind(this);

        //读取Token
        mAccessToken = (String) SharePreferenceUtil.get(this, Constant.ACCESSTOKEN, "");

        Intent intent = getIntent();
        //资源
        mResourceInfo = intent.getParcelableExtra("resourceInfo");
        //竞标数量
        mBidQty = intent.getStringExtra("bidQty");
        initView();
    }

    private void initView() {
        mConfirmPromotion.setOnTitleBarClick(this);
        mConfirmPromotionConfirm.setOnClickListener(this);
        mConfirmPromotionMoney.setText("本次推广资源的竞标保证金为￥" + FormatUtils.MoneyFormat(mResourceInfo.getBidDeposit())
                + "元，请于竞标截止日前转到如下账户。\n汇款转账时，必须在备注处填写协议单号。");
        mConfirmPromotionTime.setText("本资源竞标截止日为" + TimeUtil.timeStamp2Date(mResourceInfo.getBidExpiredTipAt() + "", "yyyy年MM月dd日"));
    }

    @Override
    public void leftOnClick() {
        finish();
    }

    @Override
    public void rightOnClick() {

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.confirm_promotion_confirm:
                //确认-提交订单-弹出Dialog
                LogUtil.Companion.d("ResourceID：" + mResourceInfo.getId());
                submitPromotion();
                break;
            case R.id.dialog_confirm_promotion:
                //进入到资源列表界面
                startActivity(new Intent(getApplicationContext(), ResourcesActivity.class));
                mDialog.dismiss();
                finish();
                break;
        }
    }

    private void submitPromotion() {
        mConfirmPromotionConfirm.setEnabled(false);
        String order_url = Constant.BASE_URL + PLACE_ORDER;
        OkHttpUtils
                .post()
                .url(order_url)
                .addHeader("X-Authorization-Token", mAccessToken)
                .addParams("resourceId", mResourceInfo.getId() + "")
                .addParams("bidQty", mBidQty)
                .addParams("userAccountType", "corporate")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtil.Companion.d("确认推广E：" + e.getMessage());
                        Util.showTimeOutNotice(currentContext);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        mConfirmPromotionConfirm.setEnabled(true);
                        LogUtil.Companion.d("确认推广：" + response);
                        ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                        switch (errorBean.getStatus()) {
                            case "success":
                                //提交成功
                                //显示继续推广dialog
                                AlertDialog.Builder builder = new AlertDialog.Builder(ConfirmPromotionActivity.this, R.style.dialog);
                                View v = View.inflate(ConfirmPromotionActivity.this, R.layout.dialog_confirm_promotion, null);
                                builder.setView(v);
                                builder.setCancelable(false);
                                TextView submit = (TextView) v.findViewById(R.id.dialog_confirm_promotion);
                                submit.setOnClickListener(ConfirmPromotionActivity.this);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                    //如果当前API大于17
                                    if (!ConfirmPromotionActivity.this.isDestroyed()) {
                                        //判断当前Activity是否被销毁
                                        mDialog = builder.create();
                                        mDialog.show();
                                    }
                                }
                                break;
                            case "failure":
                                Util.showError(currentContext, errorBean.getReason());
                                break;
                        }
                    }
                });
    }
}
