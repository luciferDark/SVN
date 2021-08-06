package com.Util;

import javax.swing.*;

public class DialogUtil {
    public static void showMessageDialog(String title, String msg) {
        JOptionPane.showMessageDialog(null,
                msg,
                title,
                JOptionPane.INFORMATION_MESSAGE);
    }

    public static int showMessageYORNDialog(String title, String msg) {
        return JOptionPane.showConfirmDialog(null,
                msg,
                title,
                JOptionPane.YES_NO_OPTION);
    }

    public static int showMessageYORNWithDescDialog(String title, String msg, String[] desc) {
        return JOptionPane.showOptionDialog(null,
                msg,
                title,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                desc,
                desc[0]);
    }

    public static String showMessageWithDescListDialog(String title, String msg, Object[] desc, String defultValue) {
        return (String) JOptionPane.showInputDialog(null,
                msg,
                title,
                JOptionPane.PLAIN_MESSAGE,
                new ImageIcon("icon.png"),
                desc,
                defultValue);
    }

    public static String showMessageWithInputDialog(String title, String msg) {
        return JOptionPane.showInputDialog(null,
                msg + "\n",
                title,
                JOptionPane.PLAIN_MESSAGE);
    }
}
