package com.yimiao100.sale.activity;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yimiao100.sale.R;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.bean.Area;
import com.yimiao100.sale.bean.City;
import com.yimiao100.sale.bean.ErrorBean;
import com.yimiao100.sale.bean.ImageBean;
import com.yimiao100.sale.bean.Province;
import com.yimiao100.sale.bean.RegionListBean;
import com.yimiao100.sale.utils.BitmapUtil;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.SharePreferenceUtil;
import com.yimiao100.sale.utils.Util;
import com.yimiao100.sale.view.TitleView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jeesoft.widget.pickerview.CharacterPickerWindow;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;

import static com.yimiao100.sale.ext.JSON.parseObject;


/**
 * 个人设置界面
 */
public class PersonalSettingActivity extends BaseActivity implements TitleView
        .TitleBarOnClickListener {

    @BindView(R.id.personal_title)
    TitleView mPersonalTitle;
    @BindView(R.id.personal_setImage)
    CircleImageView mPersonalSetImage;
    @BindView(R.id.personal_name)
    TextView mPersonalName;
    @BindView(R.id.ll_personal_setName)
    LinearLayout mLlPersonalSetName;
    @BindView(R.id.personal_phone)
    TextView mPersonalPhone;
    @BindView(R.id.ll_personal_setPhone)
    LinearLayout mLlPersonalSetPhone;
    @BindView(R.id.personal_email)
    TextView mPersonalEmail;
    @BindView(R.id.ll_personal_setEmail)
    LinearLayout mLlPersonalSetEmail;
    @BindView(R.id.personal_region)
    TextView mPersonalRegion;
    @BindView(R.id.ll_personal_setRegion)
    LinearLayout mLlPersonalSetRegion;
    @BindView(R.id.personal_idCard)
    TextView mPersonalIdCard;
    @BindView(R.id.ll_personal_setIdCard)
    LinearLayout mLlPersonalSetIdCard;
    @BindView(R.id.ll_personal_changePwd)
    LinearLayout mLlPersonalChangePwd;

    private final int REQUEST_SETTING = 101;
    @BindView(R.id.ll_personal_setAddress)
    LinearLayout mLlPersonalSetAddress;

    //省市县三级联动选择器
    private CharacterPickerWindow mOptions;
    private List<String> mOptions1Items;
    private List<List<String>> mOptions2Items;
    private List<List<List<String>>> mOptions3Items;

    private static final int PHOTO_REQUEST_CAMERA = 1;          // 拍照
    private static final int PHOTO_REQUEST_GALLERY = 2;         // 从相册中选择
    private static final int PHOTO_REQUEST_CUT = 3;             // 结果

    /* 头像名称 */
    private static final String PHOTO_FILE_NAME = "temp_photo.jpg";


    private final String URL_REGION_LIST = Constant.BASE_URL + "/api/region/all";
    private final String URL_UPDATE_REGION = Constant.BASE_URL + "/api/user/update_region";

    private File tempFile;
    private AlertDialog mDialog;

    private List<Province> mProvinceList;
    private Uri mUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_setting);
        ButterKnife.bind(this);


        initData();

        mPersonalTitle.setOnTitleBarClick(this);
        //初始化滚轮联动器
        mOptions = new CharacterPickerWindow(this);

    }

    /**
     * 获取用户数据
     */
    private void initData() {
        //从本地获取数据
        //获取用户姓名
        String user_name = (String) SharePreferenceUtil.get(this, Constant.CNNAME, "");
        //获取用户注册账号
        String accountNumber = (String) SharePreferenceUtil.get(this, Constant.ACCOUNT_NUMBER,
                "未知");
        //获取用户电话
        String user_phone_number = (String) SharePreferenceUtil.get(this, Constant.PHONENUMBER,
                accountNumber);
        //获取用户邮箱
        String user_email = (String) SharePreferenceUtil.get(this, Constant.EMAIL, "");
        //获取用户推广地域并设置
        String region = (String) SharePreferenceUtil.get(this, Constant.REGION, "");
        //获取用户身份证号
        String user_id_number = (String) SharePreferenceUtil.get(this, Constant.IDNUMBER, "");
        //获取用户头像URL
        String user_icon_url = (String) SharePreferenceUtil.get(this, Constant.PROFILEIMAGEURL, "");

        //本地显示数据
        mPersonalName.setText(user_name);
        mPersonalPhone.setText(user_phone_number);
        mPersonalEmail.setText(user_email);
        mPersonalIdCard.setText(user_id_number);
        mPersonalRegion.setText(region);
        //如果头像地址非空，则加载头像
        if (!user_icon_url.isEmpty()) {
            Picasso.with(this).load(user_icon_url).placeholder(R.mipmap.ico_my_default_avatar).into
                    (mPersonalSetImage);
        }
        //初始化region数据
        initRegion();
    }

    /**
     * 初始化地域数据
     */
    private void initRegion() {
        //解析Json
        String region_list = (String) SharePreferenceUtil.get(getApplicationContext(), Constant
                .REGION_LIST, "");
        if (TextUtils.isEmpty(region_list)) {
            //重新联网获取region数据
            refreshRegion();
        } else {
            //解析本地读取数据
            RegionListBean regionListBean = parseObject(region_list, RegionListBean
                    .class);
            mProvinceList = regionListBean.getProvinceList();
            //处理数据
            handleRegionData();
        }

    }

    /**
     * 联网刷新region信息
     */
    private void refreshRegion() {
        OkHttpUtils.get().url(URL_REGION_LIST).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.Companion.d("获取地域信息E：" + e.getLocalizedMessage());
                e.printStackTrace();
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.Companion.d("获取地域信息：" + response);
                ErrorBean errorBean = parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        //保存地域信息列表
                        SharePreferenceUtil.put(getApplicationContext(), Constant.REGION_LIST,
                                response);
                        mProvinceList = parseObject(response, RegionListBean.class)
                                .getProvinceList();
                        handleRegionData();
                        break;
                    case "failure":
                        Util.showError(currentContext, errorBean.getReason());
                        break;
                }
            }
        });
    }

    /**
     * 处理region数据
     */
    private void handleRegionData() {
        //省列表集合
        mOptions1Items = new ArrayList<>();
        //省<市>列表集合
        mOptions2Items = new ArrayList<>();
        //省<市<县>>列表集合
        mOptions3Items = new ArrayList<>();

        for (Province province : mProvinceList) {
            String ProvinceName = province.getName();
            List<City> cityList = province.getCityList();
            if (cityList.size() == 0) {
                City city_temp = new City();
                city_temp.setAreaList(new ArrayList<Area>());
                cityList.add(city_temp);
            }
            List<String> mOptions2Items_temp = new ArrayList<>();
            List<List<String>> mOptions3Items_temp = new ArrayList<>();
            for (City city : cityList) {
                String CityName = city.getName();
                mOptions2Items_temp.add(CityName == null ? "" : CityName);
                List<Area> areaList = city.getAreaList();
                if (areaList.size() == 0) {
                    areaList.add(new Area());
                }
                List<String> mOptions3Items_temp_temp = new ArrayList<>();
                for (Area area : areaList) {
                    String AreaName = area.getName();
                    mOptions3Items_temp_temp.add(AreaName == null ? "" : AreaName);
                }
                mOptions3Items_temp.add(mOptions3Items_temp_temp);
            }
            mOptions1Items.add(ProvinceName);
            mOptions2Items.add(mOptions2Items_temp);
            mOptions3Items.add(mOptions3Items_temp);
        }
    }

    @OnClick({R.id.personal_setImage, R.id.ll_personal_setName, R.id.ll_personal_setPhone, R.id
            .ll_personal_setEmail, R.id.ll_personal_setRegion, R.id.ll_personal_setIdCard, R.id
            .ll_personal_changePwd, R.id.ll_personal_setAddress})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.personal_setImage:        //点击换头像
                showImageDialog();
                break;
            case R.id.ll_personal_setName:      //设置姓名
                startActivityForResult(new Intent(this, PersonalNameActivity.class),
                        REQUEST_SETTING);
                break;
            case R.id.ll_personal_setPhone:     //设置电话号码
                startActivityForResult(new Intent(this, PersonalPhoneActivity.class),
                        REQUEST_SETTING);
                break;
            case R.id.ll_personal_setEmail:     //设置邮箱
                startActivityForResult(new Intent(this, PersonalEmailActivity.class),
                        REQUEST_SETTING);
                break;
            case R.id.ll_personal_setRegion:    //设置省市县
                setRegion(view);
                break;
            case R.id.ll_personal_setIdCard:    //设置身份证号
                startActivityForResult(new Intent(this, PersonalIDCardActivity.class),
                        REQUEST_SETTING);
                break;
            case R.id.ll_personal_setAddress:   //设置常用邮寄地址
                startActivity(new Intent(this, PersonalAddressActivity.class));
                break;
            case R.id.ll_personal_changePwd:    //修改密码
                startActivity(new Intent(this, ChangePwdActivity.class));
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SETTING) {
            switch (resultCode) {
                case 1:
                    String name = data.getStringExtra("name");
                    mPersonalName.setText(name);
                    break;
                case 2:
                    String phone = data.getStringExtra("phone");
                    mPersonalPhone.setText(phone);
                    break;
                case 3:
                    String email = data.getStringExtra("email");
                    mPersonalEmail.setText(email);
                    break;
                case 4:
                    String idCard = data.getStringExtra("idCard");
                    mPersonalIdCard.setText(idCard);
                    break;
            }
        } else if (requestCode == PHOTO_REQUEST_GALLERY) {
            // 从相册返回的数据
            if (data != null) {
                Uri uri = data.getData();
                crop(uri);
            }
        } else if (requestCode == PHOTO_REQUEST_CAMERA) {
            // 从相机返回的数据
            if (resultCode == RESULT_OK) {
                crop(mUri);
            }
        } else if (requestCode == PHOTO_REQUEST_CUT) {
            // 从剪切图片返回的数据
            if (data != null && resultCode == RESULT_OK) {
                Bitmap bitmap = data.getParcelableExtra("data");
                //显示在本地
                mPersonalSetImage.setImageBitmap(bitmap);
                //保存在在SD卡中
                File file = BitmapUtil.setPicToView(bitmap, "head.jpg");
                /**
                 * 更新头像
                 */
                String UPLOAD_PROFILE_IMAGE = "/api/user/upload_profile_image";
                String url = Constant.BASE_URL + UPLOAD_PROFILE_IMAGE;
                String accessToken = (String) SharePreferenceUtil.get(this, "accessToken", "");
                LogUtil.Companion.d("头像设置：" + accessToken);

                OkHttpUtils.post().url(url)
                        .addHeader(ACCESS_TOKEN, mAccessToken)
                        .addFile("profileImage", "head.jpg", file)
                        .build().execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Util.showTimeOutNotice(currentContext);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        LogUtil.Companion.d("更新头像" + response);
                        ErrorBean errorBean = parseObject(response, ErrorBean.class);
                        switch (errorBean.getStatus()) {
                            case "success":
                                ImageBean imageBean = parseObject(response,
                                        ImageBean.class);
                                //拿到头像的URL地址，更新本地数据
                                String profileImageUrl = imageBean.getProfileImageUrl();
                                SharePreferenceUtil.put(getApplicationContext(), Constant
                                        .PROFILEIMAGEURL, profileImageUrl);
                                break;
                            case "failure":
                                Util.showError(currentContext, errorBean.getReason());
                                break;
                        }
                    }
                });
                try {
                    // 将临时文件删除
                    tempFile.delete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * 点击换头像
     */
    private void showImageDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        CharSequence[] items = {"拍照", "从相册选择"};
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        //打开相机拍照,激活相机
                        Intent intentCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        tempFile = new File(Environment.getExternalStorageDirectory(),
                                PHOTO_FILE_NAME);
                        // 从文件中创建uri
                        mUri = Uri.fromFile(tempFile);
                        intentCapture.putExtra(MediaStore.EXTRA_OUTPUT, mUri);
                        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CAMERA
                        startActivityForResult(intentCapture, PHOTO_REQUEST_CAMERA);
                        mDialog.dismiss();
                        break;
                    case 1:
                        //打开相册,激活系统图库，选择一张图片
                        Intent intentPick = new Intent(Intent.ACTION_PICK);
                        intentPick.setType("image/*");
                        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_GALLERY
                        startActivityForResult(intentPick, PHOTO_REQUEST_GALLERY);
                        mDialog.dismiss();
                        break;
                }
            }
        });
        mDialog = builder.create();
        mDialog.show();
    }


    /**
     * 剪切图片
     *
     * @param uri
     */
    private void crop(Uri uri) {
        // 裁剪图片意图
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // 裁剪框的比例，1：1
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // 裁剪后输出图片的尺寸大小
        intent.putExtra("outputX", 250);
        intent.putExtra("outputY", 250);

        intent.putExtra("outputFormat", "JPEG");// 图片格式
        intent.putExtra("noFaceDetection", true);// 取消人脸识别
        intent.putExtra("return-data", true);

        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CUT
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    /**
     * 设置省市县
     *
     * @param view
     */
    private void setRegion(View view) {
        //设置三级联动
        mOptions.setPicker(mOptions1Items, mOptions2Items, mOptions3Items);
        mOptions.setFocusable(true);
        //设置不循环
        mOptions.setCyclic(false);
        //设置默认选中的三级项目
        mOptions.setSelectOptions(0, 1, 1);
        //监听确定选择按钮
        mOptions.setOnoptionsSelectListener(new CharacterPickerWindow.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                final String region = mOptions1Items.get(options1)
                        + "\t" + mOptions2Items.get(options1).get(option2)
                        + "\t" + mOptions3Items.get(options1).get(option2).get(options3);

                //点击确定后，将数据上传服务器
                //省份id
                int provinceId = mProvinceList.get(options1).getId();
                //市id
                int cityId = mProvinceList.get(options1).getCityList().get(option2).getId();
                //区/县id
                int countyId = mProvinceList.get(options1).getCityList().get(option2).getAreaList()
                        .get(options3).getId();
                //更新用户区域信息
                OkHttpUtils.post().url(URL_UPDATE_REGION)
                        .addHeader(ACCESS_TOKEN, mAccessToken)
                        .addParams("provinceId", provinceId + "")
                        .addParams("cityId", cityId + "")
                        .addParams("areaId", countyId + "")
                        .build()
                        .execute(new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                LogUtil.Companion.d("更新用户区域信息E：" + e.getMessage());
                                Util.showTimeOutNotice(currentContext);
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                LogUtil.Companion.d("更新用户区域信息：" + response);
                                ErrorBean errorBean = parseObject(response, ErrorBean.class);
                                switch (errorBean.getStatus()) {
                                    case "success":
                                        //提交成功，将新数据显示在用户界面
                                        mPersonalRegion.setText(region);
                                        //将新数据保存在本地
                                        SharePreferenceUtil.put(getApplicationContext(), Constant
                                                .REGION, region);
                                        break;
                                    case "failure":
                                        Util.showError(currentContext, errorBean.getReason());
                                        break;
                                }
                            }
                        });

            }
        });
        //点击弹出选项选择器
        mOptions.showAtLocation(view, Gravity.BOTTOM, 0, 0);

        mOptions.setFocusable(false);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //当三级联动对话框正在显示的时候，按下返回键，其实是让对话框消失
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mOptions.isShowing()) {
                mOptions.dismiss();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void leftOnClick() {
        LogUtil.Companion.d("leftOnClick???" + mOptions.isShowing());
        finish();
    }

    @Override
    public void rightOnClick() {

    }
}
