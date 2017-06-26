package com.yimiao100.sale.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;

import com.yimiao100.sale.R;
import com.yimiao100.sale.activity.VideoDetailActivity;
import com.yimiao100.sale.adapter.listview.ScoreAdapter;
import com.yimiao100.sale.base.BaseFragmentSingleList;
import com.yimiao100.sale.bean.ErrorBean;
import com.yimiao100.sale.bean.OpenClass;
import com.yimiao100.sale.bean.OpenClassBean;
import com.yimiao100.sale.bean.OpenClassResult;
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
 * 学习/做任务-领取积分
 * Created by Michel on 2016/10/24.
 */

public class WinScoreFragment extends BaseFragmentSingleList {


    private final String URL_GET_INTEGRAL = Constant.BASE_URL + "/api/course/open_list";
    private final String INTEGRAL_TYPE = "integralType";
    private final String COLLECT_FLAG = "collectFlag";

    private final String mIntegralType = "increase";
    private final String mCollectFlag = "true";
    private ArrayList<OpenClass> mIntegralClasses;
    private ScoreAdapter mIntegralAdapter;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setEmptyView(getString(R.string.empty_view_win), R.mipmap.ico_curriculum);
    }

    @Override
    protected String initPageTitle() {
        return "领取积分";
    }

    @Override
    public void onStart() {
        super.onStart();
        onRefresh();
    }



    private RequestCall getBuild(int page) {
        return OkHttpUtils.post().url(URL_GET_INTEGRAL).addHeader(ACCESS_TOKEN, mAccessToken)
                .addParams(PAGE, page + "").addParams(PAGE_SIZE, mPageSize)
                .addParams(INTEGRAL_TYPE, mIntegralType).addParams(COLLECT_FLAG, mCollectFlag)
                .build();
    }


    @Override
    protected void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int courseId = mIntegralClasses.get(position).getId();
        Intent intent = new Intent(getContext(), VideoDetailActivity.class);
        intent.putExtra("courseId", courseId);
        startActivity(intent);
    }

    @Override
    protected void onRefresh() {
        getBuild(1).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.d("领取积分E：" + e.getLocalizedMessage());
                if (WinScoreFragment.this.isAdded()) {
                    //防止Fragment点击报空指针
                    Util.showTimeOutNotice(getActivity());
                }
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.d("领取积分：" + response);
                mSwipeRefreshLayout.setRefreshing(false);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        OpenClassResult pagedResult = JSON.parseObject(response, OpenClassBean
                                .class).getPagedResult();
                        mPage = 2;
                        mTotalPage = pagedResult.getTotalPage();
                        mIntegralClasses = pagedResult.getPagedList();
                        handleEmptyData(mIntegralClasses);
                        mIntegralAdapter = new ScoreAdapter(mIntegralClasses);
                        mListView.setAdapter(mIntegralAdapter);
                        break;
                    case "failure":
                        Util.showError(getActivity(), errorBean.getReason());
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
                LogUtil.d("领取积分E：" + e.getLocalizedMessage());
                if (WinScoreFragment.this.isAdded()) {
                    //防止Fragment点击报空指针
                    Util.showTimeOutNotice(getActivity());
                }
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.d("领取积分：" + response);
                mListView.onLoadMoreComplete();
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        mPage++;
                        mIntegralClasses.addAll(JSON.parseObject(response, OpenClassBean
                                .class).getPagedResult().getPagedList());
                        mIntegralAdapter.notifyDataSetChanged();
                        break;
                    case "failure":
                        Util.showError(getActivity(), errorBean.getReason());
                        break;
                }
            }
        });
    }
}
