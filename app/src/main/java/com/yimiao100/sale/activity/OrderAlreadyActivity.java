package com.yimiao100.sale.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.Spanned;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import com.yimiao100.sale.utils.CompressUtil;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.FormatUtils;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.SharePreferenceUtil;
import com.yimiao100.sale.utils.TimeUtil;
import com.yimiao100.sale.utils.ToastUtil;
import com.yimiao100.sale.utils.Util;
import com.yimiao100.sale.view.TitleView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.PhotoPreview;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;
import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

/**
 * 我的业务-第三状态-待签约
 */
public class OrderAlreadyActivity extends BaseActivity implements TitleView
        .TitleBarOnClickListener {

    @BindView(R.id.order_already_title)
    TitleView mOrderAlreadyTitle;
    @BindView(R.id.order_already_service)
    ImageView mService;
    @BindView(R.id.order_already_submit_time)
    TextView mOrderAlreadySubmitTime;
    @BindView(R.id.order_already_vendor_name)
    TextView mOrderAlreadyVendorName;
    @BindView(R.id.order_already_product_name)
    TextView mOrderAlreadyCommonName;
    @BindView(R.id.order_already_region)
    TextView mOrderAlreadyRegion;
    @BindView(R.id.order_already_spec)
    TextView mOrderAlreadySpec;
    @BindView(R.id.order_already_dosage_form)
    TextView mOrderAlreadyDosageForm;
    @BindView(R.id.order_already_time)
    TextView mOrderAlreadyTime;
    @BindView(R.id.order_already_total_money)
    TextView mOrderAlreadyTotalMoney;
    @BindView(R.id.order_already_download)
    ImageView mOrderAlreadyDownload;
    @BindView(R.id.order_already_re)
    TextView mOrderAlreadyRe;
    @BindView(R.id.order_already_upload)
    Button mOrderAlreadyUpload;
    @BindView(R.id.order_already_customer)
    TextView mOrderAlreadyCustomer;
    @BindView(R.id.order_already_no)
    TextView mOrderAlreadyNo;
    private AlertDialog mDialog;
    private ProgressDialog mProgressDownloadDialog;
    private ProgressDialog mProgressUploadDialog;

    private final String URL_UPLOAD_FILE = Constant.BASE_URL +
            "/api/order/batch_upload_protocol_file";
    private final String URL_DOWNLOAD_FILE = Constant.BASE_URL + "/api/order/fetch_protocol";
    private final String ORDER_ID = "orderId";
    private final String ZIP_FILE = "zipFile";
    private String mOrderId;
    private ResourceListBean mOrder;
    private File mFile;
    private Badge mBadge;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_already);
        ButterKnife.bind(this);

        initView();

        initData();
    }

    private void initView() {
        mOrderAlreadyTitle.setOnTitleBarClick(this);
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
        Intent intent = getIntent();
        mOrder = intent.getParcelableExtra("order");
        //提交日期
        String submit_time = TimeUtil.timeStamp2Date(mOrder.getCreatedAt() + "", "yyyy年MM月dd日");
        mOrderAlreadySubmitTime.setText(submit_time + getString(R.string.order_already_notice));
        //厂家名称
        String vendorName = mOrder.getVendorName();
        mOrderAlreadyVendorName.setText(vendorName);
        //产品名称-分类名
        String categoryName = mOrder.getCategoryName();
        mOrderAlreadyCommonName.setText("产品：" + categoryName);
        //规格
        String spec = mOrder.getSpec();
        mOrderAlreadySpec.setText("规格：" + spec);
        //剂型
        String dosageForm = mOrder.getDosageForm();
        mOrderAlreadyDosageForm.setText("剂型：" + dosageForm);
        //区域
        String region = mOrder.getProvinceName() + "\t" + mOrder.getCityName() + "\t" + mOrder
                .getAreaName();
        mOrderAlreadyRegion.setText("区域：" + region);
        //时间
        String time = TimeUtil.timeStamp2Date(mOrder.getCreatedAt() + "", "yyyy.MM.dd");
        mOrderAlreadyTime.setText("时间：" + time);
        //保证金
        String totalDeposit = FormatUtils.MoneyFormat(mOrder.getSaleDeposit());
        Spanned totalMoney = Html.fromHtml("推广保证金：" + "<font color=\"#4188d2\">" + totalDeposit +
                "</font>" + "(人民币)");
        mOrderAlreadyTotalMoney.setText(totalMoney);
        // 客户
        if (mOrder.getCustomerName() != null) {
            mOrderAlreadyCustomer.setText("客户：" + mOrder.getCustomerName());
        }
        //协议单号
        String serialNo = mOrder.getSerialNo();
        mOrderAlreadyNo.setText("协议单号：" + serialNo);
        //总额保证金
        String orderTotalDeposit = FormatUtils.MoneyFormat(mOrder.getSaleDeposit());
        //竞标有效时间
        String defaultExpiredAt = TimeUtil.timeStamp2Date(mOrder.getDefaultExpiredAt() + "",
                "yyyy年MM月dd日");
        switch (mOrder.getUserAccountType()) {
            case "corporate":
                mOrderAlreadyRe.setText("请在" + defaultExpiredAt
                        + "之前，通过贵公司的对公账号将产品推广保证金" + orderTotalDeposit +
                        "转到如上账户，并且下载电子协议，认真阅读每一条协议内容。");
                break;
            case "personal":
                mOrderAlreadyRe.setText("请在" + defaultExpiredAt
                        + "之前，通过已绑定的个人银行卡将产品推广保证金" + orderTotalDeposit +
                        "转到如上账户，并且下载电子协议，认真阅读每一条协议内容。");
                break;
        }
        //订单id
        mOrderId = mOrder.getId() + "";
        //获取是否已经阅读过免责信息
        boolean isRead = (boolean) SharePreferenceUtil.get(currentContext, mOrder.getVendorName()
                + mOrderId, false);
        LogUtil.d("isRead?" + isRead);
        if (!isRead) {
            //没有阅读过，弹窗显示
            //弹窗
            showOrderDialog();
        }
    }

    /**
     * 弹窗显示免责信息
     */
    private void showOrderDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(this, R.layout.dialog_disclaimer, null);
        CheckBox order_read_already = (CheckBox) view.findViewById(R.id.order_read_already);
        final ImageView order_agree = (ImageView) view.findViewById(R.id.order_agree);
        builder.setView(view);
        //设置返回键无效
//        builder.setCancelable(false);
        mDialog = builder.create();
        //监听复选框选中状态。
        order_read_already.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //选中时，“同意”可点击，变成蓝色
                if (isChecked) {
                    order_agree.setClickable(true);
                    order_agree.setImageResource(R.mipmap.ico_my_order_agree_activation);
                    order_agree.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //设置记录
                            SharePreferenceUtil.put(currentContext, mOrder.getVendorName() +
                                    mOrderId, true);
                            mDialog.dismiss();
                        }
                    });
                } else {
                    //不可点击
                    order_agree.setClickable(false);
                    order_agree.setImageResource(R.mipmap.ico_my_order_agree_default);
                }
            }
        });
        //当Dialog弹出时，按下返回键，直接结束Activity
        mDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (mDialog.isShowing()) {
                        mDialog.dismiss();
                        OrderAlreadyActivity.this.finish();
                        return false;
                    }
                }
                return false;
            }
        });
        mDialog.show();
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

    @OnClick({R.id.order_already_service, R.id.order_already_download, R.id.order_already_upload,
            R.id.order_already_show_account})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.order_already_service:
                //联系客服
                Util.enterCustomerService(this);
                break;
            case R.id.order_already_download:
                //下载电子协议
                downloadAgreement();
                break;
            case R.id.order_already_show_account:
                //显示收款账户
                showAccountDialog();
                break;
            case R.id.order_already_upload:
                //上传电子协议
                uploadAgreement();
                break;
        }
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
                .addParams(ORDER_ID, mOrderId).build()      //使用私人定制版FileCallBack
                .connTimeOut(1000 * 60 * 10).readTimeOut(1000 * 60 * 10).execute(new ProtocolFileCallBack(fileHead) {

            @Override
            public void onBefore(Request request, int id) {
                super.onBefore(request, id);
                mProgressDownloadDialog.show();
            }

            @Override
            public void inProgress(float progress, long total, int id) {
                super.inProgress(progress, total, id);
                LogUtil.d("progress：" + progress);
                LogUtil.d("total：" + total);
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


    /**
     * 显示账户信息
     */
    private void showAccountDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.dialog);
        View view = View.inflate(this, R.layout.dialog_account, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * 选择多张图片上传
     */
    private void uploadAgreement() {
        PhotoPicker.builder()
                .setPhotoCount(9)
                .setShowCamera(false)
                .setPreviewEnabled(false)
                .start(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK &&
                (requestCode == PhotoPicker.REQUEST_CODE || requestCode == PhotoPreview
                        .REQUEST_CODE)) {
            //图片选择完成
            ArrayList<String> photos;
            if (data != null) {
                photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                for (String photo : photos) {
                    LogUtil.d(photo);
                }
                String fileName = "order" + System.currentTimeMillis() + ".zip";
                //将文件压缩到本地
                mFile = CompressUtil.zipANDSave(photos, fileName);
                if (mFile != null) {
                    upload(mFile, fileName);
                } else {
                    //提示获取文件失败
                    ToastUtil.showShort(currentContext, "文件获取失败，请稍后重试");
                }
            }
        }
    }

    /**
     * 上传压缩文件
     *
     * @param file
     * @param fileName
     */
    private void upload(File file, String fileName) {
        //上传进度条显示
        mProgressUploadDialog = new ProgressDialog(this);
        mProgressUploadDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressUploadDialog.setCancelable(false);
        mProgressUploadDialog.setTitle(getString(R.string.upload_progress_dialog_title));
        final ProgressDialog responseDialog = new ProgressDialog(this);
        responseDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        responseDialog.setCancelable(false);
        responseDialog.setMessage(getString(R.string.response_progress_dialog_title));
        OkHttpUtils.post().url(URL_UPLOAD_FILE)
                .addHeader(ACCESS_TOKEN, accessToken)
                .addFile(ZIP_FILE, fileName, file)
                .addParams(ORDER_ID, mOrderId).build()
                .connTimeOut(1000 * 60 * 10)
                .readTimeOut(1000 * 60 * 10)
                .writeTimeOut(1000 * 60 * 10).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                e.printStackTrace();
                Util.showTimeOutNotice(currentContext);
            }

            @Override
            public void onBefore(Request request, int id) {
                super.onBefore(request, id);
                mProgressUploadDialog.show();
            }

            @Override
            public void inProgress(float progress, long total, int id) {
                super.inProgress(progress, total, id);
                LogUtil.d("progress" + progress);
                mProgressUploadDialog.setProgress((int) (100 * progress - 0.5));
                if (progress == 1f) {
                    mProgressUploadDialog.dismiss();
                    responseDialog.show();
                }
            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
                if (responseDialog.isShowing()) {
                    responseDialog.dismiss();
                }
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.d(response);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        if (mFile != null) {
                            mFile.delete();
                        }
                        ToastUtil.showShort(currentContext, "上传成功");
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
