package com.yimiao100.sale.base;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.yimiao100.sale.R;
import com.yimiao100.sale.view.PullToRefreshListView;
import com.yimiao100.sale.view.TitleView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 布局仅为标题和ListView的BaseActivity
 */
public abstract class BaseActivitySingleList extends BaseActivity implements TitleView.TitleBarOnClickListener {

    @BindView(R.id.list_title)
    protected TitleView mTitle;
    @BindView(R.id.list_list_view)
    protected PullToRefreshListView mListView;
    @BindView(R.id.list_swipe)
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected View mEmptyView;
    protected TextView mEmptyText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_single_list);
        ButterKnife.bind(this);

        initView();

        initData();

    }

    protected void initView() {
        initTitleView();

        initEmptyView();

        initRefreshView();

        initListView();

    }


    private void initEmptyView() {
        mEmptyView = findViewById(R.id.list_empty_view);
        mEmptyText = (TextView) mEmptyView.findViewById(R.id.empty_text);
    }

    protected void setEmptyView(String emptyText, int id) {
        mEmptyText.setText(emptyText);
        mEmptyText.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(id), null, null);
    }

    protected void initData() {
        setTitle(mTitle);
        onRefresh();
    }

    private void initTitleView() {
        mTitle.setOnTitleBarClick(this);
    }

    private void initRefreshView() {
        //设置刷新监听
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                BaseActivitySingleList.this.onRefresh();
            }
        });
        //设置下拉圆圈的颜色
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color
                        .holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        //设置手指在屏幕下拉多少距离会触发下拉刷新
        mSwipeRefreshLayout.setDistanceToTriggerSync(400);
    }

    private void initListView() {

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BaseActivitySingleList.this.onItemClick(parent, view, position, id);
            }
        });

        mListView.setOnRefreshingListener(new PullToRefreshListView.OnRefreshingListener() {
            @Override
            public void onLoadMore() {
                if (page <= totalPage) {
                    BaseActivitySingleList.this.onLoadMore();
                } else {
                    mListView.noMore();
                }
            }
        });

    }

    /**
     * 加载更多
     */
    protected abstract void onLoadMore();


    /**
     * 条目点击
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    protected abstract void onItemClick(AdapterView<?> parent, View view, int position, long id);

    /**
     * 下拉刷新
     */
    protected abstract void onRefresh() ;

    /**
     * 设置标题栏
     */
    protected abstract void setTitle(TitleView titleView);

    /**
     * 处理空数据
     * @param list
     */
    protected void handleEmptyData(ArrayList<?> list) {
        if (list.size() == 0) {
            //显示emptyView
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mEmptyView.setVisibility(View.GONE);
        }
    }
    @Override
    public void leftOnClick() {
        finish();
    }

    @Override
    public void rightOnClick() {

    }
}
