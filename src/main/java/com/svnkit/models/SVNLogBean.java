package com.svnkit.models;

import com.Util.TimeFormat;
import com.svnkit.SvnUtils;
import org.tmatesoft.svn.core.SVNLogEntry;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SVNLogBean {
    private long revision;
    private String author;
    private Date date;
    private String message;
    private List<SVNKindBean> paths;

    public SVNLogBean(){

    }

    public SVNLogBean(SVNLogEntry entry){
        this.revision = entry.getRevision();
        this.author = entry.getAuthor();
        this.date = entry.getDate();
        this.message = entry.getMessage();
        this.paths =SvnUtils.getPathList(entry.getChangedPaths());
    }

    public SVNLogBean(long revision, String author, Date date, String message, List<SVNKindBean> paths) {
        this.revision = revision;
        this.author = author;
        this.date = date;
        this.message = message;
        this.paths = paths;
    }

    public long getRevision() {
        return revision;
    }

    public SVNLogBean setRevision(long revision) {
        this.revision = revision;
        return this;
    }

    public String getAuthor() {
        return author;
    }

    public SVNLogBean setAuthor(String author) {
        this.author = author;
        return this;
    }

    public Date getDate() {
        return date;
    }

    public SVNLogBean setDate(Date date) {
        this.date = date;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public SVNLogBean setMessage(String message) {
        this.message = message;
        return this;
    }

    public List<SVNKindBean> getPaths() {
        return paths;
    }

    public SVNLogBean setPaths(List<SVNKindBean> paths) {
        this.paths = paths;
        return this;
    }

    public SVNLogBean addPaths(SVNKindBean path) {
        if (null == path){
            return this;
        }
        if (this.paths == null){
            this.paths = new ArrayList<SVNKindBean>();
        }
        if (!this.paths.contains(path)){
            this.paths.add(path);
        }
        return this;
    }

    public String pathToString(List<SVNKindBean> paths) {
        StringBuilder builder = new StringBuilder();
        paths.forEach(svnKindBean -> {
            builder.append(svnKindBean.getType().getDesc())
                    .append(" : ")
                    .append(svnKindBean.getPath())
                    .append("\n");
        });
        return builder.toString().trim();
    }

    @Override
    public String toString() {
        return
                "Revision: " + revision +
                "\nAuthor: " + author +
                "\nDate: " + TimeFormat.FORMAT_YYYYMMDD_HHMMSS_SSSS(date) +
                "\nMessage:\n" + message +
                "\n----\n" + pathToString(paths);
    }
}
