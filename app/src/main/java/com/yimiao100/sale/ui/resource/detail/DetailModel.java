package com.yimiao100.sale.ui.resource.detail;

import com.yimiao100.sale.api.Api;
import com.yimiao100.sale.api.ApiService;
import com.yimiao100.sale.mvpbase.BaseModel;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 请求网络数据
 * Created by Michel on 2018/3/19.
 */

public class DetailModel extends BaseModel implements DetailContract.Model{

    private final DetailPresenter presenter;

    public DetailModel(DetailPresenter presenter) {
        super();
        this.presenter = presenter;
    }

    @Override
    public void requestPolicyContent(int resourceId) {
        Api.getInstance().requestVaccineDetail(accessToken, resourceId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resourceDetailBean -> {
                    switch (resourceDetailBean.getStatus()) {
                        case "success":
                            // 请求推广协议成功
                            presenter.requestSuccess(resourceDetailBean.getResourceInfo().getPolicyContent());
                            break;
                        case "failure":
                            presenter.requestFailure(resourceDetailBean.getReason());
                            break;
                    }
                }, throwable -> presenter.onError(throwable.getMessage()));
    }
}
