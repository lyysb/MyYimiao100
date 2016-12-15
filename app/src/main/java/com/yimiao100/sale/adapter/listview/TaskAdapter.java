package com.yimiao100.sale.adapter.listview;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yimiao100.sale.R;
import com.yimiao100.sale.bean.OpenClass;
import com.yimiao100.sale.utils.ViewHolderUtil;

import java.util.ArrayList;

/**
 * 全部任务Adapter
 * Created by 亿苗通 on 2016/10/24.
 */

public class TaskAdapter extends BaseAdapter {

    private final ArrayList<OpenClass> mList;

    public TaskAdapter(ArrayList<OpenClass> taskClasses) {
        mList = taskClasses;
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
        OpenClass taskClass = getItem(position);
        if (convertView == null) {
            convertView = View.inflate(parent.getContext(), R.layout.item_task, null);
        }
        TextView className = ViewHolderUtil.get(convertView, R.id.task_item_title);
        TextView vendorName = ViewHolderUtil.get(convertView, R.id.task_item_vendor_name);
        ImageView status = ViewHolderUtil.get(convertView, R.id.task_item_status);

        className.setText(taskClass.getCourseName());
        vendorName.setText("来源：" + taskClass.getVendorName());
        if (taskClass.getIntegralStatus() == 1) {
            status.setImageResource(R.mipmap.ico_integral_already_complete);
        } else {
            status.setImageResource(R.mipmap.ico_integral_to_complete);
        }


        return convertView;
    }
}
