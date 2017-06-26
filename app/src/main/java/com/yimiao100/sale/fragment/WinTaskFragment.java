package com.yimiao100.sale.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;

import com.yimiao100.sale.R;
import com.yimiao100.sale.activity.VideoDetailActivity;
import com.yimiao100.sale.adapter.listview.TaskAdapter;
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
 * 学习/做任务-全部任务
 * Created by Michel on 2016/10/24.
 */

public class WinTaskFragment extends BaseFragmentSingleList {
    private final String URL_ALL_TASK = Constant.BASE_URL + "/api/course/open_list";

    private final String INTEGRAL_TYPE = "integralType";

    private final String mIntegralType = "increase";
    private ArrayList<OpenClass> mTaskClasses;
    private TaskAdapter mTaskAdapter;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setEmptyView(getString(R.string.empty_view_win), R.mipmap.ico_curriculum);
    }

    @Override
    protected String initPageTitle() {
        return "全部任务";
    }

    @Override
    public void onStart() {
        super.onStart();
        onRefresh();
    }


    private RequestCall getBuild(int page) {
        return OkHttpUtils.post().url(URL_ALL_TASK).addHeader(ACCESS_TOKEN, mAccessToken)
                .addParams(PAGE, page + "").addParams(PAGE_SIZE, mPageSize)
                .addParams(INTEGRAL_TYPE, mIntegralType).build();
    }


    @Override
    protected void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int courseId = mTaskClasses.get(position).getId();
        Intent intent = new Intent(getContext(), VideoDetailActivity.class);
        intent.putExtra("courseId", courseId);
        startActivity(intent);
    }

    @Override
    protected void onRefresh() {
        getBuild(1).execute(new StringCallback() {

            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.d("全部任务E：" + e.getLocalizedMessage());
                if (WinTaskFragment.this.isAdded()) {
                    //防止Fragment点击报空指针
                    Util.showTimeOutNotice(getActivity());
                }
            }

            @Override
            public void onResponse(String response, int id) {
                mSwipeRefreshLayout.setRefreshing(false);
                LogUtil.d("全部任务：" + response);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        OpenClassResult pagedResult = JSON.parseObject(response, OpenClassBean
                                .class).getPagedResult();
                        mPage = 2;
                        mTotalPage = pagedResult.getTotalPage();
                        mTaskClasses = pagedResult.getPagedList();
                        handleEmptyData(mTaskClasses);
                        mTaskAdapter = new TaskAdapter(mTaskClasses);
                        mListView.setAdapter(mTaskAdapter);
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
                LogUtil.d("全部任务E：" + e.getLocalizedMessage());
                if (WinTaskFragment.this.isAdded()) {
                    //防止Fragment点击报空指针
                    Util.showTimeOutNotice(getActivity());
                }
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.d("全部任务：" + response);
                mListView.onLoadMoreComplete();
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        mPage++;
                        mTaskClasses.addAll(JSON.parseObject(response, OpenClassBean.class)
                                .getPagedResult().getPagedList());
                        mTaskAdapter.notifyDataSetChanged();
                        break;
                    case "failure":
                        Util.showError(getActivity(), errorBean.getReason());
                        break;
                }
            }
        });
    }
}
