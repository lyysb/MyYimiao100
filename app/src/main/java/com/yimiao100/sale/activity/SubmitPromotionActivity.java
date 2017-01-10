package com.yimiao100.sale.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.tencent.mm.sdk.constants.Build;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.yimiao100.sale.R;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.bean.ErrorBean;
import com.yimiao100.sale.bean.ResourceListBean;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.FormatUtils;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.ToastUtil;
import com.yimiao100.sale.utils.Util;
import com.yimiao100.sale.view.TitleView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 提交竞标保证金
 * Created by Michel
 */
public class SubmitPromotionActivity extends BaseActivity implements TitleView
        .TitleBarOnClickListener {

    @BindView(R.id.submit_promotion_title)
    TitleView mTitle;
    @BindView(R.id.submit_promotion_amount)
    TextView mAmount;
    @BindView(R.id.submit_promotion_wx)
    RadioButton mWx;
    @BindView(R.id.submit_promotion_pay)
    Button mSubmitPromotionPay;

    private final String RESOURCE_ID = "resourceId";
    private final String BID_QTY = "bidQty";
    private final String USER_ACCOUNT_TYPE = "userAccountType";
    private final String CHANNEL = "channel";
    private final String ORDER_ID = "orderId";
    private String URL_PAY;


    private HashMap<String, String> mParams;

    private String mResourceId;
    private String mBidQty;
    private String mUserAccountType;
    private String mChannel;
    private String mOrderId;
    private String mMark;
    private ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_promotion);
        ButterKnife.bind(this);

        // TODO: 2017/1/5 暂时用RadioButton，默认只能使用微信支付，以后加入其它支付方式再重构

        initView();

        initData();

        initReceiver();
    }

    private void initView() {
        mTitle.setOnTitleBarClick(this);
        //默认选中微信支付
        mWx.setChecked(true);
    }

    private void initData() {
        mParams = new HashMap<>();
        mUserAccountType = "corporate";
        Intent intent = getIntent();
        //根据不同的来源添加不同的参数，显示不同的数据
        mMark = intent.getStringExtra("mark");
        LogUtil.Companion.d("mark_" + mMark);
        switch (mMark) {
            case "resource":
                URL_PAY = Constant.BASE_URL + "/api/order/place_order";
                //资源
                ResourceListBean resourceInfo = intent.getParcelableExtra("resourceInfo");
                mResourceId = resourceInfo.getId() + "";
                //竞标数量
                mBidQty = intent.getStringExtra("bidQty");
                //实付款金额
                double bidDeposit = resourceInfo.getBidDeposit();
                mAmount.setText("实付款：￥" + FormatUtils.MoneyFormat(bidDeposit));
                //添加参数
                mParams.put(RESOURCE_ID, mResourceId);
                mParams.put(BID_QTY, mBidQty);
                break;
            case "order":
                URL_PAY = Constant.BASE_URL + "/api/order/pay_bid_deposit";
                ResourceListBean order = intent.getParcelableExtra("order");
                mAmount.setText("实付款：￥" + order.getBidDeposit());
                mOrderId = order.getId() + "";
                LogUtil.Companion.d(mOrderId);
                //添加参数
                mParams.put(ORDER_ID, mOrderId);
                break;
        }

    }

    private void initReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.yimiao100.wx");
        registerReceiver(mReceiver, filter);
    }

    @OnClick(R.id.submit_promotion_pay)
    public void onClick() {
        //判断选中的支付方式
        if (mWx.isChecked()) {
            //按钮禁止点击
            mSubmitPromotionPay.setEnabled(false);
            mChannel = "wx";
            //调用微信支付接口
            weChatPay();
        }
    }

    /**
     * 微信原生支付接口
     */
    private void weChatPay() {
        //调用微信API之前需要向微信注册APP_ID
        final IWXAPI weChatId = WXAPIFactory.createWXAPI(this, Constant.WX_APP_ID, false);
        //将该app注册到微信
        weChatId.registerApp(Constant.WX_APP_ID);
        //调起支付之前可以检查是否安装微信
        boolean wxAppInstalled = weChatId.isWXAppInstalled();
        if (!wxAppInstalled) {
            //提示用户安装微信
            ToastUtil.showShort(currentContext, "请先安装微信");
            mSubmitPromotionPay.setEnabled(true);
            return;
        }
        //如果安装微信，检查微信版本是否支持支付
        boolean isPaySupported = weChatId.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT;
        if (isPaySupported) {
            //可以发起支付
            toWxPay(weChatId);
        } else {
            ToastUtil.showShort(currentContext, "您目前微信版本不支持支付，请先升级微信");
            mSubmitPromotionPay.setEnabled(true);
        }
    }

    /**
     * 进行微信支付
     *
     * @param weChatId
     */
    private void toWxPay(final IWXAPI weChatId) {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage("请求数据中..请稍后");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        //链接服务器，生成支付订单
        OkHttpUtils.post().url(URL_PAY).addHeader(ACCESS_TOKEN, mAccessToken)
                .params(mParams)
                .addParams(USER_ACCOUNT_TYPE, mUserAccountType).addParams(CHANNEL, mChannel)
                .build().connTimeOut(30000L).readTimeOut(30000L).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                mProgressDialog.dismiss();
                mSubmitPromotionPay.setEnabled(true);
                e.printStackTrace();
                Util.showTimeOutNotice(currentContext);
            }


            @Override
            public void onResponse(String response, int id) {
                mProgressDialog.dismiss();
                mSubmitPromotionPay.setEnabled(true);
                LogUtil.Companion.d(response);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        //从服务端获取返回信息
                        JSONObject jsonObject;
                        try {
                            jsonObject = new JSONObject(response);
                            if (null != jsonObject && jsonObject.has("payRequest")) {
                                JSONObject payRequest = jsonObject.getJSONObject("payRequest");
                                PayReq req = new PayReq();
                                req.appId = payRequest.getString("appid");
                                req.partnerId = payRequest.getString("partnerid");
                                req.prepayId = payRequest.getString("prepayid");
                                req.nonceStr = payRequest.getString("noncestr");
                                req.timeStamp = payRequest.getString("timestamp");
                                req.packageValue = payRequest.getString("package");
                                req.sign = payRequest.getString("sign");
                                //去WXPayEntry做回调
                                weChatId.sendReq(req);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            ToastUtil.showShort(currentContext, "支付异常，请稍后再试");
                        }
                        break;
                    case "failure":
                        Util.showError(currentContext, errorBean.getReason());
                        break;
                }
            }
        });
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            int code = intent.getIntExtra("code", 1);
            //收到广播后，处理结果
            handleResult(code);
        }
    };

    private void handleResult(final int code) {
        //0 支付成功
        //-1 发生错误 可能的原因：签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等。
        //-2 用户取消 发生场景：用户不支付了，点击取消，返回APP。
        AlertDialog.Builder builder = new AlertDialog.Builder(SubmitPromotionActivity.this);
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
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (0 == code) {
                    //支付成功，返回列表页
                    Class clz = null;
                    switch (mMark) {
                        case "resource":
                            clz = ResourcesActivity.class;
                            break;
                        case "order":
                            clz = OrderActivity.class;
                            break;
                    }
                    Intent intent1 = new Intent(SubmitPromotionActivity.this, clz);
                    startActivity(intent1);
                }
                //支付出现问题（取消，失败），不需要做任何操作。停留在支付页面
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    @Override
    public void leftOnClick() {
        finish();
    }

    @Override
    public void rightOnClick() {

    }
}
