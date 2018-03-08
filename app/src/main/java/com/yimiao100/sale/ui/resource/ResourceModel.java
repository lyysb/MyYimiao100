package com.yimiao100.sale.ui.resource;

import com.blankj.utilcode.util.ActivityUtils;
import com.yimiao100.sale.api.Api;
import com.yimiao100.sale.mvpbase.BaseModel;
import com.yimiao100.sale.utils.CarouselUtil;
import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * ResourceModel
 * Created by Michel on 2018/3/6.
 */

public class ResourceModel extends BaseModel implements ResourceContract.Model {

    private final ResourcePresenter presenter;
    private HashMap<String, String> regionIds = new HashMap<>();

    public ResourceModel(ResourcePresenter presenter) {
        super();
        this.presenter = presenter;
    }

    @Override
    public void initData() {
        // 请求轮播图数据 并通过Presenter将Ad交给View层
        CarouselUtil.getCarouselList(ActivityUtils.getTopActivity(), "vaccine", presenter::initAdSuccess);
        // 请求疫苗列表数据
        Api.getInstance().requestVaccineList(accessToken, regionIds, 1, "10")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resourceBean -> {
                    switch (resourceBean.getStatus()) {
                        case "success":
                            // 通过Presenter将数据交给UI层展示数据
                            presenter.initListSuccess(resourceBean.getResourceResult());
                            break;
                        case "failure":
                            // 通过Presenter将数据交给UI层展示错误信息
                            presenter.requestFailure(resourceBean.getReason());
                            break;
                    }
                },throwable -> presenter.onError(throwable.getMessage()) );
    }

    @Override
    public void refreshData() {
        // 请求轮播图数据 并通过Presenter将Ad交给View层
        CarouselUtil.getCarouselList(ActivityUtils.getTopActivity(), "vaccine", presenter::initAdSuccess);
        // 请求疫苗列表数据
        Api.getInstance().requestVaccineList(accessToken, regionIds, 1, "10")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resourceBean -> {
                    switch (resourceBean.getStatus()) {
                        case "success":
                            // 通过Presenter将数据交给UI层展示数据
                            presenter.initListSuccess(resourceBean.getResourceResult());
                            break;
                        case "failure":
                            // 通过Presenter将数据交给UI层展示错误信息
                            presenter.requestFailure(resourceBean.getReason());
                            break;
                    }
                },throwable -> presenter.onError(throwable.getMessage()) );
    }

    @Override
    public void loadMoreData(int page) {
        Api.getInstance().requestVaccineList(accessToken, regionIds, page, "10")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resourceBean -> {
                    switch (resourceBean.getStatus()) {
                        case "success":
                            // 通过Presenter将数据交给UI层展示数据
                            presenter.loadMoreSuccess(resourceBean.getResourceResult());
                            break;
                        case "failure":
                            // 通过Presenter将数据交给UI层展示错误信息
                            presenter.requestFailure(resourceBean.getReason());
                            break;
                    }
                },throwable -> presenter.onError(throwable.getMessage()) );
    }

    @Override
    public void resourceSearch(HashMap<String, String> regionIDs) {
        this.regionIds = regionIDs;
        Api.getInstance().requestVaccineList(accessToken, regionIds, 1, "10")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resourceBean -> {
                    switch (resourceBean.getStatus()) {
                        case "success":
                            // 通过Presenter将数据交给UI层展示数据
                            presenter.initListSuccess(resourceBean.getResourceResult());
                            break;
                        case "failure":
                            // 通过Presenter将数据交给UI层展示错误信息
                            presenter.requestFailure(resourceBean.getReason());
                            break;
                    }
                },throwable -> presenter.onError(throwable.getMessage()) );
    }
}
