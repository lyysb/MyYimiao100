package com.yimiao100.sale.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.yimiao100.sale.R;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.bean.Address;
import com.yimiao100.sale.bean.AuthorizationApply;
import com.yimiao100.sale.bean.CategoryList;
import com.yimiao100.sale.bean.ErrorBean;
import com.yimiao100.sale.bean.ProductList;
import com.yimiao100.sale.bean.Province;
import com.yimiao100.sale.bean.UploadBean;
import com.yimiao100.sale.bean.VendorList;
import com.yimiao100.sale.ext.JSON;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.ProgressDialogUtil;
import com.yimiao100.sale.utils.ToastUtil;
import com.yimiao100.sale.utils.Util;
import com.yimiao100.sale.view.TitleView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 申请授权书详情页
 */
public class AuthorizationDetailActivity extends BaseActivity implements TitleView
        .TitleBarOnClickListener, OptionsPickerView.OnOptionsSelectListener {

    private final String URL_APLLY = Constant.BASE_URL + "/api/apply/authz_apply";
    private final String URL_SUBMIT = Constant.BASE_URL + "/api/apply/submit_authz_apply";

    private final String AUTHZ_APPLY_ID = "authzApplyId";
    @BindView(R.id.authorization_detail_select_address)
    TextView mTvSelectAddress;
    private int mAuthzApplyId;
    @BindView(R.id.authorization_detail_title)
    TitleView mTitle;
    @BindView(R.id.authorization_detail_vendor_name)
    TextView mTvVendorName;
    @BindView(R.id.authorization_detail_category)
    TextView mTvCategory;
    @BindView(R.id.authorization_detail_product)
    TextView mTvProduct;
    @BindView(R.id.authorization_detail_spec)
    TextView mTvSpec;
    @BindView(R.id.authorization_detail_dosage_form)
    TextView mTvDosageForm;
    @BindView(R.id.authorization_detail_province)
    TextView mTvProvince;
    @BindView(R.id.authorization_detail_region_remark)
    EditText mEvRegionRemark;
    @BindView(R.id.authorization_detail_qualification_num)
    EditText mEvQualificationNum;
    @BindView(R.id.authorization_detail_contract_num)
    EditText mEvContractNum;
    @BindView(R.id.authorization_detail_agreement_num)
    EditText mEvAgreementNum;
    @BindView(R.id.authorization_detail_authz_copy_num)
    EditText mEvAuthzCopyNum;
    @BindView(R.id.authorization_detail_authz_num)
    EditText mEvAuthzNum;
    @BindView(R.id.authorization_detail_product_desc_num)
    EditText mEvProductDescNum;
    @BindView(R.id.authorization_detail_biz_license_copy_num)
    EditText mEvBizLicenseCopyNum;
    @BindView(R.id.authorization_detail_remark)
    EditText mEvRemark;
    @BindView(R.id.authorization_detail_cn_name)
    TextView mTvCnName;
    @BindView(R.id.authorization_detail_phone_number)
    TextView mTvPhoneNumber;
    @BindView(R.id.authorization_detail_full_address)
    TextView mTvFullAddress;
    @BindView(R.id.authorization_detail_submit)
    Button mBtnSubmit;

    private HashMap<String, String> mParams;

    private final String VENDOR_ID = "vendorId";
    private final String CATEGORY_ID = "categoryId";
    private final String PRODUCT_ID = "productId";
    private final String SPEC_ID = "specId";
    private final String DOSAGE_FORM_ID = "dosageFormId";
    private final String PROVINCE_ID = "provinceId";
    private final String REGION_REMARK = "regionRemark";
    private final String QUALIFICATION_NUM = "qualificationNum";
    private final String CONTRACT_NUM = "contractNum";
    private final String AGREEMENT_NUM = "agreementNum";
    private final String AUTHZ_NUM = "authzNum";
    private final String AUTHZ_COPY_NUM = "authzCopyNum";
    private final String PRODUCT_DESC_NUM = "productDescNum";
    private final String BIZ_LICENSE_COPY_NUM = "bizLicenseCopyNum";
    private final String REMARK = "remark";
    private final String CN_NAME = "cnName";
    private final String PHONE_NUMBER = "phoneNumber";
    private final String FULL_ADDRESS = "fullAddress";

    private int mVendorId;
    private int mCategoryId;
    private int mProductId;
    private int mSpecId;
    private int mDosageFormId;
    private int mProvinceId;
    private String mRegionRemark;
    private String mQualificationNum;
    private String mContractNum;
    private String mAgreementNum;
    private String mAuthzNum;
    private String mAuthzCopyNum;
    private String mProductDescNum;
    private String mBizLicenseCopyNum;
    private String mRemark;
    private String mCnName;
    private String mPhoneNumber;
    private String mFullAddress;
    private OptionsPickerView mOptionsPickerView;
    private final int REQUEST_ADDRESS = 100;
    private final int FROM_ITEM_OK = 200;
    private ArrayList<VendorList> mBizSelect;
    private ArrayList<CategoryList> mCategoryList;
    private ArrayList<ProductList> mProductList;
    private ArrayList<Province> mProvinceList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization_detail);
        ButterKnife.bind(this);

        showLoadingProgress();

        initVariate();

        initView();

        initData();
    }

    private void initVariate() {
        mAuthzApplyId = getIntent().getIntExtra("authzApplyId", -1);
        LogUtil.d("authzApplyId is " + mAuthzApplyId);
        mParams = new HashMap<>();
        if (mAuthzApplyId != -1) {
            mParams.put(AUTHZ_APPLY_ID, mAuthzApplyId + "");
        }
    }

    private void initView() {
        mTitle.setOnTitleBarClick(this);
        mOptionsPickerView = new OptionsPickerView.Builder(this, this)
                .setContentTextSize(16)
                .setSubCalSize(14)
                .setSubmitColor(getResources().getColor(R.color.colorMain))
                .setCancelColor(getResources().getColor(R.color.colorMain))
                .setOutSideCancelable(false)
                .build();
    }

    private void initData() {
        OkHttpUtils.post().url(URL_APLLY).addHeader(ACCESS_TOKEN, accessToken)
                .params(mParams).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                hideLoadingProgress();
                e.printStackTrace();
                LogUtil.d("data error");
                Util.showTimeOutNotice(currentContext);
                mBizSelect = new ArrayList<>();
                if (mBizSelect.isEmpty()) {
                    mBizSelect.add(new VendorList(0, null, ""));
                }
            }

            @Override
            public void onResponse(String response, int id) {
                hideLoadingProgress();
                LogUtil.d("response is \n" + response);
                UploadBean uploadBean = JSON.parseObject(response, UploadBean.class);
                switch (uploadBean.getStatus()) {
                    case "success":
                        mBizSelect = uploadBean.getBizSelect();
                        if (mBizSelect.isEmpty()) {
                            mBizSelect.add(new VendorList(0, null, ""));
                        }
                        // 如果有数据，则回显数据
                        if (uploadBean.getAuthzApply() != null) {
                            echoData(uploadBean.getAuthzApply());
                        }
                        break;
                    case "failure":
                        mBizSelect = new ArrayList<>();
                        if (mBizSelect.isEmpty()) {
                            mBizSelect.add(new VendorList(0, null, ""));
                        }
                        Util.showError(currentContext, uploadBean.getReason());
                        break;
                }
            }
        });
    }

    /**
     * 数据回显
     *
     * @param authzApply
     */
    private void echoData(AuthorizationApply authzApply) {
        String applyStatus = authzApply.getApplyStatus();
        LogUtil.d("apply status is " + applyStatus);
        if ("passed".equals(applyStatus)) {
            // 审核通过，禁止修改和提交
            forbidButton(authzApply.getApplyStatusName());
        }
        mTvVendorName.setText(authzApply.getVendorName());
        mVendorId = authzApply.getVendorId();
        mTvCategory.setText(authzApply.getCategoryName());
        mCategoryId = authzApply.getCategoryId();
        mTvProduct.setText(authzApply.getProductName());
        mProductId = authzApply.getProductId();
        mTvSpec.setText(authzApply.getSpec());
        mSpecId = authzApply.getSpecId();
        mTvDosageForm.setText(authzApply.getDosageForm());
        mDosageFormId = authzApply.getDosageFormId();
        mTvProvince.setText(authzApply.getProvinceName());
        mProvinceId = authzApply.getProvinceId();
        mEvRegionRemark.setText(authzApply.getRegionRemark());
        mRegionRemark = authzApply.getRegionRemark();
        mEvQualificationNum.setText(authzApply.getQualificationNum());
        mQualificationNum = authzApply.getQualificationNum();
        mEvContractNum.setText(authzApply.getContractNum());
        mContractNum = authzApply.getContractNum();
        mEvAgreementNum.setText(authzApply.getAgreementNum());
        mAgreementNum = authzApply.getAgreementNum();
        mEvAuthzNum.setText(authzApply.getAuthzNum());
        mAuthzNum = authzApply.getAuthzNum();
        mEvAuthzCopyNum.setText(authzApply.getAuthzCopyNum());
        mAuthzCopyNum = authzApply.getAuthzCopyNum();
        mEvProductDescNum.setText(authzApply.getProductDescNum());
        mProductDescNum = authzApply.getProductDescNum();
        mEvBizLicenseCopyNum.setText(authzApply.getBizLicenseCopyNum());
        mBizLicenseCopyNum = authzApply.getBizLicenseCopyNum();
        mEvRemark.setText(authzApply.getRemark());
        mRemark = authzApply.getRemark();
        mTvCnName.setText(authzApply.getCnName());
        mCnName = authzApply.getCnName();
        mTvPhoneNumber.setText(authzApply.getPhoneNumber());
        mPhoneNumber = authzApply.getPhoneNumber();
        mTvFullAddress.setText(authzApply.getFullAddress());
        mFullAddress = authzApply.getFullAddress();

        // 遍历数据，设置数据源
        for (VendorList vendorList : mBizSelect) {
            if (vendorList.getVendorId() == authzApply.getVendorId()) {
                mCategoryList = vendorList.getCategoryList();
                for (CategoryList categoryList : mCategoryList) {
                    if (categoryList.getCategoryId() == authzApply.getCategoryId()) {
                        mProductList = categoryList.getProductList();
                        for (ProductList productList : mProductList) {
                            if (productList.getProductId() == authzApply.getProductId()) {
                                mProvinceList = productList.getProvinceList();
                                break;
                            }
                        }
                        break;
                    }
                }
                break;
            }
        }
    }



    /**
     * 禁止按钮点击
     * @param applyStatusName
     */
    private void forbidButton(String applyStatusName) {
        ToastUtil.showShort(this, "审核通过不可编辑");
        mTvVendorName.setEnabled(false);
        mTvCategory.setEnabled(false);
        mTvProduct.setEnabled(false);
        mTvSpec.setEnabled(false);
        mTvDosageForm.setEnabled(false);
        mTvProvince.setEnabled(false);
        mEvRegionRemark.setEnabled(false);
        mEvAgreementNum.setEnabled(false);
        mEvAuthzNum.setEnabled(false);
        mEvContractNum.setEnabled(false);
        mEvQualificationNum.setEnabled(false);
        mEvAuthzCopyNum.setEnabled(false);
        mEvProductDescNum.setEnabled(false);
        mEvBizLicenseCopyNum.setEnabled(false);
        mEvRemark.setEnabled(false);
        mTvSelectAddress.setEnabled(false);
        mBtnSubmit.setEnabled(false);
        mBtnSubmit.setText(applyStatusName);
        mBtnSubmit.setBackgroundResource(R.drawable.shape_button_forbid);
        mBtnSubmit.setTextColor(Color.GRAY);
    }

    @OnClick({R.id.authorization_detail_record,
            R.id.authorization_detail_vendor_name, R.id.authorization_detail_category,
            R.id.authorization_detail_product, R.id.authorization_detail_spec,
            R.id.authorization_detail_dosage_form, R.id.authorization_detail_province,
            R.id.authorization_detail_select_address, R.id.authorization_detail_submit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.authorization_detail_record:
                // 进入申请记录
                enterAuthorization();
                break;
            case R.id.authorization_detail_vendor_name:
                mOptionsPickerView.setPicker(mBizSelect);
                mOptionsPickerView.show(view);
                break;
            case R.id.authorization_detail_category:
                if (mTvVendorName.getText().toString().isEmpty()) {
                    ToastUtil.showShort(this, "请选择疫苗生产厂家");
                    return;
                }
                mOptionsPickerView.setPicker(mCategoryList);
                mOptionsPickerView.show(view);
                break;
            case R.id.authorization_detail_product:
                if (mTvCategory.getText().toString().isEmpty()) {
                    ToastUtil.showShort(this, "请选择疫苗种类");
                    return;
                }
                mOptionsPickerView.setPicker(mProductList);
                mOptionsPickerView.show(view);
                break;
            case R.id.authorization_detail_spec:
            case R.id.authorization_detail_dosage_form:
                if (mTvProduct.getText().toString().isEmpty()) {
                    ToastUtil.showShort(this, "请选择疫苗产品名称");
                    return;
                }
                break;
            case R.id.authorization_detail_province:
                if (mTvProduct.getText().toString().isEmpty()) {
                    ToastUtil.showShort(this, "请选择疫苗产品名称");
                    return;
                }
                mOptionsPickerView.setPicker(mProvinceList);
                mOptionsPickerView.show(view);
                break;
            case R.id.authorization_detail_select_address:
                // 进入选择地址列表
                Intent intent = new Intent(this, PersonalAddressActivity.class);
                intent.putExtra("from", "authorization");
                startActivityForResult(intent, REQUEST_ADDRESS);
                break;
            case R.id.authorization_detail_submit:
                // 校验数据并提交
                verifyData();
                break;
        }
    }

    @Override
    public void onOptionsSelect(int options1, int options2, int options3, View v) {
        switch (v.getId()) {
            case R.id.authorization_detail_vendor_name:
                selectVendor(options1);
                break;
            case R.id.authorization_detail_category:
                selectCategory(options1);
                break;
            case R.id.authorization_detail_product:
                selectProduct(options1);
                break;
            case R.id.authorization_detail_province:
                selectProvince(options1);
                break;
        }
    }

    /**
     * 选择生产厂家
     *
     * @param options1
     */
    private void selectVendor(int options1) {
        VendorList temp = mBizSelect.get(options1);
        mVendorId = temp.getVendorId();
        mTvVendorName.setText(temp.getVendorName());
        mTvCategory.setText("");
        mTvProduct.setText("");
        mTvSpec.setText("");
        mTvDosageForm.setText("");
        mTvProvince.setText("");
        mCategoryList = temp.getCategoryList();
    }

    /**
     * 选择疫苗种类
     *
     * @param options1
     */
    private void selectCategory(int options1) {
        CategoryList temp = mCategoryList.get(options1);
        mCategoryId = temp.getCategoryId();
        mTvCategory.setText(temp.getCategoryName());
        mTvProduct.setText("");
        mTvSpec.setText("");
        mTvDosageForm.setText("");
        mTvProvince.setText("");
        mProductList = temp.getProductList();
    }

    /**
     * 选择疫苗名称
     *
     * @param options1
     */
    private void selectProduct(int options1) {
        ProductList temp = mProductList.get(options1);
        mProvinceList = temp.getProvinceList();
        mProductId = temp.getProductId();
        mTvProduct.setText(temp.getProductName());
        mSpecId = temp.getSpecId();
        mTvSpec.setText(temp.getSpec());
        mDosageFormId = temp.getDosageFormId();
        mTvDosageForm.setText(temp.getDosageForm());
        mTvProvince.setText("");
    }

    /**
     * 选择省
     *
     * @param options1
     */
    private void selectProvince(int options1) {
        Province temp = mProvinceList.get(options1);
        mProvinceId = temp.getProvinceId();
        mTvProvince.setText(temp.getProvinceName());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ADDRESS:
                if (resultCode == FROM_ITEM_OK) {
                    // 获取收获地址
                    if (data != null) {
                        Address address = data.getParcelableExtra("address");
                        mCnName = address.getCnName();
                        mTvCnName.setText(mCnName);
                        mPhoneNumber = address.getPhoneNumber();
                        mTvPhoneNumber.setText(mPhoneNumber);
                        mFullAddress = //address.getProvinceName() + "\t" +
                                //address.getCityName() + "\t" +
                                //address.getAreaName() + "\t" +
                                address.getFullAddress();
                        mTvFullAddress.setText(mFullAddress);
                    } else {
                        LogUtil.d("data is null");
                    }
                } else {
                    LogUtil.d("Unknown result code：" + resultCode);
                }
                break;
            default:
                LogUtil.d("Unknown request code：" + requestCode);
                break;
        }
    }

    /**
     * 校验数据
     */
    private void verifyData() {
        if (mTvVendorName.getText().toString().isEmpty()) {
            ToastUtil.showShort(this, "请选择疫苗生产厂家");
            return;
        }
        if (mTvCategory.getText().toString().isEmpty()) {
            ToastUtil.showShort(this, "请选择疫苗种类");
            return;
        }
        if (mTvProduct.getText().toString().isEmpty()) {
            ToastUtil.showShort(this, "请选择疫苗产品名称");
            return;
        }
        if (mTvSpec.getText().toString().isEmpty()) {
            ToastUtil.showShort(this, "请选择疫苗规格");
            return;
        }
        if (mTvDosageForm.getText().toString().isEmpty()) {
            ToastUtil.showShort(this, "请选择疫苗剂型");
            return;
        }
        if (mTvProvince.getText().toString().isEmpty()) {
            ToastUtil.showShort(this, "请选择已签约省份");
            return;
        }
        mRegionRemark = mEvRegionRemark.getText().toString().trim();
        if (mRegionRemark.isEmpty()) {
            ToastUtil.showShort(this, "请填写详细市/区（县）名称");
            return;
        }
        mQualificationNum = mEvQualificationNum.getText().toString().trim();
        mContractNum = mEvContractNum.getText().toString().trim();
        mAgreementNum = mEvAgreementNum.getText().toString().trim();
        mAuthzCopyNum = mEvAuthzCopyNum.getText().toString().trim();
        mAuthzNum = mEvAuthzNum.getText().toString().trim();
        mProductDescNum = mEvProductDescNum.getText().toString().trim();
        mBizLicenseCopyNum = mEvBizLicenseCopyNum.getText().toString().trim();
        mRemark = mEvRemark.getText().toString().trim();
        if (mTvCnName.getText().toString().isEmpty()
                || mTvPhoneNumber.getText().toString().isEmpty()
                || mTvFullAddress.getText().toString().isEmpty()) {
            ToastUtil.showShort(this, "请选择收货地址");
            return;
        }
        // 显示确认弹窗
        confirmSubmit();
    }

    /**
     * 显示确认弹窗
     */
    private void confirmSubmit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.dialog);
        View view = View.inflate(this, R.layout.dialog_confirm_submit, null);
        TextView content = (TextView) view.findViewById(R.id.dialog_fillet);
        content.setText(getString(R.string.dialog_confirm_submit_authorization));
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
                submitData();
            }
        });
        dialog.show();
    }

    /**
     * 提交数据
     */
    private void submitData() {
        final ProgressDialog progressDialog = ProgressDialogUtil.getLoadingProgress(this, "提交中");
        progressDialog.show();
        OkHttpUtils.post().url(URL_SUBMIT).addHeader(ACCESS_TOKEN, accessToken)
                .params(mParams)
                .addParams(VENDOR_ID, mVendorId + "")
                .addParams(CATEGORY_ID, mCategoryId + "")
                .addParams(PRODUCT_ID, mProductId + "")
                .addParams(SPEC_ID, mSpecId + "")
                .addParams(DOSAGE_FORM_ID, mDosageFormId + "")
                .addParams(PROVINCE_ID, mProvinceId + "")
                .addParams(REGION_REMARK, mRegionRemark)
                .addParams(QUALIFICATION_NUM, mQualificationNum)        // 非必填
                .addParams(CONTRACT_NUM, mContractNum)                  // 非必填
                .addParams(AGREEMENT_NUM, mAgreementNum)                // 非必填
                .addParams(AUTHZ_NUM, mAuthzNum)                        // 非必填
                .addParams(AUTHZ_COPY_NUM, mAuthzCopyNum)               // 非必填
                .addParams(PRODUCT_DESC_NUM, mProductDescNum)           // 非必填
                .addParams(BIZ_LICENSE_COPY_NUM, mBizLicenseCopyNum)    // 非必填
                .addParams(REMARK, mRemark)                             // 非必填
                .addParams(CN_NAME, mCnName)
                .addParams(PHONE_NUMBER, mPhoneNumber)
                .addParams(FULL_ADDRESS, mFullAddress)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                e.printStackTrace();
                LogUtil.d("submit error");
                Util.showTimeOutNotice(currentContext);
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.d("response is\n" + response);
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        ToastUtil.showShort(currentContext, getString(R.string.upload_success));
                        enterAuthorization();
                        break;
                    case "failure":
                        Util.showError(currentContext, errorBean.getReason());
                        break;
                }
            }
        });
    }

    /**
     * 进入申请记录页面
     */
    private void enterAuthorization() {
        Intent intent = new Intent(currentContext, AuthorizationActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void leftOnClick() {
        finish();
    }

    @Override
    public void rightOnClick() {

    }
}
