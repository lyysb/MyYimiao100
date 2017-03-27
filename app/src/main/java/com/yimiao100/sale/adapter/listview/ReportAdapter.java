package com.yimiao100.sale.adapter.listview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yimiao100.sale.R;
import com.yimiao100.sale.bean.AdverseList;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.TimeUtil;
import com.yimiao100.sale.utils.ViewHolderUtil;

import java.util.ArrayList;

/**
 * 申报记录Adapter
 * Created by Michel on 2017/3/20.
 */

public class ReportAdapter extends BaseAdapter {

    private final ArrayList<AdverseList.PagedResult.Adverse> mList;

    public ReportAdapter(ArrayList<AdverseList.PagedResult.Adverse> list) {
        mList = list;
    }

    @Override
    public int getCount() {
        return mList != null ? mList.size() : 0;
    }

    @Override
    public AdverseList.PagedResult.Adverse getItem(int position) {
        return mList != null ? mList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return mList != null ? position : 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AdverseList.PagedResult.Adverse adverse = getItem(position);
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.item_report, null);
        }
        TextView year = ViewHolderUtil.get(convertView, R.id.report_item_year);
        TextView monthDay = ViewHolderUtil.get(convertView, R.id.report_item_month_day);
        TextView region = ViewHolderUtil.get(convertView, R.id.report_item_region);
        TextView vendorName = ViewHolderUtil.get(convertView, R.id.report_item_vendor_name);
        ImageView applyStatus = ViewHolderUtil.get(convertView, R.id.report_item_apply_status);
        year.setText(TimeUtil.timeStamp2Date(adverse.getCreatedAt() + "", "yyyy年"));
        monthDay.setText(TimeUtil.timeStamp2Date(adverse.getCreatedAt() + "", "MM月dd日"));
        region.setText(adverse.getProvinceName() + "\t" + adverse.getCityName() + "\t" + adverse.getAreaName());
        vendorName.setText(adverse.getVendorName());
        switch (adverse.getApplyStatus()) {
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
