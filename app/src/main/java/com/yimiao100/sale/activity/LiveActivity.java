package com.yimiao100.sale.activity;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.just.library.AgentWeb;
import com.yimiao100.sale.R;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.view.Html5WebView;
import com.yimiao100.sale.view.TitleView;


/**
 * H5直播界面
 */
public class LiveActivity extends BaseActivity implements TitleView.TitleBarOnClickListener {

    private LinearLayout mLayout;

    private AgentWeb mAgentWeb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live);

        TitleView titleView = (TitleView) findViewById(R.id.live_title);
        titleView.setOnTitleBarClick(this);
        mLayout = (LinearLayout) findViewById(R.id.live_web_layout);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams
                .MATCH_PARENT);
        mAgentWeb = AgentWeb.with(this)//传入Activity or Fragment
                .setAgentWebParent(mLayout, params)//传入AgentWeb 的父控件 ，如果父控件为 RelativeLayout ， 那么第二参数需要传入 RelativeLayout.LayoutParams ,第一个参数和第二个参数应该对应。
                .useDefaultIndicator()// 使用默认进度条
                .defaultProgressBarColor() // 使用默认进度条颜色
//                .setReceivedTitleCallback(mCallback) //设置 Web 页面的 title 回调
                .createAgentWeb()//
                .ready()
                .go("http://www.yimiaoquan100.com/ymt/api/live/show");
    }

    @Override
    protected void onPause() {
        mAgentWeb.getWebLifeCycle().onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mAgentWeb.getWebLifeCycle().onResume();
        super.onResume();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAgentWeb.getWebLifeCycle().onDestroy();
    }

    @Override
    public void leftOnClick() {
        finish();
    }

    @Override
    public void rightOnClick() {

    }
}
