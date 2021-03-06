package com.yimiao100.sale.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.blankj.utilcode.util.KeyboardUtils;
import com.yimiao100.sale.R;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.bean.Address;
import com.yimiao100.sale.bean.Area;
import com.yimiao100.sale.bean.City;
import com.yimiao100.sale.bean.ErrorBean;
import com.yimiao100.sale.bean.Province;
import com.yimiao100.sale.ext.JSON;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.ProgressDialogUtil;
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
import okhttp3.Call;

/**
 * 添加地址
 */
public class PersonalAddressAddActivity extends BaseActivity implements TitleView.TitleBarOnClickListener, RegionUtil.HandleRegionListListener, OptionsPickerView.OnOptionsSelectListener {

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
    private Address mAddress;
    private ProgressDialog mProgressDialog;
    private OptionsPickerView regionPicker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_address_add);
        ButterKnife.bind(this);

        initView();

        initData();
    }

    private void initView() {
        regionPicker = new OptionsPickerView.Builder(this, this)
                .setContentTextSize(16)
                .setSubCalSize(14)
                .setSubmitColor(getResources().getColor(R.color.colorMain))
                .setCancelColor(getResources().getColor(R.color.colorMain))
                .setOutSideCancelable(false)
                .build();
        mAddressAddTitle.setOnTitleBarClick(this);
    }

    private void initData() {
        Intent intent = getIntent();
        mAddress = intent.getParcelableExtra("address");
        if (mAddress != null) {
            mAddressName.setText(mAddress.getCnName());
            mAddressPhone.setText(mAddress.getPhoneNumber());
            mAddressAddress.setText(mAddress.getProvinceName() + "\t" + mAddress.getCityName()
                    + "\t" + mAddress.getAreaName());
            mAddressCode.setText(mAddress.getZipCode());
            mAddressDetail.setText(mAddress.getFullAddress());

            mProvinceId = mAddress.getProvinceId();
            mCityId = mAddress.getCityId();
            mCountyId = mAddress.getAreaId();
            mAddressId = mAddress.getId() + "";

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
                // 也许有软键盘，先收起软键盘
                KeyboardUtils.hideSoftInput(this);
                // 显示地址选择器
                regionPicker.show(view);
                break;
            case R.id.address_code_clear:
                mAddressCode.setText("");
                break;
        }
    }





    @Override
    public void onOptionsSelect(int options1, int options2, int options3, View v) {
        final String region = mOptions1Items.get(options1)
                + "\t" + mOptions2Items.get(options1).get(options2)
                + "\t" + mOptions3Items.get(options1).get(options2).get(options3);
        mAddressAddress.setText(region);
        //省份id
        mProvinceId = mProvinceList.get(options1).getId();
        //市id
        mCityId = mProvinceList.get(options1).getCityList().get(options2).getId();
        //区/县id
        mCountyId = mProvinceList.get(options1).getCityList().get(options2).getAreaList().get(options3).getId();
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
        mProgressDialog = ProgressDialogUtil.getLoadingProgress(this, "提交中");
        mProgressDialog.show();
        if (mAddress != null) {
            editAddress();
        } else {
            addAddress();
        }
    }

    /**
     * 编辑收货地址
     */
    private void editAddress() {
        OkHttpUtils.post().url(URL_UPDATE_ADDRESS).addHeader(ACCESS_TOKEN, accessToken)
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
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.Companion.d("edit response is ：" + response);
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
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
        OkHttpUtils.post().url(URL_ADD_ADDRESS).addHeader(ACCESS_TOKEN, accessToken)
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
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.Companion.d("submit response is " + response);
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
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
            setResult(FROM_NEW_CANCEL);
        }
        return super.onKeyDown(keyCode, event);
    }
}
