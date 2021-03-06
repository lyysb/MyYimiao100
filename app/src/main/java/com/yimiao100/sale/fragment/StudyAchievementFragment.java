package com.yimiao100.sale.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;

import com.yimiao100.sale.R;
import com.yimiao100.sale.activity.VideoDetailActivity;
import com.yimiao100.sale.adapter.listview.AchievedAdapter;
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
 * 学习任务-已完成
 * Created by Michel on 2016/10/24.
 */

public class StudyAchievementFragment extends BaseFragmentSingleList {

    private final String URL_EXAM_LIST = Constant.BASE_URL + "/api/course/exam_list";
    private final String EXAM_STATUS = "examStatus";

    private String mExamStatus = "1";
    private ArrayList<OpenClass> mAchievedClass;
    private AchievedAdapter mAchievedAdapter;



    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setEmptyView(getString(R.string.empty_view_study_achievement), R.mipmap.ico_study_extension);
    }

    @Override
    protected String initPageTitle() {
        return "已完成";
    }

    @Override
    public void onStart() {
        super.onStart();
        onRefresh();
    }

    @Override
    protected void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getContext(), VideoDetailActivity.class);
        int courseId = mAchievedClass.get(position).getId();
        intent.putExtra("courseId", courseId);
        startActivity(intent);
    }

    @Override
    protected void onRefresh() {
        getBuild(1).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.Companion.d("学习任务-已完成E：" + e.getLocalizedMessage());
                if (StudyAchievementFragment.this.isAdded()) {
                    //防止Fragment点击报空指针
                    Util.showTimeOutNotice(getActivity());
                }
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.Companion.d("学习任务-已完成：" + response);
                mSwipeRefreshLayout.setRefreshing(false);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        OpenClassResult pagedResult = JSON.parseObject(response, OpenClassBean
                                .class).getPagedResult();
                        mPage = 2;
                        mTotalPage = pagedResult.getTotalPage();
                        mAchievedClass = pagedResult.getPagedList();
                        handleEmptyData(mAchievedClass);
                        //已完成课程数据
                        mAchievedAdapter = new AchievedAdapter(mAchievedClass);
                        mListView.setAdapter(mAchievedAdapter);
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
                LogUtil.Companion.d("学习任务-已完成E：" + e.getLocalizedMessage());
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.Companion.d("学习任务-已完成：" + response);
                mListView.onLoadMoreComplete();
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        mPage++;
                        mAchievedClass.addAll(JSON.parseObject(response, OpenClassBean.class)
                                .getPagedResult().getPagedList());
                        mAchievedAdapter.notifyDataSetChanged();
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
                .addParams(PAGE, page + "").addParams(PAGE_SIZE, "10")
                .addParams(EXAM_STATUS, mExamStatus).build();
    }
}
