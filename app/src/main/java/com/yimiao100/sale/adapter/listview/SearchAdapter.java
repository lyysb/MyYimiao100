package com.yimiao100.sale.adapter.listview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yimiao100.sale.R;
import com.yimiao100.sale.utils.ViewHolderUtil;

import java.util.ArrayList;

/**
 * Created by 亿苗通 on 2016/9/19.
 */
public class SearchAdapter extends BaseAdapter{


    private final ArrayList<String> mHistoryList;

    public SearchAdapter(ArrayList<String> historyList) {
        mHistoryList = historyList;
    }

    @Override
    public int getCount() {
        return mHistoryList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_history, null);
        }
        TextView tv = ViewHolderUtil.get(convertView, R.id.search_history);
        tv.setText(mHistoryList.get(position));
        return convertView;
    }
}
