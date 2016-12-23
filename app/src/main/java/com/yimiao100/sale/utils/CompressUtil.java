package com.yimiao100.sale.utils;

import android.os.Environment;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * zip压缩方法
 * Created by Michel on 2016/12/23.
 */

public class CompressUtil {
    private static final int BUFFER = 2048;
    private CompressUtil() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static File zipANDSave(ArrayList<String> files, String zipFileName) {
        File zipFile = null;
        try {
            zipFile= new File(Environment.getExternalStorageDirectory().getAbsolutePath(),
                    zipFileName);
            FileOutputStream fos = new FileOutputStream(zipFile);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(fos));

            byte[] buf = new byte[BUFFER];
            for (String file : files) {
                File temp = new File(file);
                FileInputStream in = new FileInputStream(temp);
                out.putNextEntry(new ZipEntry(temp.getName()));
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.closeEntry();
                in.close();
            }
            out.close();
            fos.close();
            LogUtil.d("success");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return zipFile;
    }
}
