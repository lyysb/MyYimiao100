package com.yimiao100.sale.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
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
            zipFile= Util.createFile(zipFileName);
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

    /**
     * @param files key-压缩在文件内的名字；val-文件真实路径
     * @param zipFileName
     * @return
     */
    public static File zipANDSave(HashMap<String, String> files, String zipFileName) {
        File zipFile = null;
        try {
            zipFile = Util.createFile(zipFileName);
            FileOutputStream fos = new FileOutputStream(zipFile);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(fos));

            byte[] buf = new byte[BUFFER];
            Iterator<Map.Entry<String, String>> iterator = files.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                // 获取设置的压缩文件内的名字
                String key = entry.getKey();
                // 获取压缩文件内的真实路径
                String value = entry.getValue();
                File temp = new File(value);
                FileInputStream in = new FileInputStream(temp);
                out.putNextEntry(new ZipEntry(key));
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
