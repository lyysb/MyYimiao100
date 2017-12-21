package com.yimiao100.sale.mvpbase;

/**
 * Presenter工厂实现类
 * Created by Michel on 2017/12/18.
 */

public class PresenterFactoryImpl<V extends IBaseView<P>, P extends IBasePresenter<V>> implements PresenterFactory<V, P> {

    /**
     * 需要创建的Presenter的类型
     */
    private final Class<P> presenterClass;

    /**
     * 根据注解创建Presenter的工厂实现类
     * @param viewClazz 需要创建Presenter的V层实现类
     * @param <V> 当前View实现的接口类型
     * @param <P> 当前要创建的Presenter类型
     * @return 工厂类
     */
    public static <V extends IBaseView<P>, P extends IBasePresenter<V>> PresenterFactoryImpl<V, P> createFactory(Class<?> viewClazz) {
        CreatePresenter annotation = viewClazz.getAnnotation(CreatePresenter.class);
        Class<P> pClass = null;
        if (annotation != null) {
            pClass = (Class<P>) annotation.value();
        }
        return pClass == null ? null : new PresenterFactoryImpl<>(pClass);
    }

    private PresenterFactoryImpl(Class<P> presenterClass) {
        this.presenterClass = presenterClass;
    }
    @Override
    public P createPresenter() {
        try {
            return presenterClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Presenter创建失败!，检查是否声明了@CreatePresenter(xx.class)注解");
        }
    }
}
