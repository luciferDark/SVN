package com.svnkit;

import com.Util.Log;
import com.Util.Util;
import com.svnkit.interfaces.handlers.*;
import com.svnkit.models.SVNKitBean;
import com.svnkit.models.SVNLogBean;
import com.svnkit.models.SvnRepoPojo;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.*;
import org.tmatesoft.svn.core.wc.admin.SVNLookClient;

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

    private static String svnHostName = "svn://bosslovesvn.diezhi.local";
    private String workingRepositoryPath = "/BossLove/BossLove_Client/branches";
    private String workingRepositoryPath_GuoTrunk = "/BossLove/BossLove_Client/trunk";
    private String workingRepositoryPath_JP_branches = "/BossLove/BossLove_Sea_Client/JP/branches";
    private String workingRepositoryPath_JP = "/BossLove/BossLove_Sea_Client/JP/trunk";
    private String workingRepositoryPath_TW_branches = "/BossLove/BossLove_Sea_Client/TW/branches";
    private String workingRepositoryPath_TW = "/BossLove/BossLove_Sea_Client/TW/trunk";

    private List<SVNKitBean> cnList_release = null;
    private List<SVNKitBean> cnList_package = null;
    private List<SVNKitBean> europeList_release = null;
    private List<SVNKitBean> europeList_package = null;
    private List<SVNKitBean> krList_release = null;
    private List<SVNKitBean> krList_package = null;
    private List<SVNKitBean> seaenList_release = null;
    private List<SVNKitBean> seaenList_package = null;
    private List<SVNKitBean> twList_release = null;
    private List<SVNKitBean> twList_package = null;
    private List<SVNKitBean> jpList_release = null;
    private List<SVNKitBean> jpList_package = null;

    public SVN() {
        init();
    }

    private void init() {
        Log.log("SVN init");
        SVNRepositoryFactoryImpl.setup();
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

    /*=========================RepositoryFileList=========================*/
    public void initRepositoryPath(){
        initData();
        initShowRepositoryFileList();
    }

    private void initData() {
        cnList_release = new ArrayList<>();
        cnList_package = new ArrayList<>();

        europeList_release = new ArrayList<>();
        europeList_package = new ArrayList<>();

        krList_release = new ArrayList<>();
        krList_package = new ArrayList<>();

        seaenList_release = new ArrayList<>();
        seaenList_package = new ArrayList<>();

        twList_release = new ArrayList<>();
        twList_package = new ArrayList<>();

        jpList_release = new ArrayList<>();
        jpList_package = new ArrayList<>();

        workingRepositoryPath = svnHostName + workingRepositoryPath;
        workingRepositoryPath_GuoTrunk = svnHostName + workingRepositoryPath_GuoTrunk;
        workingRepositoryPath_JP_branches = svnHostName + workingRepositoryPath_JP_branches;
        workingRepositoryPath_JP = svnHostName + workingRepositoryPath_JP;
        workingRepositoryPath_TW_branches = svnHostName + workingRepositoryPath_TW_branches;
        workingRepositoryPath_TW = svnHostName + workingRepositoryPath_TW;
    }

    private void clearDataCn() {
        cnList_release.clear();
        cnList_package.clear();
    }

    private void clearDataKr() {
        europeList_release.clear();
        europeList_package.clear();
    }

    private void clearDataEurope() {
        krList_release.clear();
        krList_package.clear();
    }

    private void clearDataSeaen() {
        seaenList_release.clear();
        seaenList_package.clear();
    }

    private void clearDataTw() {
        twList_release.clear();
        twList_package.clear();
    }

    private void clearDataJp() {
        jpList_release.clear();
        jpList_package.clear();
    }

    private void clearData() {
        clearDataCn();
        clearDataEurope();
        clearDataKr();
        clearDataSeaen();
        clearDataTw();
        clearDataJp();
    }

    private void initShowRepositoryFileList() {
        clearData();
        initShowRepositoryFileList(workingRepositoryPath);
        initShowRepositoryFileList(workingRepositoryPath_GuoTrunk);
        initShowRepositoryFileList(workingRepositoryPath_JP_branches);
        initShowRepositoryFileList(workingRepositoryPath_JP);
        initShowRepositoryFileList(workingRepositoryPath_TW_branches);
        initShowRepositoryFileList(workingRepositoryPath_TW);
    }

    private void initShowRepositoryFileList(String logSVNPath) {
        if (Util.isStringEmpty(logSVNPath)) {
            return;
        }
        try {
            SVNRepository svnRepository = excuteSVNRepository(logSVNPath);
            listEntries(svnRepository);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void listEntries(SVNRepository repository) {
        try {
            Collection collection = repository.getDir("", -1, null, (Collection) null);
            Iterator iterator = collection.iterator();
            while (iterator.hasNext()) {
                SVNDirEntry entry = (SVNDirEntry) iterator.next();
                classifyRepository(entry);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void classifyRepository(SVNDirEntry entry) {
        String repositoryName = entry.getName();
        SVNKitBean.BranchType branchType = null;
        if (repositoryName.contains("_release") || repositoryName.contains("Project") || repositoryName.contains("BossLove_Client_Unity_U5")) {
            branchType = SVNKitBean.BranchType.RELEASE;
        } else if (repositoryName.contains("_package")) {
            branchType = SVNKitBean.BranchType.PACKAGE;
        } else {
        }
        if (null == branchType) {
            return;
        }
        if (repositoryName.contains("_kr")) {
            add2List(branchType == SVNKitBean.BranchType.RELEASE ? krList_release : krList_package,
                    entry,
                    SVNKitBean.ChannelType.KR, branchType);
        } else if (repositoryName.contains("_europ") || repositoryName.contains("_europe")) {
            add2List(branchType == SVNKitBean.BranchType.RELEASE ? europeList_release : europeList_package,
                    entry,
                    SVNKitBean.ChannelType.EUROP, branchType);
        } else if (repositoryName.contains("_seaen")) {
            add2List(branchType == SVNKitBean.BranchType.RELEASE ? seaenList_release : seaenList_package,
                    entry,
                    SVNKitBean.ChannelType.SEAEN, branchType);
        } else if (repositoryName.contains("_jp") || repositoryName.contains("JP_") || repositoryName.contains("JPProject")) {
            add2List(branchType == SVNKitBean.BranchType.RELEASE ? jpList_release : jpList_package,
                    entry,
                    SVNKitBean.ChannelType.JP, branchType);
        } else if (repositoryName.contains("_tw") || repositoryName.contains("TWProject")) {
            add2List(branchType == SVNKitBean.BranchType.RELEASE ? twList_release : twList_package,
                    entry,
                    SVNKitBean.ChannelType.TW, branchType);
        } else {
            add2List(branchType == SVNKitBean.BranchType.RELEASE ? cnList_release : cnList_package,
                    entry,
                    SVNKitBean.ChannelType.CN, branchType);
        }
    }

    private void add2List(List list, SVNDirEntry entry, SVNKitBean.ChannelType channelType, SVNKitBean.BranchType branchType) {
        SVNKitBean bean = new SVNKitBean(entry, SVNKitBean.ChannelType.CN, branchType);
        if (!list.contains(bean)) {
            list.add(bean);
        }
    }

    public List<SVNKitBean> getSVNChannelList(String channelsprefix, String branchssubfix) {
        if (Contants.Country_CN.equals(channelsprefix) && Contants.BRANCH_PACKAGE.equals(branchssubfix)) {
            return cnList_package;
        } else if (Contants.Country_CN.equals(channelsprefix) && Contants.BRANCH_RELEASE.equals(branchssubfix)) {
            return cnList_release;
        } else if (Contants.Country_TW.equals(channelsprefix) && Contants.BRANCH_PACKAGE.equals(branchssubfix)) {
            return twList_package;
        } else if (Contants.Country_TW.equals(channelsprefix) && Contants.BRANCH_RELEASE.equals(branchssubfix)) {
            return twList_release;
        } else if (Contants.Country_JP.equals(channelsprefix) && Contants.BRANCH_PACKAGE.equals(branchssubfix)) {
            return jpList_package;
        } else if (Contants.Country_JP.equals(channelsprefix) && Contants.BRANCH_RELEASE.equals(branchssubfix)) {
            return jpList_release;
        } else if (Contants.Country_EUROPE.equals(channelsprefix) && Contants.BRANCH_PACKAGE.equals(branchssubfix)) {
            return europeList_package;
        } else if (Contants.Country_EUROPE.equals(channelsprefix) && Contants.BRANCH_RELEASE.equals(branchssubfix)) {
            return europeList_release;
        } else if (Contants.Country_KR.equals(channelsprefix) && Contants.BRANCH_PACKAGE.equals(branchssubfix)) {
            return krList_package;
        } else if (Contants.Country_KR.equals(channelsprefix) && Contants.BRANCH_RELEASE.equals(branchssubfix)) {
            return krList_release;
        } else if (Contants.Country_SEAEN.equals(channelsprefix) && Contants.BRANCH_PACKAGE.equals(branchssubfix)) {
            return seaenList_package;
        } else if (Contants.Country_SEAEN.equals(channelsprefix) && Contants.BRANCH_RELEASE.equals(branchssubfix)) {
            return seaenList_release;
        } else {
            return null;
        }
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

    /**
     * 获取所有的log日志 直接返回日志信息集合
     *
     * @param path
     */
    public List<SVNLogBean> getRepositoryAllLogInfo(String path) {
        List<SVNLogBean> svnLogBeans = new ArrayList<>();
        if (path.startsWith("svn://")) {
            Collection collection = showRepositoryInfo1(path, 0, -1, -1);
            Iterator iterator = collection.iterator();
            while (iterator.hasNext()) {
                svnLogBeans.add(new SVNLogBean((SVNLogEntry) iterator.next()));
            }
        }
        return svnLogBeans;
    }

    /**
     * 获取所有的log日志 并交由handler后期处理
     *
     * @param path
     * @param handler
     */
    public void showAllRepositoryInfo(String path, ISVNLogEntryHandler handler) {
        if (path.startsWith("svn://")) {
            showRepositoryInfo(path, 0, -1, -1, handler);
        } else {
            long lastestVersion = getWCInfoLastestCommitedRevision(path);
            Log.log("lastest version:", String.valueOf(lastestVersion));
            showRepositoryInfo(path, SVNRevision.BASE.getNumber(), lastestVersion, -1, handler);
        }
    }

    public void showLastestRepositoryInfo(String path, ISVNLogEntryHandler handler) {
        long lastestVersion = getWCInfoLastestCommitedRevision(path);
        Log.log("lastest version:", String.valueOf(lastestVersion));
        showRepositoryInfo(path, lastestVersion, lastestVersion, 1, handler);
    }

    public Collection showRepositoryInfo1(String path, long startRevision, long endRevision, int limit) {
        Collection collection = null;
        if (path.startsWith("svn://")) {
            SVNRepository repository = excuteSVNRepository(path);
            if (Util.isEmpty(repository)) return collection;
            try {
                collection = repository.log(new String[]{""},
                        null,
                        startRevision,
                        endRevision,
                        true,
                        true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return collection;
    }

    public void showRepositoryInfo(String path, long startRevision, long endRevision, int limit, ISVNLogEntryHandler handler) {
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
     *
     * @param path
     * @return
     */
    public long getWCInfoLastestCommitedRevision(String path) {
        SVNInfo info = getWCInfoLastestCommitedInfo(path);
        return Util.isEmpty(info) ? -1 : info.getCommittedRevision().getNumber();
    }

    /**
     * 获取svn://仓库目录最新提交Date
     *
     * @param path
     * @return
     */
    public Date getWCInfoLastestCommitedDate(String path) {
        SVNInfo info = getWCInfoLastestCommitedInfo(path);
        return Util.isEmpty(info) ? null : info.getCommittedDate();
    }

    /**
     * 获取svn://仓库目录最新提交ChangeList
     *
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

    public SVNInfo getWCInfoCommitedInfo(String path, SVNRevision startRevision, SVNRevision endRevision) {
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
            long l = svnwcClient.doGetRevisionProperty(SVNURL.parseURIEncoded(path), "", SVNRevision.HEAD,
                    new ISVNPropertyHandler() {
                        @Override
                        public void handleProperty(File file, SVNPropertyData svnPropertyData) throws SVNException {

                            Log.log("handleProperty File:", svnPropertyData.toString());
                        }

                        @Override
                        public void handleProperty(SVNURL svnurl, SVNPropertyData svnPropertyData) throws SVNException {

                            Log.log("handleProperty svnurl:", svnPropertyData.toString(), svnurl.getPath());
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
