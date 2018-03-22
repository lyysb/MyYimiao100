package com.yimiao100.sale.ui.business.vaccine;

import com.yimiao100.sale.bean.ResourceResultBean;
import com.yimiao100.sale.mvpbase.BasePresenter;

/**
 * 疫苗-我的业务列表-主持
 * Created by Michel on 2018/3/20.
 */

public class BusinessPresenter extends BasePresenter<BusinessContract.View> implements BusinessContract.Presenter{

    private final BusinessModel model;

    public BusinessPresenter() {
        model = new BusinessModel(this);
    }

    @Override
    public void initList(String userAccountType, String vendorId) {
        model.initList(userAccountType, vendorId);
    }

    @Override
    public void initSuccess(ResourceResultBean result) {
        getView().hideProgress();
        getView().initSuccess(result);
        getView().stopRefreshing();
    }

    @Override
    public void initFailure(int reason) {
        getView().hideProgress();
        getView().showFailureInfo(reason);
        getView().stopRefreshing();
    }

    @Override
    public void refreshList(String userAccountType, String vendorId) {
        model.refreshList(userAccountType, vendorId);
    }

    @Override
    public void refreshSuccess(ResourceResultBean result) {
        getView().initSuccess(result);
        getView().stopRefreshing();
    }

    @Override
    public void refreshFailure(int reason) {
        getView().showFailureInfo(reason);
        getView().stopRefreshing();
    }

    @Override
    public void loadMoreList(String userAccountType, String vendorId, int page) {
        model.loadMoreList(userAccountType, vendorId, page);
    }

    @Override
    public void loadMoreSuccess(ResourceResultBean result) {
        getView().stopLoadMore();
        getView().loadMoreSuccess(result);
    }

    @Override
    public void loadMoreFailure(int reason) {
        getView().stopLoadMore();
        getView().showFailureInfo(reason);
    }
}
