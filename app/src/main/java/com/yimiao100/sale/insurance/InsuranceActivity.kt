package com.yimiao100.sale.insurance

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.view.ViewPager
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.RelativeLayout
import com.yimiao100.sale.R
import com.yimiao100.sale.adapter.listview.InsuranceAdapter
import com.yimiao100.sale.adapter.peger.ResourceAdAdapter
import com.yimiao100.sale.base.BaseActivitySingleList
import com.yimiao100.sale.bean.*
import com.yimiao100.sale.ext.JSON
import com.yimiao100.sale.utils.*
import com.yimiao100.sale.view.RegionSearchView
import com.yimiao100.sale.view.TitleView
import com.zhy.http.okhttp.OkHttpUtils
import com.zhy.http.okhttp.callback.StringCallback
import com.zhy.http.okhttp.request.RequestCall
import okhttp3.Call
import org.jetbrains.anko.startActivity
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

/**
 * 保险列表
 */
class InsuranceActivity : BaseActivitySingleList(), RegionSearchView.onSearchClickListener, CarouselUtil.HandleCarouselListener {

    private var mRegionIDs = HashMap<String, String>()
    val SHOW_NEXT_PAGE = 0
    val URL_INSURANCE_LIST = "${Constant.BASE_URL}/api/insure/resource_list"
    lateinit var mViewPager: ViewPager
    lateinit var insuranceList: ArrayList<InsuranceDetail>
    lateinit var adapter: InsuranceAdapter

    private val mHandler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                SHOW_NEXT_PAGE -> showNextPage()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        showLoadingProgress()
        super.onCreate(savedInstanceState)
        // 空视图设置
        emptyViewSetting()
        // 设置头部布局
        initHeadView()
    }

    override fun onStart() {
        super.onStart()
        mHandler.sendEmptyMessageDelayed(SHOW_NEXT_PAGE, 3000)
    }

    override fun onStop() {
        super.onStop()
        mHandler.removeMessages(SHOW_NEXT_PAGE)
    }

    override fun initView() {
        super.initView()
        // 更改ListView分割线宽度
        mListView.dividerHeight = DensityUtil.dp2px(this, 3f)
    }

    private fun initHeadView() {
        val view = View.inflate(this, R.layout.head_resources, null)
        mListView.addHeaderView(view)
        val regionSearchView = view.findViewById(R.id.resource_search) as RegionSearchView
        mViewPager = view.findViewById(R.id.resources_view_pager) as ViewPager
        regionSearchView.setOnSearchClickListener(this)
        //获取轮播图数据
        CarouselUtil.getCarouselList(this, "insurance", this)
    }

    private fun emptyViewSetting() {
        setEmptyView(getString(R.string.empty_view_resources), R.mipmap.ico_resources)
        val layoutParams = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        // 145是HeaderView的高度
        layoutParams.setMargins(0, DensityUtil.dp2px(this, 145f), 0, 0)
        mEmptyView.layoutParams = layoutParams
    }

    override fun setTitle(titleView: TitleView) {
        titleView.setTitle("保险")
    }

    fun showNextPage() {
        mViewPager.currentItem = mViewPager.currentItem + 1
        mHandler.sendEmptyMessageDelayed(SHOW_NEXT_PAGE, 3000)
    }

    override fun onRefresh() {
        getBuild(1).execute(object : StringCallback(){
            override fun onResponse(response: String, id: Int) {
                mSwipeRefreshLayout.isRefreshing = false
                LogUtil.d("response is \n$response")
                hideLoadingProgress()
                val errorBean = JSON.parseObject(response, ErrorBean::class.java)
                when (errorBean?.status) {
                    "success" -> {
                        page = 2
                        // 解析json
                        JSON.parseObject(response, InsuranceJson::class.java)?.pagedResult?.let {
                            totalPage = it.totalPage
                            insuranceList = it.pagedList
                            handleEmptyData(insuranceList)
                            adapter = InsuranceAdapter(insuranceList)
                            mListView.adapter = adapter
                        }
                    }
                    "failure" -> Util.showError(currentContext, errorBean.reason)
                }

            }

            override fun onError(call: Call?, e: Exception, id: Int) {
                mSwipeRefreshLayout.isRefreshing = false
                e.printStackTrace()
                LogUtil.d("insurance error")
                hideLoadingProgress()
            }

        })
    }

    private fun getBuild(page: Int): RequestCall {
        return OkHttpUtils.post().url(URL_INSURANCE_LIST).addHeader(ACCESS_TOKEN, accessToken)
                .params(mRegionIDs).addParams(PAGE, page.toString())
                .addParams(PAGE_SIZE, pageSize)
                .build()
    }

    override fun onLoadMore() {
        getBuild(page).execute(object : StringCallback(){
            override fun onResponse(response: String, id: Int) {
                LogUtil.d("response is \n $response")
                hideLoadingProgress()
                val errorBean = JSON.parseObject(response, ErrorBean::class.java)
                when (errorBean?.status) {
                    "success" -> {
                        page ++
                        // 解析json
                        JSON.parseObject(response, InsuranceJson::class.java)?.pagedResult?.let {
                            insuranceList.addAll(it.pagedList)
                            adapter.notifyDataSetChanged()
                        }
                    }
                    "failure" -> Util.showError(currentContext, errorBean.reason)
                }
                mListView.onLoadMoreComplete()
            }

            override fun onError(call: Call?, e: Exception, id: Int) {
                e.printStackTrace()
                LogUtil.d("insurance error")
                hideLoadingProgress()
            }

        })
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val insuranceId = insuranceList[position -1].id
        startActivity<InsuranceDetailActivity>("insuranceId" to insuranceId)
    }

    override fun handleCarouselList(carouselList: ArrayList<Carousel>) {
        mViewPager.adapter = ResourceAdAdapter(carouselList)
        mViewPager.currentItem = mViewPager.adapter.count / 2
    }

    override fun regionSearch(regionIDs: HashMap<String, String>) {
        mRegionIDs = regionIDs
        showLoadingProgress()
        onRefresh()
    }

}
