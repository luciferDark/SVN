package com.test;

import com.Util.FileHelper;
import com.Util.Log;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class BeComparaListResultAnalytics {
    double sizeAddAll = 0;
    double sizeModAll = 0;
    double sizeDelAll = 0;
    DecimalFormat df = new DecimalFormat("0.00");

    class Info {
        String name;
        String statue;
        double size;

        @Override
        public String toString() {
            return "Info{" +
                    "name='" + name + '\'' +
                    ", statue='" + statue + '\'' +
                    ", size=" + df.format(size) + '}';
        }
    }

    List<Info> mp4Pv = new ArrayList<>();
    List<Info> mp4Phone = new ArrayList<>();
    //common/audios/phone_tw
    List<Info> voice_tw = new ArrayList<>();
    List<Info> voice_cn = new ArrayList<>();
    List<Info> voice_jp = new ArrayList<>();
    List<Info> phone_tw = new ArrayList<>();
    List<Info> phone_cn = new ArrayList<>();
    List<Info> phone_jp = new ArrayList<>();
    //common/audios/extrastory
    List<Info> extrastory = new ArrayList<>();
    //common/audios/sfx
    List<Info> sfx = new ArrayList<>();
    List<Info> battleworld = new ArrayList<>();
    List<Info> card_h = new ArrayList<>();
    List<Info> activity = new ArrayList<>();
    //i6/sp
    List<Info> home = new ArrayList<>();
    //i6/sp
    List<Info> sp = new ArrayList<>();
    //i6/imgs
    List<Info> imgs = new ArrayList<>();
    //lua
    List<Info> lua = new ArrayList<>();
    //other
    List<Info> other = new ArrayList<>();

    private void logResult() {
        logList(mp4Pv, "mp4Pv");
        logList(mp4Phone, "mp4Phone");
        logList(voice_tw, "voice_tw");
        logList(voice_cn, "voice_cn");
        logList(voice_jp, "voice_jp");
        logList(phone_tw, "phone_tw");
        logList(phone_cn, "phone_cn");
        logList(phone_jp, "phone_jp");
        logList(extrastory, "extrastory");
        logList(sfx, "sfx");
        logList(battleworld, "battleworld");
        logList(card_h, "card_h");
        logList(home, "home");
        logList(sp, "sp");
        logList(lua, "lua");
        logList(other, "other");

        Log.log("数据总大小：",
                "Add大小=", df.format(sizeAddAll) + "Mb",
                "Mod大小=", df.format(sizeModAll) + "Mb",
                "Add&Mod大小=", df.format(sizeAddAll + sizeModAll) + "Mb",
                "Del大小=", df.format(sizeDelAll) + "Mb"
        );
    }

    public void init() {
        FileHelper helper = new FileHelper();
        String path = "G:\\杂\\back\\3.txt";
        helper.setReadLineCallback(new FileHelper.ReadLineCallback() {
            @Override
            public void readlineCallback(String line) {
                handleLineString(line);
            }
        });
        helper.readFileByLines(new File(path));
        logResult();
    }

    private void logList(List<Info> result, String msg) {
        double sizeAdd = 0;
        double sizeMod = 0;
        double sizeDel = 0;
        for (int i = 0; i < result.size(); i++) {
            if (result.get(i).statue.equals("ADD")) {
                sizeAdd += result.get(i).size;
            } else if (result.get(i).statue.equals("MOD")) {
                sizeMod += result.get(i).size;
            } else {
                sizeDel += result.get(i).size;
            }
            if (msg.equals("other")){
//                Log.log("other", result.get(i).toString());
            }
        }

        sizeAddAll += sizeAdd;
        sizeModAll += sizeMod;
        sizeDelAll += sizeDel;
        Log.log("数据", msg,
                "Add大小=", df.format(sizeAdd) + "Mb",
                "Mod大小=", df.format(sizeMod) + "Mb",
                "Add&Mod大小=", df.format(sizeAdd + sizeMod) + "Mb",
                "Del大小=", df.format(sizeDel) + "Mb");
    }

    private String[] result;

    private void handleLineString(String msg) {
        if (msg.contains("ADD file num:")
                || msg.contains("MOD file num:")
                || msg.contains("DEL file num")
                || msg.contains("ALL ADD&MOD file num")) {
            Log.log(msg);
            return;
        }
        result = msg.split(",");
        if (result.length < 3) {
            Log.log("result.length<3", msg);
            return;
        }
        Info info = new Info();
        info.name = result[0];
        info.size = Double.parseDouble(result[1]);
        info.statue = result[2];

        if (info.name.contains("common/video") && !info.name.contains("common/video/phone")) {
            mp4Pv.add(info);
        } else if (info.name.contains("common/video/phone")) {
            mp4Phone.add(info);
        } else if (info.name.contains("voice_tw")) {
            voice_tw.add(info);
        } else if (info.name.contains("voice_cn")) {
            voice_cn.add(info);
        } else if (info.name.contains("voice_jp")) {
            voice_jp.add(info);
        } else if (info.name.contains("phone_tw")) {
            phone_tw.add(info);
        } else if (info.name.contains("phone_cn")) {
            phone_cn.add(info);
        } else if (info.name.contains("phone_jp")) {
            phone_jp.add(info);
        } else if (info.name.contains("common/audios/extrastory")) {
            extrastory.add(info);
        } else if (info.name.contains("common/audios/sfx") || info.name.contains("common/audios/sfx")) {
            sfx.add(info);
        } else if (info.name.contains("i6/home")) {
            home.add(info);
        } else if (info.name.contains("common/audios/battleworld")) {
            battleworld.add(info);
        } else if (info.name.contains("card_h")) {
            card_h.add(info);
        } else if (info.name.contains("i6/sp/")) {
            sp.add(info);
        } else if (info.name.startsWith("lua/")) {
            lua.add(info);
        } else {
            other.add(info);
        }
    }
}
