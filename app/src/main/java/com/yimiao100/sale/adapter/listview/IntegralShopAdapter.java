package com.yimiao100.sale.adapter.listview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.yimiao100.sale.R;
import com.yimiao100.sale.bean.Category;
import com.yimiao100.sale.utils.DensityUtil;
import com.yimiao100.sale.utils.ScreenUtil;
import com.yimiao100.sale.utils.ViewHolderUtil;

import java.util.ArrayList;

/**
 * 积分商城
 * Created by 亿苗通 on 2016/11/10.
 */
public class IntegralShopAdapter extends BaseAdapter {

    private final ArrayList<Category> mList;

    public IntegralShopAdapter(ArrayList<Category> categoryList) {
        mList = categoryList;
    }

    @Override
    public int getCount() {
        return mList != null ? mList.size() : 0;
    }

    @Override
    public Category getItem(int position) {
        return mList != null ? mList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Category category = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_integral_shop, null);
        }
        ImageView type = ViewHolderUtil.get(convertView, R.id.integral_shop_logo);
        Picasso.with(parent.getContext()).load(category.getImageUrl())
                .placeholder(R.mipmap.ico_default_long_picture)
                .resize(ScreenUtil.getScreenWidth(parent.getContext()), DensityUtil.dp2px(parent.getContext(), 115))
                .into(type);
        return convertView;
    }
}
