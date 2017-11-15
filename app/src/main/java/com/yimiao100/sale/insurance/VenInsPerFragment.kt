package com.yimiao100.sale.insurance

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import com.yimiao100.sale.R
import com.yimiao100.sale.activity.*
import com.yimiao100.sale.adapter.listview.VendorArrayAdapter
import com.yimiao100.sale.base.BaseFragmentSingleList
import com.yimiao100.sale.bean.*
import com.yimiao100.sale.ext.JSON
import com.yimiao100.sale.utils.Constant
import com.yimiao100.sale.utils.LogUtil
import com.yimiao100.sale.utils.Util
import com.zhy.http.okhttp.OkHttpUtils
import com.zhy.http.okhttp.callback.StringCallback
import okhttp3.Call

/**
 * Created by michel on 2017/8/31.
 */
class VenInsPerFragment: BaseFragmentSingleList() {

    lateinit var moduleType: String
    lateinit var URL_VENDOR_LIST: String
    lateinit var list: ArrayList<BizList>

    private val MODULE_TYPE = "moduleType"
    private val userAccountType = "personal"
    private val SALE_WITHDRAWAL = "sale_withdrawal"               //来自推广费
    private val DEPOSIT_WITHDRAWAL = "deposit_withdrawal"         //来自保证金
    private val USER_ACCOUNT_TYPE = "userAccountType"
    private val BIZ_TYPE = "bizType"
    private val bizType = "insurance"

    override fun initPageTitle(): String = "个人推广"

    override fun initVariate() {
        super.initVariate()
        moduleType = activity.intent.getStringExtra("moduleType")
        when (moduleType) {
            SALE_WITHDRAWAL -> URL_VENDOR_LIST = "${Constant.BASE_URL}/api/biz/sale_withdrawal"
            DEPOSIT_WITHDRAWAL -> URL_VENDOR_LIST = "${Constant.BASE_URL}/api/biz/deposit_withdrawal"
        }
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when (moduleType) {
            SALE_WITHDRAWAL -> {
                //来自推广费
                setEmptyView(getString(R.string.empty_view_vendor_sale), R.mipmap.ico_extension_reward)
            }
            DEPOSIT_WITHDRAWAL -> {
                //来自保证金
                setEmptyView(getString(R.string.empty_view_vendor_deposit), R.mipmap.ico_bond_factory_list)
            }
        }
        mListView.cancleLoadMore()
    }


    override fun onRefresh() {
        LogUtil.d("URL_VENDOR_LIST is $URL_VENDOR_LIST\n$BIZ_TYPE is $bizType\n$MODULE_TYPE is $moduleType\n$USER_ACCOUNT_TYPE is $userAccountType\n")
        OkHttpUtils.post().url(URL_VENDOR_LIST).addHeader(ACCESS_TOKEN, mAccessToken)
                .addParams(BIZ_TYPE, bizType)
                .addParams(MODULE_TYPE, moduleType)
                .addParams(USER_ACCOUNT_TYPE, userAccountType)
                .build().execute(object : StringCallback() {
            override fun onError(call: Call, e: Exception, id: Int) {
                mSwipeRefreshLayout.isRefreshing = false
                LogUtil.d("vendor list error is :")
                e.printStackTrace()
                Util.showTimeOutNotice(activity)
            }

            override fun onResponse(response: String, id: Int) {
                mSwipeRefreshLayout.isRefreshing = false
                LogUtil.d("response ：\n" + response)
                 JSON.parseObject(response, BizListJson::class.java)?.let {
                     when (it.status) {
                         "success" -> {
                             list = it.bizList
                             handleEmptyData(list)
                             val adapter = VendorArrayAdapter(it.bizList)
                             mListView.adapter = adapter
                         }
                         "failure" -> Util.showError(activity, it.reason)
                     }
                 }
            }
        })
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val vendor = list[position]
        val intent = Intent()
        var clz: Class<*>? = null
        when (moduleType) {
            SALE_WITHDRAWAL -> {
                // 推广奖励
                clz = PromoInsActivity::class.java
                intent.putExtra("companyId", vendor.objectId)
                intent.putExtra("logoImageUrl", vendor.imageUrl)
                intent.putExtra("companyName", vendor.objectName)
            }
            DEPOSIT_WITHDRAWAL -> {
                // 推广保证金
                clz = AssInsActivity::class.java
                intent.putExtra("companyId", vendor.objectId)
                intent.putExtra("logoImageUrl", vendor.imageUrl)
                intent.putExtra("companyName", vendor.objectName)
            }
        }
        intent.setClass(context, clz)
        intent.putExtra(USER_ACCOUNT_TYPE, userAccountType)
        startActivity(intent)
    }

    override fun onLoadMore() {}
}