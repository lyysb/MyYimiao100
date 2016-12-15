package com.yimiao100.sale.adapter.listview;

import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.yimiao100.sale.R;
import com.yimiao100.sale.bean.AssuranceList;
import com.yimiao100.sale.utils.FormatUtils;
import com.yimiao100.sale.utils.ViewHolderUtil;

import java.util.ArrayList;

/**
 * 保证金提现Adapter
 * Created by 亿苗通 on 2016/9/9.
 */
public class AssuranceAdapter extends BaseAdapter {


    private final ArrayList<AssuranceList> mList;
    private OnCheckedChangeListener mListener;

    public AssuranceAdapter(ArrayList<AssuranceList> list) {
        mList = list;
    }

    @Override
    public int getCount() {
        return mList != null ? mList.size() : 0;
    }

    @Override
    public AssuranceList getItem(int position) {
        return mList != null ? mList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        AssuranceList assuranceList = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_assurance, null);
        }
        //客户名称
        String customerName = assuranceList.getCustomerName();
        TextView assurance_customer_name = ViewHolderUtil.get(convertView, R.id.assurance_customer_name);
        assurance_customer_name.setText(customerName);
        //产品名
        String productName = assuranceList.getProductName();
        TextView assurance_product_formal_name = ViewHolderUtil.get(convertView, R.id.assurance_product_formal_name);
        assurance_product_formal_name.setText(productName);
        //分类名
        String categoryName = assuranceList.getCategoryName();
        TextView assurance_product_common_name = ViewHolderUtil.get(convertView, R.id.assurance_product_common_name);
        assurance_product_common_name.setText(categoryName);
        //剂型
        String dosageForm = assuranceList.getDosageForm();
        TextView assurance_dosage_form = ViewHolderUtil.get(convertView, R.id.assurance_dosage_form);
        assurance_dosage_form.setText("剂型：" + dosageForm);
        //规格
        String spec = assuranceList.getSpec();
        TextView assurance_spec = ViewHolderUtil.get(convertView, R.id.assurance_spec);
        assurance_spec.setText("规格：" + spec);
        //协议单号
        String serialNo = assuranceList.getSerialNo();
        TextView assurance_serial_no = ViewHolderUtil.get(convertView, R.id.assurance_serial_no);
        assurance_serial_no.setText("协议单号：" + serialNo);
        //保证金
        double totalDepositWithdraw = assuranceList.getTotalDepositWithdraw();
        TextView assurance_total_amount = ViewHolderUtil.get(convertView, R.id.assurance_total_amount);
        Spanned amount = Html.fromHtml("推广保证金：￥<font color=\"#d24141\">" + FormatUtils.MoneyFormat
                (totalDepositWithdraw) + "</font>元");
        assurance_total_amount.setText(amount);


        //记录点击状态，方便清空点击状态
        CheckBox assurance_check = ViewHolderUtil.get(convertView, R.id.assurance_check);
        assurance_check.setChecked(assuranceList.isChecked());
        assurance_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mListener != null) {
                    mListener.onCheckedChanged(position, isChecked);
                }
            }
        });
        return convertView;
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        mListener = listener;
    }
    public interface OnCheckedChangeListener{
        void onCheckedChanged(int position, boolean isChecked);
    }
}
