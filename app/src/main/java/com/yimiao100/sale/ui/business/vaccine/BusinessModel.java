package com.yimiao100.sale.ui.business.vaccine;

import com.yimiao100.sale.api.Api;
import com.yimiao100.sale.mvpbase.BaseModel;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 疫苗-我的业务列表-数据请求
 * Created by Michel on 2018/3/20.
 */

public class BusinessModel extends BaseModel implements BusinessContract.Model {

    private final BusinessPresenter presenter;

    public BusinessModel(BusinessPresenter presenter) {
        this.presenter = presenter;
    }

    /**
     * 初始化列表
     */
    @Override
    public void initList(String userAccountType, String vendorId) {
        Api.getInstance().requestBusinessList(accessToken, 1, "10", userAccountType, vendorId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resourceBean -> {
                    switch (resourceBean.getStatus()) {
                        case "success":
                            presenter.initSuccess(resourceBean.getResourceResult());
                            break;
                        case "failure":
                            presenter.initFailure(resourceBean.getReason());
                            break;
                    }
                }, throwable -> presenter.onError(throwable.getMessage()));
    }
}
