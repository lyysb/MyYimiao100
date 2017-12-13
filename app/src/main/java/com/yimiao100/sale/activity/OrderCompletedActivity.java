package com.yimiao100.sale.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.meiqia.core.MQManager;
import com.meiqia.core.bean.MQMessage;
import com.meiqia.core.callback.OnGetMessageListCallback;
import com.yimiao100.sale.R;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.bean.ErrorBean;
import com.yimiao100.sale.bean.Event;
import com.yimiao100.sale.bean.ResourceListBean;
import com.yimiao100.sale.callback.ProtocolFileCallBack;
import com.yimiao100.sale.ext.JSON;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.FormatUtils;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.TimeUtil;
import com.yimiao100.sale.utils.ToastUtil;
import com.yimiao100.sale.utils.Util;
import com.yimiao100.sale.view.TitleView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;
import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

/**
 * 我的业务-第四状态-已签约
 */
public class OrderCompletedActivity extends BaseActivity implements TitleView.TitleBarOnClickListener {

    @BindView(R.id.order_complete_title)
    TitleView mOrderCompleteTitle;
    @BindView(R.id.order_complete_service)
    ImageView mService;
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
    @BindView(R.id.order_complete_customer)
    TextView mOrderCompleteCustomer;
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
    private Badge mBadge;

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
        mBadge = new QBadgeView(this).bindTarget(mService)
                .setBadgePadding(4, true)
                .setGravityOffset(9, true)
                .setShowShadow(false);
        MQManager.getInstance(this).getUnreadMessages(new OnGetMessageListCallback() {
            @Override
            public void onSuccess(List<MQMessage> list) {
                if (list.size() != 0) {
                    mBadge.setBadgeNumber(-1);
                }
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
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
        if (mOrder.getCustomerName() != null) {
            mOrderCompleteCustomer.setText("客户：" + mOrder.getCustomerName());
        }
        mSerialNo = mOrder.getSerialNo();
        mOrderCompleteSerialNo.setText("协议单号：" + mSerialNo);
        mOrderProtocolUrl = mOrder.getOrderProtocolUrl();

        mProductName = mOrder.getProductName();
        mCustomerName = mOrder.getCustomerName();

    }

    @Override
    public void onEventMainThread(@NotNull Event event) {
        super.onEventMainThread(event);
        switch (Event.eventType) {
            case RECEIVE_MSG:
                // 收到客服消息，显示小圆点
                mBadge.setBadgeNumber(-1);
                break;
            case READ_MSG:
                // 设置小圆点为0
                mBadge.setBadgeNumber(0);
                break;
            default:
                LogUtil.d("unknown event type is " + Event.eventType);
                break;
        }
    }

    @OnClick({R.id.order_complete_service, R.id.order_complete_into, R.id.order_complete_view, R.id.order_complete_download})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.order_complete_service:
                //联系客服
                Util.enterCustomerService(this);
                break;
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
        ReconciliationDetailActivity.start(this, mOrderId, mCategoryName, mProductName, mVendorName,
                mCustomerName, mDosageForm, mSpec, mSerialNo,
                mOrder.getUserAccountType(), mOrder.getVendorId(),
                mOrder.getVendorLogoImageUrl());
    }

    private void downloadAlreadyAgreement(String orderProtocolUrl) {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setTitle(getString(R.string.download_progress_dialog_title));
        String fileName = orderProtocolUrl.substring(orderProtocolUrl.lastIndexOf("/") + 1);
        LogUtil.d("已签约协议fileName：" + fileName);
        OkHttpUtils.get().url(orderProtocolUrl)
                .build().execute(
                new FileCallBack(Util.getApkPath(), fileName) {
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
                            Uri uri;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                uri = FileProvider.getUriForFile(currentContext, "com.yimiao100.sale.fileprovider", response);
                            } else {
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                uri = Uri.fromFile(response);
                            }
                            intent.setDataAndType(uri, "image/*");
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
        OkHttpUtils.post().url(URL_DOWNLOAD_FILE).addHeader(ACCESS_TOKEN, accessToken)
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
                    LogUtil.d("下载成功");
                    return super.parseNetworkResponse(response, id);
                } else if (400 == response.code()) {
                    //解析显示错误信息
                    LogUtil.d("下载失败");
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
                LogUtil.d("onResponse：" + response.getAbsolutePath());
                LogUtil.d("response.name：" + response.getName());
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
                Uri uri;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    uri = FileProvider.getUriForFile(currentContext, "com.yimiao100.sale.fileprovider", response);
                } else {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    uri = Uri.fromFile(response);
                }
                intent.putExtra(Intent.EXTRA_STREAM, uri);
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
                Uri uri;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    uri = FileProvider.getUriForFile(currentContext, "com.yimiao100.sale.fileprovider", response);
                } else {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    uri = Uri.fromFile(response);
                }
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
