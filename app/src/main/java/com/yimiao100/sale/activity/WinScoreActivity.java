package com.yimiao100.sale.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.yimiao100.sale.R;
import com.yimiao100.sale.adapter.fragment.WinAdapter;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.fragment.WinScoreFragment;
import com.yimiao100.sale.fragment.WinTaskFragment;
import com.yimiao100.sale.view.TitleView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 学习/赚积分
 */
public class WinScoreActivity extends BaseActivity implements TitleView.TitleBarOnClickListener {

    @BindView(R.id.win_tabs)
    TabLayout mWinTabs;
    @BindView(R.id.win_view_pager)
    ViewPager mWinViewPager;
    @BindView(R.id.win_title)
    TitleView mWinTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_win_score);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        //设置标题点击
        mWinTitle.setOnTitleBarClick(this);
        //初始化ViewPager
        initViewPager();
        //初始化Tab
        initTabs();
    }

    private void initViewPager() {
        ArrayList<Fragment> fragments = new ArrayList<>();
        //全部任务
        fragments.add(new WinTaskFragment());
        //领取积分
        fragments.add(new WinScoreFragment());
        mWinViewPager.setAdapter(new WinAdapter(getSupportFragmentManager(), fragments));
    }

    private void initTabs() {
        mWinTabs.setupWithViewPager(mWinViewPager);
    }

    @Override
    public void leftOnClick() {
        finish();
    }

    @Override
    public void rightOnClick() {

    }
}
