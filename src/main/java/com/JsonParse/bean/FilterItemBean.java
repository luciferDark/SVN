package com.JsonParse.bean;

import java.util.List;

public class FilterItemBean {
    public String name;
    public boolean off;
    public int flag;
    public List<String> items;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isOff() {
        return off;
    }

    public void setOff(boolean off) {
        this.off = off;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public List<String> getItems() {
        return items;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        int length = 0;
        for (String item : items) {
            builder.append("                \"").append(item).append("\"");
            if (length < items.size() -1){
                builder.append(",\n");
            }
            length ++;
        }
        return "        {" + "\n" +
                "            \"name\": \"" + name + "\"," + "\n" +
                "            \"off\": " + (off ? "true" : "false") + "," + "\n" +
                "            \"flag\": " + flag + "," + "\n" +
                "            \"items\": [\n" +
                builder.toString() +
                "\n            ]\n" +
                "        }";
    }
}
