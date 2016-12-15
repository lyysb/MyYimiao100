package com.yimiao100.sale.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ExpandableListView;

import com.yimiao100.sale.R;
import com.yimiao100.sale.utils.ToastUtil;

/**
 * 可以加载更多的ExpendableListView
 * Created by 亿苗通 on 2016/9/30.
 */
public class LoadMoreExpendableListView extends ExpandableListView implements AbsListView.OnScrollListener {

    private int mFooterViewHeight;
    private View mFooterView;

    private boolean loadingMore;
    private OnLoadMoreListener mListener;

    public LoadMoreExpendableListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initFooterView();
    }

    private void initFooterView() {
        mFooterView = LayoutInflater.from(getContext()).inflate(R.layout.footer_view, null);
        mFooterView.measure(0, 0);
        mFooterViewHeight = mFooterView.getMeasuredHeight();
        //隐藏footerView
        hideFooterView();
        //添加FooterView
        super.addFooterView(mFooterView);
        //设置滑动监听
        super.setOnScrollListener(this);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_IDLE        //如果滚动时间结束
                && getLastVisiblePosition() == getCount() - 1   //如果滑到了最底部
                && loadingMore == false                 //如果在做“加载更多”
                ) {
            //修改加载状态
            loadingMore = true;
            //显示FooterView
            showFooterView();
            //自动滑到加载出来的第一条数据
            setSelection(getCount());
            //执行加载更多的操作
            if (mListener != null) {
                mListener.onLoadMore();
            }
        }
    }
    public interface OnLoadMoreListener{
        void onLoadMore();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        mListener = listener;
    }
    public void loadingMoreComplete(){
        hideFooterView();
        loadingMore = false;
    }
    public void noMore(){
        ToastUtil.showShort(getContext(), "noMore");
        hideFooterView();
        loadingMore = false;
    }
    /**
     * 隐藏FooterView
     */
    private void hideFooterView() {
        int paddingBottom = -mFooterViewHeight;
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
     * 设置FooterView的底部Padding
     * @param paddingBottom
     */
    private void setFooterViewPaddingBottom(int paddingBottom) {
        mFooterView.setPadding(0, 0, 0, paddingBottom);
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    public LoadMoreExpendableListView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public LoadMoreExpendableListView(Context context) {
        this(context, null);
    }
}
