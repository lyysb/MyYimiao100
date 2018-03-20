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

    }

    @Override
    public void initFailure(int reason) {

    }
}
