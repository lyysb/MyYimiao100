package com.yimiao100.sale.activity


import com.yimiao100.sale.adapter.listview.VendorArrayAdapter
import com.yimiao100.sale.adapter.listview.VendorInsAdapter
import com.yimiao100.sale.base.BaseActivityWithSpinner
import com.yimiao100.sale.bean.*
import com.yimiao100.sale.ext.JSON
import com.yimiao100.sale.insurance.BusinessInsuranceActivity
import com.yimiao100.sale.insurance.ReconCusActivity
import com.yimiao100.sale.utils.Constant
import com.yimiao100.sale.utils.LogUtil
import com.yimiao100.sale.utils.ToastUtil
import com.yimiao100.sale.utils.Util
import com.yimiao100.sale.vaccine.BusinessVaccineActivity
import com.yimiao100.sale.view.RegionSearchView
import com.zhy.http.okhttp.OkHttpUtils
import com.zhy.http.okhttp.callback.StringCallback
import com.zhy.http.okhttp.request.RequestCall
import okhttp3.Call
import org.jetbrains.anko.startActivity
import java.lang.Exception

/**
 *
 */
class VendorArrayActivity : BaseActivityWithSpinner(), RegionSearchView.onSearchClickListener {


    lateinit var type: String
    lateinit var userAccountType: String
    lateinit var bizType: String
    lateinit var URL_VENDOR: String
    lateinit var list: ArrayList<BizList>
    lateinit var listIns: ArrayList<InsVendor>
    lateinit var headerView: RegionSearchView
    lateinit var adapterIns: VendorInsAdapter

    private var mRegionIDs = java.util.HashMap<String, String>()

    val USER_ACCOUNT_TYPE = "userAccountType"
    val BIZ_TYPE = "bizType"
    val RECONCILIATION = "reconciliation"
    val ORDER = "order"

    override fun initVariate() {
        super.initVariate()
        type = intent.getStringExtra("type")
    }

    override fun initView() {
        super.initView()
        headerView = RegionSearchView(this)
        headerView.setOnSearchClickListener(this)
    }


    override fun getTitleText(): String? {
        when (type) {
            RECONCILIATION -> {
                return "对账列表"
            }
            ORDER -> {
                return "业务列表"
            }
            else -> {
                return "unknown"
            }
        }
    }

    override fun getSpinnerItems(): ArrayList<String> {
        val spinnerItems = ArrayList<String>()
        spinnerItems.add("疫苗对公")
        spinnerItems.add("疫苗个人")
        if (Constant.isInsurance) {
            // 开启保险模块
            spinnerItems.add("保险对公")
            spinnerItems.add("保险个人")
        }
        return spinnerItems
    }

    override fun onRefresh(position: Int) {
        when (type) {
            RECONCILIATION -> {
                when (position) {
                    0 -> {
                        URL_VENDOR = "${Constant.BASE_URL}/api/biz/balance_biz"
                        userAccountType = "corporate"
                        requestVendorData("vaccine", "corporate")
                    }
                    1 -> {
                        URL_VENDOR = "${Constant.BASE_URL}/api/biz/balance_biz"
                        userAccountType = "personal"
                        requestVendorData("vaccine", "personal")
                    }
                    2 -> {
                        URL_VENDOR = "${Constant.BASE_URL}/api/insure/balance_order_list"
                        requestInsVendorData("insurance", "corporate")
                    }
                    3 -> {
                        URL_VENDOR = "${Constant.BASE_URL}/api/insure/balance_order_list"
                        requestInsVendorData("insurance", "personal")
                    }
                }
            }
            ORDER -> {
                when (position) {
                    0 -> {
                        URL_VENDOR = "${Constant.BASE_URL}/api/biz/user_biz"
                        userAccountType = "corporate"
                        requestVendorData("vaccine", "corporate")
                    }
                    1 -> {
                        URL_VENDOR = "${Constant.BASE_URL}/api/biz/user_biz"
                        userAccountType = "personal"
                        requestVendorData("vaccine", "personal")
                    }
                    2 -> {
                        URL_VENDOR = "${Constant.BASE_URL}/api/biz/user_biz"
                        userAccountType = "corporate"
                        requestVendorData("insurance", "corporate")
                    }
                    3 -> {
                        URL_VENDOR = "${Constant.BASE_URL}/api/biz/user_biz"
                        userAccountType = "personal"
                        requestVendorData("insurance", "personal")
                    }
                }
            }
            else -> {
                ToastUtil.showShort(this, "error type：$type")
                return
            }
        }
    }

    private fun requestVendorData(bizType: String, userAccountType: String) {
        OkHttpUtils.post().url(URL_VENDOR).addHeader(ACCESS_TOKEN, accessToken)
                .addParams(BIZ_TYPE, bizType).addParams(USER_ACCOUNT_TYPE, userAccountType)
                .build().execute(object : StringCallback() {
            override fun onResponse(response: String, id: Int) {
                refreshLayout.finishRefreshing()
                LogUtil.d("response is\n$response")
                JSON.parseObject(response, BizListJson::class.java)?.let {
                    when (it.status) {
                        "success" -> {
                            list = it.bizList
                            val adapter = VendorArrayAdapter(it.bizList)
                            handleEmptyView(list)
                            listView.adapter = adapter
                            if (listView.headerViewsCount != 0) {
                                listView.removeHeaderView(headerView)
                            }
                        }
                        "failure" -> Util.showError(currentContext, it.reason)
                    }
                }
            }

            override fun onError(call: Call?, e: Exception, id: Int) {
                e.printStackTrace()
                refreshLayout.finishRefreshing()
                Util.showTimeOutNotice(currentContext)
            }

        })
    }

    private fun requestInsVendorData(bizType: String, userAccountType: String) {
        requestCall(1, bizType, userAccountType).execute(object : StringCallback() {
            override fun onResponse(response: String, id: Int) {
                refreshLayout.finishRefreshing()
                LogUtil.d("response is\n$response")
                JSON.parseObject(response, InsVendorJson::class.java)?.let {
                    when (it.status) {
                        "success" -> {
                            page = 2
                            totalPage = it.pagedResult.totalPage
                            listIns = it.pagedResult.pagedList
                            adapterIns = VendorInsAdapter(listIns)
                            handleEmptyView(listIns)
                            listView.adapter = adapterIns
                            if (listView.headerViewsCount == 0) {
                                listView.addHeaderView(headerView)
                            }
                        }
                        "failure" -> Util.showError(currentContext, it.reason)
                    }
                }
            }

            override fun onError(call: Call?, e: Exception, id: Int) {
                e.printStackTrace()
                refreshLayout.finishRefreshing()
                Util.showTimeOutNotice(currentContext)
            }

        })

    }

    private fun requestCall(page: Int, bizType: String, userAccountType: String): RequestCall {
        return OkHttpUtils.post().url(URL_VENDOR).addHeader(ACCESS_TOKEN, accessToken)
                .params(mRegionIDs).addParams(PAGE, page.toString()).addParams(PAGE_SIZE, pageSize)
                .addParams(BIZ_TYPE, bizType).addParams(USER_ACCOUNT_TYPE, userAccountType)
                .build()
    }

    override fun regionSearch(regionIDs: HashMap<String, String>) {
        mRegionIDs = regionIDs
        refreshLayout.startRefresh()
    }

    override fun onEventMainThread(event: Event) {
        super.onEventMainThread(event)
        if (Event.eventType === EventType.ORDER_BALANCE) {
            // 重新刷新提醒数据
            onRefresh(position)
        }
    }

    override fun onListItemClick(position: Int) {
        when (type) {
            RECONCILIATION -> {
                when (this.position) {
                    0, 1 -> {
                        // 进入疫苗的对账列表
                        ReconciliationActivity.start(this, list[position].objectId, userAccountType)
                    }
                    2, 3 -> {
                        // 进入保险的客户列表
                        startActivity<ReconCusActivity>(
                                "orderId" to listIns[position - 1].id.toString(),
                                "companyId" to listIns[position - 1].companyId.toString()
                        )
                    }
                }
            }
            ORDER -> {
                when (this.position) {
                    0, 1 -> {
                        // 进入疫苗的业务订单列表
                        startActivity<BusinessVaccineActivity>("userAccountType" to userAccountType)
                    }
                    2, 3 -> {
                        // 进入保险的业务订单列表
                        startActivity<BusinessInsuranceActivity>("userAccountType" to userAccountType)
                    }
                }
            }
            else -> ToastUtil.showShort(this, "error type：$type")
        }
    }


    override fun onLoadMore(position: Int) {
        when (type) {
            RECONCILIATION -> {
                when (this.position) {
                    2 -> {
                        // 只有对账的保险个人/对公才有加载更多
                        loadMoreInsVendorData("insurance", "corporate")
                    }
                    3 -> {
                        loadMoreInsVendorData("insurance", "personal")
                    }
                    else -> refreshLayout.finishLoadmore()
                }
            }
            else -> refreshLayout.finishLoadmore()
        }
    }

    private fun loadMoreInsVendorData(bizType: String, userAccountType: String) {
        requestCall(page, bizType, userAccountType).execute(object : StringCallback() {
            override fun onError(call: Call?, e: Exception, id: Int) {
                e.printStackTrace()
            }

            override fun onResponse(response: String, id: Int) {
                refreshLayout.finishLoadmore()
                LogUtil.d("response is\n$response")
                JSON.parseObject(response, InsVendorJson::class.java)?.let {
                    when (it.status) {
                        "success" -> {
                            page++
                            listIns.addAll(it.pagedResult.pagedList)
                            adapterIns.notifyDataSetChanged()
                        }
                        "failure" -> Util.showError(currentContext, it.reason)
                    }
                }
            }
        })
    }
}
