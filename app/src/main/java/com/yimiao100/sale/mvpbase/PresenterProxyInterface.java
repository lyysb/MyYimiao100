package com.yimiao100.sale.mvpbase;

/**
 * 代理接口
 * Created by Michel on 2017/12/18.
 */

public interface PresenterProxyInterface<V extends IBaseView<P>, P extends IBasePresenter<V>> {

    /**
     * 设置创建Presenter的工厂
     * @param presenterFactory PresenterFactory类型
     */
    void setPresenterFactory(PresenterFactory<V, P> presenterFactory);

    /**
     * 获取Presenter的工厂类
     * @return 返回PresenterMvpFactory类型
     */
    PresenterFactory<V, P> getPresenterFactory();

    /**
     * 获取创建的Presenter
     * @return 指定类型的Presenter
     */
    P getPresenter();
}
