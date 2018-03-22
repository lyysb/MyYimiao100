package com.yimiao100.sale.ui.business.vaccine;

import com.yimiao100.sale.bean.ResourceResultBean;
import com.yimiao100.sale.mvpbase.IBaseModel;
import com.yimiao100.sale.mvpbase.IBasePresenter;
import com.yimiao100.sale.mvpbase.IBaseView;

/**
 * 业务列表总管
 * Created by Michel on 2018/3/20.
 */

public interface BusinessContract {

    interface View extends IBaseView<Presenter>{
        void initSuccess(ResourceResultBean result);

        void stopRefreshing();

        void stopLoadMore();

        void loadMoreSuccess(ResourceResultBean result);
    }

    interface Presenter extends IBasePresenter<View>{
        void initList(String userAccountType, String vendorId);

        void initSuccess(ResourceResultBean result);

        void initFailure(int reason);

        void refreshList(String userAccountType, String vendorId);

        void refreshSuccess(ResourceResultBean result);

        void refreshFailure(int reason);

        void loadMoreList(String userAccountType, String vendorId, int page);

        void loadMoreSuccess(ResourceResultBean result);

        void loadMoreFailure(int reason);

    }

    interface Model extends IBaseModel{
        void initList(String userAccountType, String vendorId);

        void refreshList(String userAccountType, String vendorId);

        void loadMoreList(String userAccountType, String vendorId, int page);
    }
}
