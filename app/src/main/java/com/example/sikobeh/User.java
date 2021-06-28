package com.example.sikobeh;

public class User {

    public String fullname, age, email;

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public User()
    {

    }

    public User(String fullname, String age, String email)
    {
        this.fullname = fullname;
        this.age = age;
        this.email = email;
    }
}
