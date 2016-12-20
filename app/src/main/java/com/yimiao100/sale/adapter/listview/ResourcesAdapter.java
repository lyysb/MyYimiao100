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
 * 资源列表Adapter
 * Created by 亿苗通 on 2016/8/17.
 */
public class ResourcesAdapter extends BaseAdapter{

    private Context mContext;
    private List<ResourceListBean> mList;

    public ResourcesAdapter(Context context, List<ResourceListBean> resourcesList){
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
        if (convertView == null){
            convertView = View.inflate(mContext, R.layout.item_resources, null);
        }
        //厂家名称
        TextView resource_vendor_name = ViewHolderUtil.get(convertView, R.id.resource_vendor_name);
        resource_vendor_name.setText(resource.getVendorName());
        //产品名-分类名
        TextView resource_product_commonName = ViewHolderUtil.get(convertView, R.id.resource_product_commonName);
        resource_product_commonName.setText("产品：" + resource.getCategoryName());
        //剂型
        TextView resource_dosage_form = ViewHolderUtil.get(convertView, R.id.resource_dosage_form);
        resource_dosage_form.setText("剂型：" + resource.getDosageForm());
        //规格
        TextView resource_spec = ViewHolderUtil.get(convertView, R.id.resource_spec);
        resource_spec.setText("规格：" + resource.getSpec());
        //区域
        TextView resource_region = ViewHolderUtil.get(convertView, R.id.resource_region);
        resource_region.setText("区域：" + resource.getProvinceName() + "\t" + resource.getCityName()
                    + "\t" + resource.getAreaName());
        //更新时间
        TextView resource_updated_at = ViewHolderUtil.get(convertView, R.id.resource_updated_at);
        long updatedAt = resource.getUpdatedAt();
        resource_updated_at.setText(TimeUtil.timeStamp2Date(updatedAt + "", "yyyy年MM月dd日"));

        //厂家Logo
        ImageView resource_vendor_logo = ViewHolderUtil.get(convertView, R.id.resource_vendor_logo);
        if (resource.getVendorLogoImageUrl() != null && resource.getVendorLogoImageUrl().length() != 0) {
            Picasso.with(mContext).load(resource.getVendorLogoImageUrl()).into(resource_vendor_logo);
        }




        return convertView;
    }
}
