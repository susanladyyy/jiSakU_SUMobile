package edu.bluejack22_1.jisaku.models;

public class Comment {
    String userid;
    String comment;
    String postid;

    public Comment(String userid, String comment, String postid) {
        this.userid = userid;
        this.comment = comment;
        this.postid = postid;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
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
