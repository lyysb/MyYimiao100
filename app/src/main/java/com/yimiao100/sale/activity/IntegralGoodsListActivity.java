package com.yimiao100.sale.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.alibaba.fastjson.JSON;
import com.yimiao100.sale.adapter.listview.GoodsListAdapter;
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
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;

import okhttp3.Call;

/**
 * 积分商城商品列表
 */
public class IntegralGoodsListActivity extends BaseActivitySingleList {

    private final String URL_GOODS_LIST = Constant.BASE_URL + "/api/mall/goods_list";
    private final String CATEGORY_ID = "categoryId";
    private final String INTEGRAL_BEGIN = "integralBegin";
    private final String INTEGRAL_END = "integralEnd";
    private boolean FILTER = false;                     //记录是否设置筛选

    private int mCategoryId;
    private String mIntegralBegin;
    private String mIntegralEnd;
    private ArrayList<Goods> mGoodsList;
    private String mCategoryName;
    private GoodsListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        mCategoryId = intent.getIntExtra("categoryId", -1);
        mCategoryName = intent.getStringExtra("categoryName");
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void setTitle(TitleView titleView) {
        titleView.setTitle(mCategoryName);
    }

    @Override
    protected void onLoadMore() {
        AllDataLoadMore();
        if (FILTER) {
            // TODO: 2016/11/10 筛选之后的加载更多
        } else {
            //todo 全部加载更多
        }
    }

    /**
     * 全部数据加载更多
     */
    private void AllDataLoadMore() {
        getPostFormBuilder(mPage).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Util.showTimeOutNotice(currentContext);
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.Companion.d("商品列表：" + response);
                mListView.onLoadMoreComplete();
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        GoodsResult pagedResult = JSON.parseObject(response, GoodsBean.class)
                                .getPagedResult();
                        mPage ++;
                        mGoodsList.addAll(pagedResult.getPagedList());
                        mAdapter.notifyDataSetChanged();
                        break;
                    case "failure":
                        Util.showError(IntegralGoodsListActivity.this, errorBean.getReason());
                        break;
                }
            }
        });
    }

    @Override
    protected void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, CommodityDetailActivity.class);
        Goods goods = mGoodsList.get(position);
        intent.putExtra("goods", goods);
        startActivity(intent);
    }

    @Override
    protected void onRefresh() {
        //暂定为不筛选
        getPostFormBuilder(1).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.Companion.d("商品列表E：" + e.getLocalizedMessage());
                Util.showTimeOutNotice(currentContext);
            }

            @Override
            public void onResponse(String response, int id) {
                mSwipeRefreshLayout.setRefreshing(false);
                LogUtil.Companion.d("商品列表：" + response);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        GoodsResult pagedResult = JSON.parseObject(response, GoodsBean.class)
                                .getPagedResult();
                        mPage = 2;
                        mTotalPage = pagedResult.getTotalPage();
                        mGoodsList = pagedResult.getPagedList();
                        //处理空数据
                        handleEmptyData(mGoodsList);
                        mAdapter = new GoodsListAdapter(mGoodsList);
                        mListView.setAdapter(mAdapter);
                        break;
                    case "failure":
                        Util.showError(IntegralGoodsListActivity.this, errorBean.getReason());
                        break;
                }
            }
        });
        if (FILTER) {
            // TODO: 2016/11/10 筛选之后的刷新
        } else {
            // TODO: 2016/11/10 全部刷新
        }
    }

    @Override
    public void rightOnClick() {
        super.rightOnClick();
        // TODO: 2016/11/10 显示筛选条件
    }

    private PostFormBuilder getPostFormBuilder(int page) {
        return OkHttpUtils.post().url(URL_GOODS_LIST).addHeader(ACCESS_TOKEN, mAccessToken)
                .addParams(CATEGORY_ID, mCategoryId + "").addParams(PAGE, page + "")
                .addParams(PAGE_SIZE, mPageSize);
    }
}
