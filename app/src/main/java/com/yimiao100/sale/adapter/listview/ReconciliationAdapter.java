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
import com.yimiao100.sale.utils.ViewHolderUtil;

import java.util.ArrayList;

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
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout
                    .item_reconciliation, parent, false);
        }
        //厂家名称
        String vendorName = reconciliation.getVendorName();
        TextView reconciliation_vendor_name = ViewHolderUtil.get(convertView, R.id
                .reconciliation_vendor_name);
        reconciliation_vendor_name.setText(vendorName);
        //客户名称
        String customerName = reconciliation.getCustomerName();
        TextView reconciliation_customer_name = ViewHolderUtil.get(convertView, R.id
                .reconciliation_customer_name);
        reconciliation_customer_name.setText(customerName);
        //分类
        String categoryName = reconciliation.getCategoryName();
        TextView reconciliation_product_common_name = ViewHolderUtil.get(convertView, R.id
                .reconciliation_product_common_name);
        reconciliation_product_common_name.setText(categoryName);
        //产品名
        String productName = reconciliation.getProductName();
        TextView reconciliation_product_formal_name = ViewHolderUtil.get(convertView, R.id
                .reconciliation_product_formal_name);
        reconciliation_product_formal_name.setText(productName);
        //剂型
        String dosageForm = reconciliation.getDosageForm();
        TextView reconciliation_dosage_form = ViewHolderUtil.get(convertView, R.id
                .reconciliation_dosage_form);
        reconciliation_dosage_form.setText("剂型：" + dosageForm);
        //规格
        String spec = reconciliation.getSpec();
        TextView reconciliation_spec = ViewHolderUtil.get(convertView, R.id.reconciliation_spec);
        reconciliation_spec.setText("规格：" + spec);
        //协议单号
        String serialNo = reconciliation.getSerialNo();
        TextView reconciliation_serial_no = ViewHolderUtil.get(convertView, R.id
                .reconciliation_serial_no);
        reconciliation_serial_no.setText("协议单号：" + serialNo);
        //总费用
        double totalAmount = reconciliation.getTotalAmount();
        Spanned totalMoney = Html.fromHtml("总奖励：" + "<font color=\"#ff0000\">" + FormatUtils.MoneyFormat(totalAmount) +
                "</font>" + "元");
        TextView reconciliation_total_amount = ViewHolderUtil.get(convertView, R.id
                .reconciliation_total_amount);
        reconciliation_total_amount.setText(totalMoney);

        return convertView;
    }
}
