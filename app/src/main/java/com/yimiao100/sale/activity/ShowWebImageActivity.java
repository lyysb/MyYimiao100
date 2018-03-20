package com.yimiao100.sale.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.yimiao100.sale.R;
import com.yimiao100.sale.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * 显示图片
 */
public class ShowWebImageActivity extends BaseActivity {

    @BindView(R.id.web_image_photo)
    PhotoView mWebImagePhoto;

    public static void start(Context context, String url) {
        Intent intent = new Intent(context, ShowWebImageActivity.class);
        intent.putExtra("image", url);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_web_image);
        ButterKnife.bind(this);

        String imageUrl = getIntent().getStringExtra("image");
        final PhotoViewAttacher attacher = new PhotoViewAttacher(mWebImagePhoto);
        // 设置最大放大级别为10
        attacher.setMaximumScale(10);
        if (imageUrl.isEmpty()) {
            Picasso.with(this)
                    .load(R.mipmap.ico_test)
                    .into(mWebImagePhoto, new Callback() {
                        @Override
                        public void onSuccess() {
                            attacher.update();
                        }

                        @Override
                        public void onError() {
                        }
                    });
        } else {
            Picasso.with(this)
                    .load(imageUrl)
                    .into(mWebImagePhoto, new Callback() {
                        @Override
                        public void onSuccess() {
                            attacher.update();
                        }

                        @Override
                        public void onError() {
                        }
                    });
        }
    }
}
