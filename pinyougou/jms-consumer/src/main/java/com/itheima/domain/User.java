package com.itheima.domain;

import java.io.Serializable;
import java.util.Date;

//com.itheima
public class User implements Serializable {

    private static final long serialVersionUID = -8127062631196784617L;
    //java.lang
    private int id;
    //java.lang
    private String username;
    //java.util
    private Date birthDay;

    public User(int id, String username, Date birthDay) {
        this.id = id;
        this.username = username;
        this.birthDay = birthDay;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", birthDay=" + birthDay +
                '}';
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(Date birthDay) {
        this.birthDay = birthDay;
    }
}
