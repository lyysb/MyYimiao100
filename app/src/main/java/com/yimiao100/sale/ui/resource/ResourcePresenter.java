package com.yimiao100.sale.ui.resource;

import com.yimiao100.sale.bean.Carousel;
import com.yimiao100.sale.bean.ResourceResultBean;
import com.yimiao100.sale.bean.VendorFilter;
import com.yimiao100.sale.mvpbase.BasePresenter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * ResourcePresenter
 * Created by Michel on 2018/3/6.
 */

public class ResourcePresenter extends BasePresenter<ResourceContract.View> implements ResourceContract.Presenter {

    private final ResourceModel model;

    public ResourcePresenter() {
        model = new ResourceModel(this);
    }

    @Override
    public void initData() {
        model.initData();
    }

    /**
     * 初始化Ad成功，将数据交由View层展示
     * @param carouselList
     */
    @Override
    public void initAdSuccess(ArrayList<Carousel> carouselList) {
        getView().initAdSuccess(carouselList);
    }

    /**
     * 去填充过滤条件
     * @param vendorFilters
     */
    @Override
    public void initFilterSuccess(ArrayList<VendorFilter> vendorFilters) {
        getView().initFilterSuccess(vendorFilters);
    }


    /**
     * 初始化List成功，将数据交由View层展示
     * @param resourceResult
     */
    @Override
    public void initListSuccess(ResourceResultBean resourceResult) {
        getView().hideProgress();
        getView().initListSuccess(resourceResult);
    }

    /**
     * 请求数据出现错误
     * @param reason
     */
    @Override
    public void requestFailure(int reason) {
        getView().hideProgress();
        getView().showFailureInfo(reason);
    }

    /**
     * View请求刷新，由Model进行Refresh
     */
    @Override
    public void refreshList() {
        model.refreshData();
    }

    /**
     * View请求加载更多，由Model具体执行
     * @param page
     */
    @Override
    public void loadMoreData(int page) {
        model.loadMoreData(page);
    }

    /**
     * 加载更多成功，交由View展示新增数据
     * @param resourceResult
     */
    @Override
    public void loadMoreSuccess(ResourceResultBean resourceResult) {
        getView().loadMoreSuccess(resourceResult);
    }

    /**
     * View请求条件搜索，由Model具体执行
     * @param searchIds
     */
    @Override
    public void resourceSearch(HashMap<String, String> searchIds) {
        model.resourceSearch(searchIds);
    }
}
