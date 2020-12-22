package com.varyam.firebaseapp;

public class Comment {
    private String user;
    private String commentString;

    public Comment() {
    }

    public Comment(String user, String commentString) {
        this.user = user;
        this.commentString = commentString;
    }

    public String getUser() {
        return user;
    }

    public String getCommentString() {
        return commentString;
    }
}
