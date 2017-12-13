package com.yimiao100.sale.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.yimiao100.sale.R;
import com.yimiao100.sale.adapter.peger.GuideAdapter;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.login.*;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.SharePreferenceUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GuideActivity extends BaseActivity {

    @BindView(R.id.guide_view_pager)
    ViewPager mGuideViewPager;

    int[] guide = {
            R.mipmap.ico_guide_one,
            R.mipmap.ico_guide_two,
            R.mipmap.ico_guide_three
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        ButterKnife.bind(this);

        GuideAdapter guideAdapter = new GuideAdapter(guide);
        mGuideViewPager.setAdapter(guideAdapter);
        mGuideViewPager.setCurrentItem(0);
        guideAdapter.setOnPagerClickListener(new GuideAdapter.onPagerClickListener() {
            @Override
            public void onClick() {
                if (mGuideViewPager.getCurrentItem() == 2) {
                    //进入登录界面
//                    startActivity(new Intent(currentContext, LoginActivity.class));
                    com.yimiao100.sale.login.LoginActivity.start(currentContext);
                    //记录不是第一次登录
                    SharePreferenceUtil.put(currentContext, Constant.IS_FIRST, false);
                    finish();
                }
            }
        });
    }
}
