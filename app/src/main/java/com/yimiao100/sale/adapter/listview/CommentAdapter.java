package com.yimiao100.sale.adapter.listview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yimiao100.sale.R;
import com.yimiao100.sale.bean.CommentListBean;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.TimeUtil;
import com.yimiao100.sale.utils.ViewHolderUtil;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 资讯详情页-评论列表
 * Created by 亿苗通 on 2016/8/12.
 */
public class CommentAdapter extends BaseAdapter  {
    private Context mContext;
    private List<CommentListBean> mCommentList ;
    private OnScoreClickListener mScoreClickListener;


    public CommentAdapter(Context context, List<CommentListBean> commentList){
        mContext = context;
        mCommentList = commentList;
    }

    @Override
    public int getCount() {
        if (mCommentList != null){
            return mCommentList.size();
        }else {
            return 0;
        }
    }

    @Override
    public CommentListBean getItem(int position) {
        if (mCommentList != null){
            return mCommentList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CommentListBean commentListBean = getItem(position);
        if (convertView == null){
            convertView = View.inflate(mContext, R.layout.item_comment, null);
        }
        TextView comment_user_name = ViewHolderUtil.get(convertView, R.id.comment_user_name);
        TextView comment_content = ViewHolderUtil.get(convertView, R.id.comment_content);
        TextView comment_created_at = ViewHolderUtil.get(convertView, R.id.comment_created_at);
        final TextView comment_user_score = ViewHolderUtil.get(convertView, R.id.comment_user_score);

        CircleImageView comment_user_image = ViewHolderUtil.get(convertView, R.id.comment_user_image);

        //设置评论用户名字
        comment_user_name.setText(commentListBean.getUserName() == null ? "匿名用户" : commentListBean.getUserName());
        //设置评论内容
        comment_content.setText(commentListBean.getCommentContent());
        //设置评论时间
        comment_created_at.setText(TimeUtil.timeStamp2Date(commentListBean.getCreatedAt() + "", "MM-dd HH:mm"));
        //设置评论数
        comment_user_score.setText(commentListBean.getScore() + "");
        //获取评论id
        final int commentId = commentListBean.getId();
        //设置评论点赞的点击事件
        comment_user_score.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mScoreClickListener != null){
                    mScoreClickListener.OnScoreClick(comment_user_score, commentId);
                }
            }
        });
        //根据评分状态设置图标显示
        int userScoreStatus = commentListBean.getUserScoreStatus();
        LogUtil.d(position + "-userScoreStatus-" + userScoreStatus);
        Drawable activationScore = mContext.getResources().getDrawable(R.mipmap.ico_information_activation_zambia);          //已赞图片
        Drawable defaultScore = mContext.getResources().getDrawable(R.mipmap.ico_information_default_zambia);                //未赞图片
        if (userScoreStatus == 0 ){
            comment_user_score.setCompoundDrawablesWithIntrinsicBounds(defaultScore, null, null, null);
        }else {
            comment_user_score.setCompoundDrawablesWithIntrinsicBounds(activationScore,null,null,null);
        }
        //加载评论头像
        String profileImageUrl = commentListBean.getProfileImageUrl();
        if (profileImageUrl == null) {
            Picasso.with(mContext).load(R.mipmap.ico_my_default_avatar).into(comment_user_image);
        } else {
            Picasso.with(mContext).load(profileImageUrl).into(comment_user_image);
        }

        return convertView;
    }
    public interface OnScoreClickListener{
        void OnScoreClick(TextView comment_user_score, int commentId);
    }
    public void setOnScoreClickListener(OnScoreClickListener listener){
        mScoreClickListener = listener;
    }
}
