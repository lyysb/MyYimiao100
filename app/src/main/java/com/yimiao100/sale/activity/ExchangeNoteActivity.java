package com.yimiao100.sale.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.alibaba.fastjson.JSON;
import com.yimiao100.sale.adapter.listview.NoteListAdapter;
import com.yimiao100.sale.base.BaseActivitySingleList;
import com.yimiao100.sale.bean.ErrorBean;
import com.yimiao100.sale.bean.Goods;
import com.yimiao100.sale.bean.GoodsBean;
import com.yimiao100.sale.bean.GoodsResult;
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
 * 积分兑换记录
 */
public class ExchangeNoteActivity extends BaseActivitySingleList {

    private final String URL_EXCHANGE_LIST = Constant.BASE_URL + "/api/mall/goods_exchange_list";

    private ArrayList<Goods> mGoodsList;
    private NoteListAdapter mNoteListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void setTitle(TitleView titleView) {
        titleView.setTitle("兑换记录");
    }



    @Override
    protected void onRefresh() {
        getBuild(1).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.d("兑换列表E：" + e.getLocalizedMessage());
                Util.showTimeOutNotice(currentContext);
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.d("兑换列表：" + response);
                mSwipeRefreshLayout.setRefreshing(false);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        GoodsResult pagedResult = JSON.parseObject(response, GoodsBean.class)
                                .getPagedResult();
                        mPage = 2;
                        mTotalPage = pagedResult.getTotalPage();
                        mGoodsList = pagedResult.getPagedList();
                        handleEmptyData(mGoodsList);
                        mNoteListAdapter = new NoteListAdapter(mGoodsList);
                        mListView.setAdapter(mNoteListAdapter);
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
                LogUtil.d("兑换列表E：" + e.getLocalizedMessage());
                Util.showTimeOutNotice(currentContext);
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.d("兑换列表：" + response);
                mListView.onLoadMoreComplete();
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        mPage++;
                        mGoodsList.addAll(JSON.parseObject(response, GoodsBean.class).getPagedResult().getPagedList());
                        mNoteListAdapter.notifyDataSetChanged();
                        break;
                    case "failure":
                        Util.showError(currentContext, errorBean.getReason());
                        break;
                }
            }
        });
    }
    private RequestCall getBuild(int page) {
        return OkHttpUtils.post().url(URL_EXCHANGE_LIST).addHeader(ACCESS_TOKEN, mAccessToken)
                .addParams(PAGE, page + "").addParams(PAGE_SIZE, mPageSize).build();
    }
    @Override
    protected void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
