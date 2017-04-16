package com.yimiao100.sale.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yimiao100.sale.R;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.bean.CorporateBean;
import com.yimiao100.sale.bean.ErrorBean;
import com.yimiao100.sale.bean.Experience;
import com.yimiao100.sale.bean.UserBean;
import com.yimiao100.sale.ext.JSON;
import com.yimiao100.sale.utils.BitmapUtil;
import com.yimiao100.sale.utils.CompressUtil;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.DensityUtil;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.Regex;
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
import java.util.Map;

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
    EditText mCompanyName;                              //账户名称
    EditText mCompanyBankCard;                          //公司账号
    EditText mCompanyBankName;                          //开户银行
    EditText mCompanyPhone;                             //固定电话(选填)
    EditText mCorporation;                       //姓名
    EditText mCorporationPhoneNumber;            //电话
    EditText mCorporationQQ;                     //QQ
    EditText mCorporationEmail;                  //邮箱
    EditText mCorporationIdNumber;               //身份证号
    ImageView mBindCompanyTakePhoto;                        //营业执照
    ImageView mBindCompanyCardPhoto1;                       //证件照1
    ImageView mBindCompanyCardPhoto2;                       //证件照2
    TextView mAddExperience;                               //添加经历
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

    private final String bizLicence =  "bizLicence";
    private final String personalPhoto = "personalPhoto";
    private final String idPhoto = "idPhoto";


    private static final int BIZ_FROM_CAMERA = 100;
    private static final int PERSONAL_FROM_CAMERA = 101;
    private static final int ID_FROM_CAMERA = 102;
    private static final int BIZ_FROM_PHOTO = 200;
    private static final int PERSONAL_FROM_PHOTO = 201;
    private static final int ID_FROM_PHOTO = 202;
    private static final int CORPORATE = 1;

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

    private String mZipName;

    private boolean isFirst = true;     // 是否第一次提交，默认为true

    private HashMap<String, String> mFileMap = new HashMap<>();

    private ProgressDialog mProgressDialog;
    private ArrayList<Experience> mList;
    private AlertDialog mDialog;
    private String mAccountStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_company);
        ButterKnife.bind(this);

        initView();

        initData();

        showLoadingProgress();
    }

    private void initView() {
        //开户名称
        mCompanyName = (EditText) findViewById(R.id.bind_company_name);
        //公司账号
        mCompanyBankCard = (EditText) findViewById(R.id.bind_company_bank_card);
        //开户银行
        mCompanyBankName = (EditText) findViewById(R.id.bind_company_bank_name);
        //固定电话(选填)
        mCompanyPhone = (EditText) findViewById(R.id.bind_company_phone);
        //姓名
        mCorporation = (EditText) findViewById(R.id.bind_company_corporation);
        //电话
        mCorporationPhoneNumber = (EditText) findViewById(R.id.bind_company_corporation_phone_number);
        //QQ
        mCorporationQQ = (EditText) findViewById(R.id.bind_company_corporation_qq);
        //邮箱
        mCorporationEmail = (EditText) findViewById(R.id.bind_company_corporation_email);
        //身份证号
        mCorporationIdNumber = (EditText) findViewById(R.id.bind_company_corporation_Id_number);
        //营业执照
        mBindCompanyTakePhoto = (ImageView) findViewById(R.id.bind_company_take_photo);
        mBindCompanyTakePhoto.setOnClickListener(this);
        //证件照1
        mBindCompanyCardPhoto1 = (ImageView) findViewById(R.id.bind_company_card_photo1);
        mBindCompanyCardPhoto1.setOnClickListener(this);
        //证件照2
        mBindCompanyCardPhoto2 = (ImageView) findViewById(R.id.bind_company_card_photo2);
        mBindCompanyCardPhoto2.setOnClickListener(this);
        //添加推广经历
        mAddExperience = (TextView) findViewById(R.id.bind_company_add);
        mAddExperience.setOnClickListener(this);

        //曾经所在公司
        mCompanyEver = (EditText) findViewById(R.id.bind_company_ever);
        //推广优势
        mPromotionAdvantage = (EditText) findViewById(R.id.bind_company_advantage);
        //提交
        mBindCompanySubmit = (Button) findViewById(R.id.bind_company_submit);
        mBindCompanySubmit.setOnClickListener(this);

        mBindCompanyTitle.setOnTitleBarClick(this);
    }


    private void initData() {
        OkHttpUtils.post().url(ULR_GET_CORPORATE_ACCOUNT).addHeader(ACCESS_TOKEN, mAccessToken)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.Companion.d("推广主体E：" + e.getLocalizedMessage());
                Util.showTimeOutNotice(currentContext);
                hideLoadingProgress();
            }

            @Override
            public void onResponse(String response, int id) {
                hideLoadingProgress();
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
            mCompanyName.setText(corporate.getAccountName());
            //公司账号
            mCompanyBankCard.setText(corporate.getCorporateAccount());
            //开户银行
            mCompanyBankName.setText(corporate.getBankName());
            //固定电话(选填)
            String corporatePhoneNumber = corporate.getCorporatePhoneNumber();
            if (!corporatePhoneNumber.isEmpty()) {
                mCompanyPhone.setText(corporatePhoneNumber);
            }
            //姓名
            mCorporation.setText(corporate.getCnName());
            //电话
            mCorporationPhoneNumber.setText(corporate.getPersonalPhoneNumber());
            //QQ
            mCorporationQQ.setText(corporate.getQq());
            //邮箱
            mCorporationEmail.setText(corporate.getEmail());
            //身份证号
            mCorporationIdNumber.setText(corporate.getIdNumber());
            //添加推广经历
            mList = corporate.getExperienceList();
            //曾经所在公司
            mCompanyEver.setText(corporate.getExperience());
            //推广优势
            mPromotionAdvantage.setText(corporate.getAdvantage());
            //三张图片url
            String bizUrl = corporate.getBizLicenceUrl();
            LogUtil.Companion.d("bizUrl is " + bizUrl);
            String personalUrl = corporate.getPersonalPhotoUrl();
            LogUtil.Companion.d("personalUrl is " + personalUrl);
            String idUrl = corporate.getIdPhotoUrl();
            LogUtil.Companion.d("idUrl is " + idUrl);

            Picasso picasso = Picasso.with(this);
            String bizLicencePath = (String) SharePreferenceUtil.get(this, "corporate" + bizLicence, "");
            if (!bizLicencePath.isEmpty()) {
                LogUtil.Companion.d("bizLicence path is " + bizLicencePath);
                picasso.load(new File(bizLicencePath))
                        .transform(BitmapUtil.getTransformation(mBindCompanyTakePhoto))
                        .placeholder(R.mipmap.ico_default_short_picture)
                        .error(R.mipmap.ico_binding_account_add_photos)
                        .into(mBindCompanyTakePhoto);
            } else {
                picasso.load(bizUrl)
                        .transform(BitmapUtil.getTransformation(mBindCompanyTakePhoto))
                        .error(R.mipmap.ico_binding_account_add_photos)
                        .placeholder(R.mipmap.ico_default_short_picture)
                        .into(mBindCompanyTakePhoto);
            }
            String personalPhotoPath = (String) SharePreferenceUtil.get(this, "corporate" + personalPhoto, "");
            if (!personalPhotoPath.isEmpty()) {
                LogUtil.Companion.d("personalPhotoPath is " + personalPhotoPath);
                picasso.load(new File(personalPhotoPath))
                        .transform(BitmapUtil.getTransformation(mBindCompanyCardPhoto1))
                        .placeholder(R.mipmap.ico_default_short_picture)
                        .error(R.mipmap.ico_binding_account_certificates)
                        .into(mBindCompanyCardPhoto1);
            } else {
                picasso.load(personalUrl)
                        .transform(BitmapUtil.getTransformation(mBindCompanyCardPhoto1))
                        .placeholder(R.mipmap.ico_default_short_picture)
                        .error(R.mipmap.ico_binding_account_certificates)
                        .into(mBindCompanyCardPhoto1);
            }
            String idPhotoPath = (String) SharePreferenceUtil.get(this, "corporate" + idPhoto, "");
            if (!idPhotoPath.isEmpty()) {
                LogUtil.Companion.d("idPhotoPath is " + idPhotoPath);
                picasso.load(new File(idPhotoPath))
                        .transform(BitmapUtil.getTransformation(mBindCompanyCardPhoto2))
                        .placeholder(R.mipmap.ico_default_short_picture)
                        .error(R.mipmap.ico_binding_account_certificates_two)
                        .into(mBindCompanyCardPhoto2);
            } else {
                picasso.load(idUrl)
                        .transform(BitmapUtil.getTransformation(mBindCompanyCardPhoto2))
                        .placeholder(R.mipmap.ico_default_short_picture)
                        .error(R.mipmap.ico_binding_account_certificates_two)
                        .into(mBindCompanyCardPhoto2);
            }

            //如果审核通过，则不再允许修任何改数据
            mAccountStatus = corporate.getAccountStatus();
            if (mAccountStatus != null) {
                switch (mAccountStatus) {
                    case "passed":
                        LogUtil.Companion.d("审核已通过，不可编辑");
                        ToastUtil.showShort(this, getString(R.string.account_passed_notice));
                        //禁止修改数据
                        forbidChange();
                        break;
                    case "auditing":
                        LogUtil.Companion.d("信息审核中，不可编辑");
                        ToastUtil.showShort(this, getString(R.string.account_auditing_notice));
                        //禁止修改数据
                        forbidChange();
                        break;
                }
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
        mCompanyName.setKeyListener(null);
        mCompanyBankCard.setKeyListener(null);
        mCompanyBankName.setKeyListener(null);
        mCompanyPhone.setKeyListener(null);
        mCorporation.setKeyListener(null);
        mCorporationPhoneNumber.setKeyListener(null);
        mCorporationQQ.setKeyListener(null);
        mCorporationEmail.setKeyListener(null);
        mCorporationIdNumber.setKeyListener(null);
        mCompanyEver.setKeyListener(null);
        mPromotionAdvantage.setKeyListener(null);
        mBindCompanyTakePhoto.setEnabled(false);
        mBindCompanyCardPhoto1.setEnabled(false);
        mBindCompanyCardPhoto2.setEnabled(false);
        mBindCompanySubmit.setEnabled(false);
        mBindCompanySubmit.setBackgroundResource(R.drawable.shape_button_forbid);
        mBindCompanySubmit.setTextColor(Color.GRAY);
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
                //进入经历列表
                Intent intent = new Intent(this, PromotionExperienceListActivity.class);
                intent.putParcelableArrayListExtra("experience", mList);
                intent.putExtra("type", CORPORATE);
                if (mAccountStatus != null) {
                    intent.putExtra("accountStatus", mAccountStatus);
                }
                startActivityForResult(intent, CORPORATE);
                break;
            case R.id.bind_company_submit:
                String companyName = mCompanyName.getText().toString();
                if (companyName.trim().isEmpty()) {
                    ToastUtil.showShort(this, "请填写开户名称");
                    return;
                }
                if (!companyName.matches(Regex.name)) {
                    ToastUtil.showShort(this, "开户名称只允许是中文、英文、数字和“_”");
                    return;
                }
                if (mCompanyBankCard.getText().toString().trim().isEmpty()) {
                    ToastUtil.showShort(this, "请填写公司账号");
                    return;
                }
                if (mCompanyBankName.getText().toString().trim().isEmpty()) {
                    ToastUtil.showShort(this, "请填写开户银行");
                    return;
                }

                // 提示营业执照
                if (isFirst && mBizLicence == null) {
                    ToastUtil.showShort(this, getString(R.string.account_biz_licence_notice));
                    return;
                }

                String corporation = mCorporation.getText().toString();
                if (corporation.trim().isEmpty()) {
                    ToastUtil.showShort(this, "请填写姓名");
                    return;
                }
                if (!corporation.matches(Regex.name)) {
                    ToastUtil.showShort(this, getString(R.string.regex_name));
                    return;
                }
                if (mCorporationIdNumber.getText().toString().trim().isEmpty()) {
                    ToastUtil.showShort(this, "请填写身份账号");
                    return;
                }
                if (!mCorporationIdNumber.getText().toString().matches(Regex.idCard)) {
                    ToastUtil.showShort(this, getString(R.string.regex_id_card));
                    return;
                }
                if (mCorporationPhoneNumber.getText().toString().trim().isEmpty()) {
                    ToastUtil.showShort(this, "请填写电话");
                    return;
                }
                if (mCorporationQQ.getText().toString().trim().isEmpty()) {
                    ToastUtil.showShort(this, "请填写QQ号码");
                    return;
                }
                if (mCorporationEmail.getText().toString().trim().isEmpty()) {
                    ToastUtil.showShort(this, "请填写邮箱");
                    return;
                }
                if (!mCorporationEmail.getText().toString().trim().matches(Regex.email)) {
                    ToastUtil.showShort(this, getString(R.string.regex_email));
                    return;
                }
                // 提示个人证件照
                if (isFirst && (mPersonalPhoto == null || mIdPhoto == null)) {
                    ToastUtil.showShort(this, getString(R.string.account_id_photo_notice));
                    return;
                }

                if (mList == null || mList.size() == 0) {
                    ToastUtil.showShort(this, getString(R.string.account_experience_notice));
                    return;
                }
                if (mCompanyEver.getText().toString().trim().isEmpty()) {
                    ToastUtil.showShort(this, getString(R.string.account_ever_notice));
                    return;
                }
                if (mPromotionAdvantage.getText().toString().trim().isEmpty()) {
                    ToastUtil.showShort(this, getString(R.string.account_advantage_notice));
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
                //压缩图片
                mZipName = "corporatePromotions" + TimeUtil.timeStamp2Date(System.currentTimeMillis()+"", "yyyyMMdd_HHssmm") + ".zip";
                // 压缩文件
                mZipFile = CompressUtil.zipANDSave(mFileMap, mZipName);
                if (mZipFile == null) {
                    // 提示错误并返回
                    ToastUtil.showShort(currentContext, getString(R.string.account_zip_error_notice));
                    return;
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
                        String bizLicenceName = bizLicence + "_" +
                                TimeUtil.timeStamp2Date(System.currentTimeMillis() + "", "yyyyMMddHH_mm_ss")+ ".jpg";
                        mBizLicence = new File(Environment.getExternalStorageDirectory(), bizLicenceName);
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
                        String personalPhotoName = personalPhoto + "_" +
                                TimeUtil.timeStamp2Date(System.currentTimeMillis() + "", "yyyyMMddHH_mm_ss")+ ".jpg";
                        mPersonalPhoto = new File(Environment.getExternalStorageDirectory(),
                                personalPhotoName);
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
                        String idPhotoName = idPhoto + "_" +
                                TimeUtil.timeStamp2Date(System.currentTimeMillis() + "", "yyyyMMddHH_mm_ss")+ ".jpg";
                        mIdPhoto = new File(Environment.getExternalStorageDirectory(), idPhotoName);
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
        mAccountName = mCompanyName.getText().toString().trim();
        //公司账号
        mCorporateAccount = mCompanyBankCard.getText().toString().trim();
        //开户银行
        mBankName = mCompanyBankName.getText().toString().trim();
        //公司电话号码
        mCorporatePhoneNumber = mCompanyPhone.getText().toString().trim();
        //个人姓名
        mCnName = mCorporation.getText().toString().trim();
        //个人电话号码
        mPersonalPhoneNumber = mCorporationPhoneNumber.getText().toString().trim();
        //QQ
        mQQ = mCorporationQQ.getText().toString().trim();
        //邮箱
        mEmail = mCorporationEmail.getText().toString().trim();
        //身份证号
        mIdNumber = mCorporationIdNumber.getText().toString().trim();
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
        mProgressDialog.setTitle(getString(R.string.upload_progress_dialog_title));
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
                        updateLocalData();
                        // 更新本地记录文件
                        updatePhotoData();
                        ToastUtil.showShort(currentContext, getString(R.string.account_upload_success_notice));
                        finish();
                        break;
                    case "failure":
                        Util.showError(currentContext, errorBean.getReason());
                        break;
                }
            }
        });
    }

    private void updatePhotoData() {
        if (mFileMap != null) {
            for (Map.Entry<String, String> entry : mFileMap.entrySet()) {
                String key = entry.getKey().substring(0, entry.getKey().lastIndexOf("."));
                LogUtil.Companion.d("key is " + key);
                String value = entry.getValue();
                LogUtil.Companion.d("val is " + value);
                SharePreferenceUtil.put(currentContext, "corporate" + key, value);
            }
        }
    }

    private void updateLocalData() {
        //账户存在
        SharePreferenceUtil.put(currentContext, Constant.CORPORATE_EXIT, true);
        //账户名称
        SharePreferenceUtil.put(currentContext, Constant.CORPORATE_ACCOUNT_NAME, mAccountName);
        //公司账号
        SharePreferenceUtil.put(currentContext, Constant.CORPORATE_ACCOUNT_NUMBER, mCorporateAccount);
        //开户银行
        SharePreferenceUtil.put(currentContext, Constant.CORPORATE_BANK_NAME, mBankName);
        //公司电话号码
        SharePreferenceUtil.put(currentContext, Constant.CORPORATE_PHONE_NUMBER, mCorporatePhoneNumber);
        //个人姓名
        SharePreferenceUtil.put(currentContext, Constant.CORPORATE_CN_NAME, mCnName);
        //个人电话号码
        SharePreferenceUtil.put(currentContext, Constant.CORPORATION_PERSONAL_PHONE_NUMBER, mPersonalPhoneNumber);
        //邮箱
        SharePreferenceUtil.put(currentContext, Constant.CORPORATE_EMAIL, mEmail);
        //身份证号
        SharePreferenceUtil.put(currentContext, Constant.CORPORATE_ID_NUMBER, mIdNumber);
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
                    handlePhoto(bizLicence, mBizLicence, mBindCompanyTakePhoto);
                }
                break;
            case PERSONAL_FROM_CAMERA:
                if (resultCode == RESULT_OK) {
                    handlePhoto(personalPhoto, mPersonalPhoto, mBindCompanyCardPhoto1);
                }
                break;
            case ID_FROM_CAMERA:
                if (resultCode == RESULT_OK) {
                    handlePhoto(idPhoto, mIdPhoto, mBindCompanyCardPhoto2);
                }
                break;
            case BIZ_FROM_PHOTO:
                if (data != null && resultCode == RESULT_OK) {
                    mBizLicence = new File(BitmapUtil.getRealPathFromURI(this, data.getData()));
                    handlePhoto(bizLicence, mBizLicence, mBindCompanyTakePhoto);
                }
                break;
            case PERSONAL_FROM_PHOTO:
                if (data != null && resultCode == RESULT_OK) {
                    mPersonalPhoto = new File((BitmapUtil.getRealPathFromURI(this, data.getData())));
                    handlePhoto(personalPhoto, mPersonalPhoto, mBindCompanyCardPhoto1);
                }
                break;
            case ID_FROM_PHOTO:
                if (data != null && resultCode == RESULT_OK) {
                    mIdPhoto = new File((BitmapUtil.getRealPathFromURI(this, data.getData())));
                    handlePhoto(idPhoto, mIdPhoto, mBindCompanyCardPhoto2);
                }
                break;
            case CORPORATE:
                if (data != null && resultCode == CORPORATE) {
                    mList = data.getParcelableArrayListExtra("experience");
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
