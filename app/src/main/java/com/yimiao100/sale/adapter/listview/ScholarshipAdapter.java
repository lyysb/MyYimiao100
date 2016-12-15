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
import com.yimiao100.sale.bean.ExamInfo;
import com.yimiao100.sale.utils.FormatUtils;
import com.yimiao100.sale.utils.ViewHolderUtil;

import java.util.ArrayList;

/**
 * 奖学金提现Adapter
 * Created by 亿苗通 on 2016/9/9.
 */
public class ScholarshipAdapter extends BaseAdapter {


    private final ArrayList<ExamInfo> mList;
    private OnCheckedChangeListener mListener;

    public ScholarshipAdapter(ArrayList<ExamInfo> list) {
        mList = list;
    }

    @Override
    public int getCount() {
        return mList != null ? mList.size() : 0;
    }

    @Override
    public ExamInfo getItem(int position) {
        return mList != null ? mList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ExamInfo scholarship = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_scholarship, null);
        }
        //产品名
        String productName = scholarship.getProductName();
        TextView assurance_product_formal_name = ViewHolderUtil.get(convertView, R.id.scholarship_product_name);
        assurance_product_formal_name.setText(productName);
        //分类名
        String categoryName = scholarship.getCategoryName();
        TextView assurance_product_common_name = ViewHolderUtil.get(convertView, R.id.scholarship_category_name);
        assurance_product_common_name.setText(categoryName);
        //剂型
        String dosageForm = scholarship.getDosageForm();
        TextView assurance_dosage_form = ViewHolderUtil.get(convertView, R.id.scholarship_dosage_form);
        assurance_dosage_form.setText("剂型：" + dosageForm);
        //规格
        String spec = scholarship.getSpec();
        TextView assurance_spec = ViewHolderUtil.get(convertView, R.id.scholarship_spec);
        assurance_spec.setText("规格：" + spec);
        //奖学金
        double totalAmount = scholarship.getTotalAmount();
        TextView assurance_total_amount = ViewHolderUtil.get(convertView, R.id.scholarship_total_amount);
        Spanned amount = Html.fromHtml("奖学金：￥<font color=\"#d24141\">" + FormatUtils.MoneyFormat
                (totalAmount) + "</font>元");
        assurance_total_amount.setText(amount);


        //记录点击状态，方便清空点击状态
        CheckBox assurance_check = ViewHolderUtil.get(convertView, R.id.scholarship_check);
        assurance_check.setChecked(scholarship.isChecked());
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
