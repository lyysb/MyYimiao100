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
import butterknife.OnClick;

public abstract class BaseActivityListWithText extends BaseActivity implements TitleView.TitleBarOnClickListener {

    @BindView(R.id.list_text_title)
    protected TitleView mTitle;
    @BindView(R.id.list_text_list_view)
    protected PullToRefreshListView mListView;
    @BindView(R.id.list_text_refresh_view)
    protected SwipeRefreshLayout mRefreshView;
    @BindView(R.id.list_text_text)
    protected TextView mListTextText;
    protected View mEmptyView;
    protected TextView mEmptyText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_list_with_text);
        ButterKnife.bind(this);

        initView();

        initData();
    }

    private void initView() {
        initTitleView();

        initRefreshLayout();

        initEmptyView();

        initListView();

        initBottomText();
    }

    private void initTitleView() {
        mTitle.setOnTitleBarClick(this);
        mTitle.setTitle(setTitle());
    }

    private void initRefreshLayout() {
        //设置刷新监听
        mRefreshView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                BaseActivityListWithText.this.onRefresh();
            }
        });
        //设置下拉圆圈的颜色
        mRefreshView.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color
                        .holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        //设置手指在屏幕下拉多少距离会触发下拉刷新
        mRefreshView.setDistanceToTriggerSync(400);
    }

    private void initEmptyView() {
        mEmptyView = findViewById(R.id.list_text_empty_view);
        mEmptyText = (TextView) mEmptyView.findViewById(R.id.empty_text);

    }

    protected void setEmptyView(String emptyText, int id) {
        mEmptyText.setText(emptyText);
        mEmptyText.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(id), null, null);
    }

    private void initListView() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BaseActivityListWithText.this.onItemClick(parent, view, position, id);
            }
        });

        mListView.setOnRefreshingListener(new PullToRefreshListView.OnRefreshingListener() {
            @Override
            public void onLoadMore() {
                if (mPage <= mTotalPage) {
                    BaseActivityListWithText.this.onLoadMore();
                } else {
                    mListView.noMore();
                }
            }
        });
    }

    private void initBottomText() {
        mListTextText.setText(setBottomText());
    }

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

    @OnClick(R.id.list_text_text)
    public void onClick() {
        onBottomClick();
    }

    /**
     * @return 界面标题
     */
    protected abstract String setTitle();

    /**
     * @return 底部文本
     */
    protected abstract String setBottomText();

    /**
     * 初始化数据
     */
    protected abstract void initData();

    /**
     * 底部点击事件
     */
    protected abstract void onBottomClick();

    /**
     * 条目点击
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    protected abstract void onItemClick(AdapterView<?> parent, View view, int position, long id);

    /**
     * 刷新
     */
    protected abstract void onRefresh();

    /**
     * 加载更多
     */
    protected abstract void onLoadMore();

    @Override
    public void leftOnClick() {
        finish();
    }

    @Override
    public void rightOnClick() {

    }
}
