package com.ll.ui;

import javax.swing.*;

abstract class BasePanel {
    public BasePanel() {
//        init();
    }

    public abstract void init();

    public abstract String getTitleName();

    public abstract JPanel getMainPanel();

    public abstract void setResultPanel(JTextArea area);
}
