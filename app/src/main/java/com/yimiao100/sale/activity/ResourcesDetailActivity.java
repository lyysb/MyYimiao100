package com.yimiao100.sale.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.lib.WheelView;
import com.bigkoo.pickerview.listener.CustomListener;
import com.squareup.picasso.Picasso;
import com.yimiao100.sale.R;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.bean.ErrorBean;
import com.yimiao100.sale.bean.ResourceDetailBean;
import com.yimiao100.sale.bean.ResourceListBean;
import com.yimiao100.sale.ext.JSON;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.FormatUtils;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.TimeUtil;
import com.yimiao100.sale.utils.Util;
import com.yimiao100.sale.view.TextViewExpandableAnimation;
import com.yimiao100.sale.view.TitleView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;
import okhttp3.Call;

/**
 * 资源-资源详情
 */
public class ResourcesDetailActivity extends BaseActivity implements TitleView
        .TitleBarOnClickListener, View.OnClickListener, OptionsPickerView.OnOptionsSelectListener {

    @BindView(R.id.resources_detail_title)
    TitleView mResourcesDetailTitle;
    @BindView(R.id.resources_promotion)
    ImageButton mResourcesPromotion;
    @BindView(R.id.resource_detail_vendor_name)
    TextView mResourceDetailVendorName;
    @BindView(R.id.resource_detail_product_formal_name)
    TextView mResourceDetailProductFormalName;
    @BindView(R.id.resource_detail_spec)
    TextView mResourceDetailSpec;
    @BindView(R.id.resource_detail_product_common_name)
    TextView mResourceDetailProductCommonName;
    @BindView(R.id.resource_detail_dosage_form)
    TextView mResourceDetailDosageForm;
    @BindView(R.id.resource_detail_region)
    TextView mResourceDetailRegion;
    @BindView(R.id.resource_detail_customer)
    TextView mResourceDetailCustomer;
    @BindView(R.id.resource_detail_time)
    TextView mResourceDetailTime;
    @BindView(R.id.resource_detail_quota)
    TextView mResourceDetailQuota;
    @BindView(R.id.resource_detail_total_deposit)
    TextView mResourceDetailTotalDeposit;
    @BindView(R.id.resource_detail_policy_content)
    TextViewExpandableAnimation mResourceDetailPolicyContent;
    @BindView(R.id.resource_detail_expired_at)
    TextView mResourceDetailExpiredAt;
    @BindView(R.id.resource_detail_image)
    ImageView mResourceDetailImage;
    @BindView(R.id.resource_detail_video)
    JCVideoPlayerStandard mResourceDetailVideo;

    private int mResourceID;

    private final String URL_RESOURCE_INFO = Constant.BASE_URL + "/api/resource/resource_info";
    private ResourceListBean mResourceInfo;
    private String mImageUrl;
    private OptionsPickerView mPromotionType;
    private ArrayList<String> mPromotionTypeItems;
    private String mUserAccountType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resources_detail);
        ButterKnife.bind(this);

        showLoadingProgress();

        mResourceID = getIntent().getIntExtra("resourceID", -1);

        LogUtil.d("mResourceId-" + mResourceID);

        initView();

        initData();
    }

    private void initView() {
        mResourcesDetailTitle.setOnTitleBarClick(this);
        mResourcesPromotion.setOnClickListener(this);
        mPromotionType = new OptionsPickerView.Builder(this, this)
                .setLayoutRes(R.layout.pickerview_custom_options, new CustomListener() {
                    @Override
                    public void customLayout(View v) {
                        v.findViewById(R.id.picker_view_confirm).setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                mPromotionType.returnData();
                                mPromotionType.dismiss();
                            }
                        });
                    }
                })
                .setContentTextSize(15)
                .setTextColorCenter(getResources().getColor(R.color.colorMain))
                .setDividerType(WheelView.DividerType.WRAP)
                .setDividerColor(getResources().getColor(R.color.colorMain))
                .build();
        mPromotionTypeItems = new ArrayList<>();
        mPromotionTypeItems.add("个人推广");
        mPromotionTypeItems.add("公司推广");
    }

    private void initData() {
        loadDetail();
    }

    private void loadDetail() {
        OkHttpUtils.post().url(URL_RESOURCE_INFO).addHeader(ACCESS_TOKEN, accessToken)
                .addParams("resourceId", mResourceID + "")
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                e.printStackTrace();
                Util.showTimeOutNotice(currentContext);
                hideLoadingProgress();
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.d("资源详情：" + response);
                hideLoadingProgress();
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        //解析JSON数据
                        mResourceInfo = JSON.parseObject(response, ResourceDetailBean.class)
                                .getResourceResult();
                        setDetail(mResourceInfo);
                        break;
                    case "failure":
                        Util.showError(ResourcesDetailActivity.this, errorBean.getReason());
                        break;
                }
            }
        });
    }

    private void setDetail(ResourceListBean resourceInfo) {
        //厂家名称
        String vendorName = resourceInfo.getVendorName();
        mResourceDetailVendorName.setText(vendorName);
        //产品名-分类名
        String categoryName = resourceInfo.getCategoryName();
        mResourceDetailProductFormalName.setText(categoryName);
        //商品名-产品名
        String productName = resourceInfo.getProductName();
        mResourceDetailProductCommonName.setText(productName);
        //规格
        String spec = resourceInfo.getSpec();
        mResourceDetailSpec.setText(spec);
        //剂型
        String dosageForm = resourceInfo.getDosageForm();
        mResourceDetailDosageForm.setText(dosageForm);
        //推广区域
        String provinceName = resourceInfo.getProvinceName();
        String cityName = resourceInfo.getCityName();
        String areaName = resourceInfo.getAreaName();
        mResourceDetailRegion.setText(provinceName + "\t\t" + cityName + "\t\t" + areaName);
        // 客户
        if (resourceInfo.getCustomerName() != null) {
            mResourceDetailCustomer.setText(resourceInfo.getCustomerName());
        }
        //完成周期
        long startTime = resourceInfo.getStartAt();
        long endTime = resourceInfo.getEndAt();
        String start = TimeUtil.timeStamp2Date(startTime + "", "yyyy年MM月dd日");
        String end = TimeUtil.timeStamp2Date(endTime + "", "yyyy年MM月dd日");
        mResourceDetailTime.setText(start + "—" + end);
        //基础指标&&单位
        int quota = resourceInfo.getQuota();
        mResourceDetailQuota.setText(quota + resourceInfo.getUnits());
        //保证金-总额
        double totalDeposit = resourceInfo.getSaleDeposit();
        mResourceDetailTotalDeposit.setText(FormatUtils.MoneyFormat(totalDeposit) + "（人民币）");
        //推广政策
        String policyContent = resourceInfo.getPolicyContent();
        mResourceDetailPolicyContent.setText(policyContent);
        //截止日期
        long expiredTipAt = resourceInfo.getBidExpiredTipAt();
        String expire = TimeUtil.timeStamp2Date(expiredTipAt + "", "yyyy年MM月dd日");
        mResourceDetailExpiredAt.setText(Html.fromHtml("（<font " +
                "color=\"#4188d2\">注意：</font>本资源竞标时间截止日为\t" + expire + "）"));
        //完成设置视频
        //图片url
        mImageUrl = resourceInfo.getProductImageUrl();
        //视频url
        String productVideoUrl = resourceInfo.getProductVideoUrl();
        //先判断视频链接是否为空
        if (!productVideoUrl.isEmpty()) {
            //视频链接不为空，显示视频控件，隐藏图片控件
            mResourceDetailVideo.setVisibility(View.VISIBLE);
            mResourceDetailImage.setVisibility(View.GONE);
            //播放视频
            mResourceDetailVideo.setUp(productVideoUrl
                    , JCVideoPlayer.SCREEN_LAYOUT_LIST, "产品介绍");
            //设置预览图
            Picasso.with(this)
                    .load(productVideoUrl + "?vframe/png/offset/10")
                    .into(mResourceDetailVideo.thumbImageView);
        } else {
            //视频链接为空，显示图片
            mResourceDetailVideo.setVisibility(View.GONE);
            mResourceDetailImage.setVisibility(View.VISIBLE);
            mResourceDetailImage.setOnClickListener(this);
            if (!mImageUrl.isEmpty()) {
                //图片链接不为空，则加载网络图片
                Picasso.with(this).load(mImageUrl)
                        .placeholder(R.mipmap.ico_default_bannner)
                        .into(mResourceDetailImage);
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.resource_detail_image:
                Intent intent = new Intent();
                intent.putExtra("image", mImageUrl);
                intent.setClass(this, ShowWebImageActivity.class);
                startActivity(intent);
                break;
            case R.id.resources_promotion:
                if (mResourceInfo == null) {
                    return;
                }
                // 选择推广方式
                mPromotionType.setPicker(mPromotionTypeItems);
                mPromotionType.show(v);
                break;
        }
    }

    @Override
    public void onOptionsSelect(int options1, int options2, int options3, View v) {
        switch (v.getId()) {
            case R.id.resources_promotion:
                LogUtil.d("选择：" + options1);
                Intent intent = new Intent(currentContext, ResourcesPromotionActivity.class);
                intent.putExtra("resourceInfo", mResourceInfo);
                switch (options1) {
                    case 0:
                        mUserAccountType = "personal";
                        break;
                    case 1:
                        mUserAccountType = "corporate";
                        break;
                }
                intent.putExtra("userAccountType", mUserAccountType);
                startActivity(intent);
                break;
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mPromotionType.isShowing()) {
                mPromotionType.dismiss();
                return true;
            }
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

    @Override
    public void leftOnClick() {
        finish();
    }


    @Override
    public void rightOnClick() {
    }
}
