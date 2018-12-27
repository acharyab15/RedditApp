package com.example.redditapp;

public class Post {
    private String title;
    private String author;
    private String dateUpdated;
    private String postURL;
    private String thumbnailURL;

    public Post(String title, String author, String dateUpdated, String postURL, String thumnailURL) {
        this.title = title;
        this.author = author;
        this.dateUpdated = dateUpdated;
        this.postURL = postURL;
        this.thumbnailURL = thumnailURL;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(String dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public String getPostURL() {
        return postURL;
    }

    public void setPostURL(String postURL) {
        this.postURL = postURL;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumnailURL) {
        this.thumbnailURL = thumnailURL;
    }
}
