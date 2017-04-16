package com.yimiao100.sale.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.yimiao100.sale.adapter.listview.IntegralShopAdapter;
import com.yimiao100.sale.base.BaseActivitySingleList;
import com.yimiao100.sale.bean.Category;
import com.yimiao100.sale.bean.CategoryBean;
import com.yimiao100.sale.bean.ErrorBean;
import com.yimiao100.sale.ext.JSON;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.DensityUtil;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.Util;
import com.yimiao100.sale.view.TitleView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;

import okhttp3.Call;

/**
 * 积分商城分类列表
 */
public class IntegralShopActivity extends BaseActivitySingleList {

    private final String URL_CATEGORY_LIST = Constant.BASE_URL + "/api/mall/category_list";
    private ArrayList<Category> mCategoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        showLoadingProgress();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        super.initView();
        mListView.setDividerHeight(DensityUtil.dp2px(this, 0));
        mListView.cancleLoadMore();
    }

    @Override
    protected void setTitle(TitleView titleView) {
        titleView.setTitle("积分商城");
    }

    @Override
    protected void onLoadMore() {

    }

    @Override
    protected void onRefresh() {
        OkHttpUtils.post().url(URL_CATEGORY_LIST).addHeader(ACCESS_TOKEN, mAccessToken).build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtil.Companion.d("积分商城列表E：" + e.getLocalizedMessage());
                        Util.showTimeOutNotice(currentContext);
                        hideLoadingProgress();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        LogUtil.Companion.d("积分商城列表：" + response);
                        mSwipeRefreshLayout.setRefreshing(false);
                        hideLoadingProgress();
                        ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                        switch (errorBean.getStatus()) {
                            case "success":
                                mCategoryList = JSON.parseObject(response, CategoryBean.class).getCategoryList();
                                handleEmptyData(mCategoryList);
                                mListView.setAdapter(new IntegralShopAdapter(mCategoryList));
                                break;
                            case "failure":
                                Util.showError(IntegralShopActivity.this, errorBean.getReason());
                                break;
                        }
                    }
                });
    }

    @Override
    protected void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, IntegralGoodsListActivity.class);
        int categoryId = mCategoryList.get(position).getId();
        String categoryName = mCategoryList.get(position).getCategoryName();
        intent.putExtra("categoryId", categoryId);
        intent.putExtra("categoryName", categoryName);
        startActivity(intent);
    }
}
