package com.yimiao100.sale.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.yimiao100.sale.R;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.SharePreferenceUtil;
import com.yimiao100.sale.view.PullToRefreshListView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 只有一个ListView的Fragment
 * Created by 亿苗通 on 2016/10/24.
 */

public abstract class BaseFragmentSingleList extends BaseFragment {

    @BindView(R.id.base_single_list_view)
    protected PullToRefreshListView mListView;
    @BindView(R.id.base_single_swipe)
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected final String PAGE = "page";
    protected final String PAGE_SIZE = "pageSize";

    protected String mAccessToken;
    protected int mPage;
    protected final String mPageSize = "10";
    protected int mTotalPage;
    protected View mEmptyView;
    private View mView;
    private TextView mEmptyText;

    protected boolean isSetVisibleData = false; // 暂时在子类中设置为true，切换刷新数据。

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {

        LogUtil.d(getClass().getSimpleName() + " onCreateView");
        mView = inflater.inflate(R.layout.fragment_base_single_list, null);

        ButterKnife.bind(this, mView);

        mAccessToken = (String) SharePreferenceUtil.get(getContext(), Constant.ACCESSTOKEN, "");

        initVariate();

        initView();

        return mView;
    }

    protected void initVariate() {

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LogUtil.d(getClass().getSimpleName() + " onViewCreated");
        onRefresh();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isSetVisibleData && isVisibleToUser) {
            // 如果设置根据可见性加载数据。则在可见的时候刷新数据
            onRefresh();
            LogUtil.d(getClass().getSimpleName() + " isVisibleToUser - true");
        }
    }

    /**
     * @return 初始化Tab标签
     */
    protected abstract String initPageTitle();

    private void initView() {
        initEmptyView();

        initRefreshView();

        initListView();
    }

    private void initEmptyView() {
        mEmptyView = mView.findViewById(R.id.fragment_list_empty_view);
        mEmptyText = (TextView) mEmptyView.findViewById(R.id.empty_text);
    }

    private void initRefreshView() {
        //设置刷新监听
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                BaseFragmentSingleList.this.onRefresh();
            }
        });
        //设置下拉圆圈的颜色
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R
                .color
                        .holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        //设置手指在屏幕下拉多少距离会触发下拉刷新
        mSwipeRefreshLayout.setDistanceToTriggerSync(200);
    }

    private void initListView() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BaseFragmentSingleList.this.onItemClick(parent, view, position, id);
            }
        });

        mListView.setOnRefreshingListener(new PullToRefreshListView.OnRefreshingListener() {
            @Override
            public void onLoadMore() {
                if (mPage <= mTotalPage) {
                    BaseFragmentSingleList.this.onLoadMore();
                } else {
                    mListView.noMore();
                }
            }
        });
    }

    protected void setEmptyView(String emptyText, int id) {
        mEmptyText.setText(emptyText);
        mEmptyText.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(id),
                null, null);
    }

    protected void handleEmptyData(ArrayList<?> list) {
        if (list.size() == 0) {
            //显示emptyView
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mEmptyView.setVisibility(View.GONE);
        }
    }

    protected abstract void onRefresh();

    protected abstract void onItemClick(AdapterView<?> parent, View view, int position, long id);

    protected abstract void onLoadMore();

    /**
     * @return Tab标签
     */
    public CharSequence getPageTitle() {
        return initPageTitle();
    }
}
