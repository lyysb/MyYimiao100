package com.yimiao100.sale.base

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ListView
import android.widget.TextView
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout
import com.lcodecore.tkrefreshlayout.footer.LoadingView
import com.lcodecore.tkrefreshlayout.header.progresslayout.ProgressLayout
import com.yimiao100.sale.R
import com.yimiao100.sale.view.TitleView
import org.jetbrains.anko.find
import org.jetbrains.anko.toast

abstract class BaseActivitySingleList2 : BaseActivity(), TitleView.TitleBarOnClickListener {

    protected lateinit var refreshLayout: TwinklingRefreshLayout
    protected lateinit var listView: ListView
    protected lateinit var emptyView: View
    protected lateinit var emptyText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base_single_list2)

        initVariate()

        initView()
    }

    open fun initVariate() {
        // 获取必要变量
    }

    open fun initView() {
        initTitleView()

        initRefreshLayout()

        initListView()

        initEmptyView()
    }

    private fun initTitleView() {
        val titleView = find<TitleView>(R.id.single_list_title)
        titleView.setOnTitleBarClick(this)
        titleView.setTitle(getTitleText())
    }

    private fun initRefreshLayout() {
        refreshLayout = find<TwinklingRefreshLayout>(R.id.single_list_refresh_layout)
        val header = ProgressLayout(this)
        header.setColorSchemeResources(
                android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light)
        refreshLayout.setHeaderView(header)
        val loadingView = LoadingView(this)
        refreshLayout.setBottomView(loadingView)
        refreshLayout.setFloatRefresh(true)
        refreshLayout.setOverScrollRefreshShow(false)
        refreshLayout.startRefresh()

        refreshLayout.setOnRefreshListener(object : RefreshListenerAdapter(){
            override fun onRefresh(refreshLayout: TwinklingRefreshLayout) {
                super.onRefresh(refreshLayout)
                // 刷新数据
                onRefreshData(refreshLayout)
            }

            override fun onLoadMore(refreshLayout: TwinklingRefreshLayout) {
                super.onLoadMore(refreshLayout)
                if (page <= totalPage) {
                    onLoadMoreData(refreshLayout)
                } else {
                    refreshLayout.finishLoadmore()
                    toast("全部加载完成")
                }
            }
        })
    }

    private fun initListView() {
        listView = find(R.id.single_list_list_view)
        listView.setOnItemClickListener { parent, view, position, id ->
            onListItemClick(position)
        }
    }

    private fun initEmptyView() {
        emptyView = find(R.id.single_list_empty_view)
        emptyText = emptyView.find(R.id.empty_text)
    }

    abstract fun getTitleText(): String

    abstract fun onRefreshData(refreshLayout: TwinklingRefreshLayout)

    abstract fun onLoadMoreData(refreshLayout: TwinklingRefreshLayout)

    abstract fun onListItemClick(position: Int)

    fun handleEmptyView(list: ArrayList<*>) {
        if (list.size == 0) {
            emptyView.visibility = View.VISIBLE
        } else {
            emptyView.visibility = View.GONE
        }
    }

    override fun leftOnClick() {
        finish()
    }

    override fun rightOnClick() {
    }

}
