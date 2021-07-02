package com.Util;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * log 工具类
 */
public class Log {
    public static void logn() {
        System.out.println();
    }

    public static void log(String... msgs) {
        StringBuffer buffer = new StringBuffer();
        for (String msg : msgs) {
            buffer.append(msg)
                    .append("\t");
        }
        log(buffer.toString().trim());
    }

    public static void log(String msg) {
        String logMsg = TimeFormat.FORMAT_YYYYMMDD_HHMMSS_SSSS_CN(new Date(System.currentTimeMillis())) + ":";
        String msgUTF8 = Util.getStringUtf8(msg);
        logMsg += msgUTF8;
        System.out.println(logMsg);
    }

    public static <T> void logList(List<T> item, String headStr) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("{");
        for (int i = 0; i < item.size(); i++) {
            buffer.append(item.get(i).toString());
            if (i != (item.size() - 1)) {
                buffer.append(",");
            }
        }
        buffer.append("}");
        log("==>" + headStr + ":length" + item.size() + "," + buffer.toString());
    }
}
