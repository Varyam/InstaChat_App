package com.varyam.firebaseapp;

public class user {

    private String userName;
    private String userID;
    private String profileUri;

    public user() {
    }

    public user(String userName, String userID, String profileUri) {
        this.userName = userName;
        this.userID = userID;
        this.profileUri = profileUri;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserID() {
        return userID;
    }

    public String getProfileUri() {
        return profileUri;
    }
}
