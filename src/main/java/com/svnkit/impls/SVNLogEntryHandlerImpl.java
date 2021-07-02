package com.svnkit.impls;

import com.Util.Log;
import com.Util.TimeFormat;
import com.svnkit.models.SVNKindBean;
import com.svnkit.models.SVNLogBean;
import org.tmatesoft.svn.core.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SVNLogEntryHandlerImpl implements ISVNLogEntryHandler
{
    @Override
    public void handleLogEntry(SVNLogEntry svnLogEntry) throws SVNException {
//        Log.log("handleLogEntry",
//                "\nRevision: ", String.valueOf(svnLogEntry.getRevision()),
//                "\nAuthor: ", svnLogEntry.getAuthor(),
//                "\nDate: ", TimeFormat.FORMAT_YYYYMMDD_HHMMSS(svnLogEntry.getDate()),
//                "\nMessage: ", svnLogEntry.getMessage().trim(),
//                "\nChangedPaths: ", getPaths(svnLogEntry.getChangedPaths())
//        );
        SVNLogBean svnLogBean = new SVNLogBean()
                .setRevision(svnLogEntry.getRevision())
                .setAuthor(svnLogEntry.getAuthor())
                .setDate(svnLogEntry.getDate())
                .setMessage(svnLogEntry.getMessage())
                .setPaths(getPathList(svnLogEntry.getChangedPaths()));
        Log.log("handleLogEntry", svnLogBean.toString());
    }

    public String getPaths(Map<String, SVNLogEntryPath> changedPaths){
        StringBuilder builder = new StringBuilder();
        builder.append("{\n");
        for (Map.Entry<String, SVNLogEntryPath> entry : changedPaths.entrySet()) {
            builder
                    .append(getType(entry.getValue()))
                    .append("\t")
                    .append(entry.getValue().getPath())
                    .append("\n");
        }
        builder.append("}");
        return builder.toString();
    }
    public List<SVNKindBean> getPathList(Map<String, SVNLogEntryPath> changedPaths){
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

    private SVNKindBean.KindType getType(SVNLogEntryPath entryPath) {
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
}
