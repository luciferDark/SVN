package com.svnkit;

import com.Util.Log;
import com.Util.TimeFormat;
import com.Util.Util;
import com.svnkit.interfaces.handlers.*;
import com.svnkit.models.SvnRepoPojo;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.*;
import org.tmatesoft.svn.core.wc.admin.SVNLookClient;
import org.tmatesoft.svn.core.wc2.SvnRevisionRange;

import java.io.File;
import java.util.*;

public class SVN implements ISVNEventHandler {
    private SVNClientManager clientManager = null;
    private ISVNAuthenticationManager authenticationManager = null;

    private SVNLookClient svnLookClient;
    private SVNWCClient svnwcClient;
    private SVNLogClient svnLogClient;
    private SVNUpdateClient svnUpdateClient;
    private SVNDiffClient svnDiffClient;
    private SVNCommitClient svnCommitClient;
    private SVNChangelistClient svnChangelistClient;

    private ISVNInfoEventHandler infoEventHandler;
    private ISVNUpdateEventHandler updateEventHandler;
    private ISVNRevertEventHandler revertEventHandler;
    private ISVNCleanEventHandler cleanEventHandler;
    private ISVNMergeEventHandler mergeEventHandler;
    private ISVNConflictEventHandler conflictEventHandler;
    private ISVNProcessEventHandler processEventHandler;

    private ISVNInfoHandler svnInfoHandler;

    public SVN() {
        init();
    }

    private void init() {
        Log.log("SVN init");
//        DAVRepositoryFactory.setup();
        SVNRepositoryFactoryImpl.setup();
//        FSRepositoryFactory.setup();
        authenticationManager = SVNWCUtil.createDefaultAuthenticationManager(
                Contants.SVN_USERNAME,
                Contants.SVN_PASSWORD.toCharArray()
        );

        clientManager = SVNClientManager.newInstance();
        clientManager.setEventHandler(this);
        clientManager.setAuthenticationManager(authenticationManager);

        svnLookClient = clientManager.getLookClient();
        svnwcClient = clientManager.getWCClient();
        svnCommitClient = clientManager.getCommitClient();
        svnDiffClient = clientManager.getDiffClient();
        svnLogClient = clientManager.getLogClient();
        svnUpdateClient = clientManager.getUpdateClient();
        svnChangelistClient = clientManager.getChangelistClient();
    }

    @Override
    public void handleEvent(SVNEvent svnEvent, double progress) throws SVNException {
        SVNEventAction action = svnEvent.getAction();
        Log.log("SVNClientManager:", "handleEvent action ", action.toString());
        if (SVNEventAction.PROGRESS == action) {
            if (null != processEventHandler) processEventHandler.handleEvent(svnEvent, progress);
        } else if (SVNEventAction.UPDATE_UPDATE == action) {
            if (null != updateEventHandler) updateEventHandler.handleEvent(svnEvent, progress);
        } else if (SVNEventAction.REVERT == action) {
            if (null != revertEventHandler) revertEventHandler.handleEvent(svnEvent, progress);
        } else if (SVNEventAction.CLEANUP_EXTERNAL == action) {
            if (null != cleanEventHandler) cleanEventHandler.handleEvent(svnEvent, progress);
        } else if (SVNEventAction.INFO_EXTERNAL == action) {
            if (null != infoEventHandler) infoEventHandler.handleEvent(svnEvent, progress);
        } else if (SVNEventAction.MERGE_BEGIN == action ||
                SVNEventAction.MERGE_RECORD_INFO_BEGIN == action ||
                SVNEventAction.MERGE_RECORD_INFO == action ||
                SVNEventAction.MERGE_ELIDE_INFO == action ||
                SVNEventAction.FOREIGN_MERGE_BEGIN == action ||
                SVNEventAction.MERGE_COMPLETE == action) {
            if (null != mergeEventHandler) mergeEventHandler.handleEvent(svnEvent, progress);
        } else if (SVNEventAction.TREE_CONFLICT == action ||
                SVNEventAction.FAILED_CONFLICT == action ||
                SVNEventAction.SKIP_CONFLICTED == action) {
            if (null != conflictEventHandler) conflictEventHandler.handleEvent(svnEvent, progress);
        }
    }

    @Override
    public void checkCancelled() throws SVNCancelException {
        Log.log("checkCancelled");
    }

    /*=========================Repository=========================*/
    public SVNRepository excuteSVNRepository(String url) {
        SVNRepository repository = null;
        try {
            repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(url));
            repository.setAuthenticationManager(authenticationManager);
        } catch (SVNException e) {
            e.printStackTrace();
        }
        return repository;
    }

    public void showAllRepositoryInfo(String path, ISVNLogEntryHandler handler) {
        Log.log("showAllRepositoryInfo:", path);
        if (path.startsWith("svn://")) {
            showRepositoryInfo(path, 0, -1,-1, handler);
        } else {
            long lastestVersion = getWCInfoLastestCommitedRevision(path);
            Log.log("lastest version:", String.valueOf(lastestVersion));
            showRepositoryInfo(path, SVNRevision.BASE.getNumber(), lastestVersion,-1, handler);
        }
    }

    public void showLastestRepositoryInfo(String path, ISVNLogEntryHandler handler) {
        long lastestVersion = getWCInfoLastestCommitedRevision(path);
        Log.log("lastest version:", String.valueOf(lastestVersion));
        showRepositoryInfo(path, lastestVersion, lastestVersion,1,handler);
    }

    public void showRepositoryInfo(String path,long startRevision,long  endRevision,int limit, ISVNLogEntryHandler handler) {
        if (path.startsWith("svn://")) {
            SVNRepository repository = excuteSVNRepository(path);
            if (Util.isEmpty(repository)) return;
            try {
                repository.log(new String[]{""},
                        startRevision,
                        endRevision,
                        true,
                        true,
                        handler);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            File mFile = new File(path);
            try {
                clientManager.getLogClient().doLog(
                        new File[]{mFile},
                        SVNRevision.create(startRevision),
                        SVNRevision.create(endRevision),
                        true,
                        true,
                        limit,
                        handler);
            } catch (SVNException e) {
                e.printStackTrace();
            }
        }
    }

    public void showRepositoryInfo(String path, SVNRevision svnRevision) {
        if (Util.isStringEmpty(path)) return;

    }

    public List<SvnRepoPojo> getRepoCatalog(String url, String openPath) {
        try {
            SVNRepository repository = excuteSVNRepository(url);
            Collection<SVNDirEntry> entries = repository.getDir(openPath, 438747, null, (Collection<SVNDirEntry>) null);
            List<SvnRepoPojo> svns = new ArrayList<SvnRepoPojo>();
            Iterator<SVNDirEntry> it = entries.iterator();
            while (it.hasNext()) {
                SVNDirEntry entry = it.next();
                SvnRepoPojo svn = new SvnRepoPojo();
                svn.setCommitMessage(entry.getCommitMessage());
                svn.setDate(entry.getDate());
                svn.setKind(entry.getKind().toString());
                svn.setName(entry.getName());
                svn.setRepositoryRoot(entry.getRepositoryRoot().toString());
                svn.setRevision(entry.getRevision());
                svn.setSize(entry.getSize() / 1024);
                svn.setUrl(openPath.equals("") ? new StringBuffer("/").append(entry.getName()).toString() : new StringBuffer(openPath).append("/").append(entry.getName()).toString());
                svn.setAuthor(entry.getAuthor());
                svn.setState(svn.getKind() == "file" ? null : "closed");
                svns.add(svn);
                Log.log(svn.toString());
            }
            Log.log("获得版本库文件信息");
            return svns;
        } catch (SVNException e) {
            Log.log(e.getMessage());
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            Log.log(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /*=========================svnLookClient=========================*/
    public void showLookInfo(String path, SVNRevision svnRevision) {
        if (Util.isStringEmpty(path)) return;
        if (Util.isEmpty(svnwcClient)) return;
        File file = new File(path);
        if (!file.exists()) return;
//        SVNRevision svnRevision = SVNRevision.create(svnRevisionL);
        try {
            if (Util.isEmpty(svnInfoHandler)) {
                svnwcClient.doInfo(file, svnRevision);
            } else {
                svnwcClient.doInfo(file, svnRevision, svnRevision, SVNDepth.FILES, null, svnInfoHandler);
            }
        } catch (SVNException e) {
            e.printStackTrace();
        }
    }
    /*=========================svnwcClient=========================*/

    public void showWCBaseReposityInfo(String path) {
        showWCInfo(path, SVNRevision.BASE);
    }

    public void showWCPreviousReposityInfo(String path) {
        showWCInfo(path, SVNRevision.PREVIOUS);
    }

    public void showWCLastestReposityInfo(String path) {
        showWCInfo(path, SVNRevision.HEAD);
    }

    public void showWCLastestWCInfo(String path) {
        showWCInfo(path, SVNRevision.WORKING);
    }

    /**
     * 获取目录的svn log
     *
     * @param path
     * @param svnRevision
     */
    public void showWCInfo(String path, SVNRevision svnRevision) {
        if (Util.isStringEmpty(path)) return;
        if (Util.isEmpty(svnwcClient)) return;
        File file = new File(path);
        if (!file.exists()) return;
//        SVNRevision svnRevision = SVNRevision.create(svnRevisionL);
        try {
            if (Util.isEmpty(svnInfoHandler)) {
                svnwcClient.doInfo(file, svnRevision);
            } else {
                svnwcClient.doInfo(file, svnRevision, svnRevision, SVNDepth.FILES, null, svnInfoHandler);
            }
        } catch (SVNException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取svn://仓库目录最新提交revision
     * @param path
     * @return
     */
    public long getWCInfoLastestCommitedRevision(String path) {
        SVNInfo info = getWCInfoLastestCommitedInfo(path);
        return Util.isEmpty(info) ? -1 : info.getCommittedRevision().getNumber();
    }

    /**
     * 获取svn://仓库目录最新提交Date
     * @param path
     * @return
     */
    public Date getWCInfoLastestCommitedDate(String path) {
        SVNInfo info = getWCInfoLastestCommitedInfo(path);
        return Util.isEmpty(info) ? null : info.getCommittedDate();
    }

    /**
     * 获取svn://仓库目录最新提交ChangeList
     * @param path
     * @return
     */
    public String getWCInfoLastestCommitedChangeList(String path) {
        SVNInfo info = getWCInfoLastestCommitedInfo(path);
        return Util.isEmpty(info) ? "" : info.getChangelistName();
    }

    public SVNInfo getWCInfoLastestCommitedInfo(String path) {
        return getWCInfoCommitedInfo(path, SVNRevision.HEAD, SVNRevision.HEAD);
    }

    public SVNInfo getWCInfoAllCommitedInfo(String path) {
        return getWCInfoCommitedInfo(path, SVNRevision.BASE, SVNRevision.HEAD);
    }

    public SVNInfo getWCInfoCommitedInfo(String path, SVNRevision startRevision,SVNRevision endRevision) {
        SVNInfo svnInfo = null;
        if (path.startsWith("svn://")) {
            try {
                svnInfo = svnwcClient.doInfo(SVNURL.parseURIEncoded(path), startRevision, endRevision);
            } catch (SVNException e) {
                e.printStackTrace();
            }
        }
        return svnInfo;
    }

    public void getWCInfo(String path) {
        try {
            long l = svnwcClient.doGetRevisionProperty(SVNURL.parseURIEncoded(path),"", SVNRevision.HEAD,
                    new ISVNPropertyHandler(){
                        @Override
                        public void handleProperty(File file, SVNPropertyData svnPropertyData) throws SVNException {

                            Log.log("handleProperty File:", svnPropertyData.toString());
                        }

                        @Override
                        public void handleProperty(SVNURL svnurl, SVNPropertyData svnPropertyData) throws SVNException {

                            Log.log("handleProperty svnurl:", svnPropertyData.toString(),  svnurl.getPath());
                        }

                        @Override
                        public void handleProperty(long l, SVNPropertyData svnPropertyData) throws SVNException {

                            Log.log("handleProperty long:", svnPropertyData.toString());
                        }
                    });
        } catch (SVNException e) {
            e.printStackTrace();
        }
    }

    /*=========================Set=Get=========================*/
    public void setConflictEventHandler(ISVNConflictEventHandler conflictEventHandler) {
        this.conflictEventHandler = conflictEventHandler;
    }

    public void setUpdateEventHandler(ISVNUpdateEventHandler updateEventHandler) {
        this.updateEventHandler = updateEventHandler;
    }

    public void setMergeEventHandler(ISVNMergeEventHandler mergeEventHandler) {
        this.mergeEventHandler = mergeEventHandler;
    }

    public void setProcessEventHandler(ISVNProcessEventHandler processEventHandler) {
        this.processEventHandler = processEventHandler;
    }

    public void setInfoEventHandler(ISVNInfoEventHandler infoEventHandler) {
        this.infoEventHandler = infoEventHandler;
    }

    public void setRevertEventHandler(ISVNRevertEventHandler revertEventHandler) {
        this.revertEventHandler = revertEventHandler;
    }

    public void setCleanEventHandler(ISVNCleanEventHandler cleanEventHandler) {
        this.cleanEventHandler = cleanEventHandler;
    }

    public void setSvnInfoHandler(ISVNInfoHandler svnInfoHandler) {
        this.svnInfoHandler = svnInfoHandler;
    }

    public SVNWCClient getSvnwcClient() {
        return svnwcClient;
    }

    public SVNLogClient getSvnLogClient() {
        return svnLogClient;
    }

    public SVNUpdateClient getSvnUpdateClient() {
        return svnUpdateClient;
    }

    public SVNDiffClient getSvnDiffClient() {
        return svnDiffClient;
    }

    public SVNCommitClient getSvnCommitClient() {
        return svnCommitClient;
    }

    public SVNLookClient getSvnLookClient() {
        return svnLookClient;
    }

    public SVNChangelistClient getSvnChangelistClient() {
        return svnChangelistClient;
    }
}
