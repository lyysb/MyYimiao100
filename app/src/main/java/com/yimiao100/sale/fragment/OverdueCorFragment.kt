package com.yimiao100.sale.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
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
import com.yimiao100.sale.bean.UserFundBean
import com.yimiao100.sale.ext.JSON
import com.yimiao100.sale.utils.Constant
import com.yimiao100.sale.utils.FormatUtils
import com.yimiao100.sale.utils.LogUtil
import com.yimiao100.sale.utils.Util
import com.zhy.http.okhttp.OkHttpUtils
import com.zhy.http.okhttp.callback.StringCallback
import okhttp3.Call
import org.jetbrains.anko.find
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast
import java.lang.Exception

/**
 * 逾期-对公
 * Created by michel on 2017/10/31.
 * update by michel on 2017年11月16日 -- 新增下拉刷新
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
    lateinit var adapter: OverdueAdapter


    val URL_USER_FUND_ALL = "${Constant.BASE_URL}/api/fund/user_fund_all"
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
        refreshLayout.setOnRefreshListener(object : RefreshListenerAdapter(){
            override fun onRefresh(refreshLayout: TwinklingRefreshLayout) {
                super.onRefresh(refreshLayout)
                refreshData()
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
        adapter = OverdueAdapter(overdueList)
        listView.adapter = adapter
        adapter.setOnCheckedChangeListener(this)
    }

    private fun refreshData() {
        checkedCount.text = "已选择：0"
        checkedAmount.text = "您目前申请提现的金额是0.00元"
        finalAmount = 0.0
        OkHttpUtils.post().url(URL_USER_FUND_ALL).addHeader(ACCESS_TOKEN, accessToken)
                .build().execute(object : StringCallback() {
            override fun onResponse(response: String, id: Int) {
                refreshLayout.finishRefreshing()
                LogUtil.d("response is\n$response")
                JSON.parseObject(response, UserFundBean::class.java)?.let {
                    when (it.status) {
                        "success" -> {
                            overdueList.clear()
                            it.userFundAll?.let {
                                val overdueAmount = it.vaccineCorporateAdvance
                                if (overdueAmount != 0.0) {
                                    overdueList.add(Overdue(false, overdueAmount))
                                }
                            }
                            adapter.notifyDataSetChanged()
                            handleEmptyView(overdueList)
                        }
                        else -> Util.showError(activity, it.reason)
                    }
                }
            }

            override fun onError(call: Call?, e: Exception, id: Int) {
                refreshLayout.finishRefreshing()
                e.printStackTrace()
            }

        })
    }

    override fun onCheckedChanged(position: Int, isChecked: Boolean) {
        // todo 多条目数据逻辑待定
        if (isChecked) {
            checkedCount.text = "已选择：1"
            checkedAmount.text = "您目前申请提现的金额是${
                FormatUtils.MoneyFormat(overdueList[position].overdueAmount)
            }元"
            finalAmount = overdueList[position].overdueAmount
        } else {
            checkedCount.text = "已选择：0"
            checkedAmount.text = "您目前申请提现的金额是0.00元"
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