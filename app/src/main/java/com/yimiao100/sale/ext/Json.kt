package com.yimiao100.sale.ext

import com.google.gson.Gson

/**
 * JSON解析封装
 * Created by Michel on 2017/4/14.
 */
class JSON {
    companion object{

        private val jsonInstance by lazy(LazyThreadSafetyMode.SYNCHRONIZED){
            Gson()
        }

        @JvmStatic
        fun <T> parseObject(json: String, clazz: Class<T>): T? {

            return jsonInstance.fromJson(json, clazz)
        }

        @JvmStatic
        fun toJSONString(obj: Any): String{
            return jsonInstance.toJson(obj)
        }
    }
}
