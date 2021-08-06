package com.svnkit.impls;

import com.Util.Log;
import com.Util.TimeFormat;
import com.svnkit.SvnUtils;
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
        SVNLogBean svnLogBean = new SVNLogBean(svnLogEntry);
        Log.log("handleLogEntry", svnLogBean.toString());
    }
}
