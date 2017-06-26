package com.yimiao100.sale.adapter.listview;

import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yimiao100.sale.R;
import com.yimiao100.sale.bean.ReconciliationList;
import com.yimiao100.sale.utils.FormatUtils;

import java.util.ArrayList;

import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

/**
 * 对账列表Adapter
 * Created by 亿苗通 on 2016/8/25.
 */
public class ReconciliationAdapter extends BaseAdapter {

    private final ArrayList<ReconciliationList> mList;

    public ReconciliationAdapter(ArrayList<ReconciliationList> reconciliationList) {

        mList = reconciliationList;
    }

    @Override
    public int getCount() {
        return mList != null ? mList.size() : 0;
    }

    @Override
    public ReconciliationList getItem(int position) {
        return mList != null ? mList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ReconciliationList reconciliation = getItem(position);
        Holder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout
                    .item_reconciliation, parent, false);
            holder = new Holder();
            holder.reconciliation_vendor_name = (TextView) convertView
                    .findViewById(R.id.reconciliation_vendor_name);

            holder.reconciliation_customer_name = (TextView) convertView
                    .findViewById(R.id.reconciliation_customer_name);

            holder.reconciliation_product_common_name = (TextView) convertView
                    .findViewById(R.id.reconciliation_product_common_name);

            holder.reconciliation_product_formal_name = (TextView) convertView
                    .findViewById(R.id.reconciliation_product_formal_name);

            holder.reconciliation_dosage_form = (TextView) convertView
                    .findViewById(R.id.reconciliation_dosage_form);

            holder.reconciliation_spec = (TextView) convertView
                    .findViewById(R.id.reconciliation_spec);

            holder.reconciliation_serial_no = (TextView) convertView
                    .findViewById(R.id.reconciliation_serial_no);

            holder.reconciliation_total_amount = (TextView) convertView
                    .findViewById(R.id.reconciliation_total_amount);

            holder.badge = new QBadgeView(parent.getContext())
                    .bindTarget(convertView.findViewById(R.id.reconciliation_vendor_name));
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        //厂家名称
        String vendorName = reconciliation.getVendorName();
        holder.reconciliation_vendor_name.setText(vendorName);
        //客户名称
        String customerName = reconciliation.getCustomerName();
        holder.reconciliation_customer_name.setText(customerName);
        //分类
        String categoryName = reconciliation.getCategoryName();
        holder.reconciliation_product_common_name.setText(categoryName);
        //产品名
        String productName = reconciliation.getProductName();
        holder.reconciliation_product_formal_name.setText(productName);
        //剂型
        String dosageForm = reconciliation.getDosageForm();
        holder.reconciliation_dosage_form.setText("剂型：" + dosageForm);
        //规格
        String spec = reconciliation.getSpec();
        holder.reconciliation_spec.setText("规格：" + spec);
        //协议单号
        String serialNo = reconciliation.getSerialNo();
        holder.reconciliation_serial_no.setText("协议单号：" + serialNo);
        //总费用
        double totalAmount = reconciliation.getTotalAmount();
        Spanned totalMoney = Html.fromHtml("总奖励：" + "<font color=\"#ff0000\">" + FormatUtils.MoneyFormat(totalAmount) +
                "</font>" + "元");
        holder.reconciliation_total_amount.setText(totalMoney);
        int tipStatus = reconciliation.getTipStatus();

        holder.badge.setBadgePadding(4, true)
                .setGravityOffset(0, true)
                .setBadgeNumber(tipStatus == 1 ? -1 : 0);
        return convertView;
    }
    class Holder{
        TextView reconciliation_vendor_name;
        TextView reconciliation_customer_name;
        TextView reconciliation_product_common_name;
        TextView reconciliation_product_formal_name;
        TextView reconciliation_dosage_form;
        TextView reconciliation_spec;
        TextView reconciliation_serial_no;
        TextView reconciliation_total_amount;
        Badge badge;
    }
}
