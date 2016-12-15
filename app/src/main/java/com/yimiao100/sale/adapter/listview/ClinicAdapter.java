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
 * 门诊列表Adapter
 * Created by 亿苗通 on 2016/8/29.
 */
public class ClinicAdapter extends BaseAdapter {

    private final LayoutInflater mInflater;
    private final ArrayList<CDCListBean> clinicList;

    public ClinicAdapter(Context context, ArrayList<CDCListBean> list) {
        mInflater = LayoutInflater.from(context);
        clinicList = list;
    }

    @Override
    public int getCount() {
        return clinicList != null ? clinicList.size() : 0;
    }

    @Override
    public CDCListBean getItem(int position) {
        return clinicList != null ? clinicList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CDCListBean clinic = getItem(position);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_customer, parent, false);
        }
        //门诊名称
        TextView cdc_name = ViewHolderUtil.get(convertView, R.id.cdc_name);
        String cdcName = clinic.getClinicName();
        cdc_name.setText(cdcName);
        //门诊地址
        TextView cdc_address = ViewHolderUtil.get(convertView, R.id.cdc_address);
        String address = clinic.getAddress();
        cdc_address.setText(address);
        //门诊电话
        TextView cdc_phone_number = ViewHolderUtil.get(convertView, R.id.cdc_phone_number);
        String phoneNumber = clinic.getPhoneNumber();
        cdc_phone_number.setText(phoneNumber);

        return convertView;
    }
}
