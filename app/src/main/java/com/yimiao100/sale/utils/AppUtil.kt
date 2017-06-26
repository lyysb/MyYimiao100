package com.yimiao100.sale.utils

import android.content.Context
import android.content.pm.PackageManager

/**
 * 跟App相关的辅助类
 * Created by Michel on 2016/7/26.
 */
class AppUtil private constructor() {
    init {
        /* cannot be instantiated */
        throw UnsupportedOperationException("cannot be instantiated") as Throwable
    }

    companion object {

        /**
         * 获取应用程序名称
         */
        @JvmStatic
        fun getAppName(context: Context): String? {
            try {
                val packageManager = context.packageManager
                val packageInfo = packageManager.getPackageInfo(context.packageName, 0)
                val labelRes = packageInfo.applicationInfo.labelRes
                return context.resources.getString(labelRes)
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }

            return null
        }

        /**
         * [获取应用程序版本名称信息]
         * @param context
         *
         * @return 当前应用的版本名称
         */
        @JvmStatic
        fun getVersionName(context: Context): String? {
            try {
                val packageManager = context.packageManager
                val packageInfo = packageManager.getPackageInfo(context.packageName, 0)
                return packageInfo.versionName

            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }

            return null
        }

        /**
         * 获取应用程序版本号
         * @param context
         * *
         * @return 当前应用的版本号
         */
        @JvmStatic
        fun getVersionCode(context: Context): Int {
            // 获取包管理器
            val packageManager = context.packageManager
            try {
                // 获取包信息
                val packageInfo = packageManager.getPackageInfo(context.packageName, 0)
                return packageInfo.versionCode
            } catch (e: PackageManager.NameNotFoundException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }

            return 0
        }
    }
}
