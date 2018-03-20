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
     * @param context 上下文
     * @param payAmount 实付款金额-bidDeposit
     * @param bizData 批量竞标json
     */
    public static void start(Context context,double payAmount, String bizData) {
        Intent intent = new Intent(context, PayActivity.class);
        intent.putExtra("payAmount", payAmount);
        intent.putExtra("bizData", bizData);
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
        payAmount = intent.getDoubleExtra("payAmount", -1.0);
        bizData = intent.getStringExtra("bizData");
        LogUtils.d("payAmount：" + payAmount);
        LogUtils.json(bizData);
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
                        ToastUtils.showShort("请选择支付方式");
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
            ToastUtils.showShort("请先安装微信");
            return;
        }
        // 检查微信版本是否支持
        if (weChatId.getWXAppSupportAPI() < Build.PAY_SUPPORTED_SDK_INT) {
            // 提示更新微信版本
            ToastUtils.showShort("您目前微信版本不支持支付，请先升级微信");
            return;
        }
        // 发起支付
        getPresenter().requestPay(weChatId, bizData);
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
        builder.setTitle("支付结果");
        switch (code) {
            case 0:
                builder.setMessage("支付成功");
                break;
            case -1:
                builder.setMessage("支付失败，请稍后再试");
                break;
            case -2:
                builder.setMessage("支付取消");
                break;
            default:
                builder.setMessage("支付失败，请稍后再试");
                break;
        }
        builder.setCancelable(false);
        builder.setPositiveButton("ok", (dialog, which) -> {
            if (0 == code) {
                //支付成功，返回列表页
                ResourceActivity.start(getApplicationContext());
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
