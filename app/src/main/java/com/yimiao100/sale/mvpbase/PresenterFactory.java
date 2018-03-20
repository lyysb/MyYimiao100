package com.yimiao100.sale.mvpbase;

/**
 * Presenter工厂接口
 * Created by Michel on 2017/12/18.
 */

public interface PresenterFactory<V extends IBaseView<P>, P extends IBasePresenter<V>> {

    /**
     * 创建Presenter的接口方法
     * @return 需要创建的Presenter
     */
    P createPresenter();
}