package com.yimiao100.sale.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.yimiao100.sale.R;
import com.yimiao100.sale.activity.InformationDetailActivity;
import com.yimiao100.sale.activity.LiveActivity;
import com.yimiao100.sale.activity.SearchActivity;
import com.yimiao100.sale.adapter.listview.InformationAdapter;
import com.yimiao100.sale.adapter.peger.InformationAdAdapter;
import com.yimiao100.sale.bean.Carousel;
import com.yimiao100.sale.bean.ErrorBean;
import com.yimiao100.sale.bean.ImageListBean;
import com.yimiao100.sale.bean.InformationBean;
import com.yimiao100.sale.bean.PagedListBean;
import com.yimiao100.sale.bean.PagedResultBean;
import com.yimiao100.sale.utils.CarouselUtil;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.DensityUtil;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.Util;
import com.yimiao100.sale.view.PullToRefreshListView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * 资讯列表界面
 * Created by 亿苗通 on 2016/8/1.
 */
public class InformationFragment extends Fragment implements SwipeRefreshLayout
        .OnRefreshListener, AdapterView.OnItemClickListener, PullToRefreshListView
        .OnRefreshingListener,InformationAdAdapter
        .OnAdClickListener, ViewPager.OnPageChangeListener, View.OnClickListener, CarouselUtil.HandleCarouselListener {
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

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private View mView;

    private final String URL_NEWS_LIST = Constant.BASE_URL + "/api/news/list";
    private final String PAGE = "page";
    private final String PAGE_SIZE = "pageSize";
    private final String URL_CAROUSEL = Constant.BASE_URL + "/api/carousel/list";
    private final String CAROUSEL_TYPE = "carouselType";


    private PullToRefreshListView mLv_information;
    private List<PagedListBean> mPagedList;
    private InformationAdapter mInformationAdapter;

    private int mPage;
    private int mTotalPage;
    private String mCarouselType = "news";

    private ViewPager mInformationAd;
    private TextView mDesc;
    private LinearLayout mDots;
    private InformationAdAdapter mAdapter;
    private View mHeadView;
    private ArrayList<Carousel> mCarouselList;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        mView = View.inflate(getContext(), R.layout.fragment_informartion, null);

        initView();

        initData();
        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
        mHandler.sendEmptyMessageDelayed(SHOW_NEXT_PAGE, 5000);
    }

    private void initView() {
        mHeadView = View.inflate(getContext(), R.layout.head_information, null);
        initAdView();

        initSearchView();

        initLiveView();


        initRefreshLayout();

        initListView();
    }

    /**
     * 直播按钮
     */
    private void initLiveView() {
        ImageView liveRadio = (ImageView) mView.findViewById(R.id.information_live_radio);
        liveRadio.setOnClickListener(this);
    }

    /**
     * 广告轮播图
     */
    private void initAdView() {
        mInformationAd = (ViewPager) mHeadView.findViewById(R.id.information_ad);
        mInformationAd.addOnPageChangeListener(this);
        mDesc = (TextView) mHeadView.findViewById(R.id.tv_desc);
        mDots = (LinearLayout) mHeadView.findViewById(R.id.ll_dots);
    }

    /**
     * 搜索按钮
     */
    private void initSearchView() {
        //搜索
        ImageView information_search = (ImageView) mView.findViewById(R.id.information_search);
        information_search.setOnClickListener(this);
    }

    /**
     * 刷新控件
     */
    private void initRefreshLayout() {
        //下拉刷新控件
        mSwipeRefreshLayout = (SwipeRefreshLayout) mView.findViewById(R.id.information_swipe);
        //设置刷新监听
        mSwipeRefreshLayout.setOnRefreshListener(this);
        //设置下拉圆圈的颜色
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R
                        .color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        //设置手指在屏幕下拉多少距离会触发下拉刷新
        mSwipeRefreshLayout.setDistanceToTriggerSync(400);
    }

    /**
     * ListView
     */
    private void initListView() {
        //listView
        mLv_information = (PullToRefreshListView) mView.findViewById(R.id.information_list);
        mLv_information.setOnItemClickListener(this);
        mLv_information.setOnRefreshingListener(this);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        //获取轮播图数据
        initAdContent();
        //获取资讯数据
        initInformation();
    }

    /**
     * 获取轮播图数据
     */
    private void initAdContent() {
        //请求获取轮播图
        CarouselUtil.getCarouselList(getActivity(), "news", this);
    }

    /**
     * 处理轮播图
     * @param carouselList
     */
    @Override
    public void handleCarouselList(ArrayList<Carousel> carouselList) {
        if (carouselList.get(0).getMediaUrl().equals("http://oduhua0b1.bkt.clouddn" +
                ".com/banner_placeholder.png")
                && carouselList.size() == 1) {
            //不做任何操作，意味着是假数据。
        } else {
            mDots.removeAllViews();
            if (mLv_information.getHeaderViewsCount() == 0) {
                mLv_information.addHeaderView(mHeadView);
            }

            mCarouselList = carouselList;
            mAdapter = new InformationAdAdapter(mCarouselList);
            mInformationAd.setAdapter(mAdapter);
            //显示当前选中的界面
            mInformationAd.setCurrentItem(mAdapter.getCount() / 2);
            mAdapter.setOnAdClickListener(InformationFragment.this);
            //初始化小圆点
            initDots();
            //选中小圆点并且设置文字描述
            changeDescAndDot(0);
        }



    }

    /**
     * 初始化资讯数据
     */
    private void initInformation() {
        getBuild(1).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.d("资讯列表E：" + e.getMessage());
                if (InformationFragment.this.isAdded()) {
                    //防止Fragment点击报空指针
                    Util.showTimeOutNotice(getActivity());
                }
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.d("资讯列表：" + response);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        mPage = 2;
                        PagedResultBean pagedResult = JSON.parseObject(response, InformationBean
                                .class).getPagedResult();
                        mTotalPage = pagedResult.getTotalPage();
                        //获取资讯列表
                        mPagedList = pagedResult.getPagedList();
                        mInformationAdapter = new InformationAdapter(getContext(), mPagedList);
                        mLv_information.setAdapter(mInformationAdapter);
                        break;
                    case "failure":
                        Util.showError(getActivity(), errorBean.getReason());
                        break;
                }
            }
        });
    }

    /**
     * 轮播图-初始化轮播图小圆点
     */
    private void initDots() {
        for (int i = 0; i < mCarouselList.size(); i++) {
            View dot = new View(getContext());
            dot.setBackgroundResource(R.drawable.selector_dot);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DensityUtil.dp2px
                    (getContext(), 5), DensityUtil.dp2px(getContext(), 5));
            params.leftMargin = i == 0 ? 0 : DensityUtil.dp2px(getContext(), 5);  //
            // 如果是第0个点，不需要设置leftMargin
            dot.setLayoutParams(params);        // 设置dot的layout参数
            mDots.addView(dot);       // 把dot添加到线性布局中
        }
    }

    /**
     * 轮播图-根据位置设置小圆点
     *
     * @param position
     */
    private void changeDescAndDot(int position) {
        // 显示position位置的文字描述
        mDesc.setText(mCarouselList.get(position).getObjectTitle());
        // 把position位置的点设置为selected状态
        for (int i = 0; i < mDots.getChildCount(); i++) {
            mDots.getChildAt(i).setSelected(i == position);
        }
    }

    /**
     * 轮播图-显示下一页
     */
    public void showNextPage() {
        if (mInformationAd != null) {
            mInformationAd.setCurrentItem(mInformationAd.getCurrentItem() + 1);
            mHandler.sendEmptyMessageDelayed(SHOW_NEXT_PAGE, 5000);
        }
    }

    @Override
    public void onRefresh() {
        //刷新资讯
        refreshInformation();
        //重新初始化轮播图
        initAdContent();
    }

    /**
     * 刷新资讯
     */
    private void refreshInformation() {
        getBuild(1).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.d("刷新资讯E：" + e.getMessage());
                if (InformationFragment.this.isAdded()) {
                    //防止Fragment点击报空指针
                    Util.showTimeOutNotice(getActivity());
                }
            }

            @Override
            public void onResponse(String response, int id) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // 停止刷新
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 1000);
                LogUtil.d("刷新资讯：" + response);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        mPage = 2;
                        PagedResultBean pagedResult = JSON.parseObject(response, InformationBean
                                .class).getPagedResult();
                        mTotalPage = pagedResult.getTotalPage();
                        //获取资讯列表
                        if (mPagedList != null) {
                            mPagedList.clear();
                        }
                        mPagedList = pagedResult.getPagedList();
                        mInformationAdapter = new InformationAdapter(getContext(), mPagedList);
                        mLv_information.setAdapter(mInformationAdapter);
                        break;
                    case "failure":
                        Util.showError(getActivity(), errorBean.getReason());
                        break;
                }
            }
        });
    }

    /**
     * 条目点击，进入资讯详情
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mLv_information.getHeaderViewsCount() != 0) {
            position -= 1;
        }
        int news_id = mPagedList.get(position).getId();
        //更改阅读状态
        mPagedList.get(position).setReaded(true);
        mInformationAdapter.notifyDataSetChanged();

        List<ImageListBean> imageList = mPagedList.get(position).getImageList();
        String imageUrl;
        if (imageList.size() != 0) {
            imageUrl = imageList.get(0).getImageUrl();
        } else {
            imageUrl = Constant.DEFAULT_IMAGE;
        }
        Intent intent = new Intent(getContext(), InformationDetailActivity.class);
        intent.putExtra("newsId", news_id);
        intent.putExtra("imageUrl", imageUrl);
        startActivity(intent);
    }

    /**
     * 点击广告轮播图
     *
     * @param position 处理过的position
     */
    @Override
    public void onAdClick(int position) {
        if (mCarouselList != null && mCarouselList.size() != 0) {
            Carousel carousel = mCarouselList.get(position);
            int newsId = carousel.getObjectId();
            String imageUrl = carousel.getMediaUrl();
            Intent intent = new Intent(getContext(), InformationDetailActivity.class);
            intent.putExtra("newsId", newsId);
            intent.putExtra("imageUrl", imageUrl);
            startActivity(intent);
        }
    }


    /**
     * 下拉加载更多
     */
    @Override
    public void onLoadMore() {
        if (mPage <= mTotalPage) {
            getBuild(mPage).execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    LogUtil.d("资讯列表E：" + e.getMessage());
                    if (InformationFragment.this.isAdded()) {
                        //防止Fragment点击报空指针
                        Util.showTimeOutNotice(getActivity());
                    }
                }

                @Override
                public void onResponse(String response, int id) {
                    LogUtil.d("资讯列表：" + response);
                    ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                    switch (errorBean.getStatus()) {
                        case "success":
                            mPage++;
                            mPagedList.addAll(JSON.parseObject(response, InformationBean.class)
                                    .getPagedResult().getPagedList());
                            mInformationAdapter.notifyDataSetChanged();
                            break;
                        case "failure":
                            Util.showError(getActivity(), errorBean.getReason());
                            break;
                    }
                    mLv_information.onLoadMoreComplete();
                }
            });
        } else {
            mLv_information.noMore();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.information_search:
                startActivity(new Intent(getContext(), SearchActivity.class));
                break;
            case R.id.information_live_radio:
                startActivity(new Intent(getContext(), LiveActivity.class));
                break;
        }
    }

    @Override
    public void onPageSelected(int position) {
        LogUtil.d("Information-position---" + position);
        changeDescAndDot(position % mCarouselList.size());
    }

    private RequestCall getBuild(int page) {
        return OkHttpUtils.post().url(URL_NEWS_LIST).addParams(PAGE, page + "")
                .addParams(PAGE_SIZE, "10").build();
    }

    @Override
    public void onStop() {
        super.onStop();
        mHandler.removeMessages(SHOW_NEXT_PAGE);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
