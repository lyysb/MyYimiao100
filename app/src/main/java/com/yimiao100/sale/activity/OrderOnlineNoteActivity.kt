package com.yimiao100.sale.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout
import com.lcodecore.tkrefreshlayout.footer.LoadingView
import com.lcodecore.tkrefreshlayout.header.progresslayout.ProgressLayout
import com.squareup.picasso.Picasso
import com.yimiao100.sale.R
import com.yimiao100.sale.adapter.listview.OrderNoteAdapter
import com.yimiao100.sale.base.BaseActivity
import com.yimiao100.sale.bean.OrderNote
import com.yimiao100.sale.bean.OrderNoteBean
import com.yimiao100.sale.bean.Vendor
import com.yimiao100.sale.ext.JSON
import com.yimiao100.sale.utils.Constant
import com.yimiao100.sale.utils.DensityUtil
import com.yimiao100.sale.utils.LogUtil
import com.yimiao100.sale.utils.Util
import com.yimiao100.sale.view.TitleView
import com.zhy.http.okhttp.OkHttpUtils
import com.zhy.http.okhttp.callback.StringCallback
import com.zhy.http.okhttp.request.RequestCall
import de.hdodenhof.circleimageview.CircleImageView
import okhttp3.Call
import org.jetbrains.anko.find
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import java.lang.Exception

/**
 * 下单记录列表
 */
class OrderOnlineNoteActivity : BaseActivity(), TitleView.TitleBarOnClickListener {


    lateinit var vendor: Vendor
    lateinit var refreshLayout: TwinklingRefreshLayout
    lateinit var listView: ListView
    lateinit var list: ArrayList<OrderNote>
    lateinit var adapter: OrderNoteAdapter

    val URL_ORDER_ONLINE_LIST = Constant.BASE_URL + "/api/order_online/order_online_list"
    val VENDOR_ID = "vendorId"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_online_note)

        initVariate()

        initView()

        initData()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        initVariate()

        initView()

        initData()
    }

    private fun initVariate() {
        vendor = intent.getParcelableExtra("vendor")
    }

    private fun initView() {
        find<TitleView>(R.id.order_online_note_title).setOnTitleBarClick(this)

        find<ImageView>(R.id.order_online_note_online).setOnClickListener {
            startActivity<OrderOnlineActivity>("vendor" to vendor)
        }

        refreshLayout = find(R.id.order_online_note_refresh)
        val header = ProgressLayout(this)
        header.setColorSchemeResources(
                android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light)
        refreshLayout.setHeaderView(header)
        val loadingView = LoadingView(this)
        refreshLayout.setBottomView(loadingView)
        refreshLayout.setFloatRefresh(true)
        refreshLayout.setOverScrollRefreshShow(false)
        refreshLayout.setOnRefreshListener(object : RefreshListenerAdapter() {
            override fun onRefresh(refreshLayout: TwinklingRefreshLayout?) {
                super.onRefresh(refreshLayout)
                onRefresh()
            }

            override fun onLoadMore(refreshLayout: TwinklingRefreshLayout?) {
                super.onLoadMore(refreshLayout)
                onLoadMore()
            }
        })
        refreshLayout.startRefresh()

        listView = find(R.id.order_online_note_list)
        val listHeader = LayoutInflater.from(this).inflate(R.layout.header_order_note, null)
        val vendorLogo = listHeader.find<CircleImageView>(R.id.header_vendor_logo)
        Picasso.with(this).load(vendor.logoImageUrl)
                .resize(DensityUtil.dp2px(this, 32F), DensityUtil.dp2px(this, 32F))
                .into(vendorLogo)
        listHeader.find<TextView>(R.id.header_vendor_name).text = vendor.vendorName
        if (listView.headerViewsCount == 0) {
            listView.addHeaderView(listHeader, null, false)
        }
        listView.setOnItemClickListener { parent, view, position, id ->
            val orderNote = list[position - 1]
            startActivity<OrderOnlineActivity>("vendor" to vendor, "orderId" to orderNote.id)
        }
    }

    private fun initData() {

    }

    private fun onRefresh() {
        requestCall(1).execute(object : StringCallback() {
            override fun onResponse(response: String, id: Int) {
                refreshLayout.finishRefreshing()
                LogUtil.d("refresh success, result is $response")
                val orderNoteBean = JSON.parseObject(response, OrderNoteBean::class.java)
                when (orderNoteBean?.status) {
                    "success" -> {
                        page = 2
                        totalPage = orderNoteBean.pagedResult.totalPage
                        list = orderNoteBean.pagedResult.pagedList
                        adapter = OrderNoteAdapter(list)
                        listView.adapter = adapter
                    }
                    "failure" -> Util.showError(currentContext, orderNoteBean.reason)
                }
            }

            override fun onError(call: Call?, e: Exception?, id: Int) {
                refreshLayout.finishRefreshing()
                LogUtil.d("refresh error")
                e?.printStackTrace()
                Util.showTimeOutNotice(currentContext)
            }

        })
    }

    private fun onLoadMore() {
        if (page < totalPage) {
            requestCall(page).execute(object : StringCallback() {
                override fun onResponse(response: String, id: Int) {
                    refreshLayout.finishLoadmore()
                    LogUtil.d("loadMore success, result is $response")
                    val orderNoteBean = JSON.parseObject(response, OrderNoteBean::class.java)
                    when (orderNoteBean?.status) {
                        "success" -> {
                            page ++
                            list.addAll(orderNoteBean.pagedResult.pagedList)
                            adapter.notifyDataSetChanged()
                        }
                        "failure" -> Util.showError(currentContext, orderNoteBean.reason)
                    }
                }

                override fun onError(call: Call?, e: Exception?, id: Int) {
                    refreshLayout.finishLoadmore()
                    LogUtil.d("loadMore error")
                    e?.printStackTrace()
                    Util.showTimeOutNotice(currentContext)
                }

            })
        } else {
            Handler().postDelayed({
                refreshLayout.finishLoadmore()
                toast("全部加载完成")
            }, 1000)

        }
    }

    private fun requestCall(page: Int): RequestCall {
        return OkHttpUtils.post().url(URL_ORDER_ONLINE_LIST).addHeader(ACCESS_TOKEN, accessToken)
                .addParams(PAGE, page.toString()).addParams(PAGE_SIZE, pageSize)
                .addParams(VENDOR_ID, vendor.id.toString())
                .build()
    }


    override fun leftOnClick() {
        finish()
    }

    override fun rightOnClick() {
    }
}
