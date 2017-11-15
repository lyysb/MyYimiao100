package com.yimiao100.sale.insurance

import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.text.Html
import android.view.KeyEvent
import android.view.View
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.TextView
import com.meiqia.core.MQManager
import com.meiqia.core.bean.MQMessage
import com.meiqia.core.callback.OnGetMessageListCallback
import com.yimiao100.sale.R
import com.yimiao100.sale.base.BaseActivity
import com.yimiao100.sale.bean.*
import com.yimiao100.sale.callback.ProtocolFileCallBack
import com.yimiao100.sale.ext.JSON
import com.yimiao100.sale.utils.*
import com.yimiao100.sale.view.TitleView
import com.zhy.http.okhttp.OkHttpUtils
import okhttp3.Call
import okhttp3.Request
import okhttp3.Response
import org.jetbrains.anko.find
import q.rorbin.badgeview.Badge
import q.rorbin.badgeview.QBadgeView
import java.io.File

class BusinessAlreadyActivity : BaseActivity(), TitleView.TitleBarOnClickListener {


    lateinit var mOrder: Business
    lateinit var mService: ImageView
    lateinit var mOrderAlreadyRe: TextView
    lateinit var mBadge: Badge
    lateinit var mOrderId: String
    lateinit var mDialog: AlertDialog
    lateinit var mProgressDownloadDialog: ProgressDialog


    private val URL_DOWNLOAD_FILE = "${Constant.BASE_URL}/api/order/fetch_protocol"
    private val ORDER_ID = "orderId"
    private val ZIP_FILE = "zipFile"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_business_already)

        initVariate()

        initView()

        initData()
    }

    private fun initVariate() {
        mOrder = intent.getParcelableExtra("order")
    }

    private fun initView() {
        find<TitleView>(R.id.business_already_title).setOnTitleBarClick(this)
        mService = find(R.id.business_already_service)
        mService.setOnClickListener {
            //联系客服
            Util.enterCustomerService(this)
        }
        find<TextView>(R.id.business_already_show_account).setOnClickListener {
            //显示收款账户
            showAccountDialog()
        }
        find<ImageView>(R.id.business_already_download).setOnClickListener {
            //下载电子协议
            downloadAgreement()
        }
        mBadge = QBadgeView(this).bindTarget(mService)
                .setBadgePadding(4f, true)
                .setGravityOffset(9f, true)
                .setShowShadow(false)
        MQManager.getInstance(this).getUnreadMessages(object : OnGetMessageListCallback {
            override fun onSuccess(list: List<MQMessage>) {
                if (list.isNotEmpty()) {
                    mBadge.badgeNumber = -1
                }
            }

            override fun onFailure(i: Int, s: String) {

            }
        })
    }

    private fun initData() {
        val submit_time = TimeUtil.timeStamp2Date(mOrder.createdAt.toString(), "yyyy年MM月dd日")
        find<TextView>(R.id.business_already_submit_time).text = submit_time + getString(R.string.order_already_notice)
        find<TextView>(R.id.business_already_company_name).text = mOrder.companyName
        find<TextView>(R.id.business_already_product_name).text = "保险名称：${mOrder.productName}"
        find<TextView>(R.id.business_already_region).text =
                "区域：${mOrder.provinceName}\t${mOrder.cityName}\t${mOrder.areaName}"
        find<TextView>(R.id.business_already_time).text = "时间：${TimeUtil.timeStamp2Date(mOrder.createdAt.toString(), "yyyy.MM.dd")}"
        val totalDeposit = FormatUtils.MoneyFormat(mOrder.saleDeposit)
        val totalMoney = Html.fromHtml("推广保证金：<font color=\"#4188d2\">$totalDeposit</font>(人民币)")
        find<TextView>(R.id.business_already_total_money).text = totalMoney
        val customerView = find<TextView>(R.id.business_already_customer)
        customerView.text = "客户：${mOrder.customerName}"
        customerView.visibility = if (mOrder.customerName == null) View.GONE else View.VISIBLE
        find<TextView>(R.id.business_already_no).text = "协议单号：${mOrder.serialNo}"

        //总额保证金
        val orderTotalDeposit = FormatUtils.MoneyFormat(mOrder.saleDeposit)
        //竞标有效时间
        val defaultExpiredAt = TimeUtil.timeStamp2Date(mOrder.defaultExpiredAt.toString(), "yyyy年MM月dd日")
        mOrderAlreadyRe = find(R.id.business_already_re)
        when (mOrder.userAccountType) {
            "corporate" ->
                mOrderAlreadyRe.text = "请在$defaultExpiredAt 之前，通过贵公司的对公账号将产品推广保证金$orderTotalDeposit 转到如上账户，并且下载电子协议，认真阅读每一条协议内容。"
            "personal" ->
                mOrderAlreadyRe.text = "请在$defaultExpiredAt 之前，通过已绑定的个人银行卡将产品推广保证金$orderTotalDeposit 转到如上账户，并且下载电子协议，认真阅读每一条协议内容。"
        }
        //订单id
        mOrderId = mOrder.id.toString()
        //获取是否已经阅读过免责信息
        val isRead = SharePreferenceUtil.get(currentContext, mOrder.productName + mOrderId, false) as Boolean
        LogUtil.d("isRead?" + isRead)
        if (!isRead) {
            //没有阅读过，弹窗显示
            //弹窗
            showOrderDialog()
        }
    }

    /**
     * 显示账户信息
     */
    private fun showAccountDialog() {
        val builder = AlertDialog.Builder(this, R.style.dialog)
        val view = View.inflate(this, R.layout.dialog_account, null)
        builder.setView(view)
        val dialog = builder.create()
        dialog.show()
    }

    /**
     * 弹窗显示免责信息
     */
    private fun showOrderDialog() {
        val builder = AlertDialog.Builder(this)
        val view = View.inflate(this, R.layout.dialog_disclaimer, null)
        val order_read_already = view.findViewById(R.id.order_read_already) as CheckBox
        val order_agree = view.findViewById(R.id.order_agree) as ImageView
        builder.setView(view)
        //设置返回键无效
        //        builder.setCancelable(false);
        mDialog = builder.create()
        //监听复选框选中状态。
        order_read_already.setOnCheckedChangeListener { buttonView, isChecked ->
            //选中时，“同意”可点击，变成蓝色
            if (isChecked) {
                order_agree.isClickable = true
                order_agree.setImageResource(R.mipmap.ico_my_order_agree_activation)
                order_agree.setOnClickListener {
                    //设置记录
                    SharePreferenceUtil.put(currentContext, mOrder.productName + mOrderId, true)
                    mDialog.dismiss()
                }
            } else {
                //不可点击
                order_agree.isClickable = false
                order_agree.setImageResource(R.mipmap.ico_my_order_agree_default)
            }
        }
        //当Dialog弹出时，按下返回键，直接结束Activity
        mDialog.setOnKeyListener(DialogInterface.OnKeyListener { dialog, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (mDialog.isShowing) {
                    mDialog.dismiss()
                    this@BusinessAlreadyActivity.finish()
                    return@OnKeyListener false
                }
            }
            false
        })
        mDialog.show()
    }

    /**
     * 下载电子协议
     */
    private fun downloadAgreement() {
        mProgressDownloadDialog = ProgressDialog(this)
        mProgressDownloadDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        mProgressDownloadDialog.setTitle(getString(R.string.download_progress_dialog_title))
        mProgressDownloadDialog.setCancelable(false)
        val fileHead = "推广协议" + mOrder.productName
        OkHttpUtils.post().url(URL_DOWNLOAD_FILE).addHeader(ACCESS_TOKEN, accessToken)
                .addParams(ORDER_ID, mOrderId).build()      //使用私人定制版FileCallBack
                .connTimeOut((1000 * 60 * 10).toLong())
                .readTimeOut((1000 * 60 * 10).toLong())
                .execute(object : ProtocolFileCallBack(fileHead) {

            override fun onBefore(request: Request?, id: Int) {
                super.onBefore(request, id)
                mProgressDownloadDialog.show()
            }

            override fun inProgress(progress: Float, total: Long, id: Int) {
                super.inProgress(progress, total, id)
                LogUtil.d("progress：" + progress)
                LogUtil.d("total：" + total)
            }

            override fun onAfter(id: Int) {
                super.onAfter(id)
                mProgressDownloadDialog.dismiss()
            }

            override fun onError(call: Call, e: Exception, id: Int) {
                Util.showTimeOutNotice(currentContext)
            }

            @Throws(Exception::class)
            override fun parseNetworkResponse(response: Response, id: Int): File? {
                if (200 == response.code()) {
                    //协议下载成功
                    LogUtil.d("下载成功")
                    return super.parseNetworkResponse(response, id)
                } else if (400 == response.code()) {
                    //解析显示错误信息
                    LogUtil.d("下载失败")
                    val errorBean = JSON.parseObject(response.body().toString(),
                            ErrorBean::class.java)
                    Util.showError(currentContext, errorBean!!.reason)
                    return null
                }
                return super.parseNetworkResponse(response, id)
            }

            override fun onResponse(response: File?, id: Int) {
                if (response == null) {
                    //如果返回来null证明下载错误
                    return
                }
                LogUtil.d("onResponse：" + response.absolutePath)
                LogUtil.d("response.name：" + response.name)
                //下载成功，显示分享或者发送出去对话框
                showSuccessDialog(response)
            }
        })
    }

    /**
     * 文件下载成功，选择文件处理方式

     * @param response
     */
    private fun showSuccessDialog(response: File) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.download_complete_dialog_title))
        builder.setMessage(getString(R.string.download_complete_dialog_msg))
        builder.setCancelable(false)
        builder.setPositiveButton(getString(R.string.download_complete_dialog_pb)) { dialog, which ->
            //打开邮箱
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra("subject", response.name) //
            intent.putExtra("body", "Email from CodePad") //正文
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(response))
            //添加附件，附件为file对象
            if (response.name.endsWith(".gz")) {
                intent.type = "application/x-gzip" //如果是gz使用gzip的mime
            } else if (response.name.endsWith(".txt")) {
                intent.type = "text/plain" //纯文本则用text/plain的mime
            } else {
                intent.type = "application/octet-stream" //其他的均使用流当做二进制数据来发送
            }
            startActivity(intent) //调用系统的mail客户端进行发送
            dialog.dismiss()
        }
        builder.setNegativeButton(getString(R.string.download_complete_dialog_nb)) { dialog, which ->
            val intent = Intent("android.intent.action.VIEW")
            intent.addCategory("android.intent.category.DEFAULT")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            val uri = Uri.fromFile(response)
            intent.setDataAndType(uri, "application/msword")
            startActivity(intent)
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }


    override fun onEventMainThread(event: Event) {
        super.onEventMainThread(event)
        when (Event.eventType) {
            EventType.RECEIVE_MSG ->
                // 收到客服消息，显示小圆点
                mBadge.badgeNumber = -1
            EventType.READ_MSG ->
                // 设置小圆点为0
                mBadge.badgeNumber = 0
            else -> LogUtil.d("unknown event type is " + Event.eventType)
        }
    }


    override fun leftOnClick() {
        finish()
    }

    override fun rightOnClick() {
    }
}
