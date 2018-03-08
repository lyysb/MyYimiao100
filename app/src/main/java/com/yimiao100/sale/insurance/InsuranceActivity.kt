package com.yimiao100.sale.insurance

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.view.ViewPager
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.RelativeLayout
import cn.bingoogolapple.bgabanner.BGABanner
import com.blankj.utilcode.util.LogUtils
import com.squareup.picasso.Picasso
import com.yimiao100.sale.R
import com.yimiao100.sale.activity.JumpActivity
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
import org.jetbrains.anko.find
import org.jetbrains.anko.startActivity
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

/**
 * 保险列表
 */
class InsuranceActivity : BaseActivitySingleList(), RegionSearchView.onSearchClickListener, CarouselUtil.HandleCarouselListener {

    private var mRegionIDs = HashMap<String, String>()
    val URL_INSURANCE_LIST = "${Constant.BASE_URL}/api/insure/resource_list"
    lateinit var insuranceList: ArrayList<InsuranceDetail>
    lateinit var adapter: InsuranceAdapter
    lateinit var adBanner: BGABanner

    override fun onCreate(savedInstanceState: Bundle?) {
        showLoadingProgress()
        super.onCreate(savedInstanceState)
        // 空视图设置
        emptyViewSetting()
        // 设置头部布局
        initHeadView()
    }

    override fun onResume() {
        super.onResume()
        adBanner.startAutoPlay()
    }

    override fun onPause() {
        super.onPause()
        adBanner.stopAutoPlay()
    }

    override fun initView() {
        super.initView()
        // 更改ListView分割线宽度
        mListView.dividerHeight = DensityUtil.dp2px(this, 3f)
    }

    private fun initHeadView() {
        val view = View.inflate(this, R.layout.head_resources, null)
        adBanner = view.find(R.id.resource_banner)
        mListView.addHeaderView(view)
        val regionSearchView = view.find<RegionSearchView>(R.id.resource_search)
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
        adBanner.setAdapter { banner, itemView, model, position ->
            Picasso.with(currentContext)
                    .load((model as Carousel).mediaUrl)
                    .placeholder(R.mipmap.ico_default_bannner)
                    .resize(ScreenUtil.getScreenWidth(currentContext), DensityUtil.dp2px(currentContext, 99f))
                    .into(itemView as ImageView)
        }
        val desc = java.util.ArrayList<String>()
//        for (carousel in carouselList) {
//            desc.add(carousel.objectTitle)
//        }
        carouselList.mapTo(desc) { it.objectTitle }
        adBanner.setData(carouselList, desc)
        adBanner.setDelegate { banner, itemView, model, position ->
            LogUtils.d((model as Carousel).pageJumpUrl)
            if (model.pageJumpUrl == null) {
                return@setDelegate
            }
            if (TextUtils.equals(model.pageJumpUrl, "")) {
                return@setDelegate
            }
            JumpActivity.start(this, model.pageJumpUrl)
            LogUtils.d(model.pageJumpUrl)
        }
    }

    override fun regionSearch(regionIDs: HashMap<String, String>) {
        mRegionIDs = regionIDs
        showLoadingProgress()
        onRefresh()
    }

}
