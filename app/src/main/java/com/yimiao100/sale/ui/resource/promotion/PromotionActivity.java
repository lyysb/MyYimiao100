package com.yimiao100.sale.ui.resource.promotion;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.yimiao100.sale.R;
import com.yimiao100.sale.activity.BindCompanyActivity;
import com.yimiao100.sale.activity.BindPersonalActivity;
import com.yimiao100.sale.bean.Bid;
import com.yimiao100.sale.bean.BizData;
import com.yimiao100.sale.bean.CorporateBean;
import com.yimiao100.sale.bean.PersonalBean;
import com.yimiao100.sale.bean.ResourceListBean;
import com.yimiao100.sale.ext.JSON;
import com.yimiao100.sale.mvpbase.BaseActivity;
import com.yimiao100.sale.ui.pay.PayActivity;
import com.yimiao100.sale.utils.CheckUtil;
import com.yimiao100.sale.utils.FormatUtils;
import com.yimiao100.sale.utils.TimeUtil;
import com.yimiao100.sale.view.TitleView;

import java.math.BigDecimal;
import java.util.ArrayList;


/**
 * 疫苗推广界面
 */
public class PromotionActivity extends BaseActivity {

    private String userAccountType;
    private SparseArray<ResourceListBean> resources;    // 由上页数据传递而来，进行数据统计（可以获得实际支付金额）
    private SparseIntArray promotions;    // key-记录资源id，value-记录竞标数量，变换数据传递到下一页
    private SparseArray<BottomNote> bottomNotes;    // 只是用来记录和更新底部数据显示，不做数据传递
    private final String PERSONAL = "personal";
    private final String CORPORATE = "corporate";
    private TitleView titleView;
    private TextView tvPromotionItem1;
    private TextView tvPromotionItem2;
    private TextView tvPromotionItem3;
    private TextView tvPromotionOption1;
    private TextView tvPromotionOption2;
    private TextView tvPromotionOption3;
    private TextView finalCount;
    private TextView finalAmount;
    private AlertDialog dialog;

    public static void start(Context context, String userAccountType, SparseArray<ResourceListBean> resources) {
        Intent intent = new Intent(context, PromotionActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("userAccountType", userAccountType);
        bundle.putSparseParcelableArray("resources", resources);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    protected void init() {
        super.init();

        initVariate();

        initView();
    }

    private void initVariate() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            userAccountType = bundle.getString("userAccountType");
            resources = bundle.getSparseParcelableArray("resources");
            promotions = new SparseIntArray();
            bottomNotes = new SparseArray<>();
        }
    }

    private void initView() {
        // TitleView
        titleView = (TitleView) findViewById(R.id.promotion_title);
        titleView.setOnTitleBarClick(new TitleView.TitleBarOnClickListener() {
            @Override
            public void leftOnClick() {
                finish();
            }

            @Override
            public void rightOnClick() {

            }
        });
        // 推广主体详情
        findViewById(R.id.promotion_bind).setOnClickListener(v -> navigateToBindPromotion());
        tvPromotionItem1 = (TextView) findViewById(R.id.promotion_item1);
        tvPromotionItem2 = (TextView) findViewById(R.id.promotion_item2);
        tvPromotionItem3 = (TextView) findViewById(R.id.promotion_item3);
        tvPromotionOption1 = (TextView) findViewById(R.id.promotion_option1);
        tvPromotionOption2 = (TextView) findViewById(R.id.promotion_option2);
        tvPromotionOption3 = (TextView) findViewById(R.id.promotion_option3);
        // 资源详情
        ResourceListBean resourceDetail = resources.valueAt(0);
        TextView tvVendorName = (TextView) findViewById(R.id.promotion_vendor_name);
        TextView tvCategoryName = (TextView) findViewById(R.id.promotion_category_name);
        TextView tvProductName = (TextView) findViewById(R.id.promotion_product_name);
        tvVendorName.setText(resourceDetail.getVendorName());
        tvCategoryName.setText(resourceDetail.getCategoryName());
        tvProductName.setText(resourceDetail.getProductName());
        // Layout
        LinearLayout layout = (LinearLayout) findViewById(R.id.promotion_item);
        int tempCount = 0;
        double tempAmount = 0.0;
        for (int i = resources.size() - 1; i >= 0; i--) {
            View itemView = LayoutInflater.from(this).inflate(R.layout.item_res_pro, null);
            ResourceListBean resource = resources.valueAt(i);
            layout.addView(itemView);
            initItem(itemView, resource);

            tempCount += resource.getQuota();
            tempAmount += resource.getQuota() * resource.getSaleDeposit();
        }
        // 竞标统计
        finalCount = (TextView) findViewById(R.id.promotion_final_count);
        finalAmount = (TextView) findViewById(R.id.promotion_final_amount);
        finalCount.setText(tempCount + resourceDetail.getUnits());
        finalAmount.setText(FormatUtils.MoneyFormat(tempAmount) + "(人民币)");
        // 进入支付
        findViewById(R.id.promotion_submit).setOnClickListener(v -> showConfirmDialog());
    }

    /**
     * 是否进入支付
     */
    private void showConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.dialog);
        View view = View.inflate(this, R.layout.dialog_submit_promotion, null);
        view.findViewById(R.id.dialog_submit).setOnClickListener(v -> navigateToPay());
        builder.setView(view);
        dialog = builder.create();
        dialog.show();
    }

    /**
     * 确定进入支付界面
     */
    private void navigateToPay() {
        dialog.dismiss();
        // 统计实际支付金额
        BigDecimal bigDecimal = new BigDecimal("0.0");
        for (int i = 0; i < resources.size(); i++) {
            ResourceListBean item = resources.valueAt(i);
            double bidDeposit = item.getBidDeposit();
            BigDecimal decimal = new BigDecimal(bidDeposit + "");
            bigDecimal = bigDecimal.add(decimal);
        }
        double payAmount = bigDecimal.doubleValue();

        // 统计竞标id和数量
        ArrayList<Bid> bidList = new ArrayList<>();
        for (int i = 0; i < promotions.size(); i++) {
            int resourceId = promotions.keyAt(i);
            int bidQty = promotions.valueAt(i);
            bidList.add(new Bid(String.valueOf(resourceId), String.valueOf(bidQty)));
        }
        BizData bizData = new BizData(userAccountType, bidList);
        String bizDataJson = JSON.toJSONString(bizData);

        // 进入支付页面
        PayActivity.startFromVaccineRes(this, "vaccine_res", payAmount, bizDataJson);
    }


    /**
     * 跳转到绑定推广主体
     */
    private void navigateToBindPromotion() {
        switch (userAccountType) {
            case PERSONAL:
                startActivity(new Intent(this, BindPersonalActivity.class));
                break;
            case CORPORATE:
                startActivity(new Intent(this, BindCompanyActivity.class));
                break;
        }
    }

    /**
     * 初始化多条目布局内容
     */
    private void initItem(View itemView, ResourceListBean resource) {
        // 初始化竞标数量记录
        promotions.put(resource.getId(), 0);
        // 初始化底部记录
        BottomNote note = new BottomNote(resource.getQuota(), resource.getSaleDeposit());
        bottomNotes.put(resource.getId(), note);
        // 初始化View
        TextView tvRegion = (TextView) itemView.findViewById(R.id.promotion_region);
        TextView tvCustomer = (TextView) itemView.findViewById(R.id.promotion_customer);
        TextView tvTime = (TextView) itemView.findViewById(R.id.promotion_time);
        TextView tvQuota = (TextView) itemView.findViewById(R.id.promotion_quota);
        TextView tvDeposit = (TextView) itemView.findViewById(R.id.promotion_total_deposit);
        // 推广区域
        tvRegion.setText(resource.getProvinceName() + "\t\t" +
                resource.getCityName() + "\t\t" +
                resource.getAreaName()
        );
        // 客户
        tvCustomer.setText(resource.getCustomerName());
        // 推广周期
        long startTime = resource.getStartAt();
        long endTime = resource.getEndAt();
        String start = TimeUtil.timeStamp2Date(startTime + "", "yyyy年MM月dd日");
        String end = TimeUtil.timeStamp2Date(endTime + "", "yyyy年MM月dd日");
        tvTime.setText(start + "—" + end);
        // 基础服务量
        int quota = resource.getQuota();
        String units = resource.getUnits();
        tvQuota.setText(quota + units);
        // 推广保证金基数
        double saleDeposit = resource.getSaleDeposit();
        tvDeposit.setText(FormatUtils.MoneyFormat(saleDeposit) + "(人民币)");
        // 竞标数量
        TextView itemCount = (TextView) itemView.findViewById(R.id.promotion_count);
        itemCount.setText("0");
        // 最终竞标数量
        TextView itemTotalCount = (TextView) itemView.findViewById(R.id.promotion_total_count);
        itemTotalCount.setText(quota + units);
        // 推广保证金=(基础服务量+竞标数量)*推广保证金基数
        TextView itemTotalAmount = (TextView) itemView.findViewById(R.id.promotion_total_amount);
        String format = FormatUtils.MoneyFormat(quota * saleDeposit);
        itemTotalAmount.setText(format + "(人民币)");
        // 竞标增量
        int increment = resource.getIncrement();
        // 每次改变量
        int change = quota * increment /100;
        // -
        itemView.findViewById(R.id.promotion_subtract).setOnClickListener(v -> {
            // 减少
            Integer currentCount = Integer.parseInt(itemCount.getText().toString());
            if (currentCount < change) {
                return;
            }
            currentCount -= change;
            // 更新数据显示
            updateViewData(resource, quota, units, saleDeposit, itemCount, itemTotalCount, itemTotalAmount, currentCount);
        });
        // +
        itemView.findViewById(R.id.promotion_add).setOnClickListener(v -> {
            // 增加
            Integer currentCount = Integer.parseInt(itemCount.getText().toString());
            currentCount += change;
            // 更新数据显示
            updateViewData(resource, quota, units, saleDeposit, itemCount, itemTotalCount, itemTotalAmount, currentCount);
        });

    }

    /**
     * 更新底部数据显示
     */
    private void updateViewData(ResourceListBean resource, int quota, String units, double saleDeposit, TextView itemCount, TextView itemTotalCount, TextView itemTotalAmount, Integer currentCount) {
        itemCount.setText(currentCount + "");
        itemTotalCount.setText((quota + currentCount) + units);
        String formatVal = FormatUtils.MoneyFormat((quota + currentCount) * saleDeposit);
        itemTotalAmount.setText(formatVal + "(人民币)");
        // 更新数据记录
        promotions.put(resource.getId(), currentCount);
        // 更新底部显示
        BottomNote currentNote = bottomNotes.get(resource.getId());
        currentNote.setCurrentCount(currentCount);
        bottomNotes.put(resource.getId(), currentNote);
        int tempCount = 0;
        double tempAmount = 0.0;
        for (int i = 0; i < bottomNotes.size(); i++) {
            BottomNote tempNote = bottomNotes.valueAt(i);
            tempCount += tempNote.getBaseCount() + tempNote.getCurrentCount();
            tempAmount += (tempNote.getBaseCount() + tempNote.getCurrentCount()) * tempNote.getBaseAmount();
        }
        finalCount.setText(tempCount + resource.getUnits());
        finalAmount.setText(FormatUtils.MoneyFormat(tempAmount) + "(人民币)");
    }

    @Override
    protected void onResume() {
        super.onResume();
        switch (userAccountType) {
            case PERSONAL:
                CheckUtil.checkPersonal(this, this::initPersonalData);
                break;
            case CORPORATE:
                CheckUtil.checkCorporate(this, this::initCorporateData);
                break;
        }
    }

    private void initPersonalData(PersonalBean personal) {
        LogUtils.d("personal promotion");
        titleView.setTitle("个人推广");
        tvPromotionItem1.setText(getString(R.string.resources_promotion_personal_item1));
        tvPromotionItem2.setText(getString(R.string.resources_promotion_personal_item2));
        tvPromotionItem3.setText(getString(R.string.resources_promotion_personal_item3));
        tvPromotionOption1.setHint(getString(R.string.resources_promotion_personal_option1));
        tvPromotionOption2.setHint(getString(R.string.resources_promotion_personal_option2));
        tvPromotionOption3.setHint(getString(R.string.resources_promotion_personal_option3));
        // 推广人姓名
        String cnName = personal.getCnName();
        tvPromotionOption1.setText(cnName);
        // 推广人身份证号
        String idNumber = personal.getIdNumber();
        tvPromotionOption2.setText(idNumber);
        // 推广人联系电话
        String personalPhoneNumber = personal.getPersonalPhoneNumber();
        tvPromotionOption3.setText(personalPhoneNumber);
    }

    private void initCorporateData(CorporateBean corporate) {
        LogUtils.d("corporate promotion");
        titleView.setTitle("公司推广");
        tvPromotionItem1.setText(getString(R.string.resources_promotion_corporate_item1));
        tvPromotionItem2.setText(getString(R.string.resources_promotion_corporate_item2));
        tvPromotionItem3.setText(getString(R.string.resources_promotion_corporate_item3));
        tvPromotionOption1.setHint(getString(R.string.resources_promotion_corporate_option1));
        tvPromotionOption2.setHint(getString(R.string.resources_promotion_corporate_option2));
        tvPromotionOption3.setHint(getString(R.string.resources_promotion_corporate_option3));
        //公司名称
        String bankAccountName = corporate.getAccountName();
        tvPromotionOption1.setText(bankAccountName);
        //公司账号
        String bankAccountNumber = corporate.getCorporateAccount();
        tvPromotionOption2.setText(bankAccountNumber);
        //推广人
        String cnName = corporate.getCnName();
        tvPromotionOption3.setText(cnName);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_res_promotion;
    }

    /**
     * 专门用来记录每个条目的三个关键值
     */
    class BottomNote {
        private int baseCount;
        private double baseAmount;
        private int currentCount;

        public BottomNote(int baseCount, double baseAmount) {
            this.baseCount = baseCount;
            this.baseAmount = baseAmount;
        }

        public int getBaseCount() {
            return baseCount;
        }

        public double getBaseAmount() {
            return baseAmount;
        }

        public int getCurrentCount() {
            return currentCount;
        }

        public void setCurrentCount(int currentCount) {
            this.currentCount = currentCount;
        }
    }
}
