package com;

import com.JsonParse.bean.FilterContants;
import com.JsonParse.bean.FilterItemBean;
import com.Util.FileHelper;
import com.Util.Log;
import com.alibaba.fastjson.JSON;
import com.ll.ui.MainPanel;
import org.jb2011.lnf.beautyeye.*;

import javax.swing.*;
import javax.swing.plaf.InsetsUIResource;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Main {

    public static void main(String[] agrs) {
        initBeautyEye();
        showPanel();
//        test3();
    }

    /**
     * 展示面板
     */
    public static void showPanel() {
        JFrame mainFrame = new JFrame();
        MainPanel startPanel = new MainPanel();
        mainFrame.setContentPane(startPanel.getMainPanel());
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setBounds(100, 100, 630, 660);
        mainFrame.setVisible(true);
        mainFrame.setResizable(false);
        String title = "㊎㊌㊍㊋㊏五行齐聚bug全无 - By 麒麟";
        mainFrame.setTitle(title);
    }

    private static void test3() {
        String[] split = "".split("\\||,");
        for (String s : split) {
            Log.log("split", s);
        }
    }

    private static void test2() {
        String path1 = "H:/work-projects/TW/trunk/TWProject_arm64/Assets/Builds/i6/Activity";
        File file = new File(path1);
        File[] fileChildren = file.listFiles();
        List<String> result = new ArrayList<>();
        for (File fileChild : fileChildren) {
            if (!fileChild.getName().contains("meta")) {
                result.add(fileChild.getName());
            }
        }
        Collections.sort(result);
        Log.log("====get fileChild\n", result.toString());
    }

    public static void test1() {
        String path1 = "G:/杂/比较/3.txt";
        String path2 = "G:/杂/比较/4.txt";
        String contants1 = new FileHelper().readFileByLines(new File(path1));
        String contants2 = new FileHelper().readFileByLines(new File(path2));
        Log.log("====get contants1\n", contants1);
        Log.log("====get contants2\n", contants2);
        List<String> result1 = Arrays.asList(contants1.split("\n"));
        List<String> result2 = Arrays.asList(contants2.split("\n"));
        List<String> result3 = new ArrayList<>();
        List<String> result4 = new ArrayList<>();
        List<String> result5 = new ArrayList<>();
        Log.log("====get result1\n", result1.toString());
        Log.log("====get result2\n", result2.toString());
        for (String s : result2) {
            String id = s.split("&&")[0];
            if (result1.contains(id)) {
                result3.add(id);
                result4.add(s);
            } else {
                result5.add(s);
            }
        }
        Log.log("====get result3\n", result3.toString());
        Log.log("====get result4\n", result4.toString());
        Log.log("====get result5\n", result5.toString());

        for (String s : result4) {
            String[] item = s.split("&&");
            String id = s.split("&&")[0];
            String time = s.split("&&")[1];
            String name = s.split("&&")[2];
            Log.log("id", id, "name", name, "time", time);
        }
    }

    public static void test() {
        String path = "H:/work-projects/TW/trunk/TWProject_arm64/Design/PackageSettings/default1.json";
        String path1 = "H:/work-projects/TW/trunk/TWProject_arm64/Design/PackageSettings/1.txt";
        String path2 = "H:/work-projects/TW/trunk/TWProject_arm64/Design/PackageSettings/2.txt";
        File file = new File(path);
        FileHelper helper = new FileHelper();
        String contants = new FileHelper().readFileByLines(file);
//        Log.log("====get contants\n", contants);
        FilterContants result = JSON.parseObject(contants, FilterContants.class);
//        Log.log("====get result\n", result.toMyString());
        List<FilterItemBean> beans = result.getPackages();
        List<Integer> activityIds = new ArrayList<>();
        for (FilterItemBean bean : beans) {
            String name = bean.getName();
            if (name.contains("(") && name.contains(")")) {
                String activityId = name.substring(name.indexOf("(") + 1, name.indexOf(")"));
//                Log.log("====activityId", activityId);
                activityIds.add(Integer.decode(activityId));
            }
        }
        Collections.sort(activityIds);
        Log.log("====activityIds", activityIds.toString());
        List<FilterItemBean> sortBeans = new ArrayList<>();
        for (Integer activityId : activityIds) {
            for (FilterItemBean bean : beans) {
                if (bean.getName().contains(String.valueOf(activityId))) {
                    sortBeans.add(bean);
                    break;
                }
            }
        }
        Log.log("====get resultSort\n", result.toMyString(sortBeans));
        helper.writeToFile(new File(path1), result.toMyString());
        helper.writeToFile(new File(path2), result.toMyString(sortBeans));
    }

    private static void initBeautyEye() {
        // 设置BeautyEye皮肤
        try {
            BeautyEyeLNFHelper.frameBorderStyle = BeautyEyeLNFHelper.FrameBorderStyle.translucencySmallShadow;
            UIManager.put("RootPane.setupButtonVisible", false);
            //改变InsetsUIResource参数的值即可实现
            UIManager.put("TabbedPane.tabAreaInsets", new InsetsUIResource(3,5,2,5));
            UIManager.put("ToolBar.isPaintPlainBackground", Boolean.TRUE);
            for (int i = 0; i < DEFAULT_FONT.length; i++)
                UIManager.put(DEFAULT_FONT[i],new Font("隶书", Font.PLAIN,12));
            BeautyEyeLNFHelper.launchBeautyEyeLNF();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** UIManager中UI字体相关的key */
    public static String[] DEFAULT_FONT  = new String[]{
            "Table.font"
            ,"TableHeader.font"
            ,"CheckBox.font"
            ,"Tree.font"
            ,"Viewport.font"
            ,"ProgressBar.font"
            ,"RadioButtonMenuItem.font"
            ,"ToolBar.font"
            ,"ColorChooser.font"
            ,"ToggleButton.font"
            ,"Panel.font"
            ,"TextArea.font"
            ,"Menu.font"
            ,"TableHeader.font"
            // ,"TextField.font"
            ,"OptionPane.font"
            ,"MenuBar.font"
            ,"Button.font"
            ,"Label.font"
            ,"PasswordField.font"
            ,"ScrollPane.font"
            ,"MenuItem.font"
            ,"ToolTip.font"
            ,"List.font"
            ,"EditorPane.font"
            ,"Table.font"
            ,"TabbedPane.font"
            ,"RadioButton.font"
            ,"CheckBoxMenuItem.font"
            ,"TextPane.font"
            ,"PopupMenu.font"
            ,"TitledBorder.font"
            ,"ComboBox.font"
    };
}
