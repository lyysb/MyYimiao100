package com.yimiao100.sale.adapter.listview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yimiao100.sale.R;
import com.yimiao100.sale.utils.ViewHolderUtil;

import java.util.ArrayList;

/**
 * 搜索历史Adapter
 * Created by 亿苗通 on 2016/9/19.
 */
public class SearchAdapter extends BaseAdapter{


    private final ArrayList<String> mHistoryList;
    private OnDeleteClickListener mListener;

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
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_history, null);
        }
        TextView tv = ViewHolderUtil.get(convertView, R.id.search_history);
        tv.setText(mHistoryList.get(position));
        ImageView delete = ViewHolderUtil.get(convertView, R.id.search_delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.delete(position);
                }
            }
        });
        return convertView;
    }

    /**
     * 删除按钮点击监听
     * @param listener
     */
    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        mListener = listener;
    }

    public interface OnDeleteClickListener {
        void delete(int position);
    }
}
