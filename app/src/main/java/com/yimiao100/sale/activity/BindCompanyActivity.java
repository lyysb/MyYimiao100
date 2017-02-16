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
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.squareup.picasso.Picasso;
import com.yimiao100.sale.R;
import com.yimiao100.sale.adapter.listview.ExperienceAdapter;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.bean.CorporateBean;
import com.yimiao100.sale.bean.ErrorBean;
import com.yimiao100.sale.bean.Experience;
import com.yimiao100.sale.bean.UserBean;
import com.yimiao100.sale.utils.BitmapUtil;
import com.yimiao100.sale.utils.CompressUtil;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.DensityUtil;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.SharePreferenceUtil;
import com.yimiao100.sale.utils.TimeUtil;
import com.yimiao100.sale.utils.ToastUtil;
import com.yimiao100.sale.utils.Util;
import com.yimiao100.sale.view.TitleView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Request;


/**
 * 推广主体-绑定对公主体界面
 */
public class BindCompanyActivity extends BaseActivity implements TitleView
        .TitleBarOnClickListener, View.OnClickListener {

    @BindView(R.id.bind_company_title)
    TitleView mBindCompanyTitle;
    @BindView(R.id.bind_company_list_view)
    ListView mBindCompanyListView;
    EditText mBindCompanyName;                              //账户名称
    EditText mBindCompanyBankCard;                          //公司账号
    EditText mBindCompanyBankName;                          //开户银行
    EditText mBindCompanyPhone;                             //固定电话(选填)
    EditText mBindCompanyCorporation;                       //姓名
    EditText mBindCompanyCorporationPhoneNumber;            //电话
    EditText mBindCompanyCorporationQQ;                     //QQ
    EditText mBindCompanyCorporationEmail;                  //邮箱
    EditText mBindCompanyCorporationIdNumber;               //身份证号
    ImageView mBindCompanyTakePhoto;                        //营业执照
    ImageView mBindCompanyCardPhoto1;                       //证件照1
    ImageView mBindCompanyCardPhoto2;                       //证件照2
    ImageView mAddExperience;                               //添加经历
    EditText mCompanyEver;                                  //曾经所在公司及职位
    EditText mPromotionAdvantage;                           //推广优势
    Button mBindCompanySubmit;                              //提交按钮


    private final String URL_CORPORATE_USER_ACCOUNT = Constant.BASE_URL +
            "/api/user/post_corporate_user_account";
    private final String ULR_GET_CORPORATE_ACCOUNT = Constant.BASE_URL +
            "/api/user/get_user_account";
    private final String ACCOUNT_NAME = "accountName";                      //账户名称
    private final String CORPORATE_ACCOUNT = "corporateAccount";            //公司账号
    private final String BANK_NAME = "bankName";                            //开户银行
    private final String CORPORATE_PHONE_NUMBER = "corporatePhoneNumber";    //公司电话号码
    private final String CN_NAME = "cnName";                                //个人姓名
    private final String PERSONAL_PHONE_NUMBER = "personalPhoneNumber";     //个人电话号码
    private final String QQ = "qq";                                         //QQ
    private final String EMAIL = "email";                                   //邮箱
    private final String ID_NUMBER = "idNumber";                            //身份证号
    private final String EXPERIENCE = "experience";                         //工作经历
    private final String ADVANTAGE = "advantage";                           //推广优势
    private final String EXPERIENCE_LIST = "experienceList";                //推广经历
    private final String ZIP_FILE = "zipFile";                              //压缩文件


    private static final int BIZ_FROM_CAMERA = 100;
    private static final int PERSONAL_FROM_CAMERA = 101;
    private static final int ID_FROM_CAMERA = 102;
    private static final int BIZ_FROM_PHOTO = 200;
    private static final int PERSONAL_FROM_PHOTO = 201;
    private static final int ID_FROM_PHOTO = 202;
    private static final int ADD_EXPERIENCE = 300;

    private final int WIDTH = 300;
    private final int HEIGHT = 80;
    private final int MAX_SIZE = 250;


    private String mAccountName;
    private String mCorporateAccount;
    private String mBankName;
    private String mCorporatePhoneNumber;
    private String mCnName;
    private String mPersonalPhoneNumber;
    private String mQQ;
    private String mEmail;
    private String mIdNumber;
    private String mExperience;
    private String mAdvantage;
    private String mExperienceList;

    private File mBizLicence;
    private File mPersonalPhoto;
    private File mIdPhoto;
    private File mZipFile;

    private String mBizLicenceName = "bizLicence.jpg";
    private String mPersonalPhotoName = "personalPhoto.jpg";
    private String mIdPhotoName = "idPhoto.jpg";
    private String mZipName;

    private boolean isFirst = true;     // 是否第一次提交，默认为true


    private HashMap<String, String> mFileMap = new HashMap<>();


    private ProgressDialog mProgressDialog;
    private ArrayList<Experience> mList;
    private ExperienceAdapter mAdapter;
    private AlertDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_company);
        ButterKnife.bind(this);

        initView();

        initData();
    }

    private void initView() {
        initHeaderView();

        initFooterView();

        mList = new ArrayList<>();
        mAdapter = new ExperienceAdapter(mList);
        mBindCompanyListView.setAdapter(mAdapter);

        mBindCompanyTitle.setOnTitleBarClick(this);
    }

    private void initHeaderView() {
        View headerView = View.inflate(this, R.layout.head_bind_company, null);
        //开户名称
        mBindCompanyName = (EditText) headerView.findViewById(R.id.bind_company_name);
        //公司账号
        mBindCompanyBankCard = (EditText) headerView.findViewById(R.id.bind_company_bank_card);
        //开户银行
        mBindCompanyBankName = (EditText) headerView.findViewById(R.id.bind_company_bank_name);
        //固定电话(选填)
        mBindCompanyPhone = (EditText) headerView.findViewById(R.id.bind_company_phone);
        //姓名
        mBindCompanyCorporation = (EditText) headerView.findViewById(R.id.bind_company_corporation);
        //电话
        mBindCompanyCorporationPhoneNumber = (EditText) headerView.findViewById(R.id
                .bind_company_corporation_phone_number);
        //QQ
        mBindCompanyCorporationQQ = (EditText) headerView.findViewById(R.id
                .bind_company_corporation_qq);
        //邮箱
        mBindCompanyCorporationEmail = (EditText) headerView.findViewById(R.id
                .bind_company_corporation_email);
        //身份证号
        mBindCompanyCorporationIdNumber = (EditText) headerView.findViewById(R.id
                .bind_company_corporation_Id_number);
        //营业执照
        mBindCompanyTakePhoto = (ImageView) headerView.findViewById(R.id.bind_company_take_photo);
        mBindCompanyTakePhoto.setOnClickListener(this);
        //证件照1
        mBindCompanyCardPhoto1 = (ImageView) headerView.findViewById(R.id.bind_company_card_photo1);
        mBindCompanyCardPhoto1.setOnClickListener(this);
        //证件照2
        mBindCompanyCardPhoto2 = (ImageView) headerView.findViewById(R.id.bind_company_card_photo2);
        mBindCompanyCardPhoto2.setOnClickListener(this);
        //添加推广经历
        mAddExperience = (ImageView) headerView.findViewById(R.id.bind_company_add);
        mAddExperience.setOnClickListener(this);

        mBindCompanyListView.addHeaderView(headerView);
    }

    private void initFooterView() {
        View footerView = View.inflate(this, R.layout.foot_bind_company, null);
        //曾经所在公司
        mCompanyEver = (EditText) footerView.findViewById(R.id.bind_company_ever);
        //推广优势
        mPromotionAdvantage = (EditText) footerView.findViewById(R.id.bind_company_advantage);
        //提交
        mBindCompanySubmit = (Button) footerView.findViewById(R.id.bind_company_submit);
        mBindCompanySubmit.setOnClickListener(this);

        mBindCompanyListView.addFooterView(footerView);
    }

    private void initData() {
        OkHttpUtils.post().url(ULR_GET_CORPORATE_ACCOUNT).addHeader(ACCESS_TOKEN, mAccessToken)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.Companion.d("推广主体E：" + e.getLocalizedMessage());
                Util.showTimeOutNotice(currentContext);
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.Companion.d("推广主体：" + response);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        //回显网络数据
                        echoData(JSON.parseObject(response, UserBean.class).getUserAccount()
                                .getCorporate());
                        break;
                    case "failure":
                        Util.showError(currentContext, errorBean.getReason());
                        break;
                }
            }
        });
    }

    /**
     * 回显网络数据
     *
     * @param corporate
     */
    private void echoData(CorporateBean corporate) {
        if (corporate != null) {
            LogUtil.Companion.d("已有账户提交");
            //不是第一次提交
            isFirst = false;
            //开户名称
            mBindCompanyName.setText(corporate.getAccountName());
            //公司账号
            mBindCompanyBankCard.setText(corporate.getCorporateAccount());
            //开户银行
            mBindCompanyBankName.setText(corporate.getBankName());
            //固定电话(选填)
            String corporatePhoneNumber = corporate.getCorporatePhoneNumber();
            if (!TextUtils.equals(corporatePhoneNumber, "")) {
                mBindCompanyPhone.setText(corporatePhoneNumber);
            }
            //姓名
            mBindCompanyCorporation.setText(corporate.getCnName());
            //电话
            mBindCompanyCorporationPhoneNumber.setText(corporate.getPersonalPhoneNumber());
            //QQ
            mBindCompanyCorporationQQ.setText(corporate.getQq());
            //邮箱
            mBindCompanyCorporationEmail.setText(corporate.getEmail());
            //身份证号
            mBindCompanyCorporationIdNumber.setText(corporate.getIdNumber());
            //显示推广经历
            mList.addAll(corporate.getExperienceList());
            mAdapter.notifyDataSetChanged();
            //曾经所在公司
            mCompanyEver.setText(corporate.getExperience());
            //推广优势
            mPromotionAdvantage.setText(corporate.getAdvantage());
            //三张图片url
            String bizUrl = corporate.getBizLicenceUrl();
            LogUtil.Companion.d("bizUrl-" + bizUrl);
            String personalUrl = corporate.getPersonalPhotoUrl();
            LogUtil.Companion.d("personalUrl-" + personalUrl);
            String idUrl = corporate.getIdPhotoUrl();
            LogUtil.Companion.d("idUrl-" + idUrl);

            Picasso.with(getApplicationContext()).load(bizUrl)
                    .placeholder(R.mipmap.ico_binding_account_add_photos)
                    .transform(BitmapUtil.getTransformation(mBindCompanyTakePhoto))
                    .into(mBindCompanyTakePhoto);
            Picasso.with(getApplicationContext()).load(personalUrl)
                    .transform(BitmapUtil.getTransformation(mBindCompanyCardPhoto1))
                    .placeholder(R.mipmap.ico_binding_account_certificates)
                    .into(mBindCompanyCardPhoto1);
            Picasso.with(getApplicationContext()).load(idUrl)
                    .transform(BitmapUtil.getTransformation(mBindCompanyCardPhoto2))
                    .placeholder(R.mipmap.ico_binding_account_certificates_two)
                    .error(R.mipmap.ico_binding_account_certificates_two)
                    .into(mBindCompanyCardPhoto2);

            //如果审核通过，则不再允许修任何改数据
            if (corporate.getAccountStatus() != null && TextUtils.equals("passed", corporate
                    .getAccountStatus())) {
                LogUtil.Companion.d("已通过审核，禁止修改任何数据");
                //禁止修改数据
                forbidChange();
            }
        } else {
            LogUtil.Companion.d("第一次提交");
            // 是第一次提交
            isFirst = true;
        }

    }


    /**
     * 禁止修改数据
     */
    private void forbidChange() {
        mBindCompanyName.setKeyListener(null);
        mBindCompanyBankCard.setKeyListener(null);
        mBindCompanyBankName.setKeyListener(null);
        mBindCompanyPhone.setKeyListener(null);
        mBindCompanyCorporation.setKeyListener(null);
        mBindCompanyCorporationPhoneNumber.setKeyListener(null);
        mBindCompanyCorporationQQ.setKeyListener(null);
        mBindCompanyCorporationEmail.setKeyListener(null);
        mBindCompanyCorporationIdNumber.setKeyListener(null);
        mCompanyEver.setKeyListener(null);
        mPromotionAdvantage.setKeyListener(null);
        mBindCompanyTakePhoto.setEnabled(false);
        mBindCompanyCardPhoto1.setEnabled(false);
        mBindCompanyCardPhoto2.setEnabled(false);
        mAddExperience.setEnabled(false);
        mBindCompanySubmit.setEnabled(false);
    }


    @Override
    @OnClick({R.id.bind_company_service})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bind_company_service:
                //联系客服
                Util.enterCustomerService(this);
                break;
            case R.id.bind_company_take_photo:
                //拍摄营业执照
                getBiz();
                break;
            case R.id.bind_company_card_photo1:
                //拍摄证件照1
                getPhoto1();
                break;
            case R.id.bind_company_card_photo2:
                //拍摄证件照2
                getPhoto2();
                break;
            case R.id.bind_company_add:
                //添加经历
                startActivityForResult(new Intent(this, PromotionExperienceActivity.class),
                        ADD_EXPERIENCE);
                break;
            case R.id.bind_company_submit:
                if (mBindCompanyName.getText().toString().trim().isEmpty()) {
                    ToastUtil.showShort(this, "请填写开户名称");
                    return;
                }
                if (mBindCompanyBankCard.getText().toString().trim().isEmpty()) {
                    ToastUtil.showShort(this, "请填写公司账号");
                    return;
                }
                if (mBindCompanyBankName.getText().toString().trim().isEmpty()) {
                    ToastUtil.showShort(this, "请填写开户银行");
                    return;
                }

                // 提示营业执照
                if (isFirst && mBizLicence == null) {
                    ToastUtil.showLong(getApplicationContext(), "初次上传，请拍摄营业执照");
                    return;
                }

                if (mBindCompanyCorporation.getText().toString().trim().isEmpty()) {
                    ToastUtil.showShort(this, "请填写姓名");
                    return;
                }
                if (mBindCompanyCorporationIdNumber.getText().toString().trim().isEmpty()) {
                    ToastUtil.showShort(this, "请填写身份账号");
                    return;
                }
                if (mBindCompanyCorporationPhoneNumber.getText().toString().trim().isEmpty()) {
                    ToastUtil.showShort(this, "请填写电话");
                    return;
                }
                if (mBindCompanyCorporationQQ.getText().toString().trim().isEmpty()) {
                    ToastUtil.showShort(this, "请填写QQ号码");
                    return;
                }
                if (mBindCompanyCorporationEmail.getText().toString().trim().isEmpty()) {
                    ToastUtil.showShort(this, "请填写邮箱");
                    return;
                }
                // 提示个人证件照
                if (isFirst && (mPersonalPhoto == null || mIdPhoto == null)) {
                    ToastUtil.showLong(getApplicationContext(), "初次上传，请拍摄证件照");
                    return;
                }

                if (mList.size() == 0) {
                    ToastUtil.showShort(currentContext, "请填写推广经历");
                    return;
                }
                if (mCompanyEver.getText().toString().trim().isEmpty()) {
                    ToastUtil.showShort(this, "曾经所在公司及职位");
                    return;
                }
                if (mPromotionAdvantage.getText().toString().trim().isEmpty()) {
                    ToastUtil.showShort(this, "请填写推广优势");
                    return;
                }
                //显示提交确定弹窗
                showConfirmDialog();
                break;
            case R.id.dialog_check:
                //Dialog消失
                mDialog.dismiss();
                break;
            case R.id.dialog_confirm:
                //Dialog消失
                mDialog.dismiss();
                //如果有图片，则压缩图片
                if (mBizLicence != null || mPersonalPhoto != null || mIdPhoto != null) {
                    // 压缩图片
                    mZipName = "promotions" + TimeUtil.timeStamp2Date(System.currentTimeMillis()+"", "yyyyMMdd_HHssmm") + ".zip";
                    // 压缩文件
                    mZipFile = CompressUtil.zipANDSave(mFileMap, mZipName);
                    if (mZipFile == null) {
                        // 提示错误并返回
                        ToastUtil.showShort(currentContext, "文件压缩出现未知错误，暂无法上传图片");
                        return;
                    }
                }
                //提交数据
                submitCorporateAccount();
                break;
        }
    }


    /**
     * 显示提交确定弹窗
     */
    private void showConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.dialog);
        View view = View.inflate(this, R.layout.dialog_confirm_submit, null);
        builder.setView(view);
        mDialog = builder.create();
        mDialog.show();
        Button check = (Button) view.findViewById(R.id.dialog_check);
        check.setOnClickListener(this);
        Button submit = (Button) view.findViewById(R.id.dialog_confirm);
        submit.setOnClickListener(this);
    }

    /**
     * 获取营业执照
     */
    private void getBiz() {
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
                        mBizLicence = new File(Environment.getExternalStorageDirectory(),
                                mBizLicenceName);
                        intentCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mBizLicence));
                        startActivityForResult(intentCapture, BIZ_FROM_CAMERA);
                        break;
                    case 1:
                        //打开相册
                        //激活系统图库，选择一张图片
                        Intent intentPick = new Intent(Intent.ACTION_PICK);
                        intentPick.setType("image/*");
                        startActivityForResult(intentPick, BIZ_FROM_PHOTO);
                        break;
                }
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * 获取个人手持证件照
     */
    private void getPhoto1() {
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
                        mPersonalPhoto = new File(Environment.getExternalStorageDirectory(),
                                mPersonalPhotoName);
                        intentCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile
                                (mPersonalPhoto));
                        startActivityForResult(intentCapture, PERSONAL_FROM_CAMERA);
                        break;
                    case 1:
                        //打开相册
                        //激活系统图库，选择一张图片
                        Intent intentPick = new Intent(Intent.ACTION_PICK);
                        intentPick.setType("image/*");
                        startActivityForResult(intentPick, PERSONAL_FROM_PHOTO);
                        break;
                }
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * 获取身份证照片
     */
    private void getPhoto2() {
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
                        mIdPhoto = new File(Environment.getExternalStorageDirectory(),
                                mIdPhotoName);
                        intentCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mIdPhoto));
                        startActivityForResult(intentCapture, ID_FROM_CAMERA);
                        break;
                    case 1:
                        //打开相册
                        //激活系统图库，选择一张图片
                        Intent intentPick = new Intent(Intent.ACTION_PICK);
                        intentPick.setType("image/*");
                        startActivityForResult(intentPick, ID_FROM_PHOTO);
                        break;
                }
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    /**
     * 提交数据
     */
    private void submitCorporateAccount() {
        //禁止重复点击
        mBindCompanySubmit.setEnabled(false);
        //获取参数
        //账户名称
        mAccountName = mBindCompanyName.getText().toString().trim();
        //公司账号
        mCorporateAccount = mBindCompanyBankCard.getText().toString().trim();
        //开户银行
        mBankName = mBindCompanyBankName.getText().toString().trim();
        //公司电话号码
        mCorporatePhoneNumber = mBindCompanyPhone.getText().toString().trim();
        //个人姓名
        mCnName = mBindCompanyCorporation.getText().toString().trim();
        //个人电话号码
        mPersonalPhoneNumber = mBindCompanyCorporationPhoneNumber.getText().toString().trim();
        //QQ
        mQQ = mBindCompanyCorporationQQ.getText().toString().trim();
        //邮箱
        mEmail = mBindCompanyCorporationEmail.getText().toString().trim();
        //身份证号
        mIdNumber = mBindCompanyCorporationIdNumber.getText().toString().trim();
        //推广经历
        mExperienceList = JSON.toJSONString(mList);
        LogUtil.Companion.d("推广经历：" + mExperienceList);
        //工作经历
        mExperience = mCompanyEver.getText().toString().trim();
        //推广优势
        mAdvantage = mPromotionAdvantage.getText().toString().trim();
        //上传进度条显示
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setTitle("正在上传，请稍后...");
        //请求网络发送数据
        OkHttpUtils.post().url(URL_CORPORATE_USER_ACCOUNT).addHeader(ACCESS_TOKEN, mAccessToken)
                .addParams(ACCOUNT_NAME, mAccountName)
                .addParams(CORPORATE_ACCOUNT, mCorporateAccount)
                .addParams(BANK_NAME, mBankName)
                .addParams(CORPORATE_PHONE_NUMBER, mCorporatePhoneNumber)
                .addParams(CN_NAME, mCnName)
                .addParams(PERSONAL_PHONE_NUMBER, mPersonalPhoneNumber)
                .addParams(QQ, mQQ)
                .addParams(EMAIL, mEmail)
                .addParams(ID_NUMBER, mIdNumber)
                .addParams(EXPERIENCE, mExperience)
                .addParams(ADVANTAGE, mAdvantage)
                .addParams(EXPERIENCE_LIST, mExperienceList)
                .addFile(ZIP_FILE, mZipName, mZipFile)
                .build().connTimeOut(10 * 60 * 1000)
                .readTimeOut(10 * 60 * 1000)
                .writeTimeOut(10 * 60 * 1000).execute(new StringCallback() {
            @Override
            public void onBefore(Request request, int id) {
                super.onBefore(request, id);
                mProgressDialog.show();
                LogUtil.Companion.d("onBefore");
            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
                mProgressDialog.dismiss();
                LogUtil.Companion.d("onAfter");
            }

            @Override
            public void inProgress(float progress, long total, int id) {
                super.inProgress(progress, total, id);
                mProgressDialog.setProgress((int) (100 * progress + 0.5));
                LogUtil.Companion.d("inProgress");
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                //允许按钮点击
                mBindCompanySubmit.setEnabled(true);
                e.printStackTrace();
                Util.showTimeOutNotice(currentContext);
            }

            @Override
            public void onResponse(String response, int id) {
                //允许按钮点击
                mBindCompanySubmit.setEnabled(true);
                LogUtil.Companion.d("onResponse");
                LogUtil.Companion.d("绑定对公主体：" + response);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        //记录对公账户数据
                        //账户存在
                        SharePreferenceUtil.put(getApplicationContext(),
                                Constant.CORPORATE_EXIT, true);
                        //账户名称
                        SharePreferenceUtil.put(getApplicationContext(), Constant
                                .CORPORATE_ACCOUNT_NAME, mAccountName);
                        //公司账号
                        SharePreferenceUtil.put(getApplicationContext(), Constant
                                .CORPORATE_ACCOUNT_NUMBER, mCorporateAccount);
                        //开户银行
                        SharePreferenceUtil.put(getApplicationContext(), Constant
                                .CORPORATE_BANK_NAME, mBankName);
                        //公司电话号码
                        SharePreferenceUtil.put(getApplicationContext(), Constant
                                .CORPORATE_PHONE_NUMBER, mCorporatePhoneNumber);
                        //个人姓名
                        SharePreferenceUtil.put(getApplicationContext(), Constant
                                .CORPORATE_CN_NAME, mCnName);
                        //个人电话号码
                        SharePreferenceUtil.put(getApplicationContext(), Constant
                                .CORPORATION_PERSONAL_PHONE_NUMBER, mPersonalPhoneNumber);
                        //邮箱
                        SharePreferenceUtil.put(getApplicationContext(), Constant
                                .CORPORATE_EMAIL, mEmail);
                        //身份证号
                        SharePreferenceUtil.put(getApplicationContext(), Constant
                                .CORPORATE_ID_NUMBER, mIdNumber);

                        ToastUtil.showLong(getApplicationContext(), "提交成功，请等待审核");
                        finish();
                        break;
                    case "failure":
                        Util.showError(currentContext, errorBean.getReason());
                        break;
                }
            }
        });
    }

    /**
     * 处理返回的数据
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case BIZ_FROM_CAMERA:
                if (resultCode == RESULT_OK) {
                    handlePhoto("bizLicence", mBizLicence, mBindCompanyTakePhoto);
                }
                break;
            case PERSONAL_FROM_CAMERA:
                if (resultCode == RESULT_OK) {
                    handlePhoto("personalPhoto", mPersonalPhoto, mBindCompanyCardPhoto1);
                }
                break;
            case ID_FROM_CAMERA:
                if (resultCode == RESULT_OK) {
                    handlePhoto("idPhoto", mIdPhoto, mBindCompanyCardPhoto2);
                }
                break;
            case BIZ_FROM_PHOTO:
                if (data != null && resultCode == RESULT_OK) {
                    mBizLicence = new File(BitmapUtil.getRealPathFromURI(this, data.getData()));
                    handlePhoto("bizLicence", mBizLicence, mBindCompanyTakePhoto);
                }
                break;
            case PERSONAL_FROM_PHOTO:
                if (data != null && resultCode == RESULT_OK) {
                    mPersonalPhoto = new File((BitmapUtil.getRealPathFromURI(this, data.getData())));
                    handlePhoto("personalPhoto", mPersonalPhoto, mBindCompanyCardPhoto1);
                }
                break;
            case ID_FROM_PHOTO:
                if (data != null && resultCode == RESULT_OK) {
                    mIdPhoto = new File((BitmapUtil.getRealPathFromURI(this, data.getData())));
                    handlePhoto("idPhoto", mIdPhoto, mBindCompanyCardPhoto2);
                }
                break;
            case ADD_EXPERIENCE:
                if (resultCode == ADD_EXPERIENCE) {
                    //添加经历条目，刷新数据
                    Experience experience = data.getParcelableExtra("experience");
                    LogUtil.Companion.d("推广经历条目：" + experience.toString());
                    mList.add(experience);
                    mAdapter.notifyDataSetChanged();
                }
                break;
        }
    }

    /**
     * 添加到压缩文件集合中
     * @param childFileName 压缩文件中单个文件的名字
     * @param file  压缩文件中的单个文件
     * @param imageView 前台回显数据
     */
    private void handlePhoto(String childFileName, File file, ImageView imageView) {
        // 压缩文件内单个文件的名字
        String key = childFileName + file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("."));
        // 压缩文件内单个文件的真实路径
        String val = file.getAbsolutePath();
        mFileMap.put(key, val);
        LogUtil.Companion.d("key：" + key);
        LogUtil.Companion.d("val：" + val);
        //压缩回显
        Bitmap bitmap = BitmapUtil.decodeSampledBitmapFromFile(file, DensityUtil.dp2px(this,
                WIDTH), DensityUtil.dp2px(this, HEIGHT));
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        }
    }


    @Override
    public void leftOnClick() {
        finish();
    }

    @Override
    public void rightOnClick() {

    }


}
