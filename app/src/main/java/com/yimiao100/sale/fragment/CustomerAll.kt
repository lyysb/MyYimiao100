package com.yimiao100.sale.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.RelativeLayout
import com.yimiao100.sale.R
import com.yimiao100.sale.activity.CustomerDetailActivity
import com.yimiao100.sale.adapter.listview.CustomerAdapter
import com.yimiao100.sale.base.BaseFragmentSingleList
import com.yimiao100.sale.bean.CDCBean
import com.yimiao100.sale.bean.CDCListBean
import com.yimiao100.sale.bean.ErrorBean
import com.yimiao100.sale.ext.JSON
import com.yimiao100.sale.utils.Constant
import com.yimiao100.sale.utils.DensityUtil
import com.yimiao100.sale.utils.LogUtil
import com.yimiao100.sale.utils.Util
import com.yimiao100.sale.view.RegionSearchView
import com.zhy.http.okhttp.OkHttpUtils
import com.zhy.http.okhttp.callback.StringCallback
import com.zhy.http.okhttp.request.RequestCall
import okhttp3.Call
import java.lang.Exception
import java.util.*

/**
 * 全部客户
 * Created by Michel on 2017/5/23.
 */
class CustomerAll : BaseFragmentSingleList(), RegionSearchView.onSearchClickListener {

    val URL_CDC_LIST = Constant.BASE_URL + "/api/cdc/cdc_list"
    val REQUEST_CDC_DETAIL = 110
    val RESULT_CDC_COLLECTION = 111

    lateinit var list: ArrayList<CDCListBean>
    lateinit var adapter: CustomerAdapter
    var regionParams: HashMap<String, String>? = null


    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isSetVisibleData = true
        setEmptyView(getString(R.string.empty_view_customer), R.mipmap.ico_my_client)

        initSearchView()
    }

    override fun initPageTitle() = "全部客户"

    private fun initSearchView() {
        val searchView = RegionSearchView(activity)
        val layoutParams = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(0, DensityUtil.dp2px(context, 46F), 0, 0)
        mEmptyView.layoutParams = layoutParams
        searchView.setOnSearchClickListener(this)
        mListView.addHeaderView(searchView, null, false)
    }

    override fun regionSearch(regionIDs: HashMap<String, String>) {
        regionParams = regionIDs
        onRefresh()
    }


    override fun onRefresh() {
        getBuild(1)!!.execute(object : StringCallback() {
            override fun onResponse(response: String, id: Int) {
                mSwipeRefreshLayout.isRefreshing = false
                val errorBean = JSON.parseObject(response, ErrorBean::class.java)
                when (errorBean?.status) {
                    "success" -> {
                        JSON.parseObject(response, CDCBean::class.java)?.pagedResult?.let {
                            list = it.pagedList
                            handleEmptyData(list)
                            mPage = 2
                            mTotalPage = it.totalPage
                            adapter = CustomerAdapter(activity, list)
                            mListView.adapter = adapter
                        }
                    }
                    "failure" -> Util.showError(activity, errorBean.reason)
                }
            }

            override fun onError(call: Call?, e: Exception, id: Int) {
                LogUtil.d("customer-all load error")
                e.printStackTrace()
            }

        })
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val intent = Intent(context, CustomerDetailActivity::class.java)
        val cdc = list[position - 1]
        intent.putExtra("cdc", cdc)
        intent.putExtra("position", position - 1)
        startActivityForResult(intent, REQUEST_CDC_DETAIL)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CDC_DETAIL) {
            //进入了客户详情页
            if (resultCode == RESULT_CDC_COLLECTION) {
                //在详情页用户做了“收藏”相关的操作
                val position = data?.getIntExtra("position", -1)
                if (position != -1) {
                    val cdc = data?.getParcelableExtra<CDCListBean>("cdc")
                    list[position!!] = cdc!!
                } else {
                    // 传回数据有误，直接刷新全部数据
                    onRefresh()
                }

            }
        }
    }

    override fun onLoadMore() {
        getBuild(mPage)!!.execute(object : StringCallback() {
            override fun onResponse(response: String, id: Int) {
                mListView.onLoadMoreComplete()
                val errorBean = JSON.parseObject(response, ErrorBean::class.java)
                when (errorBean?.status) {
                    "success" -> {
                        JSON.parseObject(response, CDCBean::class.java)?.pagedResult?.let {
                            list.addAll(it.pagedList)
                            mPage++
                            adapter.notifyDataSetChanged()
                        }
                    }
                    "failure" -> Util.showError(activity, errorBean.reason)
                }
            }

            override fun onError(call: Call?, e: Exception, id: Int) {
                LogUtil.d("customer-all load more error")
                e.printStackTrace()
            }

        })
    }

    private fun getBuild(page: Int): RequestCall? {
        return OkHttpUtils.post().url(URL_CDC_LIST).addHeader(ACCESS_TOKEN, mAccessToken)
                .params(regionParams)
                .addParams(PAGE, page.toString()).addParams(PAGE_SIZE, mPageSize)
                .build()
    }


}
