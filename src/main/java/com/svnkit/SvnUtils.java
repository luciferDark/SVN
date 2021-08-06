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
            // 判断满足条件
            if (null != conditions) {
                for (String condition : conditions) {
                    if (log.toLowerCase().contains(condition)) {
                        include = true;
                        break;
                    }
                }
            }
            //判断排除条件
            if (null != excludeConditions && include) {
                for (String exclude : excludeConditions) {
                    if (log.toLowerCase().contains(exclude)) {
                        include = false;
                        break;
                    }
                }
            }
            //排除纯策划配置
            if (include && excludeConfigs) {
                if (log.toLowerCase().contains("配置")) {
                    List<SVNKindBean> paths = svnLogBean.getPaths();
                    String path = "";
                    for (SVNKindBean item : paths) {
                        path = item.getPath().toLowerCase();
                        if (!(path.endsWith(".lua") && !path.contains("lua/data/")) &&  //策划配置的提交， 不包含非配置的lua文件
                                !(path.contains("string.lua")) &&   //策划配置的提交， 不包含string文件
                                !(path.contains("uistring.lua")) &&   //策划配置的提交， 不包含uistring文件
                                !(path.contains(".prefab")) &&   //策划配置的提交， 不包含prefab文件
                                !(path.contains(".mat")) &&   //策划配置的提交， 不包含mat文件
                                !(path.contains(".png")) &&   //策划配置的提交， 不包含png文件
                                !(path.contains(".anim")) &&   //策划配置的提交， 不包含anim文件
                                !(path.contains(".mp4")) &&   //策划配置的提交， 不包含mp4文件
                                !(path.contains(".mp3") && !path.contains("/download/") && !path.contains("/audios/phone_")) &&   //策划配置的提交， 不包含非download路径下的mp3文件
                                !(path.contains(".controller"))  //策划配置的提交， 不包含controller文件
                        ) {
                            include = false;
                            break;
                        }
                    }

                    StringBuilder builder = new StringBuilder();
                    for (SVNKindBean svnLogBeanPath : svnLogBean.getPaths()) {
                        builder.append("\n\t").append(svnLogBeanPath.getType()).append(":\t").append(svnLogBeanPath.getPath());
                    }
                    if (!include) {
                        excludeConfigResult.add(svnLogBean);
//                        Log.log("====排除策划纯提交的配置版本", String.valueOf(svnLogBean.getRevision()), svnLogBean.getAuthor(), builder.toString());
                    } else {
                        notExcludeConfigResult.add(svnLogBean);
//                        Log.log("====未排除策划纯提交的配置版本", String.valueOf(svnLogBean.getRevision()), svnLogBean.getAuthor(), builder.toString());
                    }
                }
            }

            //排除纯策划老师提交，但是日志又没有 配置 关键字的版本
            if (include) {
                List<SVNKindBean> paths = svnLogBean.getPaths();
                String path = "";
                boolean isAllConfigs = true;
                for (SVNKindBean item : paths) {
                    path = item.getPath().toLowerCase();
                    if (!
                            ((path.contains("lua/data") && path.endsWith(".lua")) ||
                                    (path.contains("design/excel_config") && path.endsWith(".xlsx")) ||
                                    (path.contains("design/") && path.endsWith("国服表格工具.xlsm")) ||
                                    (path.contains("design/server_tool_exe") && path.endsWith(".csv")))
                    ) {
                        isAllConfigs = false;
                        break;
                    }
                }

                if (isAllConfigs) {
                    isAllConfigResult.add(svnLogBean);
//                    Log.log("====排除纯配置", svnLogBean.getPaths().toString());
                    include = false;
                }
            }

            //排除纯后端老师提交
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
//                    Log.log("====排除纯服务器协议配置", svnLogBean.getPaths().toString());
                    include = false;
                }
            }

            if (include) {
                result.add(svnLogBean);
                include = false;
            }
        }
//        Log.log("====排除的策划纯提交的配置所有版本", getVersions(excludeConfigResult).toString());
//        Log.log("====未排除的策划提交的配置所有版本", getVersions(notExcludeConfigResult).toString());
//        Log.log("====排除的单纯策划提交的配置，但是不包含配置关键字的所有版本", getVersions(isAllConfigResult).toString());
//        Log.log("====排除纯服务器协议配置的所有版本", getVersions(isAllServerConfigResult).toString());
        return result;
    }

    public static List<SVNLogBean> getBeansByRevisions(List<SVNLogBean> svnLogBeans, List<Long> conditions) {
        List<SVNLogBean> result = new ArrayList<>();
        Long revesion = -1L;
        boolean include = false;
        for (SVNLogBean svnLogBean : svnLogBeans) {
            revesion = svnLogBean.getRevision();
            // 判断满足条件
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
        Log.log("====排除策划纯提交的配置版本", String.valueOf(svnLogBean.getRevision()), svnLogBean.getAuthor(), builder.toString());
    }
}
