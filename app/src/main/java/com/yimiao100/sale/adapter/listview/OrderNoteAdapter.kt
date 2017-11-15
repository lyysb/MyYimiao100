package com.yimiao100.sale.adapter.listview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.yimiao100.sale.R
import com.yimiao100.sale.bean.OrderNote
import com.yimiao100.sale.utils.TimeUtil
import com.yimiao100.sale.utils.ViewHolderUtil

/**
 * Created by Michel on 2017/6/9.
 */
class OrderNoteAdapter(var list: ArrayList<OrderNote>?): BaseAdapter() {


    override fun getCount(): Int = list?.size ?: 0

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getItem(position: Int): OrderNote = list?.get(position) ?: null!!

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val orderNote = getItem(position)
        val v: View
        if (convertView == null) {
            v = LayoutInflater.from(parent.context).inflate(R.layout.item_order_note, null)
        } else {
            v = convertView
        }
        ViewHolderUtil.get<TextView>(v, R.id.order_item_category_name).text = "产品名称：        ${orderNote.categoryName}"
        ViewHolderUtil.get<TextView>(v, R.id.order_item_consignee_name).text = "联系人姓名：    ${orderNote.consigneeName}"
        ViewHolderUtil.get<TextView>(v, R.id.order_item_delivery_at).text = "申请发货日期：${TimeUtil.timeStamp2Date(orderNote.applyDeliveryAt.toString(), "yyyy年MM月dd日")}"
        ViewHolderUtil.get<TextView>(v, R.id.order_item_customer).text = "客\t\t\t户：${orderNote.customerName}"
        ViewHolderUtil.get<TextView>(v, R.id.order_item_consignee_phone).text = "联系电话：${orderNote.consigneePhoneNumber}"
        ViewHolderUtil.get<TextView>(v, R.id.order_item_delivery_qty).text = "发货数量：${orderNote.deliveryQty}\t${orderNote.deliveryUnits}"
        ViewHolderUtil.get<TextView>(v, R.id.order_item_address).text = "详细地址：        ${orderNote.consigneeAddress}"
        return v
    }
}