package com.yimiao100.sale.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.yimiao100.sale.R;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.bean.ErrorBean;
import com.yimiao100.sale.bean.ResourceListBean;
import com.yimiao100.sale.callback.ProtocolFileCallBack;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.FormatUtils;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.TimeUtil;
import com.yimiao100.sale.utils.ToastUtil;
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
import okhttp3.Response;

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
    private ResourceListBean mOrder;


    private final String URL_DOWNLOAD_FILE = Constant.BASE_URL + "/api/order/fetch_protocol";
    private final String ORDER_ID = "orderId";

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
        mOrder = getIntent().getParcelableExtra("order");
        mOrderId = mOrder.getId() + "";
        String submit_time = TimeUtil.timeStamp2Date(mOrder.getCreatedAt() + "", "yyyy年MM月dd日");
        mOrderCompleteSubmitTime.setText(submit_time + getString(R.string.order_complete_notice));
        mVendorName = mOrder.getVendorName();
        mOrderCompleteVendorName.setText(mVendorName);
        mCategoryName = mOrder.getCategoryName();
        mOrderCompleteCommonName.setText("产品：" + mCategoryName);
        mSpec = mOrder.getSpec();
        mOrderCompleteSpec.setText("规格：" + mSpec);
        mDosageForm = mOrder.getDosageForm();
        mOrderCompleteDosageForm.setText("剂型：" + mDosageForm);
        String region = mOrder.getProvinceName() + "\t" + mOrder.getCityName() + "\t" + mOrder.getAreaName();
        mOrderCompleteRegion.setText("区域：" + region);
        String time = TimeUtil.timeStamp2Date(mOrder.getCreatedAt() + "", "yyyy.MM.dd");
        mOrderCompleteTime.setText("时间：" + time);
        String totalDeposit = FormatUtils.MoneyFormat(mOrder.getSaleDeposit());
        Spanned totalMoney = Html.fromHtml("推广保证金：" + "<font color=\"#4188d2\">" + totalDeposit + "</font>" + "(人民币)");
        mOrderCompleteTotalMoney.setText(totalMoney);
        mSerialNo = mOrder.getSerialNo();
        mOrderCompleteSerialNo.setText("协议单号：" + mSerialNo);
        mOrderProtocolUrl = mOrder.getOrderProtocolUrl();

        mProductName = mOrder.getProductName();
        mCustomerName = mOrder.getCustomerName();

    }

    @OnClick({R.id.order_complete_into, R.id.order_complete_view, R.id.order_complete_download})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.order_complete_into:
                if (mOrder.getTotalQty() == null || mOrder.getTotalQty().isEmpty()) {
                    // 提示没有发货信息
                    ToastUtil.showShort(this, getString(R.string.order_complete_enter_reconciliation));
                } else {
                    //进入到对账详情页面
                    enterReconciliationDetail();
                    return;
                }
                break;
            case R.id.order_complete_view:
                //下载已签约协议，使用相册打开--已废弃
//                downloadAlreadyAgreement(mOrderProtocolUrl);
                ToastUtil.showShort(currentContext, "请查看您之前所拍摄照片");
                break;
            case R.id.order_complete_download:
                //下载电子协议
                downloadAgreement();
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
        mProgressDialog.setTitle(getString(R.string.download_progress_dialog_title));
        String fileName = orderProtocolUrl.substring(orderProtocolUrl.lastIndexOf("/") + 1);
        LogUtil.Companion.d("已签约协议fileName：" + fileName);
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
                        LogUtil.Companion.d("onError：" + e.getMessage().toString());
                        Util.showTimeOutNotice(currentContext);
                    }

                    @Override
                    public void onResponse(File response, int id) {
                        LogUtil.Companion.d("已签约协议：onResponse：" + response.getAbsolutePath());
                        LogUtil.Companion.d("已签约协议：response.name：" + response.getName());
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

    /**
     * 下载电子协议
     */
    private void downloadAgreement() {
        mProgressDownloadDialog = new ProgressDialog(this);
        mProgressDownloadDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDownloadDialog.setTitle(getString(R.string.download_progress_dialog_title));
        mProgressDownloadDialog.setCancelable(false);
        String fileHead = "推广协议" + mOrder.getProductName();
        OkHttpUtils.post().url(URL_DOWNLOAD_FILE).addHeader(ACCESS_TOKEN, mAccessToken)
                .addParams(ORDER_ID, mOrderId).build().connTimeOut(1000 * 60 * 10)
                .readTimeOut(1000 * 60 * 10).execute(new ProtocolFileCallBack(fileHead) {
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
            public void onError(Call call, Exception e, int id) {
                Util.showTimeOutNotice(currentContext);
            }

            @Override
            public File parseNetworkResponse(Response response, int id) throws Exception {
                if (200 == response.code()) {
                    //协议下载成功
                    LogUtil.Companion.d("下载成功");
                    return super.parseNetworkResponse(response, id);
                } else if (400 == response.code()) {
                    //解析显示错误信息
                    LogUtil.Companion.d("下载失败");
                    ErrorBean errorBean = JSON.parseObject(response.body().toString(),
                            ErrorBean.class);
                    Util.showError(currentContext, errorBean.getReason());
                    return null;
                }
                return super.parseNetworkResponse(response, id);
            }

            @Override
            public void onResponse(File response, int id) {
                if (response == null) {
                    //如果返回来null证明下载错误
                    return;
                }
                LogUtil.Companion.d("onResponse：" + response.getAbsolutePath());
                LogUtil.Companion.d("response.name：" + response.getName());
                //下载成功，显示分享或者发送出去对话框
                showSuccessDialog(response);
            }
        });
    }
    /**
     * 文件下载成功，选择文件处理方式
     *
     * @param response
     */
    private void showSuccessDialog(final File response) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.download_complete_dialog_title));
        builder.setMessage(getString(R.string.download_complete_dialog_msg));
        builder.setCancelable(false);
        builder.setPositiveButton(getString(R.string.download_complete_dialog_pb), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //打开邮箱
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra("subject", response.getName()); //
                intent.putExtra("body", "Email from CodePad"); //正文
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(response));
                //添加附件，附件为file对象
                if (response.getName().endsWith(".gz")) {
                    intent.setType("application/x-gzip"); //如果是gz使用gzip的mime
                } else if (response.getName().endsWith(".txt")) {
                    intent.setType("text/plain"); //纯文本则用text/plain的mime
                } else {
                    intent.setType("application/octet-stream"); //其他的均使用流当做二进制数据来发送
                }
                startActivity(intent); //调用系统的mail客户端进行发送
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(getString(R.string.download_complete_dialog_nb), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent("android.intent.action.VIEW");
                intent.addCategory("android.intent.category.DEFAULT");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Uri uri = Uri.fromFile(response);
                intent.setDataAndType(uri, "application/msword");
                startActivity(intent);
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
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
