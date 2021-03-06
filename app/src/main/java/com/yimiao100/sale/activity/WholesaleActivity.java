package com.yimiao100.sale.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.just.library.AgentWeb;
import com.yimiao100.sale.R;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.view.Html5WebView;

/**
 * 批签发
 */
public class WholesaleActivity extends BaseActivity {

    private LinearLayout mLayout;
    private AgentWeb mAgentWeb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wholesale);

        mLayout = (LinearLayout) findViewById(R.id.web_layout);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams
                .MATCH_PARENT);
        mAgentWeb = AgentWeb.with(this)//传入Activity or Fragment
                .setAgentWebParent(mLayout, params)//传入AgentWeb 的父控件 ，如果父控件为 RelativeLayout ， 那么第二参数需要传入 RelativeLayout.LayoutParams ,第一个参数和第二个参数应该对应。
                .useDefaultIndicator()// 使用默认进度条
                .defaultProgressBarColor() // 使用默认进度条颜色
                .createAgentWeb()//
                .ready()
                .go("http://www.yimiao100.com/ymq_analyze/index.html#/query");

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mAgentWeb.handleKeyEvent(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
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
    public void onDestroy() {
        mAgentWeb.getWebLifeCycle().onDestroy();
        super.onDestroy();
    }
}
