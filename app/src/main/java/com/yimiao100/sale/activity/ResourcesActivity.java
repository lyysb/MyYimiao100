package com.yimiao100.sale.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.blankj.utilcode.util.LogUtils;
import com.squareup.picasso.Picasso;
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
import com.yimiao100.sale.utils.ScreenUtil;
import com.yimiao100.sale.utils.Util;
import com.yimiao100.sale.view.RegionSearchView;
import com.yimiao100.sale.view.TitleView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.bingoogolapple.bgabanner.BGABanner;
import okhttp3.Call;

import static com.yimiao100.sale.ext.JSON.parseObject;


/**
 * 资源-资源列表
 */
public class ResourcesActivity extends BaseActivitySingleList implements CarouselUtil
        .HandleCarouselListener, RegionSearchView.onSearchClickListener {


    private final String URL_RESOURCE_LIST = Constant.BASE_URL + "/api/resource/resource_list";

    private ArrayList<ResourceListBean> mResourcesList;
    private ResourcesAdapter mResourcesAdapter;
    private HashMap<String, String> mRegionIDs = new HashMap<>();
    private BGABanner adBanner;

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
        titleView.setTitle("疫苗");
    }

    @Override
    protected void onStart() {
        super.onStart();
        adBanner.startAutoPlay();
    }

    /**
     * 初始化头部View
     */
    private void initHeadView() {
        View view = View.inflate(this, R.layout.head_resources, null);
        mListView.addHeaderView(view, null, false);
        RegionSearchView regionSearchView = (RegionSearchView) view.findViewById(R.id.resource_search);
        adBanner = (BGABanner) view.findViewById(R.id.resource_banner);
        regionSearchView.setOnSearchClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        //获取轮播图数据
        CarouselUtil.getCarouselList(this, "vaccine", this);
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
                        LogUtil.d("nextPage is " + page + ", totalPage is " + totalPage);
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
        adBanner.stopAutoPlay();
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
                        page++;
                        LogUtil.d("nextPage is " + page + ", totalPage is " + totalPage);
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
        adBanner.setAdapter((banner, itemView, model, position) ->
                Picasso.with(getCurrentContext())
                        .load(((Carousel) model).getMediaUrl())
                        .placeholder(R.mipmap.ico_default_bannner)
                        .resize(ScreenUtil.getScreenWidth(getCurrentContext()), DensityUtil.dp2px(getCurrentContext(), 99))
                        .into((ImageView) itemView));
        List<String> desc = new ArrayList<>();
        for (Carousel carousel : carouselList) {
            desc.add(carousel.getObjectTitle());
        }
        adBanner.setData(carouselList, desc);
        adBanner.setDelegate((banner, itemView, model, position) -> {
            if (TextUtils.equals(((Carousel) model).getPageJumpUrl(), "")) {
                LogUtils.d("You Jump, I Jump");
//                JumpActivity.start(getCurrentContext(), ((Carousel) model).getPageJumpUrl());
            } else {
                LogUtils.d("Fuck your DD");
            }
            LogUtils.d(((Carousel) model).getPageJumpUrl());
        });
    }
}