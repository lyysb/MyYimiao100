package com.yimiao100.sale.adapter.listview;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yimiao100.sale.R;
import com.yimiao100.sale.bean.ResourceListBean;
import com.yimiao100.sale.glide.ImageLoad;
import com.yimiao100.sale.utils.TimeUtil;
import com.yimiao100.sale.utils.ViewHolderUtil;

import java.util.List;

/**
 * 我的业务-Adapter
 * Created by 亿苗通 on 2016/8/17.
 */
public class BusinessResAdapter extends BaseAdapter {

    private boolean isShowMultiSelect;
    private List<ResourceListBean> list;
    private OnCheckBoxClickListener listener;

    public BusinessResAdapter(List<ResourceListBean> list, boolean isShowMultiSelect) {
        this.list = list;
        this.isShowMultiSelect = isShowMultiSelect;
    }

    @Override
    public int getCount() {
        return list != null ? list.size() : 0;
    }

    @Override
    public ResourceListBean getItem(int position) {
        return list != null ? list.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return list != null ? position : 0;
    }

    public interface OnCheckBoxClickListener {
        void onCheckBoxClick(CheckBox checkBox, int position);
    }

    public void setOnCheckBoxClickListener(OnCheckBoxClickListener listener) {
        this.listener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ResourceListBean resource = getItem(position);
        Context context = parent.getContext();
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_res_business, null);
        }
        // 多选框
        CheckBox checkBox = ViewHolderUtil.get(convertView, R.id.business_select);
        checkBox.setChecked(resource.isChecked());
        checkBox.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCheckBoxClick(checkBox, position);
            }
        });
//        checkBox.setVisibility(isShowMultiSelect ? View.VISIBLE : View.GONE);--下部有判断
        //厂家名称
        TextView order_vendor_name = ViewHolderUtil.get(convertView, R.id.order_vendor_name);
        order_vendor_name.setText(resource.getVendorName());
        //产品名-分类名
        TextView order_product_commonName = ViewHolderUtil.get(convertView, R.id.order_product_commonName);
        order_product_commonName.setText("产品：" + resource.getCategoryName());
        //剂型
        TextView order_dosage_form = ViewHolderUtil.get(convertView, R.id.order_dosage_form);
        order_dosage_form.setText("剂型：" + resource.getDosageForm());
        //规格
        TextView order_spec = ViewHolderUtil.get(convertView, R.id.order_spec);
        order_spec.setText("规格：" + resource.getSpec());
        //区域
        TextView order_region = ViewHolderUtil.get(convertView, R.id.order_region);
        order_region.setText("区域：" + resource.getProvinceName() + "\t" + resource.getCityName()
                + "\t" + resource.getAreaName());
        //更新时间
        TextView order_updated_at = ViewHolderUtil.get(convertView, R.id.order_updated_at);
        long updatedAt = resource.getUpdatedAt();
        order_updated_at.setText(TimeUtil.timeStamp2Date(updatedAt + "", "yyyy.MM.dd"));

        //厂家Logo
        ImageView order_vendor_logo = ViewHolderUtil.get(convertView, R.id.order_vendor_logo);
        if (resource.getVendorLogoImageUrl() != null && resource.getVendorLogoImageUrl().length() != 0) {
            ImageLoad.loadUrl(context, resource.getVendorLogoImageUrl(), order_vendor_logo);
        }
        // 客户
        TextView order_customer = ViewHolderUtil.get(convertView, R.id.order_customer);
        if (resource.getCustomerName() != null) {
            order_customer.setText("客户：" + resource.getCustomerName());
        }

        //协议单号
        TextView order_serial_no = ViewHolderUtil.get(convertView, R.id.order_serial_no);
        order_serial_no.setText("协议单号：" + resource.getSerialNo());

        //状态值
        TextView order_status = ViewHolderUtil.get(convertView, R.id.order_status);
        order_status.setVisibility(View.VISIBLE);
        //设置状态显示内容
        String orderStatus = resource.getOrderStatus();
        //显示状态名
        String orderStatusName = resource.getOrderStatusName();
        order_status.setText(orderStatusName);
        // 只有未支付状态下的多选才显示
        if (TextUtils.equals(orderStatus, "unpaid") && isShowMultiSelect) {
            checkBox.setVisibility(View.VISIBLE);
        } else {
            checkBox.setVisibility(View.GONE);
        }
        switch (orderStatus) {
            case "unpaid":
                //最初状态-未支付
                order_status.setTextColor(context.getResources().getColor(R.color.colorBrown));
                break;
            case "bidding":
                //第一状态-竞标中
                order_status.setTextColor(context.getResources().getColor(R.color.colorOrigin));
            case "defaulted":
                //错误状态-已违约
                order_status.setTextColor(context.getResources().getColor(R.color.colorRed));
                break;
            case "to_be_signed":
                //第三状态-待签约
                order_status.setTextColor(context.getResources().getColor(R.color.color666));
                break;
            case "already_signed":
                //第四状态-已签约
                order_status.setTextColor(context.getResources().getColor(R.color.colorMain));
                break;
            case "end":
                // 最终状态-已终止
                order_status.setTextColor(context.getResources().getColor(R.color.colorEnd));
                break;
            default:
                //其他界面--审核中|未通过
                order_status.setTextColor(context.getResources().getColor(R.color.color999));
                break;
        }
        return convertView;
    }

    /**
     * 显示多选
     */
    public void showMultiSelect() {
        isShowMultiSelect = true;
        notifyDataSetChanged();
    }

    /**
     * 取消多选
     */
    public void hideMultiSelect() {
        isShowMultiSelect = false;
        notifyDataSetChanged();

    }

}
