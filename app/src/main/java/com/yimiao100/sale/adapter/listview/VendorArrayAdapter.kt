package com.yimiao100.sale.adapter.listview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.squareup.picasso.Picasso
import com.yimiao100.sale.R
import com.yimiao100.sale.bean.BizList
import com.yimiao100.sale.utils.DensityUtil
import com.yimiao100.sale.utils.ViewHolderUtil
import de.hdodenhof.circleimageview.CircleImageView
import org.jetbrains.anko.find
import q.rorbin.badgeview.QBadgeView

/**
 * Created by michel on 2017/8/28.
 */
class VendorArrayAdapter(var list: ArrayList<BizList>): BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val biz = getItem(position)
        val v: View
        if (convertView == null) {
            v = LayoutInflater.from(parent.context).inflate(R.layout.item_vendor_list, null)
        } else {
            v = convertView
        }
        val logo = ViewHolderUtil.get<CircleImageView>(v, R.id.vendor_logo)
        Picasso.with(parent.context).load(biz.imageUrl)
                .placeholder(R.mipmap.ico_default_short_picture)
                .resize(DensityUtil.dp2px(parent.context, 50f), DensityUtil.dp2px(parent.context, 50f))
                .into(logo)
        ViewHolderUtil.get<TextView>(v, R.id.vendor_title).text = biz.objectName
        QBadgeView(parent.context)
                .bindTarget(v.find(R.id.vendor_logo))
                .setBadgePadding(4f, true)
                .setGravityOffset(0f, true).badgeNumber = if (biz.tipStatus == 1) -1 else 0
        return v
    }

    override fun getItem(position: Int): BizList = list[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getCount(): Int = list.size
}