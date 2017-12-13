package com.yimiao100.sale.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;

import com.yimiao100.sale.R;
import com.yimiao100.sale.adapter.listview.ResourcesAdapter;
import com.yimiao100.sale.adapter.peger.ResourceAdAdapter;
import com.yimiao100.sale.base.BaseActivitySingleList;
import com.yimiao100.sale.bean.Carousel;
import com.yimiao100.sale.bean.ErrorBean;
import com.yimiao100.sale.bean.ResourceBean;
import com.yimiao100.sale.bean.ResourceListBean;
import com.yimiao100.sale.bean.ResourceResultBean;
import com.yimiao100.sale.utils.CarouselUtil;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.DensityUtil;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.Util;
import com.yimiao100.sale.view.RegionSearchView;
import com.yimiao100.sale.view.TitleView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;

import static com.yimiao100.sale.ext.JSON.parseObject;


/**
 * 资源-资源列表
 */
public class ResourcesActivity extends BaseActivitySingleList implements CarouselUtil
        .HandleCarouselListener, RegionSearchView.onSearchClickListener {


    private final String URL_RESOURCE_LIST = Constant.BASE_URL + "/api/resource/resource_list";

    private ArrayList<ResourceListBean> mResourcesList;

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
    private ViewPager mResources_view_pager;
    private ResourcesAdapter mResourcesAdapter;
    private HashMap<String, String> mRegionIDs = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        showLoadingProgress();
        super.onCreate(savedInstanceState);
        setEmptyView(getString(R.string.empty_view_resources), R.mipmap.ico_resources);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup
                .LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        // 145是HeaderView的高度
        layoutParams.setMargins(0, DensityUtil.dp2px(this, 145), 0, 0);
        mEmptyView.setLayoutParams(layoutParams);
        initHeadView();
    }

    @Override
    protected void initView() {
        super.initView();
        //更改ListView分割线宽度
        mListView.setDividerHeight(DensityUtil.dp2px(this, 3));
    }

    @Override
    protected void setTitle(TitleView titleView) {
        titleView.setTitle("资源");
    }

    @Override
    protected void onStart() {
        super.onStart();
        mHandler.sendEmptyMessageDelayed(SHOW_NEXT_PAGE, 3000);
    }

    /**
     * 初始化头部View
     */
    private void initHeadView() {
        View view = View.inflate(this, R.layout.head_resources, null);
        mListView.addHeaderView(view, null, false);
        RegionSearchView regionSearchView = (RegionSearchView) view.findViewById(R.id.resource_search);
        mResources_view_pager = (ViewPager) view.findViewById(R.id.resources_view_pager);
        regionSearchView.setOnSearchClickListener(this);
        //获取轮播图数据
        CarouselUtil.getCarouselList(this, "vaccine", this);
    }

    @Override
    protected void initData() {
        super.initData();
    }


    @Override
    public void regionSearch(@NotNull HashMap<String, String> regionIDs) {
        mRegionIDs = regionIDs;
        showLoadingProgress();
        onRefresh();
    }

    private RequestCall getBuild(int page) {
        return OkHttpUtils.post().url(URL_RESOURCE_LIST).addHeader(ACCESS_TOKEN, accessToken)
                .params(mRegionIDs)
                .addParams(PAGE, page + "")
                .addParams(PAGE_SIZE, pageSize)
                .build();
    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //获取Item的资源信息，携带resourceId进入资源详情界面
        int resourceID = mResourcesList.get(position - 1).getId();
        Intent intent = new Intent(this, ResourcesDetailActivity.class);
        //封装resourceID
        intent.putExtra("resourceID", resourceID);
        //进入详情页
        startActivity(intent);
    }

    /**
     * 自动切换下一页图片
     */
    public void showNextPage() {
        mResources_view_pager.setCurrentItem(mResources_view_pager.getCurrentItem() + 1);
        mHandler.sendEmptyMessageDelayed(SHOW_NEXT_PAGE, 3000);
    }


    @Override
    public void onRefresh() {
        //刷新列表
        getBuild(1).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.d("资源列表E：" + e.getMessage());
                Util.showTimeOutNotice(currentContext);
                hideLoadingProgress();
            }

            @Override
            public void onResponse(String response, int id) {
                mSwipeRefreshLayout.setRefreshing(false);
                LogUtil.d("资源列表：" + response);
                hideLoadingProgress();
                ErrorBean errorBean = parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        page = 2;
                        ResourceResultBean resourceResult = parseObject(response,
                                ResourceBean.class).getResourceResult();
                        totalPage = resourceResult.getTotalPage();
                        LogUtil.d("nextPage is " + page + ", totalPage is " + totalPage) ;
                        //解析JSON，填充Adapter
                        //获取资源列表
                        mResourcesList = resourceResult.getResourcesList();
                        handleEmptyData(mResourcesList);
                        //填充Adapter
                        mResourcesAdapter = new ResourcesAdapter(getApplicationContext(),
                                mResourcesList);
                        mListView.setAdapter(mResourcesAdapter);
                        break;
                    case "failure":
                        Util.showError(currentContext, errorBean.getReason());
                        break;
                }
            }
        });
    }


    @Override
    protected void onStop() {
        super.onStop();
        mHandler.removeMessages(SHOW_NEXT_PAGE);
    }

    @Override
    protected void onLoadMore() {
        getBuild(page).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.d("资源列表E：" + e.getMessage());
                Util.showTimeOutNotice(currentContext);
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.d("资源列表：" + response);
                ErrorBean errorBean = parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        page ++;
                        LogUtil.d("nextPage is " + page + ", totalPage is " + totalPage) ;
                        mResourcesList.addAll(parseObject(response, ResourceBean
                                .class).getResourceResult().getResourcesList());
                        mResourcesAdapter.notifyDataSetChanged();
                        break;
                    case "failure":
                        Util.showError(currentContext, errorBean.getReason());
                        break;
                }
                mListView.onLoadMoreComplete();
            }
        });
    }


    /**
     * 处理轮播图数据
     *
     * @param carouselList
     */
    @Override
    public void handleCarouselList(ArrayList<Carousel> carouselList) {
        mResources_view_pager.setAdapter(new ResourceAdAdapter(carouselList));
        mResources_view_pager.setCurrentItem(mResources_view_pager.getAdapter().getCount() / 2);
        mResources_view_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int
                    positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                LogUtil.d("资源position------" + position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}