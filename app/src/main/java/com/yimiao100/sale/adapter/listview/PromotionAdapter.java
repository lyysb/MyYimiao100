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
import com.yimiao100.sale.bean.PromotionList;
import com.yimiao100.sale.utils.FormatUtils;
import com.yimiao100.sale.utils.ViewHolderUtil;

import java.util.ArrayList;

/**
 * 推广费提现Adapter
 * Created by 亿苗通 on 2016/9/9.
 */
public class PromotionAdapter extends BaseAdapter {


    private final ArrayList<PromotionList> mList;
    private OnCheckedChangeListener mListener;

    public PromotionAdapter(ArrayList<PromotionList> list) {
        mList = list;
    }

    @Override
    public int getCount() {
        return mList != null ? mList.size() : 0;
    }

    @Override
    public PromotionList getItem(int position) {
        return mList != null ? mList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final PromotionList item = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_promotion, null);
        }
        //客户名称
        String customerName = item.getCustomerName();
        TextView promotion_item_customer_name = ViewHolderUtil.get(convertView, R.id.promotion_item_customer_name);
        promotion_item_customer_name.setText(customerName);
        //产品名
        String productName = item.getProductName();
        TextView promotion_item_product_formal_name = ViewHolderUtil.get(convertView, R.id.promotion_item_product_formal_name);
        promotion_item_product_formal_name.setText(productName);
        //分类名
        String categoryName = item.getCategoryName();
        TextView promotion_item_product_common_name = ViewHolderUtil.get(convertView, R.id.promotion_item_product_common_name);
        promotion_item_product_common_name.setText(categoryName);
        //剂型
        String dosageForm = item.getDosageForm();
        TextView promotion_item_dosage_form = ViewHolderUtil.get(convertView, R.id.promotion_item_dosage_form);
        promotion_item_dosage_form.setText("剂型：" + dosageForm);
        //规格
        String spec = item.getSpec();
        TextView promotion_item_spec = ViewHolderUtil.get(convertView, R.id.promotion_item_spec);
        promotion_item_spec.setText("规格：" + spec);
        //协议单号
        String serialNo = item.getSerialNo();
        TextView promotion_item_serial_no = ViewHolderUtil.get(convertView, R.id.promotion_item_serial_no);
        promotion_item_serial_no.setText("协议单号：" + serialNo);
        //推广费
        double totalAmount = item.getTotalAmount();
        TextView promotion_item_total_amount = ViewHolderUtil.get(convertView, R.id.promotion_item_total_amount);
        Spanned amount = Html.fromHtml("总奖励：<font color=\"#d24141\">" + FormatUtils.RMBFormat(totalAmount) + "</font>元");
        promotion_item_total_amount.setText(amount);


        //记录点击状态，方便清空点击状态
        final CheckBox checkBox = ViewHolderUtil.get(convertView, R.id.promotion_item_check);
        checkBox.setChecked(item.isChecked());
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.setChecked(checkBox.isChecked());
                if (mListener != null) {
                    mListener.onCheckedChanged(position, checkBox.isChecked());
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
