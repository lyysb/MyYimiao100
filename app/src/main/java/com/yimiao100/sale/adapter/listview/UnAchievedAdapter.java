package com.yimiao100.sale.adapter.listview;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yimiao100.sale.R;
import com.yimiao100.sale.bean.OpenClass;
import com.yimiao100.sale.utils.ViewHolderUtil;

import java.util.ArrayList;

/**
 * 未完成Adapter
 * Created by 亿苗通 on 2016/10/24.
 */

public class UnAchievedAdapter extends BaseAdapter {

    private final ArrayList<OpenClass> mList;

    public UnAchievedAdapter(ArrayList<OpenClass> unAchievedClass) {
        mList = unAchievedClass;
    }

    @Override
    public int getCount() {
        return mList != null ? mList.size() : 0;
    }

    @Override
    public OpenClass getItem(int position) {
        return mList != null ? mList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        OpenClass unAchievedClass = getItem(position);
        if (convertView == null) {
            convertView = View.inflate(parent.getContext(), R.layout.item_un_achieved, null);
        }
        TextView className = ViewHolderUtil.get(convertView, R.id.unAchieved_name);
        TextView vendorName = ViewHolderUtil.get(convertView, R.id.unAchieved_vendor_name);

        className.setText(unAchievedClass.getCourseName());
        vendorName.setText("来源：" + unAchievedClass.getVendorName());
        return convertView;
    }
}
