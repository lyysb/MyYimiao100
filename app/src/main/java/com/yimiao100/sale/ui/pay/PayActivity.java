package com.yimiao100.sale.ui.pay;


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AlertDialog;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.jakewharton.rxbinding2.view.RxView;
import com.tencent.mm.sdk.constants.Build;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.yimiao100.sale.R;
import com.yimiao100.sale.mvpbase.BaseActivity;
import com.yimiao100.sale.mvpbase.CreatePresenter;
import com.yimiao100.sale.ui.business.vaccine.BusinessActivity;
import com.yimiao100.sale.ui.resource.ResourceActivity;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.FormatUtils;
import com.yimiao100.sale.view.TitleView;

import java.util.concurrent.TimeUnit;

/**
 * 支付界面
 */
@CreatePresenter(PayPresenter.class)
public class PayActivity extends BaseActivity<PayContract.View, PayContract.Presenter> implements PayContract.View {

    private ProgressDialog loadingDialog;
    private double payAmount;
    private String bizData;
    private String from;
    private String orderIds;
    private String userAccountType;
    private String vendorId;

    /*
    * 参数:bizData(json格式的字符串)
        {
            "userAccountType": "personal",
            "bidList": [
                {
                    "resourceId": "1",
                    "bidQty": "1"
                },
                {
                    "resourceId": "2",
                    "bidQty": "2"
                },
                {
                    "resourceId": "3",
                    "bidQty": "3"
                }
            ]
        }
    * */

    /**
     * 从 疫苗资源列表 跳转而来
     */
    public static void startFromVaccineRes(Context context, String from, double payAmount, String bizData) {
        Intent intent = new Intent(context, PayActivity.class);
        intent.putExtra("from", from);
        intent.putExtra("payAmount", payAmount);
        intent.putExtra("bizData", bizData);
        context.startActivity(intent);
    }

    /**
     * 从 我的业务 跳转而来
     */
    public static void startFromVaccineBus(Context context, String from, double payAmount, String userAccountType, String vendorId, String orderIds) {
        Intent intent = new Intent(context, PayActivity.class);
        intent.putExtra("from", from);
        intent.putExtra("payAmount", payAmount);
        intent.putExtra("userAccountType", userAccountType);
        intent.putExtra("vendorId", vendorId);
        intent.putExtra("orderIds", orderIds);
        context.startActivity(intent);
    }

    @Override
    protected void init() {
        super.init();
        initVariate();

        initView();

        initReceiver();
    }

    private void initVariate() {
        Intent intent = getIntent();
        from = intent.getStringExtra("from");
        payAmount = intent.getDoubleExtra("payAmount", -1.0);
        switch (from) {
            case "vaccine_res":
                bizData = intent.getStringExtra("bizData");
                LogUtils.d("payAmount：" + payAmount);
                LogUtils.json(bizData);
                break;
            case "vaccine_bus":
                userAccountType = intent.getStringExtra("userAccountType");
                vendorId = intent.getStringExtra("vendorId");
                orderIds = intent.getStringExtra("orderIds");
                LogUtils.d("orderIds：" + orderIds);
                break;
        }
    }

    private void initView() {
        // TitleView
        TitleView titleView = (TitleView) findViewById(R.id.pay_title);
        titleView.setOnTitleBarClick(new TitleView.TitleBarOnClickListener() {
            @Override
            public void leftOnClick() {
                finish();
            }

            @Override
            public void rightOnClick() {

            }
        });
        // 实付款
        TextView tvPayAmount = (TextView) findViewById(R.id.pay_amount);
        tvPayAmount.setText("实付款：¥" + FormatUtils.MoneyFormat(payAmount));
        // 支付方式选择-微信
        CheckBox cbWxPay = (CheckBox) findViewById(R.id.pay_wx);
        // 确认支付
        Button btPay = (Button) findViewById(R.id.pay_pay);
        RxView.clicks(btPay)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(o -> {
                    if (!cbWxPay.isChecked()) {
                        ToastUtils.showShort(getString(R.string.pay_way_first));
                        return;
                    }
                    // 微信支付
                    weChatPay();
                });
        // LoadingProgressDialog
        loadingDialog = new ProgressDialog(this);
        loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loadingDialog.setMessage(getResources().getString(R.string.pay_loading));
        loadingDialog.setCancelable(false);
    }

    private void initReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.yimiao100.wx");
        registerReceiver(weChatReceiver, filter);
    }

    /**
     * 进行微信支付
     */
    private void weChatPay() {
        // 向微信注册AppId
        final IWXAPI weChatId = WXAPIFactory.createWXAPI(this, Constant.WX_APP_ID, false);
        // 注册到微信
        weChatId.registerApp(Constant.WX_APP_ID);
        // 检查是否安装微信
        if (!weChatId.isWXAppInstalled()) {
            // 提示用户安装微信
            ToastUtils.showShort(getString(R.string.install_wx));
            return;
        }
        // 检查微信版本是否支持
        if (weChatId.getWXAppSupportAPI() < Build.PAY_SUPPORTED_SDK_INT) {
            // 提示更新微信版本
            ToastUtils.showShort(getString(R.string.update_wx));
            return;
        }
        // 发起支付
        switch (from) {
            case "vaccine_res":
                // 请求批量竞标支付
                getPresenter().requestBizDataPay(weChatId, bizData);
                break;
            case "vaccine_bus":
                // 请求我的业务批量支付
                getPresenter().requestBidDeposit(weChatId, orderIds);
                break;
        }
    }

    private BroadcastReceiver weChatReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int code = intent.getIntExtra("code", 1);
            // 处理结果
            showPayResult(code);
        }
    };

    /**
     * 处理支付结果
     */
    private void showPayResult(final int code) {
        LogUtils.d("pay code is " + code);
        //0 支付成功
        //-1 发生错误 可能的原因：签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等。
        //-2 用户取消 发生场景：用户不支付了，点击取消，返回APP。
        AlertDialog.Builder builder = new AlertDialog.Builder(PayActivity.this);
        builder.setTitle(getString(R.string.pay_result));
        switch (code) {
            case 0:
                builder.setMessage(getString(R.string.pay_success));
                break;
            case -1:
                builder.setMessage(getString(R.string.pay_failure));
                break;
            case -2:
                builder.setMessage(getString(R.string.pay_cancel));
                break;
            default:
                builder.setMessage(getString(R.string.pay_failure));
                break;
        }
        builder.setCancelable(false);
        builder.setPositiveButton("ok", (dialog, which) -> {
            if (0 == code) {
                //支付成功，返回列表页
                switch (from) {
                    case "vaccine_res":
                        ResourceActivity.start(PayActivity.this);
                        break;
                    case "vaccine_bus":
                        BusinessActivity.start(PayActivity.this, userAccountType, vendorId);
                        break;
                }
            }
            //支付出现问题（取消，失败），不需要做任何操作。停留在支付页面
            dialog.dismiss();
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(weChatReceiver);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_pay;
    }

    @Override
    public void showPayLoading() {
        loadingDialog.show();
    }

    @Override
    public void dismissPayLoading() {
        loadingDialog.dismiss();
    }
}
