package com.yimiao100.sale.utils

import java.math.BigDecimal

/**
 * Created by michel on 2017/8/25.
 */
class CalculateUtils {

    companion object{

        fun mul(var1: Double, var2: Double): Double{
            val b1 = BigDecimal(var1.toString())
            val b2 = BigDecimal(var2.toString())
            return b1.multiply(b2).toDouble()
        }

        /**
         * 四舍五入
         */
        @JvmStatic
        fun round(var1: Double, scale: Int): Double {
            if (scale < 0) {
                throw IllegalArgumentException("精确位数必须是正整数或零")

            }

            val b = BigDecimal(var1.toString())
            val one = BigDecimal("1")
            return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).toDouble()
        }


    }
}