package com.example.sikobeh;

public class Berita {

    public String desc;
    public String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String judul;
    public String loc;
    public String beritaurl;
    public String timeupload;

    public Berita() {
    }

    public Berita(String desc, String judul, String loc, String beritaurl, String timeupload, String key) {
        this.desc = desc;
        this.judul = judul;
        this.loc = loc;
        this.beritaurl = beritaurl;
        this.timeupload = timeupload;
        this.key = key;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }

    public String getBeritaurl() { return beritaurl; }

    public void setBeritaurl(String beritaurl) { this.beritaurl = beritaurl; }

    public String getTimeupload() {
        return timeupload;
    }

    public void setTimeupload(String timeupload) {
        this.timeupload = timeupload;
    }
}
