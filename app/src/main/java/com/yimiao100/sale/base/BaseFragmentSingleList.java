package com.yimiao100.sale.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.yimiao100.sale.R;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.SharePreferenceUtil;
import com.yimiao100.sale.view.PullToRefreshListView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 只有一个ListView的Fragment
 * Created by 亿苗通 on 2016/10/24.
 */

public abstract class BaseFragmentSingleList extends Fragment {

    @BindView(R.id.base_single_list_view)
    protected PullToRefreshListView mListView;
    @BindView(R.id.base_single_swipe)
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected final String ACCESS_TOKEN = "X-Authorization-Token";
    protected final String PAGE = "page";
    protected final String PAGE_SIZE = "pageSize";

    protected String mAccessToken;
    protected int mPage;
    protected final String mPageSize = "10";
    protected int mTotalPage;
    private View mView;
    private View mEmptyView;
    private TextView mEmptyText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_base_single_list, null);

        ButterKnife.bind(this, mView);

        mAccessToken = (String) SharePreferenceUtil.get(getContext(), Constant.ACCESSTOKEN, "");
        initView();

        onRefresh();



        return mView;
    }



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
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color
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
        mEmptyText.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(id), null, null);
    }

    protected void handleEmptyData(ArrayList<?> list) {
        if (list.size() == 0) {
            //显示emptyView
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mEmptyView.setVisibility(View.GONE);
        }
    }
    /**
     * 条目点击事件
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    protected abstract void onItemClick(AdapterView<?> parent, View view, int position, long id);

    /**
     * 刷新数据
     */
    protected abstract void onRefresh();


    /**
     * 加载更多数据
     */
    protected abstract void onLoadMore();
}
