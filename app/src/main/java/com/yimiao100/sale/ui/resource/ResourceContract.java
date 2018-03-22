package com.yimiao100.sale.ui.resource;

import android.widget.AdapterView;

import com.yimiao100.sale.bean.Carousel;
import com.yimiao100.sale.bean.ResourceResultBean;
import com.yimiao100.sale.bean.VendorFilter;
import com.yimiao100.sale.mvpbase.IBaseModel;
import com.yimiao100.sale.mvpbase.IBasePresenter;
import com.yimiao100.sale.mvpbase.IBaseView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 以MVP方式重写资源List页面
 * Created by Michel on 2018/3/6.
 */

public interface ResourceContract {

    interface View extends IBaseView<Presenter> {
        void initAdSuccess(ArrayList<Carousel> carouselList);

        void initFilterSuccess(ArrayList<VendorFilter> vendorFilters);

        void initListSuccess(ResourceResultBean resourceResult);

        void loadMoreSuccess(ResourceResultBean resourceResult);

        void navigateToResourceDetail(AdapterView<?> parent, android.view.View view, int position, long id);
    }

    interface Presenter extends IBasePresenter<View>{
        void initData();

        void initAdSuccess(ArrayList<Carousel> carouselList);

        void initFilterSuccess(ArrayList<VendorFilter> vendorFilters);

        void initListSuccess(ResourceResultBean resourceResult);

        void requestFailure(int reason);

        void refreshList();

        void loadMoreData(int page);

        void loadMoreSuccess(ResourceResultBean resourceResult);

        void resourceSearch(HashMap<String, String> searchIds);
    }

    interface Model extends IBaseModel{
        void initData();

        void refreshData();

        void loadMoreData(int page);

        void resourceSearch(HashMap<String, String> searchIds);
    }
}
