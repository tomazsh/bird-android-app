package com.nedeljko.bird.app.models;

import org.json.JSONException;
import org.json.JSONObject;

public class User {
    private String mName;
    private String mProfileImageUrl;
    private int mRemoteId;
    private String mScreenName;

    public User(JSONObject jsonObject) {
        try {
            mName = jsonObject.getString("name");
            mProfileImageUrl = jsonObject.getString("profile_image_url").replace("normal", "bigger");
            mRemoteId = jsonObject.getInt("id");
            mScreenName = jsonObject.getString("screen_name");
        } catch (JSONException jsonException) {
            System.out.println("Exception creating User from JSON response: " + jsonException);
        }
    }

    //region Getters and Setters

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getProfileImageUrl() {
        return mProfileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        mProfileImageUrl = profileImageUrl;
    }

    public int getRemoteId() {
        return mRemoteId;
    }

    public void setRemoteId(int remoteId) {
        mRemoteId = remoteId;
    }

    public String getScreenName() {
        return mScreenName;
    }

    public void setScreenName(String screenName) {
        mScreenName = screenName;
    }

    //endregion
}
