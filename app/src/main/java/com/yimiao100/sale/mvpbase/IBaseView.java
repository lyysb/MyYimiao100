package com.yimiao100.sale.mvpbase;

/**
 * MVP-View
 */
public interface IBaseView<P extends IBasePresenter> {

    void showProgress();

    void hideProgress();

    void showFailureInfo(int reason);

    void onError(String errorMsg);

}
