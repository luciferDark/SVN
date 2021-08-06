package com.ll.ui;

import com.Util.Log;
import com.svnkit.SVNHelper;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MainPanel extends BasePanel {
    private JTabbedPane tablePanelContainer;
    private JPanel panelMain;
    private JPanel resultPanel;

    private SvnFilterActivitiesPanel mSvnFilterActivitiesPanel;
    private JScrollPane mResultScrollPanel;
    public JTextArea mResultArea;

    private List<BasePanel> tablePanelList;

    public MainPanel(){
        super();
        init();
    }

    @Override
    public void init() {
        initData();
    }

    private void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SVNHelper.getInstance().initReportiesPathUrl();
                initPanels();
                initListener();
            }
        }).start();
    }

    private void initListener() {

    }

    @Override
    public String getTitleName() {
        return "主面板";
    }

    @Override
    public JPanel getMainPanel() {
        return panelMain;
    }

    @Override
    public void setResultPanel(JTextArea area) {

    }

    private void initPanels() {
        tablePanelList = new ArrayList<>();
        initResultArea();
        initSVNFilterActivitiesPanel();
        addAllPanel2TablePanel();
    }

    /**
     * 初始化结果面板
     */
    private void initResultArea() {
        mResultArea = new JTextArea(7, 90);
        mResultArea.setSelectedTextColor(Color.BLUE);
        mResultArea.setCaretColor(Color.CYAN);
        mResultArea.setLineWrap(true);
        mResultArea.setWrapStyleWord(true);
        mResultScrollPanel = new JScrollPane(mResultArea);
        //设置矩形大小.参数依次为(矩形左上角横坐标x,矩形左上角纵坐标y，矩形长度，矩形宽度)
        mResultScrollPanel.setBounds(0, 0, 550, 250);
        resultPanel.add(mResultScrollPanel);
    }

    private void initSVNFilterActivitiesPanel() {
        mSvnFilterActivitiesPanel = new SvnFilterActivitiesPanel();
        tablePanelList.add(mSvnFilterActivitiesPanel);
    }

    private void addAllPanel2TablePanel() {
        if (null == tablePanelList) {
            return;
        }
        tablePanelList.forEach(panel -> {
                    if (null != panel.getMainPanel()){
                        Log.log("add tab ", panel.getTitleName(), " into tabs");
                        panel.setResultPanel(mResultArea);
                        tablePanelContainer.add(panel.getTitleName(), panel.getMainPanel());
                    }
                }
        );
    }
}
