package com.svnkit;

import com.Util.Log;
import com.Util.TimeFormat;
import com.svnkit.impls.SVNLogEntryHandlerImpl;
import com.svnkit.impls.handlers.*;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.wc.ISVNInfoHandler;
import org.tmatesoft.svn.core.wc.SVNInfo;

public class SVNHelper implements
        ISVNInfoHandler
{
    private static SVNHelper mInstance = null;

    private SVN svn = null;

    private SVNHelper() {
        init();
    }

    public static SVNHelper getInstance() {
        if (null == mInstance) {
            synchronized (SVNHelper.class) {
                if (null == mInstance) {
                    mInstance = new SVNHelper();
                }
            }
        }
        return mInstance;
    }

    private void init() {
        Log.log("SVNHelper init");
        svn = new SVN();
        
        setHandler();
    }

    public SVN getSvn() {
        return svn;
    }

    private void setHandler() {
        svn.setSvnInfoHandler(this);
        svn.setCleanEventHandler(new SVNCleanEventHandlerImp());
        svn.setConflictEventHandler(new SVNConflictEventHandlerImp());
        svn.setInfoEventHandler(new SVNInfoEventHandlerImp());
        svn.setMergeEventHandler(new SVNMergeEventHandlerImp());
        svn.setProcessEventHandler(new SVNProgressEventHandlerImp());
        svn.setRevertEventHandler(new SVNRevertEventHandlerImp());
        svn.setUpdateEventHandler(new SVNUpdateEventHandlerImp());
    }

    public void start() {
        Log.log("SVNHelper start");
        String path = "H:\\work-projects\\TW\\branches\\evol_tw_release_210625";
        String path1 = "svn://bosslovesvn.diezhi.local/BossLove/BossLove_Sea_Client/TW/branches/evol_tw_release_210625";
        svn.showLastestRepositoryInfo(path1, new SVNLogEntryHandlerImpl());
        Log.log("================================================");
        svn.showAllRepositoryInfo(path1, new SVNLogEntryHandlerImpl());
    }

    @Override
    public void handleInfo(SVNInfo svnInfo) throws SVNException {
        Log.log("SVNInfo:",
                "\n\t author:\t\t\t",svnInfo.getAuthor(),
                "\n\t Commit date:\t\t\t",TimeFormat.FORMAT_YYYYMMDD_HHMMSS(svnInfo.getCommittedDate()),
                "\n\t sum:\t\t\t",svnInfo.getChecksum(),
                "\n\t listName:\t\t\t",svnInfo.getChangelistName(),
                "\n\t path:\t\t\t",svnInfo.getPath(),
                "\n\t revision:\t\t\t",svnInfo.getRevision().toString(),
                "\n\t commit revision:\t\t\t",svnInfo.getCommittedRevision().toString(),
                "\n\t url:\t\t\t",svnInfo.getURL().toDecodedString()
        );
    }

}
