package com.Util;

import java.util.ArrayList;
import java.util.List;

public class FileUtils {
    /**
     * 判断String是否是空
     * @param agr
     * @return
     */
    public static boolean stringEmpty(String agr) {
        if (agr == null || agr == "" || agr.length() <= 0) {
            return true;
        }
        return false;
    }

    /**
     * 获取字符串中的数字配置
     * @param agr
     * @param key
     * @return
     */
    public static int getLineStrInt(String agr, String key) {
        if (stringEmpty(agr) || stringEmpty(key) || !agr.contains(key)) {
            return -1;
        }
        int able = -1;
        try {
            able = Integer.parseInt((agr.substring(agr.indexOf(key) + key.length())).trim());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return able;
    }

    /**
     * 获取字符串中配置的int集合
     * @param agr
     * @param key
     * @return
     */
    public static int[] getLineStrInts(String agr, String key) {
        if (stringEmpty(agr) || stringEmpty(key) || !agr.contains(key)) {
            return null;
        }
        int[] able = null;
        try {
            String intsStr = agr.substring(agr.indexOf(key) + key.length());
            String[] ints = (intsStr.substring(1, intsStr.length() - 1)).split(",");
            if (ints.length <= 0) {
                return able;
            }
            able = new int[ints.length];
            for (int index = 0; index < ints.length; index++) {
                able[index] = Integer.parseInt(ints[index].trim());
            }
        } catch (Exception e) {
            able = null;
            e.printStackTrace();
        }
        return able;
    }

    /**
     * 获取字符串中配置的int集合
     * @param agr
     * @param key
     * @return
     */
    public static List<Integer> getLineStrListInts(String agr, String key) {
        if (stringEmpty(agr) || stringEmpty(key) || !agr.contains(key)) {
            return null;
        }
        List<Integer> able = new ArrayList<>();
        able.clear();
        try {
            String intsStr = agr.substring(agr.indexOf(key) + key.length());
            String[] ints = (intsStr.substring(1, intsStr.length() - 1)).split(",");
            if (ints.length <= 0) {
                return null;
            }
            for (int index = 0; index < ints.length; index++) {
                if (!FileUtils.stringEmpty(ints[index].trim())) {
                    able.add(Integer.parseInt(ints[index].trim()));
                }
            }
        } catch (Exception e) {
            able = null;
            e.printStackTrace();
        }
        return able;
    }

    /**
     * 获取字符串
     * @param agr
     * @param key
     * @return
     */
    public static String getLineStrString(String agr, String key) {
        if (stringEmpty(agr) || stringEmpty(key) || !agr.contains(key)) {
            return null;
        }
        String mStr = null;
        try {
            mStr = agr.substring(agr.indexOf(key) + key.length()).trim();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mStr;
    }
    /**
     * 获取字符串中字符集合
     * @param agr
     * @param key
     * @return
     */
    public static String[] getLineStrStrings(String agr, String key) {
        if (stringEmpty(agr) || stringEmpty(key) || !agr.contains(key)) {
            return null;
        }
        String[] mStrs = null;
        try {
            String str = agr.substring(agr.indexOf(key) + key.length());
            mStrs = (str.substring(1, str.length() - 1)).split(",");
        } catch (Exception e) {
            mStrs = null;
            e.printStackTrace();
        }
        return mStrs;
    }

    /**
     * 获取字符串中字符集合
     * @param agr
     * @param key
     * @return
     */
    public static List<String> getLineStrListStrings(String agr, String key) {
        if (stringEmpty(agr) || stringEmpty(key) || !agr.contains(key)) {
            return null;
        }
        List<String> mStrList = new ArrayList<>();
        mStrList.clear();
        try {
            String str = agr.substring(agr.indexOf(key) + key.length());
            String[] mStr = (str.substring(1, str.length() - 1)).split(",");
            if (mStr == null || mStr.length <= 0) {
                return null;
            }
            for (int i = 0; i < mStr.length; i++) {
                mStrList.add(mStr[i]);
            }
        } catch (Exception e) {
            mStrList = null;
            e.printStackTrace();
        }
        return mStrList;
    }
}
