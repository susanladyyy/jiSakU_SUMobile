package edu.bluejack22_1.jisaku.models;

import java.util.ArrayList;

public class Post {
    private String currdocId, docId, title, caption, complexity, category, videoPath, userid, date;
    private ArrayList<Comment> comment;
    private ArrayList<String> wishlist;

    public Post(String currdocId, String docId, String title, String caption, String complexity, String category, String videoPath, String userid, String date, ArrayList<Comment> comment, ArrayList<String> wishlist) {
        this.currdocId = currdocId;
        this.docId = docId;
        this.title = title;
        this.caption = caption;
        this.complexity = complexity;
        this.category = category;
        this.videoPath = videoPath;
        this.userid = userid;
        this.date = date;
        this.comment = comment;
        this.wishlist = wishlist;
    }

    public String getCurrdocId() {
        return currdocId;
    }

    public void setCurrdocId(String currdocId) {
        this.currdocId = currdocId;
    }

    public ArrayList<String> getWishlist() {
        return wishlist;
    }

    public void setWishlist(ArrayList<String> wishlist) {
        this.wishlist = wishlist;
    }

    public ArrayList<Comment> getComment() {
        return comment;
    }

    public void setComment(ArrayList<Comment> comment) {
        this.comment = comment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getComplexity() {
        return complexity;
    }

    public void setComplexity(String complexity) {
        this.complexity = complexity;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }
}
