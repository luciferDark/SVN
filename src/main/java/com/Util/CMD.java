package com.Util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 运行CMD命令
 */
public class CMD {

    public static void runCMD(String cmd) {
        if (Util.isStringEmpty(cmd)) {
            Log.log("cmd is empty");
            return;
        }
        try {
            Log.log("runCMD: " + cmd);
            Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void runCMDWithDir(String cmd, String dir) {
        if (Util.isStringEmpty(cmd) || Util.isStringEmpty(dir)) {
            Log.log("cmd or dir is empty");
            return;
        }
        try {
            Runtime.getRuntime().exec(cmd, null, new File(dir));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void runCMDByCmdPrefixWithSwithDir(String cmd, String fileDir) {
        if (Util.isStringEmpty(fileDir)) {
            Log.log("cmd is empty");
            return;
        }
        String cmdPrefix = "cmd /c start ";
        String cmdStr = cmdPrefix + cmd;
        Log.log("runCMD: " + cmdStr + "\t fileDir:" + fileDir);
        runCMDWithDir(cmdStr, fileDir);
    }

    public static void runCMDByCmdPrefix(String cmd) {
        if (Util.isStringEmpty(cmd)) {
            Log.log("cmd is empty");
            return;
        }
        String cmdPrefix = "cmd /c start ";
        Log.log("runCMD: " + cmdPrefix + cmd);
        runCMD(cmdPrefix + cmd);
    }

    /**
     * 使用权限运行CMD命令
     *
     * @param cmd
     */
    public static void runCMDWithPermission(String cmd) {
        if (Util.isStringEmpty(cmd)) {
            Log.log("cmd is empty");
            return;
        }
        try {
            Log.log("runCMDWithPermission: " + cmd);
            Runtime.getRuntime().exec("cmd /c " + cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 使用权限获取CMD命令并获取返回结果
     *
     * @param cmd
     * @return
     */
    public static String runCMDWithPermissionGetResult2(String cmd) {
        if (Util.isStringEmpty(cmd)) {
            Log.log("cmd is empty");
            return "";
        }
        StringBuffer result = new StringBuffer();
        ProcessBuilder builder = null;
        Process process = null;
        BufferedReader br = null;
        try {
            Log.log("runCMDWithPermissionGetResult2: " + cmd);
            builder = new ProcessBuilder("cmd", "/c", cmd);
            process = builder.start();
            InputStream in = process.getInputStream();
            br = new BufferedReader(new InputStreamReader(in, "GBK"));
            String tmp = null;
            while ((tmp = br.readLine()) != null) {
                result.append(tmp).append("\n");
            }
            in.close();
            in = null;
//            Runtime.getRuntime().exec("cmd /c " + cmd);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                    br = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (process != null) {
                process.destroy();
                process = null;
            }

        }
        return result.toString();
    }

    /**
     * 使用权限获取CMD命令并获取返回结果
     *
     * @param cmd
     * @return
     */
    public static String runCMDWithPermissionGetResult(String cmd) {
        if (Util.isStringEmpty(cmd)) {
            Log.log("cmd is empty");
            return "";
        }
        StringBuffer result = new StringBuffer();
        Process process = null;
        try {
            Log.log("runCMDWithPermissionGetResult: " + cmd);
            process = Runtime.getRuntime().exec("cmd /C /D /E " + cmd);
            InputStream in = process.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "GBK"));
            String tmp = null;
            while ((tmp = br.readLine()) != null) {
                result.append(tmp).append("\n");
            }
            in.close();
            in = null;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (process != null) {
                process.destroy();
                process = null;
            }
        }
        return result.toString();
    }

    /**
     * 运行CMD命令并获取返回结果
     *
     * @param cmd
     * @return
     */
    public static String runCMDWithResult(String cmd) {
        if (Util.isStringEmpty(cmd)) {
            Log.log("cmd is empty");
            return "";
        }
        String result = "";
        Process process = null;
        try {
            Log.log("runCMDWithResult: " + cmd);
            process = Runtime.getRuntime().exec(cmd);
            ByteArrayOutputStream resultOutStream = new ByteArrayOutputStream();

            InputStream errorInStream = new BufferedInputStream(process.getErrorStream());
            InputStream processInStream = new BufferedInputStream(process.getInputStream());
            int num = 0;
            byte[] bs = new byte[1024 * 3];
            while ((num = errorInStream.read(bs)) != -1) {
                resultOutStream.write(bs, 0, num);
            }
            while ((num = processInStream.read(bs)) != -1) {
                resultOutStream.write(bs, 0, num);
            }
            result = new String(resultOutStream.toByteArray(), "GBK");
            errorInStream.close();
            errorInStream = null;
            processInStream.close();
            processInStream = null;
            resultOutStream.close();
            resultOutStream = null;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (process != null) {
                process.destroy();
                process = null;
            }
        }
        return result;
    }

    /**
     * 运行CMD命令并获取返回结果
     *
     * @param cmd
     * @return
     */
    public static String runCMDWithResult2(String cmd) {
        if (Util.isStringEmpty(cmd)) {
            Log.log("cmd is empty");
            return "";
        }
        StringBuffer result = new StringBuffer();
        Process process = null;
        try {
            Log.log("runCMDWithResult2: " + cmd);
            process = Runtime.getRuntime().exec(cmd);
            InputStream in = process.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "GBK"));
            String tmp = null;
            while ((tmp = br.readLine()) != null) {
                result.append(tmp).append("\n");
            }
            in.close();
            in = null;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (process != null) {
                process.destroy();
                process = null;
            }
        }
        return result.toString();
    }


    public static List<String> runCMDWithResultWithFileter(String cmd, List<String> filter) {
        if (Util.isStringEmpty(cmd)) {
            Log.log("cmd is empty");
            return null;
        }
        List<String> result = new ArrayList<String>();
        Process process = null;
        try {
            Log.log("runCMDWithResult2: " + cmd);
            process = Runtime.getRuntime().exec(cmd);
            InputStream in = process.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "GBK"));
            String tmp = null;
            boolean container = false;
            while ((tmp = br.readLine()) != null) {
                for (String item : filter) {
                    if (tmp.contains(item.trim())) {
                        container = true;
                        break;
                    }
                }
                if (container) {
                    result.add(tmp);
                    container = false;
                }
            }
            in.close();
            in = null;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (process != null) {
                process.destroy();
                process = null;
            }
        }
        return result;
    }
}
