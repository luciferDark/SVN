package com.svnkit.models;

import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNNodeKind;

public class SVNKitBean implements Comparable<SVNKitBean> {
    public SVNDirEntry entry;
    public ChannelType channelType;
    public BranchType branchType;

    public SVNKitBean(SVNDirEntry entry) {
        this.entry = entry;
    }

    public SVNKitBean(SVNDirEntry entry, ChannelType channelType, BranchType branchType) {
        this.entry = entry;
        this.channelType = channelType;
        this.branchType = branchType;
    }

    @Override
    public String toString() {
        return entry.getName();
    }

    @Override
    public int compareTo(SVNKitBean o) {
        if (entry.getKind() == SVNNodeKind.DIR && o.entry.getKind() == SVNNodeKind.DIR
                || entry.getKind() == SVNNodeKind.FILE && o.entry.getKind() == SVNNodeKind.FILE) {
            return entry.getName().compareTo(o.entry.getName());
        } else if (entry.getKind() == SVNNodeKind.DIR && o.entry.getKind() == SVNNodeKind.FILE) {
            return -1;
        } else if (entry.getKind() == SVNNodeKind.FILE && o.entry.getKind() == SVNNodeKind.DIR) {
            return 1;
        }
        return 0;
    }

    public enum ChannelType{
        CN,
        TW,
        JP,
        KR,
        EUROP,
        SEAEN
    }
    public enum BranchType{
        PACKAGE,
        RELEASE
    }
}
