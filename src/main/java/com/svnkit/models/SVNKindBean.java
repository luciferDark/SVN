package com.svnkit.models;

public class SVNKindBean {
    private KindType type;
    private String path;

    public KindType getType() {
        return type;
    }

    public void setType(KindType type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public SVNKindBean() {

    }
    public SVNKindBean(KindType type, String path) {
        this.type = type;
        this.path = path;
    }

    @Override
    public String toString() {
        return "SVNKindBean{" +
                "type=" + type +
                ", path='" + path + '\'' +
                '}';
    }

    public static enum KindType{
        M("Modified"),
        A("Add"),
        D("Delete")
        ;
        private String desc ="";
        private KindType(String desc){
            this.desc = desc;
        }
        public String getDesc() {
            return desc;
        }

        @Override
        public String toString() {
            return  desc;
        }
    }
}
