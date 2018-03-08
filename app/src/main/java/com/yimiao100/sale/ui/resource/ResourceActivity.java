package com.yimiao100.sale.ui.resource;


import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.squareup.picasso.Picasso;
import com.yimiao100.sale.R;
import com.yimiao100.sale.activity.JumpActivity;
import com.yimiao100.sale.activity.ResourcesDetailActivity;
import com.yimiao100.sale.adapter.listview.ResourcesAdapter;
import com.yimiao100.sale.bean.Carousel;
import com.yimiao100.sale.bean.ResourceListBean;
import com.yimiao100.sale.bean.ResourceResultBean;
import com.yimiao100.sale.mvpbase.BaseActivity;
import com.yimiao100.sale.mvpbase.CreatePresenter;
import com.yimiao100.sale.utils.DensityUtil;
import com.yimiao100.sale.utils.ScreenUtil;
import com.yimiao100.sale.utils.ToastUtil;
import com.yimiao100.sale.view.RegionSearchView;
import com.yimiao100.sale.view.TitleView;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.bgabanner.BGABanner;

/**
 * MVP-疫苗列表
 */
@CreatePresenter(ResourcePresenter.class)
public class ResourceActivity extends BaseActivity<ResourceContract.View, ResourceContract.Presenter> implements ResourceContract.View{

    private ListView listView;
    private BGABanner adBanner;
    private View emptyView;
    private ArrayList<ResourceListBean> resourcesList;
    private int page;
    private int totalPage;
    private TwinklingRefreshLayout refreshLayout;
    private ResourcesAdapter adapter;

    public static void start(Context context) {
        context.startActivity(new Intent(context, ResourceActivity.class));
    }

    @Override
    protected void init() {
        super.init();

        initView();

        showProgress();
    }

    /**
     * 初始化View
     */
    private void initView() {
        // TitleView
        TitleView titleView = (TitleView) findViewById(R.id.resource_title);
        titleView.setOnTitleBarClick(new TitleView.TitleBarOnClickListener() {
            @Override
            public void leftOnClick() {
                finish();
            }

            @Override
            public void rightOnClick() {

            }
        });
        // EmptyView
        emptyView = findViewById(R.id.resource_empty);
        TextView emptyText = (TextView) emptyView.findViewById(R.id.empty_text);
        emptyText.setText(getString(R.string.empty_view_resources));
        emptyText.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.ico_resources), null, null);
        // TwinklingRefreshLayout
        refreshLayout = (TwinklingRefreshLayout) findViewById(R.id.resource_refresh);
        refreshLayout.setFloatRefresh(true);
        refreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                super.onRefresh(refreshLayout);
                getPresenter().refreshList();
            }

            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                super.onLoadMore(refreshLayout);
                if (page <= totalPage) {
                    getPresenter().loadMoreData(page);
                } else {
                    refreshLayout.finishLoadmore();
                    ToastUtils.showShort("全部加载完成");
                }
            }
        });
        // ListView
        listView = (ListView) findViewById(R.id.resource_list);
        listView.setOnItemClickListener(this::navigateToResourceDetail);
        // HeaderView
        View view = View.inflate(this, R.layout.head_resources, null);
        listView.addHeaderView(view, null, false);
        // AdView
        adBanner = (BGABanner) view.findViewById(R.id.resource_banner);
        // RegionSearchView
        RegionSearchView regionSearchView = (RegionSearchView) view.findViewById(R.id.resource_search);
        regionSearchView.setOnSearchClickListener(getPresenter()::resourceSearch);
    }

    @Override
    protected void onResume() {
        super.onResume();
        adBanner.startAutoPlay();
    }

    @Override
    protected void onPause() {
        super.onPause();
        adBanner.stopAutoPlay();
    }

    /**
     * 展示Ad数据
     * @param carouselList
     */
    @Override
    public void initAdSuccess(ArrayList<Carousel> carouselList) {
        adBanner.setAdapter((banner, itemView, model, position) ->
                Picasso.with(this)
                        .load(((Carousel) model).getMediaUrl())
                        .placeholder(R.mipmap.ico_default_bannner)
                        .resize(ScreenUtil.getScreenWidth(this), DensityUtil.dp2px(this, 99))
                        .into((ImageView) itemView));
        List<String> desc = new ArrayList<>();
        for (Carousel carousel : carouselList) {
            desc.add(carousel.getObjectTitle());
        }
        adBanner.setData(carouselList, desc);
        adBanner.setDelegate((banner, itemView, model, position) -> {
            LogUtils.d(((Carousel) model).getPageJumpUrl());
            if (((Carousel) model).getPageJumpUrl() == null) {
                return;
            }
            if (TextUtils.equals(((Carousel) model).getPageJumpUrl(), "")) {
                return;
            }
            JumpActivity.start(this, ((Carousel) model).getPageJumpUrl());
            LogUtils.d(((Carousel) model).getPageJumpUrl());
        });
    }

    /**
     * 初始化列表
     * @param resourceResult
     */
    @Override
    public void initListSuccess(ResourceResultBean resourceResult) {
        page = 2;
        totalPage = resourceResult.getTotalPage();
        // 设置Adapter
        resourcesList = resourceResult.getResourcesList();
        adapter = new ResourcesAdapter(getApplicationContext(), resourcesList);
        listView.setAdapter(adapter);
        // 处理空View
        handleEmptyData(resourcesList);
        // 停止刷新
        refreshLayout.finishRefreshing();
    }

    /**
     * 增加更多
     * @param resourceResult
     */
    @Override
    public void loadMoreSuccess(ResourceResultBean resourceResult) {
        page++;
        resourcesList.addAll(resourceResult.getResourcesList());
        adapter.notifyDataSetChanged();
        refreshLayout.finishLoadmore();
    }

    @Override
    public void showFailureInfo(int reason) {
        super.showFailureInfo(reason);
        refreshLayout.finishRefreshing();
        refreshLayout.finishLoadmore();
    }

    @Override
    public void onError(String errorMsg) {
        super.onError(errorMsg);
        refreshLayout.finishRefreshing();
        refreshLayout.finishLoadmore();
    }

    private void handleEmptyData(ArrayList<?> list) {
        if (list.size() == 0) {
            //显示emptyView
            emptyView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void navigateToResourceDetail(AdapterView<?> parent, View view, int position, long id) {
        // 跳入详情
        int resourceID = resourcesList.get(position - 1).getId();
        ResourcesDetailActivity.start(this, resourceID);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_resource;
    }
}
