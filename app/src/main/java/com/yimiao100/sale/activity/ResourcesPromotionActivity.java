package com.yimiao100.sale.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.yimiao100.sale.R;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.bean.CorporateBean;
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
 * 资源-公司推广
 */
public class ResourcesPromotionActivity extends BaseActivity implements TitleView
        .TitleBarOnClickListener, View.OnClickListener, CheckUtil.CorporatePassedListener {


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
    @BindView(R.id.resources_promotion_re_names)
    TextView mResourcesPromotionReNames;
    @BindView(R.id.resources_promotion_re_phone)
    TextView mResourcesPromotionRePhone;
    @BindView(R.id.resources_promotion_re_bank)
    TextView mResourcesPromotionReBank;
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
    private int mNum;
    private AlertDialog mDialog;

    private int mChange;
    private int mQuota;
    private ResourceListBean mResourceInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resources_promotion);
        ButterKnife.bind(this);

        initView();


        Intent intent = getIntent();
        //资源
        mResourceInfo = intent.getParcelableExtra("resourceInfo");
        LogUtil.d("resourceId-" + mResourceInfo.getId());

        initData();


    }

    @Override
    protected void onStart() {
        super.onStart();
        //校验账户信息
        CheckUtil.checkCorporate(this, this);
    }

    /**
     * 校验完毕
     * 处理对公账户信息数据
     *
     * @param corporate
     */
    @Override
    public void handleCorporate(CorporateBean corporate) {
        //公司账号名称
        String bankAccountName = corporate.getAccountName();
        mResourcesPromotionReNames.setText(bankAccountName);
        //公司账号
        String bankAccountNumber = corporate.getCorporateAccount();
        mResourcesPromotionReBank.setText(bankAccountNumber);
        //联系人
        String cnName = corporate.getCnName();
        mResourcesPromotionRePhone.setText(cnName);
    }

    private void initView() {
        mResourcesPromotionTitle.setOnTitleBarClick(this);
    }


    private void initData() {
        mResourcesPromotionTitle.setTitle("公司推广");
        mResourcesPromotionBody.setText("推广主体详情");
        mResourcesPromotionBody.setCompoundDrawablesWithIntrinsicBounds(
                getResources().getDrawable(R.mipmap.ico_company_promotion_details), null, null,
                null);
        mResourcesPromotionReNames.setHint("请绑定对公信息");
        mResourcesPromotionRePhone.setHint("请绑定对公信息");
        mResourcesPromotionReBank.setHint("请绑定对公信息");
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
        mResourcePromotionTotalCount.setText(mQuota + mResourceInfo.getUnits());
        mPromotionNum.setText("0");
        //保证金-总额
        double totalDeposit = mResourceInfo.getSaleDeposit();
        mResourcePromotionTotalDeposit.setText(FormatUtils.MoneyFormat(totalDeposit) + "（人民币）");
        //竞标增量
        int increment = mResourceInfo.getIncrement();

        mChange = mQuota * increment / 100;
    }

    @Override
    public void leftOnClick() {
        finish();
    }

    @Override
    public void rightOnClick() {

    }

    @OnClick({R.id.promotion_subtract, R.id.promotion_add, R.id.resources_promotion_submit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.promotion_subtract:
                //指标数减少
                mNum = Integer.parseInt(mPromotionNum.getText().toString());
                if (mNum >= mChange) {
                    mNum = mNum - mChange;
                }
                mPromotionNum.setText(mNum + "");
                mResourcePromotionTotalCount.setText((mQuota + mNum) + mResourceInfo.getUnits());
                break;
            case R.id.promotion_add:
                //指标数增加
                mNum = Integer.parseInt(mPromotionNum.getText().toString());
                mNum = mNum + mChange;
                mPromotionNum.setText(mNum + "");
                mResourcePromotionTotalCount.setText((mQuota + mNum) + mResourceInfo.getUnits());
                break;
            case R.id.resources_promotion_submit:
                //提交
                showSubmitDialog();
                break;
            case R.id.dialog_tv3:
                //点击Dialog确定-进入推广确定界面
                Intent intent = new Intent(this, ConfirmPromotionActivity.class);
                intent.putExtra("resourceInfo", mResourceInfo);                 //资源
                intent.putExtra("bidQty", mPromotionNum.getText().toString());  //竞标数量
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
        builder.setView(view);
        TextView submit = (TextView) view.findViewById(R.id.dialog_tv3);
        submit.setOnClickListener(this);
        mDialog = builder.create();
        mDialog.show();
    }


}
