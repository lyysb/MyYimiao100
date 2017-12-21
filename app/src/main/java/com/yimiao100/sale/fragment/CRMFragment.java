package com.yimiao100.sale.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.header.progresslayout.ProgressLayout;
import com.squareup.picasso.Picasso;
import com.yimiao100.sale.R;
import com.yimiao100.sale.activity.*;
import com.yimiao100.sale.adapter.peger.CRMAdAdapter;
import com.yimiao100.sale.base.BaseFragment;
import com.yimiao100.sale.bean.Carousel;
import com.yimiao100.sale.bean.ErrorBean;
import com.yimiao100.sale.bean.Event;
import com.yimiao100.sale.bean.Tips;
import com.yimiao100.sale.bean.TipsBean;
import com.yimiao100.sale.bean.WaveBean;
import com.yimiao100.sale.bean.WaveStat;
import com.yimiao100.sale.ext.JSON;
import com.yimiao100.sale.insurance.InsuranceActivity;
import com.yimiao100.sale.utils.CarouselUtil;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.DensityUtil;
import com.yimiao100.sale.utils.FormatUtils;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.ScreenUtil;
import com.yimiao100.sale.utils.SharePreferenceUtil;
import com.yimiao100.sale.utils.Util;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.bingoogolapple.bgabanner.BGABanner;
import me.itangqi.waveloadingview.WaveLoadingView;
import okhttp3.Call;
import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;



/**
 * CRM
 * Created by 亿苗通 on 2016/8/1.
 */
public class CRMFragment extends BaseFragment implements View.OnClickListener, CarouselUtil
        .HandleCarouselListener {


    private View mView;

    private final String URL_SALE_STAT = Constant.BASE_URL + "/api/stat/sale_stat";
    private final String BALANCE_ORDER = "balance_order";                   //对账订单
    private final String ORDER_ONLINE = "order_online";                     // 在线下单
    private final String ACCESS_TOKEN = "X-Authorization-Token";
    private final String USER_TIPS_ALL = Constant.BASE_URL + "/api/tip/user_tips_all";

    private TwinklingRefreshLayout mRefreshLayout;
    private ImageButton mNotice;
    private ImageButton mReconciliation;
    private ImageView mCustomer;
    private Badge mBadge;
    private WaveLoadingView mWaveShip;
    private WaveLoadingView mWavePayment;
    private BGABanner banner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        mView = View.inflate(getContext(), R.layout.fragment_crm, null);
        initView();
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtils.d("onResume");
        banner.startAutoPlay();
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtils.d("onPause");
        banner.stopAutoPlay();
    }


    private void initView() {
        // 客服
        mCustomer = (ImageView) mView.findViewById(R.id.crm_service);
        mBadge = new QBadgeView(getContext()).bindTarget(mCustomer)
                .setBadgePadding(4, true)
                .setGravityOffset(9, true)
                .setShowShadow(false);

        mCustomer.setOnClickListener(this);
        // 刷新控件
        mRefreshLayout = (TwinklingRefreshLayout) mView.findViewById(R.id.crm_refresh_layout);
        ProgressLayout header = new ProgressLayout(getActivity());
        header.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color
                .holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        mRefreshLayout.setHeaderView(header);
        mRefreshLayout.setFloatRefresh(true);
        mRefreshLayout.setOverScrollRefreshShow(false);
        mRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {

            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                super.onRefresh(refreshLayout);
                // 重新加载数据
                initData();
            }
        });
        //广告
        banner = (BGABanner) mView.findViewById(R.id.crm_banner);
        //水波纹统计
        mWaveShip = (WaveLoadingView) mView.findViewById(R.id.wave_ship);
        mWaveShip.setOnClickListener(this);
        mWavePayment = (WaveLoadingView) mView.findViewById(R.id.wave_payment);
        mWavePayment.setOnClickListener(this);

        //通知
        mNotice = (ImageButton) mView.findViewById(R.id.crm_notice);
        mNotice.setOnClickListener(this);
        //资源
        mView.findViewById(R.id.crm_resources).setOnClickListener(this);
        //保险
        View insuranceView = mView.findViewById(R.id.crm_insurance);
        insuranceView.setOnClickListener(this);
        //客户--废弃
        View customerView = mView.findViewById(R.id.crm_customer);
        customerView.setOnClickListener(this);
        if (Constant.isInsurance) {
            // 开启保险模块
            insuranceView.setVisibility(View.VISIBLE);
            customerView.setVisibility(View.GONE);
        } else {
            // 关闭保险模块
            insuranceView.setVisibility(View.GONE);
            customerView.setVisibility(View.VISIBLE);
        }
        //申报
        mView.findViewById(R.id.crm_upload).setOnClickListener(this);
        //对账
        mReconciliation = (ImageButton) mView.findViewById(R.id.crm_reconciliation);
        mReconciliation.setOnClickListener(this);
        //财富
        mView.findViewById(R.id.crm_riches).setOnClickListener(this);
        // 在线下单
        mView.findViewById(R.id.crm_order_online).setOnClickListener(this);
        // 疫苗查询
        mView.findViewById(R.id.crm_query).setOnClickListener(this);
    }

    private void initData() {
        //获取广告轮播图
        CarouselUtil.getCarouselList(getActivity(), "biz", this);
        //初始化水波纹数据
        initWave();
        // 获取相关提示
        initUserTips();
    }

    private void initUserTips() {
        OkHttpUtils.post().url(USER_TIPS_ALL).addHeader(ACCESS_TOKEN, accessToken)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.d("user tips is error");
                e.printStackTrace();
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.d("get user tips success : " + response);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        // 解析
                        Tips tips = JSON.parseObject(response, TipsBean.class).getTips();
                        if (tips.getNotice() == 1) {
                            mNotice.setImageResource(R.drawable.selector_crm_notice_unread);
                        } else {
                            mNotice.setImageResource(R.drawable.selector_crm_notice);
                        }
                        if (tips.getOrder_balance() == 1) {
                            mReconciliation.setImageResource(R.drawable.selector_crm_reconciliation_unread);
                        } else {
                            mReconciliation.setImageResource(R.drawable.selector_crm_reconciliation);
                        }
                        break;
                    case "failure":
                        Util.showError(getActivity(), errorBean.getReason());
                        break;
                }

            }
        });
    }

    @Override
    public void onEventMainThread(@NotNull Event event) {
        super.onEventMainThread(event);
        switch (Event.eventType) {
            case NOTICE:
            case ORDER_BALANCE:
                // 重新刷新提醒数据
                initUserTips();
                break;
            case RECEIVE_MSG:
                // 收到客服消息，显示小圆点
                mBadge.setBadgeNumber(-1);
                break;
            case READ_MSG:
                // 设置小圆点为0
                mBadge.setBadgeNumber(0);
                break;
            default:
                LogUtil.d("unknown event type is " + Event.eventType);
                break;
        }
    }

    /**
     * 初始化水波纹
     *
     */
    private void initWave() {
        OkHttpUtils.post().url(URL_SALE_STAT).addHeader(ACCESS_TOKEN, accessToken)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                if (mRefreshLayout.isShown()) {
                    mRefreshLayout.finishRefreshing();
                }
            }

            @Override
            public void onResponse(String response, int id) {
                if (mRefreshLayout.isShown()) {
                    mRefreshLayout.finishRefreshing();
                }
                LogUtil.d("CRM-Wave：" + response);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        WaveStat waveStat = JSON.parseObject(response, WaveBean.class)
                                .getStatResult();
                        double goodsPercent;        //发货统计
                        if (waveStat.getDeliveryTotalQty() != 0) {
                            goodsPercent = (double) waveStat.getDeliveryQty() / (double) waveStat
                                    .getDeliveryTotalQty();
                            if (goodsPercent > 1) {
                                goodsPercent = 1;
                            }
                        } else {
                            goodsPercent = 0;
                        }
                        double paymentPercent;      //回款统计
                        if (waveStat.getPaymentTotalAmount() != 0) {
                            paymentPercent = waveStat.getPaymentAmount() / waveStat
                                    .getPaymentTotalAmount();
                            if (paymentPercent > 1) {
                                paymentPercent = 1;
                            }
                        } else {
                            paymentPercent = 0;
                        }
                        mWaveShip.setProgressValue((int) (goodsPercent * 100));
                        mWaveShip.setCenterTitleColor(goodsPercent >= 0.55 ? Color.parseColor
                                ("#ffffff") : Color.parseColor("#000000"));
                        mWaveShip.setCenterTitle(FormatUtils.PercentFormat(goodsPercent * 100) + "%");

                        mWavePayment.setProgressValue((int) (paymentPercent * 100));
                        mWavePayment.setCenterTitleColor(paymentPercent >= 0.55 ? Color.parseColor
                                ("#ffffff") : Color.parseColor("#000000"));
                        mWavePayment.setCenterTitle(FormatUtils.PercentFormat(paymentPercent * 100) + "%");
                        break;
                    case "failure":
                        Util.showError(getActivity(), errorBean.getReason());
                        break;
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.crm_service:
                // 进入客服
                Util.enterCustomerService(getContext());
                break;
            case R.id.wave_ship:
                //进入发货统计
                startActivity(new Intent(getContext(), ShipActivity.class));
                break;
            case R.id.wave_payment:
                //进入回款统计
                startActivity(new Intent(getContext(), PaymentActivity.class));
                break;
            case R.id.crm_notice:
                //跳转到通知列表
                startActivity(new Intent(getContext(), NoticeActivity.class));
                break;
            case R.id.crm_resources:
                //跳转到资源列表
                startActivity(new Intent(getContext(), ResourcesActivity.class));
                break;
            case R.id.crm_insurance:
                // 跳转到保险列表
                startActivity(new Intent(getContext(), InsuranceActivity.class));
                break;
            case R.id.crm_customer:
                //跳转到客户列表
                startActivity(new Intent(getContext(), CustomerActivity.class));
                break;
            case R.id.crm_upload:
                //申报
                startActivity(new Intent(getContext(), UploadActivity.class));
                break;
            case R.id.crm_reconciliation:
                //跳转到对账列表
//                Intent reconciliationIntent = new Intent(getContext(), VendorListActivity.class);
//                reconciliationIntent.putExtra("moduleType", BALANCE_ORDER);
//                startActivity(reconciliationIntent);
                Intent intent = new Intent(getContext(), VendorArrayActivity.class);
                intent.putExtra("type", "reconciliation");
                startActivity(intent);
                break;
            case R.id.crm_riches:
                //跳转到财富列表
                startActivity(new Intent(getContext(), RichesActivity.class));
                break;
            case R.id.crm_order_online:
                // 跳转到在线下单
                Intent orderOnlineIntent = new Intent(getContext(), VendorOrderOnlineActivity.class);
                orderOnlineIntent.putExtra("moduleType", ORDER_ONLINE);
                startActivity(orderOnlineIntent);
                break;
            case R.id.crm_query:
                // 进入疫苗查询
                startActivity(new Intent(getContext(), VaccineQueryActivity.class));
                break;

        }
    }


    /**
     * 处理广告轮播图数据
     *
     * @param carouselList
     */
    @Override
    public void handleCarouselList(ArrayList<Carousel> carouselList) {
        LogUtil.d("list size is " + carouselList.size());
        banner.setAdapter((banner, itemView, model, position) ->
                Picasso.with(getContext())
                        .load(((Carousel) model).getMediaUrl())
                        .placeholder(R.mipmap.ico_default_bannner)
                        .resize(ScreenUtil.getScreenWidth(getContext()), DensityUtil.dp2px(getContext(), 190))
                        .into((ImageView) itemView));
        List<String> desc = new ArrayList<>();
        for (Carousel carousel : carouselList) {
            desc.add(carousel.getObjectTitle());
        }
        banner.setData(carouselList, desc);
    }


    /**
     * 解决如下问题
     * java.lang.IllegalStateException: No host
     */
    @Override
    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
