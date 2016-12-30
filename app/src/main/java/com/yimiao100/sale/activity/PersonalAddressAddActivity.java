package com.yimiao100.sale.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.yimiao100.sale.R;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.bean.Area;
import com.yimiao100.sale.bean.City;
import com.yimiao100.sale.bean.ErrorBean;
import com.yimiao100.sale.bean.Province;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.RegionUtil;
import com.yimiao100.sale.utils.ToastUtil;
import com.yimiao100.sale.utils.Util;
import com.yimiao100.sale.view.TitleView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jeesoft.widget.pickerview.CharacterPickerWindow;
import okhttp3.Call;

/**
 * 添加地址
 */
public class PersonalAddressAddActivity extends BaseActivity implements TitleView.TitleBarOnClickListener, RegionUtil.HandleRegionListListener {

    @BindView(R.id.address_name)
    EditText mAddressName;
    @BindView(R.id.address_phone)
    EditText mAddressPhone;
    @BindView(R.id.address_address)
    TextView mAddressAddress;
    @BindView(R.id.address_code)
    EditText mAddressCode;
    @BindView(R.id.address_detail)
    EditText mAddressDetail;
    @BindView(R.id.address_add_title)
    TitleView mAddressAddTitle;


    private ArrayList<Province> mProvinceList;
    private CharacterPickerWindow mOptions;
    private List<String> mOptions1Items;
    private List<List<String>> mOptions2Items;
    private List<List<List<String>>> mOptions3Items;

    private int mProvinceId;
    private int mCityId;
    private int mCountyId;
    private Bundle mBundle;
    private String mAddressId;

    private final String URL_UPDATE_ADDRESS = Constant.BASE_URL + "/api/user/update_address";
    private final String URL_ADD_ADDRESS = Constant.BASE_URL + "/api/user/add_address";
    private final String URL_REGION_LIST = Constant.BASE_URL + "/api/region/all";
    private final String ADDRESS_ID = "addressId";
    private final String PROVINCE_ID = "provinceId";
    private final String CITY_ID = "cityId";
    private final String AREA_ID = "areaId";
    private final String CN_NAME = "cnName";
    private final String PHONE_NUMBER = "phoneNumber";
    private final String ZIP_CODE = "zipCode";
    private final String FULL_ADDRESS = "fullAddress";
    private final int FROM_NEW_OK = 100;
    private final int FROM_NEW_CANCEL = 101;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_address_add);
        ButterKnife.bind(this);

        initView();

        initData();
    }

    private void initView() {
        mOptions = new CharacterPickerWindow(this);
        mAddressAddTitle.setOnTitleBarClick(this);
    }

    private void initData() {
        Intent intent = getIntent();
        mBundle = intent.getExtras();
        if (mBundle != null) {
            String name = mBundle.getString("name");
            mAddressName.setText(name);
            String phone = mBundle.getString("phone");
            mAddressPhone.setText(phone);
            String region = mBundle.getString("region");
            mAddressAddress.setText(region);
            String code = mBundle.getString("code");
            mAddressCode.setText(code);
            String full = mBundle.getString("full");
            mAddressDetail.setText(full);

            mProvinceId = mBundle.getInt("provinceId");
            mCityId = mBundle.getInt("cityId");
            mCountyId = mBundle.getInt("areaId");
            mAddressId = mBundle.getString("addressId");
        }

        RegionUtil.getRegionList(this, this);
    }

    @Override
    public void handleRegionList(ArrayList<Province> provinceList) {
        mProvinceList = provinceList;
        handleRegionData();
    }

    /**
     * 处理数据
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

    @OnClick({R.id.address_name_clear, R.id.address_phone_clear, R.id.address_address, R.id
            .address_code_clear})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.address_name_clear:
                mAddressName.setText("");
                break;
            case R.id.address_phone_clear:
                mAddressPhone.setText("");
                break;
            case R.id.address_address:
                showRegion(view);
                break;
            case R.id.address_code_clear:
                mAddressCode.setText("");
                break;
        }
    }

    /**
     * 显示地址选择器
     * @param view
     */
    private void showRegion(View view) {
        //设置三级联动
        mOptions.setPicker(mOptions1Items, mOptions2Items, mOptions3Items);
        //设置可以获取焦点
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
                mAddressAddress.setText(region);
                //省份id
                mProvinceId = mProvinceList.get(options1).getId();
                //市id
                mCityId = mProvinceList.get(options1).getCityList().get(option2).getId();
                //区/县id
                mCountyId = mProvinceList.get(options1).getCityList().get(option2).getAreaList()
                        .get(options3).getId();
            }
        });
        //点击弹出选项选择器
        mOptions.showAtLocation(view, Gravity.BOTTOM, 0, 0);
        mOptions.setFocusable(false);
    }



    @Override
    public void rightOnClick() {
        if (mAddressName.getText().toString().trim().isEmpty()
                || mAddressPhone.getText().toString().trim().isEmpty()
                || TextUtils.equals(mAddressAddress.getText().toString(), "请添加收货人所在地区")
                || mAddressCode.getText().toString().trim().isEmpty()
                || mAddressDetail.getText().toString().trim().isEmpty()
                ) {
            ToastUtil.showShort(getApplicationContext(), "请填写所有数据");
            return;
        }
        if (mBundle != null) {
            editAddress();
        } else {
            addAddress();
        }
    }


    /**
     * 编辑收货地址
     */
    private void editAddress() {
        OkHttpUtils.post().url(URL_UPDATE_ADDRESS).addHeader(ACCESS_TOKEN, mAccessToken)
                .addParams(ADDRESS_ID, mAddressId).addParams(PROVINCE_ID, mProvinceId + "")
                .addParams(CITY_ID, mCityId + "").addParams(AREA_ID, mCountyId + "")
                .addParams(CN_NAME, mAddressName.getText().toString().trim())
                .addParams(PHONE_NUMBER, mAddressPhone.getText().toString().trim())
                .addParams(ZIP_CODE, mAddressCode.getText().toString().trim())
                .addParams(FULL_ADDRESS, mAddressDetail.getText().toString().trim())
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.Companion.d("编辑收货地址E：" + e.getLocalizedMessage());
                Util.showTimeOutNotice(currentContext);
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.Companion.d("编辑收货地址：" + response);
                LogUtil.Companion.d("provinceId：" + mProvinceId + "");
                LogUtil.Companion.d("cityId：" + mCityId + "");
                LogUtil.Companion.d("areaId：" + mCountyId + "");
                LogUtil.Companion.d("cnName：" + mAddressName.getText().toString().trim());
                LogUtil.Companion.d("phoneNumber：" + mAddressPhone.getText().toString().trim());
                LogUtil.Companion.d("zipCode：" + mAddressCode.getText().toString().trim());
                LogUtil.Companion.d("fullAddress：" + mAddressDetail.getText().toString().trim());
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        ToastUtil.showShort(getApplicationContext(), "修改成功");
                        setResult(RESULT_OK);
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
     * 增加收货地址
     */
    private void addAddress() {
        OkHttpUtils.post().url(URL_ADD_ADDRESS).addHeader(ACCESS_TOKEN, mAccessToken)
                .addParams(PROVINCE_ID, mProvinceId + "").addParams(CITY_ID, mCityId + "")
                .addParams(AREA_ID, mCountyId + "")
                .addParams(CN_NAME, mAddressName.getText().toString().trim())
                .addParams(PHONE_NUMBER, mAddressPhone.getText().toString().trim())
                .addParams(ZIP_CODE, mAddressCode.getText().toString().trim())
                .addParams(FULL_ADDRESS, mAddressDetail.getText().toString().trim())
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.Companion.d("提交收货地址E：" + e.getLocalizedMessage());
                Util.showTimeOutNotice(currentContext);
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.Companion.d("提交收货地址：" + response);
                LogUtil.Companion.d("provinceId：" + mProvinceId + "");
                LogUtil.Companion.d("cityId：" + mCityId + "");
                LogUtil.Companion.d("areaId：" + mCountyId + "");
                LogUtil.Companion.d("cnName：" + mAddressName.getText().toString().trim());
                LogUtil.Companion.d("phoneNumber：" + mAddressPhone.getText().toString().trim());
                LogUtil.Companion.d("zipCode：" + mAddressCode.getText().toString().trim());
                LogUtil.Companion.d("fullAddress：" + mAddressDetail.getText().toString().trim());
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        ToastUtil.showShort(getApplicationContext(), "保存成功");
                        setResult(FROM_NEW_OK);         //用于积分商城确认订单
                        finish();
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
        setResult(FROM_NEW_CANCEL);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //当三级联动对话框正在显示的时候，按下返回键，其实是让对话框消失
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mOptions.isShowing()) {
                mOptions.dismiss();
                return true;
            }
            setResult(FROM_NEW_CANCEL);
        }
        return super.onKeyDown(keyCode, event);
    }


}
