package com.svnkit;

import com.Util.Log;
import com.svnkit.models.SVNKindBean;
import com.svnkit.models.SVNLogBean;
import org.tmatesoft.svn.core.SVNLogEntryPath;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SvnUtils {
    public static List<SVNKindBean> getPathList(Map<String, SVNLogEntryPath> changedPaths) {
        List<SVNKindBean> result = new ArrayList<>();
        SVNKindBean item = null;
        for (Map.Entry<String, SVNLogEntryPath> entry : changedPaths.entrySet()) {
            item = new SVNKindBean();
            item.setPath(entry.getValue().getPath());
            item.setType(getType(entry.getValue()));
            result.add(item);
        }
        return result;
    }

    private static SVNKindBean.KindType getType(SVNLogEntryPath entryPath) {
        SVNKindBean.KindType result = SVNKindBean.KindType.M;
        char entrytype = entryPath.getType();
        if ("M".equals(String.valueOf(entrytype))) {
            result = SVNKindBean.KindType.M;
        } else if ("A".equals(String.valueOf(entrytype))) {
            result = SVNKindBean.KindType.A;
        } else if ("D".equals(String.valueOf(entrytype))) {
            result = SVNKindBean.KindType.D;
        }
        return result;
    }

    public static List<Long> getVersions(List<SVNLogBean> svnLogBeans) {
        List<Long> result = new ArrayList<>();
        for (SVNLogBean svnLogBean : svnLogBeans) {
            if (!result.contains(svnLogBean.getRevision())) {
                result.add(svnLogBean.getRevision());
            }
        }
        return result;
    }

    public static List<Long> getVersionsByLog(List<SVNLogBean> svnLogBeans, List<String> conditions, List<String> excludeConditions, boolean excludeConfigs) {
        return getVersions(getBeansByLog(svnLogBeans, conditions, excludeConditions, excludeConfigs));
    }

    public static List<SVNLogBean> getBeansByLog(List<SVNLogBean> svnLogBeans, List<String> conditions, List<String> excludeConditions, boolean excludeConfigs) {
        List<SVNLogBean> result = new ArrayList<>();
        String log = "";
        boolean include = false;
        List<SVNLogBean> excludeConfigResult = new ArrayList<>();
        List<SVNLogBean> notExcludeConfigResult = new ArrayList<>();
        List<SVNLogBean> isAllConfigResult = new ArrayList<>();
        List<SVNLogBean> isAllServerConfigResult = new ArrayList<>();

        for (SVNLogBean svnLogBean : svnLogBeans) {
            log = svnLogBean.getMessage();
            // ??????????????????
            if (null != conditions) {
                for (String condition : conditions) {
                    if (log.toLowerCase().contains(condition.toLowerCase())) {
                        include = true;
                        break;
                    }
                }
            }
            //??????????????????
            if (null != excludeConditions && include) {
                for (String exclude : excludeConditions) {
                    if (log.toLowerCase().contains(exclude.toLowerCase())) {
                        include = false;
                        break;
                    }
                }
            }
            //?????????????????????
            if (include && excludeConfigs) {
                if (log.toLowerCase().contains("??????")) {
                    List<SVNKindBean> paths = svnLogBean.getPaths();
                    boolean isAllConfigs = true;
                    String path = "";
                    for (SVNKindBean item : paths) {
                        path = item.getPath().toLowerCase();
                        if (
                                !(!(path.endsWith(".lua") && !path.contains("lua/data/")) &&  //???????????????????????? ?????????????????????lua??????
                                        !(path.contains("string.lua")) &&   //???????????????????????? ?????????string??????
                                        !(path.contains("uistring.lua")) &&   //???????????????????????? ?????????uistring??????
                                        !(path.contains(".prefab")) &&   //???????????????????????? ?????????prefab??????
                                        !(path.contains(".mat")) &&   //???????????????????????? ?????????mat??????
                                        !(path.contains(".png")) &&   //???????????????????????? ?????????png??????
                                        !(path.contains(".anim")) &&   //???????????????????????? ?????????anim??????
                                        !(path.contains(".mp4")) &&   //???????????????????????? ?????????mp4??????
                                        !(path.contains(".mp3") && !path.contains("/download/") && !path.contains("/audios/phone_")) &&   //???????????????????????? ????????????download????????????mp3??????
                                        !(path.contains(".controller")))  //???????????????????????? ?????????controller??????
                        ) {
                            isAllConfigs = false;
                            break;
                        }
                    }
                    if (isAllConfigs)
                        include = false;

                    StringBuilder builder = new StringBuilder();
                    for (SVNKindBean svnLogBeanPath : svnLogBean.getPaths()) {
                        builder.append("\n\t").append(svnLogBeanPath.getType()).append(":\t").append(svnLogBeanPath.getPath());
                    }
                    if (!include) {
                        excludeConfigResult.add(svnLogBean);
//                        Log.log("====????????????????????????????????????", String.valueOf(svnLogBean.getRevision()), svnLogBean.getAuthor(), builder.toString());
                    } else {
                        notExcludeConfigResult.add(svnLogBean);
//                        Log.log("====???????????????????????????????????????", String.valueOf(svnLogBean.getRevision()), svnLogBean.getAuthor(), builder.toString());
                    }
                }
            }

            //??????????????????????????????????????????????????? ?????? ??????????????????
            if (include) {
                List<SVNKindBean> paths = svnLogBean.getPaths();
                String path = "";
                boolean isAllConfigs = true;
                for (SVNKindBean item : paths) {
                    path = item.getPath().toLowerCase();
                    if (!
                            ((path.contains("lua/data") && path.endsWith(".lua")) ||
                                    (path.contains("design/excel_config") && path.endsWith(".xlsx")) ||
                                    (path.contains("design/") && path.endsWith("??????????????????.xlsm")) ||
                                    (path.contains("design/") && path.endsWith("????????????v2.xlsm")) ||
                                    (path.contains("design/server_tool_exe") && path.endsWith(".xlsx")) ||
                                    (path.contains("design/server_tool_exe") && path.endsWith(".csv")))
                    ) {
                        isAllConfigs = false;
                        break;
                    }
                }

                if (isAllConfigs) {
                    isAllConfigResult.add(svnLogBean);
//                    Log.log("====???????????????", svnLogBean.getPaths().toString());
                    include = false;
                }
            }

            //???????????????????????????
            if (include) {
                List<SVNKindBean> paths = svnLogBean.getPaths();
                String path = "";
                boolean isAllConfigs = true;
                for (SVNKindBean item : paths) {
                    path = item.getPath().toLowerCase();
                    if (!
                            (path.endsWith("externalproto.proto") || path.endsWith("protomsg.go"))||
                            (path.contains("design/server_tool_exe") || path.contains("design/server_tool"))
                    ) {
                        isAllConfigs = false;
                        break;
                    }
                }

                if (isAllConfigs) {
                    isAllServerConfigResult.add(svnLogBean);
//                    Log.log("====??????????????????????????????", svnLogBean.getPaths().toString());
                    include = false;
                }
            }

            if (include) {
                result.add(svnLogBean);
                include = false;
            }
        }
//        Log.log("====?????????????????????????????????????????????", getVersions(excludeConfigResult).toString());
//        Log.log("====?????????????????????????????????????????????", getVersions(notExcludeConfigResult).toString());
//        Log.log("====????????????????????????????????????????????????????????????????????????????????????", getVersions(isAllConfigResult).toString());
//        Log.log("====?????????????????????????????????????????????", getVersions(isAllServerConfigResult).toString());
        return result;
    }

    public static List<SVNLogBean> getBeansByRevisions(List<SVNLogBean> svnLogBeans, List<Long> conditions) {
        List<SVNLogBean> result = new ArrayList<>();
        Long revesion = -1L;
        boolean include = false;
        for (SVNLogBean svnLogBean : svnLogBeans) {
            revesion = svnLogBean.getRevision();
            // ??????????????????
            if (null != conditions) {
                for (Long condition : conditions) {
                    if (revesion.equals(condition)) {
                        include = true;
                        break;
                    }
                }
            }

            if (include) {
                result.add(svnLogBean);
                include = false;
            }
        }
        return result;
    }

    public static List<String> getLogs(List<SVNLogBean> svnLogBeans) {
        List<String> result = new ArrayList<>();
        for (SVNLogBean svnLogBean : svnLogBeans) {
            result.add(svnLogBean.getMessage());
        }
        return result;
    }

    public static void logBeans(SVNLogBean svnLogBean) {
        StringBuilder builder = new StringBuilder();
        for (SVNKindBean svnLogBeanPath : svnLogBean.getPaths()) {
            builder.append("\n\t").append(svnLogBeanPath.getType()).append(":\t").append(svnLogBeanPath.getPath());
        }
        Log.log("====????????????????????????????????????", String.valueOf(svnLogBean.getRevision()), svnLogBean.getAuthor(), builder.toString());
    }
}
