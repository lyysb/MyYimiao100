package com.yimiao100.sale.adapter.listview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.yimiao100.sale.R
import com.yimiao100.sale.bean.ReconInsDetail
import com.yimiao100.sale.utils.FormatUtils
import com.yimiao100.sale.utils.TimeUtil
import com.yimiao100.sale.utils.ViewHolderUtil

/**
 * Created by michel on 2017/8/30.
 */
class PromoInsDetailAdapter(var list: ArrayList<ReconInsDetail>): BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val item = getItem(position)
        val v: View
        if (convertView == null) {
            v = LayoutInflater.from(parent.context).inflate(R.layout.item_promo_ins_detail, null)
        } else {
            v = convertView
        }

        ViewHolderUtil.get<TextView>(v, R.id.promo_customer_name).text = item.customerName
        ViewHolderUtil.get<TextView>(v, R.id.promo_package_name).text = "套餐名称：${item.packageName}"
        ViewHolderUtil.get<TextView>(v, R.id.promo_policy_no).text = "流水单号：${item.policyNo}"
        ViewHolderUtil.get<TextView>(v, R.id.promo_amount).text = "保费单价(元/年)：${FormatUtils.MoneyFormat(item.unitPrice)}元"
        ViewHolderUtil.get<TextView>(v, R.id.promo_period).text = "保险期限：${item.insurePeriod}年"
        ViewHolderUtil.get<TextView>(v, R.id.promo_total_amount).text = "保费合计：${FormatUtils.MoneyFormat(item.policyAmount)}元"
        ViewHolderUtil.get<TextView>(v, R.id.promo_create_at).text =
                "保单生成日期：${TimeUtil.timeStamp2Date(item.policyCreatedAt, "yyyy.MM.dd")}"
        ViewHolderUtil.get<TextView>(v, R.id.promo_effective).text =
                "保单生效日期：${TimeUtil.timeStamp2Date(item.policyStartAt, "yyyy.MM.dd")} - ${TimeUtil.timeStamp2Date(item.policyEndAt, "yyyy.MM.dd")}"
        ViewHolderUtil.get<TextView>(v, R.id.promo_reward).text = "奖励金额：${FormatUtils.MoneyFormat(item.itemAmount)}元"


        return v
    }

    override fun getItem(position: Int): ReconInsDetail = list[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getCount(): Int = list.size
}