package com.ll.ui;

import com.Util.DialogUtil;
import com.Util.Log;
import com.Util.Util;
import com.svnkit.Contants;
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
                        "老板，有问题！",
                        "svn 路径是空的请检查");
                return;
            }
            if (svnPath.contains("BossLove_Client_Unity_U5")){
                DialogUtil.showMessageDialog(
                        "老板，有问题！",
                        "svn 路径是国服主干，搜索会很慢，不建议搜索。");
                return;
            }
            String conditions = mConditions.getText().trim();
            String excludes = mExclude.getText().trim();
            String[] mConditions = conditions.split("\\||,");
            String[] mExcludes = Util.isStringEmpty(excludes) ? null : excludes.split("\\||,");
            List<String> mConditionList = Arrays.asList(mConditions);
            List<String> mExcludesList = Util.isEmpty(mExcludes) ? null : Arrays.asList(mExcludes);
            if (null != mResultArea) {
                String result = SVNHelper.getInstance().getVersionsByLog(svnPath, mConditionList, mExcludesList).toString();
                mResultArea.setText(result);
            }
        });

        mSwitchPath.addActionListener(event ->
                switchReportiesPathUrlUI()
        );
    }


    @Override
    public String getTitleName() {
        return "活动功能筛选";
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
