package com.yimiao100.sale.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yimiao100.sale.R;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.bean.ResourceListBean;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.TimeUtil;
import com.yimiao100.sale.utils.Util;
import com.yimiao100.sale.view.TitleView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Request;

/**
 * 我的业务-第四状态-已签约
 */
public class OrderCompletedActivity extends BaseActivity implements TitleView.TitleBarOnClickListener {

    @BindView(R.id.order_complete_title)
    TitleView mOrderCompleteTitle;
    @BindView(R.id.order_complete_submit_time)
    TextView mOrderCompleteSubmitTime;
    @BindView(R.id.order_complete_vendor_name)
    TextView mOrderCompleteVendorName;
    @BindView(R.id.order_complete_common_name)
    TextView mOrderCompleteCommonName;
    @BindView(R.id.order_complete_region)
    TextView mOrderCompleteRegion;
    @BindView(R.id.order_complete_spec)
    TextView mOrderCompleteSpec;
    @BindView(R.id.order_complete_dosage_form)
    TextView mOrderCompleteDosageForm;
    @BindView(R.id.order_complete_time)
    TextView mOrderCompleteTime;
    @BindView(R.id.order_complete_total_money)
    TextView mOrderCompleteTotalMoney;
    @BindView(R.id.order_complete_serial_no)
    TextView mOrderCompleteSerialNo;
    @BindView(R.id.order_complete_into)
    TextView mOrderCompleteInto;
    @BindView(R.id.order_complete_view)
    TextView mOrderCompleteView;
    @BindView(R.id.order_complete_download)
    ImageView mOrderCompleteDownload;
    private String mResourceProtocolUrl;
    private ProgressDialog mProgressDownloadDialog;
    private String mOrderProtocolUrl;
    private ProgressDialog mProgressDialog;
    private String mVendorName;
    private String mCategoryName;
    private String mSerialNo;
    private String mSpec;
    private String mDosageForm;
    private String mOrderId;
    private String mProductName;
    private String mCustomerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_completed);
        ButterKnife.bind(this);

        initView();

        initData();
    }

    private void initView() {
        mOrderCompleteTitle.setOnTitleBarClick(this);
    }

    private void initData() {
        ResourceListBean order = getIntent().getParcelableExtra("order");
        mOrderId = order.getId() + "";
        String submit_time = TimeUtil.timeStamp2Date(order.getCreatedAt() + "", "yyyy年MM月dd日");
        mOrderCompleteSubmitTime.setText(submit_time + "提交的申请推广已经正式签约，\n请按照推广协议尽快完成推广任务。");
        mVendorName = order.getVendorName();
        mOrderCompleteVendorName.setText(mVendorName);
        mCategoryName = order.getCategoryName();
        mOrderCompleteCommonName.setText(mCategoryName);
        mSpec = order.getSpec();
        mOrderCompleteSpec.setText("规格：" + mSpec);
        mDosageForm = order.getDosageForm();
        mOrderCompleteDosageForm.setText("剂型：" + mDosageForm);
        String region = order.getProvinceName() + "\t" + order.getCityName() + "\t" + order.getAreaName();
        mOrderCompleteRegion.setText("区域：" + region);
        String time = TimeUtil.timeStamp2Date(order.getCreatedAt() + "", "yyyy.MM.dd");
        mOrderCompleteTime.setText("时间：" + time);
        String totalDeposit = order.getSaleDeposit() + "";
        Spanned totalMoney = Html.fromHtml("推广保证金：" + "<font color=\"#4188d2\">" + totalDeposit + "</font>" + "(人民币)");
        mOrderCompleteTotalMoney.setText(totalMoney);
        mSerialNo = order.getSerialNo();
        mOrderCompleteSerialNo.setText("协议单号：" + mSerialNo);
        mOrderProtocolUrl = order.getOrderProtocolUrl();
        mResourceProtocolUrl = order.getResourceProtocolUrl();

        mProductName = order.getProductName();
        mCustomerName = order.getCustomerName();

    }

    @OnClick({R.id.order_complete_into, R.id.order_complete_view, R.id.order_complete_download})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.order_complete_into:
                //进入到对账详情页面
                enterReconciliationDetail();
                break;
            case R.id.order_complete_view:
                //下载已签约协议，使用相册打开
                downloadAlreadyAgreement(mOrderProtocolUrl);
                break;
            case R.id.order_complete_download:
                //下载电子协议
                downloadAgreement(mResourceProtocolUrl);
                break;
        }
    }

    private void enterReconciliationDetail() {
        Intent intent = new Intent(this, ReconciliationDetailActivity.class);
        Bundle bundle = new Bundle();
        intent.putExtra("orderId", mOrderId);
        //产品名-分类名
        bundle.putString("categoryName", mCategoryName);
        //商品名
        bundle.putString("productName", mProductName);
        //厂家名称
        bundle.putString("vendorName", mVendorName);
        //客户名称
        bundle.putString("customerName", mCustomerName);
        //剂型
        bundle.putString("dosageForm", mDosageForm);
        //规格
        bundle.putString("spec", mSpec);
        //协议单号
        bundle.putString("serialNo", mSerialNo);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void downloadAlreadyAgreement(String orderProtocolUrl) {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setTitle("正在下载，请稍后...");
        String fileName = orderProtocolUrl.substring(orderProtocolUrl.lastIndexOf("/") + 1);
        LogUtil.d("已签约协议fileName：" + fileName);
        OkHttpUtils.get().url(orderProtocolUrl)
                .build().execute(
                new FileCallBack(Environment.getExternalStorageDirectory().getAbsolutePath(), fileName) {
                    @Override
                    public void onBefore(Request request, int id) {
                        super.onBefore(request, id);
                        mProgressDialog.show();
                    }

                    @Override
                    public void onAfter(int id) {
                        super.onAfter(id);
                        mProgressDialog.dismiss();
                    }

                    @Override
                    public void inProgress(float progress, long total, int id) {
                        super.inProgress(progress, total, id);
                        mProgressDialog.setProgress((int) (100 * progress + 0.5));
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtil.d("onError：" + e.getMessage().toString());
                        Util.showTimeOutNotice(currentContext);
                    }

                    @Override
                    public void onResponse(File response, int id) {
                        LogUtil.d("已签约协议：onResponse：" + response.getAbsolutePath());
                        LogUtil.d("已签约协议：response.name：" + response.getName());
                        if (response != null && response.isFile() == true) {
                            Intent intent = new Intent();
                            intent.setAction(android.content.Intent.ACTION_VIEW);
                            intent.setDataAndType(Uri.fromFile(response), "image/*");
                            startActivity(intent);
                        }
                    }
                }
        );
    }

    private void downloadAgreement(String resourceProtocolUrl) {
        mProgressDownloadDialog = new ProgressDialog(this);
        mProgressDownloadDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDownloadDialog.setTitle("当前下载进度");
        mProgressDownloadDialog.setCancelable(false);
        String fileName = resourceProtocolUrl.substring(resourceProtocolUrl.lastIndexOf("/") + 1);
        LogUtil.d("电子协议fileName：" + fileName);
        OkHttpUtils.get().url(resourceProtocolUrl)
                .build().execute(
                new FileCallBack(Environment.getExternalStorageDirectory().getAbsolutePath(), fileName) {
                    @Override
                    public void onBefore(Request request, int id) {
                        super.onBefore(request, id);
                        mProgressDownloadDialog.show();
                    }

                    @Override
                    public void onAfter(int id) {
                        super.onAfter(id);
                        mProgressDownloadDialog.dismiss();
                    }

                    @Override
                    public void inProgress(float progress, long total, int id) {
                        super.inProgress(progress, total, id);
                        mProgressDownloadDialog.setProgress((int) (100 * progress + 0.5));
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtil.d("onError：" + e.getMessage().toString());
                        Util.showTimeOutNotice(currentContext);
                    }

                    @Override
                    public void onResponse(File response, int id) {
                        LogUtil.d("onResponse：" + response.getAbsolutePath());
                        LogUtil.d("response.name：" + response.getName());

                        //打开邮箱
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.putExtra("subject", response.getName()); //
                        intent.putExtra("body", "Email from CodePad"); //正文
                        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(response)); //添加附件，附件为file对象
                        if (response.getName().endsWith(".gz")) {
                            intent.setType("application/x-gzip"); //如果是gz使用gzip的mime
                        } else if (response.getName().endsWith(".txt")) {
                            intent.setType("text/plain"); //纯文本则用text/plain的mime
                        } else {
                            intent.setType("application/octet-stream"); //其他的均使用流当做二进制数据来发送
                        }
                        startActivity(intent); //调用系统的mail客户端进行发送}
                    }
                }
        );
    }

    @Override
    public void leftOnClick() {
        finish();
    }

    @Override
    public void rightOnClick() {

    }
}
