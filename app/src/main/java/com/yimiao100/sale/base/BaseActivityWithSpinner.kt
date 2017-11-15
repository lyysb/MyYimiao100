package com.yimiao100.sale.base

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.ListView
import com.jaredrummler.materialspinner.MaterialSpinner
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout
import com.lcodecore.tkrefreshlayout.footer.LoadingView
import com.lcodecore.tkrefreshlayout.header.progresslayout.ProgressLayout
import com.yimiao100.sale.R
import com.yimiao100.sale.utils.LogUtil
import com.yimiao100.sale.utils.ToastUtil
import com.yimiao100.sale.view.TitleView
import org.jetbrains.anko.find

abstract class BaseActivityWithSpinner : BaseActivity(), TitleView.TitleBarOnClickListener {

    lateinit var title: TitleView
    lateinit var spinner: MaterialSpinner
    lateinit var refreshLayout: TwinklingRefreshLayout
    lateinit var listView: ListView
    lateinit var emptyView: View

    var position: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base_with_spinner)

        initVariate()

        initView()

        initData()
    }

    open fun initVariate() {

    }

    open fun initView() {
        title = find(R.id.spinner_title)
        spinner = find(R.id.spinner)
        spinner.gravity = Gravity.CENTER
        emptyView = find(R.id.spinner_empty_view)
        refreshLayout = find(R.id.spinner_refresh_layout)
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
        listView = find(R.id.spinner_list_view)
        // 初始化View监听
        initViewListener()
    }

    private fun initData() {
        title.setTitle(getTitleText())
        spinner.setItems(getSpinnerItems())
    }

    abstract fun getTitleText(): String?

    abstract fun getSpinnerItems(): ArrayList<String>

    private fun initViewListener() {
        title.setOnTitleBarClick(this)
        spinner.setOnItemSelectedListener { view, position, id, item ->
            LogUtil.d("spinner item click's \nposition is $position\nitem is $item")
            this.position = position
            refreshLayout.startRefresh()
        }
        refreshLayout.setOnRefreshListener(object : RefreshListenerAdapter() {
            override fun onRefresh(refreshLayout: TwinklingRefreshLayout?) {
                super.onRefresh(refreshLayout)
                onRefresh(position)
            }

            override fun onLoadMore(refreshLayout: TwinklingRefreshLayout) {
                super.onLoadMore(refreshLayout)
                if (page <= totalPage) {
                    onLoadMore(position)
                } else {
                    refreshLayout.finishLoadmore()
                    ToastUtil.showShort(currentContext, "全部加载完成")
                }
            }
        })
        listView.setOnItemClickListener { parent, view, position, id ->
            onListItemClick(position)
        }
    }

    fun handleEmptyView(list: ArrayList<*>) {
        if (list.size == 0) {
            emptyView.visibility = View.VISIBLE
        } else {
            emptyView.visibility = View.GONE
        }
    }

    abstract fun onRefresh(position: Int)

    abstract fun onListItemClick(position: Int)

    abstract fun onLoadMore(position: Int)

    override fun leftOnClick() {
        finish()
    }

    override fun rightOnClick() {
    }
}
