package com.yimiao100.sale.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.yimiao100.sale.R;
import com.yimiao100.sale.utils.ToastUtil;

/**
 * 实现上拉加载
 * Created by 亿苗通 on 2016/9/6.
 */
public class PullToRefreshListView extends ListView implements AbsListView.OnScrollListener {

    private int footerViewHeight;
    private View footerView;
    private boolean loadingMore;
    private OnRefreshingListener mOnRefreshingListener;
    private SwipeRefreshLayoutEnabledListener mSwipeRefreshLayoutEnabledListener;
    private TextView mTv_more;
    private boolean isOpened = true;


    public PullToRefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initFooterView();
    }



    private void initFooterView() {
        footerView = LayoutInflater.from(getContext()).inflate(R.layout.footer_view, null);
        mTv_more = (TextView) footerView.findViewById(R.id.more);
        //必须先测量才能获得测量的高度
        footerView.measure(0, 0);
        footerViewHeight = footerView.getMeasuredHeight();
        //初始隐藏FooterView
        hideFooterView();
        super.addFooterView(footerView, null, false);
        //设置滑动监听
        super.setOnScrollListener(this);
    }

    /**
     * 刷新监听
     */
    public void setOnRefreshingListener(OnRefreshingListener mOnRefreshingListener) {
        this.mOnRefreshingListener = mOnRefreshingListener;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == OnScrollListener.SCROLL_STATE_IDLE   //如果滑动状态为空闲状态
                && getLastVisiblePosition() == getCount() - 1       //如果滑到了最底部
                && !loadingMore                             //如果当前没有去做正在加载更多的事情
                && isOpened                                 //如果开启了加载更多
                ) {
            loadingMore = true;
            showFooterView();
            setSelection(getCount());
            if (mOnRefreshingListener != null) {
                mOnRefreshingListener.onLoadMore();
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        boolean enable = false;
        int visibility = this.getVisibility();
        if (visibility == 0 && this != null && this.getChildCount() > 0) {
            // check if the first item of the list is visible
            boolean firstItemVisible = this.getFirstVisiblePosition() == 0;
            // check if the top of the first item is visible
            boolean topOfFirstItemVisible = this.getChildAt(0).getTop() == 0;
            // enabling or disabling the refresh layout
            enable = firstItemVisible && topOfFirstItemVisible;
        }
        if (mSwipeRefreshLayoutEnabledListener != null) {
            mSwipeRefreshLayoutEnabledListener.swipeEnabled(enable);
        }
    }

    /** 解决SwipeLayout滑动冲突*/
    public void setSwipeRefreshLayoutEnabled(SwipeRefreshLayoutEnabledListener swipeRefreshLayoutEnabledListener){
        mSwipeRefreshLayoutEnabledListener = swipeRefreshLayoutEnabledListener;
    }
    public interface SwipeRefreshLayoutEnabledListener{
        void swipeEnabled(boolean enable);
    }
    public interface OnRefreshingListener {
        void onLoadMore();
    }

    /**
     * 加载完成，由外部调用
     */
    public void onLoadMoreComplete() {
        hideFooterView();
        loadingMore = false;
    }



    /**
     * 已无更多数据，由外部调用
     */
    public void noMore(){
        ToastUtil.showShort(getContext(), "全部加载完成");
        hideFooterView();
        loadingMore = false;
    }

    /**
     * 关闭加载更多功能
     */
    public void cancleLoadMore(){
        isOpened = false;
    }
    /**
     * 隐藏FooterView
     */
    private void hideFooterView() {
        int paddingBottom = -footerViewHeight;
        setFooterViewPaddingBottom(paddingBottom);
    }

    /**
     * 显示FooterView
     */
    private void showFooterView() {
        int paddingBottom = 0;
        setFooterViewPaddingBottom(paddingBottom);
    }

    /**
     * 设置FooterView的Padding
     */
    private void setFooterViewPaddingBottom(int paddingBottom) {
        footerView.setPadding(0, 0, 0, paddingBottom);
    }

    public PullToRefreshListView(Context context) {
        this(context, null);
    }

    public PullToRefreshListView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

}
