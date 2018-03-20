package com.yimiao100.sale.ui.resource.detail;

import com.yimiao100.sale.mvpbase.IBaseModel;
import com.yimiao100.sale.mvpbase.IBasePresenter;
import com.yimiao100.sale.mvpbase.IBaseView;

/**
 * 疫苗资源详情
 * Created by Michel on 2018/3/16.
 */

public interface DetailContract {

    interface View extends IBaseView<Presenter>{
        void showPolicyContent(String policyContent);
    }

    interface Presenter extends IBasePresenter<View>{
        void requestPolicyContent(int resourceId);

        void requestSuccess(String policyContent);

        void requestFailure(int reason);
    }

    interface Model extends IBaseModel{
        void requestPolicyContent(int resourceId);
    }
}
