package com.yimiao100.sale.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.yimiao100.sale.R;
import com.yimiao100.sale.adapter.listview.ReportAdapter;
import com.yimiao100.sale.base.BaseActivityListWithText;
import com.yimiao100.sale.bean.AdverseList;
import com.yimiao100.sale.bean.ErrorBean;
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
 * 不良反应申报列表页
 */
public class ReportActivity extends BaseActivityListWithText {

    private final String URL_ADVERSE_LIST = Constant.BASE_URL + "/api/apply/adverse_list";
    private ArrayList<AdverseList.PagedResult.Adverse> mList;
    private ReportAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setEmptyView(getString(R.string.empty_view_report), R.mipmap.ico_blank);

        showLoadingProgress();
    }

    @Override
    protected String setTitle() {
        return "申报记录";
    }

    @Override
    protected String setBottomText() {
        return "不良反应申报";
    }

    @Override
    protected void initData() {
        getBuild(1).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                e.printStackTrace();
                LogUtil.Companion.d("init report list data error");
                hideLoadingProgress();
                Util.showTimeOutNotice(currentContext);
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.Companion.d("init data success , response is\n" + response);
                hideLoadingProgress();
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        AdverseList.PagedResult pagedResult = JSON.parseObject(response, AdverseList.class).getPagedResult();
                        mPage = 2;
                        mTotalPage = pagedResult.getTotalPage();
                        mList = pagedResult.getPagedList();
                        mAdapter = new ReportAdapter(mList);
                        mListView.setAdapter(mAdapter);
                        handleEmptyData(mList);
                        break;
                    case "failure":
                        Util.showError(currentContext, errorBean.getReason());
                        break;
                }
            }
        });

    }

    @Override
    protected void onBottomClick() {
        startActivityForResult(new Intent(this, ReportDetailActivity.class), 100);
        finish();
    }

    @Override
    protected void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AdverseList.PagedResult.Adverse adverse = mList.get(position);
        int adverseApplyId = adverse.getId();
        Intent intent = new Intent(this, ReportDetailActivity.class);
        intent.putExtra("adverseApplyId", adverseApplyId);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onRefresh() {
        getBuild(1).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                e.printStackTrace();
                LogUtil.Companion.d("refresh report list data error");
                mRefreshView.setRefreshing(false);
                Util.showTimeOutNotice(currentContext);
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.Companion.d("refresh data success , response is\n" + response);
                mRefreshView.setRefreshing(false);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        AdverseList.PagedResult pagedResult = JSON.parseObject(response, AdverseList.class).getPagedResult();
                        mPage = 2;
                        mTotalPage = pagedResult.getTotalPage();
                        mList = pagedResult.getPagedList();
                        mAdapter = new ReportAdapter(mList);
                        mListView.setAdapter(mAdapter);
                        handleEmptyData(mList);
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
                e.printStackTrace();
                LogUtil.Companion.d("load more report list data error");
                Util.showTimeOutNotice(currentContext);
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.Companion.d("load more data success , response is\n" + response);
                mListView.onLoadMoreComplete();
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        AdverseList.PagedResult pagedResult = JSON.parseObject(response, AdverseList.class).getPagedResult();
                        mPage++;
                        mList.addAll(pagedResult.getPagedList());
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                showLoadingProgress();
                initData();
            }
        }
    }

    private RequestCall getBuild(int page) {
        return OkHttpUtils.post().url(URL_ADVERSE_LIST).addHeader(ACCESS_TOKEN, mAccessToken)
                .addParams(PAGE, page + "").addParams(PAGE_SIZE, mPageSize).build();
    }
}
