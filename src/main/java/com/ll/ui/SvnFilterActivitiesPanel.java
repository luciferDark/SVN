package com.ll.ui;

import com.Util.DialogUtil;
import com.Util.Log;
import com.Util.Util;
import com.svnkit.Contants;
import com.svnkit.SVNCmdHelper;
import com.svnkit.SVNHelper;
import com.svnkit.models.SVNKitBean;

import javax.swing.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SvnFilterActivitiesPanel extends BasePanel {
    private JPanel panel;
    private JComboBox mSvnPath;
    private JComboBox mCountries;
    private JComboBox mBranches;
    private JTextArea mConditions;
    private JTextArea mExclude;
    private JButton mStartSearchBtn;
    private JButton mSwitchPath;
    private JButton mShowLogBtn;
    private JTextArea mResultArea;

    List<SVNKitBean> svnChannelList;

    public SvnFilterActivitiesPanel() {
        super();
        init();
    }

    @Override
    public void init() {
        initData();
        initListener();
    }

    private void initData() {
        DefaultComboBoxModel boxModelCountries = new DefaultComboBoxModel(Contants.COUNTRIES);
        DefaultComboBoxModel boxModelBranches = new DefaultComboBoxModel(Contants.BRANCHES);
        mCountries.setModel(boxModelCountries);
        mBranches.setModel(boxModelBranches);

        switchReportiesPathUrlUI();
    }

    private void switchReportiesPathUrlUI() {
        String[] pathsData = getReportiesPathUrls(mCountries.getSelectedItem().toString(), mBranches.getSelectedItem().toString());
        mSvnPath.setModel(new DefaultComboBoxModel(pathsData));
    }

    private String[] getReportiesPathUrls(String country, String branch) {
        svnChannelList = SVNHelper.getInstance().getSVNChannelList(country, branch);
        Collections.sort(svnChannelList);
        String[] result = new String[svnChannelList.size()];
        for (SVNKitBean svnKitBean : svnChannelList) {
            result[svnChannelList.indexOf(svnKitBean)] = svnKitBean.toString();
        }
        for (String s : result) {
            Log.log("result", s);
        }
        return result;
    }

    private String getRealSvnPath(String path) {
        String result = path;

        for (SVNKitBean svnKitBean : svnChannelList) {
            if (path.equals(svnKitBean.toString())) {
                return svnKitBean.entry.getURL().toDecodedString();
            }
        }
        return result;
    }

    private void initListener() {
        mStartSearchBtn.addActionListener(event -> {
            String svnPath = getRealSvnPath((String) mSvnPath.getSelectedItem());
            if (Util.isStringEmpty(svnPath)){
                DialogUtil.showMessageDialog(
                        "?????????????????????",
                        "svn ????????????????????????");
                return;
            }
            if (svnPath.contains("BossLove_Client_Unity_U5")){
                DialogUtil.showMessageDialog(
                        "?????????????????????",
                        "svn ????????????????????????????????????????????????????????????");
                return;
            }
            String conditions = mConditions.getText().trim();
            if (Util.isStringEmpty(conditions)){
                DialogUtil.showMessageDialog(
                        "?????????????????????",
                        "???????????????????????????????????????????????????????????????????????????");
                return;
            }
            StringBuilder result = new StringBuilder("?????????... ...");
            mResultArea.setText(result.toString());
            String excludes = mExclude.getText().trim();
            String[] mConditions = conditions.split("\\||,");
            String[] mExcludes = Util.isStringEmpty(excludes) ? null : excludes.split("\\||,");
            List<String> mConditionList = Arrays.asList(mConditions);
            List<String> mExcludesList = Util.isEmpty(mExcludes) ? null : Arrays.asList(mExcludes);
            if (null != mResultArea) {
                result = new StringBuilder();
                List svnVersions = SVNHelper.getInstance().getVersionsByLog(svnPath, mConditionList, mExcludesList);
                Log.log(svnVersions.toString());
                for (int i = 0; i < svnVersions.size(); i++) {
                    result.append(svnVersions.get(i));
                    if (i != svnVersions.size() - 1){
                        result.append(", ");
                    }
                }mResultArea.setText(result.toString());
            }
        });

        mSwitchPath.addActionListener(event ->
                switchReportiesPathUrlUI()
        );

        mShowLogBtn.addActionListener(
                event ->{String svnPath = getRealSvnPath((String) mSvnPath.getSelectedItem());
                    if (Util.isStringEmpty(svnPath)){
                        DialogUtil.showMessageDialog(
                                "?????????????????????",
                                "svn ????????????????????????");
                        return;
                    }
                    Log.log("?????????svn?????????", svnPath);
                    SVNCmdHelper.log(svnPath);
                }
        );

    }

    @Override
    public String getTitleName() {
        return "??????????????????";
    }

    @Override
    public JPanel getMainPanel() {
        return panel;
    }

    @Override
    public void setResultPanel(JTextArea area) {
        this.mResultArea = area;
    }
}
