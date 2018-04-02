package com.yimiao100.sale.ui.resource.detail;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.lib.WheelView;
import com.bigkoo.pickerview.listener.CustomListener;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.yimiao100.sale.R;
import com.yimiao100.sale.activity.ResourcesPromotionActivity;
import com.yimiao100.sale.activity.ShowWebImageActivity;
import com.yimiao100.sale.bean.ImageBean;
import com.yimiao100.sale.bean.ResourceListBean;
import com.yimiao100.sale.glide.ImageLoad;
import com.yimiao100.sale.mvpbase.BaseActivity;
import com.yimiao100.sale.mvpbase.CreatePresenter;
import com.yimiao100.sale.ui.resource.promotion.PromotionActivity;
import com.yimiao100.sale.utils.FormatUtils;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.TimeUtil;
import com.yimiao100.sale.view.TextViewExpandableAnimation;
import com.yimiao100.sale.view.TitleView;

import java.util.ArrayList;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

/**
 * MVP
 */
@CreatePresenter(DetailPresenter.class)
public class DetailActivity extends BaseActivity<DetailContract.View, DetailContract.Presenter> implements DetailContract.View {

    private SparseArray<ResourceListBean> resources;
    private ArrayList<Integer> keyVal;
    private JCVideoPlayerStandard jcIntroduce;
    private OptionsPickerView promotionType;
    private ArrayList promotionTypeItems;
    private TextViewExpandableAnimation tvPolicyContent;

    public static void start(Activity activity, SparseArray<ResourceListBean> resources) {
        Intent intent = new Intent(activity, DetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSparseParcelableArray("resources", resources);
        intent.putExtras(bundle);
        activity.startActivityForResult(intent, 100);
    }

    @Override
    protected void init() {
        super.init();

        initVariate();

        initView();
    }

    private void initVariate() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            resources = bundle.getSparseParcelableArray("resources");
            initViewDate(resources.valueAt(0));
        }
        keyVal = new ArrayList<>();
    }

    private void initView() {
        // title
        TitleView titleView = (TitleView) findViewById(R.id.detail_title);
        titleView.setOnTitleBarClick(new TitleView.TitleBarOnClickListener() {
            @Override
            public void leftOnClick() {
                backToLastPage();
                finish();
            }

            @Override
            public void rightOnClick() {

            }
        });
        // Layout
        LinearLayout itemLayout = (LinearLayout) findViewById(R.id.detail_item);
        ArrayList<Integer> list = new ArrayList<>();
        // 由于SparseArray存储的数值都是按键值从小到大的顺序排列好的。
        for (int i = resources.size() - 1; i >= 0; i--) {
            View itemView = LayoutInflater.from(this).inflate(R.layout.item_resource_detail, null);
            ResourceListBean resource = resources.valueAt(i);
            int actI = resources.size() - i - 1;
            // 记录放置View的索引
            list.add(actI);
            itemLayout.addView(itemView, actI);
            initItem(itemLayout, list, itemView, resource, actI);
        }
        // promotionType
        promotionType = new OptionsPickerView.Builder(this, this::selectType)
                .setLayoutRes(
                        R.layout.pickerview_custom_options,
                        v -> v.findViewById(R.id.picker_view_confirm).setOnClickListener(
                                v1 -> {
                                    promotionType.returnData();
                                    promotionType.dismiss();
                                }
                        )
                )
                .setContentTextSize(15)
                .setTextColorCenter(getResources().getColor(R.color.colorMain))
                .setDividerType(WheelView.DividerType.WRAP)
                .setDividerColor(getResources().getColor(R.color.colorMain))
                .build();
        promotionTypeItems = new ArrayList<>();
        promotionTypeItems.add("个人推广");
        promotionTypeItems.add("公司推广");
        ImageButton ibPromotion = (ImageButton) findViewById(R.id.detail_promotion);
        ibPromotion.setOnClickListener(v -> {
            promotionType.setPicker(promotionTypeItems);
            promotionType.show(v);
        });
    }

    private void selectType(int options1, int options2, int options3, View v) {
        switch (v.getId()) {
            case R.id.detail_promotion:
                LogUtil.d("选择：" + options1);
                String userAccountType = "";
                switch (options1) {
                    case 0:
                        userAccountType = "personal";
                        break;
                    case 1:
                        userAccountType = "corporate";
                        break;
                }
                PromotionActivity.start(this, userAccountType, resources);
                break;
        }
    }

    /**
     * 初始化主布局以及数据
     */
    private void initViewDate(ResourceListBean resource) {
        TextView tvVendorName = (TextView) findViewById(R.id.detail_vendor_name);
        TextView tvCategoryName = (TextView) findViewById(R.id.detail_category_name);
        TextView tvSpec = (TextView) findViewById(R.id.detail_spec);
        TextView tvProductName = (TextView) findViewById(R.id.detail_product_name);
        TextView tvDosageForm = (TextView) findViewById(R.id.detail_dosage_form);
        tvPolicyContent = (TextViewExpandableAnimation) findViewById(R.id.detail_policy_content);
        TextView tvExpiredAt = (TextView) findViewById(R.id.detail_expired_at);
        ImageView ivIntroduce = (ImageView) findViewById(R.id.detail_image);
        jcIntroduce = (JCVideoPlayerStandard) findViewById(R.id.detail_video);
        // 厂家
        tvVendorName.setText(resource.getVendorName());
        // 产品
        tvCategoryName.setText(resource.getCategoryName());
        // 规格
        tvSpec.setText(resource.getSpec());
        // 商品
        tvProductName.setText(resource.getProductName());
        // 剂型
        tvDosageForm.setText(resource.getDosageForm());
        // 推广协议--由于上层数据并未含有推广协议，暂定单独请求某一资源接口获取推广协议
        getPresenter().requestPolicyContent(resource.getId());
//        if (resource.getPolicyContent() != null) {
//            tvPolicyContent.setText(resource.getPolicyContent());
//        }
        // 截止日期
        long expiredTipAt = resource.getBidExpiredTipAt();
        String expire = TimeUtil.timeStamp2Date(expiredTipAt + "", "yyyy年MM月dd日");
        tvExpiredAt.setText(Html.fromHtml("（<font " +
                "color=\"#4188d2\">注意：</font>本资源竞标时间截止日为\t" + expire + "）"));
        // 产品介绍
        String productImageUrl = resource.getProductImageUrl();
        String productVideoUrl = resource.getProductVideoUrl();
        if (productVideoUrl.isEmpty()) {
            jcIntroduce.setVisibility(View.GONE);
            ivIntroduce.setVisibility(View.VISIBLE);
            ImageLoad.loadUrl(this, productImageUrl, ivIntroduce);
            ivIntroduce.setOnClickListener(v ->
                    ShowWebImageActivity.start(getBaseContext(), productImageUrl)
            );
        } else {
            ivIntroduce.setVisibility(View.GONE);
            jcIntroduce.setVisibility(View.VISIBLE);
            jcIntroduce.setUp(productVideoUrl, JCVideoPlayer.SCREEN_LAYOUT_LIST, "产品介绍");
            ImageLoad.loadUrl(this, productVideoUrl + "?vframe/png/offset/10", jcIntroduce.thumbImageView);
        }
    }

    @Override
    public void showPolicyContent(String policyContent) {
        tvPolicyContent.setText(policyContent);
    }

    /**
     * 初始化可删除布局
     */
    private void initItem(LinearLayout itemLayout, ArrayList<Integer> list, View itemView, ResourceListBean resource, int actI) {
        TextView itRegion = (TextView) itemView.findViewById(R.id.item_res_region);
        TextView itCustomer = (TextView) itemView.findViewById(R.id.item_res_customer);
        TextView itTime = (TextView) itemView.findViewById(R.id.item_res_time);
        TextView itQuota = (TextView) itemView.findViewById(R.id.item_res_quota);
        TextView itDeposit = (TextView) itemView.findViewById(R.id.item_res_deposit);
        ImageView ivDelete = (ImageView) itemView.findViewById(R.id.item_res_delete);

        // 删除数据
        ivDelete.setOnClickListener(v -> {
            // 如果只剩下一条数据，则不删除
            if (itemLayout.getChildCount() == 1) {
                ToastUtils.showShort(getString(R.string.detail_at_least_one));
                return;
            }
            // 移除View
            int deletePosition = list.indexOf(actI);
            itemLayout.removeViewAt(deletePosition);
            list.remove(deletePosition);
            ToastUtils.showShort(getString(R.string.detail_delete));
            // 记录所删除数据的key，即资源id
            ResourceListBean temp = resources.valueAt(resources.size() - deletePosition - 1);
            LogUtils.d("移除：" + temp.getAreaName());
            keyVal.add(temp.getId());
            // 移除本页数据记录
            resources.removeAt(resources.size() - deletePosition - 1);
        });
        // 推广区域
        itRegion.setText(resource.getProvinceName() + "\t\t" +
                resource.getCityName() + "\t\t" +
                resource.getAreaName());
        // 客户
        itCustomer.setText(resource.getCustomerName());
        // 推广周期
        long startTime = resource.getStartAt();
        long endTime = resource.getEndAt();
        String start = TimeUtil.timeStamp2Date(startTime + "", "yyyy年MM月dd日");
        String end = TimeUtil.timeStamp2Date(endTime + "", "yyyy年MM月dd日");
        itTime.setText(start + "—" + end);
        //基础指标&&单位
        int quota = resource.getQuota();
        itQuota.setText(quota + resource.getUnits());
        //保证金-总额
        double totalDeposit = resource.getSaleDeposit();
        itDeposit.setText(FormatUtils.MoneyFormat(totalDeposit) + "（人民币）");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            backToLastPage();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        if (JCVideoPlayer.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        JCVideoPlayer.releaseAllVideos();
    }

    /**
     * 返回上一页
     */
    private void backToLastPage() {
        if (keyVal.size() != 0) {
            // 有删除操作，回到上一页需要比对数据
            Intent data = new Intent();
            Bundle bundle = new Bundle();
            bundle.putIntegerArrayList("keys", keyVal);
            data.putExtras(bundle);
            setResult(200, data);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_detail;
    }
}
