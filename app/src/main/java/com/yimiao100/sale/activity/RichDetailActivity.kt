package com.yimiao100.sale.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.yimiao100.sale.R
import com.yimiao100.sale.adapter.listview.RichesDetailAdapter
import com.yimiao100.sale.base.BaseActivityWithSpinner
import com.yimiao100.sale.bean.RichesDetailBean
import com.yimiao100.sale.bean.RichesDetailList
import com.yimiao100.sale.ext.JSON
import com.yimiao100.sale.utils.Constant
import com.yimiao100.sale.utils.LogUtil
import com.yimiao100.sale.utils.ToastUtil
import com.yimiao100.sale.utils.Util
import com.zhy.http.okhttp.OkHttpUtils
import com.zhy.http.okhttp.callback.StringCallback
import com.zhy.http.okhttp.request.RequestCall
import okhttp3.Call
import org.jetbrains.anko.startActivity
import java.lang.Exception

class RichDetailActivity : BaseActivityWithSpinner() {

    val URL_ACCOUNT_DETAIL_LIST = "${Constant.BASE_URL}/api/fund/account_detail_list"
    val ACCOUNT_TYPE = "accountType"
    val BIZ_TYPE = "bizType"

    lateinit var list: ArrayList<RichesDetailList>
    lateinit var adapter: RichesDetailAdapter

    override fun getTitleText(): String = "财富明细"

    override fun getSpinnerItems(): ArrayList<String> {
        val list = ArrayList<String>()

        list.add("疫苗对公")
        list.add("疫苗个人")
        if (Constant.isInsurance) {
            // 开启保险
            list.add("保险对公")
            list.add("保险个人")
        }

        return list
    }

    override fun onRefresh(position: Int) {
        when (position) {
            0 -> {
                requestData("vaccine", "corporate")
            }
            1 -> {
                requestData("vaccine", "personal")
            }
            2 -> {
                requestData("insurance", "corporate")
            }
            3 -> {
                requestData("insurance", "personal")
            }
            else -> ToastUtil.showShort(this, "unknown error type")
        }

    }

    private fun requestData(bizType: String, accountType: String) {
        requestCall(1, bizType, accountType).execute(object : StringCallback(){
            override fun onResponse(response: String, id: Int) {
                LogUtil.d("response is\n$response")
                refreshLayout.finishRefreshing()
                JSON.parseObject(response, RichesDetailBean::class.java)?.let {
                    when (it.status) {
                        "success" -> {
                            page = 2
                            totalPage = it.pagedResult.totalPage
                            list = it.pagedResult.pagedList
                            handleEmptyView(list)
                            adapter = RichesDetailAdapter(list)
                            listView.adapter = adapter
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

    private fun requestCall(page: Int, bizType: String, accountType: String): RequestCall {
        return OkHttpUtils.post().url(URL_ACCOUNT_DETAIL_LIST).addHeader(ACCESS_TOKEN, accessToken)
                .addParams(BIZ_TYPE, bizType)
                .addParams(PAGE, page.toString()).addParams(PAGE_SIZE, pageSize)
                .addParams(ACCOUNT_TYPE, accountType).build()
    }

    override fun onListItemClick(position: Int) {
        startActivity<RichItemDetailActivity>(
                "id" to list[position].id
        )
    }

    override fun onLoadMore(position: Int) {
        when (position) {
            0 -> {
                requestMoreData("vaccine", "corporate")
            }
            1 -> {
                requestMoreData("vaccine", "personal")
            }
            2 -> {
                requestMoreData("insurance", "corporate")
            }
            3 -> {
                requestMoreData("insurance", "personal")
            }
            else -> ToastUtil.showShort(this, "unknown error type")
        }
    }

    private fun requestMoreData(bizType: String, accountType: String) {
        requestCall(page, bizType, accountType).execute(object : StringCallback(){
            override fun onResponse(response: String, id: Int) {
                LogUtil.d("response is\n$response")
                refreshLayout.finishLoadmore()
                JSON.parseObject(response, RichesDetailBean::class.java)?.let {
                    when (it.status) {
                        "success" -> {
                            page++
                            list.addAll(it.pagedResult.pagedList)
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


}
