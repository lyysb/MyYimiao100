package com.yimiao100.sale.adapter.listview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yimiao100.sale.R;
import com.yimiao100.sale.bean.CDCListBean;
import com.yimiao100.sale.utils.ViewHolderUtil;

import java.util.ArrayList;

/**
 * Created by 亿苗通 on 2016/8/29.
 */
public class CustomerAdapter extends BaseAdapter {

    private final LayoutInflater mInflater;
    private final ArrayList<CDCListBean> cdcList;

    public CustomerAdapter(Context context, ArrayList<CDCListBean> list) {
        mInflater = LayoutInflater.from(context);
        cdcList = list;
    }

    @Override
    public int getCount() {
        return cdcList != null ? cdcList.size() : 0;
    }

    @Override
    public CDCListBean getItem(int position) {
        return cdcList != null ? cdcList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CDCListBean cdc = getItem(position);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_customer, parent, false);
        }
        //CDC名称
        TextView cdc_name = ViewHolderUtil.get(convertView, R.id.cdc_name);
        String cdcName = cdc.getCdcName();
        cdc_name.setText(cdcName);
        //CDC地址
        TextView cdc_address = ViewHolderUtil.get(convertView, R.id.cdc_address);
        String address = cdc.getAddress();
        cdc_address.setText(address);
        //CDC电话
        TextView cdc_phone_number = ViewHolderUtil.get(convertView, R.id.cdc_phone_number);
        String phoneNumber = cdc.getPhoneNumber();
        cdc_phone_number.setText(phoneNumber);

        return convertView;
    }
}
