package com.yimiao100.sale.utils

import android.util.Log

/**
 * Log的统一管理类
 * Created by Michel on 2016/7/26.
 */
class LogUtil private constructor() {
    init {
        /* 禁止实例化 */
        throw UnsupportedOperationException("cannot be instantiated")
    }

    companion object {
        // 是否需要打印bug，可以在application的onCreate函数里面初始化
        private val TAG = "亿苗通"
        @JvmStatic
        var isDebug = true

        @JvmStatic
        fun i(msg: String) {
            if (isDebug)
                Log.i(TAG, msg)
        }

        @JvmStatic
        fun d(msg: String) {
            if (isDebug)
                Log.d(TAG, msg)
        }

        @JvmStatic
        fun e(msg: String) {
            if (isDebug)
                Log.e(TAG, msg)
        }

        @JvmStatic
        fun v(msg: String) {
            if (isDebug)
                Log.v(TAG, msg)
        }


        @JvmStatic
        fun i(tag: String, msg: String) {
            if (isDebug)
                Log.i(tag, msg)
        }

        @JvmStatic
        fun d(tag: String, msg: String) {
            if (isDebug)
                Log.i(tag, msg)
        }

        @JvmStatic
        fun e(tag: String, msg: String) {
            if (isDebug)
                Log.i(tag, msg)
        }

        @JvmStatic
        fun v(tag: String, msg: String) {
            if (isDebug)
                Log.i(tag, msg)
        }
    }
}
