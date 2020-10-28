package com.example.map2;

import android.app.Application;

class UsersInfo extends Application {

    private static String FullName;
    private static String Username;
    private static String Email;
    private static String Phone;
    private static String Password;


    public UsersInfo() {
    }

    public UsersInfo(String fname, String username, String email, String phone, String password) {
        this.FullName = fname;
        this.Username = username;
        this.Email = email;
        this.Phone = phone;
        this.Password = password;
    }


    public static String getFullName() {
        return FullName;
    }

    public static void setFullName(String fullName) {
        FullName = fullName;
    }

    public static String getUsername() {
        return Username;
    }

    public static void setUsername(String username) {
        Username = username;
    }

    public static String getEmail() {
        return Email;
    }

    public static void setEmail(String email) {
        Email = email;
    }

    public static String getPhone() {
        return Phone;
    }

    public static void setPhone(String phone) {
        Phone = phone;
    }

    public static String getPassword() {
        return Password;
    }

    public static void setPassword(String password) {
        Password = password;
    }
}

