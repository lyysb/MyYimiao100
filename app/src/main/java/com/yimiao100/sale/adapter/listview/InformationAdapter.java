package com.yimiao100.sale.adapter.listview;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yimiao100.sale.R;
import com.yimiao100.sale.bean.ImageListBean;
import com.yimiao100.sale.bean.PagedListBean;
import com.yimiao100.sale.glide.ImageLoad;
import com.yimiao100.sale.utils.TimeUtil;
import com.yimiao100.sale.utils.ViewHolderUtil;

import java.util.List;

/**
 * 资讯列表的Adapter
 * Created by 亿苗通 on 2016/8/8.
 */
public class InformationAdapter extends BaseAdapter {

    private List<PagedListBean> pagedList;
    private Context context;
    private ImageView mType1_image;
    private ImageView mType2_image1;
    private ImageView mType2_image2;
    private ImageView mType2_image3;
    private ImageView mType3_image;
    final int TYPE_COUNT = 3;   //种类
    final int TYPE_1 = 1;   //图文混排
    final int TYPE_2 = 2;   //多图
    final int TYPE_3 = 0;   //大图



    public InformationAdapter(Context context, List<PagedListBean> pagedList) {
        this.context = context;
        this.pagedList = pagedList;
    }

    @Override
    public int getItemViewType(int position) {
        int layoutType = pagedList.get(position).getLayoutType();
        if (layoutType == 1)
            return TYPE_1;
        else if (layoutType == 2)
            return TYPE_2;
        else
            return TYPE_3;
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_COUNT;
    }

    @Override
    public int getCount() {
        return pagedList != null ? pagedList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return pagedList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //获得每个新闻条目类
        PagedListBean pagedListBean = pagedList.get(position);
        //获取新闻种类
        int layoutType = getItemViewType(position);
        switch (layoutType){
            case TYPE_1:
                if (convertView == null){
                    convertView = View.inflate(context, R.layout.item_information_1, null);
                }
                TextView type1_title = ViewHolderUtil.get(convertView, R.id.type1_title);
                TextView type1_abstract = ViewHolderUtil.get(convertView, R.id.type1_abstract);
                TextView type1_source = ViewHolderUtil.get(convertView, R.id.type1_source);
                TextView type1_commentNumber = ViewHolderUtil.get(convertView, R.id.type1_commentNumber);
                TextView type1_publishAt = ViewHolderUtil.get(convertView, R.id.type1_publishAt);

                type1_title.setText(pagedListBean.getTitle());
                type1_title.setTextColor(pagedListBean.isReaded() ? Color.parseColor("#999999") : Color.parseColor("#333333"));
                type1_abstract.setText(pagedListBean.getNewsAbstract());
                type1_source.setText(pagedListBean.getNewsSource());
                type1_commentNumber.setText("评论（" + pagedListBean.getCommentNumber() + "）");
                //发布时间
                long publishAt = pagedListBean.getPublishAt();
                type1_publishAt.setText(TimeUtil.time2Date(publishAt, "yyyy年MM月dd日"));

                //加载图片
                mType1_image = ViewHolderUtil.get(convertView, R.id.type1_image);
                List<ImageListBean> imageList1 = pagedListBean.getImageList();
                if (imageList1.size() > 0) {
                    String imageUrl_1 = imageList1.get(0).getImageUrl();
                    ImageLoad.loadShortUrl(context, imageUrl_1 + "?imageMogr2/thumbnail/270x225", mType1_image);
                }
                ImageView type1_top = ViewHolderUtil.get(convertView, R.id.type1_top);
                type1_top.setVisibility(pagedListBean.getIsTop() == 1 ? View.VISIBLE : View.GONE);
                break;
            case TYPE_2:
                if (convertView == null){
                    convertView = View.inflate(context, R.layout.item_information_2, null);
                }
                TextView type2_title = ViewHolderUtil.get(convertView, R.id.type2_title);
                TextView type2_source = ViewHolderUtil.get(convertView, R.id.type2_source);
                TextView type2_commentNumber = ViewHolderUtil.get(convertView, R.id.type2_commentNumber);
                TextView type2_publishAt = ViewHolderUtil.get(convertView, R.id.type2_publishAt);

                type2_title.setText(pagedListBean.getTitle());
                type2_title.setTextColor(pagedListBean.isReaded() ? Color.parseColor("#999999") : Color.parseColor("#333333"));

                type2_source.setText(pagedListBean.getNewsSource());
                type2_commentNumber.setText("评论（" + pagedListBean.getCommentNumber() + "）");
                type2_publishAt.setText(TimeUtil.time2Date(pagedListBean.getPublishAt(), "yyyy年MM月dd日"));

                mType2_image1 = ViewHolderUtil.get(convertView, R.id.type2_image1);
                mType2_image2 = ViewHolderUtil.get(convertView, R.id.type2_image2);
                mType2_image3 = ViewHolderUtil.get(convertView, R.id.type2_image3);

                List<ImageListBean> imageList = pagedListBean.getImageList();
                if (imageList.size() == 1){
                    String imageUrl_2_1 = pagedListBean.getImageList().get(0).getImageUrl();
                    ImageLoad.loadShortUrl(context, imageUrl_2_1, mType2_image1);
                }else if (imageList.size() == 2){
                    String imageUrl_2_1 = pagedListBean.getImageList().get(0).getImageUrl();
                    String imageUrl_2_2 = pagedListBean.getImageList().get(1).getImageUrl();
                    ImageLoad.loadShortUrl(context, imageUrl_2_1, mType2_image1);
                    ImageLoad.loadShortUrl(context, imageUrl_2_2, mType2_image2);
                }else if (imageList.size() >= 3){
                    String imageUrl_2_1 = pagedListBean.getImageList().get(0).getImageUrl();
                    String imageUrl_2_2 = pagedListBean.getImageList().get(1).getImageUrl();
                    String imageUrl_2_3 = pagedListBean.getImageList().get(2).getImageUrl();
                    ImageLoad.loadShortUrl(context, imageUrl_2_1, mType2_image1);
                    ImageLoad.loadShortUrl(context, imageUrl_2_2, mType2_image2);
                    ImageLoad.loadShortUrl(context, imageUrl_2_3, mType2_image3);
                }
                ImageView type2_top = ViewHolderUtil.get(convertView, R.id.type2_top);
                type2_top.setVisibility(pagedListBean.getIsTop() == 1 ? View.VISIBLE : View.GONE);
                break;
            case TYPE_3:
                if (convertView == null){
                    convertView = View.inflate(context, R.layout.item_information_3, null);
                }
                TextView type3_title = ViewHolderUtil.get(convertView, R.id.type3_title);
                type3_title.setText(pagedListBean.getTitle());
                type3_title.setTextColor(pagedListBean.isReaded() ? Color.parseColor("#999999") : Color.parseColor("#333333"));

                TextView type3_source = ViewHolderUtil.get(convertView, R.id.type3_source);
                TextView type3_commentNumber = ViewHolderUtil.get(convertView, R.id.type3_commentNumber);
                TextView type3_publishAt = ViewHolderUtil.get(convertView, R.id.type3_publishAt);

                type3_source.setText(pagedListBean.getNewsSource());
                type3_commentNumber.setText("评论（" + pagedListBean.getCommentNumber() + "）");
                type3_publishAt.setText(TimeUtil.time2Date(pagedListBean.getPublishAt(), "yyyy年MM月dd日"));


                mType3_image = ViewHolderUtil.get(convertView, R.id.type3_image);
                List<ImageListBean> imageList2 = pagedListBean.getImageList();
                if (imageList2.size() > 0) {
                    String imageUrl_3 = imageList2.get(0).getImageUrl();
                    ImageLoad.loadLongUrl(context, imageUrl_3, mType3_image);
                }
                ImageView type3_top = ViewHolderUtil.get(convertView, R.id.type3_top);
                type3_top.setVisibility(pagedListBean.getIsTop() == 1 ? View.VISIBLE : View.GONE);
                break;
        }
        return convertView;
    }
}
