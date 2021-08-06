package com.svnkit;

import com.Util.Log;
import com.Util.TimeFormat;
import com.svnkit.impls.SVNLogEntryHandlerImpl;
import com.svnkit.impls.handlers.*;
import com.svnkit.models.SVNKitBean;
import com.svnkit.models.SVNLogBean;
import org.apache.subversion.javahl.*;
import org.apache.subversion.javahl.ConflictResult.Choice;
import org.apache.subversion.javahl.callback.ConfigEvent;
import org.apache.subversion.javahl.callback.ConflictResolverCallback;
import org.apache.subversion.javahl.callback.ProgressCallback;
import org.apache.subversion.javahl.types.Depth;
import org.apache.subversion.javahl.types.Revision;
import org.apache.subversion.javahl.types.RevisionRange;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.wc.ISVNInfoHandler;
import org.tmatesoft.svn.core.wc.ISVNOptions;
import org.tmatesoft.svn.core.wc.SVNDiffClient;
import org.tmatesoft.svn.core.wc.SVNInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SVNHelper implements
        ISVNInfoHandler {
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

    public void initReportiesPathUrl(){
        if (null == svn){
            return;
        }
        svn.initRepositoryPath();
    }

    public List<SVNKitBean> getSVNChannelList(String channelsprefix, String branchssubfix) {
        if (null == svn){
            return null;
        }
        return svn.getSVNChannelList(channelsprefix, branchssubfix);
    }


    private void init() {
        Log.log("SVNHelper init");
        svn = new SVN();

        setHandler();
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

    public List<Long> getVersionsByLog(String path, List<String> conditions, List<String> excludes) {
        return SvnUtils.getVersionsByLog(getRepositoryAllLogInfo(path), conditions, excludes, true);
    }

    private List<SVNLogBean> getRepositoryAllLogInfo(String path) {
        List<SVNLogBean> repositoryAllLogInfo = svn.getRepositoryAllLogInfo(path);
        return repositoryAllLogInfo;
    }

    /*=========================Test=========================*/
    private void mergeTest1(String path, List<Long> versionsByLog) {
        String localPath = "H:/work-projects/TW/branches/evol_tw_package";
        try {
            SVNDiffClient diffClient = svn.getSvnDiffClient();

            ISVNOptions mergeOptions = diffClient.getOptions();

        } catch (Exception e) {

        }
    }

    private void mergeTest(String path, List<Long> versionsByLog) {
        String localPath = "H:/work-projects/TW/branches/evol_tw_package";
        try {
            SVNClient svnClient = new SVNClient();
            List<RevisionRange> ranges = new ArrayList<>();
            for (Long version : versionsByLog) {
                RevisionRange rangeItem = new RevisionRange(Revision.getInstance(version - 1), Revision.getInstance(version));
                ranges.add(rangeItem);
            }
            svnClient.setProgressCallback(new ProgressCallback() {
                @Override
                public void onProgress(ProgressEvent progressEvent) {
                    Log.log("progressEvent:" + progressEvent.getProgress() + progressEvent.getTotal());
                }
            });
            svnClient.setConfigEventHandler(new ConfigEvent() {
                @Override
                public void onLoad(ISVNConfig isvnConfig) {
                    Log.log("ISVNConfig:" + isvnConfig.toString());
                }
            });
            svnClient.setConflictResolver(new ConflictResolverCallback() {
                @Override
                public ConflictResult resolve(ConflictDescriptor conflictDescriptor) throws SubversionException {
                    Choice choice = Choice.postpone;
                    if (conflictDescriptor.getKind() == ConflictDescriptor.Kind.tree) {
                        Log.log("==>0、树冲突：" + conflictDescriptor.getPath());
                        choice = Choice.chooseMerged;
                    } else {
                        String pathFix = conflictDescriptor.getPath().toLowerCase();
                        if (pathFix.endsWith(".working")) {
                            pathFix = pathFix.replace(".working", "").toLowerCase();
                        }
                        if (pathFix.contains("Assets/Lua/data/".toLowerCase()) ||
                                pathFix.contains("Design/excel_config/".toLowerCase())) {
                            Log.log("==>1、策划配置冲突：" + conflictDescriptor.getPath());
                            choice = Choice.chooseBase;
                        } else if (pathFix.contains("Design/server_tool_exe/".toLowerCase())) {
                            Log.log("==>2、服务器配置冲突：" + conflictDescriptor.getPath());
                            choice = Choice.chooseMineFull;
                        } else if (pathFix.endsWith(".png") ||
                                pathFix.endsWith(".png.meta") ||
                                pathFix.contains("_Alpha.png".toLowerCase()) ||
                                pathFix.contains("_RGB.png".toLowerCase()) ||
                                (pathFix.contains("UI_".toLowerCase()) && (pathFix.contains(".prefab".toLowerCase()) || pathFix.contains(".mat".toLowerCase())))) {
                            Log.log("==>3、UI资源冲突：" + conflictDescriptor.getPath());
                            choice = Choice.chooseMineFull;
                        } else if (pathFix.contains(".lua.bytes".toLowerCase()) ||
                                pathFix.contains("Builds/Lua/".toLowerCase())) {
                            Log.log("==>4、Lua Bytes冲突：" + conflictDescriptor.getPath());
                            choice = Choice.chooseMineFull;
                        } else if (pathFix.contains("ExternalProto.proto".toLowerCase()) ||
                                pathFix.contains("protomsg.go".toLowerCase())) {
                            Log.log("==>5、协议冲突：" + conflictDescriptor.getPath());
                            choice = Choice.chooseMineFull;
                        } else if (pathFix.contains(".mp3")) {
                            Log.log("==>6、Lua Bytes冲突：" + conflictDescriptor.getPath());
                            choice = Choice.chooseMineFull;
                        } else {
                            Log.log("==>end、其它冲突：" + conflictDescriptor.getPath());
                        }
                    }
                    ConflictResult result = new ConflictResult(choice, "H:/work-projects/test/1.txt");
                    return result;
                }
            });
            svnClient.merge(
                    path,
                    Revision.HEAD,
                    ranges,
                    localPath,
                    false,
                    Depth.infinity,
                    false,
                    false,
                    false);
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }

    private void testAllRepositoryInfo() {
        String path = "H:/work-projects/TW/branches/evol_tw_release_210625";
        String path1 = "svn://bosslovesvn.diezhi.local/BossLove/BossLove_Sea_Client/TW/branches/evol_tw_release_210625";
        svn.showAllRepositoryInfo(path1, new SVNLogEntryHandlerImpl());
    }

    private void testLastRepositoryInfo() {
        String path = "H:/work-projects/TW/branches/evol_tw_release_210625";
        String path1 = "svn://bosslovesvn.diezhi.local/BossLove/BossLove_Sea_Client/TW/branches/evol_tw_release_210625";
        svn.showLastestRepositoryInfo(path1, new SVNLogEntryHandlerImpl());
    }

    @Override
    public void handleInfo(SVNInfo svnInfo) throws SVNException {
        Log.log("SVNInfo:",
                "\n\t author:\t\t\t", svnInfo.getAuthor(),
                "\n\t Commit date:\t\t\t", TimeFormat.FORMAT_YYYYMMDD_HHMMSS(svnInfo.getCommittedDate()),
                "\n\t sum:\t\t\t", svnInfo.getChecksum(),
                "\n\t listName:\t\t\t", svnInfo.getChangelistName(),
                "\n\t path:\t\t\t", svnInfo.getPath(),
                "\n\t revision:\t\t\t", svnInfo.getRevision().toString(),
                "\n\t commit revision:\t\t\t", svnInfo.getCommittedRevision().toString(),
                "\n\t url:\t\t\t", svnInfo.getURL().toDecodedString()
        );
    }
}
