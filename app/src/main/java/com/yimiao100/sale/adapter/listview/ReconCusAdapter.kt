package com.yimiao100.sale.adapter.listview

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.yimiao100.sale.R
import com.yimiao100.sale.bean.ReconCus
import com.yimiao100.sale.utils.ViewHolderUtil

/**
 * Created by michel on 2017/9/12.
 */
class ReconCusAdapter(var list: ArrayList<ReconCus>) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val item = getItem(position)

        val v: View
        if (convertView == null) {
            v = LayoutInflater.from(parent.context).inflate(R.layout.item_recon_cus, null)
        } else {
            v = convertView
        }
        val text = ViewHolderUtil.get<TextView>(v, R.id.item_recon_cus_name)
        if (position % 2 == 0) {
            text.setBackgroundColor(Color.parseColor("#ffffff"))
        } else {
            text.setBackgroundColor(Color.parseColor("#f8fbff"))
        }

        text.text = item.customerName

        return v
    }

    override fun getItem(position: Int): ReconCus = list[position]

    override fun getItemId(position: Int): Long= position.toLong()

    override fun getCount(): Int = list.size
}