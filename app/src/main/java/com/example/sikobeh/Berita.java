package com.example.sikobeh;

public class Berita {

    public String desc;
    public String judul;
    public String loc;
    public String beritaurl;

    public Berita() {
    }

    public Berita(String desc, String judul, String loc, String beritaurl) {
        this.desc = desc;
        this.judul = judul;
        this.loc = loc;
        this.beritaurl = beritaurl;
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


}
