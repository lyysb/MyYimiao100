package com.yimiao100.sale.mvpbase;

import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * MVP-Presenter
 * 绑定的View必须继承自IBaseView
 */
public interface IBasePresenter<V extends IBaseView> {

    /**
     * Presenter被创建后调用
     * @param savedState 被意外销毁后重建后的Bundle
     */
    void onCreatePresenter(@Nullable Bundle savedState);

    /**
     * 绑定View
     * @param view
     */
    void onAttachView(V view);

    /**
     * 解除绑定View
     */
    void onDetachView();

    /**
     * Presenter被销毁时调用
     */
    void onDestroyPresenter();

    /**
     * 在Presenter意外销毁的时候被调用，它的调用时机和Activity、Fragment、View中的onSaveInstanceState
     * 时机相同
     * @param outState
     */
    void onSaveInstanceState(Bundle outState);


    /**
     * 获取V层接口View
     * @return 返回当前View
     */
    V getView();

    void onError(String msg);
}
