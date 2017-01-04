package com.yimiao100.sale.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
import com.yimiao100.sale.adapter.listview.StudyCollectionAdapter;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.bean.ErrorBean;
import com.yimiao100.sale.bean.OpenClass;
import com.yimiao100.sale.bean.OpenClassBean;
import com.yimiao100.sale.bean.OpenClassResult;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.DensityUtil;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.ToastUtil;
import com.yimiao100.sale.utils.Util;
import com.yimiao100.sale.view.TitleView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

/**
 * 学习界面-我的收藏
 */
public class StudyCollectionActivity extends BaseActivity implements SwipeRefreshLayout
        .OnRefreshListener, TitleView.TitleBarOnClickListener, AdapterView.OnItemClickListener {

    @BindView(R.id.study_collection_title)
    TitleView mStudyCollectionTitle;
    @BindView(R.id.study_collection_list)
    SwipeMenuListView mStudyCollectionList;
    @BindView(R.id.study_collection_swipe)
    SwipeRefreshLayout mStudyCollectionSwipe;

    private final String URL_COLLECTION_LIST = Constant.BASE_URL +
            "/api/course/user_collection_list";
    private final String URL_CANCLE_COLLECTION = Constant.BASE_URL +
            "/api/course/cancel_collection";
    private final String COURSE_ID = "courseId";

    private ArrayList<OpenClass> mCollectClasses;
    private StudyCollectionAdapter mCollectionAdapter;
    private View mEmptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_collection);
        ButterKnife.bind(this);


        initView();

        onRefresh();

    }

    private void initView() {
        mStudyCollectionTitle.setOnTitleBarClick(this);
        initEmptyView();
        initRefresh();

        initSwipeMenuListView();
    }
    private void initEmptyView() {
        mEmptyView = findViewById(R.id.study_collection_empty_view);
        TextView emptyText = (TextView) mEmptyView.findViewById(R.id.empty_text);
        emptyText.setText("多收藏也不会怀孕……");
        emptyText.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.ico_my_collection_empty), null, null);
    }


    private void initRefresh() {
        //设置刷新监听
        mStudyCollectionSwipe.setOnRefreshListener(this);
        //设置下拉圆圈的颜色
        mStudyCollectionSwipe.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light, android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        //设置手指在屏幕下拉多少距离会触发下拉刷新
        mStudyCollectionSwipe.setDistanceToTriggerSync(400);

    }

    private void initSwipeMenuListView() {
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
//                // create "shareItem" item
//                SwipeMenuItem shareItem = new SwipeMenuItem(
//                        getApplicationContext());
//                // set item background
//                shareItem.setBackground(new ColorDrawable(Color.parseColor("#8cb3da")));
//                // set item width
//                shareItem.setWidth(DensityUtil.dp2px(getApplicationContext(), 55));
//                // set a icon
//                shareItem.setIcon(R.mipmap.ico_my_collection_share);


                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.parseColor("#8cb3da")));
                // set item width
                deleteItem.setWidth(DensityUtil.dp2px(getApplicationContext(), 55));
                // set a icon
                deleteItem.setIcon(R.mipmap.ico_my_collection_delete);


                // add to menu
//                menu.addMenuItem(shareItem);
                menu.addMenuItem(deleteItem);
            }
        };
        // set creator
        mStudyCollectionList.setMenuCreator(creator);

        mStudyCollectionList.setOnMenuItemClickListener(new SwipeMenuListView
                .OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        //删除
                        delete(position);
                        break;
                }
                return false;
            }
        });
        mStudyCollectionList.setOnItemClickListener(this);
    }

    private RequestCall getBuild() {
        return OkHttpUtils.post().url(URL_COLLECTION_LIST).addHeader(ACCESS_TOKEN, mAccessToken)
                .build();
    }


    private void delete(final int position) {
        //取消收藏
        OkHttpUtils.post().url(URL_CANCLE_COLLECTION).addHeader(ACCESS_TOKEN, mAccessToken)
                .addParams(COURSE_ID, mCollectClasses.get(position).getId() + "").build().execute
                (new StringCallback() {


            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.Companion.d("取消收藏E：" + e.getLocalizedMessage());
                Util.showTimeOutNotice(currentContext);
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.Companion.d("取消收藏：" + response);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        ToastUtil.showShort(getApplicationContext(), "取消收藏成功");
                        mCollectClasses.remove(position);
                        mCollectionAdapter.notifyDataSetChanged();
                        break;
                    case "failure":
                        Util.showError(currentContext, errorBean.getReason());
                        break;
                }
            }
        });
    }

    /**
     * 进入视频详情
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int courseId = mCollectClasses.get(position).getId();
        Intent intent = new Intent(this, VideoDetailActivity.class);
        intent.putExtra("courseId", courseId);
        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        getBuild().execute(new StringCallback() {

            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.Companion.d("学习课程收藏列表E：" + e.getLocalizedMessage());
                Util.showTimeOutNotice(currentContext);
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.Companion.d("学习课程收藏列表：" + response);
                mStudyCollectionSwipe.setRefreshing(false);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        OpenClassResult pagedResult = JSON.parseObject(response, OpenClassBean
                                .class).getPagedResult();
                        mPage = 2;
                        mTotalPage = pagedResult.getTotalPage();
                        mCollectClasses = pagedResult.getPagedList();
                        if (mCollectClasses.size() == 0) {
                            mEmptyView.setVisibility(View.VISIBLE);
                        } else {
                            mEmptyView.setVisibility(View.GONE);
                        }
                        //收藏课程数据
                        mCollectionAdapter = new StudyCollectionAdapter(mCollectClasses);
                        mStudyCollectionList.setAdapter(mCollectionAdapter);
                        break;
                    case "failure":
                        Util.showError(currentContext, errorBean.getReason());
                        break;
                }
            }
        });
    }

    @Override
    public void leftOnClick() {
        finish();
    }

    @Override
    public void rightOnClick() {

    }
}
