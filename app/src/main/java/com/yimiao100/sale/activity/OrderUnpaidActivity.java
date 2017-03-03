package com.yimiao100.sale.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.Spanned;
import android.widget.TextView;

import com.yimiao100.sale.R;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.bean.ResourceListBean;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.FormatUtils;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.TimeUtil;
import com.yimiao100.sale.view.TitleView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 我的业务第一状态-未付款
 */
public class OrderUnpaidActivity extends BaseActivity implements TitleView.TitleBarOnClickListener {

    @BindView(R.id.order_unpaid_title)
    TitleView mTitle;
    @BindView(R.id.order_unpaid_submit_time)
    TextView mTime;
    @BindView(R.id.order_unpaid_vendor_name)
    TextView mVendorName;
    @BindView(R.id.order_unpaid_common_name)
    TextView mCommonName;
    @BindView(R.id.order_unpaid_region)
    TextView mRegion;
    @BindView(R.id.order_unpaid_spec)
    TextView mSpec;
    @BindView(R.id.order_unpaid_dosage_form)
    TextView mDosageForm;
    @BindView(R.id.order_unpaid_time)
    TextView mOrderTime;
    @BindView(R.id.order_unpaid_money)
    TextView mMoney;
    @BindView(R.id.order_unpaid_no)
    TextView mNo;
    @BindView(R.id.order_unpaid_hint)
    TextView mHint;
    @BindView(R.id.order_unpaid_expired_at)
    TextView mExpiredAt;
    private ResourceListBean mOrder;

    private final String URL_CHECK = Constant.BASE_URL + "/api/order/pay_query";
    private final String ORDER_ID = "orderId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_unpaid);
        ButterKnife.bind(this);

        initView();

        initData();
    }

    private void initView() {
        mTitle.setOnTitleBarClick(this);
    }

    private void initData() {
        Intent intent = getIntent();
        mOrder = intent.getParcelableExtra("order");
        //提交日期
        String submit_time = TimeUtil.timeStamp2Date(mOrder.getCreatedAt() + "", "yyyy年MM月dd日");
        mTime.setText(submit_time + "您提交的申请推广已经通过审核，\n请您尽快提交竞标保证金。");
        //厂家名称
        String vendorName = mOrder.getVendorName();
        mVendorName.setText(vendorName);
        //产品名称-分类名
        String categoryName = mOrder.getCategoryName();
        mCommonName.setText("产品：" + categoryName);
        //规格
        String spec = mOrder.getSpec();
        mSpec.setText("规格：" + spec);
        //剂型
        String dosageForm = mOrder.getDosageForm();
        mDosageForm.setText("剂型：" + dosageForm);
        //区域
        String region = mOrder.getProvinceName() + "\t" + mOrder.getCityName() + "\t" + mOrder
                .getAreaName();
        mRegion.setText("区域：" + region);
        //时间
        String time = TimeUtil.timeStamp2Date(mOrder.getCreatedAt() + "", "yyyy.MM.dd");
        mOrderTime.setText("时间：" + time);
        //保证金
        String totalDeposit = FormatUtils.MoneyFormat(mOrder.getSaleDeposit());
        Spanned totalMoney = Html.fromHtml("推广保证金：" + "<font color=\"#4188d2\">" + totalDeposit +
                "</font>" + " (人民币)");
        mMoney.setText(totalMoney);
        //协议单号
        String serialNo = mOrder.getSerialNo();
        mNo.setText("协议单号：" + serialNo);
        //竞标保证金提示
        String bidDeposit = FormatUtils.MoneyFormat(mOrder.getBidDeposit());
        mHint.setText("本次推广资源的竞标保证金为￥" + bidDeposit + "元，请于竞标截止日前尽快提交。");
        //竞标有效提示日期
        long bidExpiredTipAt = mOrder.getBidExpiredTipAt();
        String expire = TimeUtil.timeStamp2Date(bidExpiredTipAt + "", "yyyy年MM月dd日");
        mExpiredAt.setText(Html.fromHtml("（<font color=\"#4188d2\">注意：</font>本资源竞标时间截止日为\t" + expire + "）"));
        //校验当前订单支付状态
//        checkOrderStatus();
    }

    /**
     * 联网检查是否是支付中
     */
    private void checkOrderStatus() {
        //请求接口，校验支付状态
        OkHttpUtils.post().url(URL_CHECK).addHeader(ACCESS_TOKEN, mAccessToken)
                .addParams(ORDER_ID, mOrder.getId() + "")
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                e.printStackTrace();
                LogUtil.Companion.d("检查支付状态错误");
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.Companion.d("检查支付状态" + response);
            }
        });
        //如果是支付中，弹窗提示
//        showNoticeDialog();
    }

    /**
     * 显示提示对话框
     */
    private void showNoticeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("您已支付保证金，支付结果确认中，请耐心等待");
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @OnClick(R.id.order_unpaid_submit)
    public void onClick() {
        //进入支付界面
        Intent intent = new Intent(this, SubmitPromotionActivity.class);
        intent.putExtra("userAccountType", mOrder.getUserAccountType());
        intent.putExtra("order", mOrder);
        intent.putExtra("mark", "order");
        startActivity(intent);
    }

    @Override
    public void leftOnClick() {
        finish();
    }

    @Override
    public void rightOnClick() {

    }
}
