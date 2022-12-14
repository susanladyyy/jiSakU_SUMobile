package edu.bluejack22_1.jisaku.models;

import android.graphics.drawable.Drawable;

public class Activity {
    String userid;
    String activity;
    String type;
    String date;

    public Activity(String userid, String activity, String type, String date) {
        this.userid = userid;
        this.activity = activity;
        this.type = type;
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }
}
