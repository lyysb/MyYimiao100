package com.yimiao100.sale.adapter.listview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import com.yimiao100.sale.R
import com.yimiao100.sale.bean.InsuranceDetail
import com.yimiao100.sale.utils.LogUtil
import com.yimiao100.sale.utils.TimeUtil
import com.yimiao100.sale.utils.ViewHolderUtil

/**
 * Created by michel on 2017/8/22.
 */
class InsuranceAdapter(var list: ArrayList<InsuranceDetail>): BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val insuranceDetail = getItem(position)
        val v: View
        if (convertView == null) {
            v = LayoutInflater.from(parent.context).inflate(R.layout.item_insurance, null)
        } else {
            v = convertView
        }
        val logo = ViewHolderUtil.get<ImageView>(v, R.id.insurance_company_logo)
        if (insuranceDetail.companyImageUrl.isNotEmpty()) {
            Picasso.with(parent.context).load(insuranceDetail.companyImageUrl).into(logo)
        }
        ViewHolderUtil.get<TextView>(v, R.id.insurance_company_name).text = insuranceDetail.companyName
        ViewHolderUtil.get<TextView>(v, R.id.insurance_product_name).text = "保险名称：${insuranceDetail.productName}"
        ViewHolderUtil.get<TextView>(v, R.id.insurance_region).text =
                "区域：${insuranceDetail.provinceName}\t\t${insuranceDetail.cityName}\t\t${insuranceDetail.areaName}"
        ViewHolderUtil.get<TextView>(v, R.id.insurance_customer).text = "客户：${insuranceDetail.customerName}"
        ViewHolderUtil.get<TextView>(v, R.id.insurance_customer).visibility =
                if (insuranceDetail.customerName == null) View.GONE else View.VISIBLE
        ViewHolderUtil.get<TextView>(v, R.id.insurance_updated_at).text =
            TimeUtil.timeStamp2Date(insuranceDetail.updatedAt.toString(), "yyyy年MM月dd日")
        return v
    }

    override fun getItem(position: Int): InsuranceDetail = list[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getCount(): Int = list.size
}