package com.varyam.firebaseapp;

import java.util.ArrayList;
import java.util.List;

public class Post {
    private user creator;
    private String postId;
    private String caption;
    private List<String> likes;
    private List<Comment> comments;
    private String timeOfPost;
    private String currentPhotoPath;
    private String downloadUri;

    public Post() {
    }

    public Post(user creator, String postId, String timeOfPost, String currentPhotoPath, String caption) {
        this.creator = creator;
        this.postId = postId;
        this.likes = new ArrayList<>();
        this.comments = new ArrayList<>();
        this.timeOfPost = timeOfPost;
        this.caption = caption;
        this.currentPhotoPath = currentPhotoPath;
    }

    public user getCreator() {
        return creator;
    }

    public String getPostId() {
        return postId;
    }

    public String getCaption() {
        return caption;
    }

    public List<String> getLikes() {
        return likes;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public String getTimeOfPost() {
        return timeOfPost;
    }

    public String getCurrentPhotoPath() {
        return currentPhotoPath;
    }

    public String getDownloadUri() {
        return downloadUri;
    }

    public void setDownloadUri(String downloadUri) {
        this.downloadUri = downloadUri;
    }
}
