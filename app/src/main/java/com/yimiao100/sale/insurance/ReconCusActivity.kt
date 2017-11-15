package com.yimiao100.sale.insurance

import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.yimiao100.sale.R
import com.yimiao100.sale.adapter.listview.ReconCusAdapter
import com.yimiao100.sale.base.BaseActivitySingleList
import com.yimiao100.sale.bean.*
import com.yimiao100.sale.ext.JSON
import com.yimiao100.sale.utils.*
import com.yimiao100.sale.view.TitleView
import com.zhy.http.okhttp.OkHttpUtils
import com.zhy.http.okhttp.callback.StringCallback
import com.zhy.http.okhttp.request.RequestCall
import okhttp3.Call
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.find
import org.jetbrains.anko.startActivity
import java.lang.Exception

class ReconCusActivity : BaseActivitySingleList() {

    val URL_CUSTOMER = "${Constant.BASE_URL}/api/insure/balance_customer_list"
    val COMPANY_ID = "companyId"
    val ORDER_ID = "orderId"
    val CUSTOMER_NAME = "customerName"
    var customerName = ""
    lateinit var companyId: String
    lateinit var orderId: String
    lateinit var list: ArrayList<ReconCus>
    lateinit var adapter: ReconCusAdapter
    lateinit var listTitle: TextView


    val URL_UPDATE_TIPS = "${Constant.BASE_URL}/api/tip/update_tip_status"
    val TIP_TYPE = "tipType"
    val tipType = "insurance_order_balance"
    val BIZ_ID = "bizId"

    override fun initVariate() {
        super.initVariate()
        orderId = intent.getStringExtra("orderId")
        companyId = intent.getStringExtra("companyId")
    }

    override fun initView() {
        super.initView()
        val headerView = View.inflate(this, R.layout.head_recon_cus, null)
        val inputView = headerView.find<EditText>(R.id.head_recon_cus_input)
        headerView.find<ImageView>(R.id.head_recon_cus_search).setOnClickListener {
            customerName = inputView.text.toString()
            LogUtil.d("companyName is\n$customerName")
            mSwipeRefreshLayout.isRefreshing = true
            onRefresh()
        }
        listTitle = headerView.find(R.id.head_recon_cus_tv)


        mListView.addHeaderView(headerView)
    }

    override fun initData() {
        super.initData()
        showLoadingProgress()
    }

    override fun onRefresh() {
        requestCall(1).execute(object : StringCallback(){
            override fun onResponse(response: String, id: Int) {
                hideLoadingProgress()
                mSwipeRefreshLayout.isRefreshing = false
                LogUtil.d("response is\n$response")
                JSON.parseObject(response, ReconCusJson::class.java)?.let {
                    when (it.status) {
                        "success" -> {
                            page = 2
                            totalPage = it.customerResult.totalPage
                            list = it.customerResult.customerList
                            listTitle.text = "当前显示${list.size}条客户信息"
                            adapter = ReconCusAdapter(list)
                            handleEmptyData(list)
                            mListView.adapter = adapter
                            // 更新小圆点
                            updateTips()
                        }
                        else -> Util.showError(currentContext, it.reason)
                    }
                }
            }

            override fun onError(call: Call?, e: Exception, id: Int) {
                e.printStackTrace()
                hideLoadingProgress()
            }

        })
    }

    private fun updateTips() {
        OkHttpUtils.post().url(URL_UPDATE_TIPS).addHeader(ACCESS_TOKEN, accessToken)
                .addParams(TIP_TYPE, tipType).addParams(BIZ_ID, companyId)
                .addParams(ORDER_ID, orderId)
                .build().execute(object : StringCallback(){
            override fun onResponse(response: String, id: Int) {
                LogUtil.d("update tips result is\n$response")
                JSON.parseObject(response, ErrorBean::class.java)?.let {
                    when (it.status) {
                        "success" -> {
                            // 发布事件
                            val event = Event()
                            Event.eventType = EventType.ORDER_BALANCE
                            EventBus.getDefault().post(event)
                        }
                        "failure" -> Util.showError(currentContext, it.reason)
                    }
                }
            }

            override fun onError(call: Call?, e: Exception, id: Int) {
                LogUtil.d("update tips error")
                e.printStackTrace()
                Util.showTimeOutNotice(currentContext)
            }

        })
    }

    private fun requestCall(page: Int): RequestCall {
        return OkHttpUtils.post().url(URL_CUSTOMER).addHeader(ACCESS_TOKEN, accessToken)
                .addParams(PAGE, page.toString()).addParams(PAGE_SIZE, pageSize)
                .addParams(COMPANY_ID, companyId).addParams(ORDER_ID, orderId)
                .addParams(CUSTOMER_NAME, customerName)
                .build()
    }

    override fun onLoadMore() {
        requestCall(page).execute(object : StringCallback(){
            override fun onResponse(response: String, id: Int) {
                mListView.onLoadMoreComplete()
                LogUtil.d("response is\n$response")
                JSON.parseObject(response, ReconCusJson::class.java)?.let {
                    when (it.status) {
                        "success" -> {
                            page++
                            list.addAll(it.customerResult.customerList)
                            listTitle.text = "当前显示${list.size}条客户信息"
                            adapter.notifyDataSetChanged()
                        }
                        else -> Util.showError(currentContext, it.reason)
                    }
                }
            }

            override fun onError(call: Call?, e: Exception, id: Int) {
                e.printStackTrace()
            }

        })
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        startActivity<ReconInsDetailActivity>(
                "customerId" to list[position - 1].customerId.toString(),
                "customerName" to list[position - 1].customerName,
                "orderId" to orderId
        )
    }

    override fun setTitle(titleView: TitleView) {
        titleView.setTitle("客户列表")
    }


}
