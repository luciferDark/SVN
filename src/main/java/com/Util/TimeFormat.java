package com.Util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeFormat {
    public static String FORMAT_YYYYMMDD_HHMMSS_SSSS_CN(Date time){
        return new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒SSSS").format(time);
    }
    public static String FORMAT_YYYYMMDD_HHMMSS(Date time){
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time);
    }
    public static String FORMAT_YYYYMMDD_HHMMSS_SSSS(Date time){
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSSS").format(time);
    }
    public static String FORMAT_MMDD_HHMMSS_SSSS(Date time){
        return new SimpleDateFormat("MM-dd HH:mm:ss:SSSS").format(time);
    }
    public static String FORMAT_HHMMSS_SSSS(Date time){
        return new SimpleDateFormat("HH:mm:ss:SSSS").format(time);
    }
    public static String FORMAT_MMDD_HHMMSS(Date time){
        return new SimpleDateFormat("MM-dd HH:mm:ss").format(time);
    }
}
