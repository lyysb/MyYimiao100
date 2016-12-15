package com.yimiao100.sale.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.yimiao100.sale.R;
import com.yimiao100.sale.adapter.fragment.StudyTaskAdapter;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.fragment.StudyAchievementFragment;
import com.yimiao100.sale.fragment.StudyUnAchievedFragment;
import com.yimiao100.sale.view.TitleView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 学习任务
 */
public class StudyTaskActivity extends BaseActivity implements TitleView.TitleBarOnClickListener {

    @BindView(R.id.study_task_title)
    TitleView mStudyTaskTitle;
    @BindView(R.id.study_task_tabs)
    TabLayout mStudyTaskTabs;
    @BindView(R.id.study_task_view_pager)
    ViewPager mStudyTaskViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_task);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        mStudyTaskTitle.setOnTitleBarClick(this);

        initViewPager();

        initTabs();
    }

    /**
     * 初始化ViewPager
     */
    private void initViewPager() {
        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(new StudyAchievementFragment());
        fragments.add(new StudyUnAchievedFragment());
        mStudyTaskViewPager.setAdapter(new StudyTaskAdapter(getSupportFragmentManager(), fragments));
    }

    private void initTabs() {
        mStudyTaskTabs.setupWithViewPager(mStudyTaskViewPager);
    }

    @Override
    public void leftOnClick() {
        finish();
    }

    @Override
    public void rightOnClick() {

    }
}
