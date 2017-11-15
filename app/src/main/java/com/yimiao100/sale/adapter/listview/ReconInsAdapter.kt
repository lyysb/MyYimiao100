package com.yimiao100.sale.adapter.listview

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.yimiao100.sale.R
import com.yimiao100.sale.bean.Reconciliation
import com.yimiao100.sale.utils.FormatUtils
import org.jetbrains.anko.find
import q.rorbin.badgeview.Badge
import q.rorbin.badgeview.QBadgeView

/**
 * Created by michel on 2017/8/30.
 */
class ReconInsAdapter(var list: ArrayList<Reconciliation>) : BaseAdapter(){

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val item = getItem(position)
        val v : View
        val holder: Holder
        if (convertView == null) {
            v = LayoutInflater.from(parent.context).inflate(R.layout.item_recon_ins, null)
            holder = Holder()
            holder.tvCompanyName = v.find<TextView>(R.id.recon_company_name)
            holder.tvCustomerName = v.find<TextView>(R.id.recon_customer_name)
            holder.tvCategoryName = v.find<TextView>(R.id.recon_product_common_name)
            holder.tvProductName = v.find<TextView>(R.id.recon_product_formal_name)
            holder.tvSerialNo = v.find<TextView>(R.id.recon_serial_no)
            holder.tvTotalAmount = v.find<TextView>(R.id.recon_total_amount)
            holder.badge = QBadgeView(parent.context)
                    .bindTarget(v.find(R.id.recon_company_name))
            v.tag = holder
        } else {
            v = convertView
            holder = v.tag as Holder
        }
        holder.tvCompanyName.text = item.companyName
        holder.tvCustomerName.text = item.customerName
        holder.tvCustomerName.visibility = if (item.customerName == null) View.GONE else View.VISIBLE
        holder.tvCategoryName.text = item.categoryName
        holder.tvProductName.text = item.productName
        holder.tvSerialNo.text = "协议单号：${item.serialNo}"
        //总费用
        holder.tvTotalAmount.text =
                Html.fromHtml("总奖励：<font color=\"#ff0000\">${FormatUtils.MoneyFormat(item.saleTotalAmount)}</font>元").toString()
        holder.badge.setBadgePadding(4f, true)
                .setGravityOffset(0f, true).badgeNumber = if (item.tipStatus == 1) -1 else 0
        return v
    }

    internal inner class Holder {
        lateinit var tvCompanyName: TextView
        lateinit var tvCustomerName: TextView
        lateinit var tvCategoryName: TextView
        lateinit var tvProductName: TextView
        lateinit var tvSerialNo: TextView
        lateinit var tvTotalAmount: TextView
        lateinit var badge: Badge
    }

    override fun getItem(position: Int): Reconciliation = list[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getCount(): Int = list.size
}