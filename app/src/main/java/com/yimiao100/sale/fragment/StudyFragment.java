package com.yimiao100.sale.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.footer.LoadingView;
import com.lcodecore.tkrefreshlayout.header.progresslayout.ProgressLayout;
import com.squareup.picasso.Picasso;
import com.yimiao100.sale.R;
import com.yimiao100.sale.activity.JumpActivity;
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
import com.yimiao100.sale.utils.DensityUtil;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.ScreenUtil;
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
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bingoogolapple.bgabanner.BGABanner;
import okhttp3.Call;

/**
 * 学习主页
 * Created by 亿苗通 on 2016/8/1.
 */
public class StudyFragment extends BaseFragment implements View.OnClickListener, AdapterView.OnItemClickListener, PullToRefreshListView.OnRefreshingListener, CarouselUtil.HandleCarouselListener {

    @BindView(R.id.study_class_list)
    ListView mStudyClassListView;
    @BindView(R.id.study_refresh_layout)
    TwinklingRefreshLayout mRefreshLayout;
    private final String URL_OPEN_CLASS_LIST = Constant.BASE_URL + "/api/course/open_list";
    private final String ACCESS_TOKEN = "X-Authorization-Token";
    private final String PAGE = "page";
    private final String PAGE_SIZE = "pageSize";


    private int mPage;
    private int mTotalPage;

    private ArrayList<OpenClass> mOpenClasses;
    private PublicClassAdapter mOpenClassAdapter;
    private BGABanner banner;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_study, null);
        ButterKnife.bind(this, view);

        initView();

        initData();
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (banner != null) {
            if (isVisibleToUser) {
                // 如果可见，滑动切换
                banner.startAutoPlay();
            } else {
                banner.stopAutoPlay();
            }
        }
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
        banner = (BGABanner) headerView.findViewById(R.id.study_banner);
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
        banner.setAdapter((banner, itemView, model, position) ->
                Picasso.with(getContext())
                        .load(((Carousel) model).getMediaUrl())
                        .placeholder(R.mipmap.ico_default_bannner)
                        .resize(ScreenUtil.getScreenWidth(getContext()), DensityUtil.dp2px(getContext(), 190))
                        .into((ImageView) itemView));
        List<String> desc = new ArrayList<>();
        for (Carousel carousel : carouselList) {
            desc.add(carousel.getObjectTitle());
        }
        banner.setData(carouselList, desc);
        banner.setDelegate((banner, itemView, model, position) -> {
            //进入视频详情页
            Intent intent = new Intent(getContext(), VideoDetailActivity.class);
            int courseId = carouselList.get(position).getObjectId();
            intent.putExtra("courseId", courseId);
            startActivity(intent);
        });
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
        return OkHttpUtils.post().url(URL_OPEN_CLASS_LIST).addHeader(ACCESS_TOKEN, accessToken)
                .addParams(PAGE, page + "").addParams(PAGE_SIZE, "10").build();
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
