package com.yimiao100.sale.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.footer.LoadingView;
import com.lcodecore.tkrefreshlayout.header.progresslayout.ProgressLayout;
import com.yimiao100.sale.R;
import com.yimiao100.sale.activity.MineAchievementActivity;
import com.yimiao100.sale.activity.VideoDetailActivity;
import com.yimiao100.sale.activity.VideoListActivity;
import com.yimiao100.sale.activity.WinScoreActivity;
import com.yimiao100.sale.adapter.listview.PublicClassAdapter;
import com.yimiao100.sale.adapter.peger.StudyAdAdapter;
import com.yimiao100.sale.base.BaseFragment;
import com.yimiao100.sale.bean.Carousel;
import com.yimiao100.sale.bean.ErrorBean;
import com.yimiao100.sale.bean.OpenClass;
import com.yimiao100.sale.bean.OpenClassBean;
import com.yimiao100.sale.bean.OpenClassResult;
import com.yimiao100.sale.ext.JSON;
import com.yimiao100.sale.utils.CarouselUtil;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.SharePreferenceUtil;
import com.yimiao100.sale.utils.ToastUtil;
import com.yimiao100.sale.utils.Util;
import com.yimiao100.sale.view.PullToRefreshListView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

/**
 * 学习主页
 * Created by 亿苗通 on 2016/8/1.
 */
public class StudyFragment extends BaseFragment implements View.OnClickListener, AdapterView
        .OnItemClickListener, ViewPager.OnPageChangeListener, StudyAdAdapter.OnClickListener,
        PullToRefreshListView.OnRefreshingListener, CarouselUtil.HandleCarouselListener {

    @BindView(R.id.study_class_list)
    ListView mStudyClassListView;
    @BindView(R.id.study_refresh_layout)
    TwinklingRefreshLayout mRefreshLayout;
    private ViewPager mStudyViewPager;
    private TextView mStudyClassName;
    private TextView mStudyAttribute;
    /**
     * 显示下一页
     */
    public static final int SHOW_NEXT_PAGE = 0;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_NEXT_PAGE:
                    showNextPage();
                    break;
            }
        }
    };

    private final String URL_OPEN_CLASS_LIST = Constant.BASE_URL + "/api/course/open_list";
    private final String ACCESS_TOKEN = "X-Authorization-Token";
    private final String PAGE = "page";
    private final String PAGE_SIZE = "pageSize";


    private String mAccessToken;
    private int mPage;
    private int mTotalPage;

    private ArrayList<OpenClass> mOpenClasses;
    private PublicClassAdapter mOpenClassAdapter;
    private ArrayList<Carousel> mCarouselList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_study, null);
        ButterKnife.bind(this, view);

        mAccessToken = (String) SharePreferenceUtil.get(getContext(), Constant.ACCESSTOKEN, "");

        initView();

        initData();
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // 如果可见，滑动切换
            mHandler.sendEmptyMessageDelayed(SHOW_NEXT_PAGE, 3000);
        } else {
            mHandler.removeMessages(SHOW_NEXT_PAGE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeMessages(SHOW_NEXT_PAGE);
    }

    /**
     * 初始化显示上部界面
     */
    private void initView() {
        initRefreshLayout();
        initListView();
    }

    private void initRefreshLayout() {
        ProgressLayout header = new ProgressLayout(getActivity());
        header.setColorSchemeResources(
                android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        mRefreshLayout.setHeaderView(header);
        LoadingView loadingView = new LoadingView(getActivity());
        mRefreshLayout.setBottomView(loadingView);
        mRefreshLayout.setFloatRefresh(true);
        mRefreshLayout.setOverScrollRefreshShow(false);
        mRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                super.onRefresh(refreshLayout);
                initData();
            }

            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                super.onLoadMore(refreshLayout);
                if (mPage <= mTotalPage) {
                    StudyFragment.this.onLoadMore();
                } else {
                    mRefreshLayout.finishLoadmore();
                    ToastUtil.showShort(getActivity(), "全部加载完成");
                }
            }
        });
    }

    private void initListView() {
        //初始化头部布局
        View headerView = View.inflate(getContext(), R.layout.head_study, null);
        mStudyViewPager = (ViewPager) headerView.findViewById(R.id.study_view_pager);
        mStudyViewPager.addOnPageChangeListener(this);
        mStudyClassName = (TextView) headerView.findViewById(R.id.study_class_name);
        mStudyAttribute = (TextView) headerView.findViewById(R.id.study_attribute);
        //推广考试
        headerView.findViewById(R.id.study_exam).setOnClickListener(this);
        //学习赚积分
        headerView.findViewById(R.id.study_study).setOnClickListener(this);
        //我的成就
        headerView.findViewById(R.id.study_mine).setOnClickListener(this);
        mStudyClassListView.addHeaderView(headerView);
        mStudyClassListView.setOnItemClickListener(this);
    }

    private void initData() {
        //初始化公开课数据
        initPublicClassList();
        //请求网络，设置轮播图
        CarouselUtil.getCarouselList(getActivity(), "course", this);
    }

    @Override
    public void handleCarouselList(@NotNull ArrayList<Carousel> carouselList) {
        mCarouselList = carouselList;
        StudyAdAdapter adAdapter = new StudyAdAdapter(mCarouselList);
        mStudyViewPager.setAdapter(adAdapter);
        adAdapter.setOnClickListener(StudyFragment.this);
        //显示当前选中的界面
        mStudyViewPager.setCurrentItem(adAdapter.getCount() / 2);
    }

    /**
     * 初始化公开课数据
     */
    private void initPublicClassList() {
        getBuild(1).execute(new StringCallback() {

            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.d("公开课列表E：" + e.getLocalizedMessage());
                if (mRefreshLayout.isShown()) {
                    mRefreshLayout.finishRefreshing();
                }
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.d("公开课列表：" + response);
                if (mRefreshLayout.isShown()) {
                    mRefreshLayout.finishRefreshing();
                }
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        OpenClassResult pagedResult = JSON.parseObject(response, OpenClassBean
                                .class).getPagedResult();
                        mPage = 2;
                        mTotalPage = pagedResult.getTotalPage();
                        mOpenClasses = pagedResult.getPagedList();
                        //公开课数据
                        mOpenClassAdapter = new PublicClassAdapter(mOpenClasses);
                        mStudyClassListView.setAdapter(mOpenClassAdapter);
                        break;
                    case "failure":
                        Util.showError(getActivity(), errorBean.getReason());
                        break;
                }
            }
        });
    }

    /**
     * 请求网络获取公开课数据
     *
     * @param page
     * @return
     */
    private RequestCall getBuild(int page) {
        return OkHttpUtils.post().url(URL_OPEN_CLASS_LIST).addHeader(ACCESS_TOKEN, mAccessToken)
                .addParams(PAGE, page + "").addParams(PAGE_SIZE, "10").build();
    }

    /**
     * 显示下一页ViewPager
     */
    public void showNextPage() {
        mStudyViewPager.setCurrentItem(mStudyViewPager.getCurrentItem() + 1);
        mHandler.sendEmptyMessageDelayed(SHOW_NEXT_PAGE, 3000);
    }

    @Override
    public void onClick(View v) {
        Class clz = null;
        switch (v.getId()) {
            case R.id.study_exam:
                //进入推广考试界面
                clz = VideoListActivity.class;
                break;
            case R.id.study_study:
                //进入学习赚积分界面
                clz = WinScoreActivity.class;
                break;
            case R.id.study_mine:
                //进入我的成就界面
                clz = MineAchievementActivity.class;
                break;
        }
        startActivity(new Intent(getContext(), clz));
    }

    /**
     * 进入课程详情界面
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //进入课程详情界面
        OpenClass openClass = mOpenClasses.get(position - 1);
        Intent intent = new Intent(getContext(), VideoDetailActivity.class);
        int courseId = openClass.getId();
        intent.putExtra("courseId", courseId);
        startActivity(intent);
    }

    @Override
    public void onPageSelected(int position) {
        //根据当前选择界面显示文字信息
        position = position % mCarouselList.size();
        mStudyClassName.setText(mCarouselList.get(position).getObjectTitle());
        switch (mCarouselList.get(position).getIntegralType()) {
            case "increase":
                mStudyAttribute.setText("+" + mCarouselList.get(position).getIntegralValue() +
                        "积分");
                break;
            case "decrease":
                mStudyAttribute.setText("-" + mCarouselList.get(position).getIntegralValue() +
                        "积分");
                break;
            case "free":
                mStudyAttribute.setText("免费");
                break;
        }
    }

    /**
     * 点击轮播图跳转到详情页
     *
     * @param position
     */
    @Override
    public void onClick(int position) {
        //进入视频详情页
        Intent intent = new Intent(getContext(), VideoDetailActivity.class);
        int courseId = mCarouselList.get(position).getObjectId();
        intent.putExtra("courseId", courseId);
        startActivity(intent);
    }

    public void onLoadMore() {

        getBuild(mPage).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.d("公开课列表E：" + e.getLocalizedMessage());
                if (mRefreshLayout.isShown()) {
                    mRefreshLayout.finishLoadmore();
                }
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.d("公开课列表：" + response);
                if (mRefreshLayout.isShown()) {
                    mRefreshLayout.finishLoadmore();
                }
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":

                        mPage++;
                        mOpenClasses.addAll(JSON.parseObject(response, OpenClassBean
                                .class).getPagedResult().getPagedList());
                        mOpenClassAdapter.notifyDataSetChanged();
                        break;
                    case "failure":
                        Util.showError(getActivity(), errorBean.getReason());
                        break;
                }
            }
        });

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    /**
     * 解决如下问题
     * java.lang.IllegalStateException: No host
     */
    @Override
    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
