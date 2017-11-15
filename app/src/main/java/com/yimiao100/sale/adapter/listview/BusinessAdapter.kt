package com.yimiao100.sale.adapter.listview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.squareup.picasso.Picasso
import com.yimiao100.sale.R
import com.yimiao100.sale.bean.Business
import com.yimiao100.sale.utils.TimeUtil
import com.yimiao100.sale.utils.ViewHolderUtil

/**
 * Created by michel on 2017/8/28.
 */
class BusinessAdapter(
        var list: ArrayList<Business>
): BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val business = getItem(position)
        val v: View
        if (convertView == null) {
            v = LayoutInflater.from(parent.context).inflate(R.layout.item_business, null)
        } else {
            v = convertView
        }
        val objectName = ViewHolderUtil.get<TextView>(v, R.id.business_vendor_name)
        objectName.text = business.companyName
        ViewHolderUtil.get<TextView>(v, R.id.business_product_commonName).text = "保险名称：${business.productName}"
        ViewHolderUtil.get<TextView>(v, R.id.business_region).text =
            "区域：${business.provinceName}\t${business.cityName}\t${business.areaName}"
        ViewHolderUtil.get<TextView>(v, R.id.business_updated_at).text =
            TimeUtil.timeStamp2Date(business.updatedAt.toString(), "yyyy.MM.dd")
        val logo = ViewHolderUtil.get<ImageView>(v, R.id.business_vendor_logo)
        Picasso.with(parent.context).load(business.companyImageUrl).into(logo)
        val customerView = ViewHolderUtil.get<TextView>(v, R.id.business_customer)
        customerView.text = "客户：${business.customerName}"
        customerView.visibility = if (business.customerName == null) View.GONE else View.VISIBLE
        //协议单号
        ViewHolderUtil.get<TextView>(v, R.id.business_serial_no).text = "协议单号：${business.serialNo}"

        val businessStatus = ViewHolderUtil.get<TextView>(v, R.id.business_status)
        businessStatus.text = business.orderStatusName

        when (business.orderStatus) {
            "unpaid" ->
                //最初状态-未支付
                businessStatus.setTextColor(parent.context.resources.getColor(R.color.colorBrown))
            "bidding" -> {
                //第一状态-竞标中
                businessStatus.setTextColor(parent.context.resources.getColor(R.color.colorOrigin))
                //错误状态-已违约
                businessStatus.setTextColor(parent.context.resources.getColor(R.color.colorRed))
            }
            "defaulted" ->
                businessStatus.setTextColor(parent.context.resources.getColor(R.color.colorRed))
            "to_be_signed" ->
                //第三状态-待签约
                businessStatus.setTextColor(parent.context.resources.getColor(R.color.color666))
            "already_signed" ->
                //第四状态-已签约
                businessStatus.setTextColor(parent.context.resources.getColor(R.color.colorMain))
            "end" ->
                // 最终状态-已终止
                businessStatus.setTextColor(parent.context.resources.getColor(R.color.colorEnd))
            else ->
                //其他界面--审核中|未通过
                businessStatus.setTextColor(parent.context.resources.getColor(R.color.color999))
        }
        return v
    }

    override fun getItem(position: Int): Business = list[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getCount(): Int = list.size
}