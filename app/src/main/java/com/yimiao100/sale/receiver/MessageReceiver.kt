package com.yimiao100.sale.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.meiqia.core.MQMessageManager
import com.yimiao100.sale.bean.Event
import com.yimiao100.sale.bean.EventType
import com.yimiao100.sale.utils.LogUtil
import org.greenrobot.eventbus.EventBus

/**
 * 美洽即时消息推送
 */
class MessageReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        LogUtil.d("收到美洽广播")
        val action = intent.action
        // 接收新消息
        if (MQMessageManager.ACTION_NEW_MESSAGE_RECEIVED.equals(action)) {
            LogUtil.d("收到新消息")
            // 发布事件
            val event = Event()
            Event.eventType = EventType.RECEIVE_MSG
            EventBus.getDefault().post(event)
        }
    }
}
