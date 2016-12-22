package com.yimiao100.sale.adapter.listview;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yimiao100.sale.R;
import com.yimiao100.sale.bean.ResourceListBean;
import com.yimiao100.sale.utils.TimeUtil;
import com.yimiao100.sale.utils.ViewHolderUtil;

import java.util.List;

/**
 * 我的业务-Adapter
 * Created by 亿苗通 on 2016/8/17.
 */
public class OrderAdapter extends BaseAdapter {

    private Context mContext;
    private List<ResourceListBean> mList;

    public OrderAdapter(Context context, List<ResourceListBean> resourcesList) {
        mContext = context;
        mList = resourcesList;
    }

    @Override
    public int getCount() {
        return mList != null ? mList.size() : 0;
    }

    @Override
    public ResourceListBean getItem(int position) {
        return mList != null ? mList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return mList != null ? position : 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ResourceListBean resource = getItem(position);
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_order, null);
        }
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
            Picasso.with(mContext).load(resource.getVendorLogoImageUrl()).into(order_vendor_logo);
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
        switch (orderStatus) {
            case "unpaid":
                //最初状态-未支付
                order_status.setTextColor(mContext.getResources().getColor(R.color.colorOrigin));
                break;
            case "bidding":
                //第一状态-竞标中
                order_status.setTextColor(mContext.getResources().getColor(R.color.colorOrigin));
            case "defaulted":
                //错误状态-已违约
                order_status.setTextColor(mContext.getResources().getColor(R.color.colorRed));
                break;
            case "to_be_signed":
                //第三状态-待签约
                order_status.setTextColor(mContext.getResources().getColor(R.color.color666));
                break;
            case "already_signed":
                //第四状态-已签约
                order_status.setTextColor(mContext.getResources().getColor(R.color.colorMain));
                break;
            default:
                //其他界面--审核中|未通过
                order_status.setTextColor(mContext.getResources().getColor(R.color.color999));
                break;
        }
        return convertView;
    }
}
