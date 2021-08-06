package com.JsonParse.bean;

import java.util.List;

public class FilterContants {
    private String baseProfileName;
    private List<FilterItemBean> packages;

    public String getBaseProfileName() {
        return baseProfileName;
    }

    public void setBaseProfileName(String baseProfileName) {
        this.baseProfileName = baseProfileName;
    }

    public List<FilterItemBean> getPackages() {
        return packages;
    }

    public void setPackages(List<FilterItemBean> packages) {
        this.packages = packages;
    }

    @Override
    public String toString() {
        return "FilterContants{" +
                "baseProfileName='" + baseProfileName + '\'' +
                ", packages=" + packages +
                '}';
    }

    public String toMyString() {
        return toMyString(packages);
    }

    public String toMyString(List<FilterItemBean> list) {
        StringBuilder builder = new StringBuilder();
        builder.append("[\n");
        int length = 0;
        for (FilterItemBean bean : list) {
            builder.append(list.toString());
            if (length < list.size() - 1) {
                builder.append(",\n");
            }
            length++;
        }
        builder.append("\n    ]");
        return builder.toString();
    }
}
