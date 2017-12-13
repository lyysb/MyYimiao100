package com.yimiao100.sale.base;

/**
 * MVP-View
 * Created by michel on 2017/12/11.
 */
public interface BaseView {

    void showProgress();

    void hideProgress();

    void showErrorInfo(int reason);

    void timeOut(Exception e);

}
