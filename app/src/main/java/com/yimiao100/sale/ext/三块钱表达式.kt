package com.yimiao100.sale.ext

/**
 * Kotlin 中的三目运算符
 * Created by Michel on 2017/6/9.
 */
class 三块钱表达式<T>(val value: Boolean, val valueForTrue: T?)
infix fun <T> 三块钱表达式<T>.冒号(valueForFalse: T?) = if (value) valueForTrue else valueForFalse
infix fun <T> Boolean.问号(value: T?) = 三块钱表达式(this, value)