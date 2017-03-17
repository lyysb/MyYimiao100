package com.yimiao100.sale.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.alibaba.fastjson.JSON;
import com.yimiao100.sale.R;
import com.yimiao100.sale.adapter.listview.NoticeAdapter;
import com.yimiao100.sale.base.BaseActivitySingleList;
import com.yimiao100.sale.bean.ErrorBean;
import com.yimiao100.sale.bean.NoticeBean;
import com.yimiao100.sale.bean.NoticedListBean;
import com.yimiao100.sale.bean.NoticedResultBean;
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
 * 我的通知-通知列表
 */
public class NoticeActivity extends BaseActivitySingleList {


    private final String URL_USER_NOTICE_LIST = Constant.BASE_URL + "/api/notice/user_notice_list";

    private final String URL_READ_NOTICE = Constant.BASE_URL + "/api/notice/read_notice";

    private ArrayList<NoticedListBean> mPagedList;

    private NoticeAdapter mNoticeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        showLoadingProgress();
        super.onCreate(savedInstanceState);
        setEmptyView(getString(R.string.empty_view_notice), R.mipmap.ico_notice);
    }


    @Override
    protected void setTitle(TitleView titleView) {
        titleView.setTitle("通知");
    }

    @Override
    public void onRefresh() {
        //刷新列表
        getBuild(1).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.Companion.d("通知E：" + e.getMessage());
                Util.showTimeOutNotice(currentContext);
                hideLoadingProgress();
            }

            @Override
            public void onResponse(String response, int id) {
                hideLoadingProgress();
                mSwipeRefreshLayout.setRefreshing(false);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        LogUtil.Companion.d("通知：" + response);
                        mPage = 2;
                        NoticedResultBean pagedResult = JSON.parseObject(response, NoticeBean
                                .class).getPagedResult();
                        mTotalPage = pagedResult.getTotalPage();
                        //解析JSON
                        mPagedList = pagedResult.getPagedList();
                        handleEmptyData(mPagedList);
                        mNoticeAdapter = new NoticeAdapter(getApplicationContext(), mPagedList);
                        mListView.setAdapter(mNoticeAdapter);
                        break;
                    case "failure":
                        Util.showError(currentContext, errorBean.getReason());
                        break;
                }
            }
        });
    }

    @Override
    public void onLoadMore() {
        //加载更多
        getBuild(mPage).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.Companion.d("通知E：" + e.getMessage());
                Util.showTimeOutNotice(currentContext);
            }

            @Override
            public void onResponse(String response, int id) {
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        mPage++;
                        //解析JSON
                        mPagedList.addAll(JSON.parseObject(response, NoticeBean
                                .class).getPagedResult().getPagedList());
                        mNoticeAdapter.notifyDataSetChanged();
                        break;
                    case "failure":
                        Util.showError(currentContext, errorBean.getReason());
                        break;
                }
                mListView.onLoadMoreComplete();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        //点击进入通知详情页
        int noticeId = mPagedList.get(position).getNoticeId();
        Intent intent = new Intent(this, NoticeDetailActivity.class);
        intent.putExtra("noticeId", noticeId);
        startActivity(intent);
        //提交网络阅读状态
        OkHttpUtils.post().url(URL_READ_NOTICE).addHeader(ACCESS_TOKEN, mAccessToken)
                .addParams("noticeId", noticeId + "")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtil.Companion.d("提交网络阅读状态E: " + e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        LogUtil.Companion.d("提交网络阅读状态：" + response);
                        ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                        switch (errorBean.getStatus()) {
                            case "success":
                                //刷新阅读状态
                                mPagedList.get(position).setReadStatus(1);
                                mNoticeAdapter.notifyDataSetChanged();
                                break;
                            case "failure":
                                Util.showError(currentContext, errorBean.getReason());
                                break;
                        }
                    }
                });


    }

    private RequestCall getBuild(int page) {
        return OkHttpUtils.post()
                .url(URL_USER_NOTICE_LIST)
                .addHeader(ACCESS_TOKEN, mAccessToken)
                .addParams(PAGE, page + "")
                .addParams(PAGE_SIZE, mPageSize)
                .build();
    }

    @Override
    public void leftOnClick() {
        finish();
    }

    @Override
    public void rightOnClick() {

    }
}
