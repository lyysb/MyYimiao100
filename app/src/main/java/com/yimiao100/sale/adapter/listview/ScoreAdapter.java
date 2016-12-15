package com.yimiao100.sale.adapter.listview;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yimiao100.sale.R;
import com.yimiao100.sale.bean.OpenClass;
import com.yimiao100.sale.utils.ViewHolderUtil;

import java.util.ArrayList;

/**
 * 领取积分Adapter
 * Created by 亿苗通 on 2016/10/24.
 */

public class ScoreAdapter extends BaseAdapter {

    private final ArrayList<OpenClass> mList;

    public ScoreAdapter(ArrayList<OpenClass> integralClasses) {
        mList = integralClasses;
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
        OpenClass integralClass = getItem(position);
        if (convertView == null) {
            convertView = View.inflate(parent.getContext(), R.layout.item_score, null);
        }
        TextView className = ViewHolderUtil.get(convertView, R.id.get_integral_name);
        TextView vendorName = ViewHolderUtil.get(convertView, R.id.get_integral_vendor_name);
        TextView value = ViewHolderUtil.get(convertView, R.id.get_integral_value);

        className.setText(integralClass.getCourseName());
        vendorName.setText("来源：" + integralClass.getVendorName());
        if (integralClass.getIntegralStatus() == 1) {
            value.setBackgroundResource(R.mipmap.ico_integral_already_receive);
            value.setText("已领取" + integralClass.getIntegralValue() + "积分");
            value.setTextColor(Color.parseColor("#666666"));
        } else {
            value.setBackgroundResource(R.mipmap.ico_integral_receive);
            value.setText("领取" + integralClass.getIntegralValue() + "积分");
        }


        return convertView;
    }
}
