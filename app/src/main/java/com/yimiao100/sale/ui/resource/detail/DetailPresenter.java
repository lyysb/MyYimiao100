package com.yimiao100.sale.ui.resource.detail;

import com.yimiao100.sale.mvpbase.BasePresenter;

/**
 * 资源详情的Presenter
 * Created by Michel on 2018/3/19.
 */

public class DetailPresenter extends BasePresenter<DetailContract.View> implements DetailContract.Presenter{

    private final DetailModel model;

    public DetailPresenter() {
        model = new DetailModel(this);
    }

    /**
     * 获取资源的推广协议
     */
    @Override
    public void requestPolicyContent(int resourceId) {
        model.requestPolicyContent(resourceId);
    }

    @Override
    public void requestSuccess(String policyContent) {
        getView().showPolicyContent(policyContent);
    }

    @Override
    public void requestFailure(int reason) {
        getView().showFailureInfo(reason);
    }
}
