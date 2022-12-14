package edu.bluejack22_1.jisaku.models;

public class Comment {
    String userid;
    String comment;

    public Comment(String userid, String comment) {
        this.userid = userid;
        this.comment = comment;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
