package com.yimiao100.sale.adapter.listview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yimiao100.sale.R;
import com.yimiao100.sale.bean.Goods;
import com.yimiao100.sale.utils.FormatUtils;
import com.yimiao100.sale.utils.ViewHolderUtil;

import java.util.ArrayList;

/**
 * 商品列表Adapter
 * Created by 亿苗通 on 2016/11/10.
 */
public class GoodsListAdapter extends BaseAdapter {

    private final ArrayList<Goods> mList;

    public GoodsListAdapter(ArrayList<Goods> goodsList) {
        mList = goodsList;
    }

    @Override
    public int getCount() {
        return mList != null ? mList.size() : 0;
    }

    @Override
    public Goods getItem(int position) {
        return mList != null ? mList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_goods, null);
        }
        Goods goods = getItem(position);
        ImageView goodsImage = ViewHolderUtil.get(convertView, R.id.goods_item_image);
        TextView goodsName = ViewHolderUtil.get(convertView, R.id.goods_item_name);
        TextView goodsIntegral = ViewHolderUtil.get(convertView, R.id.goods_item_integral_value);
        TextView goodsPrice = ViewHolderUtil.get(convertView, R.id.goods_item_price);

        Picasso.with(parent.getContext()).load(goods.getImageUrl()).into(goodsImage);
        goodsName.setText(goods.getGoodsName());
        goodsIntegral.setText(goods.getIntegralValue() + "");
        goodsPrice.setText("市场参考价格：" + FormatUtils.RMBFormat(goods.getUnitPrice()));
        return convertView;
    }
}
