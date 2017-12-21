package com.yimiao100.sale.mvpbase;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.blankj.utilcode.util.LogUtils;

/**
 * 真正的BasePresenter
 * Created by Michel on 2017/12/15.
 */

public class BasePresenter<V extends IBaseView> implements IBasePresenter<V> {

    private V view;

    @Override
    public void onCreatePresenter(@Nullable Bundle savedState) {
        LogUtils.d("P onCreatePresenter");
    }

    @Override
    public void onAttachView(V view) {
        LogUtils.d("P onAttachView");
        this.view = view;
    }

    @Override
    public void onDetachView() {
        LogUtils.d("P onDetachView");
        view = null;
    }

    @Override
    public void onDestroyPresenter() {
        LogUtils.d("P onDestroyPresenter");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        LogUtils.d("P onSaveInstanceState");
    }

    @Override
    public V getView() {
        return view;
    }

    /**
     * 网络请求抛出异常时调用
     * @param msg
     */
    @Override
    public void onError(String msg) {
        view.hideProgress();
        view.onError(msg);
    }
}
