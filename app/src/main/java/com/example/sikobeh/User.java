package com.example.sikobeh;

public class User {

    public String fullname, pnumber, email, password, uid, imageurl;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public User()
    {

    }

    public User(String fullname, String pnumber, String email, String password, String uid)
    {
        this.fullname = fullname;
        this.pnumber = pnumber;
        this.email = email;
        this.password = password;
        this.uid = uid;
    }
}
