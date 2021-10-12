package com.svnkit;

import com.Util.CMD;
import com.Util.Constant;
import com.Util.Log;
import com.Util.Util;

/**
 * SVN辅助工具类
 */
public class SVNCmdHelper {
    /**
     * 查看log
     *
     * @param path
     */
    public static void log(String path) {
        runSVNCMD(path, Constant.CMD_SVN_LOG);
    }

    /**
     * 更新目录
     *
     * @param path
     */
    public static void update(String path) {
        runSVNCMD(path, Constant.CMD_SVN_UPDATE);
    }

    /**
     * 提交修改
     *
     * @param path
     */
    public static void commit(String path) {
        runSVNCMD(path, Constant.CMD_SVN_COMMIT);
    }

    /**
     * 查看修改
     *
     * @param path
     */
    public static void modify(String path) {
        runSVNCMD(path, Constant.CMD_SVN_MODIFY);
    }

    /**
     * 查看对应仓库
     *
     * @param path
     */
    public static void repository(String path) {
        runSVNCMD(path, Constant.CMD_SVN_REPOBROWSER);
    }

    /**
     * 合并分支
     *
     * @param path
     */
    public static void merge(String path) {
        runSVNCMD(path, Constant.CMD_SVN_MERGE);
    }

    /**
     * 清理目录
     *
     * @param path
     */
    public static void clean(String path) {
        runSVNCMD(path, Constant.CMD_SVN_CLEANUP);
    }

    /**
     * 回滚目录
     *
     * @param path
     */
    public static void revert(String path) {
        runSVNCMD(path, Constant.CMD_SVN_REVERT);
    }

    public static void runSVNCMD(String path, String svnKey) {
        if (Util.isStringEmpty(path.trim())) {
            Log.log("输入svn地址为空");
            return;
        }
        if (!svnKey.equals(Constant.CMD_SVN_LOG) && path.trim().startsWith("svn")) {
            Log.log("这个是个svn目录无法操作");
            return;
        }
        String cmd = Constant.PATH_SVN + " " + svnKey + path.trim() + Constant.CMD_SVN_CLOSEONEND0;
        CMD.runCMD(cmd);
    }
}
