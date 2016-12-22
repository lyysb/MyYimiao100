package com.yimiao100.sale.callback;

import android.os.Environment;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Response;

/**
 * 仿鸿神CallBack-专用于协议文件下载-定制版
 * Created by Michel on 2016/12/22.
 */

public abstract class ProtocolFileCallBack extends Callback<File> {

    public ProtocolFileCallBack() {

    }

    @Override
    public File parseNetworkResponse(Response response, int id) throws Exception {
        return saveFile(response,id);
    }
    public File saveFile(Response response,final int id) throws IOException
    {

        String fileDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        String header = response.header("Content-Disposition");
        String fileName = header.substring(header.lastIndexOf("=") + 1);
        InputStream is = null;
        byte[] buf = new byte[2048];
        int len = 0;
        FileOutputStream fos = null;
        try
        {
            is = response.body().byteStream();
            final long total = response.body().contentLength();

            long sum = 0;

            File dir = new File(fileDir);
            if (!dir.exists())
            {
                dir.mkdirs();
            }
            File file = new File(dir, fileName);
            fos = new FileOutputStream(file);
            while ((len = is.read(buf)) != -1)
            {
                sum += len;
                fos.write(buf, 0, len);
                final long finalSum = sum;
                OkHttpUtils.getInstance().getDelivery().execute(new Runnable()
                {
                    @Override
                    public void run()
                    {

                        inProgress(finalSum * 1.0f / total,total,id);
                    }
                });
            }
            fos.flush();

            return file;

        } finally
        {
            try
            {
                response.body().close();
                if (is != null) is.close();
            } catch (IOException e)
            {
            }
            try
            {
                if (fos != null) fos.close();
            } catch (IOException e)
            {
            }

        }
    }

}
