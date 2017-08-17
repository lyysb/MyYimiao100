package com.yimiao100.sale.base

import android.app.Activity
import android.app.ActivityManager
import android.app.ProgressDialog
import android.content.Context
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import cn.jpush.android.api.JPushInterface
import com.meiqia.meiqiasdk.controller.MQController
import com.uuch.adlibrary.AdConstant
import com.uuch.adlibrary.AdManager
import com.uuch.adlibrary.bean.AdInfo
import com.uuch.adlibrary.transformer.DepthPageTransformer
import com.yimiao100.sale.R
import com.yimiao100.sale.activity.ActivityActivity
import com.yimiao100.sale.bean.Ad
import com.yimiao100.sale.bean.AdBean
import com.yimiao100.sale.bean.AppCompatActivity
import com.yimiao100.sale.bean.Event
import com.yimiao100.sale.ext.JSON
import com.yimiao100.sale.receiver.MessageReceiver
import com.yimiao100.sale.utils.Constant
import com.yimiao100.sale.utils.LogUtil
import com.yimiao100.sale.utils.ProgressDialogUtil
import com.yimiao100.sale.utils.SharePreferenceUtil
import com.zhy.http.okhttp.OkHttpUtils
import com.zhy.http.okhttp.callback.StringCallback
import okhttp3.Call
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.startActivity
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.lang.Exception
import java.util.*

/**
 * 这个公司没有年终奖的,兄弟别指望了,也别来了,我准备辞职了
 * 另外这个项目有很多*Bug* 你坚持不了多久的,拜拜!
 */
open class BaseActivity : AppCompatActivity() {

    private lateinit var baseActivity: Activity

    @JvmField
    protected val ACCESS_TOKEN = "X-Authorization-Token"
    @JvmField
    protected val PAGE = "page"
    @JvmField
    protected val PAGE_SIZE = "pageSize"
    @JvmField
    protected val pageSize = "10"
    @JvmField
    protected val RC_SETTINGS_SCREEN = 125
    @JvmField
    protected val RC_CAMERA = 124

    @JvmField
    protected var accessToken: String = ""
    @JvmField
    protected var page: Int = 0
    @JvmField
    protected var totalPage: Int = 0
    protected lateinit var mLoadingProgress: ProgressDialog

    lateinit var messageReceiver: MessageReceiver

    var isActive: Boolean = true
    var dialogIsClosed: Boolean = true
    val URL_AD = Constant.BASE_URL + "/api/activity/current"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        baseActivity = this
        this.setActivityState(this)
        accessToken = SharePreferenceUtil.get(this, Constant.ACCESSTOKEN, "") as String
        // 注册EventBus
        EventBus.getDefault().register(this)
        // 注册美洽即时消息接收广播
        messageReceiver = MessageReceiver()
        val filter = IntentFilter()
        filter.addAction(MQController.ACTION_NEW_MESSAGE_RECEIVED)
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, filter)

    }

    override fun onResume() {
        super.onResume()
        ActivityCollector.setTopActivity(this)
        if (JPushInterface.isPushStopped(this)) {
            LogUtil.d("在${classLoader.javaClass.simpleName}极光推送服务已停止，重启极光推送")
            // 如果极光服务被停止，则恢复服务
            JPushInterface.resumePush(this)
        }
        if (!isActive && dialogIsClosed) {
            // 从后台唤醒
            isActive = true
            LogUtil.d("是否恢复到前台？ $isActive")
            // 获取活动数据，进行弹窗显示
            obtainAdDialog()
        }
    }

    /**
     * 获取广告弹窗
     */
    open fun obtainAdDialog() {
        OkHttpUtils.post().url(URL_AD).addHeader(ACCESS_TOKEN, accessToken)
                .build().execute(object : StringCallback() {
            override fun onResponse(response: String, id: Int) {
                LogUtil.d("获取广告数据：$response")
                val adBean = JSON.parseObject(response, AdBean::class.java)
                when (adBean?.status) {
                    "success" -> {
                        adBean.activity?.let {
                            showAdDialog(it)
                        }
                    }
                    "failure" -> LogUtil.d("服务器返回数据失败${adBean.reason}")
                }

            }

            override fun onError(call: Call?, e: Exception, id: Int) {
                e.printStackTrace()
                LogUtil.d("获取广告失败")
            }

        })
    }

    private fun showAdDialog(activity: Ad) {
        val advList = ArrayList<AdInfo>()
        val adInfo = AdInfo()
        adInfo.activityImg = activity.imageUrl
        advList.add(adInfo)
        val adManager = AdManager(this, advList)
        adManager.setOverScreen(true)
                .setPageTransformer(DepthPageTransformer())
                .setOnImageClickListener { view, advInfo ->
                    LogUtil.d("你点击了item")
                    if (activity.isPageJump == 1) {
                        // 如果需要进行页面跳转
                        LogUtil.d("进行页面跳转")
                        LogUtil.d("活动链接是${activity.pageJumpUrl}")
                        startActivity<ActivityActivity>("pageJumpUrl" to activity.pageJumpUrl)
                        adManager.dismissAdDialog()
                        // dismiss并不会触发closeListener，所以要执行和关闭时一样的操作
                        dialogIsClosed = true
                    }
                }
                .setOnCloseClickListener {
                    LogUtil.d("广告弹窗关闭了")
                    // 记录关闭弹窗
                    dialogIsClosed = true  //关闭了dialog后，从后台恢复到前台才进行网络请求
                }
                .showAdDialog(AdConstant.ANIM_DOWN_TO_UP)
        dialogIsClosed = false
    }

    override fun onStop() {
        super.onStop()
        if (!isAppOnForeground()) {
            // 记录当前进入后台
            isActive = false
            LogUtil.d("进入后台onStop，是否在前台？ $isActive")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // 将Activity从列表中移除
        ActivityCollector.removeActivity(this)
        // 反注册EventBus
        EventBus.getDefault().unregister(this)
        // 反注册美洽广播
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver)
    }


    fun isAppOnForeground(): Boolean {

        val activityManager = applicationContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val packageName = applicationContext.packageName

        val appProcesses = activityManager.runningAppProcesses

        appProcesses.forEach {
            if (it.processName.equals(packageName)
                    && it.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true
            }
        }
        return false
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onEventMainThread(event: Event) {
    }

    protected fun showLoadingProgress() {
        mLoadingProgress = ProgressDialogUtil.getLoadingProgress(this)
        mLoadingProgress.show()
    }

    protected fun hideLoadingProgress() {
        if (mLoadingProgress.isShowing) {
            mLoadingProgress.dismiss()
        }
    }

    protected lateinit var currentContext: Activity

    open fun setActivityState(activity: Activity) {
        currentContext = activity
        // 设置App只能竖屏显示
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        // 向自身维护的Activity栈（数据类型为List）中添加Activity
        ActivityCollector.addActivity(activity)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    open fun showSettingDialog() {
        AppSettingsDialog.Builder(this)
                .setTitle(getString(R.string.title_settings_dialog))
                .setRationale(getString(R.string.rationale_ask_again))
                .setPositiveButton(getString(R.string.setting))
                .setNegativeButton(getString(R.string.cancel))
                .setRequestCode(RC_SETTINGS_SCREEN)
                .build()
                .show()
    }
}
