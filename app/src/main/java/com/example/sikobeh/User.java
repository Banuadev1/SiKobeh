package com.example.sikobeh;

public class User {

    public String fullname, pnumber, email, uid, imageurl;

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getPnumber() {
        return pnumber;
    }

    public void setPnumber(String pnumber) {
        this.pnumber = pnumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public User()
    {

    }

    public User(String fullname, String pnumber, String email, String uid)
    {
        this.fullname = fullname;
        this.pnumber = pnumber;
        this.email = email;
        this.uid = uid;
    }
}
