package com.yimiao100.sale.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout
import com.lcodecore.tkrefreshlayout.footer.LoadingView
import com.lcodecore.tkrefreshlayout.header.progresslayout.ProgressLayout
import com.yimiao100.sale.R
import com.yimiao100.sale.vaccine.OverdueCorporateActivity
import com.yimiao100.sale.adapter.listview.OverdueAdapter
import com.yimiao100.sale.base.BaseFragment
import com.yimiao100.sale.bean.Overdue
import com.yimiao100.sale.utils.FormatUtils
import org.jetbrains.anko.find
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast

/**
 * 逾期-对公
 * Created by michel on 2017/10/31.
 */
class OverdueCorFragment : BaseFragment(), OverdueAdapter.OnCheckedChangeListener {

    lateinit var contentView: View
    lateinit var emptyView: View
    lateinit var refreshLayout: TwinklingRefreshLayout
    lateinit var listView: ListView
    lateinit var submitView: View
    lateinit var checkedCount: TextView
    lateinit var checkedAmount: TextView
    lateinit var overdueList: ArrayList<Overdue>

    var finalAmount: Double = 0.0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        contentView = inflater.inflate(R.layout.fragment_overdue, null)

        initView()

        initVariate()

        initData()
        return contentView
    }

    private fun initView() {
        initEmptyView()

        initRefreshLayout()

        initListView()

        initSubmitView()
    }

    private fun initEmptyView() {
        emptyView = contentView.find(R.id.overdue_empty_view)
    }

    private fun initRefreshLayout() {
        refreshLayout = contentView.find(R.id.overdue_refresh_layout)
        val header = ProgressLayout(context)
        header.setColorSchemeResources(
                android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light)
        refreshLayout.setHeaderView(header)
        val loadingView = LoadingView(context)
        refreshLayout.setBottomView(loadingView)
        refreshLayout.setFloatRefresh(true)
        refreshLayout.setOverScrollRefreshShow(false)
        refreshLayout.startRefresh()
        refreshLayout.setOnRefreshListener(object : RefreshListenerAdapter(){
            override fun onRefresh(refreshLayout: TwinklingRefreshLayout) {
                super.onRefresh(refreshLayout)
                refreshLayout.finishRefreshing()
            }

            override fun onLoadMore(refreshLayout: TwinklingRefreshLayout) {
                super.onLoadMore(refreshLayout)
                refreshLayout.finishLoadmore()
            }
        })
    }

    private fun initListView() {
        listView = contentView.find(R.id.overdue_list_view)
    }

    private fun initSubmitView() {
        submitView = contentView.find(R.id.overdue_submit)
        checkedCount = submitView.find(R.id.check_count)
        checkedAmount = submitView.find(R.id.check_amount)
        submitView.find<ImageButton>(R.id.check_submit).setOnClickListener {
            if (finalAmount != 0.0) {
                // 提交确认
                submitOverdue()
            } else {
                toast("请先选择您要提现的金额")
            }
        }
    }

    private fun initVariate() {
        overdueList = ArrayList()
        val overdueAmount = activity.intent.getDoubleExtra("overdueCorporateAmount", 0.0)
        if (overdueAmount != 0.0) {
            overdueList.add(Overdue(false, overdueAmount))
        }
        handleEmptyView(overdueList)
    }

    private fun initData() {
        val adapter = OverdueAdapter(overdueList)
        listView.adapter = adapter
        adapter.setOnCheckedChangeListener(this)
    }

    override fun onCheckedChanged(position: Int, isChecked: Boolean) {
        // todo 多条目数据逻辑待定
        if (isChecked) {
            checkedCount.text = "已选择：1"
            checkedAmount.text = "您目前申请提现金额是${
                FormatUtils.MoneyFormat(overdueList[position].overdueAmount)
            }元"
            finalAmount = overdueList[position].overdueAmount
        } else {
            checkedCount.text = "已选择：0"
            checkedAmount.text = "您目前申请提现金额是0.00元"
            finalAmount = 0.0
        }
    }


    private fun submitOverdue() {
        startActivity<OverdueCorporateActivity>("applyAmount" to finalAmount)
    }

    fun handleEmptyView(list: ArrayList<*>) {
        if (list.size == 0) {
            emptyView.visibility = View.VISIBLE
        } else {
            emptyView.visibility = View.GONE
        }
    }
}