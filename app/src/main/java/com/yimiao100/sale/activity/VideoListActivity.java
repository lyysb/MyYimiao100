package com.yimiao100.sale.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.yimiao100.sale.R;
import com.yimiao100.sale.adapter.listview.VideoClassAdapter;
import com.yimiao100.sale.base.BaseActivitySingleList;
import com.yimiao100.sale.bean.ErrorBean;
import com.yimiao100.sale.bean.OpenClass;
import com.yimiao100.sale.bean.OpenClassBean;
import com.yimiao100.sale.bean.OpenClassResult;
import com.yimiao100.sale.ext.JSON;
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
 * 推广界面
 */
public class VideoListActivity extends BaseActivitySingleList {

    private final String URL_EXAM_LIST = Constant.BASE_URL + "/api/course/exam_list";

    private ArrayList<OpenClass> mExamClasses;
    private VideoClassAdapter mClassAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        showLoadingProgress();
        super.onCreate(savedInstanceState);
        setEmptyView(getString(R.string.empty_view_video_list), R.mipmap.ico_study_extension);
    }

    @Override
    protected void setTitle(TitleView titleView) {
        titleView.setTitle("推广/考试");
    }


    @Override
    protected void onResume() {
        super.onResume();
        onRefresh();
    }

    @Override
    protected void onRefresh() {
        getBuild(1).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.d("考试视频列表E：" + e.getLocalizedMessage());
                Util.showTimeOutNotice(currentContext);
                hideLoadingProgress();
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.d("考试视频列表：" + response);
                hideLoadingProgress();
                mSwipeRefreshLayout.setRefreshing(false);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        OpenClassResult pagedResult = JSON.parseObject(response, OpenClassBean
                                .class).getPagedResult();
                        page =2 ;
                        totalPage = pagedResult.getTotalPage();
                        mExamClasses = pagedResult.getPagedList();
                        handleEmptyData(mExamClasses);
                        //考试课数据
                        mClassAdapter = new VideoClassAdapter(mExamClasses);
                        mListView.setAdapter(mClassAdapter);
                        break;
                    case "failure":
                        Util.showError(VideoListActivity.this, errorBean.getReason());
                        break;
                }
            }
        });
    }


    @Override
    protected void onLoadMore() {
        getBuild(page).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.d("考试视频列表E：" + e.getLocalizedMessage());
                Util.showTimeOutNotice(currentContext);
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.d("考试视频列表：" + response);
                mListView.onLoadMoreComplete();
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        page++;
                        mExamClasses.addAll(JSON.parseObject(response, OpenClassBean.class)
                                .getPagedResult().getPagedList());
                        //考试课数据
                        mClassAdapter.notifyDataSetChanged();
                        break;
                    case "failure":
                        Util.showError(VideoListActivity.this, errorBean.getReason());
                        break;
                }
            }
        });
    }

    private RequestCall getBuild(int page) {
        return OkHttpUtils.post().url(URL_EXAM_LIST).addHeader(ACCESS_TOKEN, accessToken)
                .addParams(PAGE, page + "").addParams(PAGE_SIZE, "10").build();
    }

    @Override
    protected void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int courseId = mExamClasses.get(position).getId();
        Intent intent = new Intent(this, VideoDetailActivity.class);
        intent.putExtra("courseId", courseId);
        startActivity(intent);
    }
}
