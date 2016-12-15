package com.yimiao100.sale.activity;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.yimiao100.sale.R;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.view.Html5WebView;


public class LiveActivity extends BaseActivity {

    private LinearLayout mLayout;
    private Html5WebView mWebView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live);

        mLayout = (LinearLayout) findViewById(R.id.live_web_layout);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams
                .MATCH_PARENT);
        mWebView = new Html5WebView(this);
        mWebView.setLayoutParams(params);
        mLayout.addView(mWebView);

         mWebView.loadUrl("http://www.yimiaoquan100.com/ymt/api/live/show");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLayout.removeView(mWebView);
        mWebView.destroy();
    }
}
