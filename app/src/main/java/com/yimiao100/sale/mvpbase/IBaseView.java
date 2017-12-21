package com.yimiao100.sale.mvpbase;

/**
 * MVP-View
 * Created by michel on 2017/12/11.
 */
public interface IBaseView<P extends IBasePresenter> {

    void showProgress();

    void hideProgress();

    void showFailureInfo(int reason);

    void onError(String errorMsg);

}
