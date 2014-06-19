package com.nedeljko.bird.app.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Tweet {
    private Date mCreatedAt;
    private int mRemoteId;
    private String mText;
    private User mUser;

    public Tweet(JSONObject jsonObject) {
        try {
            final String TWITTER_DATE_FORMAT = "EEE MMM dd HH:mm:ss Z yyyy";
            SimpleDateFormat dateFormat = new SimpleDateFormat(TWITTER_DATE_FORMAT, Locale.getDefault());
            try {
                mCreatedAt = dateFormat.parse(jsonObject.getString("created_at"));
            } catch (ParseException parseException) {
                System.out.println("Exception parsing date from JSON response: " + parseException);
            }

            mRemoteId = jsonObject.getInt("id");
            mText = jsonObject.getString("text");
            mUser = new User(jsonObject.getJSONObject("user"));
        } catch (JSONException jsonException) {
            System.out.println("Exception creating Tweet from JSON response: " + jsonException);
        }
    }

    public int getRemoteId() {
        return mRemoteId;
    }

    public void setRemoteId(int remoteId) {
        mRemoteId = remoteId;
    }

    public Date getCreatedAt() {
        return mCreatedAt;
    }

    public void setCreatedAt(Date createdAt) {
        mCreatedAt = createdAt;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    public User getUser() {
        return mUser;
    }

    public void setUser(User user) {
        mUser = user;
    }
}
