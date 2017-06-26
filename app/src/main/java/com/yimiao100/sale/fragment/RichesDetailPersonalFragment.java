package com.yimiao100.sale.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;

import com.yimiao100.sale.R;
import com.yimiao100.sale.activity.RichItemDetailActivity;
import com.yimiao100.sale.adapter.listview.RichesDetailAdapter;
import com.yimiao100.sale.base.BaseFragmentSingleList;
import com.yimiao100.sale.bean.RichesDetailBean;
import com.yimiao100.sale.bean.RichesDetailList;
import com.yimiao100.sale.ext.JSON;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.Util;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;

import java.util.ArrayList;

import okhttp3.Call;

/**
 * 个人推广-财富明细
 * Created by Michel on 2017/3/2.
 */

public class RichesDetailPersonalFragment extends BaseFragmentSingleList {

    private final String URL_ACCOUNT_DETAIL_LIST = Constant.BASE_URL + "/api/fund/account_detail_list";
    private final String ACCOUNT_TYPE = "accountType";

    private String mAccountType = "personal";

    private ArrayList<RichesDetailList> mLists;
    private RichesDetailAdapter mAdapter;
    @Override
    protected String initPageTitle() {
        return "个人推广";
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setEmptyView(getString(R.string.empty_view_riches_detail), R.mipmap.ico_wealth_detailed);
    }

    @Override
    protected void onRefresh() {
        getBuild(1).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.d("personal riches detail error：" + e.getLocalizedMessage());
            }

            @Override
            public void onResponse(String response, int id) {
                mSwipeRefreshLayout.setRefreshing(false);
                LogUtil.d("personal riches detail：" + response);
                RichesDetailBean richesDetail = JSON.parseObject(response, RichesDetailBean.class);
                switch (richesDetail.getStatus()) {
                    case "success":
                        mPage = 2;
                        mTotalPage = richesDetail.getPagedResult().getTotalPage();
                        mLists = richesDetail.getPagedResult().getPagedList();
                        handleEmptyData(mLists);
                        mAdapter = new RichesDetailAdapter(mLists);
                        mListView.setAdapter(mAdapter);
                        break;
                    case "failure":
                        Util.showError(getActivity(), richesDetail.getReason());
                        break;
                }
            }
        });
    }

    @Override
    protected void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), RichItemDetailActivity.class);
        intent.putExtra("id", mLists.get(position).getId());
        startActivity(intent);
    }

    @Override
    protected void onLoadMore() {
        //加载对公数据更多
        getBuild(mPage).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.d("personal riches detail error：" + e.getLocalizedMessage());
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.d("personal riches detail ：" + response);
                RichesDetailBean richesDetail = JSON.parseObject(response, RichesDetailBean.class);
                switch (richesDetail.getStatus()) {
                    case "success":
                        mPage ++;
                        mLists.addAll(richesDetail.getPagedResult().getPagedList());
                        mAdapter.notifyDataSetChanged();
                        break;
                    case "failure":
                        Util.showError(getActivity(), richesDetail.getReason());
                        break;
                }
                mListView.onLoadMoreComplete();
            }
        });
    }

    private RequestCall getBuild(int page) {
        return OkHttpUtils.post().url(URL_ACCOUNT_DETAIL_LIST).addHeader(ACCESS_TOKEN, mAccessToken)
                .addParams(PAGE, page + "").addParams(PAGE_SIZE, mPageSize)
                .addParams(ACCOUNT_TYPE, mAccountType).build();
    }
}
