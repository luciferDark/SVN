package com.Util;

import java.awt.*;
import java.io.*;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Util {
    /**
     * 判断String是否是空
     *
     * @param str
     * @return
     */
    public static boolean isStringEmpty(String str) {
        if (str == null || str == "" || str.length() <= 0
                || str.trim() == "" || str.trim().length() <= 0) {
            return true;
        }
        return false;
    }

    public static boolean isEmpty(Object object) {
        if (null == object) return true;
        return false;
    }
    /**
     * 时间转换成时间戳
     *
     * @param time
     * @return
     */
    public static long dateToTimestamp(String time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = simpleDateFormat.parse(time);
            long ts = date.getTime() / 1000;
            return ts;
        } catch (ParseException e) {
            return 0;
        }
    }

    /**
     * 时间戳转时间(11位时间戳)
     *
     * @param time
     * @return
     */
    public static String timestampToDate(long time) {
        String dateTime;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        long timeLong = Long.valueOf(time);
        dateTime = simpleDateFormat.format(new Date(timeLong));
        return dateTime;
    }

    /**
     * 获取String的Utf-8格式
     *
     * @param msg
     * @return
     */
    public static String getStringUtf8(String msg) {
        try {
            return new String(msg.getBytes(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * 打开网页链接
     *
     * @param url
     */
    public static void openTheUrl(String url) {
        if (isStringEmpty(url)) {
            Log.log("url is null,pls check");
            return;
        }
        if (!url.startsWith("http://") && !url.startsWith("https://") && !url.startsWith("www.")) {
            Log.log("url is error:" + url);
            return;
        }
        try {
            URI uri = URI.create(url);
            // 获取当前系统桌面扩展
            Desktop dp = Desktop.getDesktop();
            // 判断系统桌面是否支持要执行的功能
            if (dp.isSupported(Desktop.Action.BROWSE)) {
                dp.browse(uri);
                // 获取系统默认浏览器打开链接
            }
        } catch (NullPointerException e1) {
            // 此为uri为空时抛出异常
            e1.printStackTrace();
        } catch (IOException e1) {
            // 此为无法获取系统默认浏览器
            e1.printStackTrace();
        }
    }

}
