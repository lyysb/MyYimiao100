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

import com.blankj.utilcode.util.LogUtils;
import com.squareup.picasso.Picasso;
import com.yimiao100.sale.R;
import com.yimiao100.sale.activity.InformationDetailActivity;
import com.yimiao100.sale.activity.LiveActivity;
import com.yimiao100.sale.activity.SearchActivity;
import com.yimiao100.sale.adapter.listview.InformationAdapter;
import com.yimiao100.sale.adapter.peger.InformationAdAdapter;
import com.yimiao100.sale.base.BaseFragment;
import com.yimiao100.sale.bean.Carousel;
import com.yimiao100.sale.bean.ErrorBean;
import com.yimiao100.sale.bean.InformationBean;
import com.yimiao100.sale.bean.PagedListBean;
import com.yimiao100.sale.bean.PagedResultBean;
import com.yimiao100.sale.ext.JSON;
import com.yimiao100.sale.glide.ImageLoad;
import com.yimiao100.sale.utils.CarouselUtil;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.DensityUtil;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.ScreenUtil;
import com.yimiao100.sale.utils.Util;
import com.yimiao100.sale.view.PullToRefreshListView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.bgabanner.BGABanner;
import okhttp3.Call;

/**
 * 资讯列表界面
 * Created by 亿苗通 on 2016/8/1.
 */
public class InformationFragment extends BaseFragment implements SwipeRefreshLayout
        .OnRefreshListener, AdapterView.OnItemClickListener, PullToRefreshListView
        .OnRefreshingListener, View.OnClickListener, CarouselUtil.HandleCarouselListener {


    private SwipeRefreshLayout mSwipeRefreshLayout;
    private View mView;

    private final String URL_NEWS_LIST = Constant.BASE_URL + "/api/news/list";
    private final String PAGE = "page";
    private final String PAGE_SIZE = "pageSize";


    private PullToRefreshListView mLv_information;
    private List<PagedListBean> mPagedList;
    private InformationAdapter mInformationAdapter;

    private int mPage;
    private int mTotalPage;

    private View mHeadView;
    private BGABanner banner;


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
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (banner != null) {
            if (isVisibleToUser) {
                // 如果可见，滑动切换
                banner.startAutoPlay();
            } else {
                banner.stopAutoPlay();
            }
        }
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
        banner = (BGABanner) mHeadView.findViewById(R.id.information_banner);
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
     *
     * @param carouselList
     */
    @Override
    public void handleCarouselList(ArrayList<Carousel> carouselList) {
        if (carouselList.get(0).getMediaUrl().equals("http://oduhua0b1.bkt.clouddn" +
                ".com/banner_placeholder.png")
                && carouselList.size() == 1) {
            //不做任何操作，意味着是假数据。
        } else {
            banner.setAdapter((banner, itemView, model, position) ->
                    ImageLoad.loadAd(getContext(), ((Carousel) model).getMediaUrl(), 190, (ImageView) itemView));
            List<String> desc = new ArrayList<>();
            for (Carousel carousel : carouselList) {
                desc.add(carousel.getObjectTitle());
            }
            banner.setData(carouselList, desc);
            if (mLv_information.getHeaderViewsCount() == 0) {
                mLv_information.addHeaderView(mHeadView);
            }

            banner.setDelegate((banner, itemView, model, position) -> {
                int newsId = ((Carousel) model).getObjectId();
                Intent intent = new Intent(getContext(), InformationDetailActivity.class);
                intent.putExtra("newsId", newsId);
                startActivity(intent);
            });
        }


    }

    /**
     * 初始化资讯数据
     */
    private void initInformation() {
        getBuild(1).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                e.printStackTrace();
                LogUtil.d("资讯列表error：" + e.getMessage());
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
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.d("刷新资讯：" + response);
                mSwipeRefreshLayout.setRefreshing(false);
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

        Intent intent = new Intent(getContext(), InformationDetailActivity.class);
        intent.putExtra("newsId", news_id);
        startActivity(intent);
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

    private RequestCall getBuild(int page) {
        return OkHttpUtils.post().url(URL_NEWS_LIST).addParams(PAGE, page + "")
                .addParams(PAGE_SIZE, "10").build();
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
