package com.svnkit.models;

public class SVNSelectBean implements Comparable<SVNSelectBean> {
    private String url;
    private String showName;

    public SVNSelectBean(String url, String showName) {
        this.url = url;
        this.showName = showName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getShowName() {
        return showName;
    }

    public void setShowName(String showName) {
        this.showName = showName;
    }

    @Override
    public String toString() {
        return this.showName;
    }

    @Override
    public int compareTo(SVNSelectBean o) {
        return this.getUrl().toString().compareTo(o.getUrl().toString());
    }
}
