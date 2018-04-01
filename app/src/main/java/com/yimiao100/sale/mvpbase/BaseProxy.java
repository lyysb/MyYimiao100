package com.yimiao100.sale.mvpbase;

import android.os.Bundle;

import com.blankj.utilcode.util.LogUtils;

/**
 * 代理实现类，用来管理Presenter的生命周期，还有和view之间的关联
 */
public class BaseProxy<V extends IBaseView<P>, P extends IBasePresenter<V>> implements PresenterProxyInterface<V, P>{

    /**
     * 获取onSaveInstanceState中bundle的key
     */
    private static final String PRESENTER_KEY = "presenter_key";
    private PresenterFactory<V, P> factory;
    private P presenter;
    private Bundle bundle;
    private boolean isAttachView;


    public BaseProxy(PresenterFactory<V, P> presenterFactory) {
        factory = presenterFactory;
    }

    /**
     *  设置Presenter的工厂类,这个方法只能在创建Presenter之前调用,也就是调用getPresenter()之前，如果Presenter已经创建则不能再修改
     * @param presenterFactory PresenterFactory类型
     */
    @Override
    public void setPresenterFactory(PresenterFactory<V, P> presenterFactory) {
        if (presenter != null) {
            throw new IllegalArgumentException("这个方法只能在getMvpPresenter()之前调用，如果Presenter已经创建则不能再修改");
        }
        this.factory = presenterFactory;
    }

    /**
     * 获取Presenter的工厂类
     * @return PresenterMvpFactory类型
     */
    @Override
    public PresenterFactory<V, P> getPresenterFactory() {
        return factory;
    }

    /**
     * 获取创建的Presenter
     * @return 指定类型的Presenter
     * 如果之前创建过，而且是以外销毁则从Bundle中恢复
     */
    @Override
    public P getPresenter() {
        if (factory != null) {
            if (presenter == null) {
                presenter = factory.createPresenter();
                presenter.onCreatePresenter(bundle == null ? null : bundle.getBundle(PRESENTER_KEY));
            }
        }
        LogUtils.d("Proxy getPresenter = " + presenter);
        return presenter;
    }

    /**
     * 绑定Presenter和view
     * @param view
     */
    public void onResume(V view) {
        getPresenter();
        LogUtils.d("Proxy onResume");
        if (presenter != null && !isAttachView){
            presenter.onAttachView(view);
            isAttachView = true;
        }
    }
    /**
     * 销毁Presenter持有的View
     */
    private void onDetachView() {
        if (presenter != null && isAttachView) {
            presenter.onDetachView();
            isAttachView = false;
        }
    }

    /**
     * 销毁Presenter
     */
    public void onDestroy() {
        if (presenter != null ) {
            onDetachView();
            presenter.onDestroyPresenter();
            presenter = null;
        }
    }

    /**
     * 意外销毁的时候调用
     * @return Bundle，存入回调给Presenter的Bundle和当前Presenter的id
     */
    public Bundle onSaveInstanceState() {
        LogUtils.d("Proxy onSaveInstanceState");
        Bundle bundle = new Bundle();
        getPresenter();
        if(presenter != null){
            Bundle presenterBundle = new Bundle();
            //回调Presenter
            presenter.onSaveInstanceState(presenterBundle);
            bundle.putBundle(PRESENTER_KEY,presenterBundle);
        }
        return bundle;
    }
    /**
     * 意外关闭恢复Presenter
     * @param savedInstanceState 意外关闭时存储的Bundler
     */
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        LogUtils.d("Proxy onRestoreInstanceState");
        bundle = savedInstanceState;
    }

}
