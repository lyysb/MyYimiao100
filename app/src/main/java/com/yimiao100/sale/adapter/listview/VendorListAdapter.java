package com.yimiao100.sale.adapter.listview;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yimiao100.sale.R;
import com.yimiao100.sale.bean.Vendor;
import com.yimiao100.sale.utils.DensityUtil;
import com.yimiao100.sale.utils.ViewHolderUtil;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import q.rorbin.badgeview.QBadgeView;

/**
 * 厂家列表Adapter
 * Created by 亿苗通 on 2016/10/25.
 */

public class VendorListAdapter extends BaseAdapter {

    private final ArrayList<Vendor> mList;

    public VendorListAdapter(ArrayList<Vendor> vendorList) {
        mList = vendorList;
    }

    @Override
    public int getCount() {
        return mList != null ? mList.size() : 0;
    }

    @Override
    public Vendor getItem(int position) {
        return mList != null ? mList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Vendor vendor = getItem(position);
        if (convertView == null) {
            convertView = View.inflate(parent.getContext(), R.layout.item_vendor_list, null);
        }
        CircleImageView logo = ViewHolderUtil.get(convertView, R.id.vendor_logo);
        TextView title = ViewHolderUtil.get(convertView, R.id.vendor_title);
        Picasso.with(parent.getContext()).load(vendor.getLogoImageUrl())
                .placeholder(R.mipmap.ico_default_short_picture)
                .resize(DensityUtil.dp2px(parent.getContext(), 50), DensityUtil.dp2px(parent.getContext(), 50))
                .into(logo);
        title.setText(vendor.getVendorName());
        new QBadgeView(parent.getContext())
                .bindTarget(convertView.findViewById(R.id.vendor_logo))
                .setBadgePadding(4, true)
                .setGravityOffset(0, true)
                .setBadgeNumber(vendor.getTipStatus() == 1 ? -1 : 0);
        //设置小圆点可见性
//        ImageView tipStatus = ViewHolderUtil.get(convertView, R.id.vendor_tip_status);
//        tipStatus.setVisibility(vendor.getTipStatus() == 1 ? View.VISIBLE : View.INVISIBLE);
        return convertView;
    }
}
