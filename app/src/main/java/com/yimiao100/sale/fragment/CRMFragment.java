package com.yimiao100.sale.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.yimiao100.sale.R;
import com.yimiao100.sale.activity.CustomerActivity;
import com.yimiao100.sale.activity.NoticeActivity;
import com.yimiao100.sale.activity.OrderErrorActivity;
import com.yimiao100.sale.activity.PaymentActivity;
import com.yimiao100.sale.activity.ResourcesActivity;
import com.yimiao100.sale.activity.RichesActivity;
import com.yimiao100.sale.activity.ShipActivity;
import com.yimiao100.sale.activity.VendorListActivity;
import com.yimiao100.sale.activity.WareHouseActivity;
import com.yimiao100.sale.activity.WholesaleActivity;
import com.yimiao100.sale.adapter.peger.CRMAdAdapter;
import com.yimiao100.sale.bean.Carousel;
import com.yimiao100.sale.bean.ErrorBean;
import com.yimiao100.sale.bean.WaveBean;
import com.yimiao100.sale.bean.WaveStat;
import com.yimiao100.sale.utils.CarouselUtil;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.DensityUtil;
import com.yimiao100.sale.utils.FormatUtils;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.SharePreferenceUtil;
import com.yimiao100.sale.utils.ToastUtil;
import com.yimiao100.sale.utils.Util;
import com.yimiao100.sale.view.DynamicWave;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;

import okhttp3.Call;



/**
 * CRM
 * Created by 亿苗通 on 2016/8/1.
 */
public class CRMFragment extends Fragment implements View.OnClickListener, CarouselUtil.HandleCarouselListener {


    private View mView;
    private ViewPager mCrm_ad;

    private final String URL_SALE_STAT = Constant.BASE_URL + "/api/stat/sale_stat";
    private final int FROM_RECONCILIATION = 4;      //来自对账
    private final String ACCESS_TOKEN = "X-Authorization-Token";

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
    private DynamicWave mWaveGoods;
    private TextView mCrmGoodsEd;
    private DynamicWave mWaveMoney;
    private TextView mCrmPaymentEd;
    private TextView mCrmDesc;
    private LinearLayout mCrmDots;
    private ArrayList<Carousel> mCarouselList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        mView = View.inflate(getContext(), R.layout.fragment_crm, null);
        LogUtil.d("onCreateView");
        initContentView();
        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();

        mHandler.sendEmptyMessageDelayed(SHOW_NEXT_PAGE, 3000);
        //初始化水波纹数据
        initWave(mWaveGoods, mCrmGoodsEd, mWaveMoney, mCrmPaymentEd);
    }

    private void initContentView() {
        mAccessToken = (String) SharePreferenceUtil.get(getContext(), Constant.ACCESSTOKEN, "");
        //广告
        mCrm_ad = (ViewPager) mView.findViewById(R.id.crm_ad);
        //广告标题
        mCrmDesc = (TextView) mView.findViewById(R.id.crm_tv_desc);
        //广告小圆点
        mCrmDots = (LinearLayout) mView.findViewById(R.id.crm_ll_dots);
        //获取广告轮播图
        CarouselUtil.getCarouselList(getActivity(), "biz", this);
        //水波纹统计
        mWaveGoods = (DynamicWave) mView.findViewById(R.id.wave_goods);
        mCrmGoodsEd = (TextView) mView.findViewById(R.id.crm_goods_ed);
        mWaveGoods.setOnClickListener(this);
        mWaveMoney = (DynamicWave) mView.findViewById(R.id.wave_money);
        mCrmPaymentEd = (TextView) mView.findViewById(R.id.crm_payment_ed);
        mWaveMoney.setOnClickListener(this);

        //通知
        mView.findViewById(R.id.crm_notice).setOnClickListener(this);
        //资源
        mView.findViewById(R.id.crm_resources).setOnClickListener(this);
        //客户
        mView.findViewById(R.id.crm_customer).setOnClickListener(this);
        //分析
        mView.findViewById(R.id.crm_analysis).setOnClickListener(this);
        //对账
        mView.findViewById(R.id.crm_reconciliation).setOnClickListener(this);
        //财富
        mView.findViewById(R.id.crm_riches).setOnClickListener(this);
        //疫苗库
        mView.findViewById(R.id.crm_warehouse).setOnClickListener(this);
        //批签发
        mView.findViewById(R.id.crm_wholesale).setOnClickListener(this);
    }

    /**
     * 初始化水波纹
     * @param waveGoods
     * @param crm_goods_ed
     * @param waveMoney
     * @param crm_payment_ed
     */
    private void initWave(final DynamicWave waveGoods, final TextView crm_goods_ed, final
    DynamicWave waveMoney, final TextView crm_payment_ed) {
        OkHttpUtils.post().url(URL_SALE_STAT).addHeader(ACCESS_TOKEN, mAccessToken).build()
                .execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                if (CRMFragment.this.isAdded()) {
                    //防止Fragment点击报空指针
                    Util.showTimeOutNotice(getActivity());
                }
            }

            @Override
            public void onResponse(String response, int id) {
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
                        waveGoods.setPercent(goodsPercent);
                        crm_goods_ed.setTextColor(goodsPercent >= 0.55 ? Color.parseColor("#ffffff") : Color.parseColor("#000000"));
                        crm_goods_ed.setText(FormatUtils.PercentFormat(goodsPercent * 100) + "%");
                        waveMoney.setPercent(paymentPercent);
                        crm_payment_ed.setTextColor(paymentPercent >= 0.55 ? Color.parseColor("#ffffff") : Color.parseColor("#000000"));
                        crm_payment_ed.setText(FormatUtils.PercentFormat(paymentPercent * 100) + "%");
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
            case R.id.wave_goods:
                //进入发货统计
                startActivity(new Intent(getContext(),  ShipActivity.class));
                break;
            case R.id.wave_money:
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
            case R.id.crm_analysis:
                //todo 跳转到分析页面
                ToastUtil.showShort(getContext(), "敬请期待");
                break;
            case R.id.crm_reconciliation:
                //跳转到对账列表
                Intent reconciliationIntent = new Intent(getContext(), VendorListActivity.class);
                reconciliationIntent.putExtra("enter_from", FROM_RECONCILIATION);
                startActivity(reconciliationIntent);
                break;
            case R.id.crm_riches:
                //跳转到财富列表
                startActivity(new Intent(getContext(), RichesActivity.class));
                break;
            case R.id.crm_warehouse:
                //跳转到疫苗库
                startActivity(new Intent(getContext(), WareHouseActivity.class));
                break;
            case R.id.crm_wholesale:
                //跳转到批签发
                startActivity(new Intent(getContext(), WholesaleActivity.class));
                break;

        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mHandler.removeMessages(SHOW_NEXT_PAGE);
    }

    /**
     * 处理广告轮播图数据
     * @param carouselList
     */
    @Override
    public void handleCarouselList(ArrayList<Carousel> carouselList) {
        //填充数据
        mCarouselList = carouselList;
        CRMAdAdapter crmAdAdapter = new CRMAdAdapter(mCarouselList);
        mCrm_ad.setAdapter(crmAdAdapter);
        mCrm_ad.setCurrentItem(mCrm_ad.getAdapter().getCount() / 2);
        mCrm_ad.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int
                    positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                LogUtil.d("CRM-position---" + position);
                changeDescAndDot(position % mCarouselList.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        //初始化小圆点
        initDots(carouselList);
        //选中小圆点并且设置文字描述
        changeDescAndDot(0);
    }

    /**
     * 初始化小圆点
     * @param carouselList
     */
    private void initDots(ArrayList<Carousel> carouselList) {
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
}
