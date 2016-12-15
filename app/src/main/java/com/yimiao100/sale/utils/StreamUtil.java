package com.yimiao100.sale.utils;

import java.io.Closeable;
import java.io.IOException;

/**
 * 管理I/O流工具类
 * Created by 亿苗通 on 2016/7/28.
 */
public class StreamUtil {
    private StreamUtil(){
        /* 禁止实例化*/
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 关闭I/O流
     * @param stream
     */
    public static void endStream(Closeable stream){
        if (stream != null){
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
