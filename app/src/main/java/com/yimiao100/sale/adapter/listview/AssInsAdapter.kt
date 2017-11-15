package com.yimiao100.sale.adapter.listview

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.TextView
import com.yimiao100.sale.R
import com.yimiao100.sale.bean.AssInsList
import com.yimiao100.sale.bean.AssuranceList
import com.yimiao100.sale.utils.FormatUtils
import com.yimiao100.sale.utils.LogUtil
import com.yimiao100.sale.utils.ViewHolderUtil

/**
 * Created by michel on 2017/8/31.
 */
class AssInsAdapter(var list: ArrayList<AssInsList>): BaseAdapter() {

    private var mListener: OnCheckedChangeListener? = null

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val item = getItem(position)
        val v: View
        if (convertView == null) {
            v = LayoutInflater.from(parent.context).inflate(R.layout.item_ass_ins, null)
        } else {
            v = convertView
        }
        ViewHolderUtil.get<TextView>(v, R.id.ass_ins_region).text =
                "推广区域：${item.provinceName}${item.cityName}${item.areaName}"
        ViewHolderUtil.get<TextView>(v, R.id.ass_ins_product_name).text = "保险名称：${item.productName}"

        ViewHolderUtil.get<TextView>(v, R.id.ass_ins_serial_no).text = "协议单号：${item.serialNo}"

        ViewHolderUtil.get<TextView>(v, R.id.ass_ins_amount).text = Html.fromHtml(
                "可提现金额：<font color=\"#d24141\"><b>${FormatUtils.MoneyFormat(item.orderSaleDeposit)}元</b></font>"
        )

        val checkBox = ViewHolderUtil.get<CheckBox>(v, R.id.ass_ins_check)
        checkBox.isChecked = item.isChecked
        checkBox.setOnClickListener {
            LogUtil.d("checkBox isChecked is ${checkBox.isChecked} ")
            item.isChecked = checkBox.isChecked
            mListener?.onCheckedChanged(position, checkBox.isChecked)
        }

        return v
    }

    fun setOnCheckedChangeListener(listener: OnCheckedChangeListener) {
        mListener = listener
    }

    interface OnCheckedChangeListener {
        fun onCheckedChanged(position: Int, isChecked: Boolean)
    }

    override fun getItem(position: Int): AssInsList = list[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getCount(): Int = list.size
}