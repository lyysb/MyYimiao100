package com.yimiao100.sale.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.yimiao100.sale.R;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.bean.CorporateBean;
import com.yimiao100.sale.bean.PersonalBean;
import com.yimiao100.sale.bean.ResourceListBean;
import com.yimiao100.sale.utils.CheckUtil;
import com.yimiao100.sale.utils.FormatUtils;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.TimeUtil;
import com.yimiao100.sale.view.TitleView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * 资源-推广
 */
public class ResourcesPromotionActivity extends BaseActivity implements TitleView
        .TitleBarOnClickListener, View.OnClickListener, CheckUtil.CorporatePassedListener, CheckUtil.PersonalPassedListener {


    @BindView(R.id.resources_promotion_title)
    TitleView mResourcesPromotionTitle;
    @BindView(R.id.resources_promotion_submit)
    ImageButton mResourcesPromotionSubmit;
    @BindView(R.id.resources_promotion_body)
    TextView mResourcesPromotionBody;
    @BindView(R.id.promotion_subtract)
    Button mPromotionSubtract;
    @BindView(R.id.promotion_num)
    TextView mPromotionNum;
    @BindView(R.id.promotion_add)
    Button mPromotionAdd;
    @BindView(R.id.resource_promotion_vendor_name)
    TextView mResourcePromotionVendorName;
    @BindView(R.id.resource_promotion_product_formal_name)
    TextView mResourcePromotionProductFormalName;
    @BindView(R.id.resource_promotion_product_common_name)
    TextView mResourcePromotionProductCommonName;
    @BindView(R.id.resource_promotion_region)
    TextView mResourcePromotionRegion;
    @BindView(R.id.resource_promotion_time)
    TextView mResourcePromotionTime;
    @BindView(R.id.resource_promotion_quota)
    TextView mResourcePromotionQuota;
    @BindView(R.id.resource_promotion_total_deposit)
    TextView mResourcePromotionTotalDeposit;
    @BindView(R.id.resource_promotion_total_count)
    TextView mResourcePromotionTotalCount;
    @BindView(R.id.resource_promotion_total_amount)
    TextView mResourcePromotionTotalAmount;
    @BindView(R.id.resource_promotion_item1)
    TextView mPromotionItem1;
    @BindView(R.id.resource_promotion_item2)
    TextView mPromotionItem2;
    @BindView(R.id.resource_promotion_item3)
    TextView mPromotionItem3;
    @BindView(R.id.resources_promotion_option1)
    TextView mPromotionOption1;
    @BindView(R.id.resources_promotion_option2)
    TextView mPromotionOption2;
    @BindView(R.id.resources_promotion_option3)
    TextView mPromotionOption3;
    private int mNum;
    private AlertDialog mDialog;

    private int mChange;
    private int mQuota;
    private ResourceListBean mResourceInfo;
    private double mSaleDeposit;

    private static String  mUserAccountType;
    private static final String PERSONAL = "personal";
    private static final String CORPORATE = "corporate";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resources_promotion);
        ButterKnife.bind(this);

        initView();

        initData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //校验账户信息
        switch (mUserAccountType) {
            case PERSONAL:
                CheckUtil.checkPersonal(this, this);
                break;
            case CORPORATE:
                CheckUtil.checkCorporate(this, this);
                break;
        }
    }
    /**
     * 校验个人完毕
     * 处理个人账户信息数据
     *
     * @param personal
     */
    @Override
    public void handlePersonal(PersonalBean personal) {
        // 推广人姓名
        String cnName = personal.getCnName();
        mPromotionOption1.setText(cnName);
        // 推广人身份证号
        String idNumber = personal.getIdNumber();
        mPromotionOption2.setText(idNumber);
        // 推广人联系电话
        String personalPhoneNumber = personal.getPersonalPhoneNumber();
        mPromotionOption3.setText(personalPhoneNumber);
    }

    /**
     * 校验对公完毕
     * 处理对公账户信息数据
     *
     * @param corporate
     */
    @Override
    public void handleCorporate(CorporateBean corporate) {
        //公司名称
        String bankAccountName = corporate.getAccountName();
        mPromotionOption1.setText(bankAccountName);
        //公司账号
        String bankAccountNumber = corporate.getCorporateAccount();
        mPromotionOption2.setText(bankAccountNumber);
        //推广人
        String cnName = corporate.getCnName();
        mPromotionOption3.setText(cnName);
    }

    private void initView() {
        mResourcesPromotionTitle.setOnTitleBarClick(this);
    }

    private void initData() {
        Intent intent = getIntent();
        //资源
        mResourceInfo = intent.getParcelableExtra("resourceInfo");
        if (mResourceInfo != null) {
            LogUtil.Companion.d("resourceId is " + mResourceInfo.getId());
        }
        mUserAccountType = intent.getStringExtra("userAccountType");
        switch (mUserAccountType) {
            case PERSONAL:
                LogUtil.Companion.d("personal promotion");
                initPersonalData();
                break;
            case CORPORATE:
                LogUtil.Companion.d("corporate promotion");
                initCorporateData();
                break;
            default:
                LogUtil.Companion.d("Unknown Error");
                break;
        }
        mResourcesPromotionBody.setText("推广主体详情");
        mResourcesPromotionBody.setCompoundDrawablesWithIntrinsicBounds(
                getResources().getDrawable(R.mipmap.ico_company_promotion_details), null, null,
                null);
        //厂家名称
        String vendorName = mResourceInfo.getVendorName();
        mResourcePromotionVendorName.setText(vendorName);
        //产品名-分类名
        String categoryName = mResourceInfo.getCategoryName();
        mResourcePromotionProductFormalName.setText(categoryName);
        //通用名-产品名
        String productName = mResourceInfo.getProductName();
        mResourcePromotionProductCommonName.setText(productName);
        //推广区域
        String region = mResourceInfo.getProvinceName() + "\t" + mResourceInfo.getCityName() +
                "\t" + mResourceInfo.getAreaName();
        mResourcePromotionRegion.setText(region);
        //完成周期
        long startTime = mResourceInfo.getStartAt();
        long endTime = mResourceInfo.getEndAt();
        String start = TimeUtil.timeStamp2Date(startTime + "", "yyyy年MM月dd日");
        String end = TimeUtil.timeStamp2Date(endTime + "", "yyyy年MM月dd日");
        mResourcePromotionTime.setText(start + "—" + end);
        //基础指标&&单位
        mQuota = mResourceInfo.getQuota();
        mResourcePromotionQuota.setText(mQuota + mResourceInfo.getUnits());
        //推广保证金基数
        mSaleDeposit = mResourceInfo.getSaleDeposit();
        mResourcePromotionTotalDeposit.setText(FormatUtils.MoneyFormat(mSaleDeposit) + "（人民币）");
        //竞标增量
        int increment = mResourceInfo.getIncrement();
        //增加竞标数量
        mPromotionNum.setText("0");
        //最终竞标数量
        mResourcePromotionTotalCount.setText(mQuota + mResourceInfo.getUnits());
        //最终提交的保证金--(基础指标+竞标数量)*推广保证金基数
        mResourcePromotionTotalAmount.setText(FormatUtils.MoneyFormat(mQuota * mSaleDeposit) +
                "(人民币)");

        mChange = mQuota * increment / 100;
    }

    private void initPersonalData() {
        mResourcesPromotionTitle.setTitle("个人推广");
        mPromotionItem1.setText(getString(R.string.resources_promotion_personal_item1));
        mPromotionItem2.setText(getString(R.string.resources_promotion_personal_item2));
        mPromotionItem3.setText(getString(R.string.resources_promotion_personal_item3));
        mPromotionOption1.setHint(getString(R.string.resources_promotion_personal_option1));
        mPromotionOption2.setHint(getString(R.string.resources_promotion_personal_option2));
        mPromotionOption3.setHint(getString(R.string.resources_promotion_personal_option3));
    }


    private void initCorporateData() {
        mResourcesPromotionTitle.setTitle("公司推广");
        mPromotionItem1.setText(getString(R.string.resources_promotion_corporate_item1));
        mPromotionItem2.setText(getString(R.string.resources_promotion_corporate_item2));
        mPromotionItem3.setText(getString(R.string.resources_promotion_corporate_item3));
        mPromotionOption1.setHint(getString(R.string.resources_promotion_corporate_option1));
        mPromotionOption2.setHint(getString(R.string.resources_promotion_corporate_option2));
        mPromotionOption3.setHint(getString(R.string.resources_promotion_corporate_option3));
    }

    @Override
    public void leftOnClick() {
        finish();
    }

    @Override
    public void rightOnClick() {

    }

    @OnClick({R.id.promotion_subtract, R.id.promotion_add, R.id.resources_promotion_submit, R.id
            .resource_promotion_bind})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.resource_promotion_bind:
                //进入到推广主体界面
                if (TextUtils.equals(mUserAccountType, PERSONAL)) {
                    startActivity(new Intent(this, BindPersonalActivity.class));
                } else if (TextUtils.equals(mUserAccountType, CORPORATE)) {
                    startActivity(new Intent(this, BindCompanyActivity.class));
                }
                break;
            case R.id.promotion_subtract:
                //指标数减少
                mNum = Integer.parseInt(mPromotionNum.getText().toString());
                if (mNum >= mChange) {
                    mNum = mNum - mChange;
                }
                //增加竞标数量
                mPromotionNum.setText(mNum + "");
                //最终竞标数量
                mResourcePromotionTotalCount.setText((mQuota + mNum) + mResourceInfo.getUnits());
                //最终提交的保证金--（ 基础指标+竞标数量）*推广保证金基数
                mResourcePromotionTotalAmount.setText(FormatUtils.MoneyFormat((mQuota + mNum) *
                        mSaleDeposit) + "(人民币)");
                break;
            case R.id.promotion_add:
                //指标数增加
                mNum = Integer.parseInt(mPromotionNum.getText().toString());
                mNum = mNum + mChange;
                //增加竞标数量
                mPromotionNum.setText(mNum + "");
                //最终竞标数量
                mResourcePromotionTotalCount.setText((mQuota + mNum) + mResourceInfo.getUnits());
                //最终提交的保证金--（ 基础指标+竞标数量）*推广保证金基数-只是作为显示
                mResourcePromotionTotalAmount.setText(FormatUtils.MoneyFormat((mQuota + mNum) *
                        mSaleDeposit) + "(人民币)");
                break;
            case R.id.resources_promotion_submit:
                //提交
                showSubmitDialog();
                break;
            case R.id.dialog_submit:
                //点击Dialog确定-进入提交推广保证金
                Intent intent = new Intent(this, SubmitPromotionActivity.class);
                intent.putExtra("resourceInfo", mResourceInfo);                 //资源
                intent.putExtra("bidQty", mPromotionNum.getText().toString());  //竞标数量
                intent.putExtra("mark", "resource");     //标识来源界面
                intent.putExtra("userAccountType", mUserAccountType);
                startActivity(intent);
                mDialog.dismiss();
                break;
        }
    }


    /**
     * 显示推广确认Dialog
     */
    private void showSubmitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.dialog);
        View view = View.inflate(this, R.layout.dialog_submit_promotion, null);
        view.findViewById(R.id.dialog_submit).setOnClickListener(this);
        builder.setView(view);
        mDialog = builder.create();
        mDialog.show();
    }


}
