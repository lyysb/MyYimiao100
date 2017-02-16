package com.yimiao100.sale.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;

import com.alibaba.fastjson.JSON;
import com.yimiao100.sale.R;
import com.yimiao100.sale.activity.VideoDetailActivity;
import com.yimiao100.sale.adapter.listview.UnAchievedAdapter;
import com.yimiao100.sale.base.BaseFragmentSingleList;
import com.yimiao100.sale.bean.ErrorBean;
import com.yimiao100.sale.bean.OpenClass;
import com.yimiao100.sale.bean.OpenClassBean;
import com.yimiao100.sale.bean.OpenClassResult;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.Util;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;

import java.util.ArrayList;

import okhttp3.Call;

/**
 * 学习任务-未完成
 * Created by 亿苗通 on 2016/10/24.
 */

public class StudyUnAchievedFragment extends BaseFragmentSingleList {

    private final String URL_EXAM_LIST = Constant.BASE_URL + "/api/course/exam_list";
    private final String EXAM_STATUS = "examStatus";

    private String mExamStatus = "0";
    private ArrayList<OpenClass> mUnAchievedClass;
    private UnAchievedAdapter mUnAchievedAdapter;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setEmptyView("考试是可以“挣钱”的。但是，暂时没有学习任务。", R.mipmap.ico_study_extension);
    }

    @Override
    protected String initPageTitle() {
        return "未完成";
    }

    @Override
    public void onStart() {
        super.onStart();
        onRefresh();
    }

    @Override
    protected void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getContext(), VideoDetailActivity.class);
        int courseId = mUnAchievedClass.get(position).getId();
        intent.putExtra("courseId", courseId);
        startActivity(intent);
    }

    @Override
    protected void onRefresh() {
        getBuild(1).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.Companion.d("学习任务-未完成E：" + e.getLocalizedMessage());
                if (StudyUnAchievedFragment.this.isAdded()) {
                    //防止Fragment点击报空指针
                    Util.showTimeOutNotice(getActivity());
                }
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.Companion.d("学习任务-未完成：" + response);
                mSwipeRefreshLayout.setRefreshing(false);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        OpenClassResult pagedResult = JSON.parseObject(response, OpenClassBean.class).
                                getPagedResult();
                        mPage = 2;
                        mTotalPage = pagedResult.getTotalPage();
                        mUnAchievedClass = pagedResult.getPagedList();
                        handleEmptyData(mUnAchievedClass);
                        //未完成课程数据
                        mUnAchievedAdapter = new UnAchievedAdapter(mUnAchievedClass);
                        mListView.setAdapter(mUnAchievedAdapter);
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
                LogUtil.Companion.d("学习任务-未完成E：" + e.getLocalizedMessage());
                if (StudyUnAchievedFragment.this.isAdded()) {
                    //防止Fragment点击报空指针
                    Util.showTimeOutNotice(getActivity());
                }
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.Companion.d("学习任务-未完成：" + response);
                mListView.onLoadMoreComplete();
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        mPage++;
                        mUnAchievedClass.addAll(JSON.parseObject(response, OpenClassBean.class).
                                getPagedResult().getPagedList());
                        mUnAchievedAdapter.notifyDataSetChanged();
                        break;
                    case "failure":
                        Util.showError(getActivity(), errorBean.getReason());
                        break;
                }
            }
        });
    }
    private RequestCall getBuild(int page) {
        return OkHttpUtils.post().url(URL_EXAM_LIST).addHeader(ACCESS_TOKEN, mAccessToken)
                .addParams(PAGE, page + "").addParams(PAGE_SIZE, mPageSize)
                .addParams(EXAM_STATUS, mExamStatus).build();
    }
}
