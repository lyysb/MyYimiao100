package com.yimiao100.sale.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.meiqia.core.MQManager;
import com.meiqia.core.bean.MQMessage;
import com.meiqia.core.callback.OnGetMessageListCallback;
import com.squareup.picasso.Picasso;
import com.yimiao100.sale.R;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.bean.ErrorBean;
import com.yimiao100.sale.bean.Event;
import com.yimiao100.sale.bean.Experience;
import com.yimiao100.sale.bean.PersonalBean;
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

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Request;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

/**
 * 推广主体-绑定个人主体
 */
public class BindPersonalActivity extends BaseActivity implements TitleView
        .TitleBarOnClickListener, TextWatcher, EasyPermissions.PermissionCallbacks {


    private static final int ID_CARD1_FROM_CAMERA = 101;
    private static final int ID_CARD1_FROM_PHOTO = 102;
    private static final int ID_CARD2_FROM_CAMERA = 201;
    private static final int ID_CARD2_FROM_PHOTO = 202;
    private static final int PERSONAL = 2;
    private static final String CN_NAME = "cnName";
    private static final String ID_NUMBER = "idNumber";
    private static final String PERSONAL_PHONE_NUMBER = "personalPhoneNumber";
    private static final String QQ = "qq";
    private static final String EMAIL = "email";
    private static final String BANK_NAME = "bankName";
    private static final String OPENING_BANK = "openingBank";
    private static final String BANK_CARD_NUMBER = "bankCardNumber";
    private static final String EXPERIENCE_LIST = "experienceList";
    private static final String EXPERIENCE = "experience";
    private static final String ADVANTAGE = "advantage";
    private static final String ZIP_FILE = "zipFile";
    private final int WIDTH = 300;
    private final int HEIGHT = 80;
    @BindView(R.id.bind_personal_title)
    TitleView mTitle;
    @BindView(R.id.bind_personal_service)
    ImageView mPersonalService;
    @BindView(R.id.bind_personal_name)
    EditText mPersonalName;
    @BindView(R.id.bind_personal_id_card)
    EditText mPersonalIdCard;
    @BindView(R.id.bind_personal_phone)
    EditText mPersonalPhone;
    @BindView(R.id.bind_personal_qq)
    EditText mPersonalQq;
    @BindView(R.id.bind_personal_email)
    EditText mPersonalEmail;
    @BindView(R.id.bind_personal_card_photo1)
    ImageView mPersonalCardPhoto1;
    @BindView(R.id.bind_personal_card_photo2)
    ImageView mPersonalCardPhoto2;
    @BindView(R.id.bind_personal_owner)
    TextView mPersonalOwner;
    @BindView(R.id.bind_personal_bank)
    EditText mPersonalBank;
    @BindView(R.id.bind_personal_bank_name)
    EditText mPersonalBankName;
    @BindView(R.id.bind_personal_bank_card)
    EditText mPersonalBankCard;
    @BindView(R.id.bind_personal_ever)
    EditText mPersonalEver;
    @BindView(R.id.bind_personal_advantage)
    EditText mPersonalAdvantage;
    @BindView(R.id.bind_personal_experience)
    TextView mPersonalExperience;
    @BindView(R.id.bind_personal_submit)
    Button mPersonalSubmit;

    private final String URL_GET_PERSONAL = Constant.BASE_URL + "/api/user/get_user_account";
    private final String URL_SUBMIT = Constant.BASE_URL + "/api/user/post_personal_user_account";
    private File mIdPhoto1;
    private File mIdPhoto2;
    private String personalPhoto = "personalPhoto";
    private String idPhoto = "idPhoto";

    private HashMap<String, String> mFileMap = new HashMap<>();
    private boolean isFirst = true;
    private ArrayList<Experience> mList;
    private AlertDialog mDialog;
    private String mPersonalName1;
    private String mPersonalIdCard1;
    private String mPersonalPhone1;
    private String mPersonalQq1;
    private String mPersonalEmail1;
    private String mPersonalBank1;
    private String mPersonalBankName1;
    private String mPersonalBankCard1;
    private String mPersonalEver1;
    private String mPersonalAdvantage1;
    private String mZipName;
    private File mZipFile;
    private String mExperienceList1;
    private ProgressDialog mProgressDialog;
    private String mAccountStatus;
    private Badge mBadge;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_personal);
        ButterKnife.bind(this);

        initView();

        initData();
    }

    private void initView() {
        mTitle.setOnTitleBarClick(this);
        mBadge = new QBadgeView(this).bindTarget(mPersonalService)
                .setBadgePadding(4, true)
                .setGravityOffset(9, true)
                .setShowShadow(false);
        mPersonalName.addTextChangedListener(this);
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
        showLoadingProgress();
        OkHttpUtils.post().url(URL_GET_PERSONAL).addHeader(ACCESS_TOKEN, accessToken)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.d("推广主体E：" + e.getLocalizedMessage());
                Util.showTimeOutNotice(currentContext);
                hideLoadingProgress();
            }

            @Override
            public void onResponse(String response, int id) {
                hideLoadingProgress();
                LogUtil.d("推广主体：" + response);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        //回显网络数据
                        echoData(JSON.parseObject(response, UserBean.class).getUserAccount()
                                .getPersonal());
                        break;
                    case "failure":
                        Util.showError(currentContext, errorBean.getReason());
                        break;
                }
            }
        });
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

    private void echoData(PersonalBean personal) {
        if (personal != null) {
            // 不是新建账户
            isFirst = false;
            LogUtil.d("修改个人账户");
            // 姓名
            mPersonalName.setText(personal.getCnName());
            // 身份证号
            mPersonalIdCard.setText(personal.getIdNumber());
            // 联系电话
            mPersonalPhone.setText(personal.getPersonalPhoneNumber());
            // QQ号码
            mPersonalQq.setText(personal.getQq());
            // 邮箱
            mPersonalEmail.setText(personal.getEmail());
            String personalUrl = personal.getPersonalPhotoUrl();
            LogUtil.d("personalUrl is" + personalUrl);
            String idUrl = personal.getIdPhotoUrl();
            LogUtil.d("idUrl is" + idUrl);
            Picasso picasso = Picasso.with(this);
            // 证件照1
            String personalPhotoPath = (String) SharePreferenceUtil.get(this, "personal" + personalPhoto, "");
            if (!personalPhotoPath.isEmpty()) {
                LogUtil.Companion.d("personalPhotoPath is " + personalPhotoPath);
                picasso.load(new File(personalPhotoPath))
                        .transform(BitmapUtil.getTransformation(mPersonalCardPhoto1))
                        .placeholder(R.mipmap.ico_default_short_picture)
                        .error(R.mipmap.ico_binding_account_certificates)
                        .into(mPersonalCardPhoto1);
            } else {
                picasso.load(personalUrl)
                        .transform(BitmapUtil.getTransformation(mPersonalCardPhoto1))
                        .placeholder(R.mipmap.ico_default_short_picture)
                        .error(R.mipmap.ico_binding_account_certificates)
                        .into(mPersonalCardPhoto1);
            }
            // 证件照2
            String idPhotoPath = (String) SharePreferenceUtil.get(this, "personal" + idPhoto, "");
            if (!idPhotoPath.isEmpty()) {
                LogUtil.d("idPhotoPath is " + idPhotoPath);
                picasso.load(new File(idPhotoPath))
                        .transform(BitmapUtil.getTransformation(mPersonalCardPhoto2))
                        .placeholder(R.mipmap.ico_default_short_picture)
                        .error(R.mipmap.ico_binding_account_certificates_two)
                        .into(mPersonalCardPhoto2);
            } else {
                picasso.load(idUrl)
                        .transform(BitmapUtil.getTransformation(mPersonalCardPhoto2))
                        .placeholder(R.mipmap.ico_default_short_picture)
                        .error(R.mipmap.ico_binding_account_certificates_two)
                        .into(mPersonalCardPhoto2);
            }
            // 持卡人
            mPersonalOwner.setText(personal.getCnName());
            // 银行名称
            mPersonalBank.setText(personal.getBankName());
            // 开户行
            mPersonalBankName.setText(personal.getOpeningBank());
            // 银行卡号
            mPersonalBankCard.setText(personal.getBankCardNumber());
            // 推广经历
            mList = personal.getExperienceList();
            // 曾经所在公司及职位
            mPersonalEver.setText(personal.getExperience());
            // 推广优势
            mPersonalAdvantage.setText(personal.getAdvantage());
            // 如果审核通过，则不再允许修任何改数据
            mAccountStatus = personal.getAccountStatus();
            if (mAccountStatus != null) {
                switch (mAccountStatus) {
                    case "passed":
                        LogUtil.d("审核已通过，不可编辑");
                        ToastUtil.showShort(this, getString(R.string.account_passed_notice));
                        //禁止修改数据
                        forbidChange();
                        break;
                    case "auditing":
                        LogUtil.d("信息审核中，不可编辑");
                        ToastUtil.showShort(this, getString(R.string.account_auditing_notice));
                        //禁止修改数据
                        forbidChange();
                        break;
                }
            }
        } else {
            isFirst = true;
            LogUtil.d("新建个人账户");
        }
    }

    /**
     * 禁止所有点击事件
     */
    private void forbidChange() {
        mPersonalName.setKeyListener(null);
        mPersonalIdCard.setKeyListener(null);
        mPersonalPhone.setKeyListener(null);
        mPersonalQq.setKeyListener(null);
        mPersonalEmail.setKeyListener(null);
        mPersonalCardPhoto1.setEnabled(false);
        mPersonalCardPhoto2.setEnabled(false);
        mPersonalBank.setKeyListener(null);
        mPersonalBankName.setKeyListener(null);
        mPersonalBankCard.setKeyListener(null);
        mPersonalEver.setKeyListener(null);
        mPersonalAdvantage.setKeyListener(null);
        mPersonalSubmit.setEnabled(false);
        mPersonalSubmit.setBackgroundResource(R.drawable.shape_button_forbid);
        mPersonalSubmit.setTextColor(Color.GRAY);
    }


    @OnClick({R.id.bind_personal_service, R.id.bind_personal_card_photo1, R.id.bind_personal_card_photo2,
            R.id.bind_personal_experience, R.id.bind_personal_submit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bind_personal_service:
                //联系客服
                Util.enterCustomerService(this);
                break;
            case R.id.bind_personal_card_photo1:
                // 选择照片1
                getPhoto1();
                break;
            case R.id.bind_personal_card_photo2:
                // 选择照片2
                getPhoto2();
                break;
            case R.id.bind_personal_experience:
                // 进入推广经历列表
                Intent intent = new Intent(this, PromotionExperienceListActivity.class);
                intent.putParcelableArrayListExtra("experience", mList);
                intent.putExtra("type", PERSONAL);
                if (mAccountStatus != null) {
                    intent.putExtra("accountStatus", mAccountStatus);
                }
                startActivityForResult(intent, PERSONAL);
                break;
            case R.id.bind_personal_submit:
                // 校验数据
                verifyData();
                break;
        }
    }

    private void getPhoto1() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        CharSequence[] items = {getString(R.string.open_camera), getString(R.string.open_DCIM)};
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        //打开相机
                        openCamera1();
                        break;
                    case 1:
                        //打开相册
                        openDCIM(ID_CARD1_FROM_PHOTO);
                        break;
                }
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @AfterPermissionGranted(1)
    public void openCamera1() {
        String personalPhotoName = personalPhoto + "_" +
                TimeUtil.timeStamp2Date(System.currentTimeMillis() + "", "yyyyMMddHH_mm_ss") + ".jpg";
        if (!EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA)) {
            // 如果没有权限则申请权限
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_camera), 1, Manifest.permission.CAMERA);
            return;
        }
        Intent intentCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mIdPhoto1 = Util.createFile(personalPhotoName);
        // 从文件中创建uri
        Uri uri;
        if (Build.VERSION.SDK_INT >= 24) {
            ContentValues contentValues = new ContentValues(1);
            contentValues.put(MediaStore.Images.Media.DATA, mIdPhoto1.getAbsolutePath());
            uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        } else {
            uri = Uri.fromFile(mIdPhoto1);
        }
        intentCapture.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intentCapture, ID_CARD1_FROM_CAMERA);
    }

    /**
     * 打开相册
     * @param requestCode
     */
    public void openDCIM(int requestCode) {
        Intent intentPick = new Intent(Intent.ACTION_PICK);
        intentPick.setType("image/*");
        startActivityForResult(intentPick, requestCode);
    }

    /**
     * 申请成功
     * @param requestCode
     * @param perms
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        LogUtil.d("申请成功：" + perms);
    }

    /**
     * 失败
     * @param requestCode
     * @param perms
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        LogUtil.d("申请失败：" + perms);
        if (EasyPermissions.somePermissionDenied(this, Manifest.permission.CAMERA)) {
            // 如果用户拒绝了相机权限
            LogUtil.d("用户拒绝相机权限");
        }
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            // 如果用户选择了不再询问，则要去设置界面打开权限
            LogUtil.d("用户选择了不再询问");
            showSettingDialog();
        }
    }


    private void getPhoto2() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        CharSequence[] items = {getString(R.string.open_camera), getString(R.string.open_DCIM)};
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        //打开相机
                        openCamera2();
                        break;
                    case 1:
                        //打开相册
                        openDCIM(ID_CARD2_FROM_PHOTO);
                        break;
                }
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @AfterPermissionGranted(2)
    public void openCamera2() {
        Intent intentCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String idPhotoName = idPhoto + "_" +
                TimeUtil.timeStamp2Date(System.currentTimeMillis() + "", "yyyyMMddHH_mm_ss") + ".jpg";
        startActivityForResult(intentCapture, ID_CARD2_FROM_CAMERA);
        if (!EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA)) {
            // 如果没有权限则申请权限
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_camera), 2, Manifest.permission.CAMERA);
            return;
        }
        mIdPhoto2 = Util.createFile(idPhotoName);
        // 从文件中创建uri
        Uri uri;
        if (Build.VERSION.SDK_INT >= 24) {
            ContentValues contentValues = new ContentValues(1);
            contentValues.put(MediaStore.Images.Media.DATA, mIdPhoto2.getAbsolutePath());
            uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        } else {
            uri = Uri.fromFile(mIdPhoto2);
        }
        intentCapture.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intentCapture, ID_CARD2_FROM_CAMERA);
    }

    /**
     * 校验数据
     */
    private void verifyData() {
        // 推广人姓名
        mPersonalName1 = mPersonalName.getText().toString().trim();
        if (mPersonalName1.isEmpty()) {
            ToastUtil.showShort(this, "请填写推广人姓名");
            return;
        }
        if (!mPersonalName1.matches(Regex.name)) {
            ToastUtil.showShort(this, getString(R.string.regex_name));
            return;
        }
        // 推广人身份证号
        mPersonalIdCard1 = mPersonalIdCard.getText().toString().trim();
        if (mPersonalIdCard1.isEmpty()) {
            ToastUtil.showShort(this, "请填写推广人身份证号");
            return;
        }
        if (!mPersonalIdCard1.matches(Regex.idCard)) {
            ToastUtil.showShort(this, getString(R.string.regex_id_card));
            return;
        }
        // 推广人联系电话
        mPersonalPhone1 = mPersonalPhone.getText().toString().trim();
        if (mPersonalPhone1.isEmpty()) {
            ToastUtil.showShort(this, "请填写推广人联系电话");
            return;
        }
        // 推广人QQ号码
        mPersonalQq1 = mPersonalQq.getText().toString().trim();
        if (mPersonalQq1.isEmpty()) {
            ToastUtil.showShort(this, "请填写推广人QQ号码");
            return;
        }
        // 推广人邮箱
        mPersonalEmail1 = mPersonalEmail.getText().toString().trim();
        if (mPersonalEmail1.isEmpty()) {
            ToastUtil.showShort(this, "请填写推广人邮箱");
            return;
        }
        if (!mPersonalEmail1.matches(Regex.email)) {
            ToastUtil.showShort(this, getString(R.string.regex_email));
            return;
        }
        // 证件照
        if (isFirst && (mIdPhoto1 == null || mIdPhoto2 == null)) {
            ToastUtil.showShort(this, getString(R.string.account_id_photo_notice));
            return;
        }
        // 银行名称
        mPersonalBank1 = mPersonalBank.getText().toString().trim();
        if (mPersonalBank1.isEmpty()) {
            ToastUtil.showShort(this, "请填写推广人银行名称");
            return;
        }
        // 开户行
        mPersonalBankName1 = mPersonalBankName.getText().toString().trim();
        if (mPersonalBankName1.isEmpty()) {
            ToastUtil.showShort(this, "请填写推广人开户行");
            return;
        }
        // 银行卡号
        mPersonalBankCard1 = mPersonalBankCard.getText().toString().trim();
        if (mPersonalBankCard1.isEmpty()) {
            ToastUtil.showShort(this, "请填写推广人银行卡号");
            return;
        }
        // 推广经历
        if (mList == null || mList.isEmpty()) {
            ToastUtil.showShort(this, getString(R.string.account_experience_notice));
            return;
        } else {
            mExperienceList1 = JSON.toJSONString(mList);
            LogUtil.d("推广经历：" + mExperienceList1);
        }
        // 曾经所在公司及职位
        mPersonalEver1 = mPersonalEver.getText().toString().trim();
        if (mPersonalEver1.isEmpty()) {
            ToastUtil.showShort(this, getString(R.string.account_ever_notice));
            return;
        }
        // 推广优势
        mPersonalAdvantage1 = mPersonalAdvantage.getText().toString().trim();
        if (mPersonalAdvantage1.isEmpty()) {
            ToastUtil.showShort(this, getString(R.string.account_advantage_notice));
            return;
        }
        // 显示是否确定提交
        showConfirmDialog();
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
        // 再确认一次
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        Button submit = (Button) view.findViewById(R.id.dialog_confirm);
        // 提交数据
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();//压缩图片
                mZipName = "personalPromotions" + TimeUtil.timeStamp2Date(System.currentTimeMillis()+"", "yyyyMMdd_HHssmm") + ".zip";
                // 压缩文件
                mZipFile = CompressUtil.zipANDSave(mFileMap, mZipName);
                if (mZipFile == null) {
                    // 提示错误并返回
                    ToastUtil.showShort(currentContext, getString(R.string.account_zip_error_notice));
                    return;
                }
                submitData();
            }
        });
    }

    /**
     * 最终提交数据
     */
    private void submitData() {
        // 禁止再次点击上传
        mPersonalSubmit.setEnabled(false);
        //上传进度条显示
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setTitle(getString(R.string.upload_progress_dialog_title));

        final ProgressDialog responseDialog = new ProgressDialog(this);
        responseDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        responseDialog.setCancelable(false);
        responseDialog.setMessage(getString(R.string.response_progress_dialog_title));
        OkHttpUtils.post().url(URL_SUBMIT).addHeader(ACCESS_TOKEN, accessToken)
                .addParams(CN_NAME, mPersonalName1)
                .addParams(ID_NUMBER, mPersonalIdCard1)
                .addParams(PERSONAL_PHONE_NUMBER, mPersonalPhone1)
                .addParams(QQ, mPersonalQq1)
                .addParams(EMAIL, mPersonalEmail1)
                .addParams(BANK_NAME, mPersonalBank1)
                .addParams(OPENING_BANK, mPersonalBankName1)
                .addParams(BANK_CARD_NUMBER, mPersonalBankCard1)
                .addParams(EXPERIENCE_LIST, mExperienceList1)
                .addParams(EXPERIENCE, mPersonalEver1)
                .addParams(ADVANTAGE, mPersonalAdvantage1)
                .addFile(ZIP_FILE, mZipName, mZipFile)
                .build().connTimeOut(10 * 60 * 1000)
                .readTimeOut(10 * 60 * 1000)
                .writeTimeOut(10 * 60 * 1000).execute(new StringCallback() {
            @Override
            public void onBefore(Request request, int id) {
                super.onBefore(request, id);
                mProgressDialog.show();
            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
                if (responseDialog.isShowing()) {
                    responseDialog.dismiss();
                }
            }

            @Override
            public void inProgress(float progress, long total, int id) {
                super.inProgress(progress, total, id);
                mProgressDialog.setProgress((int) (100 * progress + 0.5));
                if (progress == 1f) {
                    // 关闭进度条弹窗显示
                    mProgressDialog.dismiss();
                    // 显示处理弹窗
                    responseDialog.show();
                }


            }

            @Override
            public void onError(Call call, Exception e, int id) {
                e.printStackTrace();
                Util.showTimeOutNotice(currentContext);
                mPersonalSubmit.setEnabled(true);
            }

            @Override
            public void onResponse(String response, int id) {
                mPersonalSubmit.setEnabled(true);
                LogUtil.Companion.d("个人主体数据：" + response);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        // 更新本地数据
                        updateLocalData();
                        // 更新本地存储
                        updatePhotoData();
                        ToastUtil.showShort(currentContext, getString(R.string.account_upload_success_notice));
                        finish();
                        break;
                    case "failure":
                        Util.showError(currentContext, errorBean.getReason());
                        break;
                }
                try {
                    // 删除压缩包
//                    mZipFile.delete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void updatePhotoData() {
        if (mFileMap != null) {
            for (Map.Entry<String, String> entry : mFileMap.entrySet()) {
                String key = entry.getKey().substring(0, entry.getKey().lastIndexOf("."));
                LogUtil.d("key is " + key);
                String value = entry.getValue();
                LogUtil.d("val is " + value);
                SharePreferenceUtil.put(currentContext, "personal" + key, value);
            }
        }
    }

    private void updateLocalData() {
        // 记录个人主体存在
        SharePreferenceUtil.put(currentContext, Constant.PERSONAL_EXIT, true);
        // 姓名
        SharePreferenceUtil.put(currentContext, Constant.PERSONAL_CN_NAME, mPersonalName1);
        // 银行卡号
        SharePreferenceUtil.put(currentContext, Constant.PERSONAL_BANK_CARD_NUMBER, mPersonalBankCard1);
        // 身份证号
        SharePreferenceUtil.put(currentContext, Constant.PERSONAL_ID_CARD, mPersonalIdCard1);
        // 联系电话
        SharePreferenceUtil.put(currentContext, Constant.PERSONAL_PHONE_NUMBER, mPersonalPhone1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ID_CARD1_FROM_CAMERA:
                if (resultCode == RESULT_OK) {
                    handlePhoto(personalPhoto, mIdPhoto1, mPersonalCardPhoto1);
                }
                break;
            case ID_CARD2_FROM_CAMERA:
                if (resultCode == RESULT_OK) {
                    handlePhoto(idPhoto, mIdPhoto2, mPersonalCardPhoto2);
                }
                break;
            case ID_CARD1_FROM_PHOTO:
                if (data != null && resultCode == RESULT_OK) {
                    mIdPhoto1 = new File((BitmapUtil.getRealPathFromURI(this, data.getData())));
                    handlePhoto(personalPhoto, mIdPhoto1, mPersonalCardPhoto1);
                }
                break;
            case ID_CARD2_FROM_PHOTO:
                if (data != null && resultCode == RESULT_OK) {
                    mIdPhoto2 = new File((BitmapUtil.getRealPathFromURI(this, data.getData())));
                    handlePhoto(idPhoto, mIdPhoto2, mPersonalCardPhoto2);
                }
                break;
            case PERSONAL:
                if (data != null && resultCode == PERSONAL) {
                    mList = data.getParcelableArrayListExtra("experience");
                }
                break;
        }
    }

    /**
     * 添加到压缩文件集合中
     *
     * @param childFileName 压缩文件中单个文件的名字
     * @param file          压缩文件中的单个文件
     * @param imageView     前台回显数据
     */
    private void handlePhoto(String childFileName, File file, ImageView imageView) {
        // 压缩文件内单个文件的名字
        String key = childFileName + file.getAbsolutePath().substring(file.getAbsolutePath()
                .lastIndexOf("."));
        // 压缩文件内单个文件的真实路径
        String val = file.getAbsolutePath();
        mFileMap.put(key, val);
        LogUtil.d("key：" + key);
        LogUtil.d("val：" + val);
        //压缩回显
        Bitmap bitmap = BitmapUtil.decodeSampledBitmapFromFile(file, DensityUtil.dp2px(this,
                WIDTH), DensityUtil.dp2px(this, HEIGHT));
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        mPersonalOwner.setText(s.length() != 0 ? s.toString() : "");
    }

    @Override
    public void leftOnClick() {
        finish();
    }

    @Override
    public void rightOnClick() {

    }
}
