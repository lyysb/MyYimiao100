package com.yimiao100.sale.activity

import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import com.yimiao100.sale.R
import com.yimiao100.sale.base.BaseActivity
import com.yimiao100.sale.view.Html5WebView
import org.jetbrains.anko.find

/**
 * 活动界面
 */
class ActivityActivity : BaseActivity() {

    lateinit var mLayout: LinearLayout
    lateinit var mWebView: Html5WebView
    lateinit var pageJumpUrl: String
    var params: HashMap<String, String> = HashMap()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_activity)

        initView()

        initVariate()

        initData()
    }

    override fun onResume() {
        // 不会执行检测广告
        isActive = true
        super.onResume()
    }



    private fun initView() {
        mLayout = find(R.id.activity_web_layout)
        val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams
                .MATCH_PARENT)
//        Html5WebView.shouldOverrideUrlLoading = true
        mWebView = Html5WebView(this)
        mWebView.layoutParams = params
        mLayout.addView(mWebView)
    }

    private fun initVariate() {
        pageJumpUrl = intent.getStringExtra("pageJumpUrl")
        params.put(ACCESS_TOKEN, accessToken)
    }

    private fun initData() {
        mWebView.loadUrl(pageJumpUrl, params)
    }

//    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
//        if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
//            mWebView.goBack()
//            return true
//        }
//        return super.onKeyDown(keyCode, event)
//    }

    override fun onDestroy() {
        super.onDestroy()
        mLayout.removeView(mWebView)
        mWebView.removeAllViews()
        mWebView.destroy()
    }
}
