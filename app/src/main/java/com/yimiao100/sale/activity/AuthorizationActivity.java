package com.yimiao100.sale.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.alibaba.fastjson.JSON;
import com.yimiao100.sale.R;
import com.yimiao100.sale.adapter.listview.AuthorizationAdapter;
import com.yimiao100.sale.base.BaseActivityListWithText;
import com.yimiao100.sale.bean.AuthorizationList;
import com.yimiao100.sale.bean.ErrorBean;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.Util;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;

import java.util.ArrayList;

import okhttp3.Call;

/**
 * 授权书申请记录
 */
public class AuthorizationActivity extends BaseActivityListWithText {

    private final String URL_AUTHZ_LIST = Constant.BASE_URL + "/api/apply/authz_list";
    private ArrayList<AuthorizationList.PagedResultBean.Authorization> mList;
    private AuthorizationAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setEmptyView(getString(R.string.empty_view_report), R.mipmap.ico_blank);
        showLoadingProgress();
    }

    @Override
    protected String setTitle() {
        return "申请记录";
    }

    @Override
    protected String setBottomText() {
        return "申请授权书";
    }

    @Override
    protected void initData() {
        getBuild(1).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                hideLoadingProgress();
                e.printStackTrace();
                LogUtil.Companion.d("AuthorizationActivity init data error");
            }

            @Override
            public void onResponse(String response, int id) {
                hideLoadingProgress();
                LogUtil.Companion.d("response is \n" + response);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        AuthorizationList.PagedResultBean pagedResult = JSON.parseObject
                                (response, AuthorizationList.class).getPagedResult();
                        mPage = 2;
                        mTotalPage = pagedResult.getTotalPage();
                        mList = pagedResult.getPagedList();
                        handleEmptyData(mList);
                        mAdapter = new AuthorizationAdapter(mList);
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
    protected void onBottomClick() {
        startActivityForResult(new Intent(this, AuthorizationDetailActivity.class), 100);
        finish();
    }

    @Override
    protected void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, AuthorizationDetailActivity.class);
        intent.putExtra("authzApplyId", mList.get(position).getId());
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 100:
                if (resultCode == RESULT_OK) {
                    showLoadingProgress();
                    initData();
                } else {
                    LogUtil.Companion.d("Unknown result code is " + resultCode);
                }
                break;
            default:
                LogUtil.Companion.d("Unknown request code is " + requestCode);
                break;
        }
    }

    @Override
    protected void onRefresh() {
        getBuild(1).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                mRefreshView.setRefreshing(false);
                e.printStackTrace();
                LogUtil.Companion.d("AuthorizationActivity refresh data error");
            }

            @Override
            public void onResponse(String response, int id) {
                mRefreshView.setRefreshing(false);
                LogUtil.Companion.d("response is \n" + response);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        AuthorizationList.PagedResultBean pagedResult = JSON.parseObject
                                (response, AuthorizationList.class).getPagedResult();
                        mPage = 2;
                        mTotalPage = pagedResult.getTotalPage();
                        mList = pagedResult.getPagedList();
                        mAdapter = new AuthorizationAdapter(mList);
                        handleEmptyData(mList);
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
                mListView.onLoadMoreComplete();
                e.printStackTrace();
                LogUtil.Companion.d("AuthorizationActivity load more data error");
            }

            @Override
            public void onResponse(String response, int id) {
                mListView.onLoadMoreComplete();
                LogUtil.Companion.d("response is \n" + response);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        AuthorizationList.PagedResultBean pagedResult = JSON.parseObject
                                (response, AuthorizationList.class).getPagedResult();
                        mPage ++;
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

    private RequestCall getBuild(int page) {
        return OkHttpUtils.post().url(URL_AUTHZ_LIST).addHeader(ACCESS_TOKEN, mAccessToken)
                .addParams(PAGE, page + "").addParams(PAGE_SIZE, mPageSize).build();
    }
}
