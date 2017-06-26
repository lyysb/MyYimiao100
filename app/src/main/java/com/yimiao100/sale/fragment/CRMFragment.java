package com.yimiao100.sale.fragment;

import android.app.ProgressDialog;
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

import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.header.progresslayout.ProgressLayout;
import com.meiqia.core.MQManager;
import com.meiqia.core.bean.MQMessage;
import com.meiqia.core.callback.OnGetMessageListCallback;
import com.yimiao100.sale.R;
import com.yimiao100.sale.activity.CustomerActivity;
import com.yimiao100.sale.activity.NoticeActivity;
import com.yimiao100.sale.activity.PaymentActivity;
import com.yimiao100.sale.activity.ResourcesActivity;
import com.yimiao100.sale.activity.RichesActivity;
import com.yimiao100.sale.activity.ShipActivity;
import com.yimiao100.sale.activity.UploadActivity;
import com.yimiao100.sale.activity.VaccineQueryActivity;
import com.yimiao100.sale.activity.VendorListActivity;
import com.yimiao100.sale.activity.VendorOrderOnlineActivity;
import com.yimiao100.sale.adapter.peger.CRMAdAdapter;
import com.yimiao100.sale.base.BaseFragment;
import com.yimiao100.sale.bean.Carousel;
import com.yimiao100.sale.bean.ErrorBean;
import com.yimiao100.sale.bean.Event;
import com.yimiao100.sale.bean.EventType;
import com.yimiao100.sale.bean.Tips;
import com.yimiao100.sale.bean.TipsBean;
import com.yimiao100.sale.bean.WaveBean;
import com.yimiao100.sale.bean.WaveStat;
import com.yimiao100.sale.ext.JSON;
import com.yimiao100.sale.utils.CarouselUtil;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.DensityUtil;
import com.yimiao100.sale.utils.FormatUtils;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.SharePreferenceUtil;
import com.yimiao100.sale.utils.Util;
import com.yimiao100.sale.view.DynamicWave;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import me.itangqi.waveloadingview.WaveLoadingView;
import okhttp3.Call;
import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;



/**
 * CRM
 * Created by 亿苗通 on 2016/8/1.
 */
public class CRMFragment extends BaseFragment implements View.OnClickListener, CarouselUtil
        .HandleCarouselListener, ViewPager.OnPageChangeListener {


    private View mView;
    private ViewPager mCrm_ad;

    private final String URL_SALE_STAT = Constant.BASE_URL + "/api/stat/sale_stat";
    private final String BALANCE_ORDER = "balance_order";                   //对账订单
    private final String ORDER_ONLINE = "order_online";                     // 在线下单
    private final String ACCESS_TOKEN = "X-Authorization-Token";
    private final String USER_TIPS = Constant.BASE_URL + "/api/tip/user_tips";

    private String mAccessToken;
    /**
     * 显示下一页
     */
    public static final int SHOW_NEXT_PAGE = 0;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_NEXT_PAGE:
                    showNextPage();
                    break;
            }
        }
    };
    private TextView mCrmDesc;
    private LinearLayout mCrmDots;
    private ArrayList<Carousel> mCarouselList;
    private TwinklingRefreshLayout mRefreshLayout;
    private ImageButton mNotice;
    private ImageButton mReconciliation;
    private HashMap<String, ArrayList<String>> mDetails;
    private ImageView mCustomer;
    private Badge mBadge;
    private WaveLoadingView mWaveShip;
    private WaveLoadingView mWavePayment;

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
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // 如果可见，滑动切换
            mHandler.sendEmptyMessageDelayed(SHOW_NEXT_PAGE, 3000);
        } else {
            mHandler.removeMessages(SHOW_NEXT_PAGE);
        }
        LogUtil.d("setUserVisibleHint isVisibleToUser is " + isVisibleToUser);
        LogUtil.d("setUserVisibleHint handler has msg is " + mHandler.hasMessages(SHOW_NEXT_PAGE));
    }

    @Override
    public void onStart() {
        super.onStart();
        LogUtil.d("onStart handler has msg is " + mHandler.hasMessages(SHOW_NEXT_PAGE));
    }

    @Override
    public void onStop() {
        super.onStop();
        LogUtil.d("onStop handler has msg is " + mHandler.hasMessages(SHOW_NEXT_PAGE));
    }

    private void initView() {
        mAccessToken = (String) SharePreferenceUtil.get(getContext(), Constant.ACCESSTOKEN, "");
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
        mCrm_ad = (ViewPager) mView.findViewById(R.id.crm_ad);
        mCrm_ad.addOnPageChangeListener(this);
        //广告标题
        mCrmDesc = (TextView) mView.findViewById(R.id.crm_tv_desc);
        //广告小圆点
        mCrmDots = (LinearLayout) mView.findViewById(R.id.crm_ll_dots);
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
        //客户
        mView.findViewById(R.id.crm_customer).setOnClickListener(this);
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
        OkHttpUtils.post().url(USER_TIPS).addHeader(ACCESS_TOKEN, mAccessToken)
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
                        mDetails = tips.getDetails();
                        LogUtil.d(mDetails.entrySet().toString());
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
        OkHttpUtils.post().url(URL_SALE_STAT).addHeader(ACCESS_TOKEN, mAccessToken)
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

    /**
     * 显示下一页
     */
    public void showNextPage() {
        mCrm_ad.setCurrentItem(mCrm_ad.getCurrentItem() + 1);
        mHandler.sendEmptyMessageDelayed(SHOW_NEXT_PAGE, 3000);
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
                Intent reconciliationIntent = new Intent(getContext(), VendorListActivity.class);
                reconciliationIntent.putExtra("moduleType", BALANCE_ORDER);
                startActivity(reconciliationIntent);
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
        //填充数据
        mCarouselList = carouselList;
        CRMAdAdapter crmAdAdapter = new CRMAdAdapter(mCarouselList);
        mCrm_ad.setAdapter(crmAdAdapter);
        mCrm_ad.setCurrentItem(mCrm_ad.getAdapter().getCount() / 2);
        //初始化小圆点
        initDots(carouselList);
        //选中小圆点并且设置文字描述
        changeDescAndDot(0);
    }

    /**
     * 初始化小圆点
     *
     * @param carouselList
     */
    private void initDots(ArrayList<Carousel> carouselList) {
        mCrmDots.removeAllViews();
        for (int i = 0; i < carouselList.size(); i++) {
            View dot = new View(getContext());
            dot.setBackgroundResource(R.drawable.selector_dot);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DensityUtil.dp2px
                    (getContext(), 5), DensityUtil.dp2px(getContext(), 5));
            params.leftMargin = i == 0 ? 0 : DensityUtil.dp2px(getContext(), 5);  //
            // 如果是第0个点，不需要设置leftMargin
            dot.setLayoutParams(params);        // 设置dot的layout参数
            mCrmDots.addView(dot);       // 把dot添加到线性布局中
        }
    }

    /**
     * 选中小圆点并且设置文字描述
     *
     * @param position
     */
    private void changeDescAndDot(int position) {
        // 显示position位置的文字描述
        mCrmDesc.setText(mCarouselList.get(position).getObjectTitle());
        // 把position位置的点设置为selected状态
        for (int i = 0; i < mCrmDots.getChildCount(); i++) {
            mCrmDots.getChildAt(i).setSelected(i == position);
        }
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

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        LogUtil.d("CRM-position---" + position);
        changeDescAndDot(position % mCarouselList.size());
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
