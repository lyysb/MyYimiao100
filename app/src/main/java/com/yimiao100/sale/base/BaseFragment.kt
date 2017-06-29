package com.yimiao100.sale.base

import android.os.Bundle
import android.support.v4.app.Fragment
import com.yimiao100.sale.bean.Event
import com.yimiao100.sale.utils.Constant
import com.yimiao100.sale.utils.SharePreferenceUtil
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by Michel on 2017/6/8.
 */
open class BaseFragment: Fragment(){

    @JvmField
    protected val ACCESS_TOKEN = "X-Authorization-Token"

    @JvmField
    protected var accessToken: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        accessToken = SharePreferenceUtil.get(context, Constant.ACCESSTOKEN, "") as String
        EventBus.getDefault().register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onEventMainThread(event: Event){

    }
}
