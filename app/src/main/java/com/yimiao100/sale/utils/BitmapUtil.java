package com.yimiao100.sale.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.squareup.picasso.Transformation;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by 亿苗通 on 2016/8/10.
 */
public class BitmapUtil {
    private BitmapUtil(){
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 将Bitmap保存在本地
     * @param bitmap
     * @param FileName
     * @return
     */
    public static File setPicToView(Bitmap bitmap, String FileName) {
        for (int i = 0; i < 10; i++) {

        }
        FileOutputStream b = null;
        File file = new File(Util.getApkPath(), FileName);
        try {
            b = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            StreamUtil.endStream(b);
        }
        return file;
    }

    /**
     * 通过Uri获取图片的真实路径
     * @param context
     * @param contentUri
     * @return
     */
    public static String getRealPathFromURI(Context context, Uri contentUri) {
        String res = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = null;
        if (contentUri.getScheme().equals("content")) {//判断uri地址是以什么开头的
            cursor = contentResolver.query(contentUri, null, null, null, null);
        } else {
            cursor = contentResolver.query(getFileUri(contentUri, context), proj, null, null, null);//红色字体判断地址如果以file开头
        }

        if (cursor != null && cursor.moveToFirst()) {
            //这是获取的图片保存在sdcard中的位置
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        if (cursor != null) {
            cursor.close();
        }
        return res;
    }
    private static Uri getFileUri(Uri uri, Context context){
        if (uri.getScheme().equals("file")) {
            String path = uri.getEncodedPath();
            LogUtil.d("path1 is " + path);
            if (path != null) {
                path = Uri.decode(path);
                LogUtil.d("path2 is " + path);
                ContentResolver cr = context.getContentResolver();
                StringBuffer buff = new StringBuffer();
                buff.append("(")
                        .append(MediaStore.Images.ImageColumns.DATA)
                        .append("=")
                        .append("'" + path + "'")
                        .append(")");
                Cursor cur = cr.query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        new String[] { MediaStore.Images.ImageColumns._ID },
                        buff.toString(), null, null);
                int index = 0;
                for (cur.moveToFirst(); !cur.isAfterLast(); cur
                        .moveToNext()) {
                    index = cur.getColumnIndex(MediaStore.Images.ImageColumns._ID);
                    // set _id value
                    index = cur.getInt(index);
                }
                if (index == 0) {
                    //do nothing
                } else {
                    Uri uri_temp = Uri.parse("content://media/external/images/media/" + index);
                    LogUtil.d("uri_temp is " + uri_temp);
                    if (uri_temp != null) {
                        uri = uri_temp;
                    }
                }
            }
        }
        return uri;
    }

    /**
     * 质量压缩（不改变图片尺寸-用于图片上传）
     * @param image
     * @param outPath
     * @param maxSize
     * @throws IOException
     */
    public static void compressAndGenImage(Bitmap image, String outPath, int maxSize) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        // scale
        int options = 100;
        // Store the bitmap into output stream(no compress)
        image.compress(Bitmap.CompressFormat.JPEG, options, os);
        // Compress by loop
        while ( os.toByteArray().length / 1024 > maxSize) {
            // Clean up os
            os.reset();
            // interval 10
            options -= 10;
            image.compress(Bitmap.CompressFormat.JPEG, options, os);
        }
        // Generate compressed image file
        FileOutputStream fos = new FileOutputStream(outPath);
        fos.write(os.toByteArray());
        fos.flush();
        fos.close();
    }
    /**
     * 等比例压缩显示
     * @param file
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static Bitmap decodeSampledBitmapFromFile(File file, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        //避免申请内存
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), options);

        // Calculate inSampleSize
        //计算压缩比例
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(file.getAbsolutePath(), options);
    }
    private static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float)height / (float)reqHeight);
            } else {
                inSampleSize = Math.round((float)width / (float)reqWidth);
            }
        }
        return inSampleSize;
    }

    /**
     * 根据指定imageView的大小 实现图片的缩放
     * @param imageView
     * @return
     */
    @NonNull
    public static Transformation getTransformation(@NonNull final ImageView imageView) {
        return new Transformation() {
            @Override
            public Bitmap transform(Bitmap source) {
                int targetWidth = imageView.getWidth();
                LogUtil.d("source.getHeight()="  + source.getHeight()
                        + "source.getWidth()=" + source.getWidth()
                        + ",targetWidth=" + targetWidth);
                if (source.getWidth() == 0) {
                    return source;
                }

                // 如果图片小于设置的宽度，则返回原图
                if (source.getWidth() < targetWidth) {
                    return source;
                } else {
                    // 如果图片大于设置的宽度，则按照宽度比例来缩放
                    double aspectRatio = (double) source.getHeight() / (double) source.getWidth();
                    int targetHeight = (int) (targetWidth * aspectRatio);
                    if (targetHeight != 0 && targetWidth != 0) {
                        Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
                        if (result != source) {
                            // Same bitmap is returned if sizes are the same
                            source.recycle();
                        }
                        return result;
                    } else {
                        return source;
                    }
                }
            }

            @Override
            public String key() {
                return "transformation" + " desiredWidth";
            }
        };
    }

    /**
     * TODO 未完成
     * @param imageView
     * @return
     */
    public static Transformation getTransformationForAd(final ImageView imageView){
        return new Transformation() {
            @Override
            public Bitmap transform(Bitmap source) {
                int targetWidth = imageView.getWidth();
                LogUtil.d("source.getHeight()=" + source.getHeight()
                        + "source.getWidth()=" + source.getWidth()
                        + ",targetWidth=" + targetWidth);
                //返回原图
                if (source.getWidth() == 0) {
                    return source;
                }
                return null;
            }

            @Override
            public String key() {
                return "transformation" + " desiredWidth";
            }
        };
    }

}
