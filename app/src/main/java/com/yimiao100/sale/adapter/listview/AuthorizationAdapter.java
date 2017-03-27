package com.yimiao100.sale.adapter.listview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yimiao100.sale.R;
import com.yimiao100.sale.bean.AuthorizationList;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.TimeUtil;
import com.yimiao100.sale.utils.ViewHolderUtil;

import java.util.ArrayList;

/**
 * 申请授权书Adapter
 * Created by Michel on 2017/3/20.
 */

public class AuthorizationAdapter extends BaseAdapter {

    private final ArrayList<AuthorizationList.PagedResultBean.Authorization> mList;

    public AuthorizationAdapter(ArrayList<AuthorizationList.PagedResultBean.Authorization> list) {
        mList = list;
    }

    @Override
    public int getCount() {
        return mList != null ? mList.size() : 0;
    }

    @Override
    public AuthorizationList.PagedResultBean.Authorization getItem(int position) {
        return mList != null ? mList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return mList != null ? position : 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AuthorizationList.PagedResultBean.Authorization authorization = getItem(position);
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.item_authorization, null);
        }
        TextView year = ViewHolderUtil.get(convertView, R.id.authorization_item_year);
        TextView monthDay = ViewHolderUtil.get(convertView, R.id.authorization_item_month_day);
        TextView region = ViewHolderUtil.get(convertView, R.id.authorization_item_region);
        TextView vendorName = ViewHolderUtil.get(convertView, R.id.authorization_item_vendor_name);
        TextView expressNo = ViewHolderUtil.get(convertView, R.id.authorization_item_express_no);
        ImageView applyStatus = ViewHolderUtil.get(convertView, R.id.authorization_item_apply_status);
        year.setText(TimeUtil.timeStamp2Date(authorization.getCreatedAt() + "", "yyyy年"));
        monthDay.setText(TimeUtil.timeStamp2Date(authorization.getCreatedAt() + "", "MM月dd日"));
        region.setText(authorization.getProvinceName());
        vendorName.setText(authorization.getVendorName());
        expressNo.setVisibility(authorization.getExpressNo() == null ? View.GONE : View.VISIBLE);
        expressNo.setText("快递单号：" + authorization.getExpressNo());
        switch (authorization.getApplyStatus()) {
            case "auditing":
                applyStatus.setImageResource(R.mipmap.ico_application_record_pending_audit);
                break;
            case "passed":
                applyStatus.setImageResource(R.mipmap.ico_application_record_adopt);
                break;
            default:
                LogUtil.Companion.d("Unknown apply status");
                break;
        }
        return convertView;
    }
}
