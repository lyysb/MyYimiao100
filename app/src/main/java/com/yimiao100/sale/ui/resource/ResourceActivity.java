package com.yimiao100.sale.ui.resource;


import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.bigkoo.pickerview.OptionsPickerView;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.yimiao100.sale.R;
import com.yimiao100.sale.activity.JumpActivity;
import com.yimiao100.sale.activity.ResourcesDetailActivity;
import com.yimiao100.sale.adapter.listview.ResourcesAdapter;
import com.yimiao100.sale.bean.Carousel;
import com.yimiao100.sale.bean.CategoryFilter;
import com.yimiao100.sale.bean.ResourceListBean;
import com.yimiao100.sale.bean.ResourceResultBean;
import com.yimiao100.sale.bean.VendorFilter;
import com.yimiao100.sale.glide.ImageLoad;
import com.yimiao100.sale.mvpbase.BaseActivity;
import com.yimiao100.sale.mvpbase.CreatePresenter;
import com.yimiao100.sale.ui.resource.detail.DetailActivity;
import com.yimiao100.sale.view.RegionSearchView;
import com.yimiao100.sale.view.SelectAllView;
import com.yimiao100.sale.view.TitleView;

import java.util.ArrayList;
import java.util.HashMap;
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
    // 记录选中的资源列表
    private SparseArray<ResourceListBean> resources = new SparseArray<>();
    private SelectAllView selectAllView;
    private TextView tvVendorFilter;
    private TextView tvCategoryFilter;
    private OptionsPickerView resourceFilter;
    private ArrayList<VendorFilter> vendorFilters;
    private ArrayList<CategoryFilter> categoryFilters;
    private HashMap<String, String> searchIds = new HashMap<>();

    public static void start(Context context) {
        context.startActivity(new Intent(context, ResourceActivity.class));
    }

    @Override
    protected void init() {
        super.init();

        initView();

        showProgress();

        initData();
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
//        listView.setOnItemClickListener(this::navigateToResourceDetail);
        // HeaderView
        View view = View.inflate(this, R.layout.head_vaccine, null);
        listView.addHeaderView(view, null, false);
        // AdView
        adBanner = (BGABanner) view.findViewById(R.id.resource_banner);
        // Filter
        // 初始化选择器
        resourceFilter = new OptionsPickerView.Builder(this, this::filterSelect)
                .setContentTextSize(16)
                .setSubCalSize(14)
                .setSubmitColor(getResources().getColor(R.color.colorMain))
                .setCancelColor(getResources().getColor(R.color.colorMain))
                .setOutSideCancelable(false)
                .build();
        tvVendorFilter = (TextView) view.findViewById(R.id.resource_filter_vendor);
        tvVendorFilter.setOnClickListener(v -> {
            // 展示厂家过滤条目
            if (vendorFilters != null) {
                resourceFilter.setPicker(vendorFilters);
                resourceFilter.show(v);
            }
        });
        tvCategoryFilter = (TextView) view.findViewById(R.id.resource_filter_category);
        tvCategoryFilter.setOnClickListener(v -> {
            // 展示产品过滤条目
            if (tvVendorFilter.getText().toString().isEmpty()) {
                ToastUtils.showShort(getString(R.string.vendor_first));
                return;
            }
            resourceFilter.setPicker(categoryFilters);
            resourceFilter.show(v);
        });
        // RegionSearchView
        RegionSearchView regionSearchView = (RegionSearchView) view.findViewById(R.id.resource_search);
        regionSearchView.setOnSearchClickListener(getPresenter()::resourceSearch);
        regionSearchView.setOnSearchClickListener(this::search);
        // SelectAllView
        selectAllView = (SelectAllView) findViewById(R.id.resource_bottom);
        selectAllView.setOnSelectAllClickListener(this::selectAllResource);
        selectAllView.setOnConfirmClickListener(this::enterDetail);
    }

    private void initData() {
        getPresenter().initData();
    }

    private void search(HashMap<String, String> regionIds) {
        searchIds.putAll(regionIds);
        getPresenter().resourceSearch(searchIds);
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
                ImageLoad.loadAd(this, ((Carousel) model).getMediaUrl(), 99, (ImageView) itemView));
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
            JumpActivity.start(this,((Carousel) model).getObjectTitle(), ((Carousel) model).getPageJumpUrl());
            LogUtils.d(((Carousel) model).getPageJumpUrl());
        });
    }

    /**
     * 初始化过滤器
     * @param vendorFilters
     */
    @Override
    public void initFilterSuccess(ArrayList<VendorFilter> vendorFilters) {
        this.vendorFilters = vendorFilters;
        resourceFilter.setPicker(vendorFilters);
    }

    private void filterSelect(int options1, int options2, int options3, View v) {
        switch (v.getId()) {
            case R.id.resource_filter_vendor:
                VendorFilter vendorFilter = vendorFilters.get(options1);
                tvVendorFilter.setText(vendorFilter.getVendorName());
                searchIds.put("vendorId", vendorFilter.getId() + "");
                if (searchIds.containsKey("categoryId")) {
                    searchIds.remove("categoryId");
                }
                tvCategoryFilter.setText("");
                categoryFilters = vendorFilter.getCategoryList();
                break;
            case R.id.resource_filter_category:
                CategoryFilter categoryFilter = categoryFilters.get(options1);
                tvCategoryFilter.setText(categoryFilter.getCategoryName());
                searchIds.put("categoryId", categoryFilter.getId() + "");
                break;
        }
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
        // 记录选中的资源
        adapter.setOnResourceSelectListener(this::selectResource);
        // 处理空View
        handleEmptyData(resourcesList);
        // 停止刷新
        refreshLayout.finishRefreshing();
        // 清空多选记录列表
        resources.clear();
        selectAllView.deselectAll();
        selectAllView.updateSelectCount("0个");
    }

    /**
     * 记录选中的资源
     * @param position
     */
    public void selectResource(CheckBox select, int position) {
        // 获取当前资源
        ResourceListBean currentItem = resourcesList.get(position);
        // 以资源id作为资源的标识，具有唯一性
        // 判定条件
        // 1、厂家列表为空，直接添加选中厂家
        // 2、厂家列表不为空
        // 2-1、当前条目为未被选中状态
        // 2-1-1、与之前厂家不同，提示，return
        // 2-1-1、与之前产品不同，提示，return
        // 2-1-1、符合条件，添加到记录
        // 2-2、当前条目为被选中状态，移除之前记录
        if (resources.size() == 0) {
            resources.put(currentItem.getId(), currentItem);
            currentItem.setChecked(true);
        } else {
            if (!currentItem.isChecked()) {
                ResourceListBean lastItem = resources.valueAt(0);
                if (!TextUtils.equals(currentItem.getVendorName(), lastItem.getVendorName())) {
                    ToastUtils.showShort(getString(R.string.vendor_same));
                    select.setChecked(false);
                    return;
                }
                if (!TextUtils.equals(currentItem.getCategoryName(), lastItem.getCategoryName())) {
                    ToastUtils.showShort(getString(R.string.category_same));
                    select.setChecked(false);
                    return;
                }
                resources.put(currentItem.getId(), currentItem);
                currentItem.setChecked(true);
            } else {
                resources.remove(currentItem.getId());
                currentItem.setChecked(false);
            }
        }
        // 更新底部统计数据
        selectAllView.updateSelectCount(resources.size() + "个");
        // 根据需要是否选中全选
        if (resources.size() == resourcesList.size()) {
            selectAllView.selectAll();
        } else {
            selectAllView.deselectAll();
        }
    }

    /**
     * 选中当前页所有数据
     */
    private void selectAllResource() {
        if (resourcesList.size() == 0) {
            ToastUtils.showShort("暂无数据");
            return;
        }
        // 如果当前是全选状态。取消全选，清空记录集合，所有Model设置未选择状态
        // 当前是未全选。逐条判断是否符合条件进行添加，根据最终结果设置是否全选
        if (selectAllView.isSelectAll()) {
            selectAllView.deselectAll();
            resources.clear();
            for (ResourceListBean item : resourcesList) {
                item.setChecked(false);
            }
        } else {
            for (int i = 0; i < resourcesList.size(); i++) {
                ResourceListBean item = resourcesList.get(i);
                if (i == 0) {
                    resources.put(item.getId(), item);
                } else {
                    // 拿到index为0的数据
                    ResourceListBean old = resources.valueAt(0);
                    if (TextUtils.equals(item.getVendorName(), old.getVendorName())
                            && TextUtils.equals(item.getCategoryName(), old.getCategoryName())) {
                        // 符合条件，添加记录
                        resources.put(item.getId(), item);
                    } else {
                        // 不符合条件，清空记录，中断循环
                        resources.clear();
                        break;
                    }
                }
            }
            if (resources.size() == resourcesList.size()) {
                selectAllView.selectAll();
                for (ResourceListBean item : resourcesList) {
                    item.setChecked(true);
                }
            } else {
                ToastUtils.showShort(getString(R.string.vaccine_all));
            }
        }
        selectAllView.updateSelectCount(resources.size() + "个");
        adapter.notifyDataSetChanged();
    }

    /**
     * 进入资源详情
     */
    private void enterDetail() {
        if (resources.size() == 0) {
            ToastUtils.showShort(getString(R.string.vaccine_at_least));
            return;
        }
        DetailActivity.start(this, resources);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 200 && data != null) {
            if (data.getExtras() != null) {
                ArrayList<Integer> keys = data.getExtras().getIntegerArrayList("keys");
                // 移除本页记录
                // 更新页面选中状态
                for (Integer key : keys) {
                    resources.remove(key);
                    for (ResourceListBean item : resourcesList) {
                        if (key == item.getId()) {
                            item.setChecked(false);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
                // 取消全选
                if (selectAllView.isSelectAll()) {
                    selectAllView.deselectAll();
                }
                // 更新选择数量
                selectAllView.updateSelectCount(resources.size() + "个");
            }

        }
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
        // 由于数据的增加，取消全选
        selectAllView.deselectAll();
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
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        getPresenter().initData();
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
