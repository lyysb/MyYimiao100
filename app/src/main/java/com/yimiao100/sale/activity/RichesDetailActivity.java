package com.yimiao100.sale.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.alibaba.fastjson.JSON;
import com.yimiao100.sale.R;
import com.yimiao100.sale.adapter.listview.RichesDetailAdapter;
import com.yimiao100.sale.base.BaseActivitySingleList;
import com.yimiao100.sale.bean.RichesDetailBean;
import com.yimiao100.sale.bean.RichesDetailList;
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
 * 财富-明细列表
 */
public class RichesDetailActivity extends BaseActivitySingleList {


    private final String URL_ACCOUNT_DETAIL_LIST = Constant.BASE_URL + "/api/fund/account_detail_list";
    private final String ACCOUNT_TYPE = "accountType";

    private String mAccountType = "corporate";

    private ArrayList<RichesDetailList> mLists;
    private RichesDetailAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setEmptyView("没有进、哪有出啊，快到资源里面申请推广吧。", R.mipmap.ico_wealth_detailed);
    }

    @Override
    protected void setTitle(TitleView titleView) {
        titleView.setTitle("资金明细");
    }

    @Override
    protected void onStart() {
        super.onStart();
        onRefresh();
    }


    @Override
    protected void onRefresh() {
        getBuild(1).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.d("财富明细列表E：" + e.getLocalizedMessage());
            }

            @Override
            public void onResponse(String response, int id) {
                mSwipeRefreshLayout.setRefreshing(false);
                LogUtil.d("财富明细列表：" + response);
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
                        Util.showError(RichesDetailActivity.this, richesDetail.getReason());
                        break;
                }
            }
        });
    }

    @Override
    protected void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, RichItemDetailActivity.class);
        intent.putExtra("id", mLists.get(position).getId());
        startActivity(intent);
    }

    @Override
    protected void onLoadMore() {
        //加载对公数据更多
        getBuild(mPage).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.d("财富明细列表E：" + e.getLocalizedMessage());
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.d("财富明细列表：" + response);
                RichesDetailBean richesDetail = JSON.parseObject(response, RichesDetailBean.class);
                switch (richesDetail.getStatus()) {
                    case "success":
                        mPage ++;
                        mLists.addAll(richesDetail.getPagedResult().getPagedList());
                        mAdapter.notifyDataSetChanged();
                        break;
                    case "failure":
                        Util.showError(RichesDetailActivity.this, richesDetail.getReason());
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
