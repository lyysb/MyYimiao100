package com.yimiao100.sale.activity;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.blankj.utilcode.util.SPUtils;
import com.squareup.picasso.Picasso;
import com.yimiao100.sale.R;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.bean.Area;
import com.yimiao100.sale.bean.City;
import com.yimiao100.sale.bean.ErrorBean;
import com.yimiao100.sale.bean.ImageBean;
import com.yimiao100.sale.bean.Province;
import com.yimiao100.sale.utils.*;
import com.yimiao100.sale.view.TitleView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import org.jetbrains.annotations.NotNull;

import static com.yimiao100.sale.ext.JSON.parseObject;


/**
 * 个人设置界面
 */
public class PersonalSettingActivity extends BaseActivity implements TitleView
        .TitleBarOnClickListener, DialogManager.onPicCropListener, OptionsPickerView.OnOptionsSelectListener, RegionUtil.HandleRegionListListener {

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

    private List<String> mOptions1Items;
    private List<List<String>> mOptions2Items;
    private List<List<List<String>>> mOptions3Items;

    private final String URL_UPDATE_REGION = Constant.BASE_URL + "/api/user/update_region";
    private final String UPLOAD_PROFILE_IMAGE = Constant.BASE_URL + "/api/user/upload_profile_image";

    private List<Province> mProvinceList;
    private OptionsPickerView regionPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_setting);
        ButterKnife.bind(this);

        initPickerView();

        initData();

        mPersonalTitle.setOnTitleBarClick(this);
    }

    private void initPickerView() {
        regionPicker = new OptionsPickerView.Builder(this, this)
                .setContentTextSize(16)
                .setSubCalSize(14)
                .setSubmitColor(getResources().getColor(R.color.colorMain))
                .setCancelColor(getResources().getColor(R.color.colorMain))
                .setOutSideCancelable(false)
                .build();
    }

    /**
     * 获取用户数据
     */
    private void initData() {
        //从本地获取数据
        //获取用户姓名
        String user_name = SPUtils.getInstance().getString(Constant.CNNAME);
        //获取用户注册账号
        String accountNumber = SPUtils.getInstance().getString(Constant.ACCOUNT_NUMBER, getString(R.string.unknown));
        //获取用户电话
        String user_phone_number = SPUtils.getInstance().getString(Constant.PHONENUMBER, accountNumber);
        //获取用户邮箱
        String user_email = SPUtils.getInstance().getString(Constant.EMAIL);
        //获取用户推广地域并设置
        String region = SPUtils.getInstance().getString(Constant.REGION);
        //获取用户身份证号
        String user_id_number = SPUtils.getInstance().getString(Constant.IDNUMBER);
        //获取用户头像URL
        String user_icon_url = SPUtils.getInstance().getString(Constant.PROFILEIMAGEURL);

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
        RegionUtil.getRegionList(this, this);
    }

    @Override
    public void handleRegionList(ArrayList<Province> provinceList) {
        mProvinceList = provinceList;
        // 进一步加工数据
        handleRegionData();
    }

    /**
     * 进一步加工数据
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
            List<String> mOptions2Items_temp = new ArrayList<>();
            List<List<String>> mOptions3Items_temp = new ArrayList<>();
            for (City city : cityList) {
                String CityName = city.getName();
                mOptions2Items_temp.add(CityName == null ? "" : CityName);
                List<Area> areaList = city.getAreaList();
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
        regionPicker.setPicker(mOptions1Items, mOptions2Items, mOptions3Items);
    }

    @OnClick({R.id.personal_setImage, R.id.ll_personal_setName, R.id.ll_personal_setPhone, R.id
            .ll_personal_setEmail, R.id.ll_personal_setRegion, R.id.ll_personal_setIdCard, R.id
            .ll_personal_changePwd, R.id.ll_personal_setAddress})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.personal_setImage:        //点击换头像
//                showImageDialog();
                DialogManager.getInstance().showPicDialog(view).setOnPicCropListener(this);
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
                regionPicker.show(view);
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
        DialogManager.getInstance().onPicActivityResult(requestCode, resultCode, data);
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
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        DialogManager.getInstance().onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * 更新头像
     */
    private void updateHeadImage(File file) {
        OkHttpUtils.post().url(UPLOAD_PROFILE_IMAGE)
                .addHeader(ACCESS_TOKEN, accessToken)
                .addFile("profileImage", "head.jpg", file)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Util.showTimeOutNotice(currentContext);
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.d("更新头像" + response);
                ErrorBean errorBean = parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        ImageBean imageBean = parseObject(response,
                                ImageBean.class);
                        //拿到头像的URL地址，更新本地数据
                        String profileImageUrl = imageBean.getProfileImageUrl();
                        SPUtils.getInstance().put(Constant.PROFILEIMAGEURL, profileImageUrl);
                        break;
                    case "failure":
                        Util.showError(currentContext, errorBean.getReason());
                        break;
                }
            }
        });
    }


    @Override
    public void onOptionsSelect(int options1, int options2, int options3, View v) {
        final String region = mOptions1Items.get(options1)
                + "\t" + mOptions2Items.get(options1).get(options2)
                + "\t" + mOptions3Items.get(options1).get(options2).get(options3);

        //点击确定后，将数据上传服务器
        //省份id
        int provinceId = mProvinceList.get(options1).getId();
        //市id
        int cityId = mProvinceList.get(options1).getCityList().get(options2).getId();
        //区/县id
        int countyId = mProvinceList.get(options1).getCityList().get(options2).getAreaList()
                .get(options3).getId();
        //更新用户区域信息
        OkHttpUtils.post().url(URL_UPDATE_REGION)
                .addHeader(ACCESS_TOKEN, accessToken)
                .addParams("provinceId", provinceId + "")
                .addParams("cityId", cityId + "")
                .addParams("areaId", countyId + "")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtil.d("更新用户区域信息E：" + e.getMessage());
                        Util.showTimeOutNotice(currentContext);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        LogUtil.d("更新用户区域信息：" + response);
                        ErrorBean errorBean = parseObject(response, ErrorBean.class);
                        switch (errorBean.getStatus()) {
                            case "success":
                                //提交成功，将新数据显示在用户界面
                                mPersonalRegion.setText(region);
                                //将新数据保存在本地
                                SPUtils.getInstance().put(Constant.REGION, region);
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

    @Override
    public void handleBitmap(View view, Bitmap bitmap) {
        //显示在本地
        mPersonalSetImage.setImageBitmap(bitmap);
        //保存在在SD卡中
        File file = BitmapUtil.setPicToView(bitmap, "head.jpg");
        updateHeadImage(file);
    }
}