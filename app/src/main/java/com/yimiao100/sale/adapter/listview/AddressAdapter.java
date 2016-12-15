package com.yimiao100.sale.adapter.listview;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yimiao100.sale.R;
import com.yimiao100.sale.bean.Address;
import com.yimiao100.sale.utils.ViewHolderUtil;

import java.util.ArrayList;

/**
 * 地址Adapter
 * Created by 亿苗通 on 2016/10/26.
 */

public class AddressAdapter extends BaseAdapter {

    private final ArrayList<Address> mList;
    private OnClickListener mListener;

    public AddressAdapter(ArrayList<Address> addressList) {
        mList = addressList;
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public Address getItem(int position) {
        return mList == null ? null : mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Address address = getItem(position);
        if (convertView == null) {
            convertView = View.inflate(parent.getContext(), R.layout.item_address, null);
        }

        RelativeLayout address_item = ViewHolderUtil.get(convertView, R.id.address_item);
        CheckBox address_default = ViewHolderUtil.get(convertView, R.id.address_default);
        TextView address_person_name = ViewHolderUtil.get(convertView, R.id.address_person_name);
        TextView address_phone_number = ViewHolderUtil.get(convertView, R.id.address_phone_number);
        TextView address_full = ViewHolderUtil.get(convertView, R.id.address_full);
        TextView address_edit = ViewHolderUtil.get(convertView, R.id.address_edit);
        TextView address_delete = ViewHolderUtil.get(convertView, R.id.address_delete);

        String cnName = address.getCnName();
        String phoneNumber = address.getPhoneNumber();
        String addressFull = address.getProvinceName() + address.getCityName() + address.getAreaName() +
                address.getFullAddress();
        int isDefault = address.getIsDefault();

        address_person_name.setText(cnName);
        address_phone_number.setText(phoneNumber);
        address_full.setText(addressFull);
        address_default.setChecked(isDefault == 1);
        address_default.setText(isDefault == 1 ? "默认地址" : "设为默认");

        //点击事件处理
        address_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.editAddress(position);
                }
            }
        });
        address_default.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.setDefault(position);
                }
            }
        });
        address_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.editAddress(position);
                }
            }
        });
        address_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.deleteAddress(position);
                }
            }
        });
        return convertView;
    }

    public void setOnClickListener(OnClickListener listener) {
        mListener = listener;
    }
    public interface OnClickListener{
        /**
         * 编辑地址
         * @param position
         */
        void editAddress(int position);

        /**
         * 删除地址
         * @param position
         */
        void deleteAddress(int position);

        /**
         * 设为默认地址
         * @param position
         */
        void setDefault(int position);
    }
}
