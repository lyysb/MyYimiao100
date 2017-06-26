package com.yimiao100.sale.utils

import android.text.InputFilter
import android.text.Spanned
import java.util.regex.Pattern

/**
 * Created by Michel on 2017/6/19.
 */
class FilterUtils: InputFilter {

    /**
     * 小数点后的数字的位数
     */
    val PONTINT_LENGTH = 2

    //除数字外的其他的
    var p: Pattern = Pattern.compile("[0-9]*")


    /**
     * source    新输入的字符串
     * start    新输入的字符串起始下标，一般为0
     * end    新输入的字符串终点下标，一般为source长度-1
     * dest    输入之前文本框内容
     * dstart    原内容起始坐标，一般为0
     * dend    原内容终点坐标，一般为dest长度-1
     */
    override fun filter(source: CharSequence, start: Int, end: Int, dest: Spanned, dstart: Int, dend: Int): CharSequence? {
        val oldtext = dest.toString()
        LogUtil.d("输入之前文本框内容是 $oldtext")
        LogUtil.d("新输入的字符串是 ${source.toString()}")
        // 验证删除等按键
        if ("".equals(source.toString())) {
            return null
        }
        // 如果先输入小数点
        if (oldtext.equals("") && source.toString().equals(".")) {
            LogUtil.d("首位输入小数点")
            return ""
        }
        // 如果先输入0，那新输入的内容只能是小数点
        if (oldtext.equals("0") && !source.toString().equals(".")) {
            LogUtil.d("0后面输入的不是小数点")
            return ""
        }
        // 验证非数字或者小数点的情况
        val m = p.matcher(source)
        if (oldtext.contains(".")) {
            // 已经存在小数点的情况下，只能输入数字
            if (!m.matches()) {
                return null
            }
        } else {
            // 未输入小数点的情况下，可以输入小数点和数字
            if (!m.matches() && source.toString().equals(".")) {
                return null
            }
        }
        // 验证小数位精度是否正确
        if (oldtext.contains(".")) {
            val index = oldtext.indexOf(".")
            val len = dend - index
            // 小数位只能2位
            if (len > PONTINT_LENGTH) {
                val newText = dest.subSequence(dstart, dend)
                return newText
            }
        }
        return "${dest.subSequence(dstart, dend)}$source"
    }
}