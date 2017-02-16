package com.yimiao100.sale.base;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.yimiao100.sale.R;
import com.yimiao100.sale.view.TitleView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 可滑动多标签Activity
 */
public abstract class BaseActivityWithTab extends BaseActivity implements TitleView.TitleBarOnClickListener{

    @BindView(R.id.tab_title)
    TitleView mTitle;
    @BindView(R.id.tab_tabs)
    TabLayout mTabs;
    @BindView(R.id.tab_view_pager)
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_with_tab);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        initTitle();

        initViewPager();

        initTabs();
    }

    private void initTitle() {
        mTitle.setOnTitleBarClick(this);
        mTitle.setTitle(title());
    }

    /** 设置标题 */
    protected abstract String title();

    private void initViewPager() {
        mViewPager.setAdapter(new BaseFragmentPagerAdapter(getSupportFragmentManager(),setFragments()));
    }

    /**
     * @return Fragment集合
     */
    protected abstract ArrayList<BaseFragmentSingleList> setFragments();

    private void initTabs() {
        mTabs.setupWithViewPager(mViewPager);
    }

    @Override
    public void leftOnClick() {
        finish();
    }

    @Override
    public void rightOnClick() {

    }
}