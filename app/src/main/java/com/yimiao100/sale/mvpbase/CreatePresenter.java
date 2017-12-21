package com.yimiao100.sale.mvpbase;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 创建Presenter的注解
 * Created by Michel on 2017/12/18.
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface CreatePresenter {

    Class<? extends IBasePresenter> value();
}
