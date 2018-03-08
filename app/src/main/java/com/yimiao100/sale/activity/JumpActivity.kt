package com.yimiao100.sale.activity


import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.LinearLayout
import com.just.library.AgentWeb
import com.just.library.ChromeClientCallbackManager
import com.yimiao100.sale.R
import com.yimiao100.sale.base.BaseActivity
import com.yimiao100.sale.view.TitleView
import org.jetbrains.anko.find
import org.jetbrains.anko.startActivity

/**
 * Web页面
 */
class JumpActivity : BaseActivity(), TitleView.TitleBarOnClickListener {

    lateinit var titleView: TitleView
    lateinit var contentLayout: LinearLayout
    lateinit var params: LinearLayout.LayoutParams
    lateinit var agentWeb: AgentWeb

    companion object {

        @JvmStatic
        fun start(context: Context, jumpUrl: String) {
            context.startActivity<JumpActivity>("jumpUrl" to jumpUrl)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jump)

        initView()

        initData()
    }

    private fun initView() {
        titleView = find(R.id.jump_title)
        titleView.setOnTitleBarClick(this)
        contentLayout = find(R.id.jump_web)
        params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams
                .MATCH_PARENT)
    }

    private fun initData() {
        val jumpUrl = intent.getStringExtra("jumpUrl")
        agentWeb = AgentWeb.with(this)//传入Activity or Fragment
                .setAgentWebParent(contentLayout, params)//传入AgentWeb 的父控件 ，如果父控件为 RelativeLayout ， 那么第二参数需要传入 RelativeLayout.LayoutParams ,第一个参数和第二个参数应该对应。
                .useDefaultIndicator()// 使用默认进度条
                .defaultProgressBarColor() // 使用默认进度条颜色
                .setReceivedTitleCallback { view, title ->
                    titleView.setTitle(title)
                } //设置 Web 页面的 title 回调
                .createAgentWeb()//
                .ready()
                .go(jumpUrl)
    }

    override fun onPause() {
        agentWeb.webLifeCycle.onPause()
        super.onPause()
    }

    override fun onResume() {
        agentWeb.webLifeCycle.onResume()
        super.onResume()
    }


    override fun onDestroy() {
        super.onDestroy()
        agentWeb.webLifeCycle.onDestroy()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (agentWeb.handleKeyEvent(keyCode, event)) {
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun leftOnClick() {
        finish()
    }

    override fun rightOnClick() {
    }

}
