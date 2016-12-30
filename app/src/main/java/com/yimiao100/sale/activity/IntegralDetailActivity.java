package com.yimiao100.sale.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.alibaba.fastjson.JSON;
import com.yimiao100.sale.adapter.listview.IntegralDetailAdapter;
import com.yimiao100.sale.base.BaseActivitySingleList;
import com.yimiao100.sale.bean.ErrorBean;
import com.yimiao100.sale.bean.IntegralList;
import com.yimiao100.sale.bean.IntegralListBean;
import com.yimiao100.sale.bean.IntegralListResult;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.Util;
import com.yimiao100.sale.view.TitleView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;

import java.util.ArrayList;

import okhttp3.Call;

/**
 * 积分明细
 */
public class IntegralDetailActivity extends BaseActivitySingleList {
    private final String URL_INTEGRAL_LIST = Constant.BASE_URL + "/api/integral/detail_list";

    private ArrayList<IntegralList> mIntegralLists;
    private IntegralDetailAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void setTitle(TitleView titleView) {
        titleView.setTitle("积分明细");
    }

    @Override
    protected void onRefresh() {
        getBuild(1).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.Companion.d("积分明细列表E：" + e.getLocalizedMessage());
                Util.showTimeOutNotice(currentContext);
            }

            @Override
            public void onResponse(String response, int id) {
                mSwipeRefreshLayout.setRefreshing(false);
                LogUtil.Companion.d("积分明细列表E：" + response);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        IntegralListResult pagedResult = JSON.parseObject(response,
                                IntegralListBean.class).getPagedResult();
                        mPage = 2;
                        mTotalPage = pagedResult.getTotalPage();
                        mIntegralLists = pagedResult.getPagedList();
                        handleEmptyData(mIntegralLists);
                        mAdapter = new IntegralDetailAdapter(mIntegralLists);
                        mListView.setAdapter(mAdapter);
                        break;
                    case "failure":
                        Util.showError(currentContext, errorBean.getReason());
                        break;
                }
            }
        });
    }

    @Override
    protected void onLoadMore() {
        getBuild(mPage).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.Companion.d("积分明细列表E：" + e.getLocalizedMessage());
                Util.showTimeOutNotice(currentContext);
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.Companion.d("积分明细列表E：" + response);
                mListView.onLoadMoreComplete();
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        mPage++;
                        mIntegralLists.addAll(JSON.parseObject(response,
                                IntegralListBean.class).getPagedResult().getPagedList());
                        mAdapter.notifyDataSetChanged();
                        break;
                    case "failure":
                        Util.showError(currentContext, errorBean.getReason());
                        break;
                }
            }
        });
    }

    @Override
    protected void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    private RequestCall getBuild(int page) {
        return OkHttpUtils.post().url(URL_INTEGRAL_LIST).addHeader(ACCESS_TOKEN, mAccessToken)
                .addParams(PAGE, page + "").addParams(PAGE_SIZE, mPageSize)
                .build();
    }
}
