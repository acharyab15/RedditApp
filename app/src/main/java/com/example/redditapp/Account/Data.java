package com.example.redditapp.Account;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("modhash")
    @Expose
    private String modhash;

    @SerializedName("cookie")
    @Expose
    private String cookie;

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public String getModhash() {

        return modhash;
    }

    public void setModhash(String modhash) {
        this.modhash = modhash;
    }

    @Override
    public String toString() {
        return "Data{" +
                "modhash='" + modhash + '\'' +
                ", cookie='" + cookie + '\'' +
                '}';
    }
}
