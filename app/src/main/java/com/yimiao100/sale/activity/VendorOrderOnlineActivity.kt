package com.yimiao100.sale.activity

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import com.yimiao100.sale.R
import com.yimiao100.sale.adapter.listview.VendorListAdapter
import com.yimiao100.sale.base.BaseActivitySingleList
import com.yimiao100.sale.bean.ErrorBean
import com.yimiao100.sale.bean.Vendor
import com.yimiao100.sale.bean.VendorListBean
import com.yimiao100.sale.ext.JSON
import com.yimiao100.sale.utils.Constant
import com.yimiao100.sale.utils.LogUtil
import com.yimiao100.sale.utils.Util
import com.yimiao100.sale.view.TitleView
import com.zhy.http.okhttp.OkHttpUtils
import com.zhy.http.okhttp.callback.StringCallback
import okhttp3.Call
import org.jetbrains.anko.*
import java.lang.Exception

/**
 * Created by Michel on 2017/6/7.
 */
class VendorOrderOnlineActivity: BaseActivitySingleList() {

    val URL_VENDOR_LIST = Constant.BASE_URL + "/api/vendor/vendor_list_by_user"
    val MODULE_TYPE = "moduleType"
    lateinit var moduleType: String
    val USER_ACCOUNT_TYPE = "userAccountType"
    var userAccountType = "corporate"
    lateinit var list: ArrayList<Vendor>

    override fun onCreate(savedInstanceState: Bundle?) {
        moduleType = intent.getStringExtra("moduleType")
        showLoadingProgress()
        super.onCreate(savedInstanceState)
        mListView.cancleLoadMore()
    }

    override fun setTitle(titleView: TitleView?) {
        titleView?.setTitle("在线下单")
    }

    override fun initView() {
        super.initView()
        val headerView = TextView(this)
        headerView.text = "您有以下产品可以在线下单"
        headerView.textSize = 12f
        headerView.textColor = resources.getColor(R.color.colorMain)
        headerView.backgroundColor = Color.parseColor("#fafafa")
        headerView.padding = dip(12)
        headerView.compoundDrawablePadding = dip(8)
        headerView.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ico_order_online_order, 0, 0, 0)
        mListView.addHeaderView(headerView, null, false)
    }
    override fun onRefresh() {
        OkHttpUtils.post().url(URL_VENDOR_LIST).addHeader(ACCESS_TOKEN, accessToken)
                .addParams(MODULE_TYPE, moduleType).addParams(USER_ACCOUNT_TYPE, userAccountType)
                .build().execute(object : StringCallback(){
            override fun onResponse(response: String, id: Int) {
                hideLoadingProgress()
                mSwipeRefreshLayout.isRefreshing = false
                LogUtil.d("vendor list is $response")
                val errorBean = JSON.parseObject(response, ErrorBean::class.java)
                when (errorBean?.status) {
                    "success" -> {
                        JSON.parseObject(response, VendorListBean::class.java)?.let{
                            list = it.vendorList
                            handleEmptyData(list)
                            mListView.adapter = VendorListAdapter(list)
                        }
                    }
                    "failure"-> Util.showError(currentContext, errorBean.reason)
                }
            }

            override fun onError(call: Call?, e: Exception?, id: Int) {
                hideLoadingProgress()
                LogUtil.d("get vendor list is error")
                mSwipeRefreshLayout.isRefreshing = false
                e?.printStackTrace()
                Util.showTimeOutNotice(currentContext)
            }

        })
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val vendor = list[position - 1]
        startActivity<OrderOnlineActivity>("vendor" to vendor)
    }

    override fun onLoadMore() {

    }
}