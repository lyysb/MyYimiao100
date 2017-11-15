package com.yimiao100.sale.adapter.listview

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.TextView
import com.yimiao100.sale.R
import com.yimiao100.sale.bean.Overdue
import com.yimiao100.sale.utils.FormatUtils
import com.yimiao100.sale.utils.LogUtil
import com.yimiao100.sale.utils.ViewHolderUtil

/**
 * Created by michel on 2017/10/31.
 */
class OverdueAdapter(var list: ArrayList<Overdue>): BaseAdapter() {


    private var mListener: OnCheckedChangeListener? = null

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val item = getItem(position)
        val v: View
        if (convertView == null) {
            v = LayoutInflater.from(parent.context).inflate(R.layout.item_overdue, null)
        } else {
            v = convertView
        }

        ViewHolderUtil.get<TextView>(v, R.id.overdue_amount).text = Html.fromHtml(
                "<font color=\"#d24141\"><b>${FormatUtils.MoneyFormat(item.overdueAmount)}元</b></font>"
        )

        val checkBox = ViewHolderUtil.get<CheckBox>(v, R.id.overdue_ck)
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
    override fun getItem(position: Int): Overdue = list[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getCount(): Int = list.size
}