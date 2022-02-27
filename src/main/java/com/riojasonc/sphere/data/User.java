package com.riojasonc.sphere.data;

public class User{
    public static final int nameMaxLength = 24;
    public static final int passwordMaxLength = 24;

    public long id;
    public String name;
    public String password;
    public int gender = -1;
    public License license;

    public User(){

    }

    public User(String name, String password){
        this.name = name;
        this.password = password;
    }

    public User(String name, String password, License license){
        this.name = name;
        this.password = password;
        this.license = license;
    }

    public User(String name, String password, License license, int gender){
        this.name = name;
        this.password = password;
        this.license = license;
        this.gender = gender;
    }
}
