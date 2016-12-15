package com.yimiao100.sale.adapter.listview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yimiao100.sale.R;
import com.yimiao100.sale.bean.Experience;
import com.yimiao100.sale.utils.ViewHolderUtil;

import java.util.ArrayList;

/**
 * 疫苗推广经历
 * Created by 亿苗通 on 2016/11/29.
 */

public class ExperienceAdapter extends BaseAdapter {

    private final ArrayList<Experience> mList;

    public ExperienceAdapter(ArrayList<Experience> list) {
        mList = list;
    }

    @Override
    public int getCount() {
        return mList != null ? mList.size() : 0;
    }

    @Override
    public Experience getItem(int position) {
        return mList != null ? mList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Experience experience = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_experience, null);
        }
        TextView time = ViewHolderUtil.get(convertView, R.id.experience_item_time);
        TextView region = ViewHolderUtil.get(convertView, R.id.experience_item_region);
        TextView vaccine = ViewHolderUtil.get(convertView, R.id.experience_item_vaccine);
        time.setText(experience.getStartAtFormat() + "至" + experience.getEndAtFormat());
        region.setText(experience.getProvinceName() + experience.getCityName());
        vaccine.setText(experience.getProductName());
        return convertView;
    }
}
