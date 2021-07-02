package com.svnkit.impls.handlers;

import com.svnkit.interfaces.handlers.ISVNCleanEventHandler;
import org.tmatesoft.svn.core.SVNCancelException;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.wc.SVNEvent;

public class SVNCleanEventHandlerImp implements ISVNCleanEventHandler
{
    @Override
    public void handleEvent(SVNEvent svnEvent, double progress) throws SVNException {

    }

    @Override
    public void checkCancelled() throws SVNCancelException {

    }
}
