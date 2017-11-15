package com.yimiao100.sale.insurance

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.RelativeLayout
import com.yimiao100.sale.R
import com.yimiao100.sale.adapter.listview.ReconInsAdapter
import com.yimiao100.sale.adapter.listview.ReconciliationAdapter
import com.yimiao100.sale.base.BaseActivitySingleList
import com.yimiao100.sale.bean.ErrorBean
import com.yimiao100.sale.bean.Reconciliation
import com.yimiao100.sale.bean.ReconciliationBean
import com.yimiao100.sale.bean.ReconciliationJson
import com.yimiao100.sale.ext.JSON
import com.yimiao100.sale.utils.Constant
import com.yimiao100.sale.utils.DensityUtil
import com.yimiao100.sale.utils.LogUtil
import com.yimiao100.sale.utils.Util
import com.yimiao100.sale.view.RegionSearchView
import com.yimiao100.sale.view.TitleView
import com.zhy.http.okhttp.OkHttpUtils
import com.zhy.http.okhttp.callback.StringCallback
import com.zhy.http.okhttp.request.RequestCall
import okhttp3.Call
import java.util.HashMap
import org.jetbrains.anko.startActivity

class ReconciliationInsuranceActivity : BaseActivitySingleList(), RegionSearchView.onSearchClickListener {

    var companyId: Int = -1
    lateinit var userAccountType: String
    var regionParams = HashMap<String, String>()
    lateinit var list: ArrayList<Reconciliation>
    lateinit var adapter: ReconInsAdapter

    val COMPANY_ID = "companyId"
    val USER_ACCOUNT_TYPE = "userAccountType"
    val URL_ORDER_LIST = "${Constant.BASE_URL}/api/insure/balance_order_list"

    override fun onCreate(savedInstanceState: Bundle?) {
        showLoadingProgress()
        super.onCreate(savedInstanceState)
        setEmptyView(getString(R.string.empty_view_reconciliation), R.mipmap.ico_reconciliation)
    }

    override fun initVariate() {
        super.initVariate()
        companyId = intent.getIntExtra("companyId", -1)
        userAccountType = intent.getStringExtra("userAccountType")
    }

    override fun initView() {
        super.initView()
        val searchView = RegionSearchView(this)
        searchView.setOnSearchClickListener(this)
        val layoutParams = RelativeLayout.LayoutParams(ViewGroup
                .LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(0, DensityUtil.dp2px(this, 46f), 0, 0)
        mEmptyView.layoutParams = layoutParams
        mListView.addHeaderView(searchView, null, false)
        mListView.dividerHeight = DensityUtil.dp2px(this, 0f)
    }

    override fun setTitle(titleView: TitleView) {
        titleView.setTitle("对账")
    }

    override fun onRefresh() {
        getBuild(1).execute(object : StringCallback() {
            override fun onError(call: Call, e: Exception, id: Int) {
                Util.showTimeOutNotice(currentContext)
                hideLoadingProgress()
            }

            override fun onResponse(response: String, id: Int) {
                mSwipeRefreshLayout.isRefreshing = false
                hideLoadingProgress()
                LogUtil.d("对账列表：\n$response")
                JSON.parseObject(response, ReconciliationJson::class.java)?.let {
                    when (it.status) {
                        "success" -> {
                            page = 2
                            totalPage = it.pagedResult.totalPage
                            list = it.pagedResult.pagedList
                            handleEmptyData(list)
                            adapter = ReconInsAdapter(list)
                            mListView.adapter = adapter
                        }
                        "failure" -> Util.showError(currentContext, it.reason)
                    }
                }
            }
        })
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val item = list[position - 1]
        item.tipStatus = 0
        adapter.notifyDataSetChanged()
        startActivity<ReconInsDetailActivity>(
                "orderId" to item.id.toString(),
                "bizId" to item.companyId.toString(),
                "companyName" to item.companyName,
                "productName" to item.productName,
//                "customerName" to item.customerName,
                "serialNo" to item.serialNo
        )

    }

    override fun onLoadMore() {
        getBuild(page).execute(object : StringCallback() {
            override fun onError(call: Call, e: Exception, id: Int) {
                Util.showTimeOutNotice(currentContext)
                hideLoadingProgress()
            }

            override fun onResponse(response: String, id: Int) {
                mListView.onLoadMoreComplete()
                hideLoadingProgress()
                LogUtil.d("对账列表：\n$response")
                JSON.parseObject(response, ReconciliationJson::class.java)?.let {
                    when (it.status) {
                        "success" -> {
                            page ++
                            list.addAll(it.pagedResult.pagedList)
                            adapter.notifyDataSetChanged()
                        }
                        "failure" -> Util.showError(currentContext, it.reason)
                    }
                }
            }
        })
    }

    override fun regionSearch(regionIDs: HashMap<String, String>) {
        regionParams = regionIDs
        onRefresh()
    }

    private fun getBuild(page: Int): RequestCall {
        return OkHttpUtils.post().url(URL_ORDER_LIST).addHeader(ACCESS_TOKEN, accessToken)
                .params(regionParams)
                .addParams(PAGE, page.toString() + "").addParams(PAGE_SIZE, pageSize)
                .addParams(COMPANY_ID, companyId.toString())
                .addParams(USER_ACCOUNT_TYPE, userAccountType).build()
    }
}
