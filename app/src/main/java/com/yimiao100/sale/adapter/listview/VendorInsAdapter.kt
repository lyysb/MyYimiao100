package com.yimiao100.sale.adapter.listview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ProgressBar
import android.widget.TextView
import com.yimiao100.sale.R
import com.yimiao100.sale.bean.InsVendor
import com.yimiao100.sale.utils.FormatUtils
import com.yimiao100.sale.utils.ViewHolderUtil
import org.jetbrains.anko.find
import q.rorbin.badgeview.QBadgeView

/**
 * Created by michel on 2017/9/11.
 */
class VendorInsAdapter(var list: ArrayList<InsVendor>) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val item = getItem(position)
        val v: View
        if (convertView == null) {
            v = LayoutInflater.from(parent.context).inflate(R.layout.item_vendor_ins, null)
        } else {
            v = convertView
        }
        ViewHolderUtil.get<TextView>(v, R.id.item_vendor_ins_company_name).text = item.companyName
        ViewHolderUtil.get<TextView>(v, R.id.item_vendor_ins_region).text =
                "${item.provinceName}\t${item.cityName}\t${item.areaName}"
        ViewHolderUtil.get<TextView>(v, R.id.item_vendor_ins_count).text = "保单数量：${item.policyCounter}单"
        ViewHolderUtil.get<TextView>(v, R.id.item_vendor_ins_total_amount).text =
                "保单总金额：${FormatUtils.MoneyFormat(item.policyTotalAmount)}元"
        ViewHolderUtil.get<TextView>(v, R.id.item_vendor_ins_total_sale).text =
                "总金额奖励：${FormatUtils.MoneyFormat(item.saleTotalAmount)}元"
        val progressBar = ViewHolderUtil.get<ProgressBar>(v, R.id.item_vendor_ins_progress)
        progressBar.progress = item.policyTotalAmount.toInt()
        progressBar.max = item.saleQuota.toInt()

        ViewHolderUtil.get<TextView>(v, R.id.item_vendor_ins_serial_no).text = "协议单号：${item.serialNo}"
        ViewHolderUtil.get<TextView>(v, R.id.item_vendor_ins_pro_text).text =
                "${item.policyTotalAmount.toInt()}/${item.saleQuota.toInt()}"
        QBadgeView(parent.context)
                .bindTarget(v.find(R.id.item_vendor_ins))
                .setBadgePadding(4f, true)
                .setGravityOffset(0f, true).badgeNumber = if (item.tipStatus == 1) -1 else 0
        return v
    }

    override fun getItem(position: Int): InsVendor = list[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getCount(): Int = list.size
}