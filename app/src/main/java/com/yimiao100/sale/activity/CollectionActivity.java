package com.yimiao100.sale.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.yimiao100.sale.R;
import com.yimiao100.sale.adapter.listview.CollectionAdapter;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.bean.ErrorBean;
import com.yimiao100.sale.bean.ImageListBean;
import com.yimiao100.sale.bean.InformationBean;
import com.yimiao100.sale.bean.PagedListBean;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.DensityUtil;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.ToastUtil;
import com.yimiao100.sale.utils.Util;
import com.yimiao100.sale.view.TitleView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

/**
 * 收藏列表
 */
public class CollectionActivity extends BaseActivity implements TitleView.TitleBarOnClickListener, SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {

    @BindView(R.id.collection_title)
    TitleView mCollectionTitle;
    @BindView(R.id.collection_list)
    SwipeMenuListView mCollectionListView;
    @BindView(R.id.collection_swipe)
    SwipeRefreshLayout mCollectionSwipe;

    private final String URL_COLLECTION_LIST = Constant.BASE_URL + "/api/news/user_collection_list";
    private List<PagedListBean> mPagedList;
    private CollectionAdapter mCollectionAdapter;

    private final String URL_CANCEL_COLLECTION = Constant.BASE_URL + "/api/news/cancel_collection";
    private View mEmptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        ButterKnife.bind(this);

        initView();

        onRefresh();


    }

    private void initView() {
        mCollectionTitle.setOnTitleBarClick(this);
        initEmptyView();
        initRefreshView();

        initSwipeListView();
        mCollectionListView.setOnItemClickListener(this);

    }

    private void initEmptyView() {
        mEmptyView = findViewById(R.id.collection_empty_view);
        TextView emptyText = (TextView) mEmptyView.findViewById(R.id.empty_text);
        emptyText.setText(getString(R.string.empty_view_collection));
        emptyText.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.ico_my_collection_empty), null, null);
    }

    private void initRefreshView() {
        //设置刷新监听
        mCollectionSwipe.setOnRefreshListener(this);
        //设置下拉圆圈的颜色
        mCollectionSwipe.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light, android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        //设置手指在屏幕下拉多少距离会触发下拉刷新
        mCollectionSwipe.setDistanceToTriggerSync(400);
    }

    private RequestCall getBuild() {
        return OkHttpUtils.post().url(URL_COLLECTION_LIST)
                .addHeader(ACCESS_TOKEN, mAccessToken)
                .build();
    }

    @Override
    public void onRefresh() {
        getBuild().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.Companion.d("收藏列表E：" + e.getMessage());
                Util.showTimeOutNotice(currentContext);
            }

            @Override
            public void onResponse(String response, int id) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // 停止刷新
                        mCollectionSwipe.setRefreshing(false);
                    }
                }, 300);
                LogUtil.Companion.d("收藏列表：" + response);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()){
                    case "success":
                        //解析JSON，填充ListView
                        mPagedList = JSON.parseObject(response, InformationBean.class).getPagedResult().getPagedList();
                        if (mPagedList.size() == 0) {
                            mEmptyView.setVisibility(View.VISIBLE);
                        } else {
                            mEmptyView.setVisibility(View.GONE);
                        }
                        LogUtil.Companion.d(mPagedList.size() + "");
                        mCollectionAdapter = new CollectionAdapter(getApplicationContext(), mPagedList);
                        mCollectionListView.setAdapter(mCollectionAdapter);
                        break;
                    case "failure":
                        Util.showError(currentContext, errorBean.getReason());
                        break;
                }
            }
        });
    }

    private void initSwipeListView() {
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(DensityUtil.dp2px(getApplicationContext(), 90));
                // set a icon
                deleteItem.setIcon(R.mipmap.ic_delete);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
        // set creator
        mCollectionListView.setMenuCreator(creator);

        mCollectionListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                PagedListBean collectionInfo = mPagedList.get(position);
                switch (index) {
                    case 0:
                        // delete
                        delete(collectionInfo, position);
                        break;
                }
                return false;
            }
        });
    }


    private void delete(PagedListBean collectionInfo, final int position) {
        //删除收藏
        OkHttpUtils.post().url(URL_CANCEL_COLLECTION)
                .addHeader(ACCESS_TOKEN, mAccessToken)
                .addParams("newsId", collectionInfo.getId() + "")
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.Companion.d("取消收藏E：" + e.getMessage());
                Util.showTimeOutNotice(currentContext);
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.Companion.d("取消收藏：" + response);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        mPagedList.remove(position);
                        mCollectionAdapter.notifyDataSetChanged();
                        ToastUtil.showShort(getApplicationContext(), "取消收藏成功");
                        break;
                    case "failure":
                        Util.showError(currentContext, errorBean.getReason());
                        break;
                }
            }
        });

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mPagedList.get(position);
        int news_id = mPagedList.get(position).getId();
        List<ImageListBean> imageList = mPagedList.get(position).getImageList();
        String imageUrl;
        if (imageList.size() != 0) {
            imageUrl = imageList.get(0).getImageUrl();
        } else {
            imageUrl = Constant.DEFAULT_IMAGE;
        }
        Intent intent = new Intent(getApplicationContext(), InformationDetailActivity.class);
        intent.putExtra("newsId", news_id);
        intent.putExtra("imageUrl", imageUrl);
        startActivity(intent);
    }

    @Override
    public void leftOnClick() {
        finish();
    }

    @Override
    public void rightOnClick() {

    }
}
