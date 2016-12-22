package com.yimiao100.sale.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.Spanned;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.yimiao100.sale.R;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.bean.ErrorBean;
import com.yimiao100.sale.bean.ResourceListBean;
import com.yimiao100.sale.callback.ProtocolFileCallBack;
import com.yimiao100.sale.utils.BitmapUtil;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.DensityUtil;
import com.yimiao100.sale.utils.FormatUtils;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.TimeUtil;
import com.yimiao100.sale.utils.ToastUtil;
import com.yimiao100.sale.utils.Util;
import com.yimiao100.sale.view.TitleView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 我的业务-第三状态-待签约
 */
public class OrderAlreadyActivity extends BaseActivity implements TitleView
        .TitleBarOnClickListener {

    @BindView(R.id.order_already_title)
    TitleView mOrderAlreadyTitle;
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
    ImageView mOrderAlreadyUpload;
    @BindView(R.id.order_already_no)
    TextView mOrderAlreadyNo;
    private AlertDialog mDialog;
    private ProgressDialog mProgressDownloadDialog;
    private String mFileName = "agreement.jpg";
    private File mFile;
    private Uri mUri;
    private ProgressDialog mProgressDialog;

    private final String URL_UPLOAD_FILE = Constant.BASE_URL + "/api/order/upload_protocol_file";
    private final String URL_DOWNLOAD_FILE = Constant.BASE_URL + "/api/order/fetch_protocol";
    private final String ORDER_ID = "orderId";
    private String mOrderId;
    private ResourceListBean mOrder;

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
    }

    private void initData() {
        Intent intent = getIntent();
        mOrder = intent.getParcelableExtra("order");
        //提交日期
        String submit_time = TimeUtil.timeStamp2Date(mOrder.getCreatedAt() + "", "yyyy年MM月dd日");
        mOrderAlreadySubmitTime.setText(submit_time + "提交的申请推广已经通过审核，\n您提交的竞标保证金已经查收。");
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
        String totalDeposit = mOrder.getSaleDeposit() + "";
        Spanned totalMoney = Html.fromHtml("推广保证金：" + "<font color=\"#4188d2\">" + totalDeposit +
                "</font>" + "(人民币)");
        mOrderAlreadyTotalMoney.setText(totalMoney);
        //协议单号
        String serialNo = mOrder.getSerialNo();
        mOrderAlreadyNo.setText("协议单号：" + serialNo);
        //总额保证金
        String orderTotalDeposit = FormatUtils.MoneyFormat(mOrder.getSaleDeposit());
        //竞标有效时间
        String defaultExpiredAt = TimeUtil.timeStamp2Date(mOrder.getDefaultExpiredAt() + "",
                "yyyy年MM月dd日");
        mOrderAlreadyRe.setText("请在" + defaultExpiredAt
                + "之前尽快将本次推广资源的保证金" + orderTotalDeposit + "转到如下账户，并按照要求下载上传电子协议。");
        //订单id
        mOrderId = mOrder.getId() + "";
        //获取是否已经阅读过免责信息
        boolean isRead = mOrder.isRead();
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
                            mOrder.setRead(true);
                            LogUtil.d("isRead?" + mOrder.isRead());
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


    @OnClick({R.id.order_already_download, R.id.order_already_upload, R.id
            .order_already_show_account})
    public void onClick(View view) {
        switch (view.getId()) {
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
        mProgressDownloadDialog.setTitle("下载中，请稍后");
        mProgressDownloadDialog.setCancelable(false);
        OkHttpUtils.post().url(URL_DOWNLOAD_FILE).addHeader(ACCESS_TOKEN, mAccessToken)
                .addParams(ORDER_ID, mOrderId).build()      //使用私人定制版FileCallBack
                .connTimeOut(1000 * 60 * 10).readTimeOut(1000 * 60 * 10).execute(new ProtocolFileCallBack() {

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
        builder.setTitle("下载成功");
        builder.setMessage("下载完成，请选择文件处理方式");
        builder.setCancelable(false);
        builder.setPositiveButton("发送出去", new DialogInterface.OnClickListener() {
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
        builder.setNegativeButton("本地打开", new DialogInterface.OnClickListener() {
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
     * 上传
     */
    private void uploadAgreement() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        CharSequence[] items = {"拍照", "从相册选择"};
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        //打开相机
                        //拍照返回
                        Intent intentCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        mFile = new File(Environment.getExternalStorageDirectory(), mFileName);
                        mUri = Uri.fromFile(mFile);
                        intentCapture.putExtra(MediaStore.EXTRA_OUTPUT, mUri);
                        startActivityForResult(intentCapture, 100);
                        break;
                    case 1:
                        //打开相册
                        //激活系统图库，选择一张图片
                        Intent intentPick = new Intent(Intent.ACTION_PICK);
                        intentPick.setType("image/*");
                        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_GALLERY
                        startActivityForResult(intentPick, 200);
                        break;
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 100) {
                //压缩显示
                Bitmap bitmap = BitmapUtil.decodeSampledBitmapFromFile(mFile, DensityUtil.dp2px
                        (this, 338), DensityUtil.dp2px(this, 150));
                try {
                    //质量压缩上传
                    BitmapUtil.compressAndGenImage(bitmap, mFile.getAbsolutePath(), 1024);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == 200) {
                if (data != null) {
                    Uri uri = data.getData();
                    //根据uri获取图片的真实路径
                    String path = BitmapUtil.getRealPathFromURI(this, uri);
                    mFile = new File(path);
                }
            }
            submitCorporateAccount();
        }
    }

    private void submitCorporateAccount() {
        String filename = mFile.getName();
        //上传进度条显示
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setTitle("正在上传，请稍后...");
        OkHttpUtils.post().url(URL_UPLOAD_FILE)
                .addHeader(ACCESS_TOKEN, mAccessToken)
                .addParams(ORDER_ID, mOrderId)
                .addFile("protocolFile", filename, mFile)
                .build().execute(new StringCallback() {
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
                LogUtil.d("progress--" + progress + "--total--" + total);
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                ToastUtil.showLong(getApplicationContext(), e.getMessage());
                LogUtil.d("已竞标E：" + e.getMessage());
                Util.showTimeOutNotice(currentContext);
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.d("已竞标：" + response);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        ToastUtil.showLong(getApplicationContext(), "提交成功");
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
