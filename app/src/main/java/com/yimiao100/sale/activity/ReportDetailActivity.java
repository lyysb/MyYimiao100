package com.yimiao100.sale.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.TimePickerView;
import com.yimiao100.sale.R;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.bean.AdverseApply;
import com.yimiao100.sale.bean.ErrorBean;
import com.yimiao100.sale.utils.CompressUtil;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.Regex;
import com.yimiao100.sale.utils.TimeUtil;
import com.yimiao100.sale.utils.ToastUtil;
import com.yimiao100.sale.utils.Util;
import com.yimiao100.sale.view.TitleView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.PhotoPreview;
import okhttp3.Call;
import okhttp3.Request;

import static com.yimiao100.sale.utils.TimeUtil.timeStamp2Date;


/**
 * 不良反应申报页
 */
public class ReportDetailActivity extends BaseActivity implements TitleView
        .TitleBarOnClickListener, OptionsPickerView.OnOptionsSelectListener, TimePickerView
        .OnTimeSelectListener {

    private final String URL_ADVERSE_APPLY = Constant.BASE_URL + "/api/apply/adverse_apply";
    private final String ADVERSE_APPLY_ID = "adverseApplyId";
    private final String URL_SUBMIT_APPLY = Constant.BASE_URL + "/api/apply/submit_adverse_apply";
    private final String VENDOR_ID = "vendorId";
    private final String CATEGORY_ID = "categoryId";
    private final String PRODUCT_ID = "productId";
    private final String SPEC_ID = "specId";
    private final String DOSAGE_FORM_ID = "dosageFormId";
    private final String PROVINCE_ID = "provinceId";
    private final String CITY_ID = "cityId";
    private final String AREA_ID = "areaId";
    private final String INJECT_AT = "injectAt";
    private final String PATIENT_NAME = "patientName";
    private final String PATIENT_BIRTH_DATE = "patientBirthDate";
    private final String PATIENT_SEX = "patientSex";
    private final String ADVERSE_DESC = "adverseDesc";
    private final String DIAGNOSTIC_HOSPITAL = "diagnosticHospital";
    private final String DIAGNOSTIC_RESULT = "diagnosticResult";
    private final String DIAGNOSTIC_FILE = "diagnosticFile";
    private final String CONTACT_NAME = "contactName";
    private final String CONTACT_PHONE_NUMBER = "contactPhoneNumber";
    private final String CONTACT_EMAIL = "contactEmail";
    @BindView(R.id.report_detail_title)
    TitleView mTitle;
    @BindView(R.id.report_detail_inject_at)
    TextView mTvInjectAt;
    @BindView(R.id.report_detail_province)
    TextView mTvProvince;
    @BindView(R.id.report_detail_city)
    TextView mTvCity;
    @BindView(R.id.report_detail_area)
    TextView mTvArea;
    @BindView(R.id.report_detail_patient_name)
    EditText mEvPatientName;
    @BindView(R.id.report_detail_patient_sex)
    ImageView mIvPatientSex;
    @BindView(R.id.report_detail_patient_birth_date)
    TextView mTvPatientBirthDate;
    @BindView(R.id.report_detail_patient_adverse)
    EditText mEvPatientAdverse;
    @BindView(R.id.report_detail_hospital)
    EditText mEvHospital;
    @BindView(R.id.report_detail_result)
    EditText mEvResult;
    @BindView(R.id.report_detail_file)
    ImageView mIvFile;
    @BindView(R.id.report_detail_contact_name)
    EditText mEvContactName;
    @BindView(R.id.report_detail_contact_phone_number)
    EditText mEvContactPhoneNumber;
    @BindView(R.id.report_detail_contact_email)
    EditText mEvContactEmail;
    @BindView(R.id.report_detail_submit)
    Button mSubmit;
    @BindView(R.id.report_detail_vendor_name)
    TextView mTvVendorName;
    @BindView(R.id.report_detail_category_name)
    TextView mTvCategoryName;
    @BindView(R.id.report_detail_product_name)
    TextView mTvProductName;
    @BindView(R.id.report_detail_spec)
    TextView mTvSpec;
    @BindView(R.id.report_detail_dosage_form)
    TextView mTvDosageForm;


    private int mAdverseApplyId;
    private HashMap<String, String> mParams;
    private int mVendorId;
    private int mCategoryId;
    private int mProductId;
    private int mSpecId;
    private int mDosageFormId;
    private int mProvinceId;
    private int mCityId;
    private int mAreaId;
    private String mInjectAt;
    private String mPatientName;
    private String mPatientBirthDate;
    private String mPatientSex;
    private String mAdverseDesc;
    private String mDiagnosticHospital;
    private File mDiagnosticFile;
    private String mDiagnosticResult;
    private String mContactName;
    private String mContactPhoneNumber;
    private String mContactEmail;

    private String mDiagnosticFileName;
    private ProgressDialog mProgressDialog;
    private TimePickerView mTimePickerView;
    private OptionsPickerView mOptionsPicker;
    private List<AdverseApply.RegionListBean> mRegionList;
    private List<AdverseApply.RegionListBean.CityListBean> mCityList;
    private List<AdverseApply.RegionListBean.CityListBean.AreaListBean> mAreaList;
    private List<AdverseApply.BizListBean> mBizList;
    private List<AdverseApply.BizListBean.CategoryListBean> mCategoryList;
    private List<AdverseApply.BizListBean.CategoryListBean.ProductListBean> mProductList;
    private Date mInjectDate;
    private Date mBirthDate;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_detail);
        ButterKnife.bind(this);

        initVariable();

        showLoadingProgress();

        initView();

        initData();
    }

    private void initVariable() {
        mAdverseApplyId = getIntent().getIntExtra("adverseApplyId", -1);
        LogUtil.Companion.d("adverseApplyId is " + mAdverseApplyId);
        mParams = new HashMap<>();
        if (mAdverseApplyId != -1) {
            mParams.put(ADVERSE_APPLY_ID, mAdverseApplyId + "");
        }
    }

    private void initView() {
        mTitle.setOnTitleBarClick(this);
        initOptionsPickerView();
        initTimePickerView();
    }

    private void initOptionsPickerView() {
        mOptionsPicker = new OptionsPickerView.Builder(this, this)
                .setContentTextSize(14)
                .setSubCalSize(14)
                .setSubmitColor(getResources().getColor(R.color.colorMain))
                .setCancelColor(getResources().getColor(R.color.colorMain))
                .setOutSideCancelable(false)
                .build();
    }

    private void initTimePickerView() {
        Calendar startDate = Calendar.getInstance();
        startDate.set(1900, 1, 1);
        mTimePickerView = new TimePickerView.Builder(this, this)
                .setType(TimePickerView.Type.YEAR_MONTH_DAY)
                .setContentSize(14)
                .setSubCalSize(14)
                .setSubmitColor(getResources().getColor(R.color.colorMain))
                .setCancelColor(getResources().getColor(R.color.colorMain))
                .setOutSideCancelable(false)
                .setRangDate(startDate, Calendar.getInstance())
                .setDate(Calendar.getInstance()).build();
    }

    private void initData() {
        OkHttpUtils.post().url(URL_ADVERSE_APPLY).addHeader(ACCESS_TOKEN, mAccessToken)
                .params(mParams).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                hideLoadingProgress();
                LogUtil.Companion.d("init data error");
                e.printStackTrace();
                Util.showTimeOutNotice(currentContext);
                mBizList = new ArrayList<>();
                mBizList.add(processingBiz());
                mRegionList = new ArrayList<>();
                mRegionList.add(processingRegion());
            }

            @Override
            public void onResponse(String response, int id) {
                hideLoadingProgress();
                LogUtil.Companion.d("init data success, response is\n" + response);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        echoData(response);
                        break;
                    case "failure":
                        Util.showError(currentContext, errorBean.getReason());
                        mBizList = new ArrayList<>();
                        mBizList.add(processingBiz());
                        mRegionList = new ArrayList<>();
                        mRegionList.add(processingRegion());
                        break;
                }
            }
        });
    }

    /**
     * 回显/设置数据源
     *
     * @param response
     */
    private void echoData(String response) {
        AdverseApply adverseApply = JSON.parseObject(response, AdverseApply.class);
        // 处理产品信息
        mBizList = adverseApply.getBizList();
        if (mBizList.isEmpty()) {
            mBizList.add(processingBiz());
        }
        // 处理接种地点信息
        mRegionList = adverseApply.getRegionList();
        if (mRegionList.isEmpty()) {
            mRegionList.add(processingRegion());
        }
        // 处理回显信息
        if (adverseApply.getAdverseApply() != null) {
            // 禁止按钮点击
            forbidButton();
            // 回显数据
            processingAdverse(adverseApply.getAdverseApply());
        }
        if (mPatientSex == null) {
            mPatientSex = "male";
        }
    }

    /**
     * @return 空白假数据
     */
    private AdverseApply.BizListBean processingBiz() {
        AdverseApply.BizListBean bizListBean = new AdverseApply.BizListBean();
        AdverseApply.BizListBean.CategoryListBean categoryListBean
                = new AdverseApply.BizListBean.CategoryListBean();
        AdverseApply.BizListBean.CategoryListBean.ProductListBean productListBean
                = new AdverseApply.BizListBean.CategoryListBean.ProductListBean();

        productListBean.setDosageForm("");
        productListBean.setProductName("");
        productListBean.setSpec("");

        categoryListBean.setCategoryName("");
        ArrayList<AdverseApply.BizListBean.CategoryListBean.ProductListBean> productListBeen =
                new ArrayList<>();
        productListBeen.add(productListBean);
        categoryListBean.setProductList(productListBeen);

        bizListBean.setVendorName("");
        ArrayList<AdverseApply.BizListBean.CategoryListBean> categoryList = new ArrayList<>();
        categoryList.add(categoryListBean);
        bizListBean.setCategoryList(categoryList);
        return bizListBean;
    }


    /**
     * @return 空白假数据
     */
    private AdverseApply.RegionListBean processingRegion() {
        AdverseApply.RegionListBean regionListBean = new AdverseApply.RegionListBean();
        AdverseApply.RegionListBean.CityListBean cityListBean
                = new AdverseApply.RegionListBean.CityListBean();
        AdverseApply.RegionListBean.CityListBean.AreaListBean areaListBean
                = new AdverseApply.RegionListBean.CityListBean.AreaListBean();

        areaListBean.setName("");

        cityListBean.setName("");
        ArrayList<AdverseApply.RegionListBean.CityListBean.AreaListBean> areaList
                = new ArrayList<>();
        areaList.add(areaListBean);
        cityListBean.setAreaList(areaList);

        regionListBean.setName("");
        ArrayList<AdverseApply.RegionListBean.CityListBean> cityList = new ArrayList<>();
        cityList.add(cityListBean);
        regionListBean.setCityList(cityList);

        return regionListBean;
    }

    private void forbidButton() {
        mTvVendorName.setEnabled(false);
        mTvCategoryName.setEnabled(false);
        mTvProductName.setEnabled(false);
        mTvSpec.setEnabled(false);
        mTvDosageForm.setEnabled(false);
        mTvInjectAt.setEnabled(false);
        mTvProvince.setEnabled(false);
        mTvCity.setEnabled(false);
        mTvArea.setEnabled(false);
        mEvPatientName.setEnabled(false);
        mTvPatientBirthDate.setEnabled(false);
        mEvPatientAdverse.setEnabled(false);
        mEvHospital.setEnabled(false);
        mEvResult.setEnabled(false);
        mEvContactName.setEnabled(false);
        mEvContactPhoneNumber.setEnabled(false);
        mEvContactEmail.setEnabled(false);
        mIvPatientSex.setEnabled(false);
        mIvFile.setEnabled(false);
        mSubmit.setEnabled(false);
    }

    /**
     * 提交过数据，则回显
     * @param adverseApply
     */
    private void processingAdverse(AdverseApply.AdverseApplyBean adverseApply) {
        LogUtil.Companion.d("apply status is " + adverseApply.getApplyStatus());
        mTvVendorName.setText(adverseApply.getVendorName());
        mTvCategoryName.setText(adverseApply.getCategoryName());
        mTvProductName.setText(adverseApply.getProductName());
        mTvSpec.setText(adverseApply.getSpec());
        mTvDosageForm.setText(adverseApply.getDosageForm());
        mTvInjectAt.setText(timeStamp2Date(adverseApply.getInjectAt() + "", "yyyy年MM月dd日"));
        mTvProvince.setText(adverseApply.getProvinceName());
        mTvCity.setText(adverseApply.getCityName());
        mTvArea.setText(adverseApply.getAreaName());
        mEvPatientName.setText(adverseApply.getPatientName());
        switch (adverseApply.getPatientSex()) {
            case "male":
                mIvPatientSex.setImageResource(R.mipmap.ico_application_authorization_choice_two);
                break;
            case "female":
                mIvPatientSex.setImageResource(R.mipmap.ico_application_authorization_choice);
                break;
            default:
                LogUtil.Companion.d("Unknown sex");
                break;
        }
        mTvPatientBirthDate.setText(TimeUtil.timeStamp2Date(adverseApply.getPatientBirthDate(), "yyyy年MM月dd日"));
        mEvPatientAdverse.setText(adverseApply.getAdverseDesc());
        mEvHospital.setText(adverseApply.getDiagnosticHospital());
        mEvResult.setText(adverseApply.getDiagnosticResult());
        if (!adverseApply.getDiagnosticFileUrl().isEmpty()) {
            mIvFile.setImageResource(R.mipmap.ico_application_authorization_success);
        } else {
            mIvFile.setImageResource(R.mipmap.ico_application_authorization_upload);
        }
        mEvContactName.setText(adverseApply.getContactName());
        mEvContactPhoneNumber.setText(adverseApply.getContactPhoneNumber());
        mEvContactEmail.setText(adverseApply.getContactEmail());
        mSubmit.setText(adverseApply.getApplyStatusName());
        mSubmit.setBackgroundResource(R.drawable.shape_button_forbid);
        mSubmit.setTextColor(Color.GRAY);
    }


    @OnClick({R.id.report_detail_record, R.id.report_detail_vendor_name,
            R.id.report_detail_category_name, R.id.report_detail_product_name,
            R.id.report_detail_spec, R.id.report_detail_dosage_form,
            R.id.report_detail_inject_at, R.id.report_detail_province,
            R.id.report_detail_city, R.id.report_detail_area,
            R.id.report_detail_patient_sex, R.id.report_detail_patient_birth_date,
            R.id.report_detail_file, R.id.report_detail_submit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.report_detail_record:
                startActivity(new Intent(this, ReportActivity.class));
                finish();
                break;
            case R.id.report_detail_vendor_name:
                mOptionsPicker.setPicker(mBizList);
                mOptionsPicker.show(view);
                break;
            case R.id.report_detail_category_name:
                if (mTvVendorName.getText().toString().isEmpty()) {
                    ToastUtil.showShort(this, "请先选择疫苗生产厂家");
                    return;
                }
                mOptionsPicker.setPicker(mCategoryList);
                mOptionsPicker.show(view);
                break;
            case R.id.report_detail_product_name:
                if (mTvCategoryName.getText().toString().isEmpty()) {
                    ToastUtil.showShort(this, "请先选择接种疫苗种类");
                    return;
                }
                mOptionsPicker.setPicker(mProductList);
                mOptionsPicker.show(view);
                break;
            case R.id.report_detail_spec:
            case R.id.report_detail_dosage_form:
                // 选择属性
                if (mTvProductName.getText().toString().isEmpty()) {
                    ToastUtil.showShort(this, "请先选择接种疫苗名称");
                }
                break;
            case R.id.report_detail_inject_at:
                // 选择注射疫苗时间
                mTimePickerView.show(view);
                break;
            case R.id.report_detail_province:
                mOptionsPicker.setPicker(mRegionList);
                mOptionsPicker.show(view);
                break;
            case R.id.report_detail_city:
                if (mTvProvince.getText().toString().isEmpty()) {
                    ToastUtil.showShort(this, "请先选择省");
                    return;
                }
                mOptionsPicker.setPicker(mCityList);
                mOptionsPicker.show(view);
                break;
            case R.id.report_detail_area:
                if (mTvCity.getText().toString().isEmpty()) {
                    ToastUtil.showShort(this, "请先选择省/市");
                    return;
                }
                mOptionsPicker.setPicker(mAreaList);
                mOptionsPicker.show(view);
                break;
            case R.id.report_detail_patient_sex:
                // 切换性别
                selectSex();
                break;
            case R.id.report_detail_patient_birth_date:
                // 选择出生日期
                mTimePickerView.show(view);
                break;
            case R.id.report_detail_file:
                // 选择扫描件
                selectScanner();
                break;
            case R.id.report_detail_submit:
                // 校验数据
                verifyData();
                break;
        }
    }

    @Override
    public void onOptionsSelect(int options1, int options2, int options3, View v) {
        switch (v.getId()) {
            case R.id.report_detail_vendor_name:
                // 选择疫苗生产厂家
                selectVendorName(options1);
                break;
            case R.id.report_detail_category_name:
                // 选择疫苗种类
                selectCategoryName(options1);
                break;
            case R.id.report_detail_product_name:
                // 选择疫苗名称
                selectProductName(options1);
                break;
            case R.id.report_detail_province:
                // 选择省
                selectProvince(options1);
                break;
            case R.id.report_detail_city:
                // 选择市
                selectCity(options1);
                break;
            case R.id.report_detail_area:
                // 选择区县
                selectArea(options1);
                break;
        }
    }

    @Override
    public void onTimeSelect(Date date, View v) {
        switch (v.getId()) {
            case R.id.report_detail_inject_at:
                mInjectDate = date;
                // 显示疫苗注射时间
                mTvInjectAt.setText(TimeUtil.getTime(date));
                mInjectAt = TimeUtil.getTime(date, "yyyy-MM-dd");
                break;
            case R.id.report_detail_patient_birth_date:
                mBirthDate = date;
                // 显示出生日期
                mTvPatientBirthDate.setText(TimeUtil.getTime(date));
                mPatientBirthDate = TimeUtil.getTime(date, "yyyy-MM-dd");
                break;
        }
    }

    /**
     * 选择生产厂家
     */
    private void selectVendorName(int options1) {
        AdverseApply.BizListBean temp = mBizList.get(options1);
        LogUtil.Companion.d("vendorName is " + temp.getVendorName());
        mVendorId = temp.getId();
        mCategoryList = temp.getCategoryList();
        mTvVendorName.setText(temp.getVendorName());
        mTvCategoryName.setText("");
        mTvProductName.setText("");
        mTvDosageForm.setText("");
        mTvSpec.setText("");
    }


    /**
     * 选择疫苗种类
     */
    private void selectCategoryName(int options1) {
        AdverseApply.BizListBean.CategoryListBean temp = mCategoryList.get(options1);
        LogUtil.Companion.d("category name is " + temp.getCategoryName());
        mCategoryId = temp.getId();
        mProductList = temp.getProductList();
        mTvCategoryName.setText(temp.getCategoryName());
        mTvProductName.setText("");
        mTvDosageForm.setText("");
        mTvSpec.setText("");
    }

    /**
     * 选择疫苗名称
     */
    private void selectProductName(int options1) {
        AdverseApply.BizListBean.CategoryListBean.ProductListBean temp = mProductList.get(options1);
        LogUtil.Companion.d("product name is " + temp.getProductName()
                + "\nspec is " + temp.getSpec()
                + "\ndosage form is " + temp.getDosageForm() );
        mProductId = temp.getId();
        mTvProductName.setText(temp.getProductName());
        mSpecId = temp.getSpecId();
        mTvSpec.setText(temp.getSpec());
        mDosageFormId = temp.getDosageFormId();
        mTvDosageForm.setText(temp.getDosageForm());
    }

    /**
     * 选择省
     */
    private void selectProvince(int options1) {
        AdverseApply.RegionListBean temp = mRegionList.get(options1);
        LogUtil.Companion.d("provinceName is " + temp.getName());
        mProvinceId = temp.getId();
        mCityList = temp.getCityList();
        mTvProvince.setText(temp.getName());
        mTvCity.setText("");
        mTvArea.setText("");
    }

    /**
     * 选择市
     */
    private void selectCity(int options1) {
        AdverseApply.RegionListBean.CityListBean temp = mCityList.get(options1);
        LogUtil.Companion.d("cityName is " + temp.getName());
        mCityId = temp.getId();
        mAreaList = temp.getAreaList();
        mTvCity.setText(temp.getName());
        mTvArea.setText("");
    }

    /**
     * 选择区/县
     */
    private void selectArea(int options1) {
        AdverseApply.RegionListBean.CityListBean.AreaListBean temp = mAreaList.get(options1);
        LogUtil.Companion.d("areaName is " + temp.getName());
        mAreaId = temp.getId();
        mTvArea.setText(temp.getName());
    }

    /**
     * 切换性别
     */
    private void selectSex() {
        if (mPatientSex == null || mPatientSex.equals("male")) {
            // 修改为女性
            mPatientSex = "female";
            mIvPatientSex.setImageResource(R.mipmap.ico_application_authorization_choice);
        } else if (mPatientSex.equals("female")) {
            // 修改为男性
            mPatientSex = "male";
            mIvPatientSex.setImageResource(R.mipmap.ico_application_authorization_choice_two);
        }
        LogUtil.Companion.d("sex is " + mPatientSex);
    }

    /**
     * 选择扫描件
     */
    private void selectScanner() {
        PhotoPicker.builder()
                .setPhotoCount(9).setShowCamera(false)
                .setPreviewEnabled(false).start(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PhotoPicker.REQUEST_CODE:
            case PhotoPreview.REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    ArrayList<String> photos;
                    if (data != null) {
                        photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                        for (String photo : photos) {
                            LogUtil.Companion.d("photo is " + photo);
                        }
                        String mark = timeStamp2Date(System.currentTimeMillis() + "",
                                "yyyyMMdd_HH_mm_ss");
                        mDiagnosticFileName = "scanner" + mark + ".zip";
                        // 将文件压缩到本地
                        mDiagnosticFile = CompressUtil.zipANDSave(photos, mDiagnosticFileName);
                        if (mDiagnosticFile == null) {
                            ToastUtil.showShort(currentContext, "文件操作失败，请换部手机");
                            return;
                        }
                        mIvFile.setImageResource(R.mipmap.ico_application_authorization_success);
                    }
                }
                break;
            default:
                LogUtil.Companion.d("Unknown request");
                break;
        }
    }

    /**
     * 校验数据完整性
     */
    private void verifyData() {
        if (mTvVendorName.getText().toString().isEmpty()) {
            ToastUtil.showShort(this, "请选择生产厂家");
            return;
        }
        if (mTvCategoryName.getText().toString().isEmpty()) {
            ToastUtil.showShort(this, "请选择疫苗种类");
            return;
        }
        if (mTvProductName.getText().toString().isEmpty()) {
            ToastUtil.showShort(this, "请选择疫苗名称");
            return;
        }
        if (mTvSpec.getText().toString().isEmpty()
                || mTvDosageForm.getText().toString().isEmpty()) {
            ToastUtil.showShort(this, "请选择疫苗属性");
            return;
        }

        if (mTvInjectAt.getText().toString().trim().isEmpty()) {
            ToastUtil.showShort(this, "请选择注射时间");
            return;
        }
        if (mTvProvince.getText().toString().trim().isEmpty()) {
            ToastUtil.showShort(this, "请选择所推广的区域/省");
            return;
        }
        if (mTvCity.getText().toString().trim().isEmpty()) {
            ToastUtil.showShort(this, "请选择所推广的区域/市");
            return;
        }
        if (mTvArea.getText().toString().trim().isEmpty()) {
            ToastUtil.showShort(this, "请选择所推广的区域区/县");
            return;
        }
        mPatientName = mEvPatientName.getText().toString().trim();
        if (mPatientName.isEmpty()) {
            ToastUtil.showShort(this, "请输入不良反应者姓名");
            return;
        }
        if (!mPatientName.matches(Regex.name)) {
            ToastUtil.showShort(this, "不良反应者" + getString(R.string.regex_name));
            return;
        }
        if (mTvPatientBirthDate.getText().toString().isEmpty()) {
            ToastUtil.showShort(this, "请选择出生日期");
            return;
        }
        if (mInjectDate != null && mBirthDate != null) {
            if (mInjectDate.before(mBirthDate)) {
                ToastUtil.showShort(this, "注射时间不可早于出生日期");
                return;
            }
        }
        mAdverseDesc = mEvPatientAdverse.getText().toString().trim();
        if (mAdverseDesc.isEmpty()) {
            ToastUtil.showShort(this, "请输入不良反应描述");
            return;
        }
        mDiagnosticHospital = mEvHospital.getText().toString().trim();
        if (mDiagnosticHospital.isEmpty()) {
            ToastUtil.showShort(this, "请输入诊断医院");
            return;
        }
        mDiagnosticResult = mEvResult.getText().toString().trim();
        if (mDiagnosticResult.isEmpty()) {
            ToastUtil.showShort(this, "请输入诊断结果");
            return;
        }
        if (mDiagnosticFile == null) {
            ToastUtil.showShort(this, "请选择诊断结果扫描件");
            return;
        }
        mContactName = mEvContactName.getText().toString().trim();
        if (mContactName.isEmpty()) {
            ToastUtil.showShort(this, "请输入推广人姓名");
            return;
        }
        if (!mContactName.matches(Regex.name)) {
            ToastUtil.showShort(this, "推广人" + getString(R.string.regex_name));
            return;
        }

        mContactPhoneNumber = mEvContactPhoneNumber.getText().toString().trim();
        if (mContactPhoneNumber.isEmpty()) {
            ToastUtil.showShort(this, "请输入推广人联系电话");
            return;
        }
        mContactEmail = mEvContactEmail.getText().toString().trim();
        if (mContactEmail.isEmpty()) {
            ToastUtil.showShort(this, "请输入推广人邮箱");
            return;
        }
        if (!mContactEmail.matches(Regex.email)) {
            ToastUtil.showShort(this, getString(R.string.regex_email));
            return;
        }
        // 确认提交
        confirmSubmit();
    }

    /**
     * 确认提交
     */
    private void confirmSubmit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.dialog);
        View view = View.inflate(this, R.layout.dialog_confirm_submit, null);
        TextView content = (TextView) view.findViewById(R.id.dialog_fillet);
        content.setText(getString(R.string.dialog_confirm_submit_report));
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        view.findViewById(R.id.dialog_check).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.dialog_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                // 提交数据
                submitData();
            }
        });
        dialog.show();
    }

    /**
     * 提交数据
     */
    private void submitData() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setTitle(getString(R.string.upload_progress_dialog_title));
        OkHttpUtils.post().url(URL_SUBMIT_APPLY).addHeader(ACCESS_TOKEN, mAccessToken)
                .addParams(VENDOR_ID, mVendorId + "")
                .addParams(CATEGORY_ID, mCategoryId + "")
                .addParams(PRODUCT_ID, mProductId + "")
                .addParams(SPEC_ID, mSpecId + "")
                .addParams(DOSAGE_FORM_ID, mDosageFormId + "")
                .addParams(PROVINCE_ID, mProvinceId + "")
                .addParams(CITY_ID, mCityId + "")
                .addParams(AREA_ID, mAreaId + "")
                .addParams(INJECT_AT, mInjectAt)
                .addParams(PATIENT_NAME, mPatientName)
                .addParams(PATIENT_BIRTH_DATE, mPatientBirthDate)
                .addParams(PATIENT_SEX, mPatientSex)
                .addParams(ADVERSE_DESC, mAdverseDesc)
                .addParams(DIAGNOSTIC_HOSPITAL, mDiagnosticHospital)
                .addParams(DIAGNOSTIC_RESULT, mDiagnosticResult)
                .addFile(DIAGNOSTIC_FILE, mDiagnosticFileName, mDiagnosticFile)
                .addParams(CONTACT_NAME, mContactName)
                .addParams(CONTACT_PHONE_NUMBER, mContactPhoneNumber)
                .addParams(CONTACT_EMAIL, mContactEmail)
                .build().execute(new StringCallback() {

            @Override
            public void onBefore(Request request, int id) {
                super.onBefore(request, id);
                mProgressDialog.show();
            }

            @Override
            public void inProgress(float progress, long total, int id) {
                super.inProgress(progress, total, id);
                mProgressDialog.setProgress((int) (100 * progress + 0.5));
            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
                mProgressDialog.dismiss();
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                e.printStackTrace();
                LogUtil.Companion.d("submit failure");
                Util.showTimeOutNotice(currentContext);
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.Companion.d("result is " + response);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        ToastUtil.showShort(currentContext, getString(R.string.upload_success));
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

    @Override
    public void leftOnClick() {
        if (mDiagnosticFile != null && mDiagnosticFile.exists()) {
            mDiagnosticFile.delete();
        }
        finish();
    }

    @Override
    public void rightOnClick() {

    }
}
