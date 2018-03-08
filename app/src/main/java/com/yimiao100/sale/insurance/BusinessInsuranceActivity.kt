package com.yimiao100.sale.insurance

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import com.yimiao100.sale.adapter.listview.BusinessAdapter
import com.yimiao100.sale.base.BaseActivitySingleList
import com.yimiao100.sale.bean.Business
import com.yimiao100.sale.bean.BusinessJson
import com.yimiao100.sale.ext.JSON
import com.yimiao100.sale.utils.Constant
import com.yimiao100.sale.utils.LogUtil
import com.yimiao100.sale.utils.Util
import com.yimiao100.sale.view.TitleView
import com.zhy.http.okhttp.OkHttpUtils
import com.zhy.http.okhttp.callback.StringCallback
import com.zhy.http.okhttp.request.RequestCall
import okhttp3.Call
import org.jetbrains.anko.startActivity
import java.lang.Exception

class BusinessInsuranceActivity: BaseActivitySingleList() {

    lateinit var userAccountType: String
    lateinit var list: ArrayList<Business>
    lateinit var adapter: BusinessAdapter
    lateinit var vendorId: String

    val USER_ACCOUNT_TYPE = "userAccountType"
    val VENDOR_ID = "vendorId"
    val URL_BUSINESS: String = "${Constant.BASE_URL}/api/insure/user_order_list"

    companion object {

        @JvmStatic
        fun start(context: Context, vendorId: String, userAccountType: String){
            context.startActivity<BusinessInsuranceActivity>(
                    "userAccountType" to userAccountType,
                    "vendorId" to vendorId)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        showLoadingProgress()
        onRefresh()
    }

    override fun initVariate() {
        super.initVariate()
        userAccountType = intent.getStringExtra("userAccountType")
        vendorId = intent.getStringExtra("vendorId")
    }

    override fun setTitle(titleView: TitleView) {
        titleView.setTitle("我的业务")
    }

    override fun onRefresh() {
        getBuild(1).execute(object : StringCallback(){
            override fun onResponse(response: String, id: Int) {
                hideLoadingProgress()
                LogUtil.d("response is\n$response")
                mSwipeRefreshLayout.isRefreshing = false
                JSON.parseObject(response, BusinessJson::class.java)?.let {
                    when (it.status) {
                        "success" -> {
                            page = 2
                            totalPage = it.pagedResult.totalPage
                            list = it.pagedResult.pagedList
                            handleEmptyData(list)
                            adapter = BusinessAdapter(list)
                            mListView.adapter = adapter
                        }
                        "failure" -> Util.showError(currentContext, it.reason)
                    }
                }
            }

            override fun onError(call: Call?, e: Exception, id: Int) {
                hideLoadingProgress()
                e.printStackTrace()
                mSwipeRefreshLayout.isRefreshing = false
                Util.showTimeOutNotice(currentContext)
            }

        })
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        //获得条目数据，携带到详细界面
        val order = list[position]
        LogUtil.d("order is\n$order")
        var clz: Class<*>? = null
        //获取订单状态Key
        val orderStatus = order.orderStatus
        //根据订单状态名，选择打开不同的Activity
        when (orderStatus) {
            "unpaid" ->
                //最初状态-待支付
                clz = BusinessUnpaidActivity::class.java
            "bidding" ->
                //第一状态-竞标中
                clz = BusinessSubmitActivity::class.java
            "auditing" ->
                //第二状态-审核中-作废
//                clz = OrderLaterActivity.class;
                return
            "to_be_signed" ->
                //第三状态-待签约
                clz = BusinessAlreadyActivity::class.java
            "already_signed" ->
                //第四状态-已签约
                clz = BusinessCompletedActivity::class.java
            "end" ->
                clz = BusinessEndActivity::class.java
            "not_passed", "defaulted" ->
                //进入错误界面--已违约|未通过
                clz = BusinessErrorActivity::class.java
        }
        val intent = Intent(this, clz)
        intent.putExtra("order", order)
        startActivity(intent)
    }

    override fun onLoadMore() {
        getBuild(page).execute(object : StringCallback(){
            override fun onResponse(response: String, id: Int) {
                LogUtil.d("response is\n$response")
                mListView.onLoadMoreComplete()
                JSON.parseObject(response, BusinessJson::class.java)?.let {
                    when (it.status) {
                        "success" -> {
                            page++
                            list.addAll(it.pagedResult.pagedList)
                            adapter.notifyDataSetChanged()
                        }
                        "failure" -> Util.showError(currentContext, it.reason)
                    }
                }
            }

            override fun onError(call: Call?, e: Exception, id: Int) {
                e.printStackTrace()
                mListView.onLoadMoreComplete()
                Util.showTimeOutNotice(currentContext)
            }

        })
    }

    private fun getBuild(page: Int): RequestCall {
        return OkHttpUtils.post().url(URL_BUSINESS).addHeader(ACCESS_TOKEN, accessToken)
                .addParams(PAGE, page.toString()).addParams(PAGE_SIZE, pageSize)
                .addParams(USER_ACCOUNT_TYPE, userAccountType)
                .addParams(VENDOR_ID, vendorId)
                .build()
    }
}

